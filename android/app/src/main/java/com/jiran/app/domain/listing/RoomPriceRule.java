package com.jiran.app.domain.listing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** FR2.1 price suggestions and validation. Prices and rent are integer sen. */
public final class RoomPriceRule {
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private final Supplier<Map<String, ?>> config;

    /** Supply the latest config/app data on each call; no network or SDK belongs in this rule. */
    public RoomPriceRule(Supplier<Map<String, ?>> config) {
        if (config == null) {
            throw new IllegalArgumentException("Config supplier is required");
        }
        this.config = config;
    }

    public RoomPriceRule() {
        this(() -> Collections.emptyMap());
    }

    /** Input order resolves ties. Occupancy deliberately does not affect a room's share. */
    public static final class Room {
        public final String id;
        public final String type;

        public Room(String id, String type) {
            if (id == null || id.trim().isEmpty() || !isType(type)) {
                throw new IllegalArgumentException("A room needs an id and master, medium or small type");
            }
            this.id = id;
            this.type = type;
        }
    }

    /** Prices preserve request order. Suggested allocations use all rent when rooms exist. */
    public static final class Split {
        public final Map<String, Long> pricesSen;
        public final long remainingSen;

        private Split(Map<String, Long> pricesSen, long remainingSen) {
            this.pricesSen = Collections.unmodifiableMap(new LinkedHashMap<>(pricesSen));
            this.remainingSen = remainingSen;
        }
    }

    public Split splitRoomPrices(long totalRentSen, List<Room> rooms) {
        requireRent(totalRentSen);
        if (rooms == null) {
            throw new IllegalArgumentException("Rooms are required");
        }
        Map<String, BigDecimal> weights = readWeights();
        Set<String> ids = new HashSet<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        int recipient = 0;
        int firstMaster = -1;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room == null || !ids.add(room.id)) {
                throw new IllegalArgumentException("Rooms must be non-null with unique ids");
            }
            totalWeight = totalWeight.add(weights.get(room.type));
            if ("master".equals(room.type) && firstMaster < 0) {
                firstMaster = i;
            }
            if (weights.get(room.type).compareTo(weights.get(rooms.get(recipient).type)) > 0) {
                recipient = i;
            }
        }
        if (rooms.isEmpty()) {
            return new Split(Collections.emptyMap(), totalRentSen);
        }
        if (firstMaster >= 0) {
            recipient = firstMaster;
        }
        BigDecimal rent = BigDecimal.valueOf(totalRentSen);
        List<BigDecimal> rounded = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (Room room : rooms) {
            // Divide directly to integer RM to avoid double rounding at half-RM boundaries.
            BigDecimal price = rent.multiply(weights.get(room.type))
                    .divide(totalWeight.multiply(HUNDRED), 0, RoundingMode.HALF_UP).multiply(HUNDRED);
            rounded.add(price);
            sum = sum.add(price);
        }
        BigDecimal corrected = rounded.get(recipient).add(rent.subtract(sum));
        // An extreme split can round other rooms above the entire rent. Reduce in input order.
        for (int i = 0; corrected.signum() < 0 && i < rounded.size(); i++) {
            if (i != recipient) {
                BigDecimal reduction = rounded.get(i).min(corrected.negate());
                rounded.set(i, rounded.get(i).subtract(reduction));
                corrected = corrected.add(reduction);
            }
        }
        rounded.set(recipient, corrected);
        Map<String, Long> prices = new LinkedHashMap<>();
        for (int i = 0; i < rooms.size(); i++) {
            prices.put(rooms.get(i).id, rounded.get(i).longValueExact());
        }
        return new Split(prices, remainingSen(totalRentSen, prices.values()));
    }

    /** Pass ALL room prices, vacant and occupied. Rejects over-allocation without sum overflow. */
    public static long remainingSen(long totalRentSen, Iterable<Long> allRoomPricesSen) {
        requireRent(totalRentSen);
        if (allRoomPricesSen == null) {
            throw new IllegalArgumentException("All room prices are required");
        }
        long remaining = totalRentSen;
        for (Long price : allRoomPricesSen) {
            if (price == null || price < 0 || price > remaining) {
                throw new IllegalArgumentException("Prices must be non-negative and total no more than rent");
            }
            remaining -= price;
        }
        return remaining;
    }

    private Map<String, BigDecimal> readWeights() {
        Map<String, BigDecimal> weights = new LinkedHashMap<>();
        weights.put("master", new BigDecimal("1.3"));
        weights.put("medium", BigDecimal.ONE);
        weights.put("small", new BigDecimal("0.7"));
        Map<String, ?> app = config.get();
        Object raw = app == null ? null : app.get("roomPriceWeights");
        if (raw == null) {
            return weights;
        }
        if (!(raw instanceof Map)) {
            throw new IllegalArgumentException("roomPriceWeights must be a map");
        }
        Map<?, ?> supplied = (Map<?, ?>) raw;
        for (String type : weights.keySet()) {
            if (!supplied.containsKey(type)) {
                continue;
            }
            Object value = supplied.get(type);
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("Weights must be positive finite numbers");
            }
            try {
                BigDecimal weight = new BigDecimal(value.toString());
                if (weight.signum() <= 0) {
                    throw new IllegalArgumentException("Weights must be positive");
                }
                weights.put(type, weight);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Weights must be finite", exception);
            }
        }
        return weights;
    }

    private static boolean isType(String type) {
        return "master".equals(type) || "medium".equals(type) || "small".equals(type);
    }

    private static void requireRent(long totalRentSen) {
        if (totalRentSen < 0) {
            throw new IllegalArgumentException("Rent must be non-negative");
        }
    }
}
