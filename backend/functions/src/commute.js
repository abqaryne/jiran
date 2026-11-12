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

function emissionsFor(route, distanceMetres) {
  if (route.emissionProfile != null && route.emissionFactor != null) invalid('Choose one emission method');
  if (route.emissionProfile != null) {
    const estimate = calculateTripEmissions({...route.emissionProfile, distanceMetres});
    return {co2GramsPerTrip: estimate.co2GramsPerTrip, emissionSource: estimate.emissionSource};
  }
  if (route.emissionFactor != null) {
    const {gramsPerKm, source} = route.emissionFactor;
    if (!Number.isFinite(gramsPerKm) || gramsPerKm < 0
        || typeof source !== 'string' || !source.trim() || source.length > 500) {
      invalid('Emission factor needs a nonnegative number and a source');
    }
    return {co2GramsPerTrip: integer(Math.round(distanceMetres / 1000 * gramsPerKm), 'co2GramsPerTrip'),
      emissionSource: source.trim()};
  }
  return {co2GramsPerTrip: null, emissionSource: null};
}

function tripOption(route, mode) {
  if (!route) return unavailable(mode, 'missing_route');
  if (route.status === 'unavailable') {
    const reasons = ['missing_route', 'missing_schedule', 'missing_fare_assumption', 'provider_unavailable', 'vehicle_unavailable'];
    if (!reasons.includes(route.reason)) invalid('Unavailable route needs a known reason');
    return unavailable(mode, route.reason);
  }
  if (route.status !== 'available') invalid('Unknown route status');
  const durationSeconds = integer(route.durationSeconds, 'durationSeconds');
  const distanceMetres = integer(route.distanceMetres, 'distanceMetres');
  // A missing fare keeps the known time; it is never turned into RM 0 (PRD FR4.2, 11 Nov 2026).
  const fareKnown = route.perTripCostSen != null;
  return {mode, status: 'available', durationSeconds, distanceMetres,
    perTripCostSen: fareKnown ? integer(route.perTripCostSen, 'perTripCostSen') : null,
    fareStatus: fareKnown ? 'known' : 'unavailable',
    ...displayMetadata(route), ...emissionsFor(route, distanceMetres)};
}

/**
 * Per-trip FR4 calculation (PRD Module 4, Ary, 11 Nov 2026): one-way time and fare per mode. It takes no rent,
 * utilities, class time, campus days or monthly trips, and adds nothing into a month. The selected mode is the
 * lowest known one-way fare among available modes (ties: shorter duration, then mode order); a mode with an
 * unknown fare is shown but never ranked as free. Needs human review (core logic).
 */
function calculatePerTripCommute(input) {
  if (!input || typeof input !== 'object') invalid('Commute input is required');
  if (!Array.isArray(input.modes) || input.modes.length > MODES.length) invalid('Expected up to four modes');
  const byMode = new Map();
  for (const route of input.modes) {
    if (!route || !MODES.includes(route.mode) || byMode.has(route.mode)) invalid('Modes must be known and unique');
    byMode.set(route.mode, route);
  }
  const options = MODES.map(mode => tripOption(byMode.get(mode), mode));
  const ranked = options.filter(route => route.status === 'available' && route.fareStatus === 'known')
    .sort((a, b) => a.perTripCostSen - b.perTripCostSen
      || a.durationSeconds - b.durationSeconds || MODES.indexOf(a.mode) - MODES.indexOf(b.mode));
  const comparedAgainstUnknown = options.some(route => route.status === 'available' && route.fareStatus === 'unavailable');
  return {status: ranked.length ? 'ok' : 'no_known_fare', options, selectedMode: ranked[0]?.mode ?? null,
    comparisonLimited: ranked.length > 0 && comparedAgainstUnknown, isEstimate: true};
}
module.exports = {calculatePerTripCommute};
