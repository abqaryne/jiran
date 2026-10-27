package com.jiran.app.ui.rooms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jiran.app.R;
import com.jiran.app.databinding.FragmentPlaceholderBinding;

/** Rooms tab (Module 2). Placeholder that already shows the Fragment + ViewModel wiring. */
public class RoomsFragment extends Fragment {

    private FragmentPlaceholderBinding binding;
    private RoomsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceholderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        binding.title.setText(R.string.rooms_title);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
