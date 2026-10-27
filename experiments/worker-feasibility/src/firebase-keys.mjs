import {createLocalJWKSet,errors} from 'jose';
import {HttpError} from './http-error.mjs';

const ENDPOINT='https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com';
const MAX_BYTES=65536;
const MAX_TTL=21600000;
const privateFields=['d','p','q','dp','dq','qi','oth','k'];
function unavailable() { return new HttpError(503,'authentication-unavailable'); }
function object(value) { return value!==null && typeof value==='object' && !Array.isArray(value); }
function kid(value) { return typeof value==='string' && /^[A-Za-z0-9_-]{1,128}$/.test(value); }
function publicKeys(payload) {
  if(!object(payload) || !Array.isArray(payload.keys) || !payload.keys.length || payload.keys.length>10) throw unavailable();
  const seen=new Set();
  return {keys:payload.keys.map(key=>{
    if(!object(key) || key.kty!=='RSA' || !kid(key.kid) || seen.has(key.kid)
        || privateFields.some(field=>Object.hasOwn(key,field))
        || (key.alg!==undefined && key.alg!=='RS256') || (key.use!==undefined && key.use!=='sig')
        || typeof key.n!=='string' || !/^[A-Za-z0-9_-]{342,1366}$/.test(key.n)
        || typeof key.e!=='string' || !/^[A-Za-z0-9_-]{1,12}$/.test(key.e)
        || (key.key_ops!==undefined && (!Array.isArray(key.key_ops) || key.key_ops.length!==1 || key.key_ops[0]!=='verify'))) {
      throw unavailable();
    }
    seen.add(key.kid);
    return {kty:'RSA',kid:key.kid,alg:'RS256',use:'sig',n:key.n,e:key.e};
  })};
}
function ttl(headers) {
  const directives=(headers.get('cache-control')??'').split(',').map(value=>value.trim());
  if(directives.some(value=>/^(no-store|no-cache|private)(?:\s*=|$)/i.test(value))) return 0;
  const ages=directives.filter(value=>/^max-age\s*=/i.test(value));
  if(ages.length!==1) return 0;
  const match=/^max-age\s*=\s*(?:"(\d+)"|(\d+))$/i.exec(ages[0]);
  const age=headers.get('age')??'0';
  if(!match || !/^\d+$/.test(age)) return 0;
  const maximum=Number(match[1]??match[2]), elapsed=Number(age);
  if(!Number.isSafeInteger(maximum) || !Number.isSafeInteger(elapsed)) return 0;
  if(maximum>0 && elapsed>=maximum) throw unavailable();
  return Math.min(Math.max(0,maximum-elapsed)*1000,MAX_TTL);
}

/** Fixed public-key source; injected clocks/transports support offline rotation tests. */
export function createFirebaseKeyResolver({fetchImpl=globalThis.fetch,now=()=>performance.now(),
  timeoutMs=1000,cooldownMs=30000}={}) {
  if(typeof fetchImpl!=='function' || typeof now!=='function' || !Number.isInteger(timeoutMs)
      || timeoutMs<1 || timeoutMs>10000 || !Number.isInteger(cooldownMs) || cooldownMs<1 || cooldownMs>300000) {
    throw new TypeError('Invalid key resolver configuration');
  }
  let cached,pending,lastKnownKids,lastAttempt=-Infinity,failureUntil=-Infinity;
  function clock() {
    const value=now();
    if(!Number.isFinite(value) || value<0 || value>Number.MAX_SAFE_INTEGER-MAX_TTL) throw unavailable();
    return value;
  }
  async function load() {
    const requestedAt=clock(),started=performance.now(),controller=new AbortController();
    let reader,timer,response;
    function active() {
      if(controller.signal.aborted || performance.now()-started>=timeoutMs) {
        controller.abort();
        throw unavailable();
      }
    }
    const deadline=new Promise((resolve,reject)=>{
      timer=setTimeout(()=>{
        controller.abort();
        if(reader) reader.cancel().catch(()=>{});
        reject(unavailable());
      },timeoutMs);
    });
    async function execute() {
      response=await fetchImpl(ENDPOINT,{method:'GET',redirect:'manual',
        headers:{Accept:'application/json'},signal:controller.signal});
      try { active(); } catch(error) { response.body?.cancel().catch(()=>{}); throw error; }
      if(response.status!==200 || !/^application\/(?:json|jwk-set\+json)(?:\s*;.*)?$/i.test(response.headers.get('content-type')??'')) {
        response.body?.cancel().catch(()=>{});
        throw unavailable();
      }
      if(!response.body) throw unavailable();
      const lifetime=ttl(response.headers);
      reader=response.body.getReader();
      try {
        const chunks=[];
        let size=0;
        while(true) {
          const {done,value}=await reader.read();
          active();
          if(done) break;
          size+=value.byteLength;
          if(size>MAX_BYTES) { reader.cancel().catch(()=>{}); throw unavailable(); }
          chunks.push(value);
        }
        const bytes=new Uint8Array(size);
        let offset=0;
        for(const chunk of chunks) { bytes.set(chunk,offset); offset+=chunk.byteLength; }
        const payload=publicKeys(JSON.parse(new TextDecoder('utf-8',{fatal:true}).decode(bytes)));
        active();
        if(lifetime>0 && clock()>=requestedAt+lifetime) throw unavailable();
        return {resolve:createLocalJWKSet(payload),expiresAt:requestedAt+lifetime,
          kids:new Set(payload.keys.map(key=>key.kid))};
      } finally { reader.releaseLock(); }
    }
    try { return await Promise.race([deadline,execute()]); }
    catch(error) {
      controller.abort();
      reader?.cancel().catch(()=>{});
      response?.body?.cancel().catch(()=>{});
      throw error;
    }
    finally { clearTimeout(timer); }
  }
  function refresh() {
    if(pending) return pending;
    if(clock()<failureUntil) return Promise.reject(unavailable());
    lastAttempt=clock();
    pending=load().then(result=>{cached=result;lastKnownKids=result.kids;failureUntil=-Infinity;return result;},()=>{
      if(cached && clock()>=cached.expiresAt) cached=undefined;
      failureUntil=clock()+cooldownMs;
      throw unavailable();
    }).finally(()=>{pending=undefined;});
    return pending;
  }
  return async function resolve(header,token) {
    if(!object(header) || header.alg!=='RS256' || !kid(header.kid)) throw new errors.JWSInvalid('Invalid signing-key header');
    // A rotated key can arrive in the current refresh. Join it before consulting
    // old negative metadata; still-fresh known keys need not wait for that I/O.
    if(pending && (!cached || !lastKnownKids?.has(header.kid) || clock()>=cached.expiresAt)) {
      await pending;
    } else {
      // Retain only IDs for negative filtering; expired keys are never reused.
      if(lastKnownKids && !lastKnownKids.has(header.kid) && clock()-lastAttempt<cooldownMs) {
        throw new errors.JWKSNoMatchingKey();
      }
      if(!cached || clock()>=cached.expiresAt) await refresh();
    }
    try { return await cached.resolve(header,token); }
    catch(error) {
      if(error.code!=='ERR_JWKS_NO_MATCHING_KEY') throw unavailable();
      if(clock()-lastAttempt<cooldownMs) throw error;
      await refresh();
      return cached.resolve(header,token);
    }
  };
}
