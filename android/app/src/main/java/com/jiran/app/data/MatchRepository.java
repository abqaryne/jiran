package com.jiran.app.data;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.functions.FirebaseFunctions;
import com.jiran.app.BuildConfig;
import java.util.Collections;

/** Emulator-only adapter; production candidate authorization remains pending. */
public final class MatchRepository {
    public Task<MatchSummary> preview(String scenarioId) {
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            return Tasks.forException(new IllegalStateException("Fixture preview is local only"));
        }
        return FirebaseFunctions.getInstance(BuildConfig.FUNCTIONS_REGION)
                .getHttpsCallable("computeMatchScores")
                .call(Collections.singletonMap("scenarioId", scenarioId))
                .onSuccessTask(result -> Tasks.forResult(MatchSummary.from(result.getData())));
    }
}
