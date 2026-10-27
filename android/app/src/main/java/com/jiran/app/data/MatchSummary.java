package com.jiran.app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Immutable safe projection of the labelled local match fixtures. */
public final class MatchSummary {
    public static final List<String> FACTORS = Collections.unmodifiableList(Arrays.asList(
            "sleep", "cleanliness", "noise", "cooking", "smoking", "pets", "budget"));
    public final String scenarioId;
    public final String formulaVersion;
    public final String weightVersion;
    public final List<Match> matches;

    private MatchSummary(Map<?, ?> raw) {
        require("fixture".equals(raw.get("dataSource")) && "ok".equals(raw.get("status")));
        scenarioId = text(raw.get("scenarioId"));
        require(Arrays.asList("ranked", "missing-answers", "empty").contains(scenarioId));
        formulaVersion = text(raw.get("formulaVersion"));
        require("normalized-v1".equals(formulaVersion));
        weightVersion = text(raw.get("weightVersion"));
        require(weightVersion.matches("[A-Za-z0-9_.-]{1,100}"));
        Set<String> ids = new HashSet<>();
        List<Match> parsed = new ArrayList<>();
        for (Object value : list(raw.get("matches"), 100)) {
            Match entry = new Match(map(value));
            require(ids.add(entry.candidateId));
            parsed.add(entry);
        }
        matches = Collections.unmodifiableList(parsed);
    }

    public static MatchSummary from(Object payload) {
        try {
            return new MatchSummary(map(payload));
        } catch (RuntimeException error) {
            throw new IllegalArgumentException("Invalid match response", error);
        }
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("Invalid match response");
        }
    }

    private static Map<?, ?> map(Object value) {
        require(value instanceof Map);
        return (Map<?, ?>) value;
    }

    private static String text(Object value) {
        require(value instanceof String && !((String) value).isEmpty() && ((String) value).length() <= 128);
        return (String) value;
    }

    private static List<?> list(Object value, int max) {
        require(value instanceof List && ((List<?>) value).size() <= max);
        return (List<?>) value;
    }

    private static int integer(Object value, int max) {
        require(value instanceof Number);
        double number = ((Number) value).doubleValue();
        require(Double.isFinite(number) && number >= 0 && number <= max && Math.rint(number) == number);
        return (int) number;
    }

    private static Integer nullableInteger(Map<?, ?> raw, String key) {
        require(raw.containsKey(key));
        return raw.get(key) == null ? null : integer(raw.get(key), 10000);
    }

    public static final class Match {
        public final String candidateId;
        public final String status;
        public final Integer scoreBasisPoints;
        public final List<String> missingFactors;
        public final List<Integer> overlappingClassDays;
        public final List<Factor> factors;
        public final List<Reason> reasons;

        private Match(Map<?, ?> raw) {
            candidateId = text(raw.get("candidateId"));
            require(candidateId.matches("[A-Za-z0-9_-]{1,128}"));
            status = text(raw.get("status"));
            scoreBasisPoints = nullableInteger(raw, "scoreBasisPoints");
            require(raw.containsKey("score"));
            List<?> entries = list(raw.get("factors"), 7);
            require(entries.size() == 7);
            int weightSum = 0;
            List<Factor> parsedFactors = new ArrayList<>();
            List<String> expectedMissing = new ArrayList<>();
            Set<String> expectedReasons = new HashSet<>();
            for (int index = 0; index < entries.size(); index++) {
                Factor factor = new Factor(map(entries.get(index)));
                require(FACTORS.get(index).equals(factor.name));
                parsedFactors.add(factor);
                weightSum += factor.weightBasisPoints;
                if (factor.weightBasisPoints > 0) {
                    if (factor.similarityBasisPoints == null) {
                        expectedMissing.add(factor.name);
                    } else {
                        expectedReasons.add(factor.name);
                    }
                }
            }
            require(weightSum == 10000);
            factors = Collections.unmodifiableList(parsedFactors);
            List<String> missing = new ArrayList<>();
            for (Object value : list(raw.get("missingFactors"), 7)) {
                missing.add(text(value));
            }
            require(missing.equals(expectedMissing));
            missingFactors = Collections.unmodifiableList(missing);
            if ("scored".equals(status)) {
                require(missing.isEmpty() && scoreBasisPoints != null && raw.get("score") instanceof Number);
                require(((Number) raw.get("score")).doubleValue() == scoreBasisPoints / 100.0);
            } else {
                require("insufficient_data".equals(status) && !missing.isEmpty()
                        && scoreBasisPoints == null && raw.get("score") == null);
            }
            require(raw.containsKey("overlappingClassDays"));
            List<Integer> days = null;
            if (raw.get("overlappingClassDays") != null) {
                days = new ArrayList<>();
                for (Object value : list(raw.get("overlappingClassDays"), 7)) {
                    int day = integer(value, 7);
                    require(day >= 1 && !days.contains(day));
                    days.add(day);
                }
            }
            overlappingClassDays = days == null ? null : Collections.unmodifiableList(days);
            List<Reason> parsedReasons = new ArrayList<>();
            for (Object value : list(raw.get("reasons"), 7)) {
                Reason reason = new Reason(map(value));
                require(expectedReasons.remove(reason.factor));
                parsedReasons.add(reason);
            }
            require(expectedReasons.isEmpty());
            reasons = Collections.unmodifiableList(parsedReasons);
        }
    }

    public static final class Factor {
        public final String name;
        public final int weightBasisPoints;
        public final Integer similarityBasisPoints;

        private Factor(Map<?, ?> raw) {
            name = text(raw.get("factor"));
            weightBasisPoints = integer(raw.get("weightBasisPoints"), 10000);
            similarityBasisPoints = nullableInteger(raw, "similarityBasisPoints");
        }
    }

    public static final class Reason {
        public final String factor;
        public final String code;

        private Reason(Map<?, ?> raw) {
            factor = text(raw.get("factor"));
            code = text(raw.get("code"));
            require(FACTORS.contains(factor) && Arrays.asList("similar", "different", "partial_overlap").contains(code));
        }
    }
}
