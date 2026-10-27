package com.jiran.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.data.FirebaseSetup;
import com.jiran.app.databinding.FragmentLogInBinding;
import com.jiran.app.domain.auth.LogInCheck;
import com.jiran.app.domain.auth.SignUpCheck.Problem;
import com.jiran.app.ui.shell.ShellActivity;

/** Figma: Log in (FR1.2). A verified account opens the product; an unverified one is sent to Verify email. */
public class LogInFragment extends Fragment {

    private FragmentLogInBinding binding;
    private LogInViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new AuthViewModelFactory()).get(LogInViewModel.class);
        binding.back.setOnClickListener(button -> NavHostFragment.findNavController(this).navigateUp());
        binding.logIn.setOnClickListener(button -> submit());
        binding.password.setOnEditorActionListener((field, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submit();
                return true;
            }
            return false;
        });
        binding.forgotPassword.setOnClickListener(button ->
                NavHostFragment.findNavController(this).navigate(R.id.action_log_in_to_reset_password));
        binding.signUp.setOnClickListener(button ->
                NavHostFragment.findNavController(this).navigate(R.id.action_log_in_to_sign_up));
        viewModel.fieldProblems().observe(getViewLifecycleOwner(), this::showFieldProblems);
        viewModel.state().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        if (BuildConfig.USE_FIREBASE_EMULATORS && !FirebaseSetup.prepareConnection(requireActivity())) {
            showError(R.string.auth_error_local_network);
            return;
        }
        viewModel.submit(text(binding.email), text(binding.password));
    }

    private static String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private void showFieldProblems(@Nullable LogInCheck check) {
        if (check == null) {
            return;
        }
        setError(binding.emailLayout, check.email(), R.string.auth_error_email_required,
                R.string.auth_error_email_invalid);
        setError(binding.passwordLayout, check.password(), R.string.auth_error_password_required, 0);
    }

    private void setError(TextInputLayout layout, Problem problem, @StringRes int required, @StringRes int other) {
        if (problem == Problem.NONE) {
            layout.setError(null);
        } else if (problem == Problem.REQUIRED || other == 0) {
            layout.setError(getString(required));
        } else {
            layout.setError(getString(other));
        }
    }

    private void render(LogInViewModel.State state) {
        boolean loading = state == LogInViewModel.State.LOADING;
        binding.logIn.setEnabled(!loading);
        binding.logIn.setText(loading ? R.string.auth_login_loading : R.string.auth_login_button);
        if (loading) {
            binding.errorBanner.setVisibility(View.GONE);
        }
        switch (state) {
            case VERIFIED:
                viewModel.consumeResult();
                startActivity(new Intent(requireContext(), ShellActivity.class));
                requireActivity().finish();
                break;
            case NEEDS_VERIFICATION:
                viewModel.consumeResult();
                NavHostFragment.findNavController(this).navigate(R.id.action_log_in_to_verify_email);
                break;
            case INVALID_CREDENTIALS:
                showError(R.string.auth_error_credentials);
                viewModel.consumeResult();
                break;
            case TOO_MANY_ATTEMPTS:
                showError(R.string.auth_error_too_many);
                viewModel.consumeResult();
                break;
            case NETWORK:
                showError(R.string.auth_error_network);
                viewModel.consumeResult();
                break;
            case UNKNOWN:
                showError(R.string.auth_error_unknown);
                viewModel.consumeResult();
                break;
            default:
                break;
        }
    }

    private void showError(@StringRes int message) {
        binding.errorBanner.setText(getString(message));
        binding.errorBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
