// Test-only entry point: injected time/faults never enter the main Worker bundle.
import {createQuotaStore} from '../src/quota.mjs';
export class QuotaHarness {
  constructor(ctx) { this.storage=ctx.storage; }
  async fetch(request) {
    try {
      const {limits,data,at,fail}=await request.json();
      const actual=this.storage;
      const storage={transactionSync:fn=>actual.transactionSync(fn),sql:{exec(...args){
        if(fail==='storage' || (fail==='commit' && args[0].startsWith('UPDATE quota_clock'))) {
          throw new Error('synthetic private storage detail');
        }
        return actual.sql.exec(...args);
      }}};
      const result=createQuotaStore(storage,limits).reserve(data,at);
      return Response.json(result,{status:result.reserved?200:429});
    } catch(error) {
      return Response.json({error:error.code??'test-failure'},{status:error.status??500});
    }
  }
}
export default {fetch(){return new Response('Test harness only',{status:503});}};
