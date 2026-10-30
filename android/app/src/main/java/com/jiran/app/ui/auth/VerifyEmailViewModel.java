package com.jiran.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.AccountService;
import com.jiran.app.data.AccountService.AccountError;

/**
 * State of the Verify email screen: the address we sent the link to, whether the link was opened, and the result
 * of "Resend link". The 60 second wait between resends is a screen concern and lives in the Fragment.
 */
public class VerifyEmailViewModel extends ViewModel {

    /** Result of the last resend request. */
    public enum Resend { IDLE, SENDING, SENT, FAILED }

    private final AccountService accounts;
    private final MutableLiveData<Boolean> verified = new MutableLiveData<>(false);
    private final MutableLiveData<Resend> resend = new MutableLiveData<>(Resend.IDLE);
    private boolean checking;
    private long resendBlockedUntil;

    public VerifyEmailViewModel(AccountService accounts) {
        this.accounts = accounts;
    }

    /** The address the link was sent to, or null when nobody is signed in. */
    public String email() {
        return accounts.currentEmail();
    }

    public LiveData<Boolean> verified() {
        return verified;
    }

    public LiveData<Resend> resend() {
        return resend;
    }

    /** Asks the server whether the link was opened. Safe to call from onResume; overlapping calls are dropped. */
    public void checkVerified() {
        if (checking || Boolean.TRUE.equals(verified.getValue())) {
            return;
        }
        checking = true;
        accounts.checkVerified(new AccountService.VerifiedCallback() {
            @Override
            public void onResult(boolean isVerified) {
                checking = false;
                verified.setValue(isVerified);
            }

            @Override
            public void onError(AccountError error) {
                // A failed check is not an error to show: the user can open the link and come back.
                checking = false;
            }
        });
    }

    /** When "Resend link" unlocks again, in {@code SystemClock.elapsedRealtime()} milliseconds. 0 means not set. */
    public long resendBlockedUntil() {
        return resendBlockedUntil;
    }

    public void setResendBlockedUntil(long elapsedRealtimeMillis) {
        resendBlockedUntil = elapsedRealtimeMillis;
    }

    /** The screen calls this once it has shown the result of a resend, so a rotation does not show it again. */
    public void consumeResend() {
        Resend current = resend.getValue();
        if (current == Resend.SENT || current == Resend.FAILED) {
            resend.setValue(Resend.IDLE);
        }
    }

    public void resendLink() {
        if (resend.getValue() == Resend.SENDING) {
            return;
        }
        resend.setValue(Resend.SENDING);
        accounts.resendVerification(new AccountService.Callback() {
            @Override
            public void onSuccess() {
                resend.setValue(Resend.SENT);
            }

            @Override
            public void onError(AccountError error) {
                resend.setValue(Resend.FAILED);
            }
        });
    }

    /** "Wrong email? Change it": drops the unverified session so the user can sign up again. */
    public void useAnotherEmail() {
        accounts.signOut();
    }
}
