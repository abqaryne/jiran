const {createHash, webcrypto} = require('node:crypto');
const {signUpload, uploadListingPhoto} = require('../src/listing-photo');

const config = {cloudName: 'fixture-cloud', apiKey: 'fake-key', apiSecret: 'fake-secret',
  uploadPreset: 'fixture-preset', maxBytes: 100};
const publicId = 'listings/listing-1/photo-1';
const url = `https://res.cloudinary.com/fixture-cloud/image/upload/v1/${publicId}.jpg`;
const options = () => ({bytes: new Uint8Array([255, 216, 255, 0]), contentType: 'image/jpeg',
  publicId, config, cryptoApi: webcrypto, now: () => 1000000});

test('signature uses sorted parameters and SHA-256 with secret appended', async () => {
  expect(await signUpload({timestamp: '1000', overwrite: 'false'}, 'fake-secret', webcrypto))
    .toBe(createHash('sha256').update('overwrite=false&timestamp=1000fake-secret').digest('hex'));
});

test('uploads bytes to fixed image endpoint; returns only HTTPS photo URL', async () => {
  const fetchImpl = jest.fn(async (endpoint, request) => {
    expect(endpoint).toBe('https://api.cloudinary.com/v1_1/fixture-cloud/image/upload');
    expect(request.redirect).toBe('manual');
    expect(request.body.get('overwrite')).toBe('false');
    expect(request.body.get('file').name).toBe('photo.jpg');
    expect(request.body.has('api_secret')).toBe(false);
    return Response.json({public_id: publicId, resource_type: 'image', format: 'jpg', secure_url: url});
  });
  await expect(uploadListingPhoto({...options(), fetchImpl})).resolves.toBe(url);
});

test.each([
  {bytes: new Uint8Array(101)}, {bytes: new Uint8Array([1, 2, 3])},
  {bytes: 'https://example.com/photo.jpg'}, {contentType: 'application/pdf'},
  {publicId: '../another-host'},
])('rejects invalid photo before contacting provider: %j', async override => {
  const fetchImpl = jest.fn();
  await expect(uploadListingPhoto({...options(), ...override, fetchImpl})).rejects.toThrow('invalid-photo');
  expect(fetchImpl).not.toHaveBeenCalled();
});

test.each([302, 429, 500])('sanitizes provider status %s', async status => {
  await expect(uploadListingPhoto({...options(), fetchImpl: async () =>
    new Response('provider details fake-secret', {status})})).rejects.toThrow('photo-upload-unavailable');
});

test('rejects mismatched asset identity and external URLs', async () => {
  await expect(uploadListingPhoto({...options(), fetchImpl: async () => Response.json({
    public_id: 'other', resource_type: 'image', format: 'jpg', secure_url: 'https://evil.example/x',
  })})).rejects.toThrow('photo-upload-unavailable');
});
