package com.jiran.app.data;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertFalse;
public class ListingFormTest {
    @Test public void convertsExactSenAndAcceptsZeroDeposit() {
        assertEquals(1001, ListingForm.moneySen("10.01", false));
        assertEquals(10, ListingForm.moneySen("0.1", false));
        assertEquals(0, ListingForm.moneySen("0.00", true));
        assertEquals(9007199254740991L, ListingForm.moneySen("90071992547409.91", false));
    }
    @Test public void rejectsRoundingNegativeExponentAndUnsafeAmounts() {
        for (String invalid : new String[]{"", "0", "-1", "1.001", "1e3", "1,000", "NaN",
            "90071992547409.92", "999999999999999999999999"}) {
            assertThrows(invalid, IllegalArgumentException.class, () -> ListingForm.moneySen(invalid, false));
        }
    }
    @Test public void payloadKeepsEstimateSourceAndNoInventedPlaceId() {
        ListingForm form = valid();
        assertEquals("", form.invalidField());
        assertEquals("area_center_estimate", form.payload().get("coordinateSource"));
        assertEquals("Fictional Unit 1", form.payload().get("exactAddress"));
        assertEquals("Jalan Fiksi 1", form.payload().get("buildingAddress"));
        assertFalse(form.payload().containsKey("placeId"));
        assertFalse(form.payload().containsKey("status"));
    }
    @Test public void validatesRequiredFieldsAndLimits() {
        ListingForm form = valid();
        form.area = null;
        assertEquals("building", form.invalidField());
        form = valid();
        form.address = " ";
        assertEquals("address", form.invalidField());
        form = valid();
        form.buildingAddress = " ";
        assertEquals("buildingAddress", form.invalidField());
        form.buildingAddress = "x".repeat(301);
        assertEquals("buildingAddress", form.invalidField());
        form.buildingAddress = "x".repeat(300);
        assertEquals("", form.invalidField());
        form = valid();
        form.amenities.add("x".repeat(41));
        assertEquals("amenities", form.invalidField());
    }
    private static ListingForm valid() {
        ListingForm form = new ListingForm();
        form.buildingName = "Fictional Building";
        form.area = new AreaOption("sample", "Sample Area", 0, 0);
        form.buildingAddress = "Jalan Fiksi 1";
        form.address = "Fictional Unit 1";
        form.rent = "10.01";
        form.deposit = "0";
        return form;
    }
}
