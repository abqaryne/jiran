package com.jiran.app.util;

/** Guards synchronous FirebaseAuth preconditions in the developer shell. */
public final class LocalCredentials {
    private LocalCredentials() { }

    public static boolean isComplete(String email, String password) {
        return email != null && !email.trim().isEmpty()
                && password != null && !password.isEmpty();
    }
}
