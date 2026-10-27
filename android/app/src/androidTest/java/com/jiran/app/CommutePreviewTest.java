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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.jiran.app.ui.CommutePreviewActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Proves provenance and fallback reach actual native rows after a real callable. */
@RunWith(AndroidJUnit4.class)
public final class CommutePreviewTest {
    @Test
    public void displaysSourcesWarningsAndFallbackFromVerifiedLocalCallable() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS);
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Bundle args = InstrumentationRegistry.getArguments();
        String email = args.getString("email");
        String password = args.getString("password");
        assumeTrue(email != null && password != null);
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password), 30, TimeUnit.SECONDS);
        Activity activity = instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),
                CommutePreviewActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        try {
            instrumentation.runOnMainSync(() -> ((Button) activity.findViewById(R.id.commute_calculate)).performClick());
            AtomicReference<String> snapshot = new AtomicReference<>("");
            long deadline = SystemClock.elapsedRealtime() + 30000;
            while (SystemClock.elapsedRealtime() < deadline) {
                instrumentation.runOnMainSync(() -> {
                    LinearLayout rows = activity.findViewById(R.id.commute_options);
                    StringBuilder text = new StringBuilder();
                    text.append(((TextView) activity.findViewById(R.id.commute_status)).getText());
                    for (int index = 0; index < rows.getChildCount(); index++) {
                        text.append('\n').append(((TextView) rows.getChildAt(index)).getText());
                    }
                    snapshot.set(text.toString());
                });
                if (snapshot.get().contains("Synthetic routing fixture")) {
                    break;
                }
                SystemClock.sleep(100);
            }
            String visible = snapshot.get();
            assertTrue(visible, visible.contains("Synthetic routing fixture"));
            assertTrue(visible, visible.contains("Synthetic fare assumption"));
            assertTrue(visible, visible.contains("Synthetic warning: use designated footpaths."));
            assertTrue(visible, visible.contains("Provider fallback used"));
            assertTrue(visible, visible.contains("RM 612.00"));
            if ("true".equals(args.getString("capturePreview"))) {
                capturePreview(instrumentation, activity);
            }
        } finally {
            instrumentation.runOnMainSync(activity::finish);
        }
    }

    private void capturePreview(Instrumentation instrumentation, Activity activity) throws Exception {
        instrumentation.waitForIdleSync();
        AtomicReference<Bitmap> image = new AtomicReference<>();
        instrumentation.runOnMainSync(() -> {
            View page = (View) activity.findViewById(R.id.commute_options).getParent();
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            page.draw(canvas);
            image.set(bitmap);
        });
        Bitmap bitmap = image.get();
        try (FileOutputStream file = new FileOutputStream(new File(activity.getExternalFilesDir(null),
                "commute-preview.png"))) {
            assertTrue("Could not capture native preview", bitmap.compress(Bitmap.CompressFormat.PNG, 100, file));
        } finally {
            bitmap.recycle();
        }
    }
}
