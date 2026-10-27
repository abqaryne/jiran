const ENDPOINT = 'https://routes.googleapis.com/directions/v2:computeRoutes';
const FIELD_MASK = 'routes.duration,routes.distanceMeters,routes.warnings,routes.travelAdvisory.transitFare,fallbackInfo';
const MAX_BYTES = 128 * 1024;
const DAY_MS = 86400000;
const routingPolicy = require('../config/routing-policy.json');

class RoutesError extends Error {
  constructor(code, message) { super(message); this.code = code; }
}
function fail(code, message) { throw new RoutesError(code, message); }
function invalid() { fail('invalid-argument', 'Invalid route request'); }
function malformed() { fail('unavailable', 'Routes provider response invalid'); }
function object(value) { return value !== null && typeof value === 'object' && !Array.isArray(value); }
function safeInteger(value) { return Number.isSafeInteger(value) && value >= 0; }

function coordinate(value) {
  if (!object(value) || !Number.isFinite(value.latitude) || Math.abs(value.latitude) > 90
      || !Number.isFinite(value.longitude) || Math.abs(value.longitude) > 180) invalid();
  return {location:{latLng:{latitude:value.latitude,longitude:value.longitude}}};
}
function departure(value) {
  const parts = typeof value === 'string' && /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:\.\d{1,3})?(?:Z|[+-](\d{2}):(\d{2}))$/.exec(value);
  if (!parts) invalid();
  const [year,month,day,hour,minute,second,offsetHour=0,offsetMinute=0] = parts.slice(1).map(Number);
  const leap = year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
  const days = [31,leap?29:28,31,30,31,30,31,31,30,31,30,31];
  const time = Date.parse(value);
  if (month < 1 || month > 12 || day < 1 || day > days[month-1]
      || hour > 23 || minute > 59 || second > 59 || offsetHour > 23 || offsetMinute > 59
      || !Number.isFinite(time)) invalid();
  return time;
}
function requestBody(input, now) {
  const allowed = ['origin','destination','travelMode','departTime','trafficAware'];
  if (!object(input) || Object.keys(input).some(key => !allowed.includes(key))
      || !['WALK','TRANSIT','DRIVE'].includes(input.travelMode)
      || (input.trafficAware !== undefined && typeof input.trafficAware !== 'boolean')
      || (input.trafficAware === true && input.travelMode !== 'DRIVE')) invalid();
  const time = departure(input.departTime);
  if (!Number.isFinite(now)) fail('failed-precondition', 'Route clock unavailable');
  if (input.travelMode === 'TRANSIT' ? time < now - 7*DAY_MS || time > now + 100*DAY_MS : time < now) invalid();
  const body = {origin:coordinate(input.origin),destination:coordinate(input.destination),
    travelMode:input.travelMode,departureTime:new Date(time).toISOString(),
    regionCode:'MY',languageCode:'en',units:'METRIC',computeAlternativeRoutes:false};
  if (input.travelMode === 'DRIVE' && input.trafficAware) body.routingPreference = 'TRAFFIC_AWARE';
  return body;
}
function duration(value) {
  const parts = typeof value === 'string' && /^(\d{1,6})(?:\.(\d{1,9}))?s$/.exec(value);
  if (!parts) malformed();
  const seconds = Number(parts[1]) + (parts[2] && /[1-9]/.test(parts[2]) ? 1 : 0);
  if (seconds > 172800) malformed();
  return seconds;
}
function fareSen(fare) {
  if (fare === undefined) return null;
  if (!object(fare) || typeof fare.currencyCode !== 'string' || !/^[A-Z]{3}$/.test(fare.currencyCode)) malformed();
  if (fare.currencyCode !== 'MYR') return null;
  const units = fare.units ?? '0', nanos = fare.nanos ?? 0;
  if (typeof units !== 'string' || !/^(0|[1-9]\d{0,15})$/.test(units)
      || !safeInteger(nanos) || nanos >= 1000000000) malformed();
  const sen = BigInt(units)*100n + (BigInt(nanos)+5000000n)/10000000n;
  if (sen > BigInt(Number.MAX_SAFE_INTEGER)) malformed();
  return Number(sen);
}
function normalize(payload) {
  if (!object(payload) || (payload.routes !== undefined && (!Array.isArray(payload.routes) || payload.routes.length > 1))) malformed();
  if (!payload.routes?.length) return {status:'unavailable',reason:'missing_route',source:'Google Maps'};
  const route = payload.routes[0];
  if (!object(route) || !safeInteger(route.distanceMeters)) malformed();
  const warnings = route.warnings ?? [];
  if (!Array.isArray(warnings) || warnings.length > 20
      || warnings.some(warning => typeof warning !== 'string' || warning.length > 2000)) malformed();
  if (route.travelAdvisory !== undefined && !object(route.travelAdvisory)) malformed();
  if (payload.fallbackInfo !== undefined && !object(payload.fallbackInfo)) malformed();
  return {status:'available',durationSeconds:duration(route.duration),distanceMetres:route.distanceMeters,
    transitFareSen:fareSen(route.travelAdvisory?.transitFare),warnings:[...warnings],
    trafficFallback:payload.fallbackInfo !== undefined,source:'Google Maps'};
}

