const {calculateCommute} = require('../src/commute');

function input(overrides = {}) {
  return {
    roomPriceSen: 45000, utilitiesShareSen: 6200, monthlyTrips: 40,
    departTime: '2026-11-16T08:00:00+08:00',
    classStartTime: '2026-11-16T09:00:00+08:00',
    modes: [
      {mode: 'walking', status: 'available', durationSeconds: 4000, distanceMetres: 5000, perTripCostSen: 0},
      {mode: 'transit', status: 'available', durationSeconds: 1800, distanceMetres: 6000, perTripCostSen: 250,
        emissionFactor: {gramsPerKm: 50, source: 'test-fixture-only'}},
      {mode: 'e_hailing', status: 'available', durationSeconds: 900, distanceMetres: 8000, perTripCostSen: 1200},
      {mode: 'driving', status: 'available', durationSeconds: 1000, distanceMetres: 7000, perTripCostSen: 500},
    ], ...overrides,
  };
}

test.each([{routeSource:''},{routeSource:42},{costSource:''},{costSource:42},{warnings:'text'},{warnings:[42]},
  {warnings:['x'.repeat(2001)]},{trafficFallback:'true'}])('rejects malformed route display metadata %j', metadata => {
  const data=input();
  Object.assign(data.modes[1],metadata);
  expect(()=>calculateCommute(data)).toThrow();
});
test('copies provider warnings so caller mutation does not change rendered facts', () => {
  const data=input();
  data.modes[1].warnings=['Use footpaths'];
  data.modes[1].trafficFallback=false;
  const result=calculateCommute(data);
  data.modes[1].warnings.push('later mutation');
  expect(result.options[1].warnings).toEqual(['Use footpaths']);
  expect(result.options[1].trafficFallback).toBe(false);
});

test('selects costed on-time transit instead of late free walking and excludes deposit', () => {
  const result = calculateCommute(input({depositSen: 90000}));
  expect(result.selectedMode).toBe('transit');
  expect(result.trueMonthlyCostSen).toBe(61200);
  expect(result.options[0].arrivesOnTime).toBe(false);
  expect(result.options[1]).toMatchObject({monthlyCostSen: 10000, co2GramsPerTrip: 300,
    emissionSource: 'test-fixture-only', arrivalTime: '2026-11-16T00:30:00.000Z'});
  expect(result).not.toHaveProperty('depositSen');
});

test('requires arrival strictly before class start, including exact boundary', () => {
  const options = input().modes.map(route => ({...route, durationSeconds: 3600}));
  const result = calculateCommute(input({modes: options}));
  expect(result.selectedMode).toBeNull();
  expect(result.trueMonthlyCostSen).toBeNull();
  expect(result.status).toBe('no_on_time_route');
});

test('compares absolute instants with different explicit time-zone offsets', () => {
  const result = calculateCommute(input({departTime: '2026-11-16T00:00:00Z'}));
  expect(result.selectedMode).toBe('transit');
});

test('returns four unavailable modes when route data is missing', () => {
  const result = calculateCommute(input({modes: []}));
  expect(result.options.map(option => option.mode)).toEqual(['walking', 'transit', 'e_hailing', 'driving']);
  expect(result.options.every(option => option.status === 'unavailable' && option.reason === 'missing_route')).toBe(true);
  expect(result.trueMonthlyCostSen).toBeNull();
});

test('does not turn missing fares into free travel', () => {
  const route = {...input().modes[1], perTripCostSen: null};
  const result = calculateCommute(input({modes: [route]}));
  expect(result.options[1]).toMatchObject({status: 'unavailable', reason: 'missing_fare_assumption'});
  expect(result.selectedMode).toBeNull();
});

test('preserves unknown emissions without discarding an otherwise viable route', () => {
  const result = calculateCommute(input({modes: [input().modes[3]]}));
  expect(result.selectedMode).toBe('driving');
  expect(result.options[3]).toMatchObject({co2GramsPerTrip: null, emissionSource: null});
});

test('zero monthly trips preserves route timing while projecting no commute spending', () => {
  const result = calculateCommute(input({monthlyTrips: 0}));
  expect(result.selectedMode).toBe('e_hailing');
  expect(result.trueMonthlyCostSen).toBe(51200);
});

test('ties prefer shorter duration and then the stable mode order, not input order', () => {
  const routes = input().modes.map(route => ({...route, perTripCostSen: 100, durationSeconds: 1000})).reverse();
  expect(calculateCommute(input({modes: routes})).selectedMode).toBe('walking');
  routes.find(route => route.mode === 'transit').durationSeconds = 800;
  expect(calculateCommute(input({modes: routes})).selectedMode).toBe('transit');
});

test('does not mutate caller input', () => {
  const value = input();
  const original = JSON.stringify(value);
  calculateCommute(value);
  expect(JSON.stringify(value)).toBe(original);
});

test.each([
  ['roomPriceSen', -1], ['roomPriceSen', 1.5], ['roomPriceSen', '45000'],
  ['utilitiesShareSen', NaN], ['monthlyTrips', Infinity], ['monthlyTrips', -1],
  ['monthlyTrips', 1.5], ['monthlyTrips', Number.MAX_SAFE_INTEGER + 1],
  ['departTime', '2026-11-16T08:00:00'], ['departTime', '2026-02-30T08:00:00+08:00'],
  ['departTime', '2026-11-16T25:00:00Z'], ['classStartTime', 'not-a-date'],
])('rejects malformed %s=%s', (field, value) => {
  expect(() => calculateCommute(input({[field]: value}))).toThrow();
});

test.each([
  {durationSeconds: -1}, {durationSeconds: 0.1}, {distanceMetres: -1},
  {perTripCostSen: -1}, {perTripCostSen: 1.5}, {emissionFactor: {gramsPerKm: -1, source: 'x'}},
  {emissionFactor: {gramsPerKm: 50, source: ''}}, {status: 'unknown'}, {mode: 'helicopter'},
])('rejects malformed route values %j', override => {
  expect(() => calculateCommute(input({modes: [{...input().modes[1], ...override}]}))).toThrow();
});

test('rejects duplicate modes rather than picking an arbitrary record', () => {
  expect(() => calculateCommute(input({modes: [input().modes[1], input().modes[1]]}))).toThrow();
});

test('rejects money multiplication and total overflow', () => {
  expect(() => calculateCommute(input({monthlyTrips: Number.MAX_SAFE_INTEGER}))).toThrow();
  expect(() => calculateCommute(input({roomPriceSen: Number.MAX_SAFE_INTEGER}))).toThrow();
});
