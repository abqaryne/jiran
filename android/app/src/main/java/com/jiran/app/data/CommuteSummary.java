package com.jiran.app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Immutable, validated response for the local fixture preview, in the per-trip format (PRD Module 4, 11 Nov 2026):
 * one-way time and fare per mode, no monthly total and no arrival-before-class logic.
 */
public final class CommuteSummary {
    private static final List<String> MODES = Arrays.asList("walking", "transit", "e_hailing", "driving");
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    public final String scenarioId;
    /** "ok" when a mode has a known fare, "no_known_fare" otherwise. */
    public final String status;
    /** Lowest known one-way fare among available modes, or null. */
    public final String selectedMode;
    /** True when a mode with an unknown fare could not be compared with the selected one. */
    public final boolean comparisonLimited;
    public final List<Option> options;

    private CommuteSummary(Map<?, ?> raw) {
        require("fixture".equals(raw.get("dataSource")) && Boolean.TRUE.equals(raw.get("isEstimate")));
        text(raw.get("assumptionVersion"));
        scenarioId = text(raw.get("scenarioId"));
        require(Arrays.asList("per-trip", "no-known-fare", "missing-data").contains(scenarioId));
        status = text(raw.get("status"));
        require(raw.get("comparisonLimited") instanceof Boolean);
        comparisonLimited = (Boolean) raw.get("comparisonLimited");
        require(raw.get("options") instanceof List);
        List<?> entries = (List<?>) raw.get("options");
        require(entries.size() == 4);
        List<Option> parsed = new ArrayList<>();
        for (int index = 0; index < MODES.size(); index++) {
            Option option = new Option(map(entries.get(index)));
            require(MODES.get(index).equals(option.mode));
            parsed.add(option);
        }
        options = Collections.unmodifiableList(parsed);
        require(raw.containsKey("selectedMode"));
        selectedMode = raw.get("selectedMode") == null ? null : text(raw.get("selectedMode"));
        Long lowest = null;
        boolean unknownFare = false;
        for (Option option : options) {
            if (option.reason != null) {
                continue;
            }
            if (option.perTripCostSen == null) {
                unknownFare = true;
            } else if (lowest == null || option.perTripCostSen < lowest) {
                lowest = option.perTripCostSen;
            }
        }
        if ("ok".equals(status)) {
            Option selected = null;
            for (Option option : options) {
                if (option.mode.equals(selectedMode)) {
                    selected = option;
                }
            }
            require(selected != null && selected.perTripCostSen != null && selected.perTripCostSen.equals(lowest));
        } else {
            require("no_known_fare".equals(status) && selectedMode == null && lowest == null);
        }
        require(comparisonLimited == (lowest != null && unknownFare));
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

    public static final class Option {
        public final String mode;
        /** Why the mode has no route, or null when it is available. */
        public final String reason;
        public final Long durationSeconds;
        public final Long distanceMetres;
        /** One-way fare in sen, or null when the fare is unknown. Never zero because it is unknown. */
        public final Long perTripCostSen;
        public final Long co2GramsPerTrip;
        public final String emissionSource;
        public final String costSource;
        public final String routeSource;
        public final List<String> warnings;
        public final Boolean trafficFallback;

        private Option(Map<?, ?> raw) {
            mode = text(raw.get("mode"));
            costSource = optionalText(raw, "costSource");
            routeSource = optionalText(raw, "routeSource");
            warnings = warnings(raw);
            require(!raw.containsKey("trafficFallback") || raw.get("trafficFallback") instanceof Boolean);
            trafficFallback = raw.containsKey("trafficFallback") ? (Boolean) raw.get("trafficFallback") : null;
            if ("unavailable".equals(raw.get("status"))) {
                reason = text(raw.get("reason"));
                require(Arrays.asList("missing_route", "missing_schedule", "missing_fare_assumption",
                        "provider_unavailable", "vehicle_unavailable").contains(reason));
                durationSeconds = null;
                distanceMetres = null;
                perTripCostSen = null;
                co2GramsPerTrip = null;
                emissionSource = null;
            } else {
                require("available".equals(raw.get("status")));
                reason = null;
                durationSeconds = number(raw.get("durationSeconds"));
                distanceMetres = number(raw.get("distanceMetres"));
                String fareStatus = text(raw.get("fareStatus"));
                if ("known".equals(fareStatus)) {
                    perTripCostSen = number(raw.get("perTripCostSen"));
                } else {
                    require("unavailable".equals(fareStatus) && raw.get("perTripCostSen") == null);
                    perTripCostSen = null;
                }
                co2GramsPerTrip = raw.get("co2GramsPerTrip") == null ? null : number(raw.get("co2GramsPerTrip"));
                emissionSource = raw.get("emissionSource") == null ? null : text(raw.get("emissionSource"));
                require((co2GramsPerTrip == null) == (emissionSource == null));
            }
        }
    }
}
