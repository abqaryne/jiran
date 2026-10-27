const {onCall, HttpsError} = require('firebase-functions/v2/https');
const {initializeApp} = require('firebase-admin/app');
const {getFirestore, FieldValue} = require('firebase-admin/firestore');
const {session} = require('./src/session');
const {isLocalEnvironment} = require('./src/environment');

// This developer diagnostic must not be exported into a deployed project.
if (isLocalEnvironment(process.env)) {
  initializeApp();
  exports.sessionCheck = onCall({region: 'us-central1'}, async (request) => {
    let result;
    try {
      result = session(request);
    } catch (error) {
      throw new HttpsError(error.code, error.message);
    }
    await getFirestore().collection('_diagnostics').doc(result.uid).set({
      checkedAt: FieldValue.serverTimestamp(),
    });
    return result;
  });
}
