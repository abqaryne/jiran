package com.jiran.app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Locale;

/** {@link AccountService} on top of the existing Firebase email and password repository. */
public final class FirebaseAccountService implements AccountService {

    private final EmailPasswordRepository repository;

    public FirebaseAccountService() {
        this(new EmailPasswordRepository());
    }

    FirebaseAccountService(EmailPasswordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void signUp(String fullName, String email, String password, Callback callback) {
        repository.register(email.trim().toLowerCase(Locale.ROOT), password).addOnCompleteListener(created -> {
            if (!created.isSuccessful()) {
                callback.onError(map(created.getException()));
                return;
            }
            // The account exists and the email is sent. A missing display name must not undo that, so its
            // result is not checked; the profile step (E1.3) asks for the name again if it is missing.
            storeName(fullName.trim()).addOnCompleteListener(ignored -> callback.onSuccess());
        });
    }

    private Task<Void> storeName(String fullName) {
        FirebaseUser user = repository.currentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
        return user.updateProfile(request);
    }

    @Override
    public void signIn(String email, String password, VerifiedCallback callback) {
        repository.signIn(email.trim().toLowerCase(Locale.ROOT), password).addOnCompleteListener(done -> {
            if (done.isSuccessful()) {
                callback.onResult(Boolean.TRUE.equals(done.getResult()));
            } else {
                callback.onError(map(done.getException()));
            }
        });
    }

    @Override
    public void sendPasswordReset(String email, Callback callback) {
        repository.resetPassword(email.trim().toLowerCase(Locale.ROOT)).addOnCompleteListener(done -> {
            // "No account for this address" counts as success on purpose: see AccountService.
            if (done.isSuccessful() || done.getException() instanceof FirebaseAuthInvalidUserException) {
                callback.onSuccess();
            } else {
                callback.onError(map(done.getException()));
            }
        });
    }

    @Override
    public Session session() {
        FirebaseUser user = repository.currentUser();
        if (user == null) {
            return Session.SIGNED_OUT;
        }
        return user.isEmailVerified() ? Session.VERIFIED : Session.UNVERIFIED;
    }

    @Override
    public void resendVerification(Callback callback) {
        repository.sendVerification().addOnCompleteListener(done -> {
            if (done.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onError(map(done.getException()));
            }
        });
    }

    @Override
    public void checkVerified(VerifiedCallback callback) {
        repository.refreshVerifiedStatus().addOnCompleteListener(done -> {
            if (done.isSuccessful()) {
                callback.onResult(Boolean.TRUE.equals(done.getResult()));
            } else {
                callback.onError(map(done.getException()));
            }
        });
    }

    @Nullable
    @Override
    public String currentEmail() {
        FirebaseUser user = repository.currentUser();
        return user == null ? null : user.getEmail();
    }

    @Override
    public void signOut() {
        repository.signOut();
    }

    @NonNull
    private static AccountError map(@Nullable Exception exception) {
        if (exception instanceof FirebaseAuthUserCollisionException) {
            return AccountError.EMAIL_TAKEN;
        }
        if (exception instanceof FirebaseAuthInvalidCredentialsException
                || exception instanceof FirebaseAuthInvalidUserException) {
            return AccountError.INVALID_CREDENTIALS;
        }
        if (exception instanceof FirebaseTooManyRequestsException) {
            return AccountError.TOO_MANY_ATTEMPTS;
        }
        if (exception instanceof FirebaseNetworkException) {
            return AccountError.NETWORK;
        }
        return AccountError.UNKNOWN;
    }
}
