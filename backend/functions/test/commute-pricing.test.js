const {priceRoutes} = require('../src/commute-pricing');
const {calculateCommute} = require('../src/commute');
const route = (mode,extra={}) => ({mode,status:'available',distanceMetres:6000,durationSeconds:1800,...extra});
const assumptions = () => ({version:'sample-v1',
  e_hailing:{baseSen:300,rateSenPerKm:100,multiplierBasisPoints:12500,pickupSeconds:120,source:'Synthetic sample fare'},
  driving:{fuelMillilitresPerKm:70,fuelSenPerLitre:200,parkingSenPerTrip:100,tollsSenPerTrip:50,source:'Synthetic sample car costs'}});
const run = (routes,config=assumptions()) => priceRoutes({routes,assumptions:config});
test('prices sourced road formulas exactly and adds explicit pickup time',()=>{
  const result=run([route('e_hailing'),route('driving')]);
  expect(result.assumptionVersion).toBe('sample-v1');
  expect(result.modes[2]).toMatchObject({mode:'e_hailing',perTripCostSen:1125,durationSeconds:1920,costSource:'Synthetic sample fare'});
  expect(result.modes[3]).toMatchObject({mode:'driving',perTripCostSen:234,costSource:'Synthetic sample car costs'});
});
test('integrates real cost layer into monthly calculator with literal expected result',()=>{
  const priced=run([route('walking',{durationSeconds:4000}),route('transit',{transitFareSen:250}),route('e_hailing'),
    route('driving',{source:'Google Maps',warnings:['Sample provider warning'],trafficFallback:true})]);
  const result=calculateCommute({roomPriceSen:45000,utilitiesShareSen:6200,monthlyTrips:40,
    departTime:'2026-11-16T08:00:00+08:00',classStartTime:'2026-11-16T09:00:00+08:00',modes:priced.modes});
  expect(result.selectedMode).toBe('driving');
  expect(result.trueMonthlyCostSen).toBe(60560); // 45000+6200+234*40
  expect(result.options[3]).toMatchObject({costSource:'Synthetic sample car costs',routeSource:'Google Maps',
    warnings:['Sample provider warning'],trafficFallback:true});
});
test('missing road assumptions and missing transit fare never become free',()=>{
  const result=run([route('walking'),route('transit'),route('e_hailing'),route('driving')],{version:'unset'});
  expect(result.modes[0].perTripCostSen).toBe(0);
  for (const mode of result.modes.slice(1)) expect(mode).toMatchObject({status:'unavailable',reason:'missing_fare_assumption'});
});
test('known zero transit fare wins over fallback; missing fare can use sourced fallback',()=>{
  const config={version:'fallback',transit:{perTripCostSen:300,source:'Synthetic operator sample'}};
  expect(run([route('transit',{transitFareSen:0})],config).modes[1]).toMatchObject({perTripCostSen:0,costSource:'Google Maps transit fare'});
  expect(run([route('transit')],config).modes[1]).toMatchObject({perTripCostSen:300,costSource:'Synthetic operator sample'});
});
test('fractional fare rounds half-up after multiplier; fuel uses correct ml/metre units',()=>{
  const config=assumptions();
  config.e_hailing={baseSen:0,rateSenPerKm:1,multiplierBasisPoints:10000,pickupSeconds:0,source:'Sample'};
  config.driving={fuelMillilitresPerKm:1,fuelSenPerLitre:1000,parkingSenPerTrip:0,tollsSenPerTrip:0,source:'Sample'};
  expect(run([route('e_hailing',{distanceMetres:500}),route('driving',{distanceMetres:500})],config).modes.slice(2).map(r=>r.perTripCostSen)).toEqual([1,1]);
});
test('rounds only after a nonidentity multiplier instead of losing fractional sen early',()=>{
  const config=assumptions();
  config.e_hailing={baseSen:0,rateSenPerKm:1,multiplierBasisPoints:12500,pickupSeconds:0,source:'Sample'};
  expect(run([route('e_hailing',{distanceMetres:400})],config).modes[2].perTripCostSen).toBe(1);
});
test('unavailable routes retain reason; absent modes are missing_route',()=>{
  const result=run([{mode:'transit',status:'unavailable',reason:'missing_schedule'}]);
  expect(result.modes[1]).toEqual({mode:'transit',status:'unavailable',reason:'missing_schedule'});
  expect(result.modes[0]).toEqual({mode:'walking',status:'unavailable',reason:'missing_route'});
});
test('preserves explicitly sourced emissions without mutating inputs',()=>{
  const emissionFactor={gramsPerKm:50,source:'Synthetic factor'};
  const input={routes:[route('driving')],assumptions:assumptions()};
  input.assumptions.driving.emissionFactor=emissionFactor;
  const original=JSON.stringify(input);
  expect(priceRoutes(input).modes[3].emissionFactor).toEqual(emissionFactor);
  expect(JSON.stringify(input)).toBe(original);
  expect(run([route('transit',{transitFareSen:250})]).modes[1]).not.toHaveProperty('emissionFactor');
});
test('transit emission config does not force an invented fallback fare',()=>{
  const config={version:'factor-only',transit:{source:'Synthetic factor config',emissionFactor:{gramsPerKm:20,source:'Synthetic factor'}}};
  expect(run([route('transit',{transitFareSen:250})],config).modes[1])
    .toMatchObject({perTripCostSen:250,emissionFactor:config.transit.emissionFactor});
  expect(run([route('transit')],config).modes[1]).toMatchObject({status:'unavailable',reason:'missing_fare_assumption'});
});
test('retains provider warnings/fallback for downstream display, copying warning array',()=>{
  const warnings=['Use footpaths'];
  const priced=run([route('walking',{warnings,trafficFallback:false}),route('driving',{trafficFallback:true})]);
  expect(priced.modes[0]).toMatchObject({warnings,trafficFallback:false});
  expect(priced.modes[0].warnings).not.toBe(warnings);
  expect(priced.modes[3].trafficFallback).toBe(true);
});
test.each([{source:''},{source:42},{warnings:'text'},{warnings:[42]},{trafficFallback:'true'}])('rejects malformed provider display metadata %j',metadata=>{
  expect(()=>run([route('walking',metadata)])).toThrow();
});
test('driving fixed costs remain exact at safe limit and reject overflowing total',()=>{
  const config=assumptions();
  config.driving.parkingSenPerTrip=Number.MAX_SAFE_INTEGER;
  config.driving.tollsSenPerTrip=0;
  expect(run([route('driving',{distanceMetres:0})],config).modes[3].perTripCostSen).toBe(Number.MAX_SAFE_INTEGER);
  config.driving.tollsSenPerTrip=1;
  expect(()=>run([route('driving',{distanceMetres:0})],config)).toThrow();
});
test.each([
  null,{}, {version:''},{version:'v',e_hailing:{baseSen:0}},
  {version:'v',transit:{perTripCostSen:200,source:''}},
  {version:'v',driving:{...assumptions().driving,fuelSenPerLitre:-1}},
  {version:'v',driving:{...assumptions().driving,emissionFactor:{gramsPerKm:-1,source:'Sample'}}},
  {version:'v',e_hailing:{...assumptions().e_hailing,pickupSeconds:1.5}},
  {version:'v',e_hailing:{...assumptions().e_hailing,multiplierBasisPoints:0}},
])('rejects malformed present configuration before calculations %j',config=>expect(()=>run([],config)).toThrow());
test.each([
  [route('unknown')],[route('driving'),route('driving')],[route('driving',{distanceMetres:-1})],
  [route('transit',{transitFareSen:-1})],[{mode:'transit',status:'unavailable',reason:'guessed'}],
])('rejects malformed routes %j',routes=>expect(()=>run(routes)).toThrow());
test('rejects unsafe fare and pickup arithmetic instead of emitting imprecise values',()=>{
  const config=assumptions();
  config.e_hailing.baseSen=Number.MAX_SAFE_INTEGER;
  expect(()=>run([route('e_hailing')],config)).toThrow();
  expect(()=>run([route('e_hailing',{durationSeconds:Number.MAX_SAFE_INTEGER})])).toThrow();
});
