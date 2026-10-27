import {test} from 'node:test';
import assert from 'node:assert/strict';
import {readJsonBody} from '../src/read-json.mjs';

const encoder=new TextEncoder();
function request(body,options={}) {
  return new Request('http://localhost/commute-fixture',{method:'POST',
    headers:{'content-type':'application/json'},body,duplex:'half',...options});
}
const errorCode=(status,code)=>error=>error.status===status && error.code===code;

test('reads ordinary and exactly2048-byte JSON without trusting Content-Length',async()=>{
  assert.deepEqual(await readJsonBody(request('{"scenarioId":"on-time"}')),{scenarioId:'on-time'});
  const body=JSON.stringify('x'.repeat(2046));
  assert.equal(encoder.encode(body).length,2048);
  assert.equal((await readJsonBody(request(body))).length,2046);
});
test('rejects oversized chunks and cancels a stream even with misleading length',async()=>{
  let cancelled=false;
  const stream=new ReadableStream({start(c){c.enqueue(new Uint8Array(2049));},cancel(){cancelled=true;}});
  await assert.rejects(readJsonBody(request(stream,{headers:{'content-type':'application/json','content-length':'1'}})),
    errorCode(413,'request-too-large'));
  assert.equal(cancelled,true);
});
test('stalled body reaches deadline and cancels without awaiting cancellation completion',async()=>{
  let cancelled=false;
  const stream=new ReadableStream({cancel(){cancelled=true;return new Promise(()=>{});}});
  await assert.rejects(readJsonBody(request(stream),{timeoutMs:20}),errorCode(408,'request-timeout'));
  assert.equal(cancelled,true);
});
test('partial valid JSON without end-of-stream still times out',async()=>{
  let cancelled=false;
  const stream=new ReadableStream({start(c){c.enqueue(encoder.encode('{}'));},cancel(){cancelled=true;}});
  await assert.rejects(readJsonBody(request(stream),{timeoutMs:20}),errorCode(408,'request-timeout'));
  assert.equal(cancelled,true);
});
test('elapsed-time check rejects a late completed read even before its timer can run',async()=>{
  let time=0;
  const stream=new ReadableStream({pull(c){time=21;c.enqueue(encoder.encode('{}'));c.close();}});
  await assert.rejects(readJsonBody(request(stream),{timeoutMs:20,now:()=>time}),errorCode(408,'request-timeout'));
});
test('already-aborted and later-aborted requests cancel the reader',async()=>{
  for(const early of [true,false]) {
    let cancelled=false;
    const controller=new AbortController();
    const stream=new ReadableStream({cancel(){cancelled=true;}});
    if(early) controller.abort();
    const result=readJsonBody(request(stream,{signal:controller.signal}));
    if(!early) controller.abort();
    await assert.rejects(result,errorCode(408,'request-timeout'));
    assert.equal(cancelled,true);
  }
});
test('malformed JSON, invalid UTF8 and underlying stream errors remain sanitized400',async()=>{
  for(const body of ['{private detail',new Uint8Array([0xff]),
    new ReadableStream({start(c){c.error(new Error('private transport detail'));}})]) {
    await assert.rejects(readJsonBody(request(body)),errorCode(400,'invalid-request'));
  }
});
test('missing body and unsupported content type retain400/415 contracts',async()=>{
  await assert.rejects(readJsonBody(request(null)),errorCode(400,'invalid-request'));
  await assert.rejects(readJsonBody(request('{}',{headers:{'content-type':'text/plain'}})),errorCode(415,'json-required'));
});
