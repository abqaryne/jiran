package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentCommuteOptionBinding;

/**
 * Figma: Commute option. One travel mode with its time and monthly cost.
 * Call setSelected(true) on the cheapest option that still reaches class on time (blue border, blue note).
 */
public class CommuteOptionView extends FrameLayout {

    private final ComponentCommuteOptionBinding binding;

    public CommuteOptionView(Context context) {
        this(context, null);
    }

    public CommuteOptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommuteOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentCommuteOptionBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommuteOptionView);
            int icon = array.getResourceId(R.styleable.CommuteOptionView_commuteIcon, 0);
            if (icon != 0) {
                setIcon(icon);
            }
            setMode(array.getString(R.styleable.CommuteOptionView_commuteMode));
            setNote(array.getString(R.styleable.CommuteOptionView_commuteNote));
            setTime(array.getString(R.styleable.CommuteOptionView_commuteTime));
            setCost(array.getString(R.styleable.CommuteOptionView_commuteCost));
            array.recycle();
        }
    }

    public void setIcon(@DrawableRes int iconRes) {
        binding.modeIcon.setImageResource(iconRes);
    }

    public void setMode(CharSequence mode) {
        binding.mode.setText(mode);
    }

    public void setNote(CharSequence note) {
        ViewAttrs.setTextOrHide(binding.note, note);
    }

    public void setTime(CharSequence time) {
        binding.time.setText(time);
    }

    public void setCost(CharSequence cost) {
        binding.cost.setText(cost);
    }
}
