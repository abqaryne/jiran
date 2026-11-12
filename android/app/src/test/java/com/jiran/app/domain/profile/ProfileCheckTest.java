package com.jiran.app.domain.profile;

import static org.junit.Assert.assertEquals;

import com.jiran.app.domain.profile.ProfileCheck.Problem;
import org.junit.Test;

public final class ProfileCheckTest {
    @Test
    public void genderIsRequiredAndOnlyMaleOrFemale() {
        assertEquals(Problem.REQUIRED, ProfileCheck.gender(null));
        assertEquals(Problem.REQUIRED, ProfileCheck.gender(""));
        assertEquals(Problem.OUT_OF_RANGE, ProfileCheck.gender("other"));
        assertEquals(Problem.NONE, ProfileCheck.gender("male"));
        assertEquals(Problem.NONE, ProfileCheck.gender("female"));
    }

    @Test
    public void facultyAndYearAreRequired() {
        assertEquals(Problem.REQUIRED, ProfileCheck.faculty(" "));
        assertEquals(Problem.NONE, ProfileCheck.faculty("FSKTM"));
        assertEquals(Problem.REQUIRED, ProfileCheck.year(null));
        assertEquals(Problem.OUT_OF_RANGE, ProfileCheck.year(0));
        assertEquals(Problem.OUT_OF_RANGE, ProfileCheck.year(6));
        assertEquals(Problem.NONE, ProfileCheck.year(4));
        assertEquals(Problem.NONE, ProfileCheck.year(ProfileCheck.POSTGRADUATE));
    }

    @Test
    public void budgetIsPositiveSen() {
        assertEquals(Problem.REQUIRED, ProfileCheck.budgetMin(null));
        assertEquals(Problem.OUT_OF_RANGE, ProfileCheck.budgetMin(0L));
        assertEquals(Problem.NONE, ProfileCheck.budgetMin(30000L));
    }

    @Test
    public void budgetMinAboveMaxIsRejectedOnTheMaxField() {
        assertEquals(Problem.MIN_ABOVE_MAX, ProfileCheck.budgetMax(60000L, 30000L));
        assertEquals(Problem.NONE, ProfileCheck.budgetMax(30000L, 30000L));
        assertEquals(Problem.NONE, ProfileCheck.budgetMax(null, 60000L));
        assertEquals(Problem.REQUIRED, ProfileCheck.budgetMax(30000L, null));
    }
}
