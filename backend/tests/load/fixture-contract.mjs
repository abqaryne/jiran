export function fixtureOptions(seconds=60) {
  if(!Number.isInteger(seconds) || seconds<1 || seconds>300) throw new Error('Invalid fixture phase duration');
  return {maxRedirects:0,scenarios:{
    warmup:{executor:'constant-vus',exec:'commute',vus:5,duration:'10s',gracefulStop:'5s',tags:{phase:'warmup'}},
    baseline50:{executor:'constant-vus',exec:'commute',vus:50,duration:`${seconds}s`,startTime:'15s',gracefulStop:'5s',tags:{phase:'baseline50'}},
    capacity100:{executor:'constant-vus',exec:'commute',vus:100,duration:`${seconds}s`,startTime:`${20+seconds}s`,gracefulStop:'5s',tags:{phase:'capacity100'}},
    contracts:{executor:'per-vu-iterations',exec:'contracts',vus:1,iterations:5,startTime:`${25+2*seconds}s`,maxDuration:'90s',gracefulStop:'5s',tags:{phase:'contracts'}},
  },thresholds:{
    'http_req_duration{phase:baseline50}':['p(95)<3000'],
    'http_req_duration{phase:capacity100}':['p(95)<3000'],
    'completed_users{phase:baseline50}':['count==50'],
    'completed_users{phase:capacity100}':['count==100'],
    'iterations{phase:contracts}':['count==5'],
    http_req_failed:['rate<0.01'],checks:['rate==1'],
  }};
}
export function fixtureResult(status,body,scenario) {
  const result=body?.result;
  if(status!==200 || !result || result.dataSource!=='fixture' || result.scenarioId!==scenario
      || !Array.isArray(result.options) || result.options.length!==4) return false;
  if(scenario==='on-time') return result.selectedMode==='transit' && result.trueMonthlyCostSen===61200;
  if(scenario==='no-on-time-route') return result.selectedMode===null && result.trueMonthlyCostSen===null;
  if(scenario==='missing-data') return result.options[1]?.reason==='missing_schedule'
    && result.options[2]?.reason==='missing_fare_assumption';
  return false;
}
export function fixtureAccounts(value,now,minimumLifetimeSeconds) {
  if(!value || value.mode!=='local-fixture' || value.projectId!=='demo-jiran'
      || !Number.isInteger(minimumLifetimeSeconds) || minimumLifetimeSeconds<1 || minimumLifetimeSeconds>3600
      || !Number.isFinite(now) || !Number.isFinite(value.createdAt) || value.createdAt>now || now-value.createdAt>600000
      || !Number.isFinite(value.expiresAt) || value.expiresAt-now<minimumLifetimeSeconds*1000
      || !Array.isArray(value.verified) || value.verified.length!==100) throw new Error('Invalid or expired local fixture accounts');
  const users=[...value.verified,value.unverified];
  if(users.some(user=>!user || typeof user.uid!=='string' || !user.uid || user.uid.length>128
      || typeof user.token!=='string' || !user.token || user.token.length>8192)
      || new Set(users.map(user=>user.uid)).size!==101 || new Set(users.map(user=>user.token)).size!==101) {
    throw new Error('Local fixture accounts must be distinct');
  }
  return users;
}
