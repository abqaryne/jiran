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
 * Figma: Room card. One card per vacant Room: photo, price, title, meta and a separate one-way commute estimate.
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
            setCommute(array.getString(R.styleable.RoomCardView_roomCommute));
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

    /** Separate one-way travel time/fare with its mode; never a combined monthly total. */
    public void setCommute(CharSequence commute) {
        ViewAttrs.setTextOrHide(binding.commute, commute);
    }

    public ImageView getPhotoView() {
        return binding.photo;
    }
}
