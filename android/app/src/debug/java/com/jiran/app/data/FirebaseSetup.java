package com.jiran.app.data;

import android.content.Context;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;

/** Debug builds always use demo resources, even if real config is present. */
public final class FirebaseSetup {
    private FirebaseSetup() { }

    public static boolean prepareConnection(Activity activity) {
        if (Build.VERSION.SDK_INT >= 37
                && activity.checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK")
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_LOCAL_NETWORK"}, 1);
            return false;
        }
        return true;
    }

    public static void initialize(Context context) {
        // The auto-init provider may have initialized a locally supplied real app.
        for (FirebaseApp app : FirebaseApp.getApps(context)) {
            app.delete();
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("demo-jiran")
                .setApplicationId("1:1234567890:android:0000000000000000")
                // Synthetic, non-credential value satisfying the SDK's format check.
                .setApiKey("A00000000000000000000000000000000000000")
                .setStorageBucket("demo-jiran.appspot.com")
                .build();
        FirebaseApp.initializeApp(context, options);
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
        FirebaseFunctions.getInstance("us-central1").useEmulator("10.0.2.2", 5001);
        FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
    }
}
