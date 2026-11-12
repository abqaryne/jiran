const {makeListingDraft, fields} = require('../src/listing-draft');

const input = () => ({areaId: 'sample-area', buildingName: 'Fictional Building', buildingAddress: 'Jalan Fiksi 1',
  lat: 0, lng: 0, totalRentSen: 100000, depositSen: 0, genderPref: 'any',
  amenities: ['wifi'], exactAddress: 'Fictional Unit 1'});

test('draft separates exact address and derives identity; money stays in sen', () => {
  const result = makeListingDraft(input(), 'host', 'listing-1');
  expect(result.listing).toMatchObject({ownerUid: 'host', status: 'draft',
    totalRentSen: 100000, depositSen: 0, photoUrls: []});
  expect(result.listing).not.toHaveProperty('exactAddress');
  expect(result.listing.buildingAddress).toBe('Jalan Fiksi 1');
  expect(result.location).not.toHaveProperty('buildingAddress');
  expect(result.listing).not.toHaveProperty('expiresAt');
  expect(result.location).toEqual({exactAddress: 'Fictional Unit 1', residentUids: ['host']});
  expect(fields(result.listing).totalRentSen).toEqual({integerValue: '100000'});
});

test.each([
  {totalRentSen: 1.5}, {depositSen: -1}, {totalRentSen: Number.MAX_SAFE_INTEGER + 1},
  {buildingName: ' '}, {buildingName: 'x'.repeat(201)}, {lat: NaN}, {lng: 181},
  {genderPref: 'invalid'}, {amenities: [false]}, {amenities: Array(11).fill('wifi')},
  {exactAddress: ''}, {buildingAddress: ''}, {buildingAddress: ' '}, {buildingAddress: 'x'.repeat(301)}, {buildingAddress: undefined}, {ownerUid: 'stranger'}, {status: 'active'}, {photoUrls: ['https://example.com']},
  {areaId: '../private'}, {placeId: null}, {coordinateSource: 'verified'}, {coordinateSource: null},
])('rejects malformed or caller-controlled protected data: %j', override => {
  expect(() => makeListingDraft({...input(), ...override}, 'host', 'listing-1')).toThrow('invalid-listing');
});

test('manual picker keeps explicit estimate provenance beside general coordinates', () => {
  const result = makeListingDraft({...input(), coordinateSource: 'area_center_estimate'}, 'host', 'listing-1');
  expect(result.listing.coordinateSource).toBe('area_center_estimate');
  expect(result.location).not.toHaveProperty('coordinateSource');
  expect(result.listing).not.toHaveProperty('exactAddress');
});
