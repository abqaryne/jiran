package com.jiran.app.data;

import androidx.annotation.Nullable;

/**
 * What the sign-up screens need from the account backend. The screens and view models only see this interface, so
 * they can be tested with a fake and the real Firebase calls stay in {@link FirebaseAccountService}.
 * All callbacks arrive on the main thread.
 */
public interface AccountService {

    /** Why an account call failed. Screens turn this into a message. */
    enum AccountError { EMAIL_TAKEN, INVALID_CREDENTIALS, TOO_MANY_ATTEMPTS, NETWORK, UNKNOWN }

    /** Who is using the app right now. */
    enum Session { SIGNED_OUT, UNVERIFIED, VERIFIED }

    /** Result of a call that returns nothing. */
    interface Callback {
        void onSuccess();

        void onError(AccountError error);
    }

    /** Result of {@link #checkVerified}. */
    interface VerifiedCallback {
        void onResult(boolean verified);

        void onError(AccountError error);
    }

    /** Creates the account, stores the name, and sends the verification email. */
    void signUp(String fullName, String email, String password, Callback callback);

    /**
     * Signs in and reports whether the email is verified. A wrong email and a wrong password give the same
     * {@link AccountError#INVALID_CREDENTIALS}, so the app never tells which one was wrong.
     */
    void signIn(String email, String password, VerifiedCallback callback);

    /**
     * Sends a password reset link. It reports success even when the address has no account, so nobody can use
     * this screen to find out who is registered.
     */
    void sendPasswordReset(String email, Callback callback);

    /** The state of the saved session, read without a network call. */
    Session session();

    /** Sends the verification email again to the signed-in account. */
    void resendVerification(Callback callback);

    /** Reloads the account from the server and reports whether the email is now verified. */
    void checkVerified(VerifiedCallback callback);

    /** The email of the signed-in account, or null when nobody is signed in. */
    @Nullable
    String currentEmail();

    void signOut();
}
