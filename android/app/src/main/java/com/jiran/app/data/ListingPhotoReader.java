package com.jiran.app.data;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/** Bounded local decoding and JPEG re-encoding strips EXIF (including GPS) before upload. */
public final class ListingPhotoReader {
    private ListingPhotoReader() { }
    public static byte[] read(ContentResolver resolver, Uri uri) throws IOException {
        if (!"content".equals(uri.getScheme())) {
            throw new IOException("local-image-required");
        }
        ByteArrayOutputStream source = new ByteArrayOutputStream();
        try (InputStream input = resolver.openInputStream(uri)) {
            if (input == null) {
                throw new IOException("image-unavailable");
            }
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                // Defensive input bound, independent of the 2 MiB upload limit.
                if (source.size() + count > 20 * 1024 * 1024) {
                    throw new IOException("image-too-large");
                }
                source.write(buffer, 0, count);
            }
        }
        byte[] original = source.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(original, 0, original.length, options);
        if (options.outWidth <= 0 || options.outHeight <= 0
            || !("image/jpeg".equals(options.outMimeType) || "image/png".equals(options.outMimeType))) {
            throw new IOException("jpeg-or-png-required");
        }
        options.inSampleSize = 1;
        while (Math.max(options.outWidth, options.outHeight) / options.inSampleSize > 1600) {
            options.inSampleSize *= 2;
        }
        options.inJustDecodeBounds = false;
        Bitmap decoded = BitmapFactory.decodeByteArray(original, 0, original.length, options);
        if (decoded == null) {
            throw new IOException("invalid-image");
        }
        Bitmap rotated = decoded;
        try {
            ExifInterface exif = new ExifInterface(new ByteArrayInputStream(original));
            Matrix matrix = new Matrix();
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case 2: matrix.setScale(-1, 1);
                break;
                case 3: matrix.setRotate(180);
                break;
                case 4: matrix.setScale(1, -1);
                break;
                case 5: matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
                case 6: matrix.setRotate(90);
                break;
                case 7: matrix.setRotate(270);
                matrix.postScale(-1, 1);
                break;
                case 8: matrix.setRotate(270);
                break;
                default: break;
            }
            rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), matrix, true);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (!rotated.compress(Bitmap.CompressFormat.JPEG, 85, output)
                || output.size() > 2 * 1024 * 1024) {
                throw new IOException("image-too-large");
            }
            return output.toByteArray();
        } finally {
            if (rotated != decoded) {
                rotated.recycle();
            }
            decoded.recycle();
        }
    }
}
