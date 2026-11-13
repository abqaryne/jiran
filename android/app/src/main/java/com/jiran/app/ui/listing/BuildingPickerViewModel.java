package com.jiran.app.ui.listing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.AreaOption;
import com.jiran.app.data.AreaSource;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Form state only: no geocoding, Listing writes, or private address lookup. */
public final class BuildingPickerViewModel extends ViewModel {
    public enum State { IDLE, LOADING, READY, EMPTY, SIGN_IN_REQUIRED, ERROR }

    private final AreaSource source;
    private final MutableLiveData<State> state = new MutableLiveData<>(State.IDLE);
    private final MutableLiveData<Boolean> canConfirm = new MutableLiveData<>(false);
    private List<AreaOption> areas = Collections.emptyList();
    private String selectedId = "";
    private String building = "";
    private String areaText = "";
    private int generation;

    public BuildingPickerViewModel(AreaSource source) {
        this.source = source;
    }

    public LiveData<State> state() { return state; }
    public LiveData<Boolean> canConfirm() { return canConfirm; }
    public List<AreaOption> areas() { return areas; }
    public String building() { return building; }
    public String areaText() { return areaText; }
    public String selectedId() { return selectedId; }

    public void restore(String building, String areaText, String selectedId) {
        this.building = building;
        this.areaText = areaText;
        this.selectedId = selectedId;
    }

    public void load() {
        if (state.getValue() == State.LOADING) {
            return;
        }
        int request = ++generation;
        canConfirm.setValue(false);
        state.setValue(State.LOADING);
        source.load(new AreaSource.Callback() {
            @Override
            public void onLoaded(List<AreaOption> loaded) {
                if (request != generation) {
                    return;
                }
                areas = Collections.unmodifiableList(new ArrayList<>(loaded));
                AreaOption selected = selectedArea();
                if (selected == null || !selected.name.equals(areaText)) {
                    selectedId = "";
                }
                state.setValue(areas.isEmpty() ? State.EMPTY : State.READY);
                validate();
            }

            @Override
            public void onError(AreaSource.Problem problem) {
                if (request != generation) {
                    return;
                }
                areas = Collections.emptyList();
                selectedId = "";
                state.setValue(problem == AreaSource.Problem.SIGN_IN_REQUIRED
                        ? State.SIGN_IN_REQUIRED : State.ERROR);
                validate();
            }
        });
    }

    public void setBuilding(String value) {
        building = value;
        validate();
    }

    public void setAreaText(String value) {
        areaText = value;
        AreaOption selected = selectedArea();
        if (selected == null || !selected.name.equals(value)) {
            selectedId = "";
        }
        validate();
    }

    public void selectArea(String id) {
        selectedId = id;
        AreaOption selected = selectedArea();
        if (selected != null) {
            areaText = selected.name;
        }
        validate();
    }

    public AreaOption selectedArea() {
        for (AreaOption area : areas) {
            if (area.id.equals(selectedId)) {
                return area;
            }
        }
        return null;
    }

    public static String normalizeBuilding(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFC)
                .replaceAll("[\\p{Z}\\s]+", " ").trim().toLowerCase(Locale.ROOT);
    }

    public boolean validBuilding() {
        String value = normalizeBuilding(building);
        return !value.isEmpty() && value.length() <= 200
                && value.codePoints().noneMatch(Character::isISOControl);
    }

    private void validate() {
        canConfirm.setValue(state.getValue() == State.READY && selectedArea() != null && validBuilding());
    }

    @Override
    protected void onCleared() {
        generation++;
    }
}
