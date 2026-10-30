package com.jiran.app.ui.auth;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.jiran.app.R;
import com.jiran.app.databinding.FragmentVerifyEmailBinding;
import com.jiran.app.ui.common.JiranSnackbar;
import com.jiran.app.ui.shell.ShellActivity;

/**
 * Figma: Verify email. Waits for the user to open the link, with Open email app, Resend link (after a wait) and
 * Change email. Until the account is verified this is the only screen the user can reach (FR1.1).
 */
public class VerifyEmailFragment extends Fragment {

    /** Seconds the user waits before the link can be sent again. */
    private static final int RESEND_WAIT_SECONDS = 60;

    private FragmentVerifyEmailBinding binding;
    private VerifyEmailViewModel viewModel;
    private CountDownTimer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new AuthViewModelFactory()).get(VerifyEmailViewModel.class);
        String email = viewModel.email();
        if (email == null) {
            // No signed-in account (for example the process was restarted): start again from Welcome.
            NavHostFragment.findNavController(this).popBackStack(R.id.welcomeFragment, false);
            return;
        }
        binding.body.setText(getString(R.string.auth_verify_body, email));
        binding.openEmail.setOnClickListener(button -> openEmailApp());
        binding.resend.setOnClickListener(button -> {
            viewModel.resendLink();
            startCooldown();
        });
        View.OnClickListener changeEmail = button -> changeEmail();
        binding.back.setOnClickListener(changeEmail);
        binding.changeEmail.setOnClickListener(changeEmail);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        changeEmail();
                    }
                });
        viewModel.verified().observe(getViewLifecycleOwner(), verified -> {
            if (Boolean.TRUE.equals(verified)) {
                openApp();
            }
        });
        viewModel.resend().observe(getViewLifecycleOwner(), this::showResendResult);
        if (viewModel.resendBlockedUntil() == 0) {
            // The link was sent a moment ago by Sign up, so the wait starts now.
            viewModel.setResendBlockedUntil(SystemClock.elapsedRealtime() + RESEND_WAIT_SECONDS * 1000L);
        }
        startTimerForRemainingWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && viewModel.email() != null) {
            // The user may have just come back from the email app.
            viewModel.checkVerified();
        }
    }

    private void startCooldown() {
        viewModel.setResendBlockedUntil(SystemClock.elapsedRealtime() + RESEND_WAIT_SECONDS * 1000L);
        startTimerForRemainingWait();
    }

    private void startTimerForRemainingWait() {
        if (timer != null) {
            timer.cancel();
        }
        long remaining = viewModel.resendBlockedUntil() - SystemClock.elapsedRealtime();
        if (remaining <= 0) {
            showResendReady();
            return;
        }
        binding.resend.setEnabled(false);
        timer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisLeft) {
                int seconds = (int) Math.ceil(millisLeft / 1000.0);
                binding.resend.setText(getString(R.string.auth_verify_resend_wait, seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                showResendReady();
            }
        }.start();
    }

    private void showResendReady() {
        binding.resend.setEnabled(true);
        binding.resend.setText(R.string.auth_verify_resend);
    }

    private void showResendResult(VerifyEmailViewModel.Resend result) {
        if (result == VerifyEmailViewModel.Resend.SENT) {
            JiranSnackbar.make(binding.getRoot(), getString(R.string.auth_verify_resend_sent)).show();
            viewModel.consumeResend();
        } else if (result == VerifyEmailViewModel.Resend.FAILED) {
            JiranSnackbar.make(binding.getRoot(), getString(R.string.auth_verify_resend_failed)).show();
            viewModel.consumeResend();
            showResendReady();
        }
    }

    private void openEmailApp() {
        Intent inbox = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_EMAIL);
        try {
            startActivity(inbox);
        } catch (ActivityNotFoundException e) {
            JiranSnackbar.make(binding.getRoot(), getString(R.string.auth_verify_no_email_app)).show();
        }
    }

    private void changeEmail() {
        viewModel.useAnotherEmail();
        NavHostFragment.findNavController(this).navigateUp();
    }

    /** The account is verified. The profile setup (E1.3) will come next; for now the product shell opens. */
    private void openApp() {
        startActivity(new Intent(requireContext(), ShellActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        binding = null;
    }
}
