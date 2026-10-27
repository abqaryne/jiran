const {session} = require('./session');
const {parseDepartureTime,validateRouteRequest} = require('./routes-client');
const {priceRoutes} = require('./commute-pricing');
const {calculateCommute} = require('./commute');

class CommuteError extends Error {
  constructor(code,message) { super(message); this.code=code; }
}
function fail(code,message) { throw new CommuteError(code,message); }
function object(value) { return value!==null && typeof value==='object' && !Array.isArray(value); }
function unavailable(mode,reason) { return {mode,status:'unavailable',reason}; }
function body(data,clock) {
  if (!object(data) || Object.keys(data).length!==3
      || Object.keys(data).some(key=>!['listingId','roomId','departTime'].includes(key))
      || ![data.listingId,data.roomId].every(id=>typeof id==='string' && /^[A-Za-z0-9_-]{1,128}$/.test(id))) {
    fail('invalid-argument','Invalid commute request');
  }
  let departure;
  try { departure=parseDepartureTime(data.departTime); } catch { fail('invalid-argument','Invalid commute departure'); }
  if (!Number.isFinite(clock)) fail('failed-precondition','Commute clock unavailable');
  if (departure<clock || departure>clock+100*86400000) fail('invalid-argument','Invalid commute departure');
  return {listingId:data.listingId,roomId:data.roomId,departTime:new Date(departure).toISOString(),departure};
}
function providerFact(raw,mode) {
  if (!object(raw)) throw new TypeError('Invalid route facts');
  const fields=['status','reason','durationSeconds','distanceMetres','transitFareSen','source','warnings','trafficFallback'];
  return {mode,...Object.fromEntries(fields.filter(key=>Object.hasOwn(raw,key)).map(key=>[key,raw[key]]))};
}

