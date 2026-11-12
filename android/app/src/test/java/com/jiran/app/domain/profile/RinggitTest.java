package com.jiran.app.domain.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public final class RinggitTest {
    @Test
    public void wholeAndDecimalRinggitBecomeSen() {
        assertEquals(Long.valueOf(45000L), Ringgit.toSen("450"));
        assertEquals(Long.valueOf(45050L), Ringgit.toSen("450.5"));
        assertEquals(Long.valueOf(45050L), Ringgit.toSen(" RM 450.50 "));
        assertEquals(Long.valueOf(120000L), Ringgit.toSen("1,200"));
    }

    @Test
    public void emptyOrNotANumberGivesNull() {
        assertNull(Ringgit.toSen(null));
        assertNull(Ringgit.toSen(""));
        assertNull(Ringgit.toSen("abc"));
        assertNull(Ringgit.toSen("450.555"));
        assertNull(Ringgit.toSen("-50"));
    }
}
