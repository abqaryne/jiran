package com.jiran.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public final class CommuteSummaryTest {
    private Map<String, Object> available(String mode, int seconds, Integer fareSen) {
        Map<String, Object> option = new HashMap<>();
        option.put("mode", mode);
        option.put("status", "available");
        option.put("durationSeconds", seconds);
        option.put("distanceMetres", 6000);
        option.put("perTripCostSen", fareSen);
        option.put("fareStatus", fareSen == null ? "unavailable" : "known");
        return option;
    }

    private Map<String, Object> unavailable(String mode, String reason) {
        return new HashMap<>(Map.of("mode", mode, "status", "unavailable", "reason", reason));
    }

    /** Walking and driving unavailable, transit RM 2.50, e-hailing RM 12.00: transit is the lowest known fare. */
    private Map<String, Object> valid() {
        Map<String, Object> result = new HashMap<>();
        result.put("dataSource", "fixture");
        result.put("scenarioId", "per-trip");
        result.put("assumptionVersion", "fixture-v1");
        result.put("isEstimate", true);
        result.put("status", "ok");
        result.put("selectedMode", "transit");
        result.put("comparisonLimited", false);
        Map<String, Object> transit = available("transit", 1800, 250);
        transit.put("co2GramsPerTrip", 300);
        transit.put("emissionSource", "Synthetic fixture only");
        List<Map<String, Object>> options = new ArrayList<>();
        options.add(unavailable("walking", "missing_route"));
        options.add(transit);
        options.add(available("e_hailing", 900, 1200));
        options.add(unavailable("driving", "missing_route"));
        result.put("options", options);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> option(Map<String, Object> result, int index) {
        return ((List<Map<String, Object>>) result.get("options")).get(index);
    }

    @Test
    public void parsesPerTripFaresWithoutAnyMonthlyField() {
        CommuteSummary result = CommuteSummary.from(valid());
        assertEquals("ok", result.status);
        assertEquals("transit", result.selectedMode);
        assertFalse(result.comparisonLimited);
        assertEquals(Long.valueOf(250), result.options.get(1).perTripCostSen);
        assertEquals(Long.valueOf(300), result.options.get(1).co2GramsPerTrip);
        assertEquals(Long.valueOf(1800), result.options.get(1).durationSeconds);
        assertThrows(UnsupportedOperationException.class, () -> result.options.clear());
    }

    @Test
    public void acceptsUnavailableVehicleWithoutInventingCost() {
        Map<String, Object> raw = valid();
        option(raw, 3).put("reason", "vehicle_unavailable");
        CommuteSummary.Option option = CommuteSummary.from(raw).options.get(3);
        assertEquals("vehicle_unavailable", option.reason);
        assertNull(option.perTripCostSen);
        assertNull(option.durationSeconds);
    }

    @Test
    public void anUnknownFareKeepsItsTimeAndIsNeverFree() {
        Map<String, Object> raw = valid();
        raw.put("comparisonLimited", true);
        option(raw, 0).clear();
        option(raw, 0).putAll(available("walking", 4000, null));
        CommuteSummary result = CommuteSummary.from(raw);
        assertNull(result.options.get(0).perTripCostSen);
        assertEquals(Long.valueOf(4000), result.options.get(0).durationSeconds);
        assertTrue(result.comparisonLimited);
    }

    @Test
    public void rejectsAnUnknownFareThatIsReportedAsZeroOrKnown() {
        Map<String, Object> raw = valid();
        option(raw, 2).put("fareStatus", "unavailable");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> noFare = valid();
        option(noFare, 2).put("perTripCostSen", null);
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(noFare));
    }

    @Test
    public void preservesNoKnownFareStateWithoutSelection() {
        Map<String, Object> raw = valid();
        raw.put("scenarioId", "no-known-fare");
        raw.put("status", "no_known_fare");
        raw.put("selectedMode", null);
        raw.put("comparisonLimited", false);
        option(raw, 1).clear();
        option(raw, 1).putAll(available("transit", 1800, null));
        option(raw, 2).clear();
        option(raw, 2).putAll(available("e_hailing", 900, null));
        CommuteSummary result = CommuteSummary.from(raw);
        assertNull(result.selectedMode);
        assertEquals("no_known_fare", result.status);
    }

    @Test
    public void rejectsASelectionThatIsNotTheLowestKnownFare() {
        Map<String, Object> raw = valid();
        raw.put("selectedMode", "e_hailing");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> unavailableSelection = valid();
        unavailableSelection.put("selectedMode", "walking");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(unavailableSelection));
        Map<String, Object> none = valid();
        none.put("status", "no_known_fare");
        none.put("selectedMode", null);
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(none));
    }

    @Test
    public void rejectsAWrongComparisonLimitedFlag() {
        Map<String, Object> raw = valid();
        raw.put("comparisonLimited", true);
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> missing = valid();
        missing.remove("comparisonLimited");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(missing));
    }

    @Test
    public void rejectsOldMonthlyScenariosAndFixtureLabels() {
        Map<String, Object> raw = valid();
        raw.put("scenarioId", "on-time");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> live = valid();
        live.put("dataSource", "live");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(live));
    }

    @Test
    public void rejectsMalformedNumbers() {
        for (Object value : new Object[]{-1, 250.5, "250", Double.NaN, 1.0e20}) {
            Map<String, Object> raw = valid();
            option(raw, 1).put("perTripCostSen", value);
            assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        }
    }

    @Test
    public void rejectsDuplicateOrMissingModes() {
        Map<String, Object> raw = valid();
        option(raw, 1).put("mode", "walking");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
        Map<String, Object> missing = valid();
        missing.put("options", List.of());
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(missing));
    }

    @Test
    public void requiresEmissionSourceWhenCo2IsGiven() {
        Map<String, Object> raw = valid();
        option(raw, 1).put("emissionSource", "");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(raw));
    }

    @Test
    public void parsesDisplayMetadataAndRejectsBadTypes() {
        Map<String, Object> raw = valid();
        option(raw, 1).put("routeSource", "Synthetic routing fixture");
        option(raw, 1).put("costSource", "Synthetic fare assumption");
        option(raw, 1).put("warnings", List.of("Use footpaths"));
        option(raw, 1).put("trafficFallback", true);
        CommuteSummary.Option option = CommuteSummary.from(raw).options.get(1);
        assertEquals("Synthetic routing fixture", option.routeSource);
        assertEquals("Synthetic fare assumption", option.costSource);
        assertEquals(List.of("Use footpaths"), option.warnings);
        assertEquals(Boolean.TRUE, option.trafficFallback);
        for (Object value : new Object[]{null, "true", 1}) {
            Map<String, Object> bad = valid();
            option(bad, 1).put("trafficFallback", value);
            assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(bad));
        }
        Map<String, Object> badWarnings = valid();
        option(badWarnings, 1).put("warnings", "text");
        assertThrows(IllegalArgumentException.class, () -> CommuteSummary.from(badWarnings));
    }
}
