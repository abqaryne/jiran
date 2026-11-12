package com.jiran.app.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.chip.Chip;
import com.jiran.app.R;
import com.jiran.app.databinding.FragmentProfileBasicsBinding;
import com.jiran.app.domain.profile.ProfileCheck;
import com.jiran.app.domain.profile.ProfileCheck.Problem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Step 1, Figma "Profile 1 · Faculty": search or pick the faculty, then the year of study. Gender (required) and the
 * name field (only when sign-up stored no name) sit below the Figma content.
 */
public class BasicsStepFragment extends Fragment {

    private FragmentProfileBasicsBinding binding;
    private ProfileSetupViewModel viewModel;
    private String[] facultyKeys;
    private String[] facultyLabels;
    private String[] facultyNames;
    private int popular;
    @Nullable
    private String selectedFaculty;
    private boolean fillingSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBasicsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new ProfileViewModelFactory())
                .get(ProfileSetupViewModel.class);
        facultyKeys = getResources().getStringArray(R.array.profile_faculty_keys);
        facultyLabels = getResources().getStringArray(R.array.profile_faculty_labels);
        facultyNames = getResources().getStringArray(R.array.profile_faculty_names);
        popular = getResources().getInteger(R.integer.profile_popular_faculties);
        if (savedInstanceState != null) {
            selectedFaculty = savedInstanceState.getString("faculty");
        }
        showPopularFaculties();
        FacultyAdapter suggestions = new FacultyAdapter();
        binding.facultySearch.setAdapter(suggestions);
        binding.facultySearch.setOnItemClickListener((parent, row, position, id) -> {
            String name = suggestions.getItem(position);
            for (int i = 0; i < facultyNames.length; i++) {
                if (facultyNames[i].equals(name)) {
                    pickFaculty(facultyKeys[i]);
                    break;
                }
            }
        });
        binding.facultySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!fillingSearch) {
                    String query = s.toString();
                    if (!isSelectedName(query)) {
                        selectedFaculty = null;
                        binding.faculty.clearCheck();
                    }
                    updateFacultySearch(query);
                }
            }
        });

        String[] yearKeys = new String[ProfileCheck.MAX_YEAR];
        String[] yearLabels = new String[ProfileCheck.MAX_YEAR];
        for (int i = 0; i < yearKeys.length; i++) {
            int year = i + ProfileCheck.MIN_YEAR;
            yearKeys[i] = String.valueOf(year);
            yearLabels[i] = year == ProfileCheck.POSTGRADUATE ? getString(R.string.profile_year_postgraduate)
                    : getString(R.string.profile_year_n, year);
        }
        Chips.fill(binding.year, yearKeys, yearLabels);

        boolean askName = viewModel.signUpName().isEmpty();
        binding.nameLayout.setVisibility(askName ? View.VISIBLE : View.GONE);
        binding.next.setOnClickListener(button -> {
            String name = askName ? text(binding.name) : viewModel.signUpName();
            String year = Chips.single(binding.year);
            viewModel.submitBasics(name, Chips.single(binding.gender), selectedFaculty,
                    year == null ? null : Integer.valueOf(year));
        });
        viewModel.fieldProblems().observe(getViewLifecycleOwner(), problems -> {
            if (problems == null) {
                return;
            }
            binding.nameLayout.setError(problems.name == Problem.NONE ? null
                    : getString(R.string.profile_error_required));
            show(binding.facultyError, problems.faculty);
            show(binding.yearError, problems.year);
            show(binding.genderError, problems.gender);
        });
        viewModel.state().observe(getViewLifecycleOwner(), state -> {
            boolean saving = state == ProfileSetupViewModel.State.SAVING;
            binding.next.setEnabled(!saving);
            binding.next.setText(saving ? R.string.profile_saving : R.string.profile_next);
        });
    }

    /** Popular shortcuts stay separate from the dropdown used for typed searches. */
    private void showPopularFaculties() {
        String[] keys = java.util.Arrays.copyOf(facultyKeys, popular);
        String[] labels = java.util.Arrays.copyOf(facultyLabels, popular);
        Chips.fill(binding.faculty, keys, labels);
        syncFacultyChips();
        binding.faculty.setOnCheckedStateChangeListener((group, ids) -> pickFaculty(Chips.single(group)));
    }

    private boolean isSelectedName(String query) {
        for (int i = 0; i < facultyKeys.length; i++) {
            if (facultyKeys[i].equals(selectedFaculty)) {
                return facultyNames[i].equals(query);
            }
        }
        return false;
    }

    private boolean matchesFaculty(int index, String query) {
        return facultyNames[index].toLowerCase(Locale.ROOT).contains(query)
                || facultyLabels[index].toLowerCase(Locale.ROOT).contains(query);
    }

    private void updateFacultySearch(String query) {
        String needle = query.trim().toLowerCase(Locale.ROOT);
        boolean empty = needle.isEmpty();
        boolean found = empty;
        for (int i = 0; i < facultyKeys.length && !found; i++) {
            found = matchesFaculty(i, needle);
        }
        binding.facultySearchLayout.setHelperText(found ? null : getString(R.string.profile_faculty_none));
        if (empty) {
            binding.facultySearch.dismissDropDown();
        }
    }

    private void syncFacultyChips() {
        for (int i = 0; i < binding.faculty.getChildCount(); i++) {
            Chip chip = (Chip) binding.faculty.getChildAt(i);
            chip.setChecked(chip.getTag().equals(selectedFaculty));
        }
    }

    /** Both a shortcut and a dropdown item select the same stored faculty code. */
    private void pickFaculty(@Nullable String key) {
        if (key == null) {
            return;
        }
        selectedFaculty = key;
        for (int i = 0; i < facultyKeys.length; i++) {
            if (facultyKeys[i].equals(key)) {
                fillingSearch = true;
                binding.facultySearch.setText(facultyNames[i], false);
                binding.facultySearch.setSelection(facultyNames[i].length());
                fillingSearch = false;
                binding.facultySearch.dismissDropDown();
                binding.facultyError.setVisibility(View.GONE);
                updateFacultySearch(facultyNames[i]);
                syncFacultyChips();
                break;
            }
        }
    }

    /** Match anywhere in the full name or shortcut (for example FSKTM), displaying full names. */
    private final class FacultyAdapter extends ArrayAdapter<String> {
        private final Filter facultyFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().trim().toLowerCase(Locale.ROOT);
                List<String> names = new ArrayList<>();
                if (!query.isEmpty()) {
                    for (int i = 0; i < facultyNames.length; i++) {
                        if (matchesFaculty(i, query)) {
                            names.add(facultyNames[i]);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = names;
                results.count = names.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setNotifyOnChange(false);
                clear();
                addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };

        FacultyAdapter() {
            super(requireContext(), R.layout.item_profile_faculty_dropdown, new ArrayList<>());
        }

        @Override
        public Filter getFilter() {
            return facultyFilter;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("faculty", selectedFaculty);
    }

    private static String text(TextView field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    private void show(TextView error, Problem problem) {
        error.setVisibility(problem == Problem.NONE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        binding.facultySearch.dismissDropDown();
        super.onDestroyView();
        binding = null;
    }
}
