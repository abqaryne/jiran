const assert = require('node:assert/strict');

async function checkCommuteCallable({token, unverifiedToken}) {
  assert.equal(process.env.FIREBASE_AUTH_EMULATOR_HOST, '127.0.0.1:9099');
  const endpoint = 'http://127.0.0.1:5001/demo-jiran/us-central1/getCommuteOptions';
  async function call(data, authToken) {
    const response = await fetch(endpoint, {
      method:'POST', headers:{'Content-Type':'application/json', ...(authToken ? {Authorization:`Bearer ${authToken}`} : {})},
      body:JSON.stringify({data}), signal:AbortSignal.timeout(15000),
    });
    return response.json();
  }
  assert.equal((await call({scenarioId:'on-time'})).error.status, 'UNAUTHENTICATED');
  assert.equal((await call({scenarioId:'on-time'}, unverifiedToken)).error.status, 'PERMISSION_DENIED');
  assert.equal((await call({scenarioId:'__proto__'}, token)).error.status, 'INVALID_ARGUMENT');
  assert.equal((await call({scenarioId:'on-time', userId:'victim'}, token)).error.status, 'INVALID_ARGUMENT');
  const onTime = (await call({scenarioId:'on-time'}, token)).result;
  assert.equal(onTime.dataSource, 'fixture');
  assert.equal(onTime.selectedMode, 'transit');
  assert.equal(onTime.trueMonthlyCostSen, 61200);
  assert.equal(onTime.options.length, 4);
  assert.equal(onTime.options[1].monthlyCostSen, 10000);
  assert.equal(onTime.options[1].co2GramsPerTrip, 300);
  const late = (await call({scenarioId:'no-on-time-route'}, token)).result;
  assert.equal(late.selectedMode, null);
  assert.equal(late.trueMonthlyCostSen, null);
  const missing = (await call({scenarioId:'missing-data'}, token)).result;
  assert.equal(missing.options[1].reason, 'missing_schedule');
  assert.equal(missing.options[2].reason, 'missing_fare_assumption');
  console.log('PASS: commute callable auth/validation, four modes, monthly total, no-on-time and missing-data cases');
}
module.exports = {checkCommuteCallable};
