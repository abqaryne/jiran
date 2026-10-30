package com.jiran.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jiran.app.data.FirebaseAccountService;
import com.jiran.app.databinding.FragmentHomeBinding;
import com.jiran.app.ui.auth.LauncherActivity;

/** Home tab. Placeholder until the feature is built; it only offers Log out for now (moves to settings in E10.1). */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.logOut.setOnClickListener(button -> {
            new FirebaseAccountService().signOut();
            // Start again from the front door and drop every screen of the signed-in session.
            Intent restart = new Intent(requireContext(), LauncherActivity.class);
            restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(restart);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
