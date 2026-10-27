package com.jiran.app.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jiran.app.R;
import com.jiran.app.databinding.ComponentRoomCardBinding;

/**
 * Figma: Room card. One card per vacant Room: photo, price, title, meta and true monthly cost.
 * CO2 never appears here. Load the photo into {@link #getPhotoView()}.
 */
public class RoomCardView extends FrameLayout {

    private final ComponentRoomCardBinding binding;

    public RoomCardView(Context context) {
        this(context, null);
    }

    public RoomCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = ComponentRoomCardBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoomCardView);
            setPrice(array.getString(R.styleable.RoomCardView_roomPrice));
            setTitle(array.getString(R.styleable.RoomCardView_roomTitle));
            setMeta(array.getString(R.styleable.RoomCardView_roomMeta));
            setTrueCost(array.getString(R.styleable.RoomCardView_roomTrueCost));
            array.recycle();
        }
    }

    public void setPrice(CharSequence price) {
        binding.price.setText(price);
    }

    public void setTitle(CharSequence title) {
        binding.title.setText(title);
    }

    public void setMeta(CharSequence meta) {
        binding.meta.setText(meta);
    }

    /** Shows the true monthly cost line, or a message such as "No travel mode reaches class on time". */
    public void setTrueCost(CharSequence trueCost) {
        ViewAttrs.setTextOrHide(binding.trueCost, trueCost);
    }

    public ImageView getPhotoView() {
        return binding.photo;
    }
}
