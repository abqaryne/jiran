package com.jiran.app.domain.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

public final class ListingLifecycleTest {
    @Test public void expiresExactlyAtDeadlineWithoutAWrite() {
        assertEquals("active", ListingLifecycle.status("active", 1000, 999, 1));
        assertEquals("expired", ListingLifecycle.status("active", 1000, 1000, 1));
        assertEquals("expired", ListingLifecycle.status("active", 1000, 1001, 1));
        assertEquals("expired", ListingLifecycle.status("expired", 2000, 1000, 1));
        assertEquals(0, ListingLifecycle.daysRemaining(1000, 1000));
        assertEquals(0, ListingLifecycle.daysRemaining(1000, 1001));
        assertEquals(1, ListingLifecycle.daysRemaining(1000, 999));
    }
    @Test public void renewalUsesThirtyElapsedDaysFromNowAndRoundsDisplayUp() {
        long now = 1795392000123L;
        long expiry = ListingLifecycle.renew(now, 1);
        assertEquals(now + 2592000000L, expiry);
        assertEquals(30, ListingLifecycle.daysRemaining(expiry, now));
        assertEquals(30, ListingLifecycle.daysRemaining(expiry, now + 1));
        assertEquals(29, ListingLifecycle.daysRemaining(expiry, now + ListingLifecycle.DAY_MILLIS));
        assertEquals("active", ListingLifecycle.status("active", expiry, now, 1));
        assertThrows(ArithmeticException.class, () -> ListingLifecycle.renew(Long.MAX_VALUE, 1));
    }
    @Test public void fullListingClosesEvenBeforeExpiryAndCannotRenew() {
        assertEquals("closed", ListingLifecycle.status("active", 2000, 1000, 0));
        assertEquals("closed", ListingLifecycle.status("expired", 500, 1000, 0));
        assertEquals("closed", ListingLifecycle.status("closed", 2000, 1000, 2));
        assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.renew(1000, 0));
        assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.status("draft", 0, 1000, 1));
    }
    @Test public void hostCanAcceptOrRejectOnlyPendingRequests() {
        assertEquals("accepted", ListingLifecycle.decide("pending", "accepted"));
        assertEquals("rejected", ListingLifecycle.decide("pending", "rejected"));
        for (String current : new String[]{"accepted", "rejected", "cancelled"}) {
            assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.decide(current, "accepted"));
            assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.decide(current, "rejected"));
        }
        assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.decide("pending", "cancelled"));
        assertThrows(IllegalArgumentException.class, () -> ListingLifecycle.decide("pending", "pending"));
    }
}
