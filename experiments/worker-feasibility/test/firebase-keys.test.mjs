import {before,test} from 'node:test';
import assert from 'node:assert/strict';
import {exportJWK,generateKeyPair,jwtVerify,SignJWT} from 'jose';
import {createFirebaseKeyResolver} from '../src/firebase-keys.mjs';

let a,b,privateA,privateB;
before(async()=>{
  const first=await generateKeyPair('RS256',{extractable:true});
  const second=await generateKeyPair('RS256',{extractable:true});
  a={...await exportJWK(first.publicKey),kid:'key-a',alg:'RS256',use:'sig'};
  b={...await exportJWK(second.publicKey),kid:'key-b',alg:'RS256',use:'sig'};
  privateA=first.privateKey; privateB=second.privateKey;
});
const header=kid=>({alg:'RS256',kid});
function reply(keys=[a],headers={'cache-control':'public, max-age=60'},status=200) {
  return new Response(JSON.stringify({keys}),{status,headers:{'content-type':'application/json',...headers}});
}
function setup(respond=()=>reply(),options={}) {
  let clock=0;
  const calls=[];
  const resolve=createFirebaseKeyResolver({now:()=>clock,fetchImpl:async(...args)=>{
    calls.push(args); return respond(...args);
  },...options});
  return {resolve,calls,at:value=>{clock=value;}};
}
async function signed(kid,key) {
  return new SignJWT({sub:'generated-user'}).setProtectedHeader(header(kid)).sign(key);
}
const unavailable=error=>error.status===503 && error.code==='authentication-unavailable';

