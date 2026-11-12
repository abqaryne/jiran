package com.jiran.app.ui.shell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.jiran.app.R;
import com.jiran.app.data.FirestoreProfileRepository;
import com.jiran.app.data.ProfileRepository;
import com.jiran.app.databinding.ActivityShellBinding;
import com.jiran.app.ui.auth.LauncherActivity;
import com.jiran.app.ui.common.JiranTabBar;
import com.jiran.app.ui.profile.ProfileSetupActivity;

/**
 * The product shell (single-activity architecture). It hosts the navigation graph and the floating tab bar.
 * Screens are Fragments, one package per feature under ui/.
 */
public class ShellActivity extends AppCompatActivity {

    private ActivityShellBinding binding;
    private ProfileGate gate;

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
        gate = new ProfileGate(new FirestoreProfileRepository(), new ProfileGate.Listener() {
            @Override
            public void onOpenTabs() {
                binding.gateError.setVisibility(View.GONE);
                binding.navHost.setVisibility(View.VISIBLE);
                binding.tabBar.setVisibility(navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() == R.id.postListingFragment
                        ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNeedsSetup() {
                startActivity(new Intent(ShellActivity.this, ProfileSetupActivity.class));
                finish();
            }

            @Override
            public void onCheckFailed(ProfileRepository.Problem problem) {
                showFailure(problem);
            }
        });
        checkProfile();
    }

    /**
     * Profile gate (E1.3, E1.4, FR1.3). The tabs stay hidden until the check says every required setup step is saved.
     * If the check cannot be answered (no connection, session ended) the tabs stay closed and a retry screen shows:
     * the gate fails closed. The check also runs when the activity is restored. Needs human review.
     */
    private void checkProfile() {
        showChecking();
        gate.check();
    }

    private void showChecking() {
        binding.navHost.setVisibility(View.INVISIBLE);
        binding.tabBar.setVisibility(View.INVISIBLE);
        binding.gateError.setVisibility(View.GONE);
    }

    private void showFailure(ProfileRepository.Problem problem) {
        boolean signIn = problem == ProfileRepository.Problem.SIGN_IN_REQUIRED;
        binding.navHost.setVisibility(View.INVISIBLE);
        binding.tabBar.setVisibility(View.INVISIBLE);
        binding.gateErrorMessage.setText(signIn ? R.string.shell_gate_error_sign_in
                : problem == ProfileRepository.Problem.NETWORK ? R.string.shell_gate_error_network
                : R.string.shell_gate_error_unknown);
        binding.gateError.setVisibility(View.VISIBLE);
        binding.gateRetry.setOnClickListener(button -> {
            if (signIn) {
                // The launcher reads the saved session and opens log in when there is none.
                startActivity(new Intent(this, LauncherActivity.class));
                finish();
            } else {
                checkProfile();
            }
        });
    }

    private void bindTabs(NavController navController) {
        binding.tabBar.setOnTabListener(new JiranTabBar.OnTabListener() {
            @Override
            public void onTabSelected(JiranTabBar.Tab tab) {
                openTab(navController, destinationFor(tab));
            }

            @Override
            public void onPostClicked() {
                if (navController.getCurrentDestination() != null
                        && navController.getCurrentDestination().getId() != R.id.postListingFragment) {
                    navController.navigate(R.id.postListingFragment);
                }
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (binding.navHost.getVisibility() == View.VISIBLE) {
                binding.tabBar.setVisibility(id == R.id.postListingFragment ? View.GONE : View.VISIBLE);
            }
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
        if (gate != null) {
            gate.cancel();
        }
        binding = null;
    }
}
