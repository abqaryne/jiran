/* Worker-compatible provider adapter. No Node APIs or Firebase Admin access. */
'use strict';

class PhotoError extends Error {
  constructor(code) { super(code); this.code = code; }
}

/** Cloudinary signs sorted parameters followed by the secret (not HMAC). */
async function signUpload(parameters, secret, cryptoApi = globalThis.crypto) {
  const message = Object.keys(parameters).sort()
    .map(key => `${key}=${parameters[key]}`).join('&') + secret;
  const digest = await cryptoApi.subtle.digest('SHA-256', new TextEncoder().encode(message));
  return Array.from(new Uint8Array(digest), byte => byte.toString(16).padStart(2, '0')).join('');
}

/** Upload only local image bytes; remote URLs and client-selected parameters are forbidden. */
async function uploadListingPhoto({bytes, contentType, publicId, config,
  fetchImpl = globalThis.fetch, cryptoApi = globalThis.crypto, now = Date.now}) {
  if (!config || !/^[a-z0-9_-]+$/.test(config.cloudName || '')
      || typeof config.apiKey !== 'string' || !config.apiKey
      || typeof config.apiSecret !== 'string' || !config.apiSecret
      || !/^[A-Za-z0-9_-]{1,128}$/.test(config.uploadPreset || '')
      || !Number.isSafeInteger(config.maxBytes) || config.maxBytes <= 0) {
    throw new PhotoError('photo-upload-unavailable');
  }
  if (!(bytes instanceof Uint8Array) || !bytes.length || bytes.length > config.maxBytes
      || !/^listings\/[A-Za-z0-9_-]+\/[A-Za-z0-9_-]+$/.test(publicId)) {
    throw new PhotoError('invalid-photo');
  }
  const jpeg = contentType === 'image/jpeg' && bytes[0] === 0xff
    && bytes[1] === 0xd8 && bytes[2] === 0xff;
  const png = contentType === 'image/png'
    && [137, 80, 78, 71, 13, 10, 26, 10].every((byte, i) => bytes[i] === byte);
  if (!jpeg && !png) throw new PhotoError('invalid-photo');

  const parameters = {overwrite: 'false', public_id: publicId,
    timestamp: String(Math.floor(now() / 1000)), upload_preset: config.uploadPreset};
  const form = new FormData();
  for (const [key, value] of Object.entries(parameters)) form.set(key, value);
  form.set('signature', await signUpload(parameters, config.apiSecret, cryptoApi));
  form.set('api_key', config.apiKey);
  form.set('file', new Blob([bytes], {type: contentType}), jpeg ? 'photo.jpg' : 'photo.png');
  try {
    const response = await fetchImpl(
      `https://api.cloudinary.com/v1_1/${config.cloudName}/image/upload`,
      {method: 'POST', body: form, redirect: 'manual', signal: AbortSignal.timeout(15000)});
    if (!response.ok) throw new Error();
    const reader = response.body.getReader();
    const chunks = [];
    let size = 0;
    try {
      while (true) {
        const {value, done} = await reader.read();
        if (done) break;
        size += value.byteLength;
        if (size > 16384) throw new Error();
        chunks.push(value);
      }
    } finally { void reader.cancel().catch(() => {}); }
    const result = JSON.parse(await new Blob(chunks).text());
    const prefix = `https://res.cloudinary.com/${config.cloudName}/image/upload/`;
    if (result.public_id !== publicId || result.resource_type !== 'image'
        || !['jpg', 'jpeg', 'png'].includes(result.format)
        || typeof result.secure_url !== 'string' || !result.secure_url.startsWith(prefix)
        || result.secure_url.length > 2048) throw new Error();
    return result.secure_url;
  } catch {
    // Never forward provider errors, credentials, signatures or file contents.
    throw new PhotoError('photo-upload-unavailable');
  }
}

module.exports = {PhotoError, signUpload, uploadListingPhoto};
