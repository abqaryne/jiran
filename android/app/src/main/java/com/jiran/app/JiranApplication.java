package com.jiran.app;

import android.app.Application;
import com.jiran.app.data.FirebaseSetup;

/** Initializes the environment before any screen uses Firebase. */
public final class JiranApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseSetup.initialize(this);
    }
}
