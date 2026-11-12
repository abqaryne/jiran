package com.jiran.app.ui.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.jiran.app.data.FirestoreProfileRepository;

/** Builds the profile setup view model with the real Firestore repository. */
final class ProfileViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileSetupViewModel.class)) {
            return (T) new ProfileSetupViewModel(new FirestoreProfileRepository());
        }
        throw new IllegalArgumentException("Unknown view model " + modelClass.getName());
    }
}
