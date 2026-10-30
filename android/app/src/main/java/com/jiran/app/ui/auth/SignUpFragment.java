package com.jiran.app.ui.auth;

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
import com.google.android.material.textfield.TextInputLayout;
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.data.FirebaseSetup;
import com.jiran.app.databinding.FragmentSignUpBinding;
import com.jiran.app.domain.auth.SignUpCheck;
import com.jiran.app.domain.auth.SignUpCheck.Problem;

/** Figma: Sign up. Collects name, UM email and password, and sends the verification link. */
public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new AuthViewModelFactory()).get(SignUpViewModel.class);
        binding.passwordHint.setText(getString(R.string.auth_password_hint, SignUpCheck.MIN_PASSWORD_LENGTH));
        binding.back.setOnClickListener(button -> NavHostFragment.findNavController(this).navigateUp());
        binding.createAccount.setOnClickListener(button -> submit());
        binding.password.setOnEditorActionListener((field, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submit();
                return true;
            }
            return false;
        });
        viewModel.fieldProblems().observe(getViewLifecycleOwner(), this::showFieldProblems);
        viewModel.state().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        if (BuildConfig.USE_FIREBASE_EMULATORS && !FirebaseSetup.prepareConnection(requireActivity())) {
            showError(R.string.auth_error_local_network);
            return;
        }
        viewModel.submit(text(binding.name), text(binding.email), text(binding.password));
    }

    private static String text(com.google.android.material.textfield.TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private void showFieldProblems(@Nullable SignUpCheck check) {
        if (check == null) {
            return;
        }
        setError(binding.nameLayout, check.name(), R.string.auth_error_name_required, 0);
        setError(binding.emailLayout, check.email(), R.string.auth_error_email_required,
                check.email() == Problem.NOT_UM_EMAIL ? R.string.auth_error_email_not_um
                        : R.string.auth_error_email_invalid);
        setError(binding.passwordLayout, check.password(), R.string.auth_error_password_required,
                R.string.auth_error_password_short);
    }

    /** Shows the message for the problem under the field, or clears the field when there is no problem. */
    private void setError(TextInputLayout layout, Problem problem, @StringRes int required, @StringRes int other) {
        if (problem == Problem.NONE) {
            layout.setError(null);
        } else if (problem == Problem.REQUIRED) {
            layout.setError(getString(required));
        } else if (other == R.string.auth_error_password_short) {
            layout.setError(getString(other, SignUpCheck.MIN_PASSWORD_LENGTH));
        } else {
            layout.setError(getString(other));
        }
    }

    private void render(SignUpViewModel.State state) {
        boolean loading = state == SignUpViewModel.State.LOADING;
        binding.createAccount.setEnabled(!loading);
        binding.createAccount.setText(loading ? R.string.auth_creating_account : R.string.auth_create_account);
        if (loading) {
            binding.errorBanner.setVisibility(View.GONE);
        }
        switch (state) {
            case SUCCESS:
                viewModel.consumeResult();
                NavHostFragment.findNavController(this).navigate(R.id.action_sign_up_to_verify_email);
                break;
            case EMAIL_TAKEN:
                showError(R.string.auth_error_email_taken);
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
