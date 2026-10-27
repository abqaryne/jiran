const assert = require('node:assert/strict');

async function checkMatchCallable({token, unverifiedToken}) {
  assert.equal(process.env.FIREBASE_AUTH_EMULATOR_HOST, '127.0.0.1:9099');
  const endpoint = 'http://127.0.0.1:5001/demo-jiran/us-central1/computeMatchScores';
  async function call(data, authToken) {
    const response = await fetch(endpoint, {
      method:'POST', headers:{'Content-Type':'application/json', ...(authToken ? {Authorization:`Bearer ${authToken}`} : {})},
      body:JSON.stringify({data}), signal:AbortSignal.timeout(15000),
    });
    return response.json();
  }
  assert.equal((await call({scenarioId:'ranked'})).error.status, 'UNAUTHENTICATED');
  assert.equal((await call({scenarioId:'ranked'}, unverifiedToken)).error.status, 'PERMISSION_DENIED');
  for (const data of [{scenarioId:'__proto__'},{scenarioId:'ranked',userId:'victim'},
    {scenarioId:'ranked',weights:{}},{scenarioId:'ranked',candidateIds:['victim']}]) {
    assert.equal((await call(data, token)).error.status,'INVALID_ARGUMENT');
  }
  const result = (await call({scenarioId:'ranked'}, token)).result;
  assert.equal(result.dataSource,'fixture');
  assert.equal(result.formulaVersion,'normalized-v1');
  assert.equal(result.weightVersion,'synthetic-v1');
  assert.deepEqual(result.matches.map(m=>[m.candidateId,m.score]),[['sample-a',100],['sample-b',50]]);
  assert.deepEqual(result.matches[0].overlappingClassDays,[1,3]);
  assert.deepEqual((await call({scenarioId:'ranked'}, token)).result,result);
  assert.doesNotMatch(JSON.stringify(result), /"(?:email|phone|timetable|answers|uid)"/);
  const missing = (await call({scenarioId:'missing-answers'}, token)).result.matches[0];
  assert.equal(missing.score,null);
  assert.equal(missing.status,'insufficient_data');
  assert.deepEqual(missing.missingFactors,['smoking']);
  assert.deepEqual((await call({scenarioId:'empty'}, token)).result.matches,[]);
  console.log('PASS: match callable auth, deterministic ranking, missing data, safe projection and empty candidates');
}
module.exports = {checkMatchCallable};
