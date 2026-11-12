package com.jiran.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jiran.app.R;
import com.jiran.app.databinding.FragmentProfilePreferencesBinding;
import com.jiran.app.domain.profile.ProfileCheck.Problem;

/** Step 2, Profile step 2: monthly Room budget, car ownership and About you tags; no timetable or campus-day count. */
public class PreferencesStepFragment extends Fragment {

    private FragmentProfilePreferencesBinding binding;
    private ProfileSetupViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfilePreferencesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new ProfileViewModelFactory())
                .get(ProfileSetupViewModel.class);
        binding.carRow.icon.setImageResource(R.drawable.ic_directions_car);
        binding.carRow.title.setText(R.string.profile_has_car);
        binding.carRow.subtitle.setText(R.string.profile_has_car_detail);
        binding.carRow.toggle.setContentDescription(getString(R.string.profile_has_car));
        Chips.fill(binding.lifestyle, getResources().getStringArray(R.array.profile_lifestyle_keys),
                getResources().getStringArray(R.array.profile_lifestyle_labels));
        binding.next.setOnClickListener(button -> {
            viewModel.submitPreferences(text(binding.budgetMin), text(binding.budgetMax),
                    Chips.checked(binding.lifestyle),
                    binding.carRow.toggle.isChecked());
        });
        viewModel.fieldProblems().observe(getViewLifecycleOwner(), problems -> {
            if (problems != null) {
                setError(binding.budgetMinLayout, problems.budgetMin);
                setError(binding.budgetMaxLayout, problems.budgetMax);
            }
        });
        viewModel.state().observe(getViewLifecycleOwner(), state -> {
            boolean saving = state == ProfileSetupViewModel.State.SAVING;
            binding.next.setEnabled(!saving);
            binding.next.setText(saving ? R.string.profile_saving : R.string.profile_next);
        });
    }

    private static String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private void setError(TextInputLayout layout, Problem problem) {
        switch (problem) {
            case REQUIRED:
                layout.setError(getString(R.string.profile_error_required));
                break;
            case OUT_OF_RANGE:
                layout.setError(getString(R.string.profile_error_budget_amount));
                break;
            case MIN_ABOVE_MAX:
                layout.setError(getString(R.string.profile_error_budget_min_above_max));
                break;
            default:
                layout.setError(null);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
