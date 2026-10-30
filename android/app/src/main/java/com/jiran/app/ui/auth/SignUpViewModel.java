package com.jiran.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.AccountService;
import com.jiran.app.data.AccountService.AccountError;
import com.jiran.app.domain.auth.SignUpCheck;

/**
 * State of the Sign up screen. It checks the form with {@link SignUpCheck}, calls the account service only when the
 * form is valid, and publishes what the screen should show. It holds no View or Context.
 */
public class SignUpViewModel extends ViewModel {

    /** Where the sign-up request is. */
    public enum State { IDLE, LOADING, SUCCESS, EMAIL_TAKEN, NETWORK, UNKNOWN }

    private final AccountService accounts;
    private final MutableLiveData<SignUpCheck> fieldProblems = new MutableLiveData<>();
    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);

    public SignUpViewModel(AccountService accounts) {
        this.accounts = accounts;
    }

    /** Problems to show under each field. Null until the user has tried to submit once. */
    public LiveData<SignUpCheck> fieldProblems() {
        return fieldProblems;
    }

    public LiveData<State> state() {
        return state;
    }

    /** Called by the Create account button. A second tap while loading is ignored. */
    public void submit(String fullName, String email, String password) {
        if (state.getValue() == State.LOADING) {
            return;
        }
        SignUpCheck check = SignUpCheck.of(fullName, email, password);
        fieldProblems.setValue(check);
        if (!check.isValid()) {
            return;
        }
        state.setValue(State.LOADING);
        accounts.signUp(fullName, email, password, new AccountService.Callback() {
            @Override
            public void onSuccess() {
                state.setValue(State.SUCCESS);
            }

            @Override
            public void onError(AccountError error) {
                state.setValue(stateFor(error));
            }
        });
    }

    private static State stateFor(AccountError error) {
        switch (error) {
            case EMAIL_TAKEN:
                return State.EMAIL_TAKEN;
            case NETWORK:
                return State.NETWORK;
            default:
                return State.UNKNOWN;
        }
    }

    /** The screen calls this after it has shown a one-time result (an error message), so it is not shown twice. */
    public void consumeResult() {
        State current = state.getValue();
        if (current != State.LOADING && current != State.IDLE) {
            state.setValue(State.IDLE);
        }
    }
}
