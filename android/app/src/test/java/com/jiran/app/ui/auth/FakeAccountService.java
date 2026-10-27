package com.jiran.app.ui.auth;

import com.jiran.app.data.AccountService;

/** Test double: records calls and lets the test decide when and how each call finishes. */
final class FakeAccountService implements AccountService {
    int signUpCalls;
    int checkCalls;
    int resendCalls;
    int signOutCalls;
    String email = "meiling@siswa.um.edu.my";
    Callback pendingSignUp;
    Callback pendingResend;
    VerifiedCallback pendingCheck;

    @Override
    public void signUp(String fullName, String email, String password, Callback callback) {
        signUpCalls++;
        pendingSignUp = callback;
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
