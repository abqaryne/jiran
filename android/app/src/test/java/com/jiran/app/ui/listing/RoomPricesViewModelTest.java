package com.jiran.app.ui.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;

public final class RoomPricesViewModelTest {
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
    private final Fake repository = new Fake();

    private RoomPricesViewModel loaded() {
        RoomPricesViewModel model = new RoomPricesViewModel("draft", repository);
        repository.load.success(new RoomRepository.Loaded(150000, Collections.emptyList()));
        return model;
    }

    private RoomPricesViewModel trio() {
        RoomPricesViewModel model = loaded();
        model.add("master");
        model.add("medium");
        model.add("small");
        model.applySplit();
        return model;
    }

    @Test public void waitsForLoadAndBlocksAnEmptyOrIncompleteForm() {
        RoomPricesViewModel model = new RoomPricesViewModel("draft", repository);
        model.add("master");
        model.save();
        assertTrue(model.rows().isEmpty());
        assertFalse(model.canSave());
        repository.load.success(new RoomRepository.Loaded(150000, Collections.emptyList()));
        assertFalse(model.canSave());
        model.add("master");
        assertNull(model.remainingSen());
        assertFalse(model.canSave());
        assertEquals(0, repository.saves);
    }

    @Test public void defaultSplitAndManualPriceShowExactRemainingSen() {
        RoomPricesViewModel model = trio();
        assertEquals("650.00", model.rows().get(0).price());
        assertEquals("500.00", model.rows().get(1).price());
        assertEquals("350.00", model.rows().get(2).price());
        assertEquals(BigInteger.ZERO, model.remainingSen());
        model.price(model.rows().get(2).id, "349.99");
        assertEquals(BigInteger.ONE, model.remainingSen());
        assertTrue(model.canSave());
    }

    @Test public void occupiedRoomCannotBeExcludedToHideOverAllocation() {
        RoomPricesViewModel model = trio();
        String master = model.rows().get(0).id;
        model.occupied(master, true);
        model.price(master, "650.01");
        assertEquals(BigInteger.valueOf(-1), model.remainingSen());
        model.save();
        assertFalse(model.canSave());
        assertEquals(0, repository.saves);
        model.price(master, "650");
        model.save();
        assertTrue(repository.saved.get(0).occupied);
        assertEquals(65000, repository.saved.get(0).priceSen);
    }

    @Test public void dynamicConfigOnlyChangesPricesWhenHostAppliesIt() {
        RoomPricesViewModel model = trio();
        repository.config.success(Collections.singletonMap("roomPriceWeights",
                Map.of("master", 2, "medium", 1, "small", 1)));
        assertEquals("650.00", model.rows().get(0).price());
        model.applySplit();
        assertEquals("750.00", model.rows().get(0).price());
        assertEquals("375.00", model.rows().get(1).price());
        assertFalse(model.configFallback());
    }

    @Test public void unavailableConfigUsesLabelledDefaultsAndMalformedConfigDisablesSplit() {
        RoomPricesViewModel model = trio();
        repository.config.failure(RoomRepository.Problem.NETWORK);
        assertTrue(model.configFallback());
        model.applySplit();
        assertEquals("650.00", model.rows().get(0).price());
        repository.config.success(Collections.singletonMap("roomPriceWeights", "bad"));
        assertTrue(model.configInvalid());
        model.price(model.rows().get(0).id, "600");
        model.applySplit();
        assertEquals("600", model.rows().get(0).price());
        assertTrue(model.canSave());
    }

    @Test public void invalidNumbersNeverBecomeZeroPrices() {
        RoomPricesViewModel model = trio();
        for (String text : Arrays.asList("", "-1", "1.001", "NaN", "9999999999999999", "abc")) {
            model.price(model.rows().get(0).id, text);
            assertNull(model.remainingSen());
            assertFalse(model.canSave());
            model.save();
        }
        assertEquals(0, repository.saves);
    }

    @Test public void savingLocksEditsAndWaitsForAcknowledgement() {
        RoomPricesViewModel model = trio();
        String id = model.rows().get(0).id;
        model.save();
        model.save();
        model.price(id, "1");
        model.occupied(id, true);
        model.add("small");
        assertEquals(1, repository.saves);
        assertEquals(3, model.rows().size());
        assertEquals("650.00", model.rows().get(0).price());
        assertFalse(model.rows().get(0).occupied());
        assertEquals(RoomPricesViewModel.State.SAVING, model.state());
        repository.save.success(null);
        assertEquals(RoomPricesViewModel.State.SAVED, model.state());
        assertFalse(model.dirty());
        assertFalse(model.canSave());
    }

