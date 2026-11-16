package com.jiran.app.ui.listing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.domain.listing.RoomPriceRule;
import com.jiran.app.domain.profile.Ringgit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** FR2.1: retains edits across rotation; every room contributes regardless of occupancy. */
public final class RoomPricesViewModel extends ViewModel {
    public enum State { LOADING, EDITING, SAVING, SAVED, LOAD_ERROR, SAVE_ERROR, AUTH }
    public static final class Row {
        public final String id;
        public final String type;
        public final boolean persisted;
        private String price;
        private boolean occupied;
        private String occupiedByUid;
        private Row(RoomAllocation room, boolean persisted) {
            id = room.id;
            type = room.type;
            price = money(BigInteger.valueOf(room.priceSen));
            occupied = room.occupied;
            occupiedByUid = room.occupiedByUid;
            this.persisted = persisted;
        }
        public String price() { return price; }
        public boolean occupied() { return occupied; }
    }

    private final RoomRepository repository;
    private final String listingId;
    private final List<Row> rows = new ArrayList<>();
    private final MutableLiveData<Integer> changes = new MutableLiveData<>(0);
    private Map<String, ?> config = Collections.emptyMap();
    private RoomRepository.Subscription subscription;
    private State state = State.LOADING;
    private RoomRepository.Problem problem;
    private long rent;
    private boolean configFallback = true;
    private boolean configInvalid;
    private boolean ended;
    private boolean dirty;

    public RoomPricesViewModel(String listingId, RoomRepository repository) {
        this.listingId = listingId;
        this.repository = repository;
        subscription = repository.observeConfig(new RoomRepository.Callback<Map<String, ?>>() {
            @Override public void success(Map<String, ?> value) {
                if (ended) { return; }
                config = value == null ? Collections.emptyMap() : value;
                configFallback = !config.containsKey("roomPriceWeights");
                configInvalid = false;
                try {
                    new RoomPriceRule(() -> config).splitRoomPrices(0, Collections.emptyList());
                } catch (IllegalArgumentException error) {
                    configInvalid = true;
                }
                changed();
            }
            @Override public void failure(RoomRepository.Problem value) {
                if (ended) { return; }
                if (value == RoomRepository.Problem.AUTH) {
                    endSession();
                    return;
                }
                config = Collections.emptyMap();
                configFallback = true;
                configInvalid = false;
                changed();
            }
        });
        if (ended) { subscription.remove(); }
        reload();
    }

    public LiveData<Integer> changes() { return changes; }
    public List<Row> rows() { return Collections.unmodifiableList(rows); }
    public State state() { return state; }
    public RoomRepository.Problem problem() { return problem; }
    public long rent() { return rent; }
    public boolean configFallback() { return configFallback; }
    public boolean configInvalid() { return configInvalid; }
    public boolean dirty() { return dirty; }
    public boolean editable() { return !ended && state == State.EDITING; }

    public void reload() {
        if (ended || state == State.SAVING) { return; }
        state = State.LOADING;
        changed();
        repository.load(listingId, new RoomRepository.Callback<RoomRepository.Loaded>() {
            @Override public void success(RoomRepository.Loaded value) {
                if (ended) { return; }
                rows.clear();
                for (RoomAllocation room : value.rooms) {
                    rows.add(new Row(room, true));
                }
                rent = value.totalRentSen;
                dirty = false;
                state = State.EDITING;
                changed();
            }
            @Override public void failure(RoomRepository.Problem value) {
                fail(value, State.LOAD_ERROR);
            }
        });
    }

    public void add(String type) {
        if (!editable() || rows.size() >= RoomRepository.MAX_ROOMS) { return; }
        Row row = new Row(new RoomAllocation(UUID.randomUUID().toString(), type, 0, false, null), false);
        row.price = "";
        rows.add(row);
        edited();
    }

    public void removeNew(String id) {
        if (!editable()) { return; }
        rows.removeIf(row -> row.id.equals(id) && !row.persisted);
        edited();
    }

    public void price(String id, String value) {
        if (!editable()) { return; }
        for (Row row : rows) {
            if (row.id.equals(id)) { row.price = value; }
        }
        edited();
    }

    public void occupied(String id, boolean value) {
        if (!editable()) { return; }
        for (Row row : rows) {
            if (row.id.equals(id)) {
                row.occupied = value;
                if (!value) { row.occupiedByUid = null; }
            }
        }
        edited();
    }

    public void applySplit() {
        if (!editable() || configInvalid || rows.isEmpty()) { return; }
        List<RoomPriceRule.Room> input = new ArrayList<>();
        for (Row row : rows) { input.add(new RoomPriceRule.Room(row.id, row.type)); }
        RoomPriceRule.Split split = new RoomPriceRule(() -> config).splitRoomPrices(rent, input);
        for (Row row : rows) { row.price = money(BigInteger.valueOf(split.pricesSen.get(row.id))); }
        edited();
    }

    /** Null means some price is incomplete/invalid; never treat an empty field as zero. */
    public BigInteger remainingSen() {
        BigInteger remaining = BigInteger.valueOf(rent);
        for (Row row : rows) {
            Long price = Ringgit.toSen(row.price);
            if (price == null) { return null; }
            remaining = remaining.subtract(BigInteger.valueOf(price));
        }
        return remaining;
    }

    public boolean canSave() {
        BigInteger remaining = remainingSen();
        return editable() && !rows.isEmpty() && remaining != null && remaining.signum() >= 0;
    }

    public void save() {
        if (!canSave()) { return; }
        List<RoomAllocation> input = new ArrayList<>();
        List<Long> prices = new ArrayList<>();
        for (Row row : rows) {
            long price = Ringgit.toSen(row.price);
            input.add(new RoomAllocation(row.id, row.type, price, row.occupied, row.occupiedByUid));
            prices.add(price);
        }
        RoomPriceRule.remainingSen(rent, prices);
        state = State.SAVING;
        changed();
        repository.save(listingId, Collections.unmodifiableList(input), new RoomRepository.Callback<Void>() {
            @Override public void success(Void ignored) {
                if (ended) { return; }
                dirty = false;
                state = State.SAVED;
                changed();
            }
            @Override public void failure(RoomRepository.Problem value) { fail(value, State.SAVE_ERROR); }
        });
    }

    private void fail(RoomRepository.Problem value, State failedState) {
        if (ended) { return; }
        if (value == RoomRepository.Problem.AUTH) {
            endSession();
            return;
        }
        problem = value;
        state = failedState;
        changed();
    }

    private void edited() {
        dirty = true;
        changed();
    }
    private void changed() { changes.setValue(changes.getValue() + 1); }
    public static String money(BigInteger sen) { return new BigDecimal(sen, 2).toPlainString(); }

    public void endSession() {
        ended = true;
        rows.clear();
        config = Collections.emptyMap();
        if (subscription != null) { subscription.remove(); }
        state = State.AUTH;
        changed();
    }

    @Override protected void onCleared() { endSession(); }
}
