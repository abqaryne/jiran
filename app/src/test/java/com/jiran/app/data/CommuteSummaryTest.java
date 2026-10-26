package com.jiran.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public final class CommuteSummaryTest {
    private Map<String, Object> valid() {
        Map<String, Object> result = new HashMap<>();
        result.put("dataSource", "fixture");
        result.put("scenarioId", "on-time");
        result.put("assumptionVersion", "fixture-v1");
        result.put("isEstimate", true);
        result.put("status", "ok");
        result.put("selectedMode", "transit");
        result.put("roomPriceSen", 45000);
        result.put("utilitiesShareSen", 6200);
        result.put("monthlyTrips", 40);
        result.put("trueMonthlyCostSen", 61200.0);
        result.put("departTime", "2026-11-16T00:00:00.000Z");
        result.put("classStartTime", "2026-11-16T01:00:00.000Z");
        Map<String, Object> transit = new HashMap<>();
        transit.put("mode", "transit");
        transit.put("status", "available");
        transit.put("durationSeconds", 1800);
        transit.put("distanceMetres", 6000);
        transit.put("perTripCostSen", 250);
        transit.put("monthlyCostSen", 10000);
        transit.put("co2GramsPerTrip", 300);
        transit.put("emissionSource", "Synthetic fixture only");
        transit.put("arrivalTime", "2026-11-16T00:30:00.000Z");
        transit.put("arrivesOnTime", true);
        List<Map<String, Object>> options = new ArrayList<>();
        options.add(new HashMap<>(Map.of("mode", "walking", "status", "unavailable", "reason", "missing_route")));
        options.add(transit);
        options.add(new HashMap<>(Map.of("mode", "e_hailing", "status", "unavailable", "reason", "missing_route")));
        options.add(new HashMap<>(Map.of("mode", "driving", "status", "unavailable", "reason", "missing_route")));
        result.put("options", options);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> transit(Map<String, Object> result) {
        return ((List<Map<String, Object>>) result.get("options")).get(1);
    }

    @Test
    public void preservesProvenanceAndCopiesWarningsWithoutInventingFallback() {
        Map<String, Object> raw = valid();
        List<String> warnings = new ArrayList<>(List.of("Use footpaths", "<b>plain text</b>"));
        transit(raw).put("costSource", "Synthetic fare");
        transit(raw).put("routeSource", "Synthetic route");
        transit(raw).put("warnings", warnings);
        transit(raw).put("trafficFallback", true);
        CommuteSummary.Option option = CommuteSummary.from(raw).options.get(1);
        assertEquals("Synthetic fare", option.costSource);
        assertEquals("Synthetic route", option.routeSource);
        assertEquals(Boolean.TRUE, option.trafficFallback);
        warnings.clear();
        assertEquals(List.of("Use footpaths", "<b>plain text</b>"), option.warnings);
        assertThrows(UnsupportedOperationException.class, () -> option.warnings.clear());
        CommuteSummary.Option absent = CommuteSummary.from(valid()).options.get(1);
        assertNull(absent.trafficFallback);
        assertNull(absent.costSource);
        assertNull(absent.routeSource);
        assertEquals(List.of(), absent.warnings);
    }

    @Test
    public void rejectsMalformedPresentMetadataInsteadOfDroppingWarnings() {
        for (String field : List.of("costSource", "routeSource")) {
            for (Object value : new Object[]{null, "", "   ", 42, "x".repeat(501)}) {
                Map<String, Object> raw = valid();
                transit(raw).put(field, value);
                assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
            }
        }
        for (Object value : new Object[]{null, "warning", List.of(42), List.of("x".repeat(2001)),
                java.util.Collections.nCopies(21, "warning")}) {
            Map<String, Object> raw = valid();
            transit(raw).put("warnings", value);
            assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        }
        for (Object value : new Object[]{null, "true", 1}) {
            Map<String, Object> raw = valid();
            transit(raw).put("trafficFallback", value);
            assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        }
        Map<String, Object> explicitFalse = valid();
        transit(explicitFalse).put("trafficFallback", false);
        assertEquals(Boolean.FALSE, CommuteSummary.from(explicitFalse).options.get(1).trafficFallback);
    }

    @Test
    public void parsesIntegralSdkNumbersAndConsistentCosts() {
        CommuteSummary result = CommuteSummary.from(valid());
        assertEquals("transit", result.selectedMode);
        assertEquals(Long.valueOf(61200), result.trueMonthlyCostSen);
        assertEquals(Long.valueOf(10000), result.options.get(1).monthlyCostSen);
        assertEquals(Long.valueOf(300), result.options.get(1).co2GramsPerTrip);
        assertThrows(UnsupportedOperationException.class, () -> result.options.clear());
    }

    @Test
    public void preservesNoOnTimeStateWithoutInventingZeroTotal() {
        Map<String, Object> raw = valid();
        raw.put("status", "no_on_time_route");
        raw.put("classStartTime", "2026-11-16T00:05:00.000Z");
        raw.put("selectedMode", null);
        raw.put("trueMonthlyCostSen", null);
        transit(raw).put("arrivesOnTime", false);
        assertNull(CommuteSummary.from(raw).trueMonthlyCostSen);
    }

    @Test
    public void rejectsMalformedTotalsOrFixtureLabels() {
        for (Object value : new Object[]{-1, 61200.5, "61200", Double.NaN, 1.0e20, 0}) {
            Map<String, Object> raw = valid();
            raw.put("trueMonthlyCostSen", value);
            assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        }
        Map<String, Object> raw = valid();
        raw.put("dataSource", "live");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
    }

    @Test
    public void rejectsSelectedLateRouteAndInconsistentMonthlyCost() {
        Map<String, Object> raw = valid();
        transit(raw).put("arrivesOnTime", false);
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> badCost = valid();
        transit(badCost).put("monthlyCostSen", 10);
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(badCost));
    }

    @Test
    public void rejectsDuplicateOrMissingModes() {
        Map<String, Object> raw = valid();
        transit(raw).put("mode", "walking");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> missing = valid();
        missing.put("options", List.of());
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(missing));
    }

    @Test
    public void requiresEmissionSourceAndExplicitTimestamps() {
        Map<String, Object> raw = valid();
        transit(raw).put("emissionSource", "");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> missingOffset = valid();
        missingOffset.put("departTime", "2026-11-16T08:00:00");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(missingOffset));
    }
}
