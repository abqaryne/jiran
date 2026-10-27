package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.card.MaterialCardView;
import com.jiran.app.R;
import com.jiran.app.databinding.ComponentSearchBarBinding;

/**
 * Figma: Search bar. A 44 dp pill with a search icon and a text field.
 * With app:searchAi="true" the icon becomes the AI sparkle (plain-language search).
 */
public class SearchBarView extends MaterialCardView {

    private final ComponentSearchBarBinding binding;

    public SearchBarView(Context context) {
        this(context, null);
    }

    public SearchBarView(Context context, AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
    }

    public SearchBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardElevation(0f);
        setRadius(getResources().getDimension(R.dimen.search_height) / 2f);
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_bg_surface));
        setStrokeColor(ContextCompat.getColor(context, R.color.color_border_strong));
        setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_thin));
        binding = ComponentSearchBarBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SearchBarView);
            binding.searchInput.setHint(array.getString(R.styleable.SearchBarView_searchHint));
            setAiEnabled(array.getBoolean(R.styleable.SearchBarView_searchAi, false));
            array.recycle();
        }
    }

    public EditText getEditText() {
        return binding.searchInput;
    }

    public void setHint(CharSequence hint) {
        binding.searchInput.setHint(hint);
    }

    public void setAiEnabled(boolean enabled) {
        binding.searchIcon.setImageResource(enabled ? R.drawable.ic_auto_awesome : R.drawable.ic_search);
        int color = ContextCompat.getColor(getContext(),
                enabled ? R.color.color_brand_primary : R.color.color_text_secondary);
        ImageViewCompat.setImageTintList(binding.searchIcon, ColorStateList.valueOf(color));
    }
}
