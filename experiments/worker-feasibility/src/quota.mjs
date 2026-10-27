import {HttpError} from './http-error.mjs';

const DAY=86400000,MINUTE=60000;
const object=value=>value!==null && typeof value==='object' && !Array.isArray(value);
const exact=(value,keys)=>object(value) && Object.keys(value).length===keys.length
  && keys.every(key=>Object.hasOwn(value,key));
function unavailable() { return new HttpError(503,'quota-unavailable'); }

/** Reservation primitive for one account's SQLite Durable Object, not a public API.
 * Caller supplies a hash of verified identity, trusted limits and server time.
 * No refunds/retries: uncertain outcomes remain conservatively charged.
 */
export function createQuotaStore(storage,limits) {
  if(!exact(limits,['accountMinute','accountDay','userDay'])
      || !Object.values(limits).every(value=>Number.isSafeInteger(value) && value>0)
      || limits.accountMinute>40 || limits.accountDay>2000 || limits.userDay>limits.accountDay) {
    throw unavailable();
  }
  const policy={...limits};
  try {
    storage.sql.exec(`
      CREATE TABLE IF NOT EXISTS quota_events (
        id INTEGER PRIMARY KEY, user_key TEXT NOT NULL,
        at INTEGER NOT NULL CHECK(at>=0), units INTEGER NOT NULL CHECK(units>0));
      CREATE INDEX IF NOT EXISTS quota_events_at ON quota_events(at);
      CREATE INDEX IF NOT EXISTS quota_events_user_at ON quota_events(user_key,at);
      CREATE TABLE IF NOT EXISTS quota_clock (
        id INTEGER PRIMARY KEY CHECK(id=1), last_at INTEGER NOT NULL);
      INSERT OR IGNORE INTO quota_clock(id,last_at) VALUES(1,-1);
    `);
  } catch { throw unavailable(); }
  return {reserve(data,at=Date.now()) {
    if(!exact(data,['userKey','units']) || typeof data.userKey!=='string'
        || !/^[a-f0-9]{64}$/.test(data.userKey) || !Number.isInteger(data.units)
        || data.units<1 || data.units>40) throw new HttpError(400,'invalid-reservation');
    if(!Number.isSafeInteger(at) || at<0 || at>Number.MAX_SAFE_INTEGER-DAY) throw unavailable();
    try {
      return storage.transactionSync(()=>{
        const sql=storage.sql;
        const previous=sql.exec('SELECT last_at FROM quota_clock WHERE id=1').one().last_at;
        if(at<previous) throw unavailable();
        const userUsed=sql.exec('SELECT COALESCE(SUM(units),0) AS used FROM quota_events WHERE user_key=? AND at>?',
          data.userKey,at-DAY).one().used;
        if(userUsed+data.units>policy.userDay) return {reserved:false,reason:'user-day'};
        const dailyUsed=sql.exec('SELECT COALESCE(SUM(units),0) AS used FROM quota_events WHERE at>?',at-DAY).one().used;
        if(dailyUsed+data.units>policy.accountDay) return {reserved:false,reason:'account-day'};
        const minuteUsed=sql.exec('SELECT COALESCE(SUM(units),0) AS used FROM quota_events WHERE at>?',at-MINUTE).one().used;
        if(minuteUsed+data.units>policy.accountMinute) return {reserved:false,reason:'account-minute'};
        sql.exec('DELETE FROM quota_events WHERE at<=?',at-DAY);
        sql.exec('INSERT INTO quota_events(user_key,at,units) VALUES(?,?,?)',data.userKey,at,data.units);
        sql.exec('UPDATE quota_clock SET last_at=? WHERE id=1',at);
        return {reserved:true};
      });
    } catch { throw unavailable(); }
  }};
}