    @Test public void failedOrUncertainSaveRequiresReloadAndKeepsEditsUntilThen() {
        RoomPricesViewModel model = trio();
        model.save();
        repository.save.failure(RoomRepository.Problem.NETWORK);
        assertEquals(RoomPricesViewModel.State.SAVE_ERROR, model.state());
        assertEquals("650.00", model.rows().get(0).price());
        model.save();
        assertEquals(1, repository.saves);
        model.reload();
        repository.load.success(new RoomRepository.Loaded(150000, repository.saved));
        assertTrue(model.rows().get(0).persisted);
        assertFalse(model.dirty());
    }

    @Test public void staleEditRequiresReload() {
        RoomPricesViewModel model = trio();
        model.save();
        repository.save.failure(RoomRepository.Problem.CONFLICT);
        assertEquals(RoomRepository.Problem.CONFLICT, model.problem());
        assertFalse(model.editable());
        assertFalse(model.canSave());
    }

    @Test public void existingOccupiedIdentityPreservedUntilMarkedVacant() {
        RoomPricesViewModel model = new RoomPricesViewModel("draft", repository);
        repository.load.success(new RoomRepository.Loaded(150000, Collections.singletonList(
                new RoomAllocation("existing", "master", 150000, true, "fictional-resident"))));
        model.removeNew("existing");
        assertEquals(1, model.rows().size());
        model.save();
        assertEquals("fictional-resident", repository.saved.get(0).occupiedByUid);
        repository.save.failure(RoomRepository.Problem.NETWORK);
        model.reload();
        repository.load.success(new RoomRepository.Loaded(150000, repository.saved));
        model.occupied("existing", false);
        model.save();
        assertNull(repository.saved.get(0).occupiedByUid);
    }

    @Test public void unsavedRoomsCanBeRemovedWithoutChangingOtherIds() {
        RoomPricesViewModel model = trio();
        String retained = model.rows().get(0).id;
        model.removeNew(model.rows().get(2).id);
        assertEquals(2, model.rows().size());
        assertEquals(retained, model.rows().get(0).id);
        assertEquals(BigInteger.valueOf(35000), model.remainingSen());
    }

    @Test public void sessionEndRemovesConfigListenerAndIgnoresLateCallbacks() {
        RoomPricesViewModel model = trio();
        model.save();
        model.endSession();
        repository.save.success(null);
        repository.load.success(new RoomRepository.Loaded(150000, repository.saved));
        repository.config.success(Collections.emptyMap());
        assertEquals(RoomPricesViewModel.State.AUTH, model.state());
        assertTrue(repository.removed);
        assertTrue(model.rows().isEmpty());
        assertFalse(model.canSave());
    }

    @Test public void authFailureClearsRooms() {
        RoomPricesViewModel model = trio();
        repository.config.failure(RoomRepository.Problem.AUTH);
        assertEquals(RoomPricesViewModel.State.AUTH, model.state());
        assertTrue(model.rows().isEmpty());
    }

    @Test public void failedLoadDoesNotEnableEditing() {
        RoomPricesViewModel model = new RoomPricesViewModel("draft", repository);
        repository.load.failure(RoomRepository.Problem.NETWORK);
        model.add("master");
        assertEquals(RoomPricesViewModel.State.LOAD_ERROR, model.state());
        assertTrue(model.rows().isEmpty());
    }

    private static final class Fake implements RoomRepository {
        Callback<Loaded> load;
        Callback<Map<String, ?>> config;
        Callback<Void> save;
        List<RoomAllocation> saved;
        int saves;
        boolean removed;
        @Override public void load(String listingId, Callback<Loaded> callback) { load = callback; }
        @Override public Subscription observeConfig(Callback<Map<String, ?>> callback) {
            config = callback;
            return () -> removed = true;
        }
        @Override public void save(String listingId, List<RoomAllocation> rooms, Callback<Void> callback) {
            saved = rooms;
            saves++;
            save = callback;
        }
    }
}
