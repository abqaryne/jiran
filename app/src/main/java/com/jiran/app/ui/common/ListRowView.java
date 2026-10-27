package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentListRowBinding;

/**
 * Figma: List row. Icon circle or avatar, title, subtitle, trailing text and an optional chevron.
 * A thin divider sits under the row; hide it on the last row with {@link #setDividerVisible(boolean)}.
 */
public class ListRowView extends FrameLayout {

    private final ComponentListRowBinding binding;

    public ListRowView(Context context) {
        this(context, null);
    }

    public ListRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentListRowBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ListRowView);
            setTitle(array.getString(R.styleable.ListRowView_rowTitle));
            setSubtitle(array.getString(R.styleable.ListRowView_rowSubtitle));
            setTrailing(array.getString(R.styleable.ListRowView_rowTrailing));
            int icon = array.getResourceId(R.styleable.ListRowView_rowIcon, 0);
            if (icon != 0) {
                setIcon(icon);
            }
            String initial = array.getString(R.styleable.ListRowView_rowAvatarInitial);
            if (initial != null) {
                setAvatarInitial(initial);
            }
            setShowChevron(array.getBoolean(R.styleable.ListRowView_rowShowChevron, false));
            setDividerVisible(array.getBoolean(R.styleable.ListRowView_rowShowDivider, true));
            array.recycle();
        }
    }

    public void setTitle(CharSequence title) {
        binding.title.setText(title);
    }

    public void setSubtitle(CharSequence subtitle) {
        ViewAttrs.setTextOrHide(binding.subtitle, subtitle);
    }

    public void setTrailing(CharSequence trailing) {
        ViewAttrs.setTextOrHide(binding.trailing, trailing);
    }

    /** Shows a Material Symbol inside a grey circle. */
    public void setIcon(@DrawableRes int iconRes) {
        binding.icon.setImageResource(iconRes);
        binding.icon.setVisibility(View.VISIBLE);
        binding.avatar.setVisibility(View.GONE);
    }

    /** Shows the person's initial instead of an icon. */
    public void setAvatarInitial(CharSequence name) {
        binding.avatar.setInitial(name);
        binding.avatar.setVisibility(View.VISIBLE);
        binding.icon.setVisibility(View.GONE);
    }

    public void setShowChevron(boolean show) {
        binding.chevron.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setDividerVisible(boolean visible) {
        if (visible) {
            binding.rowRoot.setBackgroundResource(R.drawable.bg_divider_bottom);
        } else {
            binding.rowRoot.setBackground(null);
        }
    }
}
