package com.jiran.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jiran.app.R;
import com.jiran.app.databinding.FragmentProfilePrivacyBinding;
import com.jiran.app.databinding.ItemProfileSwitchRowBinding;

/**
 * Step 3, Figma "Profile 3 · Privacy". Each switch on means "hidden", and all start on (private by default). The
 * Security Rules read the phone switch; the budget and photo switches are stored for the screens that show them later.
 */
public class PrivacyStepFragment extends Fragment {

    private FragmentProfilePrivacyBinding binding;
    private ProfileSetupViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfilePrivacyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new ProfileViewModelFactory())
                .get(ProfileSetupViewModel.class);
        setUp(binding.phoneRow, R.drawable.ic_call, R.string.profile_privacy_phone,
                R.string.profile_privacy_phone_detail, true, savedInstanceState);
        setUp(binding.budgetRow, R.drawable.ic_payments, R.string.profile_privacy_budget,
                R.string.profile_privacy_budget_detail, true, savedInstanceState);
        setUp(binding.photoRow, R.drawable.ic_face, R.string.profile_privacy_photo,
                R.string.profile_privacy_photo_detail, false, savedInstanceState);
        binding.next.setOnClickListener(button -> viewModel.submitPrivacy(binding.phoneRow.toggle.isChecked(),
                binding.budgetRow.toggle.isChecked(), binding.photoRow.toggle.isChecked()));
        viewModel.state().observe(getViewLifecycleOwner(), state -> {
            boolean saving = state == ProfileSetupViewModel.State.SAVING;
            binding.next.setEnabled(!saving);
            binding.next.setText(saving ? R.string.profile_saving : R.string.profile_finish);
        });
    }

    private void setUp(ItemProfileSwitchRowBinding row, @DrawableRes int icon, @StringRes int title,
            @StringRes int detail, boolean divider, @Nullable Bundle savedInstanceState) {
        row.icon.setImageResource(icon);
        row.title.setText(title);
        row.subtitle.setText(detail);
        row.toggle.setContentDescription(getString(title));
        if (savedInstanceState == null) {
            row.toggle.setChecked(true);
        }
        if (divider) {
            row.rowRoot.setBackgroundResource(R.drawable.bg_divider_bottom);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
