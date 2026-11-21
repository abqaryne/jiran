package com.jiran.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.junit.Test;

public final class RoomAllocationTest {
    @Test public void roundTripKeepsListingAndOccupancyWithoutUtilitiesOrPrivateFields() {
        RoomAllocation room = new RoomAllocation("r1", "master", 65000, true, "fictional-resident");
        Map<String, Object> data = room.fields("listing");
        assertEquals(6, data.size());
        assertEquals("listing", data.get("listingId"));
        assertFalse(data.containsKey("utilitiesSen"));
        RoomAllocation restored = RoomAllocation.fromFields("r1", data);
        assertTrue(restored.occupied);
        assertEquals(65000, restored.priceSen);
        assertEquals("fictional-resident", restored.occupiedByUid);
    }

    @Test public void vacantRoomHasNoResidentIdentity() {
        Map<String, Object> data = new RoomAllocation("r", "small", 0, false, null).fields("listing");
        assertEquals("vacant", data.get("status"));
        assertFalse(data.containsKey("occupiedByUid"));
        assertThrows(IllegalArgumentException.class,
                () -> new RoomAllocation("r", "small", 0, false, "fictional-resident"));
    }

    @Test public void invalidStoredDataFailsClosed() {
        Map<String, Object> data = new RoomAllocation("r", "small", 100, false, null).fields("listing");
        data.put("priceSen", 1.5);
        assertThrows(IllegalArgumentException.class, () -> RoomAllocation.fromFields("r", data));
        data.put("priceSen", 100L);
        data.put("status", "unknown");
        assertThrows(IllegalArgumentException.class, () -> RoomAllocation.fromFields("r", data));
        data.put("status", "vacant");
        assertThrows(IllegalArgumentException.class, () -> RoomAllocation.fromFields("different", data));
        assertThrows(IllegalArgumentException.class,
                () -> new RoomAllocation("bad/path", "master", 0, false, null));
    }
}
