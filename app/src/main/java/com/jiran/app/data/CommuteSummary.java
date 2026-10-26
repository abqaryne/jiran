package com.jiran.app.data;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Immutable, validated response for the local fixture preview. */
public final class CommuteSummary {
    private static final List<String> MODES = Arrays.asList("walking", "transit", "e_hailing", "driving");
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    public final String scenarioId;
    public final String status;
    public final String selectedMode;
    public final Long trueMonthlyCostSen;
    public final long roomPriceSen;
    public final long utilitiesShareSen;
    public final long monthlyTrips;
    public final Instant departTime;
    public final Instant classStartTime;
    public final List<Option> options;

    private CommuteSummary(Map<?, ?> raw) {
        require("fixture".equals(raw.get("dataSource")) && Boolean.TRUE.equals(raw.get("isEstimate")));
        text(raw.get("assumptionVersion"));
        scenarioId = text(raw.get("scenarioId"));
        require(Arrays.asList("on-time", "no-on-time-route", "missing-data").contains(scenarioId));
        status = text(raw.get("status"));
        roomPriceSen = number(raw.get("roomPriceSen"));
        utilitiesShareSen = number(raw.get("utilitiesShareSen"));
        monthlyTrips = number(raw.get("monthlyTrips"));
        departTime = instant(raw.get("departTime"));
        classStartTime = instant(raw.get("classStartTime"));
        require(raw.get("options") instanceof List);
        List<?> entries = (List<?>) raw.get("options");
        require(entries.size() == 4);
        List<Option> parsed = new ArrayList<>();
        for (int index = 0; index < MODES.size(); index++) {
            Option option = new Option(map(entries.get(index)), monthlyTrips, departTime, classStartTime);
            require(MODES.get(index).equals(option.mode));
            parsed.add(option);
        }
        options = Collections.unmodifiableList(parsed);
        require(raw.containsKey("selectedMode") && raw.containsKey("trueMonthlyCostSen"));
        selectedMode = raw.get("selectedMode") == null ? null : text(raw.get("selectedMode"));
        trueMonthlyCostSen = raw.get("trueMonthlyCostSen") == null
                ? null : number(raw.get("trueMonthlyCostSen"));
        if ("ok".equals(status)) {
            Option selected = null;
            for (Option option : options) {
                if (option.mode.equals(selectedMode)) {
                    selected = option;
                }
            }
            require(selected != null && selected.arrivesOnTime && selected.monthlyCostSen != null
                    && trueMonthlyCostSen != null);
            long expected = Math.addExact(Math.addExact(roomPriceSen, utilitiesShareSen), selected.monthlyCostSen);
            require(trueMonthlyCostSen == expected);
        } else {
            require("no_on_time_route".equals(status) && selectedMode == null && trueMonthlyCostSen == null);
            for (Option option : options) {
                require(!option.arrivesOnTime);
            }
        }
    }

    public static CommuteSummary from(Object payload) {
        try {
            return new CommuteSummary(map(payload));
        } catch (RuntimeException error) {
            throw new IllegalArgumentException("Invalid commute response", error);
        }
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("Invalid commute response");
        }
    }

    private static Map<?, ?> map(Object value) {
        require(value instanceof Map);
        return (Map<?, ?>) value;
    }

    private static String text(Object value) {
        require(value instanceof String && !((String) value).trim().isEmpty());
        return (String) value;
    }

    private static String optionalText(Map<?, ?> raw, String field) {
        if (!raw.containsKey(field)) {
            return null;
        }
        String value = text(raw.get(field));
        require(value.length() <= 500);
        return value.trim();
    }

    private static List<String> warnings(Map<?, ?> raw) {
        if (!raw.containsKey("warnings")) {
            return Collections.emptyList();
        }
        require(raw.get("warnings") instanceof List);
        List<?> values = (List<?>) raw.get("warnings");
        require(values.size() <= 20);
        List<String> copy = new ArrayList<>();
        for (Object value : values) {
            require(value instanceof String && ((String) value).length() <= 2000);
            copy.add((String) value);
        }
        return Collections.unmodifiableList(copy);
    }

    private static long number(Object value) {
        require(value instanceof Number);
        double result = ((Number) value).doubleValue();
        require(Double.isFinite(result) && result >= 0 && result <= MAX_SAFE_INTEGER
                && Math.rint(result) == result);
        return (long) result;
    }

    private static Instant instant(Object value) {
        return OffsetDateTime.parse(text(value)).toInstant();
    }

    public static final class Option {
        public final String mode;
        public final String reason;
        public final Long durationSeconds;
        public final Long distanceMetres;
        public final Long monthlyCostSen;
        public final Long co2GramsPerTrip;
        public final String emissionSource;
        public final Instant arrivalTime;
        public final boolean arrivesOnTime;
        public final String costSource;
        public final String routeSource;
        public final List<String> warnings;
        public final Boolean trafficFallback;

        private Option(Map<?, ?> raw, long monthlyTrips, Instant departure, Instant classStart) {
            mode = text(raw.get("mode"));
            costSource = optionalText(raw, "costSource");
            routeSource = optionalText(raw, "routeSource");
            warnings = warnings(raw);
            require(!raw.containsKey("trafficFallback") || raw.get("trafficFallback") instanceof Boolean);
            trafficFallback = raw.containsKey("trafficFallback") ? (Boolean) raw.get("trafficFallback") : null;
            if ("unavailable".equals(raw.get("status"))) {
                reason = text(raw.get("reason"));
                require(Arrays.asList("missing_route", "missing_schedule", "missing_fare_assumption",
                        "provider_unavailable").contains(reason));
                durationSeconds = null;
                distanceMetres = null;
                monthlyCostSen = null;
                co2GramsPerTrip = null;
                emissionSource = null;
                arrivalTime = null;
                arrivesOnTime = false;
            } else {
                require("available".equals(raw.get("status")));
                reason = null;
                durationSeconds = number(raw.get("durationSeconds"));
                distanceMetres = number(raw.get("distanceMetres"));
                long perTripCost = number(raw.get("perTripCostSen"));
                monthlyCostSen = number(raw.get("monthlyCostSen"));
                require(monthlyCostSen == Math.multiplyExact(perTripCost, monthlyTrips));
                co2GramsPerTrip = raw.get("co2GramsPerTrip") == null ? null : number(raw.get("co2GramsPerTrip"));
                emissionSource = raw.get("emissionSource") == null ? null : text(raw.get("emissionSource"));
                require((co2GramsPerTrip == null) == (emissionSource == null));
                arrivalTime = instant(raw.get("arrivalTime"));
                require(raw.get("arrivesOnTime") instanceof Boolean);
                arrivesOnTime = (Boolean) raw.get("arrivesOnTime");
                require(arrivalTime.equals(departure.plusSeconds(durationSeconds))
                        && arrivesOnTime == arrivalTime.isBefore(classStart));
            }
        }
    }
}
