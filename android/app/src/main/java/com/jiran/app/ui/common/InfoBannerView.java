package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentInfoBannerBinding;

/**
 * Figma: Info banner. Blue for information; success, warning and danger use the status colours.
 * XML: app:bannerKind="info|success|warning|danger", app:bannerText.
 */
public class InfoBannerView extends FrameLayout {

    /** What the banner says. */
    public enum Kind { INFO, SUCCESS, WARNING, DANGER }

    private final ComponentInfoBannerBinding binding;

    public InfoBannerView(Context context) {
        this(context, null);
    }

    public InfoBannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentInfoBannerBinding.inflate(LayoutInflater.from(context), this, true);
        Kind kind = Kind.INFO;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.InfoBannerView);
            kind = Kind.values()[array.getInt(R.styleable.InfoBannerView_bannerKind, Kind.INFO.ordinal())];
            setText(array.getString(R.styleable.InfoBannerView_bannerText));
            array.recycle();
        }
        setKind(kind);
    }

    public void setText(CharSequence text) {
        binding.bannerText.setText(text);
    }

    public void setKind(Kind kind) {
        int background;
        int foreground;
        int icon;
        switch (kind) {
            case SUCCESS:
                background = R.color.color_status_success_bg;
                foreground = R.color.color_status_success_text;
                icon = R.drawable.ic_check_circle;
                break;
            case WARNING:
                background = R.color.color_status_warning_bg;
                foreground = R.color.color_status_warning_text;
                icon = R.drawable.ic_error;
                break;
            case DANGER:
                background = R.color.color_status_danger_bg;
                foreground = R.color.color_status_danger_text;
                icon = R.drawable.ic_error;
                break;
            case INFO:
            default:
                background = R.color.color_brand_tint;
                foreground = R.color.color_brand_on_tint;
                icon = R.drawable.ic_info;
                break;
        }
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(ContextCompat.getColor(getContext(), background));
        shape.setCornerRadius(getResources().getDimension(R.dimen.radius_12));
        binding.bannerRoot.setBackground(shape);
        int color = ContextCompat.getColor(getContext(), foreground);
        binding.bannerText.setTextColor(color);
        binding.bannerIcon.setImageResource(icon);
        ImageViewCompat.setImageTintList(binding.bannerIcon, ColorStateList.valueOf(color));
    }
}
