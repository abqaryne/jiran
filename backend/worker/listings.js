'use strict';
const {makeListingDraft, fields} = require('../functions/src/listing-draft');
const {uploadListingPhoto} = require('../functions/src/listing-photo');

class RequestError extends Error {
  constructor(status, code) { super(code); this.status = status; }
}
const fail = (status, code) => { throw new RequestError(status, code); };
const json = (value, status = 200) => Response.json(value,
  {status, headers: {'cache-control': 'no-store'}});

async function boundedBytes(stream, max, signal) {
  if (!stream) fail(400, 'empty-body');
  const reader = stream.getReader();
  const parts = [];
  let size = 0;
  let onAbort;
  const aborted = new Promise((_, reject) => {
    onAbort = () => {
      void reader.cancel().catch(() => {});
      reject(new RequestError(408, 'request-timeout'));
    };
    signal.addEventListener('abort', onAbort, {once: true});
  });
  try {
    while (true) {
      if (signal.aborted) fail(408, 'request-timeout');
      const {value, done} = await Promise.race([reader.read(), aborted]);
      if (done) break;
      size += value.byteLength;
      if (size > max) fail(413, 'body-too-large');
      parts.push(value);
    }
    const result = new Uint8Array(size);
    let offset = 0;
    for (const part of parts) { result.set(part, offset); offset += part.length; }
    return result;
  } finally {
    signal.removeEventListener('abort', onAbort);
    void reader.cancel().catch(() => {});
  }
}

/** REST requests use the caller's Firebase token, so Security Rules remain authoritative. */
function createListingWorker({fetchImpl = globalThis.fetch, cryptoApi = globalThis.crypto} = {}) {
  return {async fetch(request, env) {
    try {
      const url = new URL(request.url);
      const photoMatch = /^\/listings\/([A-Za-z0-9_-]{1,128})\/photos$/.exec(url.pathname);
      if (url.search || (url.pathname !== '/listings' && !photoMatch)) fail(404, 'not-found');
      if (request.method !== 'POST') fail(405, 'method-not-allowed');
      const local = env.MODE === 'local' && env.FIREBASE_PROJECT_ID === 'demo-jiran'
        && ['localhost', '127.0.0.1', '[::1]'].includes(url.hostname);
      if (!local && (env.MODE !== 'production' || env.FIREBASE_PROJECT_ID !== 'jiran-mad')) {
        fail(503, 'service-unavailable');
      }
      const authorization = request.headers.get('authorization') || '';
      if (!/^Bearer [A-Za-z0-9_.-]+$/.test(authorization) || authorization.length > 8192) {
        fail(401, 'authentication-required');
      }
      const root = `projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents`;
      const base = local ? `http://127.0.0.1:8080/v1/${root}`
        : `https://firestore.googleapis.com/v1/${root}`;
      async function firestore(path, body) {
        const response = await fetchImpl(base + path, {method: body ? 'POST' : 'GET',
          headers: {authorization, 'content-type': 'application/json'}, redirect: 'manual',
          ...(body ? {body: JSON.stringify(body)} : {}), signal: AbortSignal.timeout(10000)});
        if ([401, 403, 404].includes(response.status)) fail(403, 'listing-access-denied');
        if (!response.ok) fail(503, 'database-unavailable');
        const bytes = await boundedBytes(response.body, 65536, AbortSignal.timeout(10000));
        return JSON.parse(new TextDecoder().decode(bytes));
      }
      if (!photoMatch) {
        if (request.headers.get('content-type') !== 'application/json') fail(415, 'json-required');
        const bytes = await boundedBytes(request.body, 8192, AbortSignal.timeout(10000));
        let draft;
        const listingId = cryptoApi.randomUUID();
        try {
          // This is an UNTRUSTED identity hint. Firestore verifies the token and requires
          // ownerUid == request.auth.uid before the atomic commit can succeed.
          const tokenPart = authorization.slice(7).split('.')[1];
          const claims = JSON.parse(atob(tokenPart.replace(/-/g, '+').replace(/_/g, '/')));
          draft = makeListingDraft(JSON.parse(new TextDecoder().decode(bytes)), claims.sub, listingId);
        } catch { fail(400, 'invalid-listing'); }
        await firestore(':commit', {writes: [
          {update: {name: `${root}/listings/${listingId}`, fields: fields(draft.listing)},
            currentDocument: {exists: false}, updateTransforms: [
              {fieldPath: 'createdAt', setToServerValue: 'REQUEST_TIME'},
              {fieldPath: 'updatedAt', setToServerValue: 'REQUEST_TIME'}]},
          {update: {name: `${root}/listings/${listingId}/private/location`, fields: fields(draft.location)},
            currentDocument: {exists: false}},
        ]});
        return json({listingId, status: 'draft'}, 201);
      }

      const listingId = photoMatch[1];
      const listing = await firestore(`/listings/${listingId}`);
      // Draft reads are Host-only. Never allow this gate to accept a public Listing.
      if (listing.fields?.status?.stringValue !== 'draft') fail(409, 'draft-required');
      if ((listing.fields?.photoUrls?.arrayValue?.values || []).length >= 5) fail(409, 'photo-limit');
      const bytes = await boundedBytes(request.body, 2 * 1024 * 1024, AbortSignal.timeout(10000));
      const publicId = `listings/${listingId}/${cryptoApi.randomUUID()}`;
      const photoUrl = await uploadListingPhoto({bytes,
        contentType: request.headers.get('content-type'), publicId, fetchImpl, cryptoApi,
        config: {cloudName: env.CLOUDINARY_CLOUD_NAME, apiKey: env.CLOUDINARY_API_KEY,
          apiSecret: env.CLOUDINARY_API_SECRET, uploadPreset: env.CLOUDINARY_UPLOAD_PRESET,
          maxBytes: 2 * 1024 * 1024}});
      if (typeof listing.updateTime !== 'string') fail(503, 'database-unavailable');
      await firestore(':commit', {writes: [{
        update: {name: `${root}/listings/${listingId}`, fields: {}},
        updateMask: {fieldPaths: []}, currentDocument: {updateTime: listing.updateTime},
        updateTransforms: [{fieldPath: 'photoUrls', appendMissingElements: {
          values: [{stringValue: photoUrl}]}},
        {fieldPath: 'updatedAt', setToServerValue: 'REQUEST_TIME'}],
      }]});
      return json({photoUrl}, 201);
    } catch (error) {
      if (error instanceof RequestError) return json({error: error.message}, error.status);
      if (error.code === 'invalid-photo') return json({error: 'invalid-photo'}, 400);
      return json({error: 'service-unavailable'}, 503);
    }
  }};
}
module.exports = {createListingWorker};
