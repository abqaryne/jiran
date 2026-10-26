package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Read-only live probe: no real account creation, email delivery or data writes. */
@RunWith(AndroidJUnit4.class)
public final class LiveFirebaseConnectionTest {
    @Test
    public void liveSdkUsesJiranProjectAndEnforcesAuthentication() throws Exception {
        assumeFalse(BuildConfig.USE_FIREBASE_EMULATORS);
        assertEquals("jiran.com", BuildConfig.APPLICATION_ID);
        assertEquals("jiran-mad", FirebaseApp.getInstance().getOptions().getProjectId());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        try {
            Tasks.await(auth.signInWithEmailAndPassword(
                    "setup-probe-" + UUID.randomUUID() + "@example.invalid", "not-a-real-password"),
                    30, TimeUnit.SECONDS);
            fail("Nonexistent account must not authenticate");
        } catch (ExecutionException expected) {
            assertTrue(expected.getCause() instanceof FirebaseAuthException);
            String code = ((FirebaseAuthException) expected.getCause()).getErrorCode();
            assertTrue("Unexpected Auth error: " + code, Arrays.asList("ERROR_INVALID_CREDENTIAL",
                    "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD").contains(code));
        }
        try {
            Tasks.await(FirebaseFirestore.getInstance().collection("_connectionChecks")
                    .document("setup-probe").get(Source.SERVER), 30, TimeUnit.SECONDS);
            fail("Anonymous Firestore access must be denied");
        } catch (ExecutionException expected) {
            assertTrue(expected.getCause() instanceof FirebaseFirestoreException);
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED,
                    ((FirebaseFirestoreException) expected.getCause()).getCode());
        }
    }
}
