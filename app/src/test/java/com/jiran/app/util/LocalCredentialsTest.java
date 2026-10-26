package com.jiran.app.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class LocalCredentialsTest {
    @Test
    public void rejectsMissingCredentialsBeforeFirebaseCanThrowSynchronously() {
        assertFalse(LocalCredentials.isComplete("", ""));
        assertFalse(LocalCredentials.isComplete("   ", "password"));
        assertFalse(LocalCredentials.isComplete("user@example.com", ""));
        assertFalse(LocalCredentials.isComplete(null, "password"));
        assertFalse(LocalCredentials.isComplete("user@example.com", null));
    }

    @Test
    public void acceptsNonemptyCredentialsForBackendVerification() {
        assertTrue(LocalCredentials.isComplete("user@example.com", "password"));
    }
}
