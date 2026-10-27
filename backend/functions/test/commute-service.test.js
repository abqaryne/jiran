const {createCommuteService} = require('../src/commute-service');
const {createRoutesClient} = require('../src/routes-client');
const now=Date.parse('2026-11-16T00:00:00Z');
const request=(data={listingId:'listing',roomId:'room',departTime:'2026-11-16T08:05:00+08:00'})=>
  ({auth:{uid:'user',token:{email_verified:true}},data});
const context=()=>({viewerUid:'user',listingId:'listing',roomId:'room',roomListingId:'listing',
  origin:{latitude:3.1,longitude:101.65},destination:{latitude:3.12,longitude:101.66},
  roomPriceSen:45000,utilitiesShareSen:6200,monthlyTrips:40,classStartTime:'2026-11-16T09:00:00+08:00',drivingAllowed:true,
  privateEmail:'host-private@example.com',unitAddress:'private house',timetable:['private class']});
const config=()=>({version:'sample-v1',
  driving:{fuelMillilitresPerKm:70,fuelSenPerLitre:200,parkingSenPerTrip:100,tollsSenPerTrip:50,source:'Synthetic car'},
  e_hailing:{baseSen:300,rateSenPerKm:100,multiplierBasisPoints:10000,pickupSeconds:0,source:'Synthetic fare'}});
const fact=input=>({status:'available',durationSeconds:input.travelMode==='WALK'?4000:1800,distanceMetres:6000,
  transitFareSen:input.travelMode==='TRANSIT'?250:null,source:'Synthetic routing',warnings:['Sample route warning'],trafficFallback:false});
