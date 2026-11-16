package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.jiran.app.data.FirestoreRoomRepository;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Run only with the disposable room-prices-fixture.cjs on demo-jiran Auth/Firestore emulators. */
@RunWith(AndroidJUnit4.class)
public final class RoomPricesPersistenceTest {
    private static final String LISTING = "room-price-fixture";

    @Test public void persistsAllRoomsRejectsOverAllocationAndDetectsStaleEditors() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        assumeTrue("true".equals(InstrumentationRegistry.getArguments().getString("roomPricesEmulator")));
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().grantRuntimePermission(
                    InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName(),
                    "android.permission.ACCESS_LOCAL_NETWORK");
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Tasks.await(auth.signInWithEmailAndPassword("room-price-fixture@siswa.um.edu.my",
                "fictional-local-test-password"), 30, TimeUnit.SECONDS);
        try {
            RoomRepository first = new FirestoreRoomRepository();
            RoomRepository.Loaded loaded = load(first);
            assertEquals(150000, loaded.totalRentSen);
            assertEquals(1, loaded.rooms.size());
            List<RoomAllocation> input = new ArrayList<>(loaded.rooms);
            input.add(new RoomAllocation("room-price-new-master", "master", 65000, false, null));
            input.add(new RoomAllocation("room-price-new-small", "small", 35000, false, null));
            assertEquals("ok", save(first, input));
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentSnapshot medium = Tasks.await(db.document("rooms/room-price-existing").get(Source.SERVER),
                    30, TimeUnit.SECONDS);
            assertEquals("occupied", medium.getString("status"));
            assertEquals("fictional-resident", medium.getString("occupiedByUid"));
            assertEquals(Long.valueOf(50000), medium.getLong("priceSen"));
            assertNotNull(medium.getTimestamp("createdAt"));
            assertNotNull(medium.getTimestamp("updatedAt"));
            assertFalse(medium.contains("utilitiesSen"));
            assertEquals(3, load(new FirestoreRoomRepository()).rooms.size());

            RoomRepository stale = new FirestoreRoomRepository();
            load(stale);
            load(first);
            List<RoomAllocation> excess = new ArrayList<>(input);
            excess.set(1, new RoomAllocation("room-price-new-master", "master", 65001, false, null));
            assertEquals("INVALID", save(first, excess));
            DocumentSnapshot unchanged = Tasks.await(db.document("rooms/room-price-new-master").get(Source.SERVER),
                    30, TimeUnit.SECONDS);
            assertEquals(Long.valueOf(65000), unchanged.getLong("priceSen"));
            List<RoomAllocation> edited = new ArrayList<>(input);
            edited.set(0, new RoomAllocation("room-price-existing", "medium", 49999, false, null));
            assertEquals("ok", save(first, edited));
            assertEquals("CONFLICT", save(stale, input));
            DocumentSnapshot vacant = Tasks.await(db.document("rooms/room-price-existing").get(Source.SERVER),
                    30, TimeUnit.SECONDS);
            assertEquals("vacant", vacant.getString("status"));
            assertFalse(vacant.contains("occupiedByUid"));
            assertEquals(3, load(new FirestoreRoomRepository()).rooms.size());
            auth.signOut();
            assertEquals("AUTH", save(first, input));
        } finally {
            auth.signOut();
        }
    }

    private static RoomRepository.Loaded load(RoomRepository repository) throws Exception {
        CompletableFuture<RoomRepository.Loaded> result = new CompletableFuture<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> repository.load(LISTING,
                new RoomRepository.Callback<RoomRepository.Loaded>() {
                    @Override public void success(RoomRepository.Loaded value) { result.complete(value); }
                    @Override public void failure(RoomRepository.Problem problem) {
                        result.completeExceptionally(new AssertionError(problem));
                    }
                }));
        RoomRepository.Loaded loaded = result.get(30, TimeUnit.SECONDS);
        assertTrue(loaded.totalRentSen > 0);
        return loaded;
    }

    private static String save(RoomRepository repository, List<RoomAllocation> rooms) throws Exception {
        CompletableFuture<String> result = new CompletableFuture<>();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> repository.save(LISTING, rooms,
                new RoomRepository.Callback<Void>() {
                    @Override public void success(Void ignored) { result.complete("ok"); }
                    @Override public void failure(RoomRepository.Problem problem) { result.complete(problem.name()); }
                }));
        return result.get(30, TimeUnit.SECONDS);
    }
}
