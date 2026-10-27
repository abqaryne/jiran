const {commutePreview} = require('../src/commute-preview');
const verified = {auth: {uid: 'local-user', token: {email_verified: true}}};

test('fixture endpoint rejects absent and unverified auth', () => {
  expect(() => commutePreview({data: {scenarioId: 'on-time'}})).toThrow('Authentication required');
  expect(() => commutePreview({auth: {uid:'u', token:{}}, data: {scenarioId:'on-time'}}))
    .toThrow('Verified email required');
});

test.each([undefined, null, [], {}, {scenarioId:'unknown'}, {scenarioId:'__proto__'},
  {scenarioId:'on-time', roomPriceSen:0}, {scenarioId:'on-time', userId:'another-user'}])(
  'rejects invalid fixture selection or client overrides %j', data => {
    expect(() => commutePreview({...verified, data})).toThrow();
  });

test('returns deterministic labelled fixture calculations without private identity', () => {
  const result = commutePreview({...verified, data:{scenarioId:'on-time'}});
  expect(result).toMatchObject({scenarioId:'on-time', dataSource:'fixture', assumptionVersion:'fixture-v1',
    selectedMode:'transit', trueMonthlyCostSen:61200});
  expect(result).not.toHaveProperty('uid');
  expect(result).toEqual(commutePreview({...verified, data:{scenarioId:'on-time'}}));
  expect(result.options[0]).toMatchObject({routeSource:'Synthetic routing fixture',warnings:['Synthetic warning: use designated footpaths.']});
  expect(result.options[1].costSource).toBe('Synthetic fare assumption');
  expect(result.options[3].trafficFallback).toBe(true);
});

test('no on-time fixture returns null selected mode and monthly total', () => {
  expect(commutePreview({...verified, data:{scenarioId:'no-on-time-route'}}))
    .toMatchObject({status:'no_on_time_route', selectedMode:null, trueMonthlyCostSen:null});
});

test('missing data remains unavailable and explicitly labelled', () => {
  const result = commutePreview({...verified, data:{scenarioId:'missing-data'}});
  expect(result.options[1]).toMatchObject({status:'unavailable',reason:'missing_schedule'});
  expect(result.options[2]).toMatchObject({status:'unavailable',reason:'missing_fare_assumption'});
  expect(result.trueMonthlyCostSen).toBeNull();
});
