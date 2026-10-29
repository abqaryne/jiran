package com.jiran.app.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jiran.app.domain.auth.SignUpCheck.Problem;
import org.junit.Test;

public final class SignUpCheckTest {
    @Test
    public void acceptsAFilledFormWithAUmEmail() {
        SignUpCheck check = SignUpCheck.of("Mei Ling Tan", "meiling@siswa.um.edu.my", "12345678");
        assertTrue(check.isValid());
    }

    @Test
    public void flagsEachEmptyFieldOnItsOwn() {
        SignUpCheck check = SignUpCheck.of("  ", "", null);
        assertFalse(check.isValid());
        assertEquals(Problem.REQUIRED, check.name());
        assertEquals(Problem.REQUIRED, check.email());
        assertEquals(Problem.REQUIRED, check.password());
    }

    @Test
    public void acceptsAnyWellFormedEmailButNotATypo() {
        assertEquals(Problem.NONE,
                SignUpCheck.of("A", "a@gmail.com", "12345678").email());
        assertEquals(Problem.NOT_AN_EMAIL,
                SignUpCheck.of("A", "a-at-um.edu.my", "12345678").email());
    }

    @Test
    public void requiresEightCharactersAndKeepsSpaces() {
        assertEquals(Problem.TOO_SHORT,
                SignUpCheck.of("A", "a@um.edu.my", "1234567").password());
        assertEquals(Problem.NONE,
                SignUpCheck.of("A", "a@um.edu.my", "12345678").password());
        assertEquals(Problem.NONE,
                SignUpCheck.of("A", "a@um.edu.my", "        ").password());
    }
}
