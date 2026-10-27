package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.jiran.app.data.CommuteRepository;
import com.jiran.app.data.CommuteSummary;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Real Android SDK → callable → pure calculator → immutable response parsing. */
@RunWith(AndroidJUnit4.class)
public final class CommuteIntegrationTest {
    @Test
    public void parsesAllThreeAuthenticatedServerFixturesWithoutInventingCosts() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Bundle args = InstrumentationRegistry.getArguments();
        String email = args.getString("email");
        String password = args.getString("password");
        assumeTrue(email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password),
                30, TimeUnit.SECONDS);
        CommuteRepository repository = new CommuteRepository();
        CommuteSummary onTime = Tasks.await(repository.preview("on-time"), 30, TimeUnit.SECONDS);
        assertEquals("transit", onTime.selectedMode);
        assertEquals(Long.valueOf(61200), onTime.trueMonthlyCostSen);
        assertEquals(Long.valueOf(10000), onTime.options.get(1).monthlyCostSen);
        assertFalse(onTime.options.get(0).arrivesOnTime);
        assertEquals("Synthetic routing fixture", onTime.options.get(0).routeSource);
        assertEquals("Synthetic warning: use designated footpaths.", onTime.options.get(0).warnings.get(0));
        assertEquals("Synthetic fare assumption", onTime.options.get(1).costSource);
        assertEquals(Boolean.TRUE, onTime.options.get(3).trafficFallback);
        CommuteSummary late = Tasks.await(repository.preview("no-on-time-route"), 30, TimeUnit.SECONDS);
        assertNull(late.selectedMode);
        assertNull(late.trueMonthlyCostSen);
        CommuteSummary missing = Tasks.await(repository.preview("missing-data"), 30, TimeUnit.SECONDS);
        assertEquals("missing_schedule", missing.options.get(1).reason);
        assertEquals("missing_fare_assumption", missing.options.get(2).reason);
        assertNull(missing.trueMonthlyCostSen);
    }
}
