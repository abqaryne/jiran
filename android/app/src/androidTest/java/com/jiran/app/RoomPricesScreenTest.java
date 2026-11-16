package com.jiran.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.view.inspector.WindowInspector;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Offline sample only; verifies the same editor used by the product destination. */
@RunWith(AndroidJUnit4.class)
public final class RoomPricesScreenTest {
    @android.annotation.TargetApi(29)
    @androidx.test.filters.SdkSuppress(minSdkVersion = 29)
    @Test public void sampleSplitOccupancyOverAllocationRotationAndSave() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = new Intent().setClassName(instrumentation.getTargetContext(),
                "com.jiran.app.ui.gallery.RoomPricesPreviewActivity");
        try (ActivityScenario<FragmentActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(screen -> screen.findViewById(R.id.room_prices_apply).performClick());
            instrumentation.waitForIdleSync();
            scenario.onActivity(screen -> {
                boolean clicked = false;
                for (View root : WindowInspector.getGlobalWindowViews()) {
                    View confirm = root.findViewById(android.R.id.button1);
                    if (confirm != null) { clicked |= confirm.performClick(); }
                }
                assertTrue("Split confirmation is shown", clicked);
            });
            instrumentation.waitForIdleSync();
            scenario.onActivity(screen -> {
                assertEquals("650.00", price(screen, 0).getText().toString());
                assertEquals("500.00", price(screen, 1).getText().toString());
                assertEquals("350.00", price(screen, 2).getText().toString());
                row(screen, 0).findViewById(R.id.room_editor_occupied).performClick();
                price(screen, 0).setText("650.01");
                assertFalse(screen.findViewById(R.id.room_prices_save).isEnabled());
                TextView error = screen.findViewById(R.id.room_prices_remaining).findViewById(R.id.banner_text);
                assertTrue(error.getText().toString().contains("Over-allocated by RM 0.01"));
            });
            screenshot(instrumentation, scenario, "e2.2-rooms-full-review.png");
            scenario.recreate();
            scenario.onActivity(screen -> {
                assertEquals("650.01", price(screen, 0).getText().toString());
                assertTrue(((com.google.android.material.chip.Chip) row(screen, 0)
                        .findViewById(R.id.room_editor_occupied)).isChecked());
                assertFalse(screen.findViewById(R.id.room_prices_save).isEnabled());
                price(screen, 0).setText("650");
                assertTrue(screen.findViewById(R.id.room_prices_save).isEnabled());
                screen.findViewById(R.id.room_prices_save).performClick();
                TextView status = screen.findViewById(R.id.room_prices_status).findViewById(R.id.banner_text);
                assertTrue(status.getText().toString().contains("Rooms saved"));
                assertTrue(status.getText().toString().contains("Sample preview"));
                assertEquals("Done", ((TextView) screen.findViewById(R.id.room_prices_save)).getText().toString());
            });
        }
    }

    private static View row(FragmentActivity screen, int index) {
        return ((LinearLayout) screen.findViewById(R.id.room_price_rows)).getChildAt(index);
    }
    private static TextView price(FragmentActivity screen, int index) {
        return row(screen, index).findViewById(R.id.room_editor_price);
    }

    @android.annotation.TargetApi(29)
    private static void screenshot(Instrumentation instrumentation, ActivityScenario<FragmentActivity> scenario,
            String name) throws Exception {
        if (!"true".equals(InstrumentationRegistry.getArguments().getString("roomPricesScreenshots"))) { return; }
        instrumentation.getUiAutomation().waitForIdle(500, 5000);
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, name);
        values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Jiran");
        android.content.ContentResolver resolver = instrumentation.getTargetContext().getContentResolver();
        android.net.Uri uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        java.util.concurrent.atomic.AtomicReference<android.graphics.Bitmap> rendered =
                new java.util.concurrent.atomic.AtomicReference<>();
        // Capture the actual laid-out scroll content, including the remaining/error banner below the fold.
        scenario.onActivity(screen -> {
            View content = screen.findViewById(R.id.room_prices_content);
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(content.getWidth(),
                    content.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            canvas.drawColor(androidx.core.content.ContextCompat.getColor(screen, R.color.color_bg_surface));
            content.draw(canvas);
            rendered.set(bitmap);
        });
        android.graphics.Bitmap screenshot = rendered.get();
        try (java.io.OutputStream output = resolver.openOutputStream(uri)) {
            assertTrue(screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output));
        } finally {
            screenshot.recycle();
        }
    }
}
