import {after,before,test} from 'node:test';
import assert from 'node:assert/strict';
import {Miniflare,Response as MockResponse,convertV4MiniflareOptions} from 'miniflare';
import {exportJWK,generateKeyPair,SignJWT} from 'jose';

let mf,privateKey,jwk;
const allRuntimes=[];
const unexpectedOutbound=[];
const keyPath='/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com';
async function runtime({bindings={},keyStatus=200}={}) {
  const outboundService=request=>{
    if(request.method!=='GET' || request.url!==`https://www.googleapis.com${keyPath}`) {
      unexpectedOutbound.push(request.method);
      throw new Error('Unexpected outbound request blocked');
    }
    return new MockResponse(keyStatus===200?JSON.stringify({keys:[jwk]}):'upstream-private-detail',
      {status:keyStatus,headers:{'content-type':'application/json','cache-control':'public,max-age=60'}});
  };
  const options=convertV4MiniflareOptions({modules:true,scriptPath:'dist/worker.mjs',compatibilityDate:'2026-10-23',
    bindings:{SPIKE_MODE:'local-feasibility',FIREBASE_PROJECT_ID:'demo-jiran',...bindings},outboundService});
  const instance=new Miniflare({...options,host:'127.0.0.1',inspectorHost:'127.0.0.1',telemetry:{enabled:false}});
  allRuntimes.push(instance);
  await instance.ready;
  return instance;
}
before(async()=>{
  const keys=await generateKeyPair('RS256',{extractable:true});
  privateKey=keys.privateKey;
  jwk={...await exportJWK(keys.publicKey),kid:'generated-local-key',alg:'RS256',use:'sig'};
  mf=await runtime();
});
after(async()=>{
  await Promise.all(allRuntimes.map(instance=>instance.dispose()));
  assert.deepEqual(unexpectedOutbound,[]);
});
async function token(changes={},header={},key=privateKey) {
  const now=Math.floor(Date.now()/1000);
  return new SignJWT({iss:'https://securetoken.google.com/demo-jiran',aud:'demo-jiran',sub:'generated-user',
    iat:now-60,auth_time:now-120,exp:now+3600,email_verified:true,email:'private@example.com',...changes})
    .setProtectedHeader({alg:'RS256',kid:'generated-local-key',...header}).sign(key);
}
async function call({path='/commute-fixture',body={scenarioId:'on-time'},jwt,method='POST',
  host='localhost',headers={},instance=mf}={}) {
  return instance.dispatchFetch(`http://${host}${path}`,{method,
    headers:{'content-type':'application/json',...(jwt===undefined?{}:{authorization:`Bearer ${jwt}`}),...headers},
    ...(method==='GET'?{}:{body:typeof body==='string'?body:JSON.stringify(body)})});
}
test('local workerd health is explicitly a fixture experiment',async()=>{
  const response=await call({path:'/health',method:'GET'});
  assert.equal(response.status,200);
  assert.deepEqual(await response.json(),{status:'local-feasibility',dataSource:'fixture'});
  assert.equal(response.headers.get('cache-control'),'no-store');
});

