package com.jiran.app.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Wraps the state of a load so a screen can show loading, data or an error without extra flags.
 * Repositories return LiveData of Resource; ViewModels pass it on; Fragments render it.
 */
public final class Resource<T> {

    /** Where the load is. */
    public enum Status { LOADING, SUCCESS, ERROR }

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull String message) {
        return new Resource<>(Status.ERROR, null, message);
    }
}
