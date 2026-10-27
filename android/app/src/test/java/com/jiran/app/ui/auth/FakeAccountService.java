package com.jiran.app.ui.auth;

import com.jiran.app.data.AccountService;

/** Test double: records calls and lets the test decide when and how each call finishes. */
final class FakeAccountService implements AccountService {
    int signUpCalls;
    int checkCalls;
    int resendCalls;
    int signOutCalls;
    int signInCalls;
    int resetCalls;
    Session session = Session.SIGNED_OUT;
    String email = "meiling@siswa.um.edu.my";
    Callback pendingSignUp;
    Callback pendingResend;
    VerifiedCallback pendingCheck;
    VerifiedCallback pendingSignIn;
    Callback pendingReset;

    @Override
    public void signUp(String fullName, String email, String password, Callback callback) {
        signUpCalls++;
        pendingSignUp = callback;
    }

    @Override
    public void signIn(String email, String password, VerifiedCallback callback) {
        signInCalls++;
        pendingSignIn = callback;
    }

    @Override
    public void sendPasswordReset(String email, Callback callback) {
        resetCalls++;
        pendingReset = callback;
    }

    @Override
    public Session session() {
        return session;
    }

    @Override
    public void resendVerification(Callback callback) {
        resendCalls++;
        pendingResend = callback;
    }

    @Override
    public void checkVerified(VerifiedCallback callback) {
        checkCalls++;
        pendingCheck = callback;
    }

    @Override
    public String currentEmail() {
        return email;
    }

    @Override
    public void signOut() {
        signOutCalls++;
    }
}
