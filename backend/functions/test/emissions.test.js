const {calculateTripEmissions} = require('../src/emissions');
const {calculateCommute} = require('../src/commute');
const {priceRoutes} = require('../src/commute-pricing');
const catalogue = require('../config/emission-factors.json');
const leg=(factorId,distanceMetres,occupants)=>({factorId,distanceMetres,...(occupants===undefined?{}:{occupants})});
const run=(legs,distanceMetres=legs.reduce((s,l)=>s+l.distanceMetres,0),allowUkProxy=true)=>
  calculateTripEmissions({legs,distanceMetres,allowUkProxy});

test('catalogue preserves gas-specific source cells and exact integer mass units',()=>{
  expect(catalogue.gas).toBe('CO2');
  expect(catalogue.originalMetric).toBe('kg CO2e of CO2 per unit');
  expect(catalogue.flatSha256).toBe('a9a455ab396dae226d510c7be6233748416d490c41a5d20f3dc7a0c45feecd5e');
  for (const factor of Object.values(catalogue.factors).filter(f=>f.isProxy)) {
    expect(factor.sourceRecordId).toMatch(/_2$/);
    expect(factor.milligramsPerUnit).toBe(Math.round(Number(factor.sourceValueKg)*1000000));
    expect(factor.flatCell).toBe(`J${factor.flatRow}`);
    expect(factor.fullCell).toMatch(/^[A-Z]+[0-9]+$/);
    expect(['vehicle-km','passenger-km']).toContain(factor.basis);
  }
});

