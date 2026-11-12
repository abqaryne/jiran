package com.jiran.app.ui.profile;

import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jiran.app.R;
import java.util.ArrayList;
import java.util.List;

/** Fills a chip group from a key list and a label list, and reads back the chosen keys. Keys go to Firestore. */
final class Chips {

    private Chips() {
    }

    static void fill(ChipGroup group, String[] keys, String[] labels) {
        LayoutInflater inflater = LayoutInflater.from(group.getContext());
        group.removeAllViews();
        for (int i = 0; i < keys.length; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_profile_chip, group, false);
            chip.setText(labels[i]);
            chip.setTag(keys[i]);
            group.addView(chip);
        }
    }

    /** The key of the one checked chip, or null when none is checked. */
    @Nullable
    static String single(ChipGroup group) {
        List<String> keys = checked(group);
        return keys.isEmpty() ? null : keys.get(0);
    }

    static List<String> checked(ChipGroup group) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip.isChecked()) {
                keys.add(String.valueOf(chip.getTag()));
            }
        }
        return keys;
    }
}
