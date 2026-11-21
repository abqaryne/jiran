package com.jiran.app.ui.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AreaOption;
import com.jiran.app.data.AreaSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class BuildingPickerViewModelTest {
    @Rule public final InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
    private FakeAreas source;
    private BuildingPickerViewModel model;
    private final AreaOption area = new AreaOption("sample_a", "Sample area A", 0, 0);

    private static final class FakeAreas implements AreaSource {
        Callback pending;
        int requests;
        @Override
        public void load(Callback callback) {
            pending = callback;
            requests++;
        }
    }

    @Before
    public void setup() {
        source = new FakeAreas();
        model = new BuildingPickerViewModel(source);
    }

    private void ready() {
        model.load();
        source.pending.onLoaded(Arrays.asList(area));
        model.setBuilding("Fictional Building");
    }

    @Test
    public void typingAreaNameDoesNotSelectIt() {
        ready();
        model.setAreaText(area.name);
        assertFalse(model.canConfirm().getValue());
        model.selectArea(area.id);
        assertTrue(model.canConfirm().getValue());
    }

    @Test
    public void editingAreaClearsPreviousSelection() {
        ready();
        model.selectArea(area.id);
        model.setAreaText("Another area");
        assertNull(model.selectedArea());
        assertFalse(model.canConfirm().getValue());
    }

    @Test
    public void rejectsMissingUnknownAndInvalidBuildingSelections() {
        ready();
        model.selectArea("unknown");
        assertFalse(model.canConfirm().getValue());
        model.selectArea(area.id);
        for (String invalid : Arrays.asList("", "  ", String.join("", Collections.nCopies(201, "x")), "A\u0000B")) {
            model.setBuilding(invalid);
            assertFalse(model.canConfirm().getValue());
        }
    }

    @Test
    public void normalizesWhitespaceAndUnicodeWithoutInventingBuildingIdentity() {
        assertEquals("Bangunan A", BuildingPickerViewModel.normalizeBuilding("  Bangunan  A \n"));
        assertEquals("Café", BuildingPickerViewModel.normalizeBuilding("Café"));
    }

    @Test
    public void displayNameKeepsTypedCase() {
        assertEquals("Pantai Hillpark", BuildingPickerViewModel.normalizeBuilding("  Pantai  Hillpark\t"));
    }

    @Test
    public void caseAndWhitespaceVariantsAreTheSameBuilding() {
        ready();
        model.selectArea(area.id);
        for (String name : Arrays.asList("Pantai Hillpark", "pantai hillpark", "PANTAI HILLPARK",
                "  PaNtAi  HILLPARK\t")) {
            model.setBuilding(name);
            assertTrue(model.canConfirm().getValue());
            assertEquals("pantai hillpark", BuildingPickerViewModel.buildingKey(model.building()));
            assertTrue(BuildingPickerViewModel.sameBuilding("Pantai Hillpark", model.building()));
        }
    }

    @Test
    public void buildingKeyDoesNotDependOnPhoneLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("pantai hillpark", BuildingPickerViewModel.buildingKey("PANTAI HILLPARK"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void buildingKeyKeepsDistinctNamesAndIsIdempotent() {
        String key = BuildingPickerViewModel.buildingKey("  CAFÉ  A ");
        assertEquals("café a", key);
        assertEquals(key, BuildingPickerViewModel.buildingKey(key));
        assertFalse(BuildingPickerViewModel.sameBuilding("CAFÉ A", "Café B"));
        assertFalse(BuildingPickerViewModel.sameBuilding("CAFÉ A", "Cafe A"));
    }

    @Test
    public void loadingAndFailureBlockConfirmationAndRetryWorks() {
        ready();
        model.selectArea(area.id);
        model.load();
        model.load();
        assertEquals(2, source.requests);
        assertFalse(model.canConfirm().getValue());
        source.pending.onError(AreaSource.Problem.UNAVAILABLE);
        assertEquals(BuildingPickerViewModel.State.ERROR, model.state().getValue());
        assertFalse(model.canConfirm().getValue());
        model.load();
        source.pending.onLoaded(Arrays.asList(area));
        assertEquals(BuildingPickerViewModel.State.READY, model.state().getValue());
        assertNull(model.selectedArea());
    }

    @Test
    public void emptyAndUnauthorizedLoadsHaveDistinctStates() {
        model.load();
        source.pending.onLoaded(Collections.emptyList());
        assertEquals(BuildingPickerViewModel.State.EMPTY, model.state().getValue());
        model.load();
        source.pending.onError(AreaSource.Problem.SIGN_IN_REQUIRED);
        assertEquals(BuildingPickerViewModel.State.SIGN_IN_REQUIRED, model.state().getValue());
    }

    @Test
    public void restorationRequiresAreaToStillExistWithSameName() {
        model.restore("Fictional Building", area.name, area.id);
        model.load();
        assertFalse(model.canConfirm().getValue());
        source.pending.onLoaded(Arrays.asList(area));
        assertTrue(model.canConfirm().getValue());
        model.load();
        source.pending.onLoaded(Collections.emptyList());
        assertFalse(model.canConfirm().getValue());
        assertNull(model.selectedArea());
    }

    @Test
    public void olderCallbackCannotReplaceNewLoad() {
        model.load();
        AreaSource.Callback old = source.pending;
        old.onError(AreaSource.Problem.UNAVAILABLE);
        model.load();
        source.pending.onLoaded(Arrays.asList(area));
        old.onLoaded(Collections.emptyList());
        assertEquals(BuildingPickerViewModel.State.READY, model.state().getValue());
    }
}
