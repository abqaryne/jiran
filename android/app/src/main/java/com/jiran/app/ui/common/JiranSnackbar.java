package com.jiran.app.ui.common;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.jiran.app.R;

/**
 * Figma: Toast. An ink pill near the bottom with white text and an optional action ("Undo").
 * Use this instead of Toast or a plain Snackbar.
 */
public final class JiranSnackbar {

    private JiranSnackbar() {
    }

    public static Snackbar make(@NonNull View anchor, @NonNull CharSequence message) {
        Snackbar snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(anchor.getContext(), R.color.color_bg_ink));
        snackbar.setTextColor(ContextCompat.getColor(anchor.getContext(), R.color.color_text_on_brand));
        snackbar.setActionTextColor(ContextCompat.getColor(anchor.getContext(), R.color.color_text_on_brand));
        return snackbar;
    }
}
