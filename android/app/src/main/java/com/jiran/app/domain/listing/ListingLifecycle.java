package com.jiran.app.domain.listing;

/** FR2.1: elapsed UTC days, independent of timezone or daylight saving. Needs human review. */
public final class ListingLifecycle {
    public static final long DAY_MILLIS = 24L * 60 * 60 * 1000;
    private ListingLifecycle() { }

    public static String status(String stored, long expiresAt, long now, int vacantRooms) {
        if (!("active".equals(stored) || "expired".equals(stored) || "closed".equals(stored))
                || vacantRooms < 0) {
            throw new IllegalArgumentException("Published listing required");
        }
        if (vacantRooms == 0 || "closed".equals(stored)) { return "closed"; }
        return "expired".equals(stored) || expiresAt <= now ? "expired" : "active";
    }

    public static long daysRemaining(long expiresAt, long now) {
        if (expiresAt <= now) { return 0; }
        long remaining = Math.subtractExact(expiresAt, now);
        return 1 + (remaining - 1) / DAY_MILLIS;
    }

    public static long renew(long now, int vacantRooms) {
        if (vacantRooms <= 0) { throw new IllegalArgumentException("No vacant rooms"); }
        return Math.addExact(now, 30 * DAY_MILLIS);
    }

    /** A Host decides a pending request once; cancellation and decisions are terminal here. */
    public static String decide(String current, String decision) {
        if (!"pending".equals(current) || !("accepted".equals(decision) || "rejected".equals(decision))) {
            throw new IllegalArgumentException("Request is no longer pending or decision is invalid");
        }
        return decision;
    }
}
