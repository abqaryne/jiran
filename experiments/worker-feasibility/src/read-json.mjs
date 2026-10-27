import {HttpError} from './http-error.mjs';

/** Local prototype body limits; never waits for a hostile cancel promise. */
export async function readJsonBody(request,{timeoutMs=1000,now=()=>performance.now()}={}) {
  if(!Number.isInteger(timeoutMs) || timeoutMs<1 || timeoutMs>10000 || typeof now!=='function') {
    throw new TypeError('Invalid body reader configuration');
  }
  if(!/^application\/json(?:\s*;.*)?$/i.test(request.headers.get('content-type')??'')) {
    throw new HttpError(415,'json-required');
  }
  if(!request.body) throw new HttpError(400,'invalid-request');
  const reader=request.body.getReader();
  const started=now();
  let expired=false,rejectDeadline,timer;
  const deadline=new Promise((resolve,reject)=>{rejectDeadline=reject;});
  function cancel() { reader.cancel().catch(()=>{}); }
  function expire() {
    expired=true;
    rejectDeadline(new HttpError(408,'request-timeout'));
    cancel();
  }
  function active() {
    const current=now();
    if(expired || request.signal.aborted || !Number.isFinite(started) || !Number.isFinite(current)
        || current<started || current-started>=timeoutMs) {
      throw new HttpError(408,'request-timeout');
    }
  }
  async function execute() {
    active();
    const chunks=[];
    let size=0;
    while(true) {
      const {done,value}=await reader.read();
      active();
      if(done) break;
      size+=value.byteLength;
      if(size>2048) throw new HttpError(413,'request-too-large');
      chunks.push(value);
    }
    const bytes=new Uint8Array(size);
    let offset=0;
    for(const chunk of chunks) { bytes.set(chunk,offset); offset+=chunk.byteLength; }
    const result=JSON.parse(new TextDecoder('utf-8',{fatal:true}).decode(bytes));
    active();
    return result;
  }
  try {
    timer=setTimeout(expire,timeoutMs);
    request.signal.addEventListener('abort',expire,{once:true});
    return await Promise.race([deadline,execute()]);
  } catch(error) {
    cancel();
    if(error instanceof HttpError) throw error;
    throw new HttpError(400,'invalid-request');
  } finally {
    clearTimeout(timer);
    request.signal.removeEventListener('abort',expire);
    reader.releaseLock();
  }
}
