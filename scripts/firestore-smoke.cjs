const assert = require('node:assert/strict');

async function checkConnectionRules({token, unverifiedToken, uid}) {
  const host = process.env.FIRESTORE_EMULATOR_HOST;
  assert.equal(host, '127.0.0.1:8080');
  const base = `http://${host}/v1/projects/demo-jiran/databases/(default)/documents`;
  const name = `projects/demo-jiran/databases/(default)/documents/_connectionChecks/${uid}`;
  async function commit(fields, authToken, documentName = name, transform = true) {
    const update = {update: {name: documentName, fields}};
    if (transform) {
      update.updateTransforms = [{fieldPath: 'checkedAt', setToServerValue: 'REQUEST_TIME'}];
    }
    return fetch(`${base}:commit`, {
      method: 'POST', headers: {'Content-Type': 'application/json', Authorization: `Bearer ${authToken}`},
      body: JSON.stringify({writes: [update]}), signal: AbortSignal.timeout(15000),
    });
  }
  assert.equal((await commit({}, unverifiedToken)).status, 403, 'Unverified caller must be denied');
  assert.equal((await commit({}, token, `${name}-another-user`)).status, 403, 'Cross-user create denied');
  assert.equal((await commit({role: {stringValue: 'admin'}}, token)).status, 403, 'Unknown field denied');
  assert.equal((await commit({}, token)).status, 200, 'Verified owner can create a timestamp-only check');
  assert.equal((await commit({}, token)).status, 200, 'Verified owner can update a valid check');
  assert.equal((await commit({}, token, name, false)).status, 403, 'Update must retain required timestamp');
  assert.equal((await commit({role: {stringValue: 'admin'}}, token)).status, 403, 'Invalid update denied');
  assert.equal((await fetch(`${base}/_connectionChecks/${uid}`, {
    headers: {Authorization: `Bearer ${token}`},
  })).status, 200, 'Owner can read own check');
  assert.equal((await fetch(`${base}/_connectionChecks/${uid}`)).status, 403, 'Anonymous read denied');
  assert.equal((await fetch(`${base}/_connectionChecks/${uid}`, {
    headers: {Authorization: `Bearer ${unverifiedToken}`},
  })).status, 403, 'Unverified read denied');
  assert.equal((await fetch(`${base}/_connectionChecks/${uid}-another-user`, {
    headers: {Authorization: `Bearer ${token}`},
  })).status, 403, 'Cross-user read denied');
  console.log('PASS: Firestore verified-owner create/read/update, missing/extra field and unauthorized access checks');
}
module.exports = {checkConnectionRules};
