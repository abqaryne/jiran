package com.jiran.app.data;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;
import com.jiran.app.domain.auth.VerifiedHostPolicy;
import com.jiran.app.domain.listing.ListingLifecycle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.LongSupplier;

/** E2.3 client lifecycle. Current rules intentionally still deny renewal/closure until Subtask 3. */
public final class FirestoreMyListingRepository implements MyListingRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String ownerUid;
    private final LongSupplier clock;

    public FirestoreMyListingRepository() { this(System::currentTimeMillis); }
    public FirestoreMyListingRepository(LongSupplier clock) {
        FirebaseUser user = auth.getCurrentUser();
        ownerUid = user == null ? "" : user.getUid();
        this.clock = clock;
    }

    private boolean sameHost() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && VerifiedHostPolicy.permits(ownerUid, user.getUid(), user.getEmail(),
                user.isEmailVerified());
    }

    private boolean ready(String id, RoomRepository.Callback<?> callback) {
        if (!sameHost()) {
            callback.failure(RoomRepository.Problem.AUTH);
            return false;
        }
        if (id != null && !id.matches("[A-Za-z0-9_-]{1,128}")) {
            callback.failure(RoomRepository.Problem.INVALID);
            return false;
        }
        return true;
    }

    @Override public void load(String id, RoomRepository.Callback<Listing> callback) {
        if (!ready(id, callback)) { return; }
        if (id == null) {
            // Owner constraint is required by Rules; no private location/contact reads.
            db.collection("listings").whereEqualTo("ownerUid", ownerUid).get(Source.SERVER)
                    .addOnSuccessListener(result -> {
                        if (!sameHost()) {
                            callback.failure(RoomRepository.Problem.AUTH);
                            return;
                        }
                        DocumentSnapshot latest = null;
                        for (DocumentSnapshot doc : result.getDocuments()) {
                            if ("draft".equals(doc.getString("status"))) { continue; }
                            if (latest == null || updated(doc) > updated(latest)) { latest = doc; }
                        }
                        if (latest == null) {
                            callback.success(null);
                        } else {
                            load(latest.getId(), callback);
                        }
                    }).addOnFailureListener(error -> fail(error, callback));
            return;
        }
        db.collection("listings").document(id).get(Source.SERVER).addOnSuccessListener(listing -> {
            if (!sameHost() || !ownerUid.equals(listing.getString("ownerUid"))) {
                callback.failure(RoomRepository.Problem.AUTH);
                return;
            }
            db.collection("rooms").whereEqualTo("listingId", id).limit(RoomRepository.MAX_ROOMS + 1L)
                    .get(Source.SERVER).addOnSuccessListener(rooms -> {
                        db.collection("listingRequests").whereEqualTo("hostUid", ownerUid)
                                .whereEqualTo("listingId", id).get(Source.SERVER).addOnSuccessListener(requests -> {
                                    if (!sameHost()) {
                                        callback.failure(RoomRepository.Problem.AUTH);
                                        return;
                                    }
                                    try {
                                        if (rooms.size() > RoomRepository.MAX_ROOMS) {
                                            throw new IllegalArgumentException("Room limit");
                                        }
                                        List<RoomAllocation> rows = new ArrayList<>();
                                        for (DocumentSnapshot room : rooms) {
                                            rows.add(RoomAllocation.fromFields(room.getId(), room.getData()));
                                        }
                                        List<Request> incoming = new ArrayList<>();
                                        for (DocumentSnapshot request : requests) {
                                            incoming.add(readRequest(id, request));
                                        }
                                        Timestamp expires = listing.getTimestamp("expiresAt");
                                        if (expires == null) { throw new IllegalArgumentException("Missing expiry"); }
                                        callback.success(new Listing(id, listing.getString("buildingName"),
                                                listing.getString("status"), expires.toDate().getTime(), rows, incoming));
                                    } catch (IllegalArgumentException | ClassCastException error) {
                                        callback.failure(RoomRepository.Problem.INVALID);
                                    }
                                }).addOnFailureListener(error -> fail(error, callback));
                    }).addOnFailureListener(error -> fail(error, callback));
        }).addOnFailureListener(error -> fail(error, callback));
    }

    private static long updated(DocumentSnapshot doc) {
        Timestamp value = doc.getTimestamp("updatedAt");
        return value == null ? 0 : value.toDate().getTime();
    }

    private Request readRequest(String id, DocumentSnapshot doc) {
        String requester = doc.getString("requesterUid");
        if (!doc.getId().equals(id + "_" + requester) || !id.equals(doc.getString("listingId"))
                || !ownerUid.equals(doc.getString("hostUid")) || ownerUid.equals(requester)) {
            throw new IllegalArgumentException("Invalid request identity");
        }
        return new Request(requester, doc.getString("message"), doc.getString("status"));
    }

    @Override public void renew(String id, RoomRepository.Callback<Void> callback) {
        lifecycle(id, true, callback);
    }

    @Override public void closeIfFull(String id, RoomRepository.Callback<Void> callback) {
        lifecycle(id, false, callback);
    }

    private void lifecycle(String id, boolean renew, RoomRepository.Callback<Void> callback) {
        if (id == null || !ready(id, callback)) { return; }
        // Android transactions cannot query. Capture the parent before enumerating room IDs, then
        // verify it and re-read every room in the transaction. Room-set writers must touch the parent
        // (as E2.2 does); E5.3 must preserve that protocol for atomic last-room closure.
        db.collection("listings").document(id).get(Source.SERVER).addOnSuccessListener(baseline -> {
            db.collection("rooms").whereEqualTo("listingId", id).limit(RoomRepository.MAX_ROOMS + 1L)
                    .get(Source.SERVER).addOnSuccessListener(rooms -> {
                        if (rooms.size() > RoomRepository.MAX_ROOMS) {
                            callback.failure(RoomRepository.Problem.INVALID);
                            return;
                        }
                        db.runTransaction(tx -> {
                            DocumentSnapshot listing = tx.get(baseline.getReference());
                            requireHost(listing);
                            if (!Objects.equals(baseline.getData(), listing.getData())) {
                                throw conflict();
                            }
                            int vacant = 0;
                            for (DocumentSnapshot ref : rooms) {
                                DocumentSnapshot room = tx.get(ref.getReference());
                                if (!id.equals(room.getString("listingId"))) { throw conflict(); }
                                if (!RoomAllocation.fromFields(room.getId(), room.getData()).occupied) { vacant++; }
                            }
                            Timestamp expires = listing.getTimestamp("expiresAt");
                            if (expires == null) { throw new IllegalArgumentException("Published listing required"); }
                            ListingLifecycle.status(listing.getString("status"), expires.toDate().getTime(),
                                    clock.getAsLong(), vacant);
                            if (vacant == 0) {
                                if (!"closed".equals(listing.getString("status"))) {
                                    tx.update(listing.getReference(), "status", "closed",
                                            "updatedAt", FieldValue.serverTimestamp());
                                }
                            } else if (renew) {
                                tx.update(listing.getReference(), "status", "active", "expiresAt",
                                        new Timestamp(new Date(ListingLifecycle.renew(clock.getAsLong(), vacant))),
                                        "updatedAt", FieldValue.serverTimestamp());
                            }
                            return null;
                        }).addOnSuccessListener(ignored -> complete(callback))
                                .addOnFailureListener(error -> fail(error, callback));
                    }).addOnFailureListener(error -> fail(error, callback));
        }).addOnFailureListener(error -> fail(error, callback));
    }

    @Override public void decide(String id, String requesterUid, String decision, RoomRepository.Callback<Void> callback) {
        if (id == null || !ready(id, callback)) { return; }
        try {
            new Request(requesterUid, "", "pending");
            ListingLifecycle.decide("pending", decision);
        } catch (IllegalArgumentException error) {
            callback.failure(RoomRepository.Problem.INVALID);
            return;
        }
        db.runTransaction(tx -> {
            requireHost(tx.get(db.collection("listings").document(id)));
            DocumentSnapshot request = tx.get(db.collection("listingRequests").document(id + "_" + requesterUid));
            Request current = readRequest(id, request);
            if (!"pending".equals(current.status)) { throw conflict(); }
            tx.update(request.getReference(), "status", ListingLifecycle.decide(current.status, decision),
                    "updatedAt", FieldValue.serverTimestamp());
            return null;
        }).addOnSuccessListener(ignored -> complete(callback)).addOnFailureListener(error -> fail(error, callback));
    }

    private void requireHost(DocumentSnapshot listing) throws FirebaseFirestoreException {
        if (!sameHost() || !ownerUid.equals(listing.getString("ownerUid"))) {
            throw new FirebaseFirestoreException("Host required", FirebaseFirestoreException.Code.PERMISSION_DENIED);
        }
    }
    private static FirebaseFirestoreException conflict() {
        return new FirebaseFirestoreException("Reload required", FirebaseFirestoreException.Code.ABORTED);
    }
    private void complete(RoomRepository.Callback<Void> callback) {
        if (sameHost()) {
            callback.success(null);
        } else {
            callback.failure(RoomRepository.Problem.AUTH);
        }
    }
    private void fail(Exception error, RoomRepository.Callback<?> callback) {
        if (!sameHost()) {
            callback.failure(RoomRepository.Problem.AUTH);
            return;
        }
        RoomRepository.Problem problem = RoomRepository.Problem.INVALID;
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException.Code code = ((FirebaseFirestoreException) error).getCode();
            if (code == FirebaseFirestoreException.Code.ABORTED) { problem = RoomRepository.Problem.CONFLICT; }
            if (code == FirebaseFirestoreException.Code.UNAVAILABLE
                    || code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED) { problem = RoomRepository.Problem.NETWORK; }
        }
        // Permission denied with an unchanged session is a write failure, not a successful renewal.
        callback.failure(problem);
    }
}
