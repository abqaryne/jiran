package com.jiran.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.data.EmailPasswordRepository;
import com.jiran.app.data.FirebaseSetup;
import com.jiran.app.util.EmailPasswordInput;

/** Backend setup flow, to be integrated into Ary's product authentication UI. */
public final class EmailPasswordActivity extends Activity {
    private EmailPasswordRepository repository;
    private EditText email;
    private EditText password;
    private TextView status;
    private Button register;
    private Button signIn;
    private Button reset;
    private Button verify;
    private Button refresh;
    private Button check;
    private Button signOut;
    private boolean busy;
    private final com.google.firebase.auth.FirebaseAuth.AuthStateListener authListener =
            ignored -> renderSession();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_email_password);
        repository = new EmailPasswordRepository();
        email = findViewById(R.id.auth_email);
        password = findViewById(R.id.auth_password);
        status = findViewById(R.id.auth_status);
        register = findViewById(R.id.auth_register);
        signIn = findViewById(R.id.auth_sign_in);
        reset = findViewById(R.id.auth_reset);
        verify = findViewById(R.id.auth_verify);
        refresh = findViewById(R.id.auth_refresh);
        check = findViewById(R.id.auth_firestore);
        signOut = findViewById(R.id.auth_sign_out);
        TextView environment = findViewById(R.id.auth_environment);
        environment.setText(BuildConfig.USE_FIREBASE_EMULATORS
                ? R.string.auth_environment_local : R.string.auth_environment_live);
        renderSession();

        register.setOnClickListener(view -> {
            if (validate(true, true)) {
                run(repository.register(emailValue(), passwordValue()), R.string.auth_registered);
            }
        });
        signIn.setOnClickListener(view -> {
            if (validate(true, false)) {
                run(repository.signIn(emailValue(), passwordValue()), R.string.auth_signed_in);
            }
        });
        reset.setOnClickListener(view -> {
            if (validate(false, false)) {
                run(repository.resetPassword(emailValue()), R.string.auth_reset_sent);
            }
        });
        verify.setOnClickListener(view -> {
            if (networkReady()) {
                run(repository.sendVerification(), R.string.auth_verification_sent);
            }
        });
        refresh.setOnClickListener(view -> {
            if (networkReady()) {
                run(repository.refreshVerifiedStatus(), R.string.auth_refreshed);
            }
        });
        check.setOnClickListener(view -> {
            if (networkReady()) {
                run(repository.checkFirestoreConnection(), R.string.auth_firestore_ok);
            }
        });
        signOut.setOnClickListener(view -> {
            repository.signOut();
            password.setText("");
            status.setText(R.string.auth_signed_out);
            renderSession();
        });
    }

    private String emailValue() {
        return email.getText().toString().trim();
    }

    private String passwordValue() {
        return password.getText().toString();
    }

    private boolean validate(boolean needsPassword, boolean creating) {
        if (!EmailPasswordInput.isValidEmail(emailValue())) {
            email.setError(getString(R.string.auth_invalid_email));
            return false;
        }
        if (needsPassword && !EmailPasswordInput.isValidPassword(passwordValue(), creating)) {
            password.setError(getString(creating
                    ? R.string.auth_short_password : R.string.auth_empty_password));
            return false;
        }
        return networkReady();
    }

    private boolean networkReady() {
        if (BuildConfig.USE_FIREBASE_EMULATORS && !FirebaseSetup.prepareConnection(this)) {
            status.setText(R.string.allow_local_network);
            return false;
        }
        return true;
    }

    private void run(Task<?> task, int successMessage) {
        busy = true;
        renderSession();
        status.setText(R.string.auth_working);
        // Keep completion attached across onStop (for example, opening the inbox).
        task.addOnCompleteListener(completed -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            busy = false;
            if (completed.isSuccessful()) {
                password.setText("");
                status.setText(successMessage);
            } else {
                status.setText(R.string.auth_failed);
            }
            renderSession();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        com.google.firebase.auth.FirebaseAuth.getInstance().addAuthStateListener(authListener);
        renderSession();
    }

    @Override
    protected void onStop() {
        com.google.firebase.auth.FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        super.onStop();
    }

    private void renderSession() {
        FirebaseUser user = repository.currentUser();
        boolean signedIn = user != null;
        boolean verified = signedIn && user.isEmailVerified();
        email.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        password.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        register.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        signIn.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        reset.setVisibility(signedIn ? View.GONE : View.VISIBLE);
        verify.setVisibility(signedIn && !verified ? View.VISIBLE : View.GONE);
        refresh.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        check.setVisibility(verified ? View.VISIBLE : View.GONE);
        signOut.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        for (Button button : new Button[]{register, signIn, reset, verify, refresh, check, signOut}) {
            button.setEnabled(!busy);
        }
        TextView session = findViewById(R.id.auth_session);
        session.setText(!signedIn ? getString(R.string.auth_no_session)
                : getString(verified ? R.string.auth_verified_session : R.string.auth_unverified_session,
                        user.getEmail()));
    }
}
