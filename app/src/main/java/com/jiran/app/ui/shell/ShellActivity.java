package com.jiran.app.ui.shell;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.jiran.app.R;
import com.jiran.app.databinding.ActivityShellBinding;

/**
 * The product shell (single-activity architecture). It hosts the navigation graph and the floating tab bar.
 * Screens are Fragments, one package per feature under ui/.
 */
public class ShellActivity extends AppCompatActivity {

    private ActivityShellBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        if (host == null) {
            return;
        }
        NavController navController = host.getNavController();
        bindTabs(navController);
    }

    private void bindTabs(NavController navController) {
        binding.tabBarContainer.tabHome.setOnClickListener(v -> openTab(navController, R.id.homeFragment));
        binding.tabBarContainer.tabRooms.setOnClickListener(v -> openTab(navController, R.id.roomsFragment));
        binding.tabBarContainer.tabMatch.setOnClickListener(v -> openTab(navController, R.id.matchFragment));
        binding.tabBarContainer.tabUnit.setOnClickListener(v -> openTab(navController, R.id.unitFragment));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            binding.tabBarContainer.tabHome.setSelected(id == R.id.homeFragment);
            binding.tabBarContainer.tabRooms.setSelected(id == R.id.roomsFragment);
            binding.tabBarContainer.tabMatch.setSelected(id == R.id.matchFragment);
            binding.tabBarContainer.tabUnit.setSelected(id == R.id.unitFragment);
        });
    }

    private void openTab(NavController navController, int destinationId) {
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == destinationId) {
            return;
        }
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.nav_main, false, true)
                .build();
        navController.navigate(destinationId, null, options);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