function setup(overrides={}) {
  const adapters={loadAuthorizedContext:jest.fn(async()=>context()),loadAssumptions:jest.fn(async()=>config()),
    consumeQuota:jest.fn(async()=>true),routesClient:{computeRoute:jest.fn(async input=>fact(input))},now:()=>now,...overrides};
  return {...adapters,service:createCommuteService(adapters)};
}
test('connects authorized inputs, distinct facts, exact pricing and monthly response without private projection',async()=>{
  const s=setup();
  const result=await s.service(request());
  expect(result).toMatchObject({selectedMode:'driving',trueMonthlyCostSen:60560,assumptionVersion:'sample-v1',partialProviderFailure:false});
  expect(s.loadAuthorizedContext).toHaveBeenCalledWith(expect.objectContaining({uid:'user',listingId:'listing',roomId:'room',signal:expect.any(AbortSignal)}));
  expect(s.consumeQuota).toHaveBeenCalledWith(expect.objectContaining({uid:'user',maxProviderCalls:3}));
  expect(s.routesClient.computeRoute).toHaveBeenCalledTimes(3);
  expect(JSON.stringify(result)).not.toMatch(/private house|host-private|private class|latitude|longitude|viewerUid/);
  expect(result.options[3]).toMatchObject({routeSource:'Synthetic routing',costSource:'Synthetic car',warnings:['Sample route warning']});
});
test('positive pickup gets its own later traffic departure, not a falsely shared road lookup',async()=>{
  const assumptions=config(); assumptions.e_hailing.pickupSeconds=120;
  const s=setup({loadAssumptions:jest.fn(async()=>assumptions),trafficAware:true});
  const result=await s.service(request());
  expect(s.consumeQuota).toHaveBeenCalledWith(expect.objectContaining({maxProviderCalls:4}));
  const road=s.routesClient.computeRoute.mock.calls.filter(([i])=>i.travelMode==='DRIVE').map(([i])=>i);
  expect(road.map(i=>i.departTime)).toEqual(['2026-11-16T00:05:00.000Z','2026-11-16T00:07:00.000Z']);
  expect(road.every(i=>i.trafficAware===true)).toBe(true);
  expect(result.options[2].durationSeconds).toBe(1920);
});
test('vehicle unavailable is explicit and never selected; unconfigured road fares do not spend lookups',async()=>{
  const ctx=context();ctx.drivingAllowed=false;
  const s=setup({loadAuthorizedContext:jest.fn(async()=>ctx),loadAssumptions:jest.fn(async()=>({version:'unset'}))});
  const result=await s.service(request());
  expect(result.selectedMode).toBe('transit');
  expect(result.options[2].reason).toBe('missing_fare_assumption');
  expect(result.options[3].reason).toBe('vehicle_unavailable');
  expect(s.routesClient.computeRoute).toHaveBeenCalledTimes(2);
});
test.each([null,{auth:{uid:'user',token:{}}},{auth:{uid:true,token:{email_verified:true}}}])('auth fails before readers or quota %j',async req=>{
  const s=setup();
  await expect(s.service(req)).rejects.toMatchObject({code:req?.auth?.uid==='user'?'permission-denied':'unauthenticated'});
  expect(s.loadAuthorizedContext).not.toHaveBeenCalled();expect(s.consumeQuota).not.toHaveBeenCalled();
});
test.each([{},[],{listingId:'../private',roomId:'room',departTime:'2026-11-16T08:05:00+08:00'},
  {...request().data,userId:'victim'},{...request().data,roomPriceSen:0},{...request().data,origin:{}},
  {...request().data,departTime:'2026-02-30T08:00:00+08:00'},
  {...request().data,departTime:'2026-11-15T08:00:00+08:00'}])('rejects invalid body before reads/paid work %j',async data=>{
  const s=setup();await expect(s.service(request(data))).rejects.toMatchObject({code:'invalid-argument'});
  expect(s.loadAuthorizedContext).not.toHaveBeenCalled();expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
});
test.each([{viewerUid:'victim'},{roomListingId:'other-listing'},{listingId:'other'},{roomId:'other'}])('enforces context identity/membership %j',async changes=>{
  const s=setup({loadAuthorizedContext:jest.fn(async()=>({...context(),...changes}))});
  await expect(s.service(request())).rejects.toMatchObject({code:'permission-denied'});
  expect(s.loadAssumptions).not.toHaveBeenCalled();expect(s.consumeQuota).not.toHaveBeenCalled();
});
test.each([{origin:{latitude:91,longitude:101}},{roomPriceSen:-1},{drivingAllowed:undefined},
  {classStartTime:'2026-11-16T08:00:00+08:00'}])('invalid server context fails before quota/provider %j',async changes=>{
  const s=setup({loadAuthorizedContext:jest.fn(async()=>({...context(),...changes}))});
  await expect(s.service(request())).rejects.toMatchObject({code:'failed-precondition'});
  expect(s.consumeQuota).not.toHaveBeenCalled();expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
});
test('invalid server rates and denied quota never call provider',async()=>{
  const s=setup({loadAssumptions:jest.fn(async()=>({version:'bad',driving:{fuelSenPerLitre:-1}}))});
  await expect(s.service(request())).rejects.toMatchObject({code:'failed-precondition'});
  expect(s.consumeQuota).not.toHaveBeenCalled();expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
  const denied=setup({consumeQuota:jest.fn(async()=>false)});
  await expect(denied.service(request())).rejects.toMatchObject({code:'resource-exhausted'});
  expect(denied.routesClient.computeRoute).not.toHaveBeenCalled();
});
test('isolates one provider failure and never leaks provider exception text',async()=>{
  const s=setup({routesClient:{computeRoute:jest.fn(async input=>{
    if(input.travelMode==='DRIVE') throw new Error('secret-key private provider message'); return fact(input);
  })}});
  const result=await s.service(request());
  expect(result).toMatchObject({selectedMode:'transit',trueMonthlyCostSen:61200,partialProviderFailure:true});
  expect(result.options[3].reason).toBe('provider_unavailable');
  expect(JSON.stringify(result)).not.toMatch(/secret-key|private provider/);
});
test('all failed providers reject unavailable instead of a false no-on-time determination',async()=>{
  const s=setup({routesClient:{computeRoute:jest.fn(async()=>{throw new Error('private upstream');})}});
  await expect(s.service(request())).rejects.toMatchObject({code:'unavailable',message:'Commute routes unavailable'});
});
test('late authorizer after overall deadline cannot start config/quota/provider work',async()=>{
  let resolveContext;
  const s=setup({timeoutMs:20,loadAuthorizedContext:jest.fn(()=>new Promise(resolve=>{resolveContext=resolve;}))});
  await expect(s.service(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(s.loadAuthorizedContext.mock.calls[0][0].signal.aborted).toBe(true);
  resolveContext(context()); await new Promise(resolve=>setImmediate(resolve));
  expect(s.loadAssumptions).not.toHaveBeenCalled();expect(s.consumeQuota).not.toHaveBeenCalled();
  expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
});
test('overall deadline signals in-flight providers and returns no late calculation',async()=>{
  const client={computeRoute:jest.fn(()=>new Promise(()=>{}))};
  const s=setup({timeoutMs:20,routesClient:client});
  await expect(s.service(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(client.computeRoute.mock.calls.length).toBe(3);
  expect(client.computeRoute.mock.calls.every(([,options])=>options.signal.aborted)).toBe(true);
});
test('sanitizes authorizer errors with no private IDs/details',async()=>{
  const s=setup({loadAuthorizedContext:jest.fn(async()=>{const e=new Error('private host email');e.code='permission-denied';throw e;})});
  await expect(s.service(request())).rejects.toMatchObject({code:'permission-denied',message:'Commute access denied'});
});
test('uses the actual Routes fetch boundary through full service with no live HTTP',async()=>{
  const fetchImpl=jest.fn(async(url,options)=>{
    const body=JSON.parse(options.body);
    return new Response(JSON.stringify({routes:[{duration:body.travelMode==='WALK'?'4000s':'1800s',distanceMeters:6000,
      travelAdvisory:body.travelMode==='TRANSIT'?{transitFare:{currencyCode:'MYR',units:'2',nanos:500000000}}:{}}]}));
  });
  const s=setup({routesClient:createRoutesClient({apiKey:'test-server-key',fetchImpl,now:()=>now})});
  const result=await s.service(request());
  expect(result.trueMonthlyCostSen).toBe(60560);
  expect(result.options[3].routeSource).toBe('Google Maps');
  expect(fetchImpl).toHaveBeenCalledTimes(3);
});
test('requires all trusted adapters and bounded deadline configuration',()=>{
  expect(()=>createCommuteService({})).toThrow();
  expect(()=>setup({timeoutMs:0})).toThrow();
  expect(()=>setup({timeoutMs:10001})).toThrow();
});

test('maps emissions after mode assignment even when road lookup is shared',async()=>{
  const emissionProfileFor=jest.fn(({mode,route})=>{
    if (!['driving','e_hailing'].includes(mode)) return undefined;
    return {allowUkProxy:true,legs:[{factorId:mode==='driving'?'uk-average-petrol-car':'uk-regular-taxi',
      distanceMetres:route.distanceMetres,occupants:1}]};
  });
  const s=setup({emissionProfileFor});
  const result=await s.service(request());
  expect(s.routesClient.computeRoute).toHaveBeenCalledTimes(3);
  expect(result.options[3].co2GramsPerTrip).toBe(965);
  expect(result.options[2].co2GramsPerTrip).toBe(1238);
  expect(result.options[1].co2GramsPerTrip).toBeNull();
  expect(JSON.stringify(emissionProfileFor.mock.calls)).not.toMatch(/privateEmail|unitAddress|timetable/);
});

test.each(['config','quota'])('late %s adapter cannot start provider work',async stage=>{
  let release;
  const waiting=jest.fn(()=>new Promise(resolve=>{release=resolve;}));
  const s=setup({timeoutMs:20,...(stage==='config'?{loadAssumptions:waiting}:{consumeQuota:waiting})});
  await expect(s.service(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  release(stage==='config'?config():true);
  await new Promise(resolve=>setImmediate(resolve));
  expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
  if(stage==='config') expect(s.consumeQuota).not.toHaveBeenCalled();
});

test('a broken emissions mapper fails rather than emitting misleading estimates',async()=>{
  const s=setup({emissionProfileFor:()=>({legs:'invalid'})});
  await expect(s.service(request())).rejects.toMatchObject({code:'failed-precondition'});
});

test('expired deadline stops work even when synchronous adapter delays the timer callback',async()=>{
  const s=setup({timeoutMs:10,loadAuthorizedContext:jest.fn(async()=>{
    const started=performance.now();
    while(performance.now()-started<25) { /* Simulate a blocking adapter. */ }
    return context();
  })});
  await expect(s.service(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(s.loadAssumptions).not.toHaveBeenCalled();
  expect(s.routesClient.computeRoute).not.toHaveBeenCalled();
});

test('a blocking provider cannot dispatch additional requests after the deadline',async()=>{
  const computeRoute=jest.fn(async input=>{
    const started=performance.now();
    while(performance.now()-started<25) { /* Simulate a blocking transport. */ }
    return fact(input);
  });
  const s=setup({timeoutMs:10,routesClient:{computeRoute}});
  await expect(s.service(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(computeRoute).toHaveBeenCalledTimes(1);
});
