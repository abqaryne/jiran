package com.jiran.app.ui.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jiran.app.R;
import com.jiran.app.databinding.FragmentPlaceholderBinding;

/** Unit tab. Placeholder until the feature is built: replace the layout and add a ViewModel. */
public class UnitFragment extends Fragment {

    private FragmentPlaceholderBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceholderBinding.inflate(inflater, container, false);
        binding.title.setText(R.string.unit_title);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
