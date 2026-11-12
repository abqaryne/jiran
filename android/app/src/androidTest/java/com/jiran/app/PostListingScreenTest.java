package com.jiran.app;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.jiran.app.data.ListingPhotoReader;
import com.jiran.app.ui.listing.BuildingPickerDialog;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
/** Local synthetic image and fake gateway only: no network, account or real personal data. */
@RunWith(AndroidJUnit4.class)
public final class PostListingScreenTest {
    @Test public void preparesPhotoWithoutGpsAndSavesSampleForm() throws Exception {
        assumeTrue(BuildConfig.USE_FIREBASE_EMULATORS && android.os.Build.VERSION.SDK_INT >= 29);
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        ContentResolver resolver = instrumentation.getTargetContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "jiran-synthetic-post-test.jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        assertTrue(uri != null);
        Activity activity = null;
        Instrumentation.ActivityMonitor monitor = null;
        try {
            Bitmap image = Bitmap.createBitmap(2000, 1000, Bitmap.Config.ARGB_8888);
            image.eraseColor(android.graphics.Color.LTGRAY);
            try (OutputStream output = resolver.openOutputStream(uri)) {
                assertTrue(image.compress(Bitmap.CompressFormat.JPEG, 90, output));
            } finally {
                image.recycle(); }
            try (ParcelFileDescriptor file = resolver.openFileDescriptor(uri, "rw")) {
                ExifInterface exif = new ExifInterface(file.getFileDescriptor());
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "0/1,0/1,0/1");
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
                exif.saveAttributes();
            }
            byte[] bytes = ListingPhotoReader.read(resolver, uri);
            ExifInterface prepared = new ExifInterface(new ByteArrayInputStream(bytes));
            assertNull(prepared.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            assertTrue(result.getHeight() > result.getWidth());
            assertTrue(result.getHeight() <= 1600);
            result.recycle();
            assertTrue(bytes.length <= 2 * 1024 * 1024);
            activity = instrumentation.startActivitySync(new Intent().setClassName(
            instrumentation.getTargetContext(), "com.jiran.app.ui.gallery.PostPreviewActivity")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Activity screen = activity;
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(() -> screen.findViewById(R.id.save).performClick());
            assertTrue(status(instrumentation, screen).contains("Check the highlighted"));
            instrumentation.runOnMainSync(() -> {
                ((TextView) screen.findViewById(R.id.building_address)).setText("Jalan Fiksi 1");
                ((TextView) screen.findViewById(R.id.address)).setText("Fictional Unit 1");
                ((TextView) screen.findViewById(R.id.rent)).setText("10.01");
                ((TextView) screen.findViewById(R.id.deposit)).setText("0");
                Bundle selection = new Bundle();
                selection.putString("areaId", "sample_a");
                selection.putString("areaName", "Sample area A");
                selection.putString("buildingName", "Fictional Building");
                selection.putDouble("areaLatitude", 0);
                selection.putDouble("areaLongitude", 0);
                selection.putString("coordinateSource", "area_center_estimate");
                selection.putBoolean("sampleData", true);
                ((FragmentActivity) screen).getSupportFragmentManager().getFragments().get(0)
                    .getChildFragmentManager().setFragmentResult(BuildingPickerDialog.RESULT_KEY, selection);
            });
            IntentFilter pickerFilter = new IntentFilter(Intent.ACTION_OPEN_DOCUMENT);
            pickerFilter.addCategory(Intent.CATEGORY_OPENABLE);
            pickerFilter.addDataType("*/*");
            monitor = instrumentation.addMonitor(pickerFilter,
            new Instrumentation.ActivityResult(Activity.RESULT_OK,
            new Intent().setData(uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)), true);
            instrumentation.runOnMainSync(() -> screen.findViewById(R.id.add_photo).performClick());
            long deadline = SystemClock.elapsedRealtime() + 10000;
            while (!hasPhoto(instrumentation, screen) && SystemClock.elapsedRealtime() < deadline) {
                SystemClock.sleep(100);
            }
            assertTrue("Selected photo reaches the form", hasPhoto(instrumentation, screen));
            instrumentation.runOnMainSync(() -> screen.findViewById(R.id.save).performClick());
            deadline = SystemClock.elapsedRealtime() + 10000;
            while (!status(instrumentation, screen).contains("Draft saved") && SystemClock.elapsedRealtime() < deadline) {
                SystemClock.sleep(100);
            }
            String text = status(instrumentation, screen);
            assertTrue(text, text.contains("Draft saved privately"));
            assertTrue(text, text.contains("nothing is uploaded or saved"));
            instrumentation.runOnMainSync(() -> {
                assertFalse(screen.findViewById(R.id.save).isEnabled());
                assertEquals("10.01", ((TextView) screen.findViewById(R.id.rent)).getText().toString());
            });
        } finally {
            if (monitor != null) {
                instrumentation.removeMonitor(monitor); }
            if (activity != null) {
                Activity screen = activity;
                instrumentation.runOnMainSync(screen::finish);
            }
            resolver.delete(uri, null, null);
        }
    }
    private static String status(Instrumentation instrumentation, Activity screen) {
        AtomicReference<String> value = new AtomicReference<>();
        instrumentation.runOnMainSync(() -> value.set(((TextView) screen.findViewById(R.id.banner_text)).getText().toString()));
        return value.get();
    }
    private static boolean hasPhoto(Instrumentation instrumentation, Activity screen) {
        AtomicReference<Boolean> value = new AtomicReference<>(false);
        instrumentation.runOnMainSync(() -> value.set(
        ((android.widget.LinearLayout) screen.findViewById(R.id.photos)).getChildCount() > 0));
        return value.get();
    }
}
