package com.jiran.app.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class EmailPasswordInputTest {
    @Test
    public void rejectsBlankAndMalformedEmailBeforeSdkCalls() {
        assertFalse(EmailPasswordInput.isValidEmail(null));
        assertFalse(EmailPasswordInput.isValidEmail(""));
        assertFalse(EmailPasswordInput.isValidEmail("not-an-email"));
        assertFalse(EmailPasswordInput.isValidEmail("student @example.com"));
    }

    @Test
    public void acceptsAnEmailWithSurroundingWhitespaceForNormalization() {
        assertTrue(EmailPasswordInput.isValidEmail(" student@example.com "));
    }

    @Test
    public void rejectsEmptyPasswordButDoesNotTrimUserPasswords() {
        assertFalse(EmailPasswordInput.isValidPassword(null, false));
        assertFalse(EmailPasswordInput.isValidPassword("", false));
        assertTrue(EmailPasswordInput.isValidPassword(" a password ", false));
    }

    @Test
    public void appliesMinimumLengthOnlyToNewAccounts() {
        assertFalse(EmailPasswordInput.isValidPassword("12345", true));
        assertTrue(EmailPasswordInput.isValidPassword("123456", true));
        assertTrue(EmailPasswordInput.isValidPassword("short", false));
    }
}
