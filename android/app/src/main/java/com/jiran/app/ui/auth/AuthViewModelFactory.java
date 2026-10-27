package com.jiran.app.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.jiran.app.data.AccountService;
import com.jiran.app.data.FirebaseAccountService;

/** Builds the Module 1 view models with the real account service. */
final class AuthViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        AccountService accounts = new FirebaseAccountService();
        if (modelClass.isAssignableFrom(SignUpViewModel.class)) {
            return (T) new SignUpViewModel(accounts);
        }
        if (modelClass.isAssignableFrom(VerifyEmailViewModel.class)) {
            return (T) new VerifyEmailViewModel(accounts);
        }
        throw new IllegalArgumentException("Unknown view model " + modelClass.getName());
    }
}
