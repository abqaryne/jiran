package com.jiran.app.data;

import android.content.Context;
import android.app.Activity;
import com.google.firebase.FirebaseApp;

/** Release relies exclusively on the team's registered Firebase application. */
public final class FirebaseSetup {
    private FirebaseSetup() { }

    public static boolean prepareConnection(Activity activity) {
        return false;
    }

    public static void initialize(Context context) {
        if (FirebaseApp.initializeApp(context) == null) {
            throw new IllegalStateException("Firebase configuration is missing");
        }
    }
}