/** Server-only boundary. Caller authorizes resources and supplies managed credentials. No cache/retries. */
function createRoutesClient({apiKey,fetchImpl=globalThis.fetch,timeoutMs=2500,now=Date.now} = {}) {
  if (!routingPolicy.liveGoogleRoutingEnabled && fetchImpl===globalThis.fetch) {
    fail('failed-precondition', 'Live Google routing disabled by free-only policy');
  }
  if (typeof apiKey !== 'string' || !/^[\x21-\x7e]{1,200}$/.test(apiKey)) {
    fail('failed-precondition', 'Routes server credential required');
  }
  if (typeof fetchImpl !== 'function' || typeof now !== 'function' || !Number.isInteger(timeoutMs)
      || timeoutMs < 1 || timeoutMs > 10000) fail('failed-precondition', 'Invalid Routes client configuration');
  return {async computeRoute(input, options={}) {
    if (!object(options) || Object.keys(options).some(key=>key!=='signal')
        || (options.signal!==undefined && !(options.signal instanceof AbortSignal))) invalid();
    const callerSignal=options.signal;
    if (callerSignal?.aborted) fail('cancelled', 'Routes provider request cancelled');
    const body = requestBody(input, now()); // Validate before a billable request.
    const controller = new AbortController();
    let reader, timer, onCallerAbort;
    const deadline = new Promise((resolve, reject) => {
      timer = setTimeout(() => {
        reject(new RoutesError('deadline-exceeded', 'Routes provider timed out'));
        controller.abort();
        if (reader) reader.cancel().catch(() => {});
      }, timeoutMs);
    });
    const cancellation = new Promise((resolve, reject) => {
      onCallerAbort = () => {
        reject(new RoutesError('cancelled', 'Routes provider request cancelled'));
        controller.abort();
        if (reader) reader.cancel().catch(() => {});
      };
      callerSignal?.addEventListener('abort', onCallerAbort, {once:true});
    });
    async function execute() {
      const response = await fetchImpl(ENDPOINT, {method:'POST',redirect:'error',
        headers:{'Content-Type':'application/json','X-Goog-Api-Key':apiKey,'X-Goog-FieldMask':FIELD_MASK},
        body:JSON.stringify(body),signal:controller.signal});
      if (controller.signal.aborted) {
        if (response.body) response.body.cancel().catch(() => {});
        fail('deadline-exceeded', 'Routes provider timed out');
      }
      if (!response.ok) {
        if (response.body) response.body.cancel().catch(() => {});
        fail(response.status === 429 ? 'resource-exhausted' : 'unavailable', 'Routes provider unavailable');
      }
      if (!response.body) malformed();
      reader = response.body.getReader();
      let size = 0;
      const chunks = [];
      while (true) {
        const {done,value} = await reader.read();
        if (done) break;
        size += value.byteLength;
        if (size > MAX_BYTES) { reader.cancel().catch(() => {}); malformed(); }
        chunks.push(Buffer.from(value));
      }
      let payload;
      try { payload = JSON.parse(Buffer.concat(chunks).toString('utf8')); } catch { malformed(); }
      return normalize(payload);
    }
    try { return await Promise.race([deadline, cancellation, execute()]); }
    catch (error) {
      if (error instanceof RoutesError) throw error;
      fail('unavailable', 'Routes provider unavailable');
    } finally {
      clearTimeout(timer);
      callerSignal?.removeEventListener('abort', onCallerAbort);
    }
  }};
}
module.exports = {createRoutesClient,parseDepartureTime:departure,validateRouteRequest:requestBody};
