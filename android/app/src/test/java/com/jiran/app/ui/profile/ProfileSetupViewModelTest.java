package com.jiran.app.ui.profile;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.ProfileRepository;
import com.jiran.app.domain.profile.ProfileCheck.Problem;
import com.jiran.app.domain.profile.SetupProgress;
import com.jiran.app.ui.profile.ProfileSetupViewModel.State;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public final class ProfileSetupViewModelTest {
    @Rule
    public final InstantTaskExecutorRule instantTasks = new InstantTaskExecutorRule();

    private FakeProfileRepository repository;
    private ProfileSetupViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeProfileRepository();
        viewModel = new ProfileSetupViewModel(repository);
    }

    private void startAt(SetupProgress progress) {
        viewModel.start();
        repository.answerProgress(progress);
    }

    @Test
    public void resumesAtTheFirstUnsavedStep() {
        startAt(new SetupProgress(true, false, false));
        assertEquals(Integer.valueOf(2), viewModel.step().getValue());
        assertEquals(State.IDLE, viewModel.state().getValue());
    }

    @Test
    public void aCompleteProfileIsDoneStraightAway() {
        startAt(new SetupProgress(true, true, true));
        assertEquals(State.DONE, viewModel.state().getValue());
    }

    @Test
    public void invalidBasicsNeverReachTheBackend() {
        startAt(new SetupProgress(false, false, false));
        viewModel.submitBasics("Mei Ling Tan", null, "fsktm", 6);
        assertFalse(repository.calls.contains("saveBasics"));
        assertEquals(Problem.REQUIRED, viewModel.fieldProblems().getValue().gender);
        assertEquals(Problem.OUT_OF_RANGE, viewModel.fieldProblems().getValue().year);
    }

    @Test
    public void validBasicsSaveThenMoveToPreferences() {
        startAt(new SetupProgress(false, false, false));
        viewModel.submitBasics("Mei Ling Tan", "female", "fsktm", 2);
        assertEquals(State.SAVING, viewModel.state().getValue());
        repository.pending.onSaved();
        assertEquals(Integer.valueOf(2), viewModel.step().getValue());
        assertEquals(State.IDLE, viewModel.state().getValue());
    }

    @Test
    public void budgetsAreSavedInSenAndMinAboveMaxIsRejected() {
        startAt(new SetupProgress(true, false, false));
        viewModel.submitPreferences("600", "300", Collections.emptyList(), false);
        assertEquals(Problem.MIN_ABOVE_MAX, viewModel.fieldProblems().getValue().budgetMax);
        assertFalse(repository.calls.contains("savePreferences"));
        viewModel.submitPreferences("300", "600.50", Collections.singletonList("quiet"), true);
        assertEquals(30000L, repository.savedMinSen);
        assertEquals(60050L, repository.savedMaxSen);
        repository.pending.onSaved();
        assertEquals(Integer.valueOf(3), viewModel.step().getValue());
    }

    @Test
    public void typedTextThatIsNotAnAmountIsOutOfRange() {
        startAt(new SetupProgress(true, false, false));
        viewModel.submitPreferences("abc", "", Collections.emptyList(), false);
        assertEquals(Problem.OUT_OF_RANGE, viewModel.fieldProblems().getValue().budgetMin);
        assertEquals(Problem.REQUIRED, viewModel.fieldProblems().getValue().budgetMax);
        assertFalse(repository.calls.contains("savePreferences"));
    }

    @Test
    public void savingPrivacyFinishesSetup() {
        startAt(new SetupProgress(true, true, false));
        viewModel.submitPrivacy(true, true, true);
        assertArrayEquals(new boolean[] {false, false, true}, repository.savedPrivacy);
        repository.pending.onSaved();
        assertEquals(State.DONE, viewModel.state().getValue());
    }

    @Test
    public void aNetworkErrorKeepsTheStepAndCanBeCleared() {
        startAt(new SetupProgress(false, false, false));
        viewModel.submitBasics("Mei Ling Tan", "male", "fsktm", 1);
        repository.pending.onError(ProfileRepository.Problem.NETWORK);
        assertEquals(State.NETWORK, viewModel.state().getValue());
        assertEquals(Integer.valueOf(1), viewModel.step().getValue());
        viewModel.consumeError();
        assertEquals(State.IDLE, viewModel.state().getValue());
    }

    @Test
    public void backGoesToThePreviousStepButNotBeforeStepOne() {
        startAt(new SetupProgress(true, true, false));
        viewModel.back();
        assertEquals(Integer.valueOf(2), viewModel.step().getValue());
        viewModel.back();
        viewModel.back();
        assertEquals(Integer.valueOf(1), viewModel.step().getValue());
    }
}
