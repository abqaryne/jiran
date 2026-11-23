package com.jiran.app.ui.gallery;

import com.jiran.app.data.MyListingRepository;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.domain.listing.ListingLifecycle;
import com.jiran.app.ui.listing.MyListingFragment;
import java.util.Arrays;
import java.util.Collections;

/** Fictional prices reused from the E2.2 sample. No Firebase calls, even when accepting requests. */
public final class SampleMyListingFragment extends MyListingFragment {
    @Override protected boolean allowsSampleData() { return true; }
    @Override protected void closeScreen() { requireActivity().finish(); }
    @Override protected MyListingRepository createRepository() {
        return new MyListingRepository() {
            private String status = "active";
            private long expiresAt = System.currentTimeMillis() - 1;
            private String requestStatus = "pending";
            @Override public void load(String id, RoomRepository.Callback<Listing> callback) {
                callback.success(new Listing("sample-my-listing", "Sample UM student listing", status, expiresAt,
                        Arrays.asList(new RoomAllocation("sample-master", "master", 65000, true, null),
                                new RoomAllocation("sample-medium", "medium", 50000, false, null),
                                new RoomAllocation("sample-small", "small", 35000, false, null)),
                        Collections.singletonList(new Request("sample-student", "Sample request: I study at UM. "
                                + "Could I arrange a viewing of the medium Room?", requestStatus))));
            }
            @Override public void renew(String id, RoomRepository.Callback<Void> callback) {
                expiresAt = ListingLifecycle.renew(System.currentTimeMillis(), 2);
                status = "active";
                callback.success(null);
            }
            @Override public void closeIfFull(String id, RoomRepository.Callback<Void> callback) { callback.success(null); }
            @Override public void decide(String id, String requesterUid, String decision, RoomRepository.Callback<Void> callback) {
                requestStatus = ListingLifecycle.decide(requestStatus, decision);
                callback.success(null);
            }
        };
    }
}
