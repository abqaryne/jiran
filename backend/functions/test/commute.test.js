const {calculatePerTripCommute} = require('../src/commute');

function input(overrides = {}) {
  return {
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
  expect(()=>calculatePerTripCommute(data)).toThrow();
});
test('copies provider warnings so caller mutation does not change rendered facts', () => {
  const data=input();
  data.modes[1].warnings=['Use footpaths'];
  data.modes[1].trafficFallback=false;
  const result=calculatePerTripCommute(data);
  data.modes[1].warnings.push('later mutation');
  expect(result.options[1].warnings).toEqual(['Use footpaths']);
  expect(result.options[1].trafficFallback).toBe(false);
});

test('returns four ordered modes, with a one-way fare, CO2 and no monthly or arrival fields', () => {
  const result = calculatePerTripCommute(input());
  expect(result.options.map(option => option.mode)).toEqual(['walking', 'transit', 'e_hailing', 'driving']);
  expect(result.options[1]).toMatchObject({perTripCostSen: 250, fareStatus: 'known', co2GramsPerTrip: 300,
    emissionSource: 'test-fixture-only'});
  expect(result.isEstimate).toBe(true);
});

test('returns four unavailable modes when route data is missing', () => {
  const result = calculatePerTripCommute(input({modes: []}));
  expect(result.options.every(option => option.status === 'unavailable' && option.reason === 'missing_route')).toBe(true);
  expect(result).toMatchObject({status: 'no_known_fare', selectedMode: null});
});

test('preserves unknown emissions without discarding an otherwise viable route', () => {
  const result = calculatePerTripCommute(input({modes: [input().modes[3]]}));
  expect(result.selectedMode).toBe('driving');
  expect(result.options[3]).toMatchObject({co2GramsPerTrip: null, emissionSource: null});
});

test('ties prefer shorter duration and then the stable mode order, not input order', () => {
  const routes = input().modes.map(route => ({...route, perTripCostSen: 100, durationSeconds: 1000})).reverse();
  expect(calculatePerTripCommute({modes: routes}).selectedMode).toBe('walking');
  routes.find(route => route.mode === 'transit').durationSeconds = 800;
  expect(calculatePerTripCommute({modes: routes}).selectedMode).toBe('transit');
});

test('does not mutate caller input', () => {
  const value = input();
  const original = JSON.stringify(value);
  calculatePerTripCommute(value);
  expect(JSON.stringify(value)).toBe(original);
});

test.each([
  {durationSeconds: -1}, {durationSeconds: 0.1}, {distanceMetres: -1},
  {perTripCostSen: -1}, {perTripCostSen: 1.5}, {emissionFactor: {gramsPerKm: -1, source: 'x'}},
  {emissionFactor: {gramsPerKm: 50, source: ''}}, {status: 'unknown'}, {mode: 'helicopter'},
])('rejects malformed route values %j', override => {
  expect(() => calculatePerTripCommute({modes: [{...input().modes[1], ...override}]})).toThrow();
});

test('rejects duplicate modes rather than picking an arbitrary record', () => {
  expect(() => calculatePerTripCommute({modes: [input().modes[1], input().modes[1]]})).toThrow();
});
