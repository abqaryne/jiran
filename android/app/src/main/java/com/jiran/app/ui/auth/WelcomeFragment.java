package com.jiran.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.jiran.app.R;
import com.jiran.app.databinding.FragmentWelcomeBinding;
import com.jiran.app.ui.common.JiranSnackbar;

/** Figma: Welcome. The first screen a new user sees. */
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signUp.setOnClickListener(button ->
                NavHostFragment.findNavController(this).navigate(R.id.action_welcome_to_sign_up));
        // Log in is built in E1.2.
        binding.logIn.setOnClickListener(button ->
                JiranSnackbar.make(binding.getRoot(), getString(R.string.auth_login_soon)).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
