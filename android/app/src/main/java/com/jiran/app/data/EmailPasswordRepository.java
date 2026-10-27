package com.jiran.app.data;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import java.util.Collections;

/** Basic Auth/Firestore infrastructure; product profiles belong to Ary/Gathan. */
public final class EmailPasswordRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    public Task<Void> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email.trim(), password)
                .onSuccessTask(result -> result.getUser().sendEmailVerification());
    }

    public Task<Boolean> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email.trim(), password)
                .onSuccessTask(result -> refreshVerifiedStatus());
    }

    public Task<Void> sendVerification() {
        FirebaseUser user = currentUser();
        if (user == null) {
            return Tasks.forException(new IllegalStateException("Sign in first"));
        }
        return user.sendEmailVerification();
    }

    public Task<Boolean> refreshVerifiedStatus() {
        FirebaseUser user = currentUser();
        if (user == null) {
            return Tasks.forResult(false);
        }
        return user.reload().onSuccessTask(ignored -> user.getIdToken(true))
                .onSuccessTask(token -> Tasks.forResult(user.isEmailVerified()));
    }

    public Task<Void> resetPassword(String email) {
        return auth.sendPasswordResetEmail(email.trim());
    }

    public void signOut() {
        auth.signOut();
    }

    public Task<Boolean> checkFirestoreConnection() {
        return refreshVerifiedStatus().onSuccessTask(verified -> {
            FirebaseUser user = currentUser();
            if (!verified || user == null) {
                return Tasks.forException(new SecurityException("Verify your email first"));
            }
            var document = FirebaseFirestore.getInstance()
                    .collection("_connectionChecks").document(user.getUid());
            return document.set(Collections.singletonMap("checkedAt", FieldValue.serverTimestamp()))
                    .onSuccessTask(ignored -> document.get(Source.SERVER))
                    .onSuccessTask(snapshot -> Tasks.forResult(snapshot.exists()));
        });
    }
}
