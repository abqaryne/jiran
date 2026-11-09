package com.jiran.app.data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Step 1 values. Private address is only sent to the Worker, never a public document. */
public final class ListingForm {
    public String buildingName = "";
    public AreaOption area;
    public String address = "";
    public String rent = "";
    public String deposit = "";
    public String gender = "any";
    public final List<String> amenities = new ArrayList<>();
    public static long moneySen(String text, boolean allowZero) {
        if (text == null || !text.trim().matches("[0-9]{1,14}(\\.[0-9]{1,2})?")) {
            throw new IllegalArgumentException("invalid-money");
        }
        long sen = new BigDecimal(text.trim()).movePointRight(2).longValueExact();
        if (sen > 9007199254740991L || sen < (allowZero ? 0 : 1)) {
            throw new IllegalArgumentException("invalid-money");
        }
        return sen;
    }
    public String invalidField() {
        if (area == null || !validText(buildingName, 200)) {
            return "building";
        }
        if (!validText(address, 500)) {
            return "address";
        }
        try {
            moneySen(rent, false);
        } catch (IllegalArgumentException error) {
            return "rent";
        }
        try {
            moneySen(deposit, true);
        } catch (IllegalArgumentException error) {
            return "deposit";
        }
        if (!Arrays.asList("any", "male_only", "female_only").contains(gender)) {
            return "gender";
        }
        if (amenities.size() > 10 || amenities.stream().anyMatch(item -> !validText(item, 40))) {
            return "amenities";
        }
        return "";
    }
    private static boolean validText(String value, int maximum) {
        return value != null && !value.trim().isEmpty() && value.length() <= maximum
            && value.chars().noneMatch(Character::isISOControl);
    }
    public Map<String, Object> payload() {
        if (!invalidField().isEmpty()) {
            throw new IllegalArgumentException("invalid-form");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("buildingName", buildingName.trim());
        data.put("areaId", area.id);
        data.put("lat", area.latitude);
        data.put("lng", area.longitude);
        data.put("coordinateSource", "area_center_estimate");
        data.put("exactAddress", address.trim());
        data.put("totalRentSen", moneySen(rent, false));
        data.put("depositSen", moneySen(deposit, true));
        data.put("genderPref", gender);
        data.put("amenities", new ArrayList<>(amenities));
        return data;
    }
}
