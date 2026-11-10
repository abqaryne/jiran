package com.jiran.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public final class AreaOptionTest {
    private Map<String, Object> document() {
        Map<String, Object> data = new HashMap<>();
        data.put("areaId", "sample_a");
        data.put("name", "Sample area A");
        data.put("lat", 0L);
        data.put("lng", 0.5);
        return data;
    }

    @Test
    public void parsesOnlyRequiredPublicReferenceFields() {
        AreaOption area = AreaOption.fromDocument("sample_a", document());
        assertEquals("sample_a", area.id);
        assertEquals("Sample area A", area.name);
        assertEquals(0, area.latitude, 0);
        assertEquals(0.5, area.longitude, 0);
    }

    @Test
    public void rejectsMismatchedIdentityAndMissingCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> AreaOption.fromDocument("wrong", document()));
        Map<String, Object> missing = document();
        missing.remove("lat");
        assertThrows(IllegalArgumentException.class, () -> AreaOption.fromDocument("sample_a", missing));
    }

    @Test
    public void rejectsNonNumericAndOutOfRangeCoordinates() {
        Map<String, Object> data = document();
        data.put("lat", "0");
        assertThrows(IllegalArgumentException.class, () -> AreaOption.fromDocument("sample_a", data));
        for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY, 91, -91}) {
            assertThrows(IllegalArgumentException.class, () -> new AreaOption("sample", "Sample", invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new AreaOption("sample", "Sample", 0, 181));
    }

    @Test
    public void rejectsEmptyNamesAndInvalidIds() {
        assertThrows(IllegalArgumentException.class, () -> new AreaOption("sample", " ", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new AreaOption("../sample", "Sample", 0, 0));
    }
}