test.each([
  ['uk-average-petrol-car',1608],['uk-regular-taxi',2064],['uk-average-motorbike',1114],
])('vehicle factor %s uses explicit whole-vehicle attribution', (factor,expected)=>{
  expect(run([leg(factor,10000,1)])).toMatchObject({status:'available',co2GramsPerTrip:expected,isProxy:true,gas:'CO2'});
});
test('two vehicle occupants share emissions; passenger factors are not divided twice',()=>{
  expect(run([leg('uk-average-petrol-car',10000,2)]).co2GramsPerTrip).toBe(804);
  expect(run([leg('uk-average-local-bus',10000)]).co2GramsPerTrip).toBe(1008);
  expect(()=>run([leg('uk-average-local-bus',10000,2)])).toThrow();
});
test('mixed walking/bus/rail uses leg distances, sums before rounding and names proxy source',()=>{
  const result=run([leg('walking-operational-zero',1000),leg('uk-average-local-bus',2000),leg('uk-light-rail',3000)]);
  expect(result.co2GramsPerTrip).toBe(264); // 0 + 201.54 + 62.79 =264.33
  expect(result.emissionSource).toContain('UK DESNZ 2026');
  expect(result.emissionSource).toContain('CO2 only');
  expect(result.emissionSource).toContain('proxy');
  expect(result.factorVersion).toBe('uk-desnz-2026-co2-v1');
  expect(run([leg('uk-average-local-bus',5),leg('uk-average-motorbike',5,1)]).co2GramsPerTrip).toBe(1);
});
test('metro proxy and walking zero preserve scope without requiring a proxy for walking',()=>{
  expect(run([leg('uk-metro',10000)]).co2GramsPerTrip).toBe(152);
  expect(run([leg('walking-operational-zero',1000)],1000,false)).toMatchObject({co2GramsPerTrip:0,isProxy:false,gas:'CO2'});
});
test.each([
  [[leg('unknown-shuttle',6000)],6000,true,'unknown_factor'],
  [[leg('uk-light-rail',6000)],6000,false,'proxy_not_enabled'],
  [[leg('uk-regular-taxi',6000)],6000,true,'missing_occupancy'],
  [[leg('uk-light-rail',5000)],6000,true,'incomplete_distance_coverage'],
  [[],6000,true,'missing_legs'],
])('unknown/incomplete inputs remain null, never zero (%s)',(legs,distance,enabled,reason)=>{
  expect(run(legs,distance,enabled)).toMatchObject({status:'unavailable',co2GramsPerTrip:null,emissionSource:null,reason});
});
test('proxy is disabled unless explicitly enabled',()=>{
  expect(calculateTripEmissions({legs:[leg('uk-light-rail',6000)],distanceMetres:6000}).reason).toBe('proxy_not_enabled');
});
test.each([
  {distanceMetres:6000},{legs:null,distanceMetres:0},{legs:[leg('uk-light-rail',-1)],distanceMetres:0},
  {legs:[leg('uk-light-rail',1.5)],distanceMetres:2}, {legs:[leg(42,1)],distanceMetres:1},
  {legs:[leg('uk-average-petrol-car',1,0)],distanceMetres:1},
  {legs:[leg('uk-average-petrol-car',1,1.5)],distanceMetres:1},
  {legs:[leg('uk-average-petrol-car',1,101)],distanceMetres:1},
  {legs:[leg('walking-operational-zero',1)],distanceMetres:1,allowUkProxy:'true'},
  {legs:Array(51).fill(leg('walking-operational-zero',1)),distanceMetres:51},
  {legs:[leg('uk-light-rail',Number.MAX_SAFE_INTEGER),leg('uk-light-rail',1)],distanceMetres:Number.MAX_SAFE_INTEGER},
])('rejects malformed inputs instead of plausible estimates %j',input=>expect(()=>calculateTripEmissions(input)).toThrow());
test('input is not mutated and source stays within Android display bounds for all factors',()=>{
  const ids=['walking-operational-zero','uk-average-petrol-car','uk-regular-taxi','uk-average-motorbike','uk-average-local-bus','uk-light-rail','uk-metro'];
  const legs=ids.map((id,i)=>leg(id,1000,i>=1&&i<=3?1:undefined));
  const before=JSON.stringify(legs);
  expect(run(legs).emissionSource.length).toBeLessThanOrEqual(500);
  expect(JSON.stringify(legs)).toBe(before);
});
test('monthly calculation consumes mixed-leg CO2 without changing money or eligibility',()=>{
  const input={roomPriceSen:45000,utilitiesShareSen:6200,monthlyTrips:40,
    departTime:'2026-11-16T08:00:00+08:00',classStartTime:'2026-11-16T09:00:00+08:00',
    modes:[{mode:'transit',status:'available',durationSeconds:1800,distanceMetres:6000,perTripCostSen:250,
      emissionProfile:{allowUkProxy:true,legs:[leg('walking-operational-zero',1000),leg('uk-average-local-bus',2000),leg('uk-light-rail',3000)]}}]};
  const result=calculateCommute(input);
  expect(result.trueMonthlyCostSen).toBe(61200);
  expect(result.options[1]).toMatchObject({co2GramsPerTrip:264,arrivesOnTime:true});
  input.modes[0].emissionProfile.allowUkProxy=false;
  const unknown=calculateCommute(input);
  expect(unknown.options[1].co2GramsPerTrip).toBeNull();
  expect(unknown.trueMonthlyCostSen).toBe(61200);
  input.modes[0].emissionFactor={gramsPerKm:1,source:'Conflicting factor'};
  expect(()=>calculateCommute(input)).toThrow();
});
test('pricing preserves and copies supplied leg profile through the complete calculation path',()=>{
  const emissionProfile={allowUkProxy:true,legs:[leg('walking-operational-zero',1000),leg('uk-average-local-bus',2000),leg('uk-light-rail',3000)]};
  const routes=[{mode:'transit',status:'available',durationSeconds:1800,distanceMetres:6000,transitFareSen:250,emissionProfile}];
  const priced=priceRoutes({routes,assumptions:{version:'published-factor-integration'}});
  expect(priced.modes[1].emissionProfile).toEqual(emissionProfile);
  emissionProfile.legs[0].distanceMetres=0;
  const result=calculateCommute({roomPriceSen:45000,utilitiesShareSen:6200,monthlyTrips:40,
    departTime:'2026-11-16T08:00:00+08:00',classStartTime:'2026-11-16T09:00:00+08:00',modes:priced.modes});
  expect(result.options[1].co2GramsPerTrip).toBe(264);
  expect(result.options[1].emissionSource).toContain('UK DESNZ 2026');
  expect(result.trueMonthlyCostSen).toBe(61200);
});
test('pricing rejects malformed/conflicting profiles even if fare is missing',()=>{
  const route={mode:'transit',status:'available',durationSeconds:1800,distanceMetres:6000,emissionProfile:{legs:'bad'}};
  expect(()=>priceRoutes({routes:[route],assumptions:{version:'v'}})).toThrow();
  route.emissionProfile={legs:[leg('uk-light-rail',6000)],allowUkProxy:true};
  expect(()=>priceRoutes({routes:[route],assumptions:{version:'v',transit:{source:'Sample',emissionFactor:{gramsPerKm:1,source:'Conflict'}}}})).toThrow();
});
