package com.jiran.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public final class MatchSummaryTest {
    private static final List<String> NAMES = Arrays.asList("sleep", "cleanliness", "noise", "cooking",
            "smoking", "pets", "budget");

    private Map<String, Object> match(String id, int score) {
        List<Map<String, Object>> factors = new ArrayList<>();
        for (String name : NAMES) {
            factors.add(new HashMap<>(Map.of("factor", name, "weightBasisPoints", name.equals("sleep") ? 10000 : 0,
                    "similarityBasisPoints", score)));
        }
        return new HashMap<>(Map.of("candidateId", id, "status", "scored", "scoreBasisPoints", score,
                "score", score / 100.0, "missingFactors", new ArrayList<>(), "factors", factors,
                "overlappingClassDays", new ArrayList<>(List.of(1, 3)), "reasons", new ArrayList<>(List.of(
                        new HashMap<>(Map.of("factor", "sleep", "code", "partial_overlap"))))));
    }

    private Map<String, Object> envelope(List<Map<String, Object>> matches) {
        return new HashMap<>(Map.of("status", "ok", "dataSource", "fixture", "scenarioId", "ranked",
                "formulaVersion", "normalized-v1", "weightVersion", "sample-v1", "matches", matches));
    }

    @Test
    public void parsesFractionalScoresIntegralFirebaseDoublesAndPreservesRank() {
        Map<String, Object> first = match("z", 8333);
        first.put("scoreBasisPoints", 8333.0);
        MatchSummary result = MatchSummary.from(envelope(new ArrayList<>(List.of(first, match("a", 5000)))));
        assertEquals("z", result.matches.get(0).candidateId);
        assertEquals(Integer.valueOf(8333), result.matches.get(0).scoreBasisPoints);
        assertEquals("a", result.matches.get(1).candidateId);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void preservesMissingAnswersAndUnknownOverlapWithoutZeroScore() {
        Map<String, Object> row = match("incomplete", 5000);
        row.put("status", "insufficient_data");
        row.put("scoreBasisPoints", null);
        row.put("score", null);
        row.put("missingFactors", List.of("sleep"));
        row.put("overlappingClassDays", null);
        row.put("reasons", List.of());
        ((List<Map<String, Object>>) row.get("factors")).get(0).put("similarityBasisPoints", null);
        MatchSummary.Match result = MatchSummary.from(envelope(List.of(row))).matches.get(0);
        assertNull(result.scoreBasisPoints);
        assertNull(result.overlappingClassDays);
        assertEquals(List.of("sleep"), result.missingFactors);
        assertEquals(0, MatchSummary.from(envelope(List.of())).matches.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void copiesNestedListsAndCannotExposeMutableServerMaps() {
        Map<String, Object> row = match("sample", 5000);
        List<Map<String, Object>> rows = new ArrayList<>(List.of(row));
        MatchSummary result = MatchSummary.from(envelope(rows));
        rows.clear();
        ((List<Integer>) row.get("overlappingClassDays")).clear();
        assertEquals(1, result.matches.size());
        assertEquals(List.of(1, 3), result.matches.get(0).overlappingClassDays);
        assertThrows(UnsupportedOperationException.class, () -> result.matches.clear());
        assertThrows(UnsupportedOperationException.class, () -> result.matches.get(0).overlappingClassDays.clear());
    }

    @Test
    public void rejectsNonFixtureEnvelopeUnknownVersionAndExcessiveCandidates() {
        for (Map.Entry<String, Object> change : Map.<String, Object>of("dataSource", "live", "scenarioId", "unknown",
                "formulaVersion", "future-v2", "status", "error").entrySet()) {
            Map<String, Object> raw = envelope(List.of());
            raw.put(change.getKey(), change.getValue());
            assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(raw));
        }
        assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(null));
        assertThrows(IllegalArgumentException.class,
                () -> MatchSummary.from(envelope(java.util.Collections.nCopies(101, match("a", 5000)))));
        assertThrows(IllegalArgumentException.class,
                () -> MatchSummary.from(envelope(List.of(match("a", 5000), match("a", 5000)))));
    }

    @Test
    public void rejectsMalformedScoreAndStateContradictions() {
        for (Object score : new Object[]{-1, 10001, 1.5, Double.NaN, Double.POSITIVE_INFINITY, "5000", null}) {
            Map<String, Object> row = match("a", 5000);
            row.put("scoreBasisPoints", score);
            assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
        }
        Map<String, Object> row = match("a", 5000);
        row.put("score", 49.99);
        assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
        row.put("score", 50.0);
        row.put("status", "insufficient_data");
        assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void rejectsInvalidFactorWeightsMissingFieldsAndReasonCodes() {
        Map<String, Object> row = match("a", 5000);
        ((List<Map<String, Object>>) row.get("factors")).get(0).put("weightBasisPoints", 9999);
        assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
        for (String field : List.of("factors", "reasons", "missingFactors", "overlappingClassDays")) {
            Map<String, Object> missing = match("a", 5000);
            missing.remove(field);
            assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(missing))));
        }
        Map<String, Object> reason = match("a", 5000);
        reason.put("reasons", List.of(Map.of("factor", "sleep", "code", "untrusted-code")));
        assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(reason))));
    }

    @Test
    public void rejectsMalformedIdentityAndOverlapDays() {
        for (Object id : new Object[]{"", "../private", "x".repeat(129), 42}) {
            Map<String, Object> row = match("a", 5000);
            row.put("candidateId", id);
            assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
        }
        for (Object days : new Object[]{List.of(0), List.of(8), List.of(1, 1), List.of("Monday"), "unknown"}) {
            Map<String, Object> row = match("a", 5000);
            row.put("overlappingClassDays", days);
            assertThrows(IllegalArgumentException.class, () -> MatchSummary.from(envelope(List.of(row))));
        }
    }
}
