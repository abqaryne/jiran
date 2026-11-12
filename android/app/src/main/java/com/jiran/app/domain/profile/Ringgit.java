package com.jiran.app.domain.profile;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** The screen shows ringgit; Firestore stores integer sen (schema rule), so 450.50 becomes 45050. */
public final class Ringgit {

    private Ringgit() {
    }

    /**
     * Parses what the student typed ("450", "450.5", "RM 450.50") into sen, or returns null when it is empty or not a
     * number. More than two decimals is not a valid amount and also gives null.
     */
    public static Long toSen(String typed) {
        if (typed == null) {
            return null;
        }
        String cleaned = typed.trim().replaceFirst("(?i)^rm\\s*", "").replace(",", "");
        if (!cleaned.matches("\\d{1,7}(\\.\\d{1,2})?")) {
            return null;
        }
        return new BigDecimal(cleaned).movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact();
    }
}
