package com.jiran.app.domain.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class UmEmailPolicyTest {
    @Test
    public void acceptsStudentAndStaffDomains() {
        assertTrue(UmEmailPolicy.isUmEmail("meiling@siswa.um.edu.my"));
        assertTrue(UmEmailPolicy.isUmEmail("lecturer@um.edu.my"));
    }

    @Test
    public void ignoresCaseAndSurroundingSpaces() {
        assertTrue(UmEmailPolicy.isUmEmail("  MeiLing@SISWA.UM.EDU.MY "));
    }

    @Test
    public void rejectsOtherDomains() {
        assertFalse(UmEmailPolicy.isUmEmail("meiling@gmail.com"));
        assertFalse(UmEmailPolicy.isUmEmail("meiling@um.edu"));
    }

    @Test
    public void rejectsLookAlikeDomains() {
        assertFalse(UmEmailPolicy.isUmEmail("a@evil-um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("a@siswa.um.edu.my.example.com"));
        assertFalse(UmEmailPolicy.isUmEmail("a@mail.siswa.um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("a@siswa.um.edu.my.com"));
    }

    @Test
    public void rejectsTwoAtSignsSoTheDomainCannotBeHidden() {
        assertFalse(UmEmailPolicy.isUmEmail("a@gmail.com@um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("a@um.edu.my@gmail.com"));
    }

    @Test
    public void rejectsMalformedAddresses() {
        assertFalse(UmEmailPolicy.isUmEmail(null));
        assertFalse(UmEmailPolicy.isUmEmail(""));
        assertFalse(UmEmailPolicy.isUmEmail("siswa.um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("@um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("a@"));
        assertFalse(UmEmailPolicy.isUmEmail("a b@um.edu.my"));
        assertFalse(UmEmailPolicy.isUmEmail("a@.um.edu.my"));
    }

    @Test
    public void wellFormedDoesNotCareAboutTheDomain() {
        assertTrue(UmEmailPolicy.isWellFormed("meiling@gmail.com"));
        assertFalse(UmEmailPolicy.isWellFormed("meiling"));
    }
}