test('workerd returns a sanitized408 for an authenticated partial upload that never ends',async()=>{
  const controller=new AbortController();
  const guard=setTimeout(()=>controller.abort(),10000);
  const body=new ReadableStream({start(c){c.enqueue(new TextEncoder().encode('{}'));}});
  try {
    const response=await mf.dispatchFetch('http://localhost/commute-fixture',{method:'POST',
      headers:{'content-type':'application/json',authorization:`Bearer ${await token()}`},
      body,duplex:'half',signal:controller.signal});
    assert.equal(response.status,408);
    assert.equal(response.headers.get('cache-control'),'no-store');
    assert.deepEqual(await response.json(),{error:'request-timeout'});
  } finally { clearTimeout(guard); controller.abort(); }
});
test('RS256-verified auth runs the existing commute calculator in workerd',async()=>{
  const response=await call({jwt:await token()});
  assert.equal(response.status,200);
  const body=await response.json();
  assert.equal(body.researchOnly,true);
  assert.equal(body.result.dataSource,'fixture');
  assert.equal(body.result.trueMonthlyCostSen,61200);
  assert.equal(body.result.selectedMode,'transit');
  assert.doesNotMatch(JSON.stringify(body),/generated-user|private@example|latitude|longitude/);
});
test('existing match scoring bundles without Firebase Admin/Functions SDKs',async()=>{
  const response=await call({path:'/match-fixture',body:{scenarioId:'ranked'},jwt:await token()});
  assert.equal(response.status,200);
  const body=await response.json();
  assert.equal(body.result.dataSource,'fixture');
  assert.equal(body.researchOnly,true);
  assert.equal(body.result.matches[0].candidateId,'sample-a');
});
test('missing auth and unverified email are distinct safe errors',async()=>{
  assert.equal((await call()).status,401);
  const response=await call({jwt:await token({email_verified:false})});
  assert.equal(response.status,403);
  assert.deepEqual(await response.json(),{error:'verified-email-required'});
});
for(const [label,changes] of [
  ['wrong audience',{aud:'other-project'}],['array audience',{aud:['demo-jiran','other-project']}],
  ['wrong issuer',{iss:'https://securetoken.google.com/other-project'}],
  ['expired',{exp:1}],['missing expiry',{exp:undefined}],['future issued-at',{iat:9999999999}],
  ['missing issued-at',{iat:undefined}],['future auth time',{auth_time:9999999999}],
  ['missing auth time',{auth_time:undefined}],['empty subject',{sub:''}],['overlong subject',{sub:'x'.repeat(129)}],
  ['missing subject',{sub:undefined}],['unconfigured tenant',{firebase:{tenant:'other'}}],
]) test(`rejects ${label} before exposing fixtures`,async()=>{
  const response=await call({jwt:await token(changes)});
  assert.equal(response.status,401);
  assert.deepEqual(await response.json(),{error:'authentication-required'});
});
test('rejects unknown kid, invalid signature, unsigned and HMAC tokens',async()=>{
  const wrongKey=await generateKeyPair('RS256');
  const unsigned=`${Buffer.from(JSON.stringify({alg:'none'})).toString('base64url')}.${Buffer.from('{}').toString('base64url')}.`;
  const hmac=await token({}, {alg:'HS256'},new Uint8Array(32));
  for(const jwt of [await token({},{kid:'unrecognized'}),await token({},{},wrongKey.privateKey),unsigned,hmac]) {
    const response=await call({jwt});
    assert.equal(response.status,401);
    assert.doesNotMatch(await response.text(),/private|signature|kid|token/i);
  }
});
test('rejects malformed Authorization and token length before key use',async()=>{
  for(const authorization of ['Basic abc','Bearer a b','Bearer '+ 'x'.repeat(8193),'Bearer a, Bearer b']) {
    assert.equal((await call({headers:{authorization}})).status,401);
  }
});
test('client key URLs cannot redirect verification and a kid is mandatory',async()=>{
  const response=await call({jwt:await token({},{jku:'https://untrusted.invalid/jwks',
    x5u:'https://untrusted.invalid/cert'})});
  assert.equal(response.status,200);
  assert.equal((await call({jwt:await token({},{kid:undefined})})).status,401);
});
test('unsupported critical JWT headers are client auth failures, not key outages',async()=>{
  const jwt=await new SignJWT({sub:'generated-user'})
    .setProtectedHeader({alg:'RS256',kid:'generated-local-key',crit:['custom'],custom:true})
    .sign(privateKey,{crit:{custom:true}});
  const response=await call({jwt});
  assert.equal(response.status,401);
  assert.deepEqual(await response.json(),{error:'authentication-required'});
});
test('blocks deployed hosts and live project bindings even with valid auth',async()=>{
  assert.equal((await call({host:'research.workers.dev',jwt:await token()})).status,503);
  const live=await runtime({bindings:{FIREBASE_PROJECT_ID:'jiran-mad'}});
  assert.equal((await call({instance:live,path:'/health',method:'GET'})).status,503);
  const disabled=await runtime({bindings:{SPIKE_MODE:'disabled'}});
  assert.equal((await call({instance:disabled,path:'/health',method:'GET'})).status,503);
});
test('bounds JSON and refuses UID/scenario overrides',async()=>{
  const jwt=await token();
  for(const body of [{scenarioId:'on-time',uid:'victim'},null,[],{scenarioId:'unknown'},'{invalid']) {
    assert.equal((await call({jwt,body})).status,400);
  }
  assert.equal((await call({jwt,body:' '.repeat(2049)})).status,413);
  assert.equal((await call({jwt,body:{scenarioId:'on-time'},headers:{'content-type':'text/plain'}})).status,415);
});
test('method/path/query errors cannot reach arbitrary operations',async()=>{
  assert.equal((await call({method:'GET'})).status,405);
  assert.equal((await call({path:'/production-commute'})).status,404);
  assert.equal((await call({path:'/commute-fixture?uid=victim'})).status,400);
});
test('key-provider failure remains a sanitized unavailable response',async()=>{
  const failing=await runtime({keyStatus:503});
  const response=await call({instance:failing,jwt:await token()});
  assert.equal(response.status,503);
  assert.deepEqual(await response.json(),{error:'authentication-unavailable'});
});
