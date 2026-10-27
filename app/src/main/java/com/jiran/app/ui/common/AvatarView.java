package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.jiran.app.R;

import java.util.Locale;

/**
 * Figma: Avatar. Initials on a soft blue circle in three sizes. Real photos only appear once people live together.
 * XML: app:avatarSize="small|medium|large", app:avatarInitial="M".
 */
public class AvatarView extends AppCompatTextView {

    /** Avatar sizes: 32, 40 and 56 dp. */
    public enum Size { SMALL, MEDIUM, LARGE }

    private int sizePx;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.bg_avatar);
        setGravity(Gravity.CENTER);
        setIncludeFontPadding(false);
        int sizeIndex = Size.MEDIUM.ordinal();
        String initial = null;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AvatarView);
            sizeIndex = array.getInt(R.styleable.AvatarView_avatarSize, sizeIndex);
            initial = array.getString(R.styleable.AvatarView_avatarInitial);
            array.recycle();
        }
        setAvatarSize(Size.values()[sizeIndex]);
        if (initial != null) {
            setInitial(initial);
        }
    }

    /** Shows the first letter of the name in capitals. */
    public void setInitial(CharSequence name) {
        if (name == null || name.length() == 0) {
            setText("");
            return;
        }
        setText(String.valueOf(name.charAt(0)).toUpperCase(Locale.getDefault()));
    }

    public void setAvatarSize(Size size) {
        int dimen;
        int appearance;
        switch (size) {
            case SMALL:
                dimen = R.dimen.avatar_small;
                appearance = R.style.TextAppearance_Jiran_LabelSmall;
                break;
            case LARGE:
                dimen = R.dimen.avatar_large;
                appearance = R.style.TextAppearance_Jiran_Section;
                break;
            case MEDIUM:
            default:
                dimen = R.dimen.avatar_medium;
                appearance = R.style.TextAppearance_Jiran_Title;
                break;
        }
        sizePx = getResources().getDimensionPixelSize(dimen);
        TextViewCompat.setTextAppearance(this, appearance);
        setTextColor(ContextCompat.getColor(getContext(), R.color.color_brand_on_tint));
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(sizePx, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(sizePx, View.MeasureSpec.EXACTLY));
    }
}
