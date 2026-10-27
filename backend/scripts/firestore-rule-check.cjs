const assert = require('node:assert/strict');
const {checkConnectionRules} = require('./firestore-smoke.cjs');
async function main() {
  assert.equal(process.env.FIREBASE_AUTH_EMULATOR_HOST, '127.0.0.1:9099');
  const base = 'http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1';
  async function post(method, data, admin = false) {
    const response = await fetch(`${base}/${method}?key=fake-key`, {
      method:'POST', headers:{'Content-Type':'application/json', ...(admin ? {Authorization:'Bearer owner'} : {})},
      body:JSON.stringify(data), signal:AbortSignal.timeout(15000),
    });
    assert.equal(response.status, 200);
    return response.json();
  }
  const email = `rules-${Date.now()}@example.com`;
  const password = 'local-test-password-123';
  const created = await post('accounts:signUp', {email, password, returnSecureToken:true});
  await post('accounts:update', {localId:created.localId, emailVerified:true}, true);
  const verified = await post('accounts:signInWithPassword', {email, password, returnSecureToken:true});
  await checkConnectionRules({token:verified.idToken, unverifiedToken:created.idToken, uid:created.localId});
}
main().catch(error => {console.error(error);process.exitCode=1;});
