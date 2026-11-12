package com.jiran.app.domain.profile;

/**
 * Turns the full name from sign-up into what other students may see. Before a Connection only {@link #initials}
 * is public (PRD stage table); {@link #firstName} goes to the private contact document for the Connection stage.
 * Needs human review: the initials rule (first letters of the first two words) is the team's choice, not the PRD's.
 */
public final class DisplayName {

    private DisplayName() {
    }

    /** "Mei Ling Tan" becomes "M.L."; a one-word name gives one letter; an empty name gives "". */
    public static String initials(String fullName) {
        String[] words = words(fullName);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Math.min(2, words.length); i++) {
            out.appendCodePoint(Character.toUpperCase(words[i].codePointAt(0))).append('.');
        }
        return out.toString();
    }

    /** "Mei Ling Tan" becomes "Mei"; an empty name gives "". */
    public static String firstName(String fullName) {
        String[] words = words(fullName);
        return words.length == 0 ? "" : words[0];
    }

    private static String[] words(String fullName) {
        if (fullName == null) {
            return new String[0];
        }
        String trimmed = fullName.trim();
        return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
    }
}
