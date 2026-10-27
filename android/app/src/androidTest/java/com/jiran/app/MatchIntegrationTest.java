package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.jiran.app.data.MatchRepository;
import com.jiran.app.data.MatchSummary;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Real SDK to local deterministic score function and immutable Java DTO. */
@RunWith(AndroidJUnit4.class)
public final class MatchIntegrationTest {
    @Test
    public void parsesRankedMissingAndEmptyFixturesThroughVerifiedSdk() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Bundle args = InstrumentationRegistry.getArguments();
        String email = args.getString("email");
        String password = args.getString("password");
        assumeTrue(email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password), 30, TimeUnit.SECONDS);
        MatchRepository repository = new MatchRepository();
        MatchSummary ranked = Tasks.await(repository.preview("ranked"), 30, TimeUnit.SECONDS);
        assertEquals("sample-a", ranked.matches.get(0).candidateId);
        assertEquals(Integer.valueOf(10000), ranked.matches.get(0).scoreBasisPoints);
        assertEquals(Integer.valueOf(5000), ranked.matches.get(1).scoreBasisPoints);
        assertEquals("similar", ranked.matches.get(0).reasons.get(0).code);
        MatchSummary missing = Tasks.await(repository.preview("missing-answers"), 30, TimeUnit.SECONDS);
        assertNull(missing.matches.get(0).scoreBasisPoints);
        assertEquals("smoking", missing.matches.get(0).missingFactors.get(0));
        assertNull(missing.matches.get(0).overlappingClassDays);
        assertEquals(0, Tasks.await(repository.preview("empty"), 30, TimeUnit.SECONDS).matches.size());
    }
}
