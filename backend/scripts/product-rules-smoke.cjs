const assert = require('node:assert/strict');

function encodeField(val) {
  if (val instanceof Date) return {timestampValue: val.toISOString()};
  if (val === null || val === undefined) return {nullValue: null};
  if (typeof val === 'string') return {stringValue: val};
  if (typeof val === 'number') {
    return Number.isInteger(val) ? {integerValue: String(val)} : {doubleValue: val};
  }
  if (typeof val === 'boolean') return {booleanValue: val};
  if (Array.isArray(val)) return {arrayValue: {values: val.map(encodeField)}};
  if (typeof val === 'object') {
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

function makeClient(base, token, project) {
  const docPrefix = `projects/${project}/databases/(default)/documents`;
  return {
    async queryListings(field, value) {
      return fetch(`${base}:runQuery`, {method: 'POST',
        headers: {'Content-Type': 'application/json', ...(token ? {Authorization: `Bearer ${token}`} : {})},
        body: JSON.stringify({structuredQuery: {from: [{collectionId: 'listings'}],
          ...(field ? {where: {fieldFilter: {field: {fieldPath: field}, op: 'EQUAL',
            value: {stringValue: value}}}} : {})}}), signal: AbortSignal.timeout(10000)});
    },
    async commit(writes) {
      return fetch(`${base}:commit`, {method: 'POST',
        headers: {'Content-Type': 'application/json', ...(token ? {Authorization: `Bearer ${token}`} : {})},
        body: JSON.stringify({writes}), signal: AbortSignal.timeout(10000)});
    },
    async get(path) {
      return fetch(`${base}/${path}`, {
        headers: token ? {Authorization: `Bearer ${token}`} : {},
        signal: AbortSignal.timeout(10000),
      });
    },
    async set(path, data, maskFields = null) {
      const update = {
        name: `${docPrefix}/${path}`,
        fields: encodeFields(data),
      };
      const write = {update};
      if (/^listings\/[^/]+$/.test(path)) {
        write.updateTransforms = [{fieldPath: 'updatedAt', setToServerValue: 'REQUEST_TIME'}];
        delete update.fields.updatedAt;
      }
      if (maskFields && Array.isArray(maskFields)) {
        write.updateMask = {fieldPaths: maskFields};
      }
      return fetch(`${base}:commit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? {Authorization: `Bearer ${token}`} : {}),
        },
        body: JSON.stringify({writes: [write]}),
        signal: AbortSignal.timeout(10000),
      });
    },
    async delete(path) {
      return fetch(`${base}:commit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token ? {Authorization: `Bearer ${token}`} : {}),
        },
        body: JSON.stringify({writes: [{delete: `${docPrefix}/${path}`}]}),
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
  const admin = makeClient(base, 'owner', project);
  const anon = makeClient(base, null, project);

  const nonce = Date.now();
  console.log('Setting up test accounts for NFR2 Security Rules suite...');
  const studentA = await createAuthUser(authBase, `student-a-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const studentB = await createAuthUser(authBase, `student-b-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const studentC = await createAuthUser(authBase, `student-c-${nonce}@siswa.um.edu.my`, 'pass123', true);
  const unverified = await createAuthUser(authBase, `unverified-${nonce}@siswa.um.edu.my`, 'pass123', false);
  const staff = await createAuthUser(authBase, `staff-${nonce}@um.edu.my`, 'pass123', true);
  const outsider = await createAuthUser(authBase, `outsider-${nonce}@example.com`, 'pass123', true);

  const clientA = makeClient(base, studentA.token, project);
  const clientB = makeClient(base, studentB.token, project);
  const clientC = makeClient(base, studentC.token, project);
  const clientUnverified = makeClient(base, unverified.token, project);
  const clientStaff = makeClient(base, staff.token, project);
  const clientOutsider = makeClient(base, outsider.token, project);

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
  const setAreaRes = await admin.set('areas/pantai_dalam', {name: 'Pantai Dalam', lat: 3.11, lng: 101.66});
  assert.equal(setAreaRes.status, 200, 'Admin setup area must succeed');

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
    clientB.set(`users/${studentB.uid}/private/contact`, {firstName: 'Bob'}),
    'Student B creates own contact'
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

  console.log('4. Testing Connection Unlocking Privacy (ADR-0007) and Connected Chats...');
  const pairId = studentA.uid < studentB.uid ? `${studentA.uid}_${studentB.uid}` : `${studentB.uid}_${studentA.uid}`;

  await assertDenied(clientA.set(`connections/${pairId}`, {status: 'active'}), 'Client creates connection');
  await assertDenied(clientA.delete(`connections/${pairId}`), 'Client deletes connection');

  // Verify that stranger C cannot create a chat without an active connection
  const chatCA = `chat_ca_${nonce}`;
  await assertDenied(
    clientC.set(`chats/${chatCA}`, {chatId: chatCA, participantUids: [studentC.uid, studentA.uid]}),
    'Stranger C creates chat with Student A without active connection'
  );

  const setConnRes = await admin.set(`connections/${pairId}`, {
    pairId,
    uidA: studentA.uid < studentB.uid ? studentA.uid : studentB.uid,
    uidB: studentA.uid < studentB.uid ? studentB.uid : studentA.uid,
    status: 'active',
  });
  assert.equal(setConnRes.status, 200, 'Admin setup connection must succeed');

  await assertPermitted(clientB.get(`users/${studentA.uid}/private/contact`), 'Connected Student B reads Student A firstName');
  await assertPermitted(clientA.get(`users/${studentB.uid}/private/contact`), 'Connected Student A reads Student B firstName');
  await assertDenied(clientC.get(`users/${studentA.uid}/private/contact`), 'Third-party Student C reads Student A contact');

  await assertDenied(clientB.get(`users/${studentA.uid}/private/phone`), 'Connected Student B reads phone when opt-in is false');
  await clientA.set(`users/${studentA.uid}/private/profile`, {privacy: {showPhoneToConnections: true}}, ['privacy']);
  await assertPermitted(clientB.get(`users/${studentA.uid}/private/phone`), 'Connected Student B reads phone when opt-in is true');

  // Connected Student A creates chat with Student B (now allowed because connection is active)
  const chatId = `chat_${nonce}`;
  await assertPermitted(
    clientA.set(`chats/${chatId}`, {chatId, participantUids: [studentA.uid, studentB.uid]}),
    'Connected Student A creates chat with Student B'
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

  // Participant ends connection
  await assertPermitted(clientA.set(`connections/${pairId}`, {status: 'ended'}, ['status']), 'Participant ends connection');
  await assertDenied(clientC.set(`connections/${pairId}`, {status: 'ended'}, ['status']), 'Stranger ends connection');

  console.log('5. Testing Listing Requests & Location Privacy...');
  const listingId = `listing_${nonce}`;
  // Existing published Listing fixture; E2.1 clients create drafts only.
  await assertPermitted(
    admin.set(`listings/${listingId}`, {listingId, ownerUid: studentA.uid, totalRentSen: 120000,
      areaId: 'sample-area', buildingName: 'Fictional Building', buildingAddress: 'Jalan Fiksi 1', lat: 0, lng: 0, depositSen: 0,
      genderPref: 'any', amenities: [], photoUrls: [], isSample: true, status: 'active',
      createdAt: new Date(), expiresAt: new Date(Date.now() + 30 * 86400000)}),
    'Admin seeds published listing fixture'
  );

  // Host cannot change ownerUid on listing update
  await assertDenied(
    clientA.set(`listings/${listingId}`, {ownerUid: studentB.uid, totalRentSen: 130000}, ['ownerUid', 'totalRentSen']),
    'Host A attempts to change listing ownerUid'
  );
  await assertPermitted(
    clientA.set(`listings/${listingId}`, {ownerUid: studentA.uid, totalRentSen: 130000}, ['ownerUid', 'totalRentSen']),
    'Host A updates listing retaining same ownerUid'
  );

  await assertPermitted(
    clientA.set(`listings/${listingId}/private/location`, {exactAddress: 'A-12-03, Pantai Hillpark', residentUids: [studentA.uid]}),
    'Host A sets exact location'
  );

  await assertDenied(clientB.get(`listings/${listingId}/private/location`), 'Applicant reads exact address before request accepted');
  await assertPermitted(clientB.get(`listings/${listingId}`), 'Student reads the public Building address');

  const reqId = `${listingId}_${studentB.uid}`;

  // Requester cannot set hostUid to self to attempt self-acceptance
  await assertDenied(
    clientB.set(`listingRequests/${reqId}`, {requestId: reqId, listingId, hostUid: studentB.uid, requesterUid: studentB.uid, status: 'pending'}),
    'Requester B sets hostUid to self to attempt self-acceptance'
  );

  // Requester cannot set mismatched hostUid
  await assertDenied(
    clientB.set(`listingRequests/${reqId}`, {requestId: reqId, listingId, hostUid: studentC.uid, requesterUid: studentB.uid, status: 'pending'}),
    'Requester B sets hostUid to mismatched third party'
  );

  // Valid request: hostUid matches listing ownerUid and is not requester
  await assertPermitted(
    clientB.set(`listingRequests/${reqId}`, {requestId: reqId, listingId, hostUid: studentA.uid, requesterUid: studentB.uid, status: 'pending'}),
    'Student B sends viewing request to Host A'
  );
  await assertDenied(clientC.get(`listingRequests/${reqId}`), 'Third-party reads listing request');

  await assertPermitted(
    clientA.set(`listingRequests/${reqId}`, {status: 'accepted'}, ['status']),
    'Host A accepts listing request'
  );

  await assertPermitted(clientB.get(`listings/${listingId}/private/location`), 'Accepted applicant B reads exact address');
  await assertDenied(clientC.get(`listings/${listingId}/private/location`), 'Unaccepted student C reads exact address');

  console.log('5b. Testing E2.1 private drafts and field validation...');
  console.log('Testing E2.1 Building picker area access...');
  await assertPermitted(admin.set(`areas/sample_${nonce}`, {
    areaId: `sample_${nonce}`, name: 'Fictional sample area', lat: 0, lng: 0,
  }), 'Admin seeds fictional area reference');
  for (const [client, label] of [[anon, 'Anonymous'], [clientUnverified, 'Unverified'],
    [clientStaff, 'Staff'], [clientOutsider, 'Non-UM']]) {
    await assertDenied(client.get(`areas/sample_${nonce}`), `${label} reads area reference`);
  }
  await assertPermitted(clientA.get(`areas/sample_${nonce}`), 'Verified UM student reads area reference');
  await assertDenied(clientA.set(`areas/sample_${nonce}`, {lat: 1}, ['lat']), 'Student cannot edit seeded area');
  const areaQuery = await fetch(`${base}:runQuery`, {
    method: 'POST', headers: {'Content-Type': 'application/json', Authorization: `Bearer ${studentA.token}`},
    body: JSON.stringify({structuredQuery: {from: [{collectionId: 'areas'}], limit: 501}}),
    signal: AbortSignal.timeout(10000),
  });
  await assertPermitted(Promise.resolve(areaQuery), 'Picker bounded areas query');
  const {makeListingDraft} = require('../functions/src/listing-draft');
  const draftId = `draft_${nonce}`;
  const draft = makeListingDraft({areaId: 'sample-area', buildingName: 'Fictional Building', buildingAddress: 'Jalan Fiksi 1',
    coordinateSource: 'area_center_estimate',
    lat: 0, lng: 0, totalRentSen: 100000, depositSen: 0, genderPref: 'any',
    amenities: ['wifi'], exactAddress: 'Fictional Unit 1'}, studentA.uid, draftId);
  const prefix = `projects/${project}/databases/(default)/documents`;
  const draftWrites = () => [
    {update: {name: `${prefix}/listings/${draftId}`, fields: encodeFields(draft.listing)},
      updateTransforms: ['createdAt', 'updatedAt'].map(fieldPath => ({fieldPath, setToServerValue: 'REQUEST_TIME'}))},
    {update: {name: `${prefix}/listings/${draftId}/private/location`, fields: encodeFields(draft.location)}},
  ];
  await assertDenied(clientA.commit(draftWrites().slice(0, 1)), 'Draft missing atomic private location');
  for (const [client, label] of [[anon, 'Anonymous'], [clientB, 'Forged owner'],
    [clientUnverified, 'Unverified'], [clientStaff, 'Staff'], [clientOutsider, 'Non-UM']]) {
    await assertDenied(client.commit(draftWrites()), `${label} draft creation`);
  }
  draft.listing.coordinateSource = 'verified';
  await assertDenied(clientA.commit(draftWrites()), 'Reject invented coordinate provenance on create');
  draft.listing.coordinateSource = 'area_center_estimate';
  await assertPermitted(clientA.commit(draftWrites()), 'Host creates draft and private location atomically');
  await assertDenied(clientA.set(`listings/${draftId}`, {}, ['coordinateSource']), 'Cannot erase estimate source');
  await assertDenied(clientA.set(`listings/${draftId}`, {coordinateSource: 'verified'}, ['coordinateSource']),
    'Cannot relabel estimates as verified');
  await assertPermitted(clientA.get(`listings/${draftId}`), 'Host reads draft');
  await assertPermitted(clientA.get(`listings/${draftId}/private/location`), 'Host reads draft address');
  await assertDenied(clientB.get(`listings/${draftId}`), 'Stranger reads draft');
  await assertDenied(anon.queryListings('status', 'active'), 'Anonymous Listing query');
  await assertDenied(clientB.queryListings(), 'Unfiltered query could include private drafts');
  await assertDenied(clientB.queryListings('status', 'draft'), 'Stranger queries all drafts');
  await assertPermitted(clientA.queryListings('ownerUid', studentA.uid), 'Host queries own Listings');
  await assertPermitted(clientB.queryListings('status', 'active'), 'Student queries active Listings');
  await assertDenied(clientB.get(`listings/${draftId}/private/location`), 'Stranger reads draft address');
  await assertDenied(clientB.set(`listings/${draftId}`, {depositSen: 1}, ['depositSen']), 'Stranger edits draft');
  for (const change of [{totalRentSen: 1.5}, {depositSen: -1}, {buildingName: 'x'.repeat(201)},
    {buildingAddress: 'x'.repeat(301)}, {buildingAddress: ''}, {buildingAddress: 42},
    {ownerUid: studentB.uid}, {listingId: 'different'}, {status: 'active'}, {isSample: true},
    {createdAt: new Date(0)}, {exactAddress: 'must stay private'}, {amenities: [false]},
    {photoUrls: ['http://example.com/x']}, {photoUrls: Array(6).fill('https://res.cloudinary.com/fixture/image/upload/x')},
    {lat: 91}, {genderPref: 'invalid'}, {expiresAt: new Date()}]) {
    await assertDenied(clientA.set(`listings/${draftId}`, change, Object.keys(change)),
      `Invalid draft update ${Object.keys(change)[0]}`);
  }
  await assertDenied(clientA.set(`listings/${draftId}`, {}, ['buildingName']), 'Cannot remove required field');
  await assertPermitted(clientA.set(`listings/${draftId}`, {depositSen: 100}, ['depositSen']), 'Host edits valid draft');
  await assertPermitted(clientA.set(`listings/${draftId}`, {buildingAddress: 'Jalan Fiksi 2'}, ['buildingAddress']),
    'Host edits the public Building address');
  await assertPermitted(clientA.set(`listings/${draftId}`, {
    photoUrls: [`https://res.cloudinary.com/fixture-cloud/image/upload/v1/listings/${draftId}/photo.jpg`],
  }, ['photoUrls']), 'Host stores Cloudinary URL');
  await assertDenied(clientA.set(`listings/${draftId}/private/location`,
    {exactAddress: 'Fictional Unit 1', residentUids: [studentA.uid, studentB.uid]}), 'Draft cannot grant stranger residency');
  await assertDenied(clientB.set(`listingRequests/${draftId}_${studentB.uid}`, {
    listingId: draftId, hostUid: studentA.uid, requesterUid: studentB.uid, status: 'pending',
  }), 'Draft cannot receive viewing requests');
  await assertDenied(clientA.delete(`listings/${draftId}`), 'Deletion deferred to Listing lifecycle');

  console.log('5c. Testing verified UM Host through the real Worker handler and emulator Rules...');
  const {createListingWorker} = require('../worker/listings');
  const {webcrypto} = require('node:crypto');
  let providerCalls = 0;
  const worker = createListingWorker({cryptoApi: webcrypto, fetchImpl: async (url, options) => {
    if (String(url).startsWith('https://api.cloudinary.com/')) {
      providerCalls++;
      const id = options.body.get('public_id');
      return Response.json({public_id: id, resource_type: 'image', format: 'jpg',
        secure_url: `https://res.cloudinary.com/fixture-cloud/image/upload/v1/${id}.jpg`});
    }
    assert.ok(String(url).startsWith('http://127.0.0.1:8080/v1/projects/demo-jiran/'),
      'Host integration test must never call a live database');
    return fetch(url, options);
  }});
  const workerEnv = {MODE: 'local', FIREBASE_PROJECT_ID: 'demo-jiran', CLOUDINARY_CLOUD_NAME: 'fixture-cloud',
    CLOUDINARY_API_KEY: 'fake-key', CLOUDINARY_API_SECRET: 'fake-secret', CLOUDINARY_UPLOAD_PRESET: 'fixture'};
  const hostInput = {areaId: 'sample-area', buildingName: 'Fictional Building', lat: 0, lng: 0,
    coordinateSource: 'area_center_estimate', totalRentSen: 100000, depositSen: 0,
    genderPref: 'any', amenities: [], exactAddress: 'Fictional Unit 1', buildingAddress: 'Jalan Fiksi 1'};
  const workerRequest = (path, token, photo = false) => worker.fetch(new Request(`http://127.0.0.1${path}`, {
    method: 'POST', headers: {...(token ? {authorization: `Bearer ${token}`} : {}),
      'content-type': photo ? 'image/jpeg' : 'application/json'},
    body: photo ? new Uint8Array([255, 216, 255, 0]) : JSON.stringify(hostInput),
  }), workerEnv);
  assert.equal((await workerRequest('/listings', null)).status, 401);
  deniedCount++;
  const lookalike = await createAuthUser(authBase, `lookalike-${nonce}@siswa.um.edu.my.example.com`, 'pass123', true);
  for (const account of [unverified, staff, outsider, lookalike]) {
    await assertDenied(workerRequest('/listings', account.token), 'Worker denies ineligible Host creation');
  }
  const created = await workerRequest('/listings', studentA.token);
  assert.equal(created.status, 201);
  passedCount++;
  const savedId = (await created.json()).listingId;
  const saved = await clientA.get(`listings/${savedId}`);
  assert.equal(saved.status, 200);
  const savedFields = (await saved.json()).fields;
  assert.equal(savedFields.ownerUid.stringValue, studentA.uid);
  assert.equal(savedFields.status.stringValue, 'draft');
  assert.ok(!savedFields.exactAddress);
  await assertPermitted(clientA.get(`listings/${savedId}/private/location`), 'Worker-created address readable by Host');
  await assertDenied(clientB.get(`listings/${savedId}/private/location`), 'Worker-created address hidden from stranger');
  for (const account of [unverified, staff, outsider, lookalike, studentB]) {
    await assertDenied(workerRequest(`/listings/${savedId}/photos`, account.token, true),
      'Worker denies ineligible or non-owner photo upload');
  }
  assert.equal((await workerRequest(`/listings/${savedId}/photos`, null, true)).status, 401);
  deniedCount++;
  assert.equal(providerCalls, 0, 'Rejected Hosts never reach the photo provider');
  await assertPermitted(workerRequest(`/listings/${savedId}/photos`, studentA.token, true),
    'Verified Host uploads through a stub provider and real Rules');
  assert.equal(providerCalls, 1);

  console.log('6. Testing Household & Co-household Data Isolation (Module 5)...');
  const householdId = `hh_${nonce}`;
  const setHhRes = await admin.set(`households/${householdId}`, {
    householdId,
    memberUids: [studentA.uid, studentB.uid],
  });
  assert.equal(setHhRes.status, 200, 'Admin setup household must succeed');

  await assertPermitted(
    clientA.set(`expenses/exp_${nonce}`, {householdId, description: 'Wi-Fi', amountSen: 10000}),
    'Household member A adds shared expense'
  );
  await assertPermitted(clientB.get(`expenses/exp_${nonce}`), 'Household member B reads shared expense');
  await assertDenied(clientC.get(`expenses/exp_${nonce}`), 'Non-member C reads shared expense');
  await assertDenied(clientC.get(`households/${householdId}`), 'Non-member C reads household');

  const setMemARes = await admin.set(`users/${studentA.uid}/private/membership`, {householdId});
  assert.equal(setMemARes.status, 200, 'Admin setup membership A must succeed');
  const setMemBRes = await admin.set(`users/${studentB.uid}/private/membership`, {householdId});
  assert.equal(setMemBRes.status, 200, 'Admin setup membership B must succeed');

  await assertPermitted(clientB.get(`users/${studentA.uid}/private/timetable`), 'Co-household member B reads timetable');
  await assertPermitted(clientB.get(`users/${studentA.uid}/private/photo`), 'Co-household member B reads photo');
  await assertDenied(clientC.get(`users/${studentA.uid}/private/timetable`), 'Non-household member C reads timetable');

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
