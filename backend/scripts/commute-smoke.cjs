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
  assert.equal((await call({scenarioId:'per-trip'})).error.status, 'UNAUTHENTICATED');
  assert.equal((await call({scenarioId:'per-trip'}, unverifiedToken)).error.status, 'PERMISSION_DENIED');
  assert.equal((await call({scenarioId:'__proto__'}, token)).error.status, 'INVALID_ARGUMENT');
  assert.equal((await call({scenarioId:'per-trip', userId:'victim'}, token)).error.status, 'INVALID_ARGUMENT');
  const perTrip = (await call({scenarioId:'per-trip'}, token)).result;
  assert.equal(perTrip.dataSource, 'fixture');
  assert.equal(perTrip.selectedMode, 'walking');
  assert.equal(perTrip.options.length, 4);
  assert.equal(perTrip.options[1].perTripCostSen, 250);
  assert.equal(perTrip.options[1].co2GramsPerTrip, 300);
  assert.equal(perTrip.trueMonthlyCostSen, undefined);
  const noFare = (await call({scenarioId:'no-known-fare'}, token)).result;
  assert.equal(noFare.selectedMode, null);
  assert.equal(noFare.status, 'no_known_fare');
  const missing = (await call({scenarioId:'missing-data'}, token)).result;
  assert.equal(missing.options[1].reason, 'missing_schedule');
  assert.equal(missing.options[2].fareStatus, 'unavailable');
  assert.equal(missing.comparisonLimited, true);
  console.log('PASS: commute callable auth/validation, four modes, per-trip fares, no-known-fare and missing-data cases');
}
module.exports = {checkCommuteCallable};
