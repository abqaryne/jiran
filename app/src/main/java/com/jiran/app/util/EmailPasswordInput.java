package com.jiran.app.util;

/** Validates SDK preconditions; Firebase remains authoritative for accounts. */
public final class EmailPasswordInput {
    private EmailPasswordInput() { }

    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    }

    public static boolean isValidPassword(String password, boolean creatingAccount) {
        return password != null && !password.isEmpty()
                && (!creatingAccount || password.length() >= 6);
    }
}
