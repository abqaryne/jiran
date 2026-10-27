package com.jiran.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.AccountService;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.LogInCheck;

/** State of the Log in screen (FR1.2). It holds no View or Context. */
public class LogInViewModel extends ViewModel {

    /** Where the log in request is. VERIFIED and NEEDS_VERIFICATION tell the screen where to go next. */
    public enum State {
        IDLE, LOADING, VERIFIED, NEEDS_VERIFICATION, INVALID_CREDENTIALS, TOO_MANY_ATTEMPTS, NETWORK, UNKNOWN
    }

    private final AccountService accounts;
    private final MutableLiveData<LogInCheck> fieldProblems = new MutableLiveData<>();
    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);

    public LogInViewModel(AccountService accounts) {
        this.accounts = accounts;
    }

    public LiveData<LogInCheck> fieldProblems() {
        return fieldProblems;
    }

    public LiveData<State> state() {
        return state;
    }

    /** Called by the Log in button. A second tap while loading is ignored. */
    public void submit(String email, String password) {
        if (state.getValue() == State.LOADING) {
            return;
        }
        LogInCheck check = LogInCheck.of(email, password);
        fieldProblems.setValue(check);
        if (!check.isValid()) {
            return;
        }
        state.setValue(State.LOADING);
        accounts.signIn(email, password, new AccountService.VerifiedCallback() {
            @Override
            public void onResult(boolean verified) {
                state.setValue(verified ? State.VERIFIED : State.NEEDS_VERIFICATION);
            }

            @Override
            public void onError(AccountError error) {
                state.setValue(stateFor(error));
            }
        });
    }

    private static State stateFor(AccountError error) {
        switch (error) {
            case INVALID_CREDENTIALS:
                return State.INVALID_CREDENTIALS;
            case TOO_MANY_ATTEMPTS:
                return State.TOO_MANY_ATTEMPTS;
            case NETWORK:
                return State.NETWORK;
            default:
                return State.UNKNOWN;
        }
    }

    /** The screen calls this after it has acted on a result, so a rotation does not act on it twice. */
    public void consumeResult() {
        State current = state.getValue();
        if (current != State.LOADING && current != State.IDLE) {
            state.setValue(State.IDLE);
        }
    }
}
