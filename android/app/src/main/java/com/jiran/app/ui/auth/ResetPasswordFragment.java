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
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.data.FirebaseSetup;
import com.jiran.app.databinding.FragmentResetPasswordBinding;
import com.jiran.app.domain.auth.SignUpCheck.Problem;
import com.jiran.app.ui.common.InfoBannerView;

/** Figma: Reset password (FR1.2). The result message is the same whether or not the address has an account. */
public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private ResetPasswordViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this, new AuthViewModelFactory()).get(ResetPasswordViewModel.class);
        binding.back.setOnClickListener(button -> NavHostFragment.findNavController(this).navigateUp());
        binding.send.setOnClickListener(button -> submit());
        binding.email.setOnEditorActionListener((field, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                submit();
                return true;
            }
            return false;
        });
        viewModel.emailProblem().observe(getViewLifecycleOwner(), this::showProblem);
        viewModel.state().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        if (BuildConfig.USE_FIREBASE_EMULATORS && !FirebaseSetup.prepareConnection(requireActivity())) {
            showBanner(InfoBannerView.Kind.DANGER, R.string.auth_error_local_network);
            return;
        }
        viewModel.submit(binding.email.getText() == null ? "" : binding.email.getText().toString());
    }

    private void showProblem(@Nullable Problem problem) {
        if (problem == null || problem == Problem.NONE) {
            binding.emailLayout.setError(null);
        } else if (problem == Problem.REQUIRED) {
            binding.emailLayout.setError(getString(R.string.auth_error_email_required));
        } else {
            binding.emailLayout.setError(getString(R.string.auth_error_email_invalid));
        }
    }

    private void render(ResetPasswordViewModel.State state) {
        boolean loading = state == ResetPasswordViewModel.State.LOADING;
        binding.send.setEnabled(!loading);
        binding.send.setText(loading ? R.string.auth_reset_sending : R.string.auth_reset_send);
        switch (state) {
            case LOADING:
                binding.banner.setVisibility(View.GONE);
                break;
            case SENT:
                showBanner(InfoBannerView.Kind.SUCCESS, R.string.auth_reset_link_sent);
                break;
            case TOO_MANY_ATTEMPTS:
                showBanner(InfoBannerView.Kind.DANGER, R.string.auth_error_too_many);
                break;
            case NETWORK:
                showBanner(InfoBannerView.Kind.DANGER, R.string.auth_error_network);
                break;
            case UNKNOWN:
                showBanner(InfoBannerView.Kind.DANGER, R.string.auth_error_unknown);
                break;
            default:
                break;
        }
    }

    private void showBanner(InfoBannerView.Kind kind, @StringRes int message) {
        binding.banner.setKind(kind);
        binding.banner.setText(getString(message));
        binding.banner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
