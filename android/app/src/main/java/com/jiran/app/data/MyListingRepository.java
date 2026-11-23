package com.jiran.app.data;

import com.jiran.app.domain.listing.ListingLifecycle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Host-only reads; writes succeed only after server acknowledgement. No private profiles are fetched. */
public interface MyListingRepository {
    final class Request {
        public final String requesterUid;
        public final String message;
        public final String status;
        public Request(String requesterUid, String message, String status) {
            if (requesterUid == null || requesterUid.isEmpty() || requesterUid.contains("/") || message == null
                    || !("pending".equals(status) || "accepted".equals(status)
                    || "rejected".equals(status) || "cancelled".equals(status))) {
                throw new IllegalArgumentException("Invalid request");
            }
            this.requesterUid = requesterUid;
            this.message = message;
            this.status = status;
        }
    }
    final class Listing {
        public final String id;
        public final String buildingName;
        public final String storedStatus;
        public final long expiresAt;
        public final List<RoomAllocation> rooms;
        public final List<Request> requests;
        public Listing(String id, String buildingName, String storedStatus, long expiresAt,
                List<RoomAllocation> rooms, List<Request> requests) {
            ListingLifecycle.status(storedStatus, expiresAt, 0, 0);
            this.id = id;
            this.buildingName = buildingName;
            this.storedStatus = storedStatus;
            this.expiresAt = expiresAt;
            this.rooms = Collections.unmodifiableList(new ArrayList<>(rooms));
            this.requests = Collections.unmodifiableList(new ArrayList<>(requests));
        }
        public int vacantRooms() {
            int count = 0;
            for (RoomAllocation room : rooms) { if (!room.occupied) { count++; } }
            return count;
        }
        public String status(long now) {
            return ListingLifecycle.status(storedStatus, expiresAt, now, vacantRooms());
        }
    }
    /** Null id selects the Host's most recently updated published listing; null result means none. */
    void load(String id, RoomRepository.Callback<Listing> callback);
    void renew(String id, RoomRepository.Callback<Void> callback);
    /** Rechecks current rooms, then closes only if none are vacant. Also reusable by E5.3. */
    void closeIfFull(String id, RoomRepository.Callback<Void> callback);
    void decide(String id, String requesterUid, String decision, RoomRepository.Callback<Void> callback);
}
