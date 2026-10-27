package com.jiran.app.ui.shell;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.jiran.app.R;
import com.jiran.app.databinding.ActivityShellBinding;
import com.jiran.app.ui.common.JiranTabBar;

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
        binding.tabBar.setOnTabListener(new JiranTabBar.OnTabListener() {
            @Override
            public void onTabSelected(JiranTabBar.Tab tab) {
                openTab(navController, destinationFor(tab));
            }

            @Override
            public void onPostClicked() {
                // Module 2: open the Post a room flow here.
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.homeFragment) {
                binding.tabBar.setSelectedTab(JiranTabBar.Tab.HOME);
            } else if (id == R.id.roomsFragment) {
                binding.tabBar.setSelectedTab(JiranTabBar.Tab.ROOMS);
            } else if (id == R.id.matchFragment) {
                binding.tabBar.setSelectedTab(JiranTabBar.Tab.MATCH);
            } else if (id == R.id.unitFragment) {
                binding.tabBar.setSelectedTab(JiranTabBar.Tab.UNIT);
            }
        });
    }

    private int destinationFor(JiranTabBar.Tab tab) {
        switch (tab) {
            case ROOMS:
                return R.id.roomsFragment;
            case MATCH:
                return R.id.matchFragment;
            case UNIT:
                return R.id.unitFragment;
            case HOME:
            default:
                return R.id.homeFragment;
        }
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
