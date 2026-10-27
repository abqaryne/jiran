const assert = require('node:assert/strict');
const {execFileSync} = require('node:child_process');
const path = require('node:path');
const {checkConnectionRules} = require('./firestore-smoke.cjs');
const {checkCommuteCallable} = require('./commute-smoke.cjs');
const {checkMatchCallable} = require('./match-smoke.cjs');

const project = 'demo-jiran';
const authHost = process.env.FIREBASE_AUTH_EMULATOR_HOST;
const firestoreHost = process.env.FIRESTORE_EMULATOR_HOST;
assert.equal(authHost, '127.0.0.1:9099', 'Run through the local emulator config');
assert.equal(firestoreHost, '127.0.0.1:8080');
const authBase = `http://${authHost}/identitytoolkit.googleapis.com/v1`;
const callable = `http://127.0.0.1:5001/${project}/us-central1/sessionCheck`;

async function post(url, body, token) {
  const response = await fetch(url, {
    method: 'POST',
    headers: {'Content-Type': 'application/json', ...(token ? {Authorization: `Bearer ${token}`} : {})},
    body: JSON.stringify(body),
    signal: AbortSignal.timeout(15000),
  });
  return {status: response.status, body: await response.json()};
}

async function main() {
  const anonymous = await post(callable, {data: {userId: 'victim'}});
  assert.equal(anonymous.body.error.status, 'UNAUTHENTICATED');

  const email = `smoke-${Date.now()}@example.com`;
  const password = 'local-test-password-123';
  const signup = await post(`${authBase}/accounts:signUp?key=fake-key`, {
    email, password, returnSecureToken: true,
  });
  assert.equal(signup.status, 200, JSON.stringify(signup.body));
  const unverified = await post(callable, {data: {}}, signup.body.idToken);
  assert.equal(unverified.body.error.status, 'PERMISSION_DENIED');

  // Emulator-only admin credential; never valid for a real Firebase project.
  const verified = await post(`${authBase}/accounts:update?key=fake-key`, {
    localId: signup.body.localId, emailVerified: true,
  }, 'owner');
  assert.equal(verified.status, 200, JSON.stringify(verified.body));
  const login = await post(`${authBase}/accounts:signInWithPassword?key=fake-key`, {
    email, password, returnSecureToken: true,
  });
  assert.equal(login.status, 200, JSON.stringify(login.body));
  const success = await post(callable, {data: {userId: 'victim'}}, login.body.idToken);
  assert.equal(success.status, 200, JSON.stringify(success.body));
  assert.deepEqual(success.body.result, {uid: signup.body.localId, status: 'ok'});
  await checkConnectionRules({token: login.body.idToken,
    unverifiedToken: signup.body.idToken, uid: signup.body.localId});
  await checkCommuteCallable({token: login.body.idToken, unverifiedToken: signup.body.idToken});
  await checkMatchCallable({token: login.body.idToken, unverifiedToken: signup.body.idToken});

  const docUrl = `http://${firestoreHost}/v1/projects/${project}/databases/(default)/documents/_diagnostics/${signup.body.localId}`;
  const stored = await fetch(docUrl, {headers: {Authorization: 'Bearer owner'}});
  assert.equal(stored.status, 200);
  const denied = await fetch(docUrl, {headers: {Authorization: `Bearer ${login.body.idToken}`}});
  assert.equal(denied.status, 403, 'Client reads must remain denied by baseline rules');
  console.log('PASS: anonymous/unverified rejection, token identity, Functions→Firestore write, client read denial');
  console.log(`Android test account (local only): ${email} / ${password}`);
  if (process.argv.includes('--android')) {
    const sdk = process.env.ANDROID_HOME || process.env.ANDROID_SDK_ROOT;
    assert.ok(sdk, 'Set ANDROID_HOME before the Android smoke test');
    const adb = path.join(sdk, 'platform-tools', process.platform === 'win32' ? 'adb.exe' : 'adb');
    const runAdb = (args) => execFileSync(adb, args, {encoding: 'utf8', timeout: 120000});
    runAdb(['install', '-r', 'app/build/outputs/apk/debug/app-debug.apk']);
    runAdb(['install', '-r', '-t', 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk']);
    const deviceSdk = Number(runAdb(['shell', 'getprop', 'ro.build.version.sdk']).trim());
    if (deviceSdk >= 37) {
      runAdb(['shell', 'pm', 'grant', 'jiran.com', 'android.permission.ACCESS_LOCAL_NETWORK']);
    }
    const output = runAdb(['shell', 'am', 'instrument', '-w', '-e', 'class',
      'com.jiran.app.FirebaseConnectionTest,com.jiran.app.EmailPasswordFlowTest,com.jiran.app.CommuteIntegrationTest,com.jiran.app.CommutePreviewTest,com.jiran.app.MatchIntegrationTest,com.jiran.app.MatchPreviewTest', '-e', 'email', email,
      '-e', 'password', password, '-e', 'capturePreview', 'true', 'jiran.com.test/androidx.test.runner.AndroidJUnitRunner']);
    console.log(output);
    assert.match(output, /OK \(6 tests\)/, 'All six Android integration tests must pass');
  }
}

main().catch((error) => { console.error(error); process.exitCode = 1; });
