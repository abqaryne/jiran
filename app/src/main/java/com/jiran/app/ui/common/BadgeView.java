package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.jiran.app.R;

/**
 * Figma: Badge. One word plus an icon on a soft coloured pill. Colour is never the only signal.
 * XML: app:badgeStatus="success|warning|danger|brand|neutral", app:badgeIcon="@drawable/ic_check", android:text.
 */
public class BadgeView extends AppCompatTextView {

    /** Meaning of the badge. */
    public enum Status { SUCCESS, WARNING, DANGER, BRAND, NEUTRAL }

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int pad = getResources().getDimensionPixelSize(R.dimen.spacing_8);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinHeight(getResources().getDimensionPixelSize(R.dimen.badge_height));
        setPaddingRelative(pad, 0, pad, 0);
        setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.spacing_4));
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_Jiran_Caption);
        Status status = Status.NEUTRAL;
        int icon = 0;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
            status = Status.values()[array.getInt(R.styleable.BadgeView_badgeStatus, Status.NEUTRAL.ordinal())];
            icon = array.getResourceId(R.styleable.BadgeView_badgeIcon, 0);
            array.recycle();
        }
        setStatus(status);
        if (icon != 0) {
            setIcon(icon);
        }
    }

    public void setStatus(Status status) {
        int background;
        int color;
        switch (status) {
            case SUCCESS:
                background = R.drawable.bg_badge_success;
                color = R.color.color_status_success_text;
                break;
            case WARNING:
                background = R.drawable.bg_badge_warning;
                color = R.color.color_status_warning_text;
                break;
            case DANGER:
                background = R.drawable.bg_badge_danger;
                color = R.color.color_status_danger_text;
                break;
            case BRAND:
                background = R.drawable.bg_badge_brand;
                color = R.color.color_brand_on_tint;
                break;
            case NEUTRAL:
            default:
                background = R.drawable.bg_badge_neutral;
                color = R.color.color_text_primary;
                break;
        }
        int resolved = ContextCompat.getColor(getContext(), color);
        setBackgroundResource(background);
        setTextColor(resolved);
        TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(resolved));
    }

    /** Sets the 16 dp icon in front of the word. */
    public void setIcon(@DrawableRes int iconRes) {
        Drawable icon = AppCompatResources.getDrawable(getContext(), iconRes);
        if (icon == null) {
            return;
        }
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size_small) - 2;
        icon.setBounds(0, 0, size, size);
        setCompoundDrawablesRelative(icon, null, null, null);
    }
}
