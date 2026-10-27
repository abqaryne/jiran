package com.jiran.app.ui.rooms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Example ViewModel that every feature follows: it holds UI state as LiveData, talks to a repository
 * (never to a View), and has no Android UI imports so it can be unit tested on the JVM.
 */
public class RoomsViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void setLoading(boolean value) {
        loading.setValue(value);
    }
}
