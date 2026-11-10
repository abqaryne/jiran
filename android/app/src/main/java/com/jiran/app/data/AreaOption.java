package com.jiran.app.data;

import java.util.Map;

/** Public area reference data. Coordinates describe an area centre, not a Building or Unit. */
public final class AreaOption {
    public final String id;
    public final String name;
    public final double latitude;
    public final double longitude;

    public AreaOption(String id, String name, double latitude, double longitude) {
        if (id == null || !id.matches("[A-Za-z0-9_-]{1,128}") || name == null
                || name.trim().isEmpty() || name.length() > 200
                || !Double.isFinite(latitude) || Math.abs(latitude) > 90
                || !Double.isFinite(longitude) || Math.abs(longitude) > 180) {
            throw new IllegalArgumentException("Invalid area reference");
        }
        this.id = id;
        this.name = name.trim();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static AreaOption fromDocument(String documentId, Map<String, Object> data) {
        if (data == null || !documentId.equals(data.get("areaId"))
                || !(data.get("name") instanceof String)
                || !(data.get("lat") instanceof Number) || !(data.get("lng") instanceof Number)) {
            throw new IllegalArgumentException("Invalid area reference");
        }
        return new AreaOption(documentId, (String) data.get("name"),
                ((Number) data.get("lat")).doubleValue(), ((Number) data.get("lng")).doubleValue());
    }

    @Override
    public String toString() {
        return name;
    }
}
