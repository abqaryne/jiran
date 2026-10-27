import {jwtVerify} from 'jose';
import {createFirebaseKeyResolver} from './firebase-keys.mjs';
import {HttpError} from './http-error.mjs';

export {HttpError} from './http-error.mjs';
const issuer='https://securetoken.google.com/demo-jiran';
// Public verification keys, never an account credential or a client-selected URL.
const keys=createFirebaseKeyResolver();
function denied() { throw new HttpError(401,'authentication-required'); }

/** Local feasibility verifier only: no revocation, UM or Firestore authorization. */
export async function verifyFirebaseToken(request) {
  const header=request.headers.get('authorization');
  const token=typeof header==='string' && /^Bearer ([A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+)$/.exec(header)?.[1];
  if (!token || token.length>8192) denied();
  let payload,protectedHeader;
  try {
    ({payload,protectedHeader}=await jwtVerify(token,keys,{algorithms:['RS256'],
      audience:'demo-jiran',issuer,requiredClaims:['exp','iat','auth_time','sub']}));
  } catch (error) {
    if (/^ERR_JW[ST]_/.test(error?.code??'')
        || ['ERR_JOSE_ALG_NOT_ALLOWED','ERR_JOSE_NOT_SUPPORTED','ERR_JWKS_NO_MATCHING_KEY'].includes(error?.code)) denied();
    throw new HttpError(503,'authentication-unavailable');
  }
  const now=Math.floor(Date.now()/1000);
  if (typeof protectedHeader.kid!=='string' || !protectedHeader.kid || protectedHeader.kid.length>128
      || payload.aud!=='demo-jiran' || typeof payload.sub!=='string' || !payload.sub || payload.sub.length>128
      || !['exp','iat','auth_time'].every(name=>Number.isSafeInteger(payload[name]) && payload[name]>=0)
      || payload.exp<=now || payload.iat>now || payload.auth_time>now
      || (payload.firebase!==undefined && (!payload.firebase || typeof payload.firebase!=='object'
        || Array.isArray(payload.firebase) || Object.hasOwn(payload.firebase,'tenant')))) denied();
  if (payload.email_verified!==true) throw new HttpError(403,'verified-email-required');
  return {uid:payload.sub,token:{email_verified:true}};
}
