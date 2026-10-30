package com.jiran.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.AccountService;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.LogInCheck;
import com.jiran.app.domain.auth.SignUpCheck.Problem;

/** State of the Reset password screen (FR1.2). The "sent" message never says whether the address has an account. */
public class ResetPasswordViewModel extends ViewModel {

    /** Where the reset request is. */
    public enum State { IDLE, LOADING, SENT, TOO_MANY_ATTEMPTS, NETWORK, UNKNOWN }

    private final AccountService accounts;
    private final MutableLiveData<Problem> emailProblem = new MutableLiveData<>();
    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);

    public ResetPasswordViewModel(AccountService accounts) {
        this.accounts = accounts;
    }

    public LiveData<Problem> emailProblem() {
        return emailProblem;
    }

    public LiveData<State> state() {
        return state;
    }

    public void submit(String email) {
        if (state.getValue() == State.LOADING) {
            return;
        }
        Problem problem = LogInCheck.emailProblem(email);
        emailProblem.setValue(problem);
        if (problem != Problem.NONE) {
            return;
        }
        state.setValue(State.LOADING);
        accounts.sendPasswordReset(email, new AccountService.Callback() {
            @Override
            public void onSuccess() {
                state.setValue(State.SENT);
            }

            @Override
            public void onError(AccountError error) {
                state.setValue(stateFor(error));
            }
        });
    }

    private static State stateFor(AccountError error) {
        switch (error) {
            case TOO_MANY_ATTEMPTS:
                return State.TOO_MANY_ATTEMPTS;
            case NETWORK:
                return State.NETWORK;
            default:
                return State.UNKNOWN;
        }
    }
}
