package com.jiran.app.domain.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

public final class SetupProgressTest {
    @Test
    public void aNewAccountStartsAtStepOne() {
        SetupProgress progress = new SetupProgress(false, false, false);
        assertEquals(1, progress.nextStep());
        assertFalse(progress.isComplete());
    }

    @Test
    public void resumesAtTheFirstMissingStep() {
        assertEquals(2, new SetupProgress(true, false, false).nextStep());
        assertEquals(3, new SetupProgress(true, true, false).nextStep());
    }

    @Test
    public void allThreeStepsMeanComplete() {
        SetupProgress progress = new SetupProgress(true, true, true);
        assertEquals(SetupProgress.STEPS + 1, progress.nextStep());
        assertTrue(progress.isComplete());
    }
    @Test
    public void preferencesCompleteWithoutAttendanceAndLegacyValuesAreIgnored() {
        Map<String, Object> publicFields = new HashMap<>();
        publicFields.put("gender", "female");
        publicFields.put("faculty", "fsktm");
        publicFields.put("year", 1);
        Map<String, Object> privateFields = new HashMap<>();
        privateFields.put("budgetMaxSen", 60000L);
        assertEquals(3, SetupProgress.fromFields(publicFields, privateFields).nextStep());
        privateFields.put("campusDaysPerWeek", 4);
        assertEquals(3, SetupProgress.fromFields(publicFields, privateFields).nextStep());
        privateFields.put("privacy", new HashMap<>());
        assertTrue(SetupProgress.fromFields(publicFields, privateFields).isComplete());
    }

    @Test
    public void missingDocumentsRequireSetup() {
        assertEquals(1, SetupProgress.fromFields(null, null).nextStep());
    }

}
