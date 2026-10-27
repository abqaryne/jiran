package com.jiran.app.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jiran.app.databinding.ComponentTabBarBinding;

/**
 * Figma: Tab bar + Post button. A floating white pill with Home, Rooms, Match and Unit, and a blue Post button.
 * The selected tab shows its filled icon in ink. Listen with {@link #setOnTabListener(OnTabListener)}.
 */
public class JiranTabBar extends FrameLayout {

    /** The four main tabs. */
    public enum Tab { HOME, ROOMS, MATCH, UNIT }

    /** Receives taps on the tabs and on Post. */
    public interface OnTabListener {
        void onTabSelected(Tab tab);

        void onPostClicked();
    }

    private final ComponentTabBarBinding binding;
    private OnTabListener listener;

    public JiranTabBar(Context context) {
        this(context, null);
    }

    public JiranTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JiranTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
        setClipToPadding(false);
        binding = ComponentTabBarBinding.inflate(LayoutInflater.from(context), this, true);
        bind(binding.tabHome, Tab.HOME);
        bind(binding.tabRooms, Tab.ROOMS);
        bind(binding.tabMatch, Tab.MATCH);
        bind(binding.tabUnit, Tab.UNIT);
        binding.postButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClicked();
            }
        });
        setSelectedTab(Tab.HOME);
    }

    private void bind(View view, Tab tab) {
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTabSelected(tab);
            }
        });
    }

    public void setOnTabListener(OnTabListener listener) {
        this.listener = listener;
    }

    /** Marks one tab as selected (filled icon, ink colour). Does not fire the listener. */
    public void setSelectedTab(Tab tab) {
        binding.tabHome.setSelected(tab == Tab.HOME);
        binding.tabRooms.setSelected(tab == Tab.ROOMS);
        binding.tabMatch.setSelected(tab == Tab.MATCH);
        binding.tabUnit.setSelected(tab == Tab.UNIT);
    }
}
