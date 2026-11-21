// Disposable local fixture for RoomPricesPersistenceTest; never accepts a live target.
const assert = require('node:assert/strict');
assert.equal(process.env.FIREBASE_AUTH_EMULATOR_HOST, '127.0.0.1:9099');
assert.equal(process.env.FIRESTORE_EMULATOR_HOST, '127.0.0.1:8080');
const fromFunctions = require('node:module').createRequire(require.resolve('../functions/package.json'));
const {initializeApp, deleteApp} = fromFunctions('firebase-admin/app');
const {getAuth} = fromFunctions('firebase-admin/auth');
const {getFirestore, FieldValue} = fromFunctions('firebase-admin/firestore');

async function main() {
  const app = initializeApp({projectId: 'demo-jiran',
    credential: {getAccessToken: async () => ({access_token: 'owner', expires_in: 3600})}});
  try {
    const auth = getAuth(app);
    const user = {uid: 'room-price-fixture-host', email: 'room-price-fixture@siswa.um.edu.my',
      password: 'fictional-local-test-password', emailVerified: true};
    await auth.createUser(user).catch(async error => {
      if (error.code !== 'auth/uid-already-exists' && error.code !== 'auth/email-already-exists') throw error;
      await auth.updateUser(user.uid, user);
    });
    const db = getFirestore(app);
    const listingId = 'room-price-fixture';
    const existing = await db.collection('rooms').where('listingId', '==', listingId).get();
    const batch = db.batch();
    for (const doc of existing.docs) batch.delete(doc.ref);
    batch.set(db.doc(`listings/${listingId}`), {listingId, ownerUid: user.uid, areaId: 'fictional-area',
      buildingName: 'Fictional Rooms Test Building', buildingAddress: 'Fictional street',
      lat: 0, lng: 0, totalRentSen: 150000, depositSen: 0, genderPref: 'any', amenities: [], photoUrls: [],
      isSample: true, status: 'draft', coordinateSource: 'area_center_estimate',
      createdAt: FieldValue.serverTimestamp(), updatedAt: FieldValue.serverTimestamp()});
    batch.set(db.doc('rooms/room-price-existing'), {roomId: 'room-price-existing', listingId,
      type: 'medium', priceSen: 50000, status: 'occupied', occupiedByUid: 'fictional-resident',
      createdAt: FieldValue.serverTimestamp(), updatedAt: FieldValue.serverTimestamp()});
    batch.set(db.doc('config/app'), {roomPriceWeights: {master: 1.3, medium: 1, small: 0.7}}, {merge: true});
    await batch.commit();
    console.log('Disposable demo-jiran Room prices fixture ready. No live project was contacted.');
  } finally {
    await deleteApp(app);
  }
}
main().catch(error => { console.error(error.message); process.exitCode = 1; });
