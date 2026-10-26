const {createRoutesClient} = require('../src/routes-client');
const now = Date.parse('2026-11-16T00:00:00Z');
const request = () => ({origin:{latitude:3.1,longitude:101.65},destination:{latitude:3.12,longitude:101.66},
  travelMode:'TRANSIT',departTime:'2026-11-16T08:05:00+08:00'});
const route = (changes={}) => ({duration:'1800s',distanceMeters:6000,...changes});
const response = (routes=[route()], extra={}) => new Response(JSON.stringify({routes,...extra}),{status:200});
function setup(reply=()=>response(), options={}) {
  const fetchImpl=jest.fn(reply);
  return {fetchImpl,client:createRoutesClient({apiKey:'test-server-key',fetchImpl,now:()=>now,...options})};
}

test('sends exact fixed-endpoint transit request, narrow field mask and header credential', async () => {
  const {client,fetchImpl}=setup();
  await client.computeRoute(request());
  expect(fetchImpl).toHaveBeenCalledTimes(1);
  const [url,init]=fetchImpl.mock.calls[0];
  expect(url).toBe('https://routes.googleapis.com/directions/v2:computeRoutes');
  expect(init).toMatchObject({method:'POST',redirect:'error',headers:{'X-Goog-Api-Key':'test-server-key','Content-Type':'application/json'}});
  expect(init.headers['X-Goog-FieldMask']).toBe('routes.duration,routes.distanceMeters,routes.warnings,routes.travelAdvisory.transitFare,fallbackInfo');
  expect(JSON.parse(init.body)).toEqual({origin:{location:{latLng:{latitude:3.1,longitude:101.65}}},
    destination:{location:{latLng:{latitude:3.12,longitude:101.66}}},travelMode:'TRANSIT',
    departureTime:'2026-11-16T00:05:00.000Z',regionCode:'MY',languageCode:'en',units:'METRIC',computeAlternativeRoutes:false});
  expect(init.signal).toBeInstanceOf(AbortSignal);
});
test.each(['WALK','DRIVE'])('supports %s, only adding traffic option for explicit DRIVE',async travelMode=>{
  const {client,fetchImpl}=setup();
  await client.computeRoute({...request(),travelMode,...(travelMode==='DRIVE'?{trafficAware:true}:{})});
  const body=JSON.parse(fetchImpl.mock.calls[0][1].body);
  expect(body.routingPreference).toBe(travelMode==='DRIVE'?'TRAFFIC_AWARE':undefined);
  expect(body).not.toHaveProperty('arrivalTime');
});
test('normalizes fields, ceiling seconds, warnings, MYR fare and source without private/raw fields', async () => {
  const {client}=setup(()=>response([route({duration:'1800.000000001s',warnings:['Use designated footpaths'],
    travelAdvisory:{transitFare:{currencyCode:'MYR',units:'2',nanos:505000000}},description:'private address',routeToken:'secret-token'})]));
  expect(await client.computeRoute(request())).toEqual({status:'available',durationSeconds:1801,distanceMetres:6000,
    transitFareSen:251,warnings:['Use designated footpaths'],trafficFallback:false,source:'Google Maps'});
});
test.each([
  [undefined,null], [{currencyCode:'USD',units:'2'},null], [{currencyCode:'MYR'},0],
  [{currencyCode:'MYR',units:'0',nanos:0},0], [{currencyCode:'MYR',units:'2',nanos:504999999},250],
])('absent/foreign fare is unknown; explicitly known zero/rounding %j',async(fare,expected)=>{
  const {client}=setup(()=>response([route(fare===undefined?{}:{travelAdvisory:{transitFare:fare}})]));
  expect((await client.computeRoute(request())).transitFareSen).toBe(expected);
});
test('preserves fallback presence without projecting provider diagnostic details',async()=>{
  const {client}=setup(()=>response([route()],{fallbackInfo:{reason:'SERVER_ERROR',routingMode:'FALLBACK_TRAFFIC_UNAWARE'}}));
  expect(await client.computeRoute({...request(),travelMode:'DRIVE',trafficAware:true})).toMatchObject({trafficFallback:true});
});
test.each([[],undefined])('empty or omitted routes mean missing_route %j',async routes=>{
  const {client}=setup(()=>routes===undefined?new Response('{}'):response(routes));
  expect(await client.computeRoute(request())).toEqual({status:'unavailable',reason:'missing_route',source:'Google Maps'});
});
test.each([
  {duration:'-1s'},{duration:'1.1234567890s'},{duration:'NaNs'},{duration:'172801s'},
  {duration:0},{duration:undefined},{distanceMeters:undefined},{distanceMeters:-1},{distanceMeters:1.1},
  {warnings:['x'.repeat(2001)]},{warnings:'warning'},{warnings:[42]},
  {travelAdvisory:{transitFare:{currencyCode:'MYR',units:'-1'}}},
  {travelAdvisory:{transitFare:{currencyCode:'MYR',units:'1',nanos:1000000000}}},
  {travelAdvisory:{transitFare:{currencyCode:'MYR',units:'9007199254740991'}}},
])('rejects malformed provider fields without raw details %j',async changes=>{
  const {client}=setup(()=>response([route(changes)]));
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:'unavailable',message:'Routes provider response invalid'});
});
test.each([
  {origin:{latitude:91,longitude:0}},{origin:{latitude:0,longitude:181}},
  {origin:{latitude:'3',longitude:101}},{destination:null},{travelMode:'BICYCLE'},
  {departTime:'2026-02-30T00:00:00Z'},{departTime:'2026-11-16T00:05:00'},
  {departTime:'2026-11-16T00:05:00+25:00'},{departTime:'2026-11-16T24:00:00Z'},
  {departTime:'2026-07-01T00:00:00Z'},{departTime:'2027-07-01T00:00:00Z'},
  {trafficAware:true},{trafficAware:'true'},{arrivalTime:'2026-11-16T00:30:00Z'},{url:'http://localhost/'},
])('rejects invalid inputs before spending requests %j',async changes=>{
  const {client,fetchImpl}=setup();
  await expect(client.computeRoute({...request(),...changes})).rejects.toMatchObject({code:'invalid-argument'});
  expect(fetchImpl).not.toHaveBeenCalled();
});
test('disallows past non-transit time; allows transit within window',async()=>{
  const {client,fetchImpl}=setup();
  await expect(client.computeRoute({...request(),travelMode:'DRIVE',departTime:'2026-11-15T00:00:00Z'})).rejects.toMatchObject({code:'invalid-argument'});
  await client.computeRoute({...request(),departTime:'2026-11-15T00:00:00Z'});
  expect(fetchImpl).toHaveBeenCalledTimes(1);
});
test('transit accepts both exact time-window boundaries and rejects one millisecond outside',async()=>{
  const {client,fetchImpl}=setup();
  for (const delta of [-7*86400000,100*86400000]) {
    await client.computeRoute({...request(),departTime:new Date(now+delta).toISOString()});
  }
  for (const delta of [-7*86400000-1,100*86400000+1]) {
    await expect(client.computeRoute({...request(),departTime:new Date(now+delta).toISOString()}))
      .rejects.toMatchObject({code:'invalid-argument'});
  }
  expect(fetchImpl).toHaveBeenCalledTimes(2);
});
test('48-hour duration accepts exact bound but rejects fractional overrun after ceiling',async()=>{
  expect((await setup(()=>response([route({duration:'172800.000000000s'})])).client.computeRoute(request())).durationSeconds).toBe(172800);
  await expect(setup(()=>response([route({duration:'172800.000000001s'})])).client.computeRoute(request()))
    .rejects.toMatchObject({code:'unavailable'});
});
test.each([undefined,'','\r\nsecret'])('requires explicit safe server credential %j',apiKey=>{
  expect(()=>createRoutesClient({apiKey})).toThrow();
});
test.each([0,10001,NaN,1.5])('bounds configured timeout %j',timeoutMs=>expect(()=>setup(undefined,{timeoutMs})).toThrow());
test.each([429,401,403,500,302])('sanitizes HTTP %d without retries/body exposure',async status=>{
  const {client,fetchImpl}=setup(()=>new Response('private provider message test-server-key',{status}));
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:status===429?'resource-exhausted':'unavailable',message:'Routes provider unavailable'});
  expect(fetchImpl).toHaveBeenCalledTimes(1);
});
test('sanitizes network and invalid JSON failures',async()=>{
  const {client}=setup(()=>{throw new Error('test-server-key private-network');});
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:'unavailable',message:'Routes provider unavailable'});
  await expect(setup(()=>new Response('not JSON')).client.computeRoute(request())).rejects.toMatchObject({code:'unavailable'});
});
test('rejects too many or malformed route containers',async()=>{
  for (const payload of [{routes:[route(),route()]},{routes:null},{routes:'bad'},[]]) {
    const {client}=setup(()=>new Response(JSON.stringify(payload)));
    await expect(client.computeRoute(request())).rejects.toMatchObject({code:'unavailable'});
  }
});
test('aborts stalled request within deadline even if injected transport ignores signal',async()=>{
  const {client,fetchImpl}=setup(()=>new Promise(()=>{}),{timeoutMs:20});
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(fetchImpl.mock.calls[0][1].signal.aborted).toBe(true);
});
test('deadline includes a stalled body and cancels the stream',async()=>{
  const cancel=jest.fn();
  const body=new ReadableStream({start(controller){controller.enqueue(new TextEncoder().encode('{'));},cancel});
  const {client}=setup(()=>new Response(body),{timeoutMs:20});
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:'deadline-exceeded'});
  expect(cancel).toHaveBeenCalled();
});
test('caps streamed response bytes and cancels instead of buffering unbounded data',async()=>{
  const cancel=jest.fn();
  const body=new ReadableStream({pull(controller){controller.enqueue(new Uint8Array(131073));},cancel});
  const {client}=setup(()=>new Response(body));
  await expect(client.computeRoute(request())).rejects.toMatchObject({code:'unavailable'});
  expect(cancel).toHaveBeenCalled();
});
