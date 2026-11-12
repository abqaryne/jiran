package com.jiran.app.domain.profile;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class DisplayNameTest {
    @Test
    public void initialsUseTheFirstTwoWords() {
        assertEquals("M.L.", DisplayName.initials("Mei Ling Tan"));
        assertEquals("A.R.", DisplayName.initials("  ahmad   rahman  "));
    }

    @Test
    public void oneWordGivesOneLetterAndEmptyGivesNothing() {
        assertEquals("S.", DisplayName.initials("Siti"));
        assertEquals("", DisplayName.initials("   "));
        assertEquals("", DisplayName.initials(null));
    }

    @Test
    public void firstNameIsTheFirstWord() {
        assertEquals("Mei", DisplayName.firstName("Mei Ling Tan"));
        assertEquals("", DisplayName.firstName(null));
    }
}
