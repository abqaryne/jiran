package com.jiran.app.ui.gallery;

import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.domain.listing.RoomPriceRule;
import com.jiran.app.ui.listing.RoomPricesFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Synthetic RM1,500 fixture with one of each type; Save only updates this in-memory sample. */
public final class SampleRoomPricesFragment extends RoomPricesFragment {
    @Override protected boolean allowsSampleData() { return true; }
    @Override protected void closeForm() { requireActivity().finish(); }
    @Override protected RoomRepository createRepository() {
        return new RoomRepository() {
            private List<RoomAllocation> saved = Arrays.asList(
                    new RoomAllocation("sample-master", "master", 0, false, null),
                    new RoomAllocation("sample-medium", "medium", 0, false, null),
                    new RoomAllocation("sample-small", "small", 0, false, null));
            @Override public void load(String id, Callback<Loaded> callback) {
                callback.success(new Loaded(150000, saved));
            }
            @Override public Subscription observeConfig(Callback<Map<String, ?>> callback) {
                callback.success(Collections.emptyMap());
                return () -> { };
            }
            @Override public void save(String id, List<RoomAllocation> rooms, Callback<Void> callback) {
                List<Long> prices = new ArrayList<>();
                for (RoomAllocation room : rooms) { prices.add(room.priceSen); }
                RoomPriceRule.remainingSen(150000, prices);
                saved = new ArrayList<>(rooms);
                callback.success(null);
            }
        };
    }
}
