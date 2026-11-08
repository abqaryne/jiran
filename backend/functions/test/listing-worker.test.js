const {webcrypto} = require('node:crypto');
const {createListingWorker} = require('../../worker/listings');

const env = {MODE: 'production', FIREBASE_PROJECT_ID: 'jiran-mad', CLOUDINARY_CLOUD_NAME: 'fixture-cloud',
  CLOUDINARY_API_KEY: 'fake-key', CLOUDINARY_API_SECRET: 'fake-secret', CLOUDINARY_UPLOAD_PRESET: 'fixture'};
const token = `fake.${Buffer.from(JSON.stringify({sub: 'host'})).toString('base64url')}.fake`;
const headers = {authorization: `Bearer ${token}`, 'content-type': 'image/jpeg'};
const photoRequest = () => new Request('https://worker.example/listings/listing-1/photos', {
  method: 'POST', headers, body: new Uint8Array([255, 216, 255, 0])});
const draftResponse = () => Response.json({updateTime: '2026-11-09T00:00:00Z',
  fields: {status: {stringValue: 'draft'}, photoUrls: {arrayValue: {values: []}}}});

test('creates draft and private location in one commit; server timestamps; no address in response', async () => {
  const fetchImpl = jest.fn(async (url, options) => {
    expect(url).toContain('firestore.googleapis.com');
    expect(options.headers.authorization).toBe(`Bearer ${token}`);
    const {writes} = JSON.parse(options.body);
    expect(writes).toHaveLength(2);
    expect(writes[0].update.fields).not.toHaveProperty('exactAddress');
    expect(writes[1].update.fields.exactAddress.stringValue).toBe('Fictional Unit 1');
    expect(writes[0].updateTransforms).toContainEqual({fieldPath: 'createdAt', setToServerValue: 'REQUEST_TIME'});
    return Response.json({});
  });
  const request = new Request('https://worker.example/listings', {method: 'POST',
    headers: {...headers, 'content-type': 'application/json'}, body: JSON.stringify({
      areaId: 'sample-area', buildingName: 'Fictional Building', lat: 0, lng: 0,
      totalRentSen: 100000, depositSen: 0, genderPref: 'any', amenities: [], exactAddress: 'Fictional Unit 1',
    })});
  const response = await createListingWorker({fetchImpl, cryptoApi: webcrypto}).fetch(request, env);
  expect(response.status).toBe(201);
  expect(await response.json()).toEqual({listingId: expect.any(String), status: 'draft'});
});

test.each([401, 403, 404])('Firestore denial %s prevents all provider calls', async status => {
  const fetchImpl = jest.fn(async () => new Response('', {status}));
  const response = await createListingWorker({fetchImpl}).fetch(photoRequest(), env);
  expect(response.status).toBe(403);
  expect(fetchImpl).toHaveBeenCalledTimes(1);
});

test('public Listing cannot authorize an upload', async () => {
  const fetchImpl = jest.fn(async () => Response.json({fields: {status: {stringValue: 'active'}}}));
  expect((await createListingWorker({fetchImpl}).fetch(photoRequest(), env)).status).toBe(409);
  expect(fetchImpl).toHaveBeenCalledTimes(1);
});

test('upload follows authorized read and saves URL with concurrency precondition', async () => {
  const fetchImpl = jest.fn(async (url, options) => {
    if (url.includes('cloudinary.com')) {
      const id = options.body.get('public_id');
      return Response.json({public_id: id, resource_type: 'image', format: 'jpg',
        secure_url: `https://res.cloudinary.com/fixture-cloud/image/upload/v1/${id}.jpg`});
    }
    if (url.endsWith(':commit')) {
      const {writes} = JSON.parse(options.body);
      expect(writes[0].currentDocument.updateTime).toBe('2026-11-09T00:00:00Z');
      expect(writes[0].updateTransforms[0].fieldPath).toBe('photoUrls');
      return Response.json({});
    }
    return draftResponse();
  });
  const response = await createListingWorker({fetchImpl, cryptoApi: webcrypto}).fetch(photoRequest(), env);
  expect(response.status).toBe(201);
  expect(fetchImpl).toHaveBeenCalledTimes(3);
  expect(JSON.stringify(await response.json())).not.toContain('fake-secret');
});

test('oversized photo fails before upload', async () => {
  const fetchImpl = jest.fn(draftResponse);
  const request = new Request('https://worker.example/listings/listing-1/photos', {
    method: 'POST', headers, body: new Uint8Array(2 * 1024 * 1024 + 1)});
  expect((await createListingWorker({fetchImpl}).fetch(request, env)).status).toBe(413);
  expect(fetchImpl).toHaveBeenCalledTimes(1);
});

test('missing authentication fails without outbound calls', async () => {
  const fetchImpl = jest.fn();
  const response = await createListingWorker({fetchImpl}).fetch(new Request(
    'https://worker.example/listings', {method: 'POST'}), env);
  expect(response.status).toBe(401);
  expect(fetchImpl).not.toHaveBeenCalled();
});

test('database failure after upload is not reported as success', async () => {
  const fetchImpl = jest.fn(async (url, options) => {
    if (url.includes('cloudinary.com')) {
      const id = options.body.get('public_id');
      return Response.json({public_id: id, resource_type: 'image', format: 'jpg',
        secure_url: `https://res.cloudinary.com/fixture-cloud/image/upload/v1/${id}.jpg`});
    }
    return url.endsWith(':commit') ? new Response('', {status: 409}) : draftResponse();
  });
  const response = await createListingWorker({fetchImpl, cryptoApi: webcrypto}).fetch(photoRequest(), env);
  expect(response.status).toBe(503);
  expect(await response.json()).toEqual({error: 'database-unavailable'});
});

test('local configuration refuses non-loopback requests', async () => {
  const fetchImpl = jest.fn();
  const response = await createListingWorker({fetchImpl}).fetch(photoRequest(), {
    MODE: 'local', FIREBASE_PROJECT_ID: 'demo-jiran',
  });
  expect(response.status).toBe(503);
  expect(fetchImpl).not.toHaveBeenCalled();
});
