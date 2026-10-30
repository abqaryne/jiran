package com.jiran.app.domain.auth;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Decides which email addresses may register (FR1.1). Jiran is for UM students only, so the list holds the student
 * domain. It is the single place to change if the team adds another domain (for example postgraduate).
 *
 * <p>A domain must match exactly. "evil-um.edu.my" and "siswa.um.edu.my.example.com" are rejected.
 */
public final class UmEmailPolicy {

    /** Domains allowed to register. Decided 30 Oct 2026 (Ary): UM student mail only, staff mail is not accepted. */
    public static final List<String> ALLOWED_DOMAINS =
            Collections.unmodifiableList(Arrays.asList("siswa.um.edu.my"));

    private UmEmailPolicy() { }

    /** True when the text looks like one address with a non-empty name, in any case, with spaces around it ignored. */
    public static boolean isWellFormed(String email) {
        if (email == null) {
            return false;
        }
        String value = email.trim();
        int at = value.indexOf('@');
        if (at <= 0 || at != value.lastIndexOf('@') || at == value.length() - 1) {
            return false;
        }
        if (value.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        String domain = value.substring(at + 1);
        return domain.contains(".") && !domain.startsWith(".") && !domain.endsWith(".");
    }

    /** True when the address is well formed and its domain is exactly one of {@link #ALLOWED_DOMAINS}. */
    public static boolean isUmEmail(String email) {
        if (!isWellFormed(email)) {
            return false;
        }
        String value = email.trim();
        String domain = value.substring(value.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        return ALLOWED_DOMAINS.contains(domain);
    }
}
