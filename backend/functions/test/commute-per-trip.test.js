const {calculatePerTripCommute} = require('../src/commute');

const route = (mode, durationSeconds, perTripCostSen, extra = {}) =>
  ({mode, status: 'available', durationSeconds, distanceMetres: 5000, perTripCostSen, ...extra});

function input(overrides = {}) {
  return {modes: [route('walking', 3000, 0), route('transit', 1800, 115),
    route('e_hailing', 900, 1300), route('driving', 1000, 500)], ...overrides};
}

test('needs no rent, utilities, class time, campus days or monthly trips', () => {
  const result = calculatePerTripCommute(input());
  expect(result.status).toBe('ok');
  for (const field of ['monthlyTrips', 'trueMonthlyCostSen', 'classStartTime', 'roomPriceSen', 'utilitiesShareSen']) {
    expect(result).not.toHaveProperty(field);
  }
  for (const option of result.options) {
    expect(option).not.toHaveProperty('monthlyCostSen');
    expect(option).not.toHaveProperty('arrivesOnTime');
  }
});

test('selects the lowest known one-way fare, and walking counts when it is free', () => {
  expect(calculatePerTripCommute(input()).selectedMode).toBe('walking');
  const noWalk = input();
  noWalk.modes[0] = {mode: 'walking', status: 'unavailable', reason: 'missing_route'};
  expect(calculatePerTripCommute(noWalk).selectedMode).toBe('transit');
});

test('breaks fare ties by shorter duration, then by mode order', () => {
  const tied = {modes: [route('transit', 2000, 300), route('driving', 1000, 300)]};
  expect(calculatePerTripCommute(tied).selectedMode).toBe('driving');
  const same = {modes: [route('transit', 1000, 300), route('driving', 1000, 300)]};
  expect(calculatePerTripCommute(same).selectedMode).toBe('transit');
});

test('an unknown fare keeps its time, is never ranked as free, and limits the comparison', () => {
  const data = {modes: [route('transit', 1800, null), route('e_hailing', 900, 1300)]};
  const result = calculatePerTripCommute(data);
  expect(result.options[1]).toMatchObject({mode: 'transit', durationSeconds: 1800, perTripCostSen: null,
    fareStatus: 'unavailable'});
  expect(result.selectedMode).toBe('e_hailing');
  expect(result.comparisonLimited).toBe(true);
});

test('with no known fare nothing is selected', () => {
  const result = calculatePerTripCommute({modes: [route('transit', 1800, null)]});
  expect(result).toMatchObject({status: 'no_known_fare', selectedMode: null});
});

test('unavailable modes keep a known reason and a missing mode is missing_route', () => {
  const result = calculatePerTripCommute({modes: [{mode: 'driving', status: 'unavailable', reason: 'vehicle_unavailable'}]});
  expect(result.options.find(o => o.mode === 'driving')).toMatchObject({status: 'unavailable', reason: 'vehicle_unavailable'});
  expect(result.options.find(o => o.mode === 'walking')).toMatchObject({reason: 'missing_route'});
});

test('CO2 stays per trip and unknown is null, never zero', () => {
  const data = input();
  data.modes[1].emissionFactor = {gramsPerKm: 50, source: 'test-fixture-only'};
  const result = calculatePerTripCommute(data);
  expect(result.options[1]).toMatchObject({co2GramsPerTrip: 250, emissionSource: 'test-fixture-only'});
  expect(result.options[0].co2GramsPerTrip).toBeNull();
});

test.each([[{perTripCostSen: -1}], [{durationSeconds: 1.5}], [{status: 'late'}]])('rejects bad route data %j', patch => {
  const data = input();
  Object.assign(data.modes[1], patch);
  expect(() => calculatePerTripCommute(data)).toThrow();
});

test('rejects duplicate or unknown modes', () => {
  expect(() => calculatePerTripCommute({modes: [route('transit', 1, 1), route('transit', 1, 1)]})).toThrow();
  expect(() => calculatePerTripCommute({modes: [route('bike', 1, 1)]})).toThrow();
});
