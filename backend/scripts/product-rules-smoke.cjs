const assert = require('node:assert/strict');

function str(v) { return {stringValue: v}; }
function int(v) { return {integerValue: String(v)}; }
function bool(v) { return {booleanValue: Boolean(v)}; }
function encodeField(val) {
  if (typeof val === 'string') return str(val);
  if (typeof val === 'number') return int(val);
  if (typeof val === 'boolean') return bool(val);
  if (Array.isArray(val)) return {arrayValue: {values: val.map(encodeField)}};
  if (val && typeof val === 'object') {
    const fields = {};
    for (const [k, v] of Object.entries(val)) {
      fields[k] = encodeField(v);
    }
    return {mapValue: {fields}};
  }
  return {nullValue: null};
}

function encodeFields(data) {
  const fields = {};
  for (const [k, v] of Object.entries(data)) {
    fields[k] = encodeField(v);
  }
  return fields;
}

function makeClient(base, token) {
  return {
    async get(path) {
      return fetch(`${base}/${path}`, {
        headers: token ? {Authorization: `Bearer ${token}`} : {},
        signal: AbortSignal.timeout(10000),
      });
    },
    async set(path, data) {
      return fetch(`${base}/${path}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? {Authorization: `Bearer ${token}`} : {}),
        },
        body: JSON.stringify({fields: encodeFields(data)}),
        signal: AbortSignal.timeout(10000),
      });
    },
    async delete(path) {
      return fetch(`${base}/${path}`, {
        method: 'DELETE',
        headers: token ? {Authorization: `Bearer ${token}`} : {},
        signal: AbortSignal.timeout(10000),
      });
    },
  };
}

async function createAuthUser(authBase, email, password, emailVerified = true) {
  const signupRes = await fetch(`${authBase}/accounts:signUp?key=fake-key`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({email, password, returnSecureToken: true}),
  });
  assert.equal(signupRes.status, 200, 'User creation failed');
  const signup = await signupRes.json();
  const uid = signup.localId;

  if (emailVerified) {
    const updateRes = await fetch(`${authBase}/accounts:update?key=fake-key`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json', Authorization: 'Bearer owner'},
      body: JSON.stringify({localId: uid, emailVerified: true}),
    });
    assert.equal(updateRes.status, 200, 'User verification failed');
    const loginRes = await fetch(`${authBase}/accounts:signInWithPassword?key=fake-key`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({email, password, returnSecureToken: true}),
    });
    assert.equal(loginRes.status, 200, 'User login failed');
    const login = await loginRes.json();
    return {uid, email, token: login.idToken};
  } else {
    return {uid, email, token: signup.idToken};
  }
}

async function checkProductRules({authBase, firestoreHost, project}) {
  const base = `http://${firestoreHost}/v1/projects/${project}/databases/(default)/documents`;
  const admin = makeClient(base, 'owner');
  const anon = makeClient(base, null);

  const nonce = Date.now();
  console.log('Setting up test accounts for NFR2 Security Rules suite...');
  const studentA = await createAuthUser(authBase, `student-a-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const studentB = await createAuthUser(authBase, `student-b-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const studentC = await createAuthUser(authBase, `student-c-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const unverified = await createAuthUser(authBase, `unverified-${nonce}@siswa.um.edu.my`, 'pass123', false);
  const staff = await createAuthUser(authBase, `staff-${nonce}@um.edu.my`, 'pass123', true);
  const outsider = await createAuthUser(authBase, `outsider-${nonce}@example.com`, 'pass123', true);

  const clientA = makeClient(base, studentA.token);
  const clientB = makeClient(base, studentB.token);
  const clientC = makeClient(base, studentC.token);
  const clientUnverified = makeClient(base, unverified.token);
  const clientStaff = makeClient(base, staff.token);
  const clientOutsider = makeClient(base, outsider.token);

  let deniedCount = 0;
  let passedCount = 0;

  async function assertDenied(promise, label) {
    const res = await promise;
    assert.equal(res.status, 403, `Expected DENIED (403) for: ${label}, got ${res.status}`);
    deniedCount++;
  }

  async function assertPermitted(promise, label) {
    const res = await promise;
    assert.ok(res.status >= 200 && res.status < 300, `Expected PERMITTED (2xx) for: ${label}, got ${res.status}`);
    passedCount++;
  }

  console.log('1. Testing Access Baseline (ADR-0006)...');
  await admin.set('areas/pantai_dalam', {name: 'Pantai Dalam', lat: 3.11, lng: 101.66});

  await assertDenied(anon.get('areas/pantai_dalam'), 'Anonymous read');
  await assertDenied(clientUnverified.get('areas/pantai_dalam'), 'Unverified student read');
  await assertDenied(clientOutsider.get('areas/pantai_dalam'), 'Non-UM email read');
  await assertDenied(clientStaff.get('areas/pantai_dalam'), 'Staff um.edu.my email read');
  await assertPermitted(clientA.get('areas/pantai_dalam'), 'Verified siswa student read areas');
  await assertDenied(clientA.set('areas/pantai_dalam', {name: 'Hacked'}), 'Student write to areas');

  console.log('2. Testing Public Profile & Privacy Subdocuments (NFR2)...');
  await assertPermitted(
    clientA.set(`users/${studentA.uid}`, {uid: studentA.uid, initials: 'S.A.', gender: 'female', faculty: 'FSKTM', year: 2}),
    'Student A creates own public profile'
  );
  await assertDenied(
    clientB.set(`users/${studentA.uid}`, {uid: studentA.uid, initials: 'H.B.'}),
    'Student B writes to Student A public profile'
  );
  await assertPermitted(clientB.get(`users/${studentA.uid}`), 'Student B reads Student A public profile');

  await assertPermitted(
    clientA.set(`users/${studentA.uid}/private/profile`, {fullName: 'Student A Full', budgetMinSen: 30000, privacy: {showPhoneToConnections: false}}),
    'Student A creates own private profile'
  );
  await assertPermitted(clientA.get(`users/${studentA.uid}/private/profile`), 'Student A reads own private profile');
  await assertDenied(clientB.get(`users/${studentA.uid}/private/profile`), 'Student B reads Student A private profile');

  await assertPermitted(
    clientA.set(`users/${studentA.uid}/private/contact`, {firstName: 'Aisyah'}),
    'Student A creates own contact'
  );
  await assertPermitted(
    clientA.set(`users/${studentA.uid}/private/phone`, {phoneNumber: '+60123456789'}),
    'Student A creates own phone'
  );
  await assertPermitted(
    clientA.set(`users/${studentA.uid}/private/timetable`, {entries: [{day: 1, start: '09:00', end: '11:00', building: 'FSKTM'}]}),
    'Student A creates own timetable'
  );
  await assertPermitted(
    clientA.set(`users/${studentA.uid}/private/photo`, {photoUrl: 'https://example.com/photo.jpg'}),
    'Student A creates own photo'
  );

  await assertDenied(clientB.get(`users/${studentA.uid}/private/contact`), 'Stranger reads Student A contact');
  await assertDenied(clientB.get(`users/${studentA.uid}/private/phone`), 'Stranger reads Student A phone');
  await assertDenied(clientB.get(`users/${studentA.uid}/private/timetable`), 'Stranger reads Student A timetable');
  await assertDenied(clientB.get(`users/${studentA.uid}/private/photo`), 'Stranger reads Student A photo');

  console.log('3. Testing Contract Checks Privacy (ADR-0005)...');
  await assertPermitted(
    clientA.set(`contractChecks/cc_${nonce}`, {uid: studentA.uid, summary: 'OK'}),
    'Student A creates contract check'
  );
  await assertPermitted(clientA.get(`contractChecks/cc_${nonce}`), 'Student A reads own contract check');
  await assertDenied(clientB.get(`contractChecks/cc_${nonce}`), 'Student B reads Student A contract check');

  console.log('4. Testing Connection Unlocking Privacy (ADR-0007)...');
  const pairId = studentA.uid < studentB.uid ? `${studentA.uid}_${studentB.uid}` : `${studentB.uid}_${studentA.uid}`;

  await assertDenied(clientA.set(`connections/${pairId}`, {status: 'active'}), 'Client creates connection');
  await assertDenied(clientA.delete(`connections/${pairId}`), 'Client deletes connection');

  await admin.set(`connections/${pairId}`, {
    pairId,
    uidA: studentA.uid < studentB.uid ? studentA.uid : studentB.uid,
    uidB: studentA.uid < studentB.uid ? studentB.uid : studentA.uid,
    status: 'active',
  });

  await assertPermitted(clientB.get(`users/${studentA.uid}/private/contact`), 'Connected Student B reads Student A firstName');
  await assertPermitted(clientA.get(`users/${studentB.uid}/private/contact`), 'Connected Student A reads Student B firstName');
  await assertDenied(clientC.get(`users/${studentA.uid}/private/contact`), 'Third-party Student C reads Student A contact');

  await assertDenied(clientB.get(`users/${studentA.uid}/private/phone`), 'Connected Student B reads phone when opt-in is false');
  await clientA.set(`users/${studentA.uid}/private/profile`, {privacy: {showPhoneToConnections: true}});
  await assertPermitted(clientB.get(`users/${studentA.uid}/private/phone`), 'Connected Student B reads phone when opt-in is true');

  await assertPermitted(clientA.set(`connections/${pairId}`, {status: 'ended'}), 'Participant ends connection');
  await assertDenied(clientC.set(`connections/${pairId}`, {status: 'ended'}), 'Stranger ends connection');

  console.log('5. Testing Listing Requests & Location Privacy...');
  const listingId = `listing_${nonce}`;
  await assertPermitted(
    clientA.set(`listings/${listingId}`, {listingId, ownerUid: studentA.uid, totalRentSen: 120000}),
    'Host A creates listing'
  );
  await assertPermitted(
    clientA.set(`listings/${listingId}/private/location`, {exactAddress: 'A-12-03, Pantai Hillpark', residentUids: [studentA.uid]}),
    'Host A sets exact location'
  );

  await assertDenied(clientB.get(`listings/${listingId}/private/location`), 'Applicant reads exact address before request accepted');

  const reqId = `${listingId}_${studentB.uid}`;
  await assertPermitted(
    clientB.set(`listingRequests/${reqId}`, {requestId: reqId, listingId, hostUid: studentA.uid, requesterUid: studentB.uid, status: 'pending'}),
    'Student B sends viewing request to Host A'
  );
  await assertDenied(clientC.get(`listingRequests/${reqId}`), 'Third-party reads listing request');

  await assertPermitted(
    clientA.set(`listingRequests/${reqId}`, {status: 'accepted'}),
    'Host A accepts listing request'
  );

  await assertPermitted(clientB.get(`listings/${listingId}/private/location`), 'Accepted applicant B reads exact address');
  await assertDenied(clientC.get(`listings/${listingId}/private/location`), 'Unaccepted student C reads exact address');

  console.log('6. Testing Household & Co-household Data Isolation (Module 5)...');
  const householdId = `hh_${nonce}`;
  await admin.set(`households/${householdId}`, {
    householdId,
    memberUids: [studentA.uid, studentB.uid],
  });

  await assertPermitted(
    clientA.set(`expenses/exp_${nonce}`, {householdId, description: 'Wi-Fi', amountSen: 10000}),
    'Household member A adds shared expense'
  );
  await assertPermitted(clientB.get(`expenses/exp_${nonce}`), 'Household member B reads shared expense');
  await assertDenied(clientC.get(`expenses/exp_${nonce}`), 'Non-member C reads shared expense');
  await assertDenied(clientC.get(`households/${householdId}`), 'Non-member C reads household');

  await admin.set(`users/${studentA.uid}/private/membership`, {householdId});
  await admin.set(`users/${studentB.uid}/private/membership`, {householdId});

  await assertPermitted(clientB.get(`users/${studentA.uid}/private/timetable`), 'Co-household member B reads timetable');
  await assertPermitted(clientB.get(`users/${studentA.uid}/private/photo`), 'Co-household member B reads photo');
  await assertDenied(clientC.get(`users/${studentA.uid}/private/timetable`), 'Non-household member C reads timetable');

  console.log('7. Testing Chats & Messages (Module 3 & 6)...');
  const chatId = `chat_${nonce}`;
  await assertPermitted(
    clientA.set(`chats/${chatId}`, {chatId, participantUids: [studentA.uid, studentB.uid]}),
    'Student A creates chat with Student B'
  );
  await assertPermitted(
    clientB.set(`chats/${chatId}/messages/m1`, {messageId: 'm1', senderUid: studentB.uid, text: 'Hello!'}),
    'Participant B sends message in chat'
  );
  await assertDenied(
    clientC.set(`chats/${chatId}/messages/m2`, {messageId: 'm2', senderUid: studentC.uid, text: 'Intruder'}),
    'Stranger C sends message in chat'
  );
  await assertDenied(clientC.get(`chats/${chatId}`), 'Stranger C reads chat');

  console.log(`\n========================================================`);
  console.log(`PASS: All NFR2 Security Rules checks succeeded!`);
  console.log(`Verified Denials (Unauthorized cases rejected): ${deniedCount}`);
  console.log(`Verified Permissions (Authorized cases allowed):  ${passedCount}`);
  console.log(`========================================================\n`);
}

module.exports = {checkProductRules};

if (require.main === module) {
  const authHost = process.env.FIREBASE_AUTH_EMULATOR_HOST || '127.0.0.1:9099';
  const firestoreHost = process.env.FIRESTORE_EMULATOR_HOST || '127.0.0.1:8080';
  const project = 'demo-jiran';
  const authBase = `http://${authHost}/identitytoolkit.googleapis.com/v1`;

  checkProductRules({authBase, firestoreHost, project})
    .catch((err) => {
      console.error('NFR2 Security Rules Test Failure:', err);
      process.exit(1);
    });
}
