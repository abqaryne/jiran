package com.jiran.app.domain.auth;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class VerifiedHostPolicyTest {
    @Test public void allowsOnlyOriginalVerifiedStudent() {
        assertTrue(VerifiedHostPolicy.permits("host", "host", "fictional@siswa.um.edu.my", true));
        assertFalse(VerifiedHostPolicy.permits("host", "host", "fictional@siswa.um.edu.my", false));
        assertFalse(VerifiedHostPolicy.permits("host", "another", "fictional@siswa.um.edu.my", true));
    }

    @Test public void rejectsMissingSessionAndNonStudentDomains() {
        assertFalse(VerifiedHostPolicy.permits("host", null, null, false));
        assertFalse(VerifiedHostPolicy.permits("", "", "fictional@siswa.um.edu.my", true));
        for (String email : new String[]{null, "fictional@um.edu.my", "fictional@example.com",
                "fictional@siswa.um.edu.my.example.com", "fictional@example.com@siswa.um.edu.my"}) {
            assertFalse(VerifiedHostPolicy.permits("host", "host", email, true));
        }
    }
}
