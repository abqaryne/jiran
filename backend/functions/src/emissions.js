const catalogue = require('../config/emission-factors.json');
const MAX_SAFE = BigInt(Number.MAX_SAFE_INTEGER);
function invalid() { const error=new TypeError('Invalid emission profile'); error.code='invalid-argument'; throw error; }
function object(value) { return value !== null && typeof value==='object' && !Array.isArray(value); }
function integer(value) { return Number.isSafeInteger(value) && value>=0; }
function gcd(a,b) { while (b) { const remainder=a%b; a=b; b=remainder; } return a; }
function unavailable(reason) {
  return {status:'unavailable',co2GramsPerTrip:null,emissionSource:null,reason,gas:'CO2',factorVersion:catalogue.version};
}

/** Exact per-traveller CO2 estimate for a fully accounted route; UK proxies require explicit opt-in. */
function calculateTripEmissions(input) {
  if (!object(input) || Object.keys(input).some(key=>!['distanceMetres','legs','allowUkProxy'].includes(key))
      || !integer(input.distanceMetres) || !Array.isArray(input.legs) || input.legs.length>50
      || (input.allowUkProxy!==undefined && typeof input.allowUkProxy!=='boolean')) invalid();
  let totalMetres=0n;
  const legs=input.legs.map(leg=>{
    if (!object(leg) || Object.keys(leg).some(key=>!['distanceMetres','factorId','occupants'].includes(key))
        || !integer(leg.distanceMetres)
        || (leg.factorId!=null && (typeof leg.factorId!=='string' || !/^[a-z0-9-]{1,80}$/.test(leg.factorId)))
        || (leg.occupants!=null && (!integer(leg.occupants) || leg.occupants<1 || leg.occupants>100))) invalid();
    const factor=Object.hasOwn(catalogue.factors,leg.factorId) ? catalogue.factors[leg.factorId] : undefined;
    if (factor?.basis==='passenger-km' && Object.hasOwn(leg,'occupants')) invalid();
    totalMetres+=BigInt(leg.distanceMetres);
    if (totalMetres>MAX_SAFE) invalid();
    return {...leg,factor};
  });
  if (!legs.length) return unavailable('missing_legs');
  if (legs.some(leg=>!leg.factor)) return unavailable('unknown_factor');
  if (legs.some(leg=>leg.factor.isProxy) && input.allowUkProxy!==true) return unavailable('proxy_not_enabled');
  if (legs.some(leg=>leg.factor.basis==='vehicle-km' && leg.occupants==null)) return unavailable('missing_occupancy');
  if (totalMetres!==BigInt(input.distanceMetres)) return unavailable('incomplete_distance_coverage');

  let numerator=0n, denominator=1n;
  for (const leg of legs) {
    const partNumerator=BigInt(leg.distanceMetres)*BigInt(leg.factor.milligramsPerUnit);
    const partDenominator=1000000n*BigInt(leg.factor.basis==='vehicle-km'?leg.occupants:1);
    numerator=numerator*partDenominator+partNumerator*denominator;
    denominator*=partDenominator;
    const divisor=gcd(numerator,denominator);
    numerator/=divisor;
    denominator/=divisor;
  }
  const grams=(2n*numerator+denominator)/(2n*denominator);
  if (grams>MAX_SAFE) invalid();
  const isProxy=legs.some(leg=>leg.factor.isProxy);
  const labels=[...new Set(legs.map(leg=>leg.factor.label))].sort().join(', ');
  let emissionSource=isProxy ? `UK DESNZ 2026 CO2 only operational proxy; excludes WTT/lifecycle. ${labels}`
    : 'Jiran walking methodology: zero powered operational CO2; excludes food/lifecycle emissions.';
  if (legs.some(leg=>leg.factorId==='uk-regular-taxi')) {
    emissionSource+='; taxi empty running/pickup excluded unless supplied as measured distance.';
  }
  return {status:'available',co2GramsPerTrip:Number(grams),emissionSource,gas:'CO2',isProxy,factorVersion:catalogue.version};
}
module.exports = {calculateTripEmissions};
