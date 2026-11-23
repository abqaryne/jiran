package com.jiran.app.ui.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.MyListingRepository;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;

public final class MyListingViewModelTest {
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
    private long now = 1000;
    private final Fake repository = new Fake();
    private MyListingViewModel model() { return new MyListingViewModel("listing", repository, () -> now); }
    private MyListingRepository.Listing listing(String status, long expiry, boolean occupied, String requestStatus) {
        return new MyListingRepository.Listing("listing", "Sample", status, expiry,
                Collections.singletonList(new RoomAllocation("room", "small", 35000, occupied, null)),
                Collections.singletonList(new MyListingRepository.Request("student", "Sample message", requestStatus)));
    }
    @Test public void expiryUpdatesWhileScreenStaysOpenWithoutWriting() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 1001, false, "pending"));
        assertEquals("active", model.status());
        assertEquals(1, model.daysRemaining());
        now = 1001;
        model.tick();
        assertEquals("expired", model.status());
        assertEquals(0, model.daysRemaining());
        assertTrue(model.canRenew());
        assertEquals(0, repository.writes);
    }
    @Test public void renewalWaitsForAcknowledgementAndReloadsServerData() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 999, false, "pending"));
        model.renew();
        model.renew();
        assertEquals(1, repository.writes);
        assertEquals(MyListingViewModel.State.SAVING, model.state());
        assertEquals("expired", model.status());
        repository.write.success(null);
        assertEquals(MyListingViewModel.State.LOADING, model.state());
        repository.load.success(listing("active", now + 2592000000L, false, "pending"));
        assertEquals("active", model.status());
        assertEquals(30, model.daysRemaining());
    }
    @Test public void failedRenewalPreservesExpiredStateAndRequiresRefresh() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 999, false, "pending"));
        model.renew();
        repository.write.failure(RoomRepository.Problem.INVALID);
        assertEquals(MyListingViewModel.State.SAVE_ERROR, model.state());
        assertEquals("expired", model.status());
        assertFalse(model.canRenew());
        model.reload();
        repository.load.success(listing("active", 999, false, "pending"));
        assertTrue(model.canRenew());
    }
    @Test public void fullListingIsClosedOnReadAndPersistenceFailureIsVisible() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 2000, true, "pending"));
        assertEquals("closed", model.status());
        assertEquals(1, repository.closes);
        model.renew();
        assertEquals(1, repository.writes);
        repository.write.failure(RoomRepository.Problem.INVALID);
        assertEquals(MyListingViewModel.State.SAVE_ERROR, model.state());
        assertEquals("closed", model.status());
        assertFalse(model.canRenew());
    }
    @Test public void successfulCloseReloadsAndDoesNotCloseAgain() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 2000, true, "pending"));
        repository.write.success(null);
        repository.load.success(listing("closed", 2000, true, "pending"));
        assertEquals(MyListingViewModel.State.READY, model.state());
        assertEquals(1, repository.closes);
    }
    @Test public void acceptAndRejectReloadAndNeverChangeRoomOccupancy() {
        for (String decision : new String[]{"accepted", "rejected"}) {
            MyListingViewModel model = model();
            repository.load.success(listing("active", 2000, false, "pending"));
            model.decide("student", decision);
            assertEquals(decision, repository.decision);
            assertEquals("pending", model.listing().requests.get(0).status);
            repository.write.success(null);
            repository.load.success(listing("active", 2000, false, decision));
            assertEquals(decision, model.listing().requests.get(0).status);
            assertFalse(model.listing().rooms.get(0).occupied);
        }
    }
    @Test public void cancelledTerminalAndUnknownRequestsCannotBeDecided() {
        MyListingViewModel model = model();
        for (String status : new String[]{"cancelled", "accepted", "rejected"}) {
            model.reload();
            repository.load.success(listing("active", 2000, false, status));
            model.decide("student", "accepted");
            model.decide("student", "rejected");
            model.decide("unknown", "accepted");
        }
        assertEquals(0, repository.writes);
    }
    @Test public void conflictPreservesOldRequestUntilReload() {
        MyListingViewModel model = model();
        repository.load.success(listing("active", 2000, false, "pending"));
        model.decide("student", "accepted");
        repository.write.failure(RoomRepository.Problem.CONFLICT);
        assertEquals(MyListingViewModel.State.SAVE_ERROR, model.state());
        assertEquals(RoomRepository.Problem.CONFLICT, model.problem());
        assertEquals("pending", model.listing().requests.get(0).status);
    }
    @Test public void signOutClearsDataAndIgnoresLateCallbacks() {
        MyListingViewModel model = model();
        RoomRepository.Callback<MyListingRepository.Listing> pending = repository.load;
        model.endSession();
        pending.success(listing("active", 2000, false, "pending"));
        assertNull(model.listing());
        assertEquals(MyListingViewModel.State.AUTH, model.state());
        model.renew();
        assertEquals(0, repository.writes);
    }
    @Test public void staleLoadCannotOverwriteNewerLoadAndEmptyStateIsSafe() {
        MyListingViewModel model = model();
        RoomRepository.Callback<MyListingRepository.Listing> old = repository.load;
        model.reload();
        repository.load.success(null);
        old.success(listing("active", 2000, false, "pending"));
        assertNull(model.listing());
        assertEquals(MyListingViewModel.State.EMPTY, model.state());
        assertFalse(model.canRenew());
    }
    private static final class Fake implements MyListingRepository {
        private RoomRepository.Callback<Listing> load;
        private RoomRepository.Callback<Void> write;
        private int writes;
        private int closes;
        private String decision;
        @Override public void load(String id, RoomRepository.Callback<Listing> callback) { load = callback; }
        @Override public void renew(String id, RoomRepository.Callback<Void> callback) { writes++;
            write = callback; }
        @Override public void closeIfFull(String id, RoomRepository.Callback<Void> callback) {
            writes++;
            closes++;
            write = callback;
        }
        @Override public void decide(String id, String requester, String status, RoomRepository.Callback<Void> callback) {
            writes++;
            decision = status;
            write = callback;
        }
    }
}
