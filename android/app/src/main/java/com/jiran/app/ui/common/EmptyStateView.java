package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentEmptyStateBinding;

/** Figma: Empty state. One icon, one bold sentence and one line of help. */
public class EmptyStateView extends FrameLayout {

    private final ComponentEmptyStateBinding binding;

    public EmptyStateView(Context context) {
        this(context, null);
    }

    public EmptyStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentEmptyStateBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView);
            int icon = array.getResourceId(R.styleable.EmptyStateView_emptyIcon, 0);
            if (icon != 0) {
                setIcon(icon);
            }
            setTitle(array.getString(R.styleable.EmptyStateView_emptyTitle));
            setMessage(array.getString(R.styleable.EmptyStateView_emptyMessage));
            array.recycle();
        }
    }

    public void setIcon(@DrawableRes int iconRes) {
        binding.emptyIcon.setImageResource(iconRes);
    }

    public void setTitle(CharSequence title) {
        binding.emptyTitle.setText(title);
    }

    public void setMessage(CharSequence message) {
        ViewAttrs.setTextOrHide(binding.emptyMessage, message);
    }
}