test('fixed public endpoint, bounded fetch options and real JOSE signature verification',async()=>{
  const s=setup();
  const result=await jwtVerify(await signed('key-a',privateA),s.resolve,{algorithms:['RS256']});
  assert.equal(result.payload.sub,'generated-user');
  assert.equal(s.calls.length,1);
  const [url,init]=s.calls[0];
  assert.equal(url,'https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com');
  assert.equal(init.method,'GET'); assert.equal(init.redirect,'manual');
  assert.equal(new Headers(init.headers).has('authorization'),false);
  assert.ok(init.signal instanceof AbortSignal);
});
test('concurrent cold callers share one fetch; fresh known keys avoid another lookup',async()=>{
  const s=setup();
  await Promise.all(Array.from({length:20},()=>s.resolve(header('key-a'))));
  s.at(59000); await s.resolve(header('key-a'));
  assert.equal(s.calls.length,1);
});
test('max-age minus Age determines expiry, including TTL shorter than rotation cooldown',async()=>{
  const s=setup(()=>reply([a],{'cache-control':'max-age=60',age:'40'}));
  await s.resolve(header('key-a'));
  s.at(19999); await s.resolve(header('key-a')); assert.equal(s.calls.length,1);
  s.at(20000); await s.resolve(header('key-a')); assert.equal(s.calls.length,2);
});
for(const headers of [{},{'cache-control':'no-store,max-age=60'}, {'cache-control':'no-cache,max-age=60'},
  {'cache-control':'max-age=invalid'},{'cache-control':'max-age=60,max-age=120'},
  {'cache-control':'max-age=60',age:'bad'}]) {
  test(`no reuse when response freshness is unavailable or forbidden: ${JSON.stringify(headers)}`,async()=>{
    const s=setup(()=>reply([a],headers));
    await s.resolve(header('key-a')); await s.resolve(header('key-a'));
    assert.equal(s.calls.length,2);
  });
}
test('caps unusually long reuse at six hours',async()=>{
  const s=setup(()=>reply([a],{'cache-control':'max-age=86400'}));
  await s.resolve(header('key-a')); s.at(21600000);
  await s.resolve(header('key-a')); assert.equal(s.calls.length,2);
});
test('unknown kid refresh is cooldown-limited, then picks up rotation and removes old keys',async()=>{
  let round=0;
  const s=setup(()=>reply(++round===1?[a]:[b]));
  await s.resolve(header('key-a')); s.at(1000);
  await assert.rejects(s.resolve(header('key-b')),{code:'ERR_JWKS_NO_MATCHING_KEY'});
  assert.equal(s.calls.length,1);
  s.at(30000);
  assert.equal((await jwtVerify(await signed('key-b',privateB),s.resolve)).payload.sub,'generated-user');
  assert.equal(s.calls.length,2);
  await assert.rejects(s.resolve(header('key-a')),{code:'ERR_JWKS_NO_MATCHING_KEY'});
  assert.equal(s.calls.length,2);
});
test('expired keys never survive failed refresh, and an outage does not trigger a fetch storm',async()=>{
  let round=0;
  const s=setup(()=>++round===1?reply([a],{'cache-control':'max-age=1'}):new Response('secret upstream',{status:503}));
  await s.resolve(header('key-a')); s.at(1000);
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  assert.equal(s.calls.length,2);
});
test('rejects invalid client header before network while ignoring client key URLs',async()=>{
  const s=setup();
  for(const h of [{alg:'HS256',kid:'key-a'}, {alg:'RS256'}, header('x'.repeat(129)),null]) {
    await assert.rejects(s.resolve(h),{code:'ERR_JWS_INVALID'});
  }
  assert.equal(s.calls.length,0);
  await s.resolve({...header('key-a'),jku:'https://evil.invalid/keys'});
  assert.equal(s.calls.length,1);
});
for(const label of ['empty','private','duplicate','wrong-type','too-many']) {
  test(`malformed key set is unavailable and never cached: ${label}`,async()=>{
    const keys={empty:[],private:[{...a,d:'private-secret'}],duplicate:[a,a],
      'wrong-type':[{...a,kty:'oct',k:'secret'}],'too-many':Array(11).fill(a)}[label];
    const s=setup(()=>reply(keys));
    await assert.rejects(s.resolve(header('key-a')),unavailable);
    assert.equal(s.calls.length,1);
  });
}
test('oversized stream is cancelled and sanitized',async()=>{
  let cancelled=false;
  const s=setup(()=>new Response(new ReadableStream({start(controller){controller.enqueue(new Uint8Array(65537));},
    cancel(){cancelled=true;}}),{headers:{'content-type':'application/json'}}));
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  assert.equal(cancelled,true);
});
test('body deadline includes stalled stream and requests cancellation',async()=>{
  let cancelled=false;
  const s=setup(()=>new Response(new ReadableStream({cancel(){cancelled=true;}}),
    {headers:{'content-type':'application/json'}}),{timeoutMs:20});
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  assert.equal(s.calls[0][1].signal.aborted,true);
  assert.equal(cancelled,true);
});
test('late fetch after deadline cannot populate the cache',async()=>{
  let release,round=0;
  const s=setup(()=>++round===1?new Promise(resolve=>{release=resolve;}):reply([b]),{timeoutMs:20});
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  release(reply([a])); await new Promise(resolve=>setImmediate(resolve));
  s.at(30001);
  await s.resolve(header('key-b'));
  assert.equal(s.calls.length,2);
  await assert.rejects(s.resolve(header('key-a')),{code:'ERR_JWKS_NO_MATCHING_KEY'});
});
test('synchronous blocking transport cannot return a successful result after deadline',async()=>{
  const s=setup(()=>{const start=performance.now(); while(performance.now()-start<15) {} return reply();},{timeoutMs:5});
  await assert.rejects(s.resolve(header('key-a')),unavailable);
});
test('invalid JSON, non-JSON response and already-stale provider documents fail closed',async()=>{
  for(const response of [new Response('{bad',{headers:{'content-type':'application/json'}}),
    new Response('{}',{headers:{'content-type':'text/html'}}),reply([a],{'cache-control':'max-age=10',age:'11'})]) {
    const s=setup(()=>response);
    await assert.rejects(s.resolve(header('key-a')),unavailable);
  }
});
test('a stale cache header also cancels the unread response stream',async()=>{
  let cancelled=false;
  const s=setup(()=>new Response(new ReadableStream({cancel(){cancelled=true;}}),
    {headers:{'content-type':'application/json','cache-control':'max-age=1',age:'2'}}));
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  assert.equal(cancelled,true);
});
test('unknown-kid outage cannot discard still-fresh known keys, but never extends their expiry',async()=>{
  let round=0;
  const s=setup(()=>++round===1?reply():new Response('unavailable',{status:503}));
  await s.resolve(header('key-a')); s.at(30000);
  await assert.rejects(s.resolve(header('key-b')),unavailable);
  await s.resolve(header('key-a'));
  assert.equal(s.calls.length,2);
  s.at(60000);
  await assert.rejects(s.resolve(header('key-a')),unavailable);
});
test('manual redirect handling rejects a redirect response without following its Location',async()=>{
  const s=setup(()=>new Response('',{status:302,headers:{location:'https://evil.invalid'}}));
  await assert.rejects(s.resolve(header('key-a')),unavailable);
  assert.equal(s.calls.length,1);
  assert.equal(s.calls[0][1].redirect,'manual');
});
for(const headers of [{},{'cache-control':'max-age=0'},{'cache-control':'max-age=1'}]) {
  test(`unknown-key cooldown survives zero/short TTL without reusing expired keys: ${JSON.stringify(headers)}`,async()=>{
    const s=setup(()=>reply([a],headers));
    await assert.rejects(s.resolve(header('unknown-0')),{code:'ERR_JWKS_NO_MATCHING_KEY'});
    s.at(1000);
    for(let index=1;index<5;index++) {
      await assert.rejects(s.resolve(header(`unknown-${index}`)),{code:'ERR_JWKS_NO_MATCHING_KEY'});
    }
    assert.equal(s.calls.length,1);
    await s.resolve(header('key-a'));
    assert.equal(s.calls.length,2);
  });
}
test('a new key joins an already-running refresh instead of using old negative metadata',async()=>{
  let round=0,release;
  const s=setup(()=>++round===1?reply([a],{'cache-control':'max-age=1'}):new Promise(resolve=>{release=resolve;}));
  await s.resolve(header('key-a')); s.at(1000);
  const known=s.resolve(header('key-a'));
  const rotated=s.resolve(header('key-b'));
  const both=Promise.all([known,rotated]);
  release(reply([a,b]));
  await both;
  assert.equal(s.calls.length,2);
});
