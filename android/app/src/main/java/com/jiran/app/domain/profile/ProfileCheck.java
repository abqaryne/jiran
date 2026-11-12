package com.jiran.app.domain.profile;

/**
 * Checks the profile setup fields before anything is written to Firestore. Like {@code SignUpCheck}, each field gets
 * its own {@link Problem} so the screen can show the message under that field. Money is integer sen (schema rule).
 */
public final class ProfileCheck {

    /** Values of {@code users/{uid}.year}: years 1 to 4, and 5 for a postgraduate (Figma "Postgraduate" chip). */
    public static final int MIN_YEAR = 1;
    public static final int POSTGRADUATE = 5;
    public static final int MAX_YEAR = POSTGRADUATE;

    /** What is wrong with one field. */
    public enum Problem { NONE, REQUIRED, OUT_OF_RANGE, MIN_ABOVE_MAX }

    private ProfileCheck() {
    }

    /** Step 1: gender is required and is only "male" or "female" (decided 31 Oct 2026). */
    public static Problem gender(String gender) {
        if (gender == null || gender.isEmpty()) {
            return Problem.REQUIRED;
        }
        return "male".equals(gender) || "female".equals(gender) ? Problem.NONE : Problem.OUT_OF_RANGE;
    }

    /** Step 1: a faculty code from the string resource list. */
    public static Problem faculty(String faculty) {
        return faculty == null || faculty.trim().isEmpty() ? Problem.REQUIRED : Problem.NONE;
    }

    /** Step 1: the academic year, {@value #MIN_YEAR} to 4, or {@value #POSTGRADUATE} for a postgraduate. */
    public static Problem year(Integer year) {
        if (year == null) {
            return Problem.REQUIRED;
        }
        return year < MIN_YEAR || year > MAX_YEAR ? Problem.OUT_OF_RANGE : Problem.NONE;
    }

    /** Step 2: the minimum budget in sen; must be above zero. */
    public static Problem budgetMin(Long minSen) {
        if (minSen == null) {
            return Problem.REQUIRED;
        }
        return minSen <= 0 ? Problem.OUT_OF_RANGE : Problem.NONE;
    }

    /** Step 2: the maximum budget in sen; must be above zero and not below the minimum. */
    public static Problem budgetMax(Long minSen, Long maxSen) {
        if (maxSen == null) {
            return Problem.REQUIRED;
        }
        if (maxSen <= 0) {
            return Problem.OUT_OF_RANGE;
        }
        return minSen != null && minSen > maxSen ? Problem.MIN_ABOVE_MAX : Problem.NONE;
    }
}
