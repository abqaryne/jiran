package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.jiran.app.data.EmailPasswordRepository;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Requires the disposable verified account created by emulator-smoke.cjs. */
@RunWith(AndroidJUnit4.class)
public final class FirebaseConnectionTest {
    @Test
    public void androidSdkCallsLocalFunctionWithAuthenticatedIdentity() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Bundle arguments = InstrumentationRegistry.getArguments();
        String email = arguments.getString("email");
        String password = arguments.getString("password");
        assumeTrue("Run emulator-smoke.cjs --android for the verified fixture",
                email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password),
                30, TimeUnit.SECONDS);
        Map<?, ?> result = (Map<?, ?>) Tasks.await(
                FirebaseFunctions.getInstance("us-central1")
                        .getHttpsCallable("sessionCheck")
                        .call(Collections.singletonMap("userId", "victim")),
                30, TimeUnit.SECONDS).getData();
        assertEquals("ok", result.get("status"));
        assertEquals(FirebaseAuth.getInstance().getCurrentUser().getUid(), result.get("uid"));
        assertEquals(true, Tasks.await(new EmailPasswordRepository().checkFirestoreConnection(),
                30, TimeUnit.SECONDS));
    }
}
