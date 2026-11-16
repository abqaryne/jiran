package com.jiran.app.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Source;
import com.jiran.app.domain.auth.VerifiedHostPolicy;
import com.jiran.app.domain.listing.RoomPriceRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Draft editor. Atomic writes and stale-edit detection; Security Rules remain the authorization boundary. */
public final class FirestoreRoomRepository implements RoomRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String ownerUid;
    private String loadedId;
    private Map<String, Object> listingBaseline;
    private Map<String, Map<String, Object>> roomBaseline = Collections.emptyMap();

    public FirestoreRoomRepository() {
        FirebaseUser user = auth.getCurrentUser();
        ownerUid = user == null ? "" : user.getUid();
    }

    private boolean sameHost() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && VerifiedHostPolicy.permits(ownerUid, user.getUid(), user.getEmail(),
                user.isEmailVerified());
    }

    @Override public void load(String listingId, Callback<Loaded> callback) {
        loadedId = null;
        if (!sameHost()) {
            callback.failure(Problem.AUTH);
            return;
        }
        if (listingId == null || !listingId.matches("[A-Za-z0-9_-]{1,128}")) {
            callback.failure(Problem.INVALID);
            return;
        }
        db.collection("listings").document(listingId).get(Source.SERVER).addOnSuccessListener(listing -> {
            if (!sameHost() || !ownerUid.equals(listing.getString("ownerUid"))) {
                callback.failure(Problem.AUTH);
                return;
            }
            if (!"draft".equals(listing.getString("status")) || !(listing.get("totalRentSen") instanceof Long)
                    || listing.getLong("totalRentSen") <= 0) {
                callback.failure(Problem.INVALID);
                return;
            }
            db.collection("rooms").whereEqualTo("listingId", listingId).limit(MAX_ROOMS + 1L)
                    .get(Source.SERVER).addOnSuccessListener(result -> {
                        if (!sameHost()) {
                            callback.failure(Problem.AUTH);
                            return;
                        }
                        try {
                            if (result.size() > MAX_ROOMS) {
                                throw new IllegalArgumentException("Room limit");
                            }
                            List<RoomAllocation> rooms = new ArrayList<>();
                            Map<String, Map<String, Object>> baseline = new HashMap<>();
                            for (DocumentSnapshot doc : result.getDocuments()) {
                                rooms.add(RoomAllocation.fromFields(doc.getId(), doc.getData()));
                                baseline.put(doc.getId(), doc.getData());
                            }
                            listingBaseline = listing.getData();
                            roomBaseline = baseline;
                            loadedId = listingId;
                            callback.success(new Loaded(listing.getLong("totalRentSen"), rooms));
                        } catch (IllegalArgumentException | ClassCastException error) {
                            callback.failure(Problem.INVALID);
                        }
                    }).addOnFailureListener(error -> callback.failure(problem(error)));
        }).addOnFailureListener(error -> callback.failure(problem(error)));
    }

    @Override public Subscription observeConfig(Callback<Map<String, ?>> callback) {
        ListenerRegistration registration = db.collection("config").document("app")
                .addSnapshotListener((snapshot, error) -> {
                    if (!sameHost()) {
                        callback.failure(Problem.AUTH);
                    } else if (error != null) {
                        callback.failure(problem(error));
                    } else if (snapshot != null) {
                        callback.success(snapshot.getData() == null ? Collections.emptyMap() : snapshot.getData());
                    }
                });
        return registration::remove;
    }

    @Override public void save(String listingId, List<RoomAllocation> rooms, Callback<Void> callback) {
        if (!sameHost()) {
            callback.failure(Problem.AUTH);
            return;
        }
        if (!Objects.equals(loadedId, listingId) || listingBaseline == null || rooms.isEmpty()
                || rooms.size() > MAX_ROOMS) {
            callback.failure(Problem.INVALID);
            return;
        }
        List<RoomAllocation> input = new ArrayList<>(rooms);
        Set<String> ids = new HashSet<>();
        List<Long> prices = new ArrayList<>();
        for (RoomAllocation room : input) {
            if (!ids.add(room.id)) {
                callback.failure(Problem.INVALID);
                return;
            }
            prices.add(room.priceSen);
        }
        // Existing rooms cannot silently disappear from the total; removing saved rooms is E2.3 work.
        if (!ids.containsAll(roomBaseline.keySet())) {
            callback.failure(Problem.INVALID);
            return;
        }
        Map<String, Object> expectedListing = new HashMap<>(listingBaseline);
        Map<String, Map<String, Object>> expectedRooms = new HashMap<>(roomBaseline);
        db.runTransaction(transaction -> {
            if (!sameHost()) {
                throw new FirebaseFirestoreException("Session changed", FirebaseFirestoreException.Code.PERMISSION_DENIED);
            }
            DocumentSnapshot listing = transaction.get(db.collection("listings").document(listingId));
            if (!expectedListing.equals(listing.getData())) {
                throw new FirebaseFirestoreException("Reload draft", FirebaseFirestoreException.Code.ABORTED);
            }
            RoomPriceRule.remainingSen(listing.getLong("totalRentSen"), prices);
            // Read all documents before writing. A retry with the same IDs cannot duplicate rooms.
            for (RoomAllocation room : input) {
                DocumentSnapshot stored = transaction.get(db.collection("rooms").document(room.id));
                if (!Objects.equals(expectedRooms.get(room.id), stored.getData())) {
                    throw new FirebaseFirestoreException("Reload rooms", FirebaseFirestoreException.Code.ABORTED);
                }
            }
            for (RoomAllocation room : input) {
                Map<String, Object> data = room.fields(listingId);
                data.put("updatedAt", FieldValue.serverTimestamp());
                if (expectedRooms.containsKey(room.id)) {
                    if (room.occupiedByUid == null) {
                        data.put("occupiedByUid", FieldValue.delete());
                    }
                    transaction.update(db.collection("rooms").document(room.id), data);
                } else {
                    data.put("createdAt", FieldValue.serverTimestamp());
                    transaction.set(db.collection("rooms").document(room.id), data);
                }
            }
            // All editors in this flow touch the parent to serialize concurrent additions.
            transaction.update(listing.getReference(), "updatedAt", FieldValue.serverTimestamp());
            return null;
        }).addOnSuccessListener(ignored -> {
            loadedId = null;
            if (sameHost()) {
                callback.success(null);
            } else {
                callback.failure(Problem.AUTH);
            }
        }).addOnFailureListener(error -> callback.failure(problem(error)));
    }

    private static Problem problem(Exception error) {
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException.Code code = ((FirebaseFirestoreException) error).getCode();
            if (code == FirebaseFirestoreException.Code.PERMISSION_DENIED
                    || code == FirebaseFirestoreException.Code.UNAUTHENTICATED) {
                return Problem.AUTH;
            }
            if (code == FirebaseFirestoreException.Code.ABORTED) {
                return Problem.CONFLICT;
            }
            if (code == FirebaseFirestoreException.Code.UNAVAILABLE
                    || code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED) {
                return Problem.NETWORK;
            }
        }
        return Problem.INVALID;
    }
}
