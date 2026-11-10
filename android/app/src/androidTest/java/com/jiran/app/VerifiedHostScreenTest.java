package com.jiran.app;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.ui.auth.AuthActivity;
import com.jiran.app.ui.shell.ShellActivity;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

/** Uses only disposable demo-jiran Auth accounts; verification is simulated by the local emulator. */
@RunWith(AndroidJUnit4.class)
public final class VerifiedHostScreenTest {
    private Instrumentation instrumentation;
    private FirebaseAuth auth;
    private ShellActivity shell;
    private Activity destination;
    private Instrumentation.ActivityMonitor monitor;

    @Before public void setup() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        instrumentation = InstrumentationRegistry.getInstrumentation();
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            instrumentation.getUiAutomation().executeShellCommand(
                    "pm grant jiran.com android.permission.ACCESS_LOCAL_NETWORK").close();
        }
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        monitor = instrumentation.addMonitor(AuthActivity.class.getName(), null, false);
    }

    @After public void cleanup() {
        if (instrumentation == null) {
            return;
        }
        instrumentation.removeMonitor(monitor);
        instrumentation.runOnMainSync(() -> {
            if (shell != null) {
                shell.finish();
            }
            if (destination != null) {
                destination.finish();
            }
        });
        auth.signOut();
    }

    @Test public void signedOutCannotOpenPost() {
        openPost();
        expectAuth(R.id.welcomeFragment);
    }

    @Test public void unverifiedStudentReturnsToVerifyEmail() throws Exception {
        account("siswa.um.edu.my", false);
        openPost();
        expectAuth(R.id.verifyEmailFragment);
    }

    @Test public void verifiedStaffCannotOpenPost() throws Exception {
        account("um.edu.my", true);
        openPost();
        expectAuth(R.id.welcomeFragment);
        assertNull(auth.getCurrentUser());
    }

    @Test public void verifiedStudentCanOpenButSignOutClosesForm() throws Exception {
        account("siswa.um.edu.my", true);
        openPost();
        instrumentation.runOnMainSync(() -> assertNotNull(shell.findViewById(R.id.address)));
        auth.signOut();
        expectAuth(R.id.welcomeFragment);
    }

    private void openPost() {
        shell = (ShellActivity) instrumentation.startActivitySync(new Intent(
                instrumentation.getTargetContext(), ShellActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(() -> shell.findViewById(R.id.post_button).performClick());
        instrumentation.waitForIdleSync();
    }

    private void expectAuth(int expected) {
        destination = instrumentation.waitForMonitorWithTimeout(monitor, 10000);
        assertNotNull("Post must redirect to the existing authentication flow", destination);
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(() -> {
            NavHostFragment host = (NavHostFragment) ((AuthActivity) destination)
                    .getSupportFragmentManager().findFragmentById(R.id.auth_nav_host);
            assertNotNull(host);
            assertEquals(expected, host.getNavController().getCurrentDestination().getId());
        });
    }

    private void account(String domain, boolean verified) throws Exception {
        String email = "host-check-" + System.nanoTime() + "@" + domain;
        Tasks.await(auth.createUserWithEmailAndPassword(email, "local-only-password-123"), 30, TimeUnit.SECONDS);
        if (!verified) {
            return;
        }
        FirebaseUser user = auth.getCurrentUser();
        HttpURLConnection connection = (HttpURLConnection) new URL(
                "http://10.0.2.2:9099/identitytoolkit.googleapis.com/v1/accounts:update?key=fake-key").openConnection();
        try {
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer owner");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            byte[] body = new JSONObject().put("localId", user.getUid()).put("emailVerified", true)
                    .toString().getBytes(StandardCharsets.UTF_8);
            try (java.io.OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
        Tasks.await(user.reload(), 30, TimeUnit.SECONDS);
        Tasks.await(user.getIdToken(true), 30, TimeUnit.SECONDS);
    }
}
