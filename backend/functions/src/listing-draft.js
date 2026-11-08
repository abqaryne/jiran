'use strict';

const INPUT_FIELDS = ['areaId', 'buildingName', 'placeId', 'lat', 'lng', 'totalRentSen',
  'depositSen', 'genderPref', 'amenities', 'exactAddress'];

function validText(value, max) {
  return typeof value === 'string' && value.trim().length > 0 && value.length <= max;
}

/** Prepare separate public/private documents; identity comes from authenticated context. */
function makeListingDraft(input, uid, listingId) {
  if (!input || Array.isArray(input) || typeof input !== 'object'
      || Object.keys(input).some(key => !INPUT_FIELDS.includes(key))
      || !validText(uid, 128) || !/^[A-Za-z0-9_-]{1,128}$/.test(listingId)
      || !validText(input.areaId, 128) || !/^[A-Za-z0-9_-]+$/.test(input.areaId)
      || !validText(input.buildingName, 200) || !validText(input.exactAddress, 500)
      || (input.placeId !== undefined && !validText(input.placeId, 256))
      || !Number.isFinite(input.lat) || Math.abs(input.lat) > 90
      || !Number.isFinite(input.lng) || Math.abs(input.lng) > 180
      || !Number.isSafeInteger(input.totalRentSen) || input.totalRentSen <= 0
      || !Number.isSafeInteger(input.depositSen) || input.depositSen < 0
      || !['male_only', 'female_only', 'any'].includes(input.genderPref)
      || !Array.isArray(input.amenities) || input.amenities.length > 10
      || input.amenities.some(value => !validText(value, 40))) {
    throw new Error('invalid-listing');
  }
  const listing = {listingId, ownerUid: uid, areaId: input.areaId,
    buildingName: input.buildingName.trim(), lat: input.lat, lng: input.lng,
    totalRentSen: input.totalRentSen, depositSen: input.depositSen,
    genderPref: input.genderPref, amenities: [...input.amenities], photoUrls: [],
    isSample: false, status: 'draft'};
  if (input.placeId !== undefined) listing.placeId = input.placeId;
  return {listing, location: {exactAddress: input.exactAddress.trim(), residentUids: [uid]}};
}

function encode(value) {
  if (typeof value === 'string') return {stringValue: value};
  if (typeof value === 'boolean') return {booleanValue: value};
  if (typeof value === 'number') return Number.isInteger(value)
    ? {integerValue: String(value)} : {doubleValue: value};
  if (Array.isArray(value)) return {arrayValue: {values: value.map(encode)}};
  throw new Error('invalid-field');
}
function fields(document) {
  return Object.fromEntries(Object.entries(document).map(([key, value]) => [key, encode(value)]));
}

module.exports = {makeListingDraft, fields};
