package com.jiran.app.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.jiran.app.data.FirebaseAccountService;
import com.jiran.app.ui.shell.ShellActivity;

/**
 * The app's front door. It has no screen: it reads the saved session and opens Welcome, Verify email or the
 * product, then finishes itself so Back never returns here.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartRoute route = StartRoute.of(new FirebaseAccountService().session());
        Intent next;
        if (route == StartRoute.PRODUCT) {
            next = new Intent(this, ShellActivity.class);
        } else {
            next = new Intent(this, AuthActivity.class);
            next.putExtra(AuthActivity.EXTRA_OPEN_VERIFY_EMAIL, route == StartRoute.VERIFY_EMAIL);
        }
        startActivity(next);
        finish();
    }
}
