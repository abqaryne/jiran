import {after,before,test} from 'node:test';
import assert from 'node:assert/strict';
import {build} from 'esbuild';
import {Miniflare,convertV4MiniflareOptions} from 'miniflare';
import {mkdtemp,rm} from 'node:fs/promises';
import {resolve,sep} from 'node:path';

let mf,namespace,options;
const userA='a'.repeat(64),userB='b'.repeat(64);
const standard={accountMinute:4,accountDay:8,userDay:3};
before(async()=>{
  await build({entryPoints:['test/quota-worker.mjs'],bundle:true,format:'esm',platform:'browser',
    target:'es2022',outfile:'dist/quota-test.mjs'});
  options=convertV4MiniflareOptions({modules:true,scriptPath:'dist/quota-test.mjs',
    compatibilityDate:'2026-10-23',durableObjects:{QUOTA:{className:'QuotaHarness',useSQLite:true}},
    outboundService(){throw new Error('Quota tests must not make outbound requests');}});
  mf=new Miniflare({...options,host:'127.0.0.1',inspectorHost:'127.0.0.1',telemetry:{enabled:false}});
  await mf.ready;
  namespace=await mf.getDurableObjectNamespace('QUOTA');
});
test('reservations survive disposal and restart of the local runtime',async()=>{
  const parent=resolve('dist'),directory=await mkdtemp(resolve(parent,'quota-persist-'));
  let first,second;
  const local=()=>new Miniflare({...options,host:'127.0.0.1',inspectorHost:'127.0.0.1',
    telemetry:{enabled:false},resourcePersistencePath:directory});
  async function persisted(instance) {
    await instance.ready;
    const ns=await instance.getDurableObjectNamespace('QUOTA');
    return ns.get(ns.idFromName('restart-account'));
  }
  try {
    first=local();
    assert.equal((await reserve(await persisted(first),{units:3})).status,200);
    await first.dispose(); first=undefined;
    second=local();
    assert.equal((await reserve(await persisted(second),{units:3})).status,429);
  } finally {
    await first?.dispose(); await second?.dispose();
    assert.ok(resolve(directory).startsWith(parent+sep));
    await rm(directory,{recursive:true,force:true});
  }
});
after(async()=>{await mf?.dispose();});
function account(name) { return namespace.get(namespace.idFromName(name)); }
async function reserve(stub,{userKey=userA,units=1,at=100000,limits=standard,fail,data}={}) {
  const response=await stub.fetch('https://quota.internal/reserve',{method:'POST',
    body:JSON.stringify({limits,data:data??{userKey,units},at,fail})});
  return {status:response.status,body:await response.json()};
}
test('concurrent reservations cannot overspend one user allowance',async()=>{
  const results=await Promise.all(Array.from({length:30},()=>reserve(account('user-race'))));
  assert.equal(results.filter(r=>r.status===200).length,3);
  assert.ok(results.filter(r=>r.status===429).every(r=>r.body.reason==='user-day'));
});
test('concurrent different users share the same atomic account minute limit',async()=>{
  const results=await Promise.all(Array.from({length:30},(_,i)=>reserve(account('account-race'),{
    userKey:i%2?userA:userB})));
  assert.equal(results.filter(r=>r.status===200).length,4);
  assert.equal(results.filter(r=>r.status===429).length,26);
});
test('concurrent distinct users cannot overspend the account daily allowance',async()=>{
  const limits={accountMinute:40,accountDay:8,userDay:3};
  const results=await Promise.all(Array.from({length:30},(_,i)=>reserve(account('daily-race'),{
    userKey:i.toString(16).padStart(64,'0'),limits})));
  assert.equal(results.filter(r=>r.status===200).length,8);
  assert.ok(results.filter(r=>r.status===429).every(r=>r.body.reason==='account-day'));
});
test('denied batch reservations consume neither account nor user allowance',async()=>{
  const stub=account('no-partial');
  assert.equal((await reserve(stub,{units:4})).status,429);
  assert.equal((await reserve(stub,{units:3})).status,200);
  assert.equal((await reserve(stub,{userKey:userB})).status,200);
  assert.equal((await reserve(stub,{userKey:userB})).status,429);
});
test('rolling minute expires exactly at its boundary, with daily usage retained',async()=>{
  const stub=account('minute');
  assert.equal((await reserve(stub,{units:3,at:0})).status,200);
  assert.equal((await reserve(stub,{userKey:userB,at:0})).status,200);
  assert.equal((await reserve(stub,{userKey:userB,at:59999})).status,429);
  assert.equal((await reserve(stub,{userKey:userB,at:60000})).status,200);
  assert.equal((await reserve(stub,{at:60000})).body.reason,'user-day');
});
test('account daily limit spans users and does not reset at a UTC date change',async()=>{
  const stub=account('day');
  const limits={accountMinute:4,accountDay:4,userDay:4};
  assert.equal((await reserve(stub,{units:4,at:86399000,limits})).status,200);
  assert.equal((await reserve(stub,{userKey:userB,at:86460000,limits})).body.reason,'account-day');
  assert.equal((await reserve(stub,{userKey:userB,at:172798999,limits})).status,429);
  assert.equal((await reserve(stub,{userKey:userB,at:172799000,limits})).status,200);
});
test('a failed write rolls back the reservation and last-clock update together',async()=>{
  const stub=account('rollback');
  assert.deepEqual(await reserve(stub,{units:3,at:200000,fail:'commit'}),{
    status:503,body:{error:'quota-unavailable'}});
  assert.equal((await reserve(stub,{units:3,at:100000})).status,200);
});
test('storage failures fail closed without private error details',async()=>{
  assert.deepEqual(await reserve(account('storage'),{fail:'storage'}),{
    status:503,body:{error:'quota-unavailable'}});
});
test('clock regression fails closed instead of dropping previously charged events',async()=>{
  const stub=account('clock');
  assert.equal((await reserve(stub)).status,200);
  assert.deepEqual(await reserve(stub,{at:99999}),{status:503,body:{error:'quota-unavailable'}});
  assert.equal((await reserve(stub)).status,200);
});
test('only bounded server limits and hashed user identifiers are accepted',async()=>{
  const stub=account('validation');
  for(const data of [{userKey:userA,units:0},{userKey:userA,units:1.5},
    {userKey:userA,units:41},{userKey:'private@example.com',units:1},
    {userKey:userA,units:1,accountDay:999},null,[]]) {
    const response=await reserve(stub,{data:data===null?false:data});
    assert.equal(response.status,400);
  }
  for(const limits of [{...standard,accountDay:2001},{...standard,accountMinute:41},
    {...standard,userDay:9},{...standard,accountDay:0},{}]) {
    assert.equal((await reserve(stub,{limits})).status,503);
  }
  for(const at of [-1,1.5,Number.MAX_SAFE_INTEGER]) {
    assert.equal((await reserve(stub,{at})).status,503);
  }
  assert.equal((await reserve(stub,{units:3})).status,200);
});
