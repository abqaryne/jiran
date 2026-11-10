package com.jiran.app.data;
import java.util.Map;
/** All callbacks return on the main thread. No automatic retry of non-idempotent writes. */
public interface ListingGateway {
    enum Problem { UNAVAILABLE, AUTH, REJECTED, UNCERTAIN }
    interface Callback {
        void success(String value);
        void failure(Problem problem);
    }
    void create(Map<String, Object> data, Callback callback);
    void upload(String listingId, byte[] jpeg, Callback callback);
}
