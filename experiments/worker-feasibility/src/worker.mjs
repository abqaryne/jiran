import commute from '../../../backend/functions/src/commute-preview.js';
import match from '../../../backend/functions/src/match-preview.js';
import {HttpError,verifyFirebaseToken} from './firebase-token.mjs';
import {readJsonBody} from './read-json.mjs';

function json(value,status=200,headers={}) {
  return Response.json(value,{status,headers:{'cache-control':'no-store',...headers}});
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
      const data=await readJsonBody(request);
      try { return json({result:handler({auth,data}),researchOnly:true}); }
      catch { throw new HttpError(400,'invalid-request'); }
    } catch(error) {
      return error instanceof HttpError?json({error:error.code},error.status):json({error:'experiment-unavailable'},503);
    }
  },
};