/** Internal orchestration only. Production authorizers, config, quota and managed Routes setup are required. */
function createCommuteService({loadAuthorizedContext,loadAssumptions,consumeQuota,routesClient,
  now=Date.now,timeoutMs=2800,trafficAware=false,emissionProfileFor=()=>undefined}={}) {
  if (![loadAuthorizedContext,loadAssumptions,consumeQuota,now,emissionProfileFor].every(value=>typeof value==='function')
      || typeof routesClient?.computeRoute!=='function' || !Number.isInteger(timeoutMs) || timeoutMs<1 || timeoutMs>10000
      || typeof trafficAware!=='boolean') fail('failed-precondition','Commute adapters required');
  return async function computeCommute(request) {
    const {uid}=session(request);
    if (typeof uid!=='string' || !uid || uid.length>128) fail('unauthenticated','Authentication required');
    const input=body(request.data,now());
    const controller=new AbortController();
    const signal=controller.signal;
    const expiresAt=performance.now()+timeoutMs;
    const active=()=>{
      if(signal.aborted || performance.now()>=expiresAt) {
        controller.abort();
        fail('deadline-exceeded','Commute request timed out');
      }
    };
    let timer;
    const deadline=new Promise((resolve,reject)=>{
      timer=setTimeout(()=>{
        reject(new CommuteError('deadline-exceeded','Commute request timed out'));
        controller.abort();
      },timeoutMs);
    });
    async function execute() {
      const context=await loadAuthorizedContext({uid,listingId:input.listingId,roomId:input.roomId,signal});
      active();
      if (!object(context) || context.viewerUid!==uid || context.listingId!==input.listingId
          || context.roomId!==input.roomId || context.roomListingId!==input.listingId) fail('permission-denied','Commute access denied');
      const origin={latitude:context.origin?.latitude,longitude:context.origin?.longitude};
      const destination={latitude:context.destination?.latitude,longitude:context.destination?.longitude};
      const calculation={roomPriceSen:context.roomPriceSen,utilitiesShareSen:context.utilitiesShareSen,
        monthlyTrips:context.monthlyTrips,departTime:input.departTime,classStartTime:context.classStartTime};
      try {
        if (typeof context.drivingAllowed!=='boolean' || parseDepartureTime(context.classStartTime)<=input.departure) throw new Error();
        calculateCommute({...calculation,modes:[]});
        validateRouteRequest({origin,destination,travelMode:'TRANSIT',departTime:input.departTime},now());
      } catch { fail('failed-precondition','Commute context unavailable'); }
      const drivingAllowed=context.drivingAllowed;
      let assumptions;
      try {
        const loaded=await loadAssumptions({uid,signal});
        active();
        assumptions=structuredClone(loaded);
        priceRoutes({routes:[],assumptions});
      } catch (error) {
        active();
        fail('failed-precondition','Commute configuration unavailable');
      }
      const jobs=new Map();
      const routes=[];
      function add(mode,travelMode,departure) {
        if (!Number.isSafeInteger(departure) || Math.abs(departure)>8640000000000000) throw new Error();
        const query={origin,destination,travelMode,departTime:new Date(departure).toISOString(),
          ...(travelMode==='DRIVE'?{trafficAware}:{})};
        validateRouteRequest(query,now());
        const key=JSON.stringify(query);
        if (!jobs.has(key)) jobs.set(key,{query,modes:[]});
        jobs.get(key).modes.push(mode);
      }
      try {
        add('walking','WALK',input.departure);
        add('transit','TRANSIT',input.departure);
        if (drivingAllowed && assumptions.driving) add('driving','DRIVE',input.departure);
        else routes.push(unavailable('driving',drivingAllowed?'missing_fare_assumption':'vehicle_unavailable'));
        if (assumptions.e_hailing) add('e_hailing','DRIVE',input.departure+assumptions.e_hailing.pickupSeconds*1000);
        else routes.push(unavailable('e_hailing','missing_fare_assumption'));
      } catch { fail('failed-precondition','Commute routing inputs unavailable'); }
      active();
      const admitted=await consumeQuota({uid,maxProviderCalls:jobs.size,signal});
      active();
      if (admitted!==true) fail('resource-exhausted','Commute request limit reached');
      const results=await Promise.all([...jobs.values()].map(async job=>{
        active();
        let raw;
        try { raw=await routesClient.computeRoute(job.query,{signal}); }
        catch { active(); return job.modes.map(mode=>unavailable(mode,'provider_unavailable')); }
        active();
        return job.modes.map(mode=>{
          try {
            const route=providerFact(raw,mode);
            priceRoutes({routes:[route],assumptions});
            return route;
          } catch { return unavailable(mode,'provider_unavailable'); }
        });
      }));
      active();
      routes.push(...results.flat());
      const partialProviderFailure=routes.some(route=>route.reason==='provider_unavailable');
      if (partialProviderFailure && !routes.some(route=>route.status==='available')) fail('unavailable','Commute routes unavailable');
      try {
        for (const route of routes.filter(value=>value.status==='available')) {
          const profile=emissionProfileFor({mode:route.mode,route:structuredClone(route)});
          if (profile!=null) route.emissionProfile=profile;
        }
        const priced=priceRoutes({routes,assumptions});
        const result=calculateCommute({...calculation,modes:priced.modes});
        active();
        return {...result,assumptionVersion:priced.assumptionVersion,partialProviderFailure};
      } catch {
        active();
        fail('failed-precondition','Commute calculation inputs unavailable');
      }
    }
    try { return await Promise.race([deadline,execute()]); }
    catch (error) {
      if (error instanceof CommuteError) throw error;
      const messages={'permission-denied':'Commute access denied','not-found':'Commute context not found',
        'failed-precondition':'Commute configuration unavailable','resource-exhausted':'Commute request limit reached'};
      const code=Object.hasOwn(messages,error?.code)?error.code:'unavailable';
      fail(code,messages[code]??'Commute service unavailable');
    } finally { clearTimeout(timer); }
  };
}
module.exports = {createCommuteService};
