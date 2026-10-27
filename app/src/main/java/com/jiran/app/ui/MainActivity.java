package com.jiran.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.util.LocalCredentials;
import com.jiran.app.data.FirebaseSetup;
import java.util.Collections;
import java.util.Map;

/** Temporary developer shell; Ary owns the shared product UI. */
public final class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            startActivity(new Intent(this, EmailPasswordActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Button check = findViewById(R.id.check_connection);
        TextView status = findViewById(R.id.connection_status);
        EditText email = findViewById(R.id.local_email);
        EditText password = findViewById(R.id.local_password);
        check.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        email.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        password.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        findViewById(R.id.open_auth_setup).setOnClickListener(view ->
                startActivity(new Intent(this, EmailPasswordActivity.class)));
        findViewById(R.id.open_commute_preview).setOnClickListener(view ->
                startActivity(new Intent(this, CommutePreviewActivity.class)));
        findViewById(R.id.open_match_preview).setOnClickListener(view ->
                startActivity(new Intent(this, MatchPreviewActivity.class)));
        check.setOnClickListener(view -> {
            if (!LocalCredentials.isComplete(email.getText().toString(),
                    password.getText().toString())) {
                status.setText(R.string.enter_local_credentials);
                return;
            }
            if (!FirebaseSetup.prepareConnection(this)) {
                status.setText(R.string.allow_local_network);
                return;
            }
            check.setEnabled(false);
            status.setText(R.string.checking_connection);
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.getText().toString().trim(),
                            password.getText().toString())
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return FirebaseFunctions.getInstance(BuildConfig.FUNCTIONS_REGION)
                                .getHttpsCallable("sessionCheck").call(Collections.emptyMap());
                    })
                    .addOnCompleteListener(this, task -> {
                        check.setEnabled(true);
                        if (task.isSuccessful()) {
                            Map<?, ?> data = (Map<?, ?>) task.getResult().getData();
                            status.setText(getString(R.string.connection_ok, data.get("uid")));
                        } else {
                            status.setText(R.string.connection_failed);
                        }
                    });
        });
    }
}
