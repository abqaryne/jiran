package com.jiran.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.tasks.Tasks;
import com.jiran.app.data.EmailPasswordRepository;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Creates disposable accounts only in the local Auth emulator. */
@RunWith(AndroidJUnit4.class)
public final class EmailPasswordFlowTest {
    @Test
    public void createsSignsInAndResetsLocalAccountWithoutUnverifiedDatabaseAccess() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        EmailPasswordRepository repository = new EmailPasswordRepository();
        repository.signOut();
        String email = "android-" + System.nanoTime() + "@example.com";
        String password = "local-test-password-123";
        Tasks.await(repository.register(email, password), 30, TimeUnit.SECONDS);
        assertFalse(repository.currentUser().isEmailVerified());
        repository.signOut();
        assertNull(repository.currentUser());
        assertFalse(Tasks.await(repository.signIn(email, password), 30, TimeUnit.SECONDS));
        try {
            Tasks.await(repository.checkFirestoreConnection(), 30, TimeUnit.SECONDS);
            fail("Unverified user must not access Firestore");
        } catch (ExecutionException expected) {
            assertTrue(expected.getCause() instanceof SecurityException);
        }
        Tasks.await(repository.resetPassword(email), 30, TimeUnit.SECONDS);
        repository.signOut();
    }
}
