// Local-only workload. This is not a production NFR1 benchmark.
import http from 'k6/http';
import {check,sleep} from 'k6';
import {SharedArray} from 'k6/data';
import {Counter} from 'k6/metrics';
import exec from 'k6/execution';
import {fixtureOptions,fixtureResult,fixtureAccounts} from './fixture-contract.mjs';

const seconds=Number(__ENV.PHASE_SECONDS||60);
export const options=fixtureOptions(seconds);
const accounts=new SharedArray('local fixture accounts',()=>fixtureAccounts(
  JSON.parse(open(__ENV.TOKEN_FILE)),Date.now(),2*seconds+150));
const endpoint='http://127.0.0.1:5001/demo-jiran/us-central1/getCommuteOptions';
const cases=['on-time','no-on-time-route','missing-data'];
const completedUsers=new Counter('completed_users');
const expected={200:http.expectedStatuses(200),400:http.expectedStatuses(400),
  401:http.expectedStatuses(401),403:http.expectedStatuses(403)};
function call(data,token,status) {
  const response=http.post(endpoint,JSON.stringify({data}),{headers:{'Content-Type':'application/json',
    ...(token?{Authorization:`Bearer ${token}`}:{})},timeout:'15s',redirects:0,responseCallback:expected[status]});
  let body;
  try {body=response.json();} catch {body=null;}
  return {response,body};
}
export function commute() {
  const account=accounts[(exec.vu.idInTest-1)%100];
  if(exec.vu.iterationInScenario===0) sleep((exec.vu.idInTest%100)/100);
  const scenario=cases[(exec.vu.iterationInScenario+exec.vu.idInTest)%cases.length];
  const {response,body}=call({scenarioId:scenario},account.token,200);
  check(response,{'fixture result matches scenario':()=>fixtureResult(response.status,body,scenario)});
  if(exec.vu.iterationInScenario===0) completedUsers.add(1);
  sleep(1);
}
export function contracts() {
  const probes=[
    {data:{scenarioId:'on-time'},status:401,error:'UNAUTHENTICATED'},
    {data:{scenarioId:'on-time'},token:accounts[100].token,status:403,error:'PERMISSION_DENIED'},
    {data:{scenarioId:'on-time'},token:'invalid-local-fixture-token',status:401,error:'UNAUTHENTICATED'},
    {data:{scenarioId:'unknown'},token:accounts[0].token,status:400,error:'INVALID_ARGUMENT'},
    {data:{scenarioId:'on-time',userId:'synthetic-other-user'},token:accounts[0].token,status:400,error:'INVALID_ARGUMENT'},
  ];
  const probe=probes[exec.vu.iterationInScenario%probes.length];
  const {response,body}=call(probe.data,probe.token,probe.status);
  check(response,{'expected auth/validation rejection':()=>response.status===probe.status && body?.error?.status===probe.error});
}
