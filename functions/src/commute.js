const MODES = Object.freeze(['walking', 'transit', 'e_hailing', 'driving']);
const {calculateTripEmissions} = require('./emissions');

function invalid(message) {
  const error = new TypeError(message);
  error.code = 'invalid-argument';
  throw error;
}

function integer(value, field) {
  if (!Number.isSafeInteger(value) || value < 0) {
    invalid(`${field} must be a nonnegative safe integer`);
  }
  return value;
}

function timestamp(value, field) {
  const parts = typeof value === 'string'
    && /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:\.\d{1,3})?(Z|[+-]\d{2}:\d{2})$/.exec(value);
  if (!parts) invalid(`${field} needs an ISO timestamp with an explicit offset`);
  const [, year, month, day, hour, minute, second] = parts.map((part, index) => index === 0 ? part : Number(part));
  const daysInMonth = new Date(Date.UTC(year, month, 0)).getUTCDate();
  const parsed = Date.parse(value);
  if (month < 1 || month > 12 || day < 1 || day > daysInMonth
      || hour > 23 || minute > 59 || second > 59 || !Number.isFinite(parsed)) {
    invalid(`${field} is not a valid calendar timestamp`);
  }
  return parsed;
}

function unavailable(mode, reason) {
  return {mode, status: 'unavailable', reason};
}

function displayMetadata(route) {
  const metadata = {};
  for (const field of ['costSource','routeSource']) {
    if (route[field] !== undefined) {
      if (typeof route[field] !== 'string' || !route[field].trim() || route[field].length > 500) invalid('Invalid route/cost source');
      metadata[field] = route[field].trim();
    }
  }
  if (route.warnings !== undefined) {
    if (!Array.isArray(route.warnings) || route.warnings.length > 20
        || route.warnings.some(warning => typeof warning !== 'string' || warning.length > 2000)) invalid('Invalid route warnings');
    metadata.warnings = [...route.warnings];
  }
  if (route.trafficFallback !== undefined) {
    if (typeof route.trafficFallback !== 'boolean') invalid('Invalid traffic fallback flag');
    metadata.trafficFallback = route.trafficFallback;
  }
  return metadata;
}

function option(route, mode, monthlyTrips, departure, classStart) {
  if (!route) return unavailable(mode, 'missing_route');
  if (route.status === 'unavailable') {
    const reasons = ['missing_route', 'missing_schedule', 'missing_fare_assumption', 'provider_unavailable'];
    if (!reasons.includes(route.reason)) invalid('Unavailable route needs a known reason');
    return unavailable(mode, route.reason);
  }
  if (route.status !== 'available') invalid('Unknown route status');
  const durationSeconds = integer(route.durationSeconds, 'durationSeconds');
  const distanceMetres = integer(route.distanceMetres, 'distanceMetres');
  if (route.perTripCostSen == null) return unavailable(mode, 'missing_fare_assumption');
  const perTripCostSen = integer(route.perTripCostSen, 'perTripCostSen');
  const monthlyCostSen = integer(perTripCostSen * monthlyTrips, 'monthlyCostSen');
  const arrival = departure + durationSeconds * 1000;
  if (!Number.isSafeInteger(arrival) || Math.abs(arrival) > 8640000000000000) {
    invalid('Arrival timestamp overflow');
  }
  let co2GramsPerTrip = null;
  let emissionSource = null;
  if (route.emissionProfile != null && route.emissionFactor != null) invalid('Choose one emission method');
  if (route.emissionProfile != null) {
    const estimate = calculateTripEmissions({...route.emissionProfile, distanceMetres});
    co2GramsPerTrip = estimate.co2GramsPerTrip;
    emissionSource = estimate.emissionSource;
  }
  if (route.emissionFactor != null) {
    const {gramsPerKm, source} = route.emissionFactor;
    if (!Number.isFinite(gramsPerKm) || gramsPerKm < 0
        || typeof source !== 'string' || !source.trim() || source.length > 500) {
      invalid('Emission factor needs a nonnegative number and a source');
    }
    co2GramsPerTrip = integer(Math.round(distanceMetres / 1000 * gramsPerKm), 'co2GramsPerTrip');
    emissionSource = source.trim();
  }
  return {mode, status: 'available', durationSeconds, distanceMetres, perTripCostSen, ...displayMetadata(route),
    monthlyCostSen, co2GramsPerTrip, emissionSource,
    arrivalTime: new Date(arrival).toISOString(), arrivesOnTime: arrival < classStart};
}

/** Pure FR4 calculation: consumes explicit inputs, never fetches providers or assumes fares. */
function calculateCommute(input) {
  if (!input || typeof input !== 'object') invalid('Commute input is required');
  const roomPriceSen = integer(input.roomPriceSen, 'roomPriceSen');
  const utilitiesShareSen = integer(input.utilitiesShareSen, 'utilitiesShareSen');
  const baseCost = integer(roomPriceSen + utilitiesShareSen, 'room and utilities total');
  const monthlyTrips = integer(input.monthlyTrips, 'monthlyTrips');
  const departure = timestamp(input.departTime, 'departTime');
  const classStart = timestamp(input.classStartTime, 'classStartTime');
  if (!Array.isArray(input.modes) || input.modes.length > MODES.length) invalid('Expected up to four modes');
  const byMode = new Map();
  for (const route of input.modes) {
    if (!route || !MODES.includes(route.mode) || byMode.has(route.mode)) invalid('Modes must be known and unique');
    byMode.set(route.mode, route);
  }
  const options = MODES.map(mode => option(byMode.get(mode), mode, monthlyTrips, departure, classStart));
  const eligible = options.filter(route => route.status === 'available' && route.arrivesOnTime);
  eligible.sort((a, b) => a.monthlyCostSen - b.monthlyCostSen
    || a.durationSeconds - b.durationSeconds || MODES.indexOf(a.mode) - MODES.indexOf(b.mode));
  const selected = eligible[0];
  const trueMonthlyCostSen = selected
    ? integer(baseCost + selected.monthlyCostSen, 'trueMonthlyCostSen') : null;
  return {status: selected ? 'ok' : 'no_on_time_route', options,
    selectedMode: selected?.mode ?? null, trueMonthlyCostSen,
    roomPriceSen, utilitiesShareSen, monthlyTrips,
    departTime: new Date(departure).toISOString(), classStartTime: new Date(classStart).toISOString(),
    isEstimate: true};
}
module.exports = {calculateCommute};
