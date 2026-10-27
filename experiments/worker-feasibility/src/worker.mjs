import commute from '../../../functions/src/commute-preview.js';
import match from '../../../functions/src/match-preview.js';
import {HttpError,verifyFirebaseToken} from './firebase-token.mjs';

function json(value,status=200,headers={}) {
  return Response.json(value,{status,headers:{'cache-control':'no-store',...headers}});
}
async function readBody(request) {
  if (!/^application\/json(?:\s*;.*)?$/i.test(request.headers.get('content-type')??'')) {
    throw new HttpError(415,'json-required');
  }
  if (!request.body) throw new HttpError(400,'invalid-request');
  const reader=request.body.getReader();
  let size=0;
  const chunks=[];
  try {
    while(true) {
      const {done,value}=await reader.read();
      if(done) break;
      size+=value.byteLength;
      if(size>2048) {
        await reader.cancel().catch(()=>{});
        throw new HttpError(413,'request-too-large');
      }
      chunks.push(value);
    }
    const bytes=new Uint8Array(size);
    let offset=0;
    for(const chunk of chunks) { bytes.set(chunk,offset); offset+=chunk.byteLength; }
    return JSON.parse(new TextDecoder('utf-8',{fatal:true}).decode(bytes));
  } catch(error) {
    if(error instanceof HttpError) throw error;
    throw new HttpError(400,'invalid-request');
  } finally { reader.releaseLock(); }
}

export default {
  async fetch(request,env) {
    try {
      const url=new URL(request.url);
      if (!['localhost','127.0.0.1','[::1]'].includes(url.hostname)
          || env.SPIKE_MODE!=='local-feasibility' || env.FIREBASE_PROJECT_ID!=='demo-jiran') {
        throw new HttpError(503,'local-experiment-only');
      }
      if(url.search) throw new HttpError(400,'invalid-request');
      if(url.pathname==='/health' && request.method==='GET') {
        return json({status:'local-feasibility',dataSource:'fixture'});
      }
      const handler={'/commute-fixture':commute.commutePreview,'/match-fixture':match.matchPreview}[url.pathname];
      if(!handler) throw new HttpError(404,'not-found');
      if(request.method!=='POST') return json({error:'method-not-allowed'},405,{allow:'POST'});
      const auth=await verifyFirebaseToken(request);
      const data=await readBody(request);
      try { return json({result:handler({auth,data}),researchOnly:true}); }
      catch { throw new HttpError(400,'invalid-request'); }
    } catch(error) {
      return error instanceof HttpError?json({error:error.code},error.status):json({error:'experiment-unavailable'},503);
    }
  },
};
