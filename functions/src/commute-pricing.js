const MODES = ['walking','transit','e_hailing','driving'];
function invalid() { const error = new TypeError('Invalid commute pricing inputs'); error.code='invalid-argument'; throw error; }
function object(value) { return value !== null && typeof value === 'object' && !Array.isArray(value); }
function integer(value) { return Number.isSafeInteger(value) && value >= 0; }
function source(value) { return typeof value === 'string' && value.trim().length > 0 && value.length <= 500; }
function money(numerator, denominator=1n) {
  const rounded=(numerator*2n+denominator)/(denominator*2n);
  if (rounded > BigInt(Number.MAX_SAFE_INTEGER)) invalid();
  return Number(rounded);
}
function validateConfig(config) {
  if (!object(config) || typeof config.version !== 'string' || !/^[A-Za-z0-9_.-]{1,100}$/.test(config.version)
      || Object.keys(config).some(key=>key!=='version' && !MODES.includes(key))) invalid();
  const fields={walking:[],transit:['perTripCostSen'],e_hailing:['baseSen','rateSenPerKm','multiplierBasisPoints','pickupSeconds'],
    driving:['fuelMillilitresPerKm','fuelSenPerLitre','parkingSenPerTrip','tollsSenPerTrip']};
  for (const mode of MODES) {
    const values=config[mode];
    if (values === undefined) continue;
    if (!object(values) || !source(values.source)
        || fields[mode].some(field=>!(mode==='transit' && values[field]===undefined) && !integer(values[field]))
        || Object.keys(values).some(key=>!['source','emissionFactor',...fields[mode]].includes(key))) invalid();
    if (mode==='e_hailing' && values.multiplierBasisPoints===0) invalid();
    const factor=values.emissionFactor;
    if (factor !== undefined && (!object(factor) || !Number.isFinite(factor.gramsPerKm) || factor.gramsPerKm < 0 || !source(factor.source))) invalid();
  }
}
/** Pure pricing with caller-supplied sourced assumptions; no production defaults. */
function priceRoutes(input) {
  if (!object(input)) invalid();
  const {routes,assumptions} = input;
  validateConfig(assumptions);
  if (!Array.isArray(routes) || routes.length > 4) invalid();
  const byMode=new Map();
  for (const route of routes) {
    if (!object(route) || !MODES.includes(route.mode) || byMode.has(route.mode)) invalid();
    if (route.status==='unavailable') {
      if (!['missing_route','missing_schedule','missing_fare_assumption','provider_unavailable'].includes(route.reason)) invalid();
    } else if (route.status!=='available' || !integer(route.durationSeconds) || !integer(route.distanceMetres)
        || (route.transitFareSen != null && !integer(route.transitFareSen))) invalid();
    if (route.warnings !== undefined && (!Array.isArray(route.warnings) || route.warnings.length > 20
        || route.warnings.some(value=>typeof value!=='string' || value.length > 2000))) invalid();
    if (route.trafficFallback !== undefined && typeof route.trafficFallback!=='boolean') invalid();
    if (route.source !== undefined && !source(route.source)) invalid();
    byMode.set(route.mode,route);
  }
  const modes=MODES.map(mode=>{
    const route=byMode.get(mode);
    if (!route) return {mode,status:'unavailable',reason:'missing_route'};
    if (route.status==='unavailable') return {mode,status:'unavailable',reason:route.reason};
    const config=assumptions[mode];
    const result={mode,status:'available',durationSeconds:route.durationSeconds,distanceMetres:route.distanceMetres};
    if (mode==='walking') { result.perTripCostSen=0; result.costSource='Walking has no fare'; }
    else if (mode==='transit' && route.transitFareSen != null) {
      result.perTripCostSen=route.transitFareSen; result.costSource='Google Maps transit fare';
    } else if (!config || (mode==='transit' && config.perTripCostSen===undefined)) return {mode,status:'unavailable',reason:'missing_fare_assumption'};
    else if (mode==='transit') { result.perTripCostSen=config.perTripCostSen; result.costSource=config.source.trim(); }
    else if (mode==='e_hailing') {
      result.perTripCostSen=money((BigInt(config.baseSen)*1000n+BigInt(route.distanceMetres)*BigInt(config.rateSenPerKm))
        *BigInt(config.multiplierBasisPoints),10000000n);
      result.durationSeconds=route.durationSeconds+config.pickupSeconds;
      if (!integer(result.durationSeconds)) invalid();
      result.costSource=config.source.trim();
    } else {
      result.perTripCostSen=money(BigInt(route.distanceMetres)*BigInt(config.fuelMillilitresPerKm)*BigInt(config.fuelSenPerLitre)
        +(BigInt(config.parkingSenPerTrip)+BigInt(config.tollsSenPerTrip))*1000000n,1000000n);
      result.costSource=config.source.trim();
    }
    if (config?.emissionFactor !== undefined) result.emissionFactor={...config.emissionFactor};
    if (route.warnings !== undefined) result.warnings=[...route.warnings];
    if (route.trafficFallback !== undefined) result.trafficFallback=route.trafficFallback;
    if (route.source !== undefined) result.routeSource=route.source.trim();
    return result;
  });
  return {assumptionVersion:assumptions.version,modes};
}
module.exports = {priceRoutes};
