package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentSectionHeaderBinding;

/** Figma: Section header. A title and an optional brand-coloured action such as "See all". */
public class SectionHeaderView extends FrameLayout {

    private final ComponentSectionHeaderBinding binding;

    public SectionHeaderView(Context context) {
        this(context, null);
    }

    public SectionHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentSectionHeaderBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderView);
            setTitle(array.getString(R.styleable.SectionHeaderView_headerTitle));
            setAction(array.getString(R.styleable.SectionHeaderView_headerAction));
            array.recycle();
        }
    }

    public void setTitle(CharSequence title) {
        binding.headerTitle.setText(title);
    }

    /** Sets the action text; an empty value hides the action. */
    public void setAction(CharSequence action) {
        ViewAttrs.setTextOrHide(binding.headerAction, action);
    }

    public void setOnActionClickListener(View.OnClickListener listener) {
        binding.headerAction.setOnClickListener(listener);
    }
}
