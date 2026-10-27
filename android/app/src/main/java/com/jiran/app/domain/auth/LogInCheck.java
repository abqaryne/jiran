package com.jiran.app.domain.auth;

import com.jiran.app.domain.auth.SignUpCheck.Problem;

/**
 * Checks the Log in form before anything is sent to Firebase. It is gentler than sign-up: any well-formed address is
 * accepted (Firebase decides whether the account exists) and the password has no length rule, so an old account is
 * never locked out by a newer rule.
 */
public final class LogInCheck {

    private final Problem email;
    private final Problem password;

    private LogInCheck(Problem email, Problem password) {
        this.email = email;
        this.password = password;
    }

    public static LogInCheck of(String email, String password) {
        Problem passwordProblem = password == null || password.isEmpty() ? Problem.REQUIRED : Problem.NONE;
        return new LogInCheck(emailProblem(email), passwordProblem);
    }

    /** REQUIRED when empty, NOT_AN_EMAIL when it is not an address, otherwise NONE. Also used by Reset password. */
    public static Problem emailProblem(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Problem.REQUIRED;
        }
        return UmEmailPolicy.isWellFormed(email) ? Problem.NONE : Problem.NOT_AN_EMAIL;
    }

    public Problem email() {
        return email;
    }

    public Problem password() {
        return password;
    }

    public boolean isValid() {
        return email == Problem.NONE && password == Problem.NONE;
    }
}
