package com.jiran.app.domain.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jiran.app.domain.auth.SignUpCheck.Problem;
import org.junit.Test;

public final class LogInCheckTest {
    @Test
    public void acceptsAnyWellFormedEmailAndAnyNonEmptyPassword() {
        assertTrue(LogInCheck.of("meiling@siswa.um.edu.my", "x").isValid());
        assertTrue(LogInCheck.of("old.account@gmail.com", " ").isValid());
    }

    @Test
    public void flagsEmptyFieldsAndABrokenAddress() {
        LogInCheck empty = LogInCheck.of(" ", "");
        assertFalse(empty.isValid());
        assertEquals(Problem.REQUIRED, empty.email());
        assertEquals(Problem.REQUIRED, empty.password());
        assertEquals(Problem.NOT_AN_EMAIL, LogInCheck.of("meiling", "x").email());
    }

    @Test
    public void emailProblemIsSharedWithResetPassword() {
        assertEquals(Problem.NONE, LogInCheck.emailProblem("a@um.edu.my"));
        assertEquals(Problem.REQUIRED, LogInCheck.emailProblem(null));
        assertEquals(Problem.NOT_AN_EMAIL, LogInCheck.emailProblem("a@@um.edu.my"));
    }
}
