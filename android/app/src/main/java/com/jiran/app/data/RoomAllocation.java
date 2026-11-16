package com.jiran.app.data;

import com.jiran.app.domain.listing.RoomPriceRule;
import java.util.HashMap;
import java.util.Map;

/** A Room's rent only. No utilities, address or contact details belong in this document. */
public final class RoomAllocation {
    public final String id;
    public final String type;
    public final long priceSen;
    public final boolean occupied;
    public final String occupiedByUid;

    public RoomAllocation(String id, String type, long priceSen, boolean occupied, String occupiedByUid) {
        new RoomPriceRule.Room(id, type);
        if (id.contains("/") || priceSen < 0 || (!occupied && occupiedByUid != null)) {
            throw new IllegalArgumentException("Invalid room allocation");
        }
        this.id = id;
        this.type = type;
        this.priceSen = priceSen;
        this.occupied = occupied;
        this.occupiedByUid = occupiedByUid;
    }

    public Map<String, Object> fields(String listingId) {
        Map<String, Object> data = new HashMap<>();
        data.put("roomId", id);
        data.put("listingId", listingId);
        data.put("type", type);
        data.put("priceSen", priceSen);
        data.put("status", occupied ? "occupied" : "vacant");
        if (occupiedByUid != null) {
            data.put("occupiedByUid", occupiedByUid);
        }
        return data;
    }

    public static RoomAllocation fromFields(String id, Map<String, Object> fields) {
        if (fields == null || !id.equals(fields.get("roomId")) || !(fields.get("priceSen") instanceof Long)
                || !("vacant".equals(fields.get("status")) || "occupied".equals(fields.get("status")))) {
            throw new IllegalArgumentException("Invalid stored room");
        }
        return new RoomAllocation(id, (String) fields.get("type"), (Long) fields.get("priceSen"),
                "occupied".equals(fields.get("status")), (String) fields.get("occupiedByUid"));
    }
}
