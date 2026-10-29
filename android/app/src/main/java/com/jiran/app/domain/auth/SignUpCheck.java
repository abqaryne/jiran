package com.jiran.app.domain.auth;

/**
 * Checks the sign-up form before anything is sent to Firebase. Each field gets its own {@link Problem}, so the
 * screen can show the message under the right field. Firebase stays the authority for accounts; this only saves
 * a round trip and gives clear messages. Any well-formed email may register (team decision, 29 Oct 2026, so first-year
 * students without a UM email can join); a UM email only earns the UM verified badge, see {@link UmEmailPolicy}.
 */
public final class SignUpCheck {

    /** Minimum password length (the Figma hint says at least 8 characters). */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /** What is wrong with one field. */
    public enum Problem { NONE, REQUIRED, NOT_AN_EMAIL, TOO_SHORT }

    private final Problem name;
    private final Problem email;
    private final Problem password;

    private SignUpCheck(Problem name, Problem email, Problem password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /** Checks the three fields. The password is never trimmed, because spaces can be part of it. */
    public static SignUpCheck of(String fullName, String email, String password) {
        return new SignUpCheck(checkName(fullName), checkEmail(email), checkPassword(password));
    }

    private static Problem checkName(String fullName) {
        return fullName == null || fullName.trim().isEmpty() ? Problem.REQUIRED : Problem.NONE;
    }

    private static Problem checkEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Problem.REQUIRED;
        }
        return UmEmailPolicy.isWellFormed(email) ? Problem.NONE : Problem.NOT_AN_EMAIL;
    }

    private static Problem checkPassword(String password) {
        if (password == null || password.isEmpty()) {
            return Problem.REQUIRED;
        }
        return password.length() < MIN_PASSWORD_LENGTH ? Problem.TOO_SHORT : Problem.NONE;
    }

    public Problem name() {
        return name;
    }

    public Problem email() {
        return email;
    }

    public Problem password() {
        return password;
    }

    public boolean isValid() {
        return name == Problem.NONE && email == Problem.NONE && password == Problem.NONE;
    }
}
