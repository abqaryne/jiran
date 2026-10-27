function reject(code, message) {
  const error = new Error(message);
  error.code = code;
  throw error;
}

function session(request) {
  if (!request?.auth?.uid) {
    reject('unauthenticated', 'Authentication required');
  }
  if (request.auth.token?.email_verified !== true) {
    reject('permission-denied', 'Verified email required');
  }
  return {uid: request.auth.uid, status: 'ok'};
}

module.exports = {session};
