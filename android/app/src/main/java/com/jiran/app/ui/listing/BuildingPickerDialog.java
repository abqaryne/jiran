package com.jiran.app.ui.listing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jiran.app.R;
import com.jiran.app.data.AreaOption;
import com.jiran.app.data.AreaSource;
import com.jiran.app.data.FirestoreAreaSource;
import com.jiran.app.databinding.DialogBuildingPickerBinding;
import com.jiran.app.ui.common.InfoBannerView;
import java.util.ArrayList;

/** Reusable picker. The future Post form consumes RESULT_KEY through FragmentResultListener. */
public class BuildingPickerDialog extends BottomSheetDialogFragment {
    public static final String RESULT_KEY = "building_picker_selection";
    private DialogBuildingPickerBinding binding;
    private BuildingPickerViewModel model;

    protected AreaSource createAreaSource() { return new FirestoreAreaSource(); }
    protected boolean isSamplePreview() { return false; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> type) {
                BuildingPickerViewModel created = new BuildingPickerViewModel(createAreaSource());
                Bundle input = savedInstanceState != null ? savedInstanceState : getArguments();
                if (input != null) {
                    created.restore(input.getString("buildingName", ""),
                            input.getString("areaName", ""), input.getString("areaId", ""));
                }
                return (T) created;
            }
        }).get(BuildingPickerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DialogBuildingPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.close.setOnClickListener(button -> dismiss());
        binding.locationNote.setText(getString(isSamplePreview()
                ? R.string.building_picker_sample : R.string.building_picker_estimate));
        binding.buildingName.setText(model.building());
        binding.area.setText(model.areaText(), false);
        binding.buildingName.addTextChangedListener(watcher(value -> model.setBuilding(value)));
        binding.area.addTextChangedListener(watcher(value -> model.setAreaText(value)));
        binding.area.setOnItemClickListener((parent, selectedView, position, id) -> {
            AreaOption selected = (AreaOption) parent.getItemAtPosition(position);
            model.selectArea(selected.id);
        });
        binding.area.setOnClickListener(field -> binding.area.showDropDown());
        binding.retry.setOnClickListener(button -> model.load());
        binding.confirm.setOnClickListener(button -> confirm());
        model.state().observe(getViewLifecycleOwner(), this::render);
        model.canConfirm().observe(getViewLifecycleOwner(), ready -> {
            binding.confirm.setEnabled(Boolean.TRUE.equals(ready));
            binding.buildingLayout.setError(model.building().isEmpty() || model.validBuilding()
                    ? null : getString(R.string.building_picker_invalid_name));
            if (model.state().getValue() == BuildingPickerViewModel.State.READY) {
                renderSelection();
            }
        });
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_16);
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, insets) -> {
            androidx.core.graphics.Insets system = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int bottom = Math.max(system.bottom, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom);
            target.setPadding(padding + system.left, padding + system.top,
                    padding + system.right, padding + bottom);
            return insets;
        });
        if (model.state().getValue() == BuildingPickerViewModel.State.IDLE) {
            model.load();
        }
    }

    private void render(BuildingPickerViewModel.State state) {
        boolean ready = state == BuildingPickerViewModel.State.READY;
        binding.area.setEnabled(ready);
        binding.retry.setVisibility(state == BuildingPickerViewModel.State.ERROR
                || state == BuildingPickerViewModel.State.EMPTY
                || state == BuildingPickerViewModel.State.SIGN_IN_REQUIRED ? View.VISIBLE : View.GONE);
        int message;
        switch (state) {
            case EMPTY:
                message = R.string.building_picker_empty;
                break;
            case ERROR:
                message = R.string.building_picker_error;
                break;
            case SIGN_IN_REQUIRED:
                message = R.string.building_picker_auth;
                break;
            case READY:
                binding.area.setAdapter(new ArrayAdapter<>(requireContext(),
                        R.layout.item_building_area, new ArrayList<>(model.areas())));
                binding.area.setText(model.areaText(), false);
                renderSelection();
                return;
            default:
                message = R.string.building_picker_loading;
                break;
        }
        binding.status.setKind(state == BuildingPickerViewModel.State.ERROR
                ? InfoBannerView.Kind.DANGER : InfoBannerView.Kind.INFO);
        binding.status.setText(getString(message));
    }

    private void renderSelection() {
        AreaOption selected = model.selectedArea();
        binding.status.setKind(selected == null ? InfoBannerView.Kind.INFO : InfoBannerView.Kind.SUCCESS);
        binding.status.setText(selected == null ? getString(R.string.building_picker_choose_area)
                : getString(R.string.building_picker_selected, selected.name));
    }

    private void confirm() {
        if (!Boolean.TRUE.equals(model.canConfirm().getValue())) {
            return;
        }
        AreaOption selected = model.selectedArea();
        Bundle result = new Bundle();
        result.putString("areaId", selected.id);
        result.putString("areaName", selected.name);
        result.putString("buildingName", BuildingPickerViewModel.normalizeBuilding(model.building()));
        result.putDouble("areaLatitude", selected.latitude);
        result.putDouble("areaLongitude", selected.longitude);
        result.putString("coordinateSource", "area_center_estimate");
        result.putBoolean("sampleData", isSamplePreview());
        // No placeId or exactAddress: neither is supplied by manual area selection.
        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) requireDialog();
        View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet != null) {
            sheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            BottomSheetBehavior.from(sheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        ViewCompat.requestApplyInsets(binding.getRoot());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("buildingName", model.building());
        outState.putString("areaName", model.areaText());
        outState.putString("areaId", model.selectedId());
    }

    private interface TextChange { void accept(String value); }

    private static TextWatcher watcher(TextChange callback) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                callback.accept(text.toString());
            }
            @Override
            public void afterTextChanged(Editable text) { }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
