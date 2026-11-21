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
    private long deposit = -1;
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
    /** Deposit from step 1, or -1 when the repository did not provide it. Display only. */
    public long deposit() { return deposit; }
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
                deposit = value.depositSen;
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
        List<Long> split = suggestedSplit();
        if (!editable() || split == null) { return; }
        for (int i = 0; i < rows.size(); i++) { rows.get(i).price = money(BigInteger.valueOf(split.get(i))); }
        edited();
    }

    /** Suggested price per row in row order, or null when there is nothing to suggest. Not applied. */
    public List<Long> suggestedSplit() {
        if (configInvalid || rows.isEmpty() || rent <= 0) { return null; }
        List<RoomPriceRule.Room> input = new ArrayList<>();
        for (Row row : rows) { input.add(new RoomPriceRule.Room(row.id, row.type)); }
        RoomPriceRule.Split split = new RoomPriceRule(() -> config).splitRoomPrices(rent, input);
        List<Long> prices = new ArrayList<>();
        for (Row row : rows) { prices.add(split.pricesSen.get(row.id)); }
        return prices;
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
    /** Text for the price box: "650" for whole ringgit, "650.50" otherwise, so it fits the compact field. */
    public static String money(BigInteger sen) {
        return sen.mod(BigInteger.valueOf(100)).signum() == 0 ? sen.divide(BigInteger.valueOf(100)).toString()
                : new BigDecimal(sen, 2).toPlainString();
    }

    /** Display form for labels: thousands separators, no ".00" for whole ringgit (150000 sen -> "1,500"). */
    public static String display(BigInteger sen) {
        BigDecimal value = new BigDecimal(sen, 2);
        boolean whole = sen.mod(BigInteger.valueOf(100)).signum() == 0;
        java.text.DecimalFormat format = new java.text.DecimalFormat(whole ? "#,##0" : "#,##0.00",
                java.text.DecimalFormatSymbols.getInstance(java.util.Locale.ROOT));
        return format.format(value);
    }

    /** Sum of all valid prices, or null while a price is incomplete. */
    public BigInteger allocatedSen() {
        BigInteger remaining = remainingSen();
        return remaining == null ? null : BigInteger.valueOf(rent).subtract(remaining);
    }

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
