const {onCall, HttpsError} = require('firebase-functions/v2/https');
const {initializeApp} = require('firebase-admin/app');
const {getFirestore, FieldValue} = require('firebase-admin/firestore');
const {session} = require('./src/session');
const {isLocalEnvironment} = require('./src/environment');
const {commutePreview} = require('./src/commute-preview');
const {matchPreview} = require('./src/match-preview');

// This developer diagnostic must not be exported into a deployed project.
if (isLocalEnvironment(process.env)) {
  initializeApp();
  exports.computeMatchScores = onCall({region: 'us-central1'}, (request) => {
    try {
      return matchPreview(request);
    } catch (error) {
      const expected = ['invalid-argument', 'unauthenticated', 'permission-denied'].includes(error.code);
      throw new HttpsError(expected ? error.code : 'internal',
        expected ? error.message : 'Match calculation unavailable');
    }
  });
  exports.getCommuteOptions = onCall({region: 'us-central1'}, (request) => {
    try {
      return commutePreview(request);
    } catch (error) {
      const expected = ['invalid-argument', 'unauthenticated', 'permission-denied'].includes(error.code);
      throw new HttpsError(expected ? error.code : 'internal',
        expected ? error.message : 'Commute calculation unavailable');
    }
  });
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
