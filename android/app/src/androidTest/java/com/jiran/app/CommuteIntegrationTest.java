package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    public void parsesAllThreeAuthenticatedServerFixturesWithoutInventingFares() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Bundle args = InstrumentationRegistry.getArguments();
        String email = args.getString("email");
        String password = args.getString("password");
        assumeTrue(email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password),
                30, TimeUnit.SECONDS);
        CommuteRepository repository = new CommuteRepository();
        CommuteSummary perTrip = Tasks.await(repository.preview("per-trip"), 30, TimeUnit.SECONDS);
        assertEquals("walking", perTrip.selectedMode);
        assertEquals(Long.valueOf(250), perTrip.options.get(1).perTripCostSen);
        assertEquals("Synthetic routing fixture", perTrip.options.get(0).routeSource);
        assertEquals("Synthetic warning: use designated footpaths.", perTrip.options.get(0).warnings.get(0));
        assertEquals("Synthetic fare assumption", perTrip.options.get(1).costSource);
        assertEquals(Boolean.TRUE, perTrip.options.get(3).trafficFallback);
        CommuteSummary noFare = Tasks.await(repository.preview("no-known-fare"), 30, TimeUnit.SECONDS);
        assertNull(noFare.selectedMode);
        assertNull(noFare.options.get(1).perTripCostSen);
        assertEquals(Long.valueOf(1800), noFare.options.get(1).durationSeconds);
        CommuteSummary missing = Tasks.await(repository.preview("missing-data"), 30, TimeUnit.SECONDS);
        assertEquals("missing_schedule", missing.options.get(1).reason);
        assertNull(missing.options.get(2).perTripCostSen);
        assertTrue(missing.comparisonLimited);
    }
}
