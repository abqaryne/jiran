package com.jiran.app.data;

import java.util.List;

/** Read-only source for the seeded area picker; callbacks run on the main thread. */
public interface AreaSource {
    enum Problem { SIGN_IN_REQUIRED, UNAVAILABLE, INVALID_DATA }

    interface Callback {
        void onLoaded(List<AreaOption> areas);
        void onError(Problem problem);
    }

    void load(Callback callback);
}
