package com.jiran.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jiran.app.R;
import com.jiran.app.databinding.ActivityProfileSetupBinding;
import com.jiran.app.domain.profile.SetupProgress;
import com.jiran.app.ui.auth.AuthActivity;
import com.jiran.app.ui.shell.ShellActivity;

/**
 * Profile setup (E1.3, FR1.3): three steps after the first verified sign-in. The shell sends a student here until
 * every step is saved; when setup is done it opens the shell again. Back on step 1 leaves the app, not the setup.
 */
public class ProfileSetupActivity extends AppCompatActivity {

    private ActivityProfileSetupBinding binding;
    private ProfileSetupViewModel viewModel;
    private int shownStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.profileRoot, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, keyboard.bottom));
            return WindowInsetsCompat.CONSUMED;
        });

        viewModel = new ViewModelProvider(this, new ProfileViewModelFactory()).get(ProfileSetupViewModel.class);
        binding.back.setOnClickListener(button -> getOnBackPressedDispatcher().onBackPressed());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Integer step = viewModel.step().getValue();
                if (step != null && step > 1) {
                    viewModel.back();
                } else {
                    finish();
                }
            }
        });
        viewModel.step().observe(this, this::showStep);
        viewModel.state().observe(this, this::render);
        if (savedInstanceState == null) {
            viewModel.start();
        } else {
            shownStep = savedInstanceState.getInt("shownStep");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("shownStep", shownStep);
    }

    private void showStep(Integer step) {
        binding.progress.setProgress(step, SetupProgress.STEPS);
        binding.stepLabel.setText(getString(R.string.profile_step, step, SetupProgress.STEPS));
        if (step == shownStep && getSupportFragmentManager().findFragmentById(R.id.step_container) != null) {
            return;
        }
        shownStep = step;
        getSupportFragmentManager().beginTransaction().replace(R.id.step_container, fragmentFor(step)).commit();
    }

    private static Fragment fragmentFor(int step) {
        switch (step) {
            case 2:
                return new PreferencesStepFragment();
            case 3:
                return new PrivacyStepFragment();
            default:
                return new BasicsStepFragment();
        }
    }

    private void render(ProfileSetupViewModel.State state) {
        boolean loading = state == ProfileSetupViewModel.State.LOADING;
        binding.loading.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.stepContainer.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        if (state == ProfileSetupViewModel.State.SAVING) {
            binding.errorBanner.setVisibility(View.GONE);
        }
        switch (state) {
            case DONE:
                startActivity(new Intent(this, ShellActivity.class));
                finish();
                break;
            case NETWORK:
                showError(R.string.profile_error_network);
                break;
            case SIGN_IN_REQUIRED:
                showError(R.string.profile_error_sign_in);
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                break;
            case UNKNOWN:
                showError(R.string.profile_error_unknown);
                break;
            default:
                break;
        }
    }

    private void showError(int message) {
        binding.errorBanner.setText(getString(message));
        binding.errorBanner.setVisibility(View.VISIBLE);
        viewModel.consumeError();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
