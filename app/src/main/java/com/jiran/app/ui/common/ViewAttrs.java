package com.jiran.app.ui.common;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/** Tiny helpers shared by the design system views. */
final class ViewAttrs {

    private ViewAttrs() {
    }

    /** Sets the text and hides the view when there is nothing to show. */
    static void setTextOrHide(TextView view, CharSequence text) {
        view.setText(text);
        view.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }
}
