package com.jiran.app.data;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.functions.FirebaseFunctions;
import com.jiran.app.BuildConfig;
import java.util.Collections;

/** Emulator fixture adapter; production listing/profile integration remains pending. */
public final class CommuteRepository {
    public Task<CommuteSummary> preview(String scenarioId) {
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            return Tasks.forException(new IllegalStateException("Fixture preview is local only"));
        }
        return FirebaseFunctions.getInstance(BuildConfig.FUNCTIONS_REGION)
                .getHttpsCallable("getCommuteOptions")
                .call(Collections.singletonMap("scenarioId", scenarioId))
                .onSuccessTask(result -> Tasks.forResult(CommuteSummary.from(result.getData())));
    }
}
