package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.jiran.app.R;

/**
 * Figma: progress steps. A row of thin pills; the first {@code current} are blue, the rest grey.
 * Used in profile setup (step 1 of 4) and the Match questions (question 2 of 7).
 * XML: app:stepTotal="4", app:stepCurrent="2".
 */
public class StepProgressView extends LinearLayout {

    private int total;
    private int current;

    public StepProgressView(Context context) {
        this(context, null);
    }

    public StepProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        int totalSteps = 4;
        int currentStep = 1;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StepProgressView);
            totalSteps = array.getInt(R.styleable.StepProgressView_stepTotal, totalSteps);
            currentStep = array.getInt(R.styleable.StepProgressView_stepCurrent, currentStep);
            array.recycle();
        }
        setProgress(currentStep, totalSteps);
    }

    public void setProgress(int currentStep, int totalSteps) {
        this.current = currentStep;
        this.total = totalSteps;
        removeAllViews();
        int height = (int) (4 * getResources().getDisplayMetrics().density);
        int gap = getResources().getDimensionPixelSize(R.dimen.spacing_4) + height / 2;
        for (int i = 0; i < total; i++) {
            View step = new View(getContext());
            GradientDrawable pill = new GradientDrawable();
            pill.setCornerRadius(height / 2f);
            pill.setColor(ContextCompat.getColor(getContext(),
                    i < current ? R.color.color_brand_primary : R.color.color_bg_fill));
            step.setBackground(pill);
            LayoutParams params = new LayoutParams(0, height, 1f);
            if (i < total - 1) {
                params.setMarginEnd(gap);
            }
            addView(step, params);
        }
        setContentDescription(getResources().getString(R.string.step_progress_description, current, total));
    }

    public int getCurrentStep() {
        return current;
    }

    public int getTotalSteps() {
        return total;
    }
}
