package com.jiran.app.ui.auth;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.jiran.app.R;

/**
 * Hosts the Starting Flow screens: Welcome, Sign up, Verify email, Log in and Reset password. Screens are
 * Fragments in the graph {@code nav_auth}.
 */
public class AuthActivity extends AppCompatActivity {

    /** Set by the launcher when the saved account is not verified yet: open Verify email straight away. */
    public static final String EXTRA_OPEN_VERIFY_EMAIL = "open_verify_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        // Keep content clear of the system bars and lift it above the keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth_root), (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        if (savedInstanceState == null && getIntent().getBooleanExtra(EXTRA_OPEN_VERIFY_EMAIL, false)) {
            Fragment host = getSupportFragmentManager().findFragmentById(R.id.auth_nav_host);
            if (host instanceof NavHostFragment) {
                ((NavHostFragment) host).getNavController().navigate(R.id.verifyEmailFragment);
            }
        }
    }
}
