package com.jiran.app.data;

import java.util.List;
import java.util.Map;

/** Callbacks are delivered on the main thread. A save succeeds only after server acknowledgement. */
public interface RoomRepository {
    // Operational transaction bound, not a claim about how many rooms UM units have.
    int MAX_ROOMS = 100;
    enum Problem { AUTH, NETWORK, CONFLICT, INVALID }
    interface Callback<T> {
        void success(T value);
        void failure(Problem problem);
    }
    final class Loaded {
        public final long totalRentSen;
        public final List<RoomAllocation> rooms;
        public Loaded(long totalRentSen, List<RoomAllocation> rooms) {
            this.totalRentSen = totalRentSen;
            this.rooms = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(rooms));
        }
    }
    interface Subscription {
        void remove();
    }
    void load(String listingId, Callback<Loaded> callback);
    Subscription observeConfig(Callback<Map<String, ?>> callback);
    void save(String listingId, List<RoomAllocation> rooms, Callback<Void> callback);
}
