package com.jiran.app.ui.gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jiran.app.databinding.ActivityComponentGalleryBinding;
import com.jiran.app.R;
import com.jiran.app.ui.listing.BuildingPickerDialog;
import com.jiran.app.ui.common.JiranSnackbar;
import com.jiran.app.ui.common.JiranTabBar;

/**
 * Debug-only gallery of every design system component, so a screen can be compared with Figma in one scroll.
 * It ships only in debug builds (src/debug). Add a component here when you add it to the design system.
 */
public class ComponentGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComponentGalleryBinding binding = ActivityComponentGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.previewBuildingPicker.setOnClickListener(button -> {
            if (getSupportFragmentManager().findFragmentByTag("building-picker") == null) {
                new SampleBuildingPickerDialog().show(getSupportFragmentManager(), "building-picker");
            }
        });
        getSupportFragmentManager().setFragmentResultListener(BuildingPickerDialog.RESULT_KEY, this,
                (key, result) -> JiranSnackbar.make(binding.getRoot(),
                        getString(R.string.gallery_building_result, result.getString("buildingName"),
                                result.getString("areaName"))).show());

        binding.fieldError.setError("Use your @siswa.um.edu.my email");
        binding.showToast.setOnClickListener(v ->
                JiranSnackbar.make(binding.getRoot(), "Request sent")
                        .setAction("Undo", view -> { })
                        .show());
        binding.tabBar.setOnTabListener(new JiranTabBar.OnTabListener() {
            @Override
            public void onTabSelected(JiranTabBar.Tab tab) {
                binding.tabBar.setSelectedTab(tab);
            }

            @Override
            public void onPostClicked() {
                JiranSnackbar.make(binding.getRoot(), "Post tapped").show();
            }
        });
        binding.avatarMedium.setInitial("Mei Ling");
        binding.commuteSelected.setSelected(true);
    }
}
