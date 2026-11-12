const {commutePreview} = require('../src/commute-preview');
const verified = {auth: {uid: 'local-user', token: {email_verified: true}}};

test('fixture endpoint rejects absent and unverified auth', () => {
  expect(() => commutePreview({data: {scenarioId: 'per-trip'}})).toThrow('Authentication required');
  expect(() => commutePreview({auth: {uid:'u', token:{}}, data: {scenarioId:'per-trip'}}))
    .toThrow('Verified email required');
});

test.each([undefined, null, [], {}, {scenarioId:'unknown'}, {scenarioId:'__proto__'},
  {scenarioId:'per-trip', classStartTime:'x'}, {scenarioId:'per-trip', userId:'another-user'}])(
  'rejects invalid fixture selection or client overrides %j', data => {
    expect(() => commutePreview({...verified, data})).toThrow();
  });

test('returns deterministic labelled fixture calculations without private identity', () => {
  const result = commutePreview({...verified, data:{scenarioId:'per-trip'}});
  expect(result).toMatchObject({scenarioId:'per-trip', dataSource:'fixture', assumptionVersion:'fixture-v1',
    selectedMode:'walking', status:'ok', isEstimate:true});
  expect(result).not.toHaveProperty('trueMonthlyCostSen');
  expect(result.options[1]).toMatchObject({perTripCostSen:250, fareStatus:'known'});
  expect(result).not.toHaveProperty('uid');
  expect(result).toEqual(commutePreview({...verified, data:{scenarioId:'per-trip'}}));
  expect(result.options[0]).toMatchObject({routeSource:'Synthetic routing fixture',warnings:['Synthetic warning: use designated footpaths.']});
  expect(result.options[1].costSource).toBe('Synthetic fare assumption');
  expect(result.options[3].trafficFallback).toBe(true);
});

test('no known fare returns a null selected mode and keeps the known times', () => {
  const result = commutePreview({...verified, data:{scenarioId:'no-known-fare'}});
  expect(result).toMatchObject({status:'no_known_fare', selectedMode:null});
  expect(result.options[1]).toMatchObject({durationSeconds:1800, perTripCostSen:null, fareStatus:'unavailable'});
});

test('missing data remains unavailable and explicitly labelled', () => {
  const result = commutePreview({...verified, data:{scenarioId:'missing-data'}});
  expect(result.options[1]).toMatchObject({status:'unavailable',reason:'missing_schedule'});
  expect(result.options[2]).toMatchObject({status:'available',perTripCostSen:null,fareStatus:'unavailable'});
  expect(result.comparisonLimited).toBe(true);
});
