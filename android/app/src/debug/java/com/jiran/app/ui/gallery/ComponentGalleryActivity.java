package com.jiran.app.ui.gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jiran.app.databinding.ActivityComponentGalleryBinding;
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
