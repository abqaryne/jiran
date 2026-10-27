package com.jiran.app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.jiran.app.ui.MatchPreviewActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Native binding after a real verified local SDK request, not final product UI. */
@RunWith(AndroidJUnit4.class)
public final class MatchPreviewTest {
    @Test
    public void displaysSampleScoreRuleReasonsAndWeights() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Bundle args = InstrumentationRegistry.getArguments();
        String email = args.getString("email");
        String password = args.getString("password");
        assumeTrue(email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password), 30, TimeUnit.SECONDS);
        Activity activity = instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),
                MatchPreviewActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        try {
            instrumentation.runOnMainSync(() -> ((Button) activity.findViewById(R.id.match_calculate)).performClick());
            AtomicReference<String> snapshot = new AtomicReference<>("");
            long deadline = SystemClock.elapsedRealtime() + 30000;
            while (SystemClock.elapsedRealtime() < deadline) {
                instrumentation.runOnMainSync(() -> snapshot.set(text(activity.findViewById(android.R.id.content))));
                if (snapshot.get().contains("sample-a")) {
                    break;
                }
                SystemClock.sleep(100);
            }
            String visible = snapshot.get();
            assertTrue(visible, visible.contains("sample-a — 100%"));
            assertTrue(visible, visible.contains("sample-b — 50%"));
            assertTrue(visible, visible.contains("Sleep time: Similar"));
            assertTrue(visible, visible.contains("Rule-based reasons (no AI)"));
            assertTrue(visible, visible.contains("Overlapping class days: Mon, Wed"));
            assertTrue(visible, visible.contains("Sleep time — weight 20%, similarity 100%"));
            if ("true".equals(args.getString("capturePreview"))) {
                capture(instrumentation, activity);
            }
            loadScenario(instrumentation, activity, 1);
            String missing = awaitText(instrumentation, activity, "Not enough answers");
            assertTrue(missing, missing.contains("Missing answers: Smoking"));
            assertTrue(missing, missing.contains("Overlapping class days: Unknown"));
            loadScenario(instrumentation, activity, 2);
            String empty = awaitText(instrumentation, activity, "No sample matches");
            assertTrue(empty, empty.contains("No sample matches"));
            instrumentation.runOnMainSync(() -> assertTrue(
                    ((ViewGroup) activity.findViewById(R.id.match_rows)).getChildCount() == 0));
        } finally {
            instrumentation.runOnMainSync(activity::finish);
        }
    }

    private void loadScenario(Instrumentation instrumentation, Activity activity, int position) {
        instrumentation.runOnMainSync(() -> {
            ((Spinner) activity.findViewById(R.id.match_scenario)).setSelection(position);
            ((Button) activity.findViewById(R.id.match_calculate)).performClick();
        });
    }

    private String awaitText(Instrumentation instrumentation, Activity activity, String expected) {
        AtomicReference<String> snapshot = new AtomicReference<>("");
        long deadline = SystemClock.elapsedRealtime() + 30000;
        while (SystemClock.elapsedRealtime() < deadline) {
            instrumentation.runOnMainSync(() -> snapshot.set(text(activity.findViewById(android.R.id.content))));
            if (snapshot.get().contains(expected)) {
                return snapshot.get();
            }
            SystemClock.sleep(100);
        }
        return snapshot.get();
    }

    private String text(View view) {
        StringBuilder output = new StringBuilder();
        if (view instanceof TextView) {
            output.append(((TextView) view).getText()).append('\n');
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                output.append(text(group.getChildAt(index)));
            }
        }
        return output.toString();
    }

    private void capture(Instrumentation instrumentation, Activity activity) throws Exception {
        instrumentation.waitForIdleSync();
        AtomicReference<Bitmap> image = new AtomicReference<>();
        instrumentation.runOnMainSync(() -> {
            View page = (View) activity.findViewById(R.id.match_rows).getParent();
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            page.draw(canvas);
            image.set(bitmap);
        });
        Bitmap bitmap = image.get();
        try (FileOutputStream file = new FileOutputStream(new File(activity.getExternalFilesDir(null), "match-preview.png"))) {
            assertTrue("Could not capture native preview", bitmap.compress(Bitmap.CompressFormat.PNG, 100, file));
        } finally {
            bitmap.recycle();
        }
    }
}
