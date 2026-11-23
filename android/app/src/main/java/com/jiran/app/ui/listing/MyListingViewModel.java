package com.jiran.app.ui.listing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.MyListingRepository;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.domain.listing.ListingLifecycle;
import java.util.function.LongSupplier;

/** FR2.1: no optimistic writes; a failed renewal keeps the previous expiry visible. */
public final class MyListingViewModel extends ViewModel {
    public enum State { LOADING, READY, SAVING, EMPTY, LOAD_ERROR, SAVE_ERROR, AUTH }
    private final MyListingRepository repository;
    private final LongSupplier clock;
    private final MutableLiveData<Integer> changes = new MutableLiveData<>(0);
    private String listingId;
    private MyListingRepository.Listing listing;
    private State state = State.LOADING;
    private RoomRepository.Problem problem;
    private int generation;
    private boolean ended;

    public MyListingViewModel(String listingId, MyListingRepository repository, LongSupplier clock) {
        this.listingId = listingId;
        this.repository = repository;
        this.clock = clock;
        reload();
    }
    public LiveData<Integer> changes() { return changes; }
    public State state() { return state; }
    public RoomRepository.Problem problem() { return problem; }
    public MyListingRepository.Listing listing() { return listing; }
    public String status() { return listing == null ? "" : listing.status(clock.getAsLong()); }
    public long daysRemaining() {
        return listing == null || !"active".equals(status()) ? 0
                : ListingLifecycle.daysRemaining(listing.expiresAt, clock.getAsLong());
    }
    public boolean canAct() { return !ended && listing != null && state == State.READY; }
    public boolean canRenew() { return canAct() && listing.vacantRooms() > 0; }
    public void tick() { if (!ended) { changed(); } }

    public void reload() {
        if (ended || state == State.SAVING) { return; }
        int token = ++generation;
        state = State.LOADING;
        problem = null;
        listing = null;
        changed();
        repository.load(listingId, new RoomRepository.Callback<MyListingRepository.Listing>() {
            @Override public void success(MyListingRepository.Listing value) {
                if (ended || token != generation) { return; }
                listing = value;
                state = value == null ? State.EMPTY : State.READY;
                if (value != null) { listingId = value.id; }
                changed();
                if (value != null && value.vacantRooms() == 0 && !"closed".equals(value.storedStatus)) {
                    state = State.SAVING;
                    changed();
                    repository.closeIfFull(value.id, new RoomRepository.Callback<Void>() {
                        @Override public void success(Void ignored) {
                            if (ended || token != generation) { return; }
                            // Re-read after reconciliation: rooms may have changed since the initial load.
                            state = State.READY;
                            reload();
                        }
                        @Override public void failure(RoomRepository.Problem error) { fail(token, error, State.SAVE_ERROR); }
                    });
                }
            }
            @Override public void failure(RoomRepository.Problem error) { fail(token, error, State.LOAD_ERROR); }
        });
    }
    public void renew() {
        if (!canRenew()) { return; }
        state = State.SAVING;
        changed();
        repository.renew(listingId, mutationCallback());
    }
    public void decide(String requesterUid, String decision) {
        if (!canAct()) { return; }
        for (MyListingRepository.Request request : listing.requests) {
            if (request.requesterUid.equals(requesterUid)) {
                try { ListingLifecycle.decide(request.status, decision); }
                catch (IllegalArgumentException error) { return; }
                state = State.SAVING;
                changed();
                repository.decide(listingId, requesterUid, decision, mutationCallback());
                return;
            }
        }
    }
    private RoomRepository.Callback<Void> mutationCallback() {
        int token = generation;
        return new RoomRepository.Callback<Void>() {
            @Override public void success(Void ignored) {
                if (ended || token != generation) { return; }
                state = State.READY;
                reload();
            }
            @Override public void failure(RoomRepository.Problem error) { fail(token, error, State.SAVE_ERROR); }
        };
    }
    private void fail(int token, RoomRepository.Problem error, State next) {
        if (ended || token != generation) { return; }
        if (error == RoomRepository.Problem.AUTH) {
            endSession();
            return;
        }
        problem = error;
        state = next;
        changed();
    }
    private void changed() { changes.setValue(changes.getValue() + 1); }
    public void endSession() {
        ended = true;
        generation++;
        listing = null;
        state = State.AUTH;
        changed();
    }
    @Override protected void onCleared() { endSession(); }
}
