// E2.3 Subtask 3. Real Auth tokens and Firestore REST writes evaluated by emulator Rules.
// Called by the existing product suite, so CI/emulator-smoke includes every case below.
async function checkListingLifecycleRules(ctx) {
  const {prefix, admin, host, requester, stranger, blocked, hostUid, requesterUid,
    strangerUid, ineligibleOwners, encodeFields, assertDenied, assertPermitted, nonce} = ctx;
  const day = 86400000;
  const id = `lifecycle_${nonce}`;
  const path = `listings/${id}`;
  const requestId = `${id}_${requesterUid}`;
  const requestPath = `listingRequests/${requestId}`;
  const roomId = `published_room_${nonce}`;
  const roomPath = `rooms/${roomId}`;
  const expires = () => new Date(Date.now() + 30 * day);
  const listing = (status = 'active', expiresAt = expires()) => ({
    listingId: id, ownerUid: hostUid, areaId: 'fictional-area', buildingName: 'Fictional UM Building',
    buildingAddress: 'Fictional street', lat: 0, lng: 0, totalRentSen: 100000, depositSen: 0,
    genderPref: 'any', amenities: [], photoUrls: [], isSample: true, status,
    coordinateSource: 'area_center_estimate', createdAt: new Date(0), expiresAt,
  });
  const listingWithOwner = ownerUid => ({...listing(), ownerUid});
  const request = () => ({requestId, listingId: id, hostUid, requesterUid,
    message: 'Fictional UM student viewing request', status: 'pending'});
  const room = () => ({roomId, listingId: id, type: 'small', priceSen: 35000, status: 'vacant'});
  // Unlike the older convenience client, raw writes let tests omit/forge timestamps.
  const write = (target, data, {create = false, mask = Object.keys(data), timestamps = true} = {}) => ({
    update: {name: `${prefix}/${target}`, fields: encodeFields(data)},
    ...(create ? {currentDocument: {exists: false}} : {updateMask: {fieldPaths: mask}}),
    ...(timestamps ? {updateTransforms: (create ? ['createdAt', 'updatedAt'] : ['updatedAt'])
      .map(fieldPath => ({fieldPath, setToServerValue: 'REQUEST_TIME'}))} : {}),
  });
  const update = (client, target, data, options) => client.commit([write(target, data, options)]);
  const seedListing = (status, expiry) => assertPermitted(admin.set(path, listing(status, expiry)), 'Seed published fixture');
  const seedRequest = (status = 'pending', changes = {}) => assertPermitted(
    admin.set(requestPath, {...request(), status, ...changes}), `Seed ${status} request`);

  console.log('5b-3. E2.3 published Listing lifecycle authorization...');
  await seedListing();
  for (const [client, uid, label] of ineligibleOwners) {
    await assertPermitted(admin.set(path, listingWithOwner(uid)), 'Seed ineligible owner fixture');
    await assertDenied(update(client, path, {status: 'active', expiresAt: expires()}), `${label} owner cannot renew`);
    await assertDenied(update(client, path, {status: 'closed'}), `${label} owner cannot close`);
    await assertDenied(client.get(path), `${label} owner cannot read`);
  }
  await seedListing();
  const draftPath = `listings/lifecycle_draft_${nonce}`;
  const draft = {...listing(), listingId: `lifecycle_draft_${nonce}`, status: 'draft'};
  delete draft.expiresAt;
  await assertPermitted(admin.set(draftPath, draft), 'Seed valid draft');
  await assertDenied(update(host, draftPath, {status: 'active', expiresAt: expires()}), 'Renewal cannot bypass draft publication');
  const newId = `direct_active_${nonce}`;
  await assertDenied(host.commit([
    write(`listings/${newId}`, {...listing(), listingId: newId, isSample: false}, {create: true}),
    {update: {name: `${prefix}/listings/${newId}/private/location`,
      fields: encodeFields({exactAddress: 'Fictional Unit', residentUids: [hostUid]})}},
  ]), 'Host cannot directly create active Listing even with valid location');
  await assertPermitted(host.get(path), 'Host reads published Listing');
  await assertPermitted(requester.get(path), 'UM student reads public Listing');
  await assertPermitted(host.query('listings', [['ownerUid', hostUid]]), 'My listing owner query');
  for (const status of ['active', 'expired', 'closed']) {
    await seedListing(status, new Date(Date.now() - day));
    for (const [client, label] of [[requester, 'Requester'], [stranger, 'Stranger'], ...blocked]) {
      await assertDenied(update(client, path, {buildingName: 'Hijacked'}), `${label} edits ${status} Listing`);
      await assertDenied(update(client, path, {status: 'active', expiresAt: expires()}), `${label} renews ${status} Listing`);
      await assertDenied(update(client, path, {status: 'closed'}), `${label} closes ${status} Listing`);
      await assertDenied(client.delete(path), `${label} deletes ${status} Listing`);
    }
    await assertPermitted(update(host, path, {buildingName: 'Fictional updated Building'}), `Host edits ${status} Listing`);
    await assertPermitted(update(host, path, {status: 'active', expiresAt: expires()}), `Host renews ${status} Listing`);
    const saved = await (await host.get(path)).json();
    require('node:assert/strict').equal(saved.fields.status.stringValue, 'active');
    require('node:assert/strict').ok(Date.parse(saved.fields.expiresAt.timestampValue) > Date.now() + 29 * day);
    await assertPermitted(update(host, path, {status: 'closed'}), 'Host closes without changing expiry');
    await assertDenied(update(host, path, {status: 'active'}), 'Closed Listing cannot reactivate with stale expiry');
  }
  await seedListing();
  for (const change of [{ownerUid: strangerUid}, {listingId: 'another'}, {createdAt: new Date()},
    {isSample: false}, {coordinateSource: 'verified'}, {status: 'draft'}, {status: 'invented'},
    {totalRentSen: -1}, {depositSen: 1.5}, {buildingName: 'x'.repeat(201)},
    {amenities: [false]}, {exactAddress: 'Private data must not be public'},
    {expiresAt: 'invalid'}, {expiresAt: new Date(Date.now() + 31 * day)},
    {expiresAt: new Date(Date.now() + 29 * day)}, {expiresAt: new Date(0)},
    {status: 'closed', expiresAt: expires()}, {status: 'active', expiresAt: expires(), depositSen: 1}]) {
    await assertDenied(update(host, path, change), `Invalid published change ${JSON.stringify(change)}`);
  }
  await assertDenied(update(host, path, {}, {mask: ['expiresAt']}), 'Cannot remove published expiry');
  await assertDenied(update(host, path, {}, {mask: ['buildingName']}), 'Cannot remove published required field');
  await assertDenied(update(host, path, {updatedAt: new Date(0)}, {timestamps: false}), 'Forged Listing timestamp');
  await assertDenied(update(host, path, {status: 'expired'}), 'Cannot expire before deadline');
  await seedListing('active', new Date(Date.now() - 1));
  await assertPermitted(update(host, path, {status: 'expired'}), 'Host can persist check-on-read expiry');
  await assertDenied(update(host, path, {status: 'active'}), 'Expired Listing needs fresh renewal');
  await assertPermitted(update(host, path, {status: 'active', expiresAt: new Date(Date.now() + 30 * day - 240000)}),
    'Renewal allows four-minute slow clock/network skew');
  await assertPermitted(update(host, path, {status: 'active', expiresAt: new Date(Date.now() + 30 * day + 240000)}),
    'Renewal allows four-minute fast clock skew');
  for (const skew of [-600000, 600000]) {
    await assertDenied(update(host, path, {status: 'active', expiresAt: new Date(Date.now() + 30 * day + skew)}),
      'Renewal denies ten-minute clock skew');
  }
  await assertDenied(host.delete(path), 'Host cannot delete Listing');

  console.log('5b-4. E2.3 published Rooms and last-room close write shapes...');
  for (const [client, label] of [[requester, 'Requester'], [stranger, 'Stranger'], ...blocked]) {
    await assertDenied(update(client, roomPath, room(), {create: true}), `${label} creates Room in another Listing`);
  }
  await assertPermitted(update(host, roomPath, room(), {create: true}), 'Host creates Room in published Listing');
  await assertPermitted(requester.get(roomPath), 'Student reads public Room');
  await assertPermitted(host.query('rooms', [['listingId', id]]), 'My listing Room query');
  for (const [client, label] of [[requester, 'Requester'], [stranger, 'Stranger'], ...blocked]) {
    for (const change of [{priceSen: 1}, {status: 'occupied', occupiedByUid: strangerUid},
      {listingId: 'another'}, {roomId: 'another'}, {type: 'master'}]) {
      await assertDenied(update(client, roomPath, change), `${label} edits published Room ${Object.keys(change)[0]}`);
    }
    await assertDenied(client.delete(roomPath), `${label} deletes published Room`);
  }
  for (const change of [{priceSen: -1}, {priceSen: 1.5}, {priceSen: 100001}, {type: 'invalid'},
    {listingId: 'another'}, {roomId: 'another'}, {createdAt: new Date(0)}, {utilitiesSen: 2000},
    {status: 'invalid'}, {occupiedByUid: requesterUid}]) {
    await assertDenied(update(host, roomPath, change), `Invalid Host Room update ${Object.keys(change)[0]}`);
  }
  await assertDenied(update(host, roomPath, {}, {mask: ['priceSen']}), 'Cannot delete Room price');
  await assertDenied(update(host, roomPath, {priceSen: 1, updatedAt: new Date(0)}, {timestamps: false}),
    'Room update needs server timestamp');
  await assertPermitted(update(host, roomPath, {priceSen: 36000}), 'Host edits public Room price');
  const closeWrites = () => [write(roomPath, {status: 'occupied', occupiedByUid: requesterUid}),
    write(path, {status: 'closed'})];
  await assertDenied(stranger.commit(closeWrites()), 'Stranger cannot batch occupancy and closure');
  await assertPermitted(host.commit(closeWrites()), 'Host can atomically mark last Room occupied and close');
  await assertPermitted(update(host, path, {status: 'closed'}), 'Subtask 1 repeated close write allowed');

  console.log('5b-5. E2.3 request creation, transitions and privacy...');
  await seedListing();
  await assertPermitted(admin.set(`${path}/private/location`, {exactAddress: 'Fictional Unit 1', residentUids: [hostUid]}),
    'Seed private location');
  for (const [client, label] of [[host, 'Host'], [stranger, 'Stranger'], ...blocked]) {
    await assertDenied(update(client, requestPath, request(), {create: true}), `${label} cannot impersonate requester`);
  }
  for (const change of [{hostUid: requesterUid}, {hostUid: strangerUid}, {requesterUid: strangerUid},
    {requestId: 'wrong'}, {listingId: 'missing'}, {listingId: '../escape'}, {message: ''}, {message: false},
    {message: 'x'.repeat(1001)}, {status: 'accepted'}, {status: 'rejected'}, {status: 'cancelled'}, {admin: true}]) {
    await assertDenied(update(requester, requestPath, {...request(), ...change}, {create: true}),
      `Invalid request create ${Object.keys(change)[0]}`);
  }
  for (const key of Object.keys(request())) {
    const fields = request();
    delete fields[key];
    await assertDenied(update(requester, requestPath, fields, {create: true}), `Request create missing ${key}`);
  }
  await assertDenied(update(requester, requestPath, {...request(), createdAt: new Date(0), updatedAt: new Date(0)},
    {create: true, timestamps: false}), 'Request create forged timestamps');
  await assertDenied(update(requester, requestPath, request(), {create: true, timestamps: false}), 'Request missing timestamps');
  await assertDenied(update(requester, `${requestPath}_wrong`, request(), {create: true}), 'Wrong deterministic path');
  for (const [status, expiry] of [['closed', expires()], ['expired', expires()], ['active', new Date(Date.now() - 1)]]) {
    await seedListing(status, expiry);
    await assertDenied(update(requester, requestPath, request(), {create: true}), `No requests to ${status}/${expiry}`);
  }
  await assertPermitted(admin.delete(path), 'Remove fixture parent');
  await assertDenied(update(requester, requestPath, request(), {create: true}), 'No orphan request create');
  await seedListing();
  await assertPermitted(update(requester, requestPath, request(), {create: true}), 'Requester creates valid pending request');
  await assertPermitted(host.get(requestPath), 'Host reads incoming request');
  await assertPermitted(requester.get(requestPath), 'Requester reads own request');
  await assertPermitted(host.query('listingRequests', [['hostUid', hostUid], ['listingId', id]]), 'Exact Android inbox query');
  await assertDenied(stranger.query('listingRequests', [['listingId', id]]), 'Stranger cannot query listing inbox');
  for (const [client, label] of [[stranger, 'Stranger'], ...blocked]) {
    await assertDenied(client.get(requestPath), `${label} reads private request`);
    for (const status of ['accepted', 'rejected', 'cancelled']) {
      await assertDenied(update(client, requestPath, {status}), `${label} sets request ${status}`);
    }
    await assertDenied(client.delete(requestPath), `${label} deletes request`);
  }
  for (const status of ['accepted', 'rejected']) {
    await assertDenied(update(requester, requestPath, {status}), `Requester cannot self-${status}`);
  }
  await assertDenied(update(host, requestPath, {status: 'cancelled'}), 'Host cannot cancel as requester');
  await assertDenied(update(host, requestPath, {status: 'pending'}), 'Host cannot replay pending');
  for (const change of [{hostUid: strangerUid}, {requesterUid: strangerUid}, {listingId: 'other'},
    {requestId: 'other'}, {message: 'Changed message'}, {message: 'x'.repeat(1001)},
    {createdAt: new Date(0)}, {extra: true}]) {
    await assertDenied(update(host, requestPath, {status: 'accepted', ...change}),
      `Host decision cannot mutate ${Object.keys(change)[0]}`);
    await assertDenied(update(requester, requestPath, {status: 'cancelled', ...change}),
      `Requester cancellation cannot mutate ${Object.keys(change)[0]}`);
  }
  await assertDenied(update(host, requestPath, {status: 'accepted'}, {mask: ['status', 'message']}), 'Cannot remove request message');
  await assertDenied(update(host, requestPath, {status: 'accepted', updatedAt: new Date(0)}, {timestamps: false}),
    'Decision needs server updatedAt');
  await assertDenied(requester.get(`${path}/private/location`), 'Pending request cannot read exact address');
  await assertPermitted(update(host, requestPath, {status: 'accepted'}), 'Host accepts pending request');
  await assertPermitted(requester.get(`${path}/private/location`), 'Accepted request unlocks this Listing address');
  await assertDenied(stranger.get(`${path}/private/location`), 'Acceptance does not unlock address for strangers');
  await seedRequest();
  await assertPermitted(update(host, requestPath, {status: 'rejected'}), 'Host rejects pending request');
  await assertDenied(requester.get(`${path}/private/location`), 'Rejected request cannot read address');
  await seedRequest();
  await assertPermitted(update(requester, requestPath, {status: 'cancelled'}), 'Requester cancels pending request');
  for (const terminal of ['accepted', 'rejected', 'cancelled']) {
    await seedRequest(terminal);
    for (const next of ['pending', 'accepted', 'rejected', 'cancelled']) {
      for (const [client, label] of [[host, 'Host'], [requester, 'Requester']]) {
        await assertDenied(update(client, requestPath, {status: next}), `${label} cannot change ${terminal} to ${next}`);
      }
    }
  }
  await seedRequest('pending', {hostUid: strangerUid});
  await assertDenied(update(stranger, requestPath, {status: 'accepted'}), 'Forged stored host is not current Listing owner');
  await assertDenied(update(host, requestPath, {status: 'accepted'}), 'Inconsistent stored host fails closed');
  await seedRequest();
  await assertPermitted(admin.set(path, {ownerUid: strangerUid}, ['ownerUid']), 'Simulate server Host transfer');
  await assertDenied(update(host, requestPath, {status: 'accepted'}), 'Old Host cannot accept after transfer');
  await assertDenied(update(stranger, requestPath, {status: 'accepted'}), 'New Host cannot decide stale old-Host request');
  await assertPermitted(admin.delete(path), 'Remove parent for orphan decision test');
  await assertDenied(update(host, requestPath, {status: 'accepted'}), 'No orphan request decision');
  await seedListing();
  await assertDenied(host.delete(requestPath), 'Host cannot delete request');
  await assertDenied(requester.delete(requestPath), 'Requester cancels instead of deleting request');
  for (const client of [host, requester]) {
    await assertDenied(update(client, `connections/${hostUid}_${requesterUid}`, {status: 'active'}, {create: true}),
      'Request acceptance does not permit client Connection creation');
  }
}
module.exports = {checkListingLifecycleRules};
