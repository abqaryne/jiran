package com.jiran.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.ProfileRepository;
import com.jiran.app.domain.profile.ProfileCheck;
import com.jiran.app.domain.profile.ProfileCheck.Problem;
import com.jiran.app.domain.profile.Ringgit;
import com.jiran.app.domain.profile.SetupProgress;
import java.util.List;

/**
 * State of the three profile setup steps (FR1.3). It checks each step with the plain-Java rules in
 * {@code domain/profile}, saves a step only when it is valid, and moves to the next step. Each step is saved on its
 * own, so closing the app halfway continues at the first unsaved step. It holds no View or Context.
 */
public class ProfileSetupViewModel extends ViewModel {

    /** Where the current request is. */
    public enum State { LOADING, IDLE, SAVING, DONE, NETWORK, SIGN_IN_REQUIRED, UNKNOWN }

    /** Field problems of the step the student just submitted. Problems of other steps stay NONE. */
    public static final class FieldProblems {
        public final Problem name;
        public final Problem gender;
        public final Problem faculty;
        public final Problem year;
        public final Problem budgetMin;
        public final Problem budgetMax;

        FieldProblems(Problem name, Problem gender, Problem faculty, Problem year, Problem budgetMin,
                Problem budgetMax) {
            this.name = name;
            this.gender = gender;
            this.faculty = faculty;
            this.year = year;
            this.budgetMin = budgetMin;
            this.budgetMax = budgetMax;
        }

        boolean isValid() {
            return name == Problem.NONE && gender == Problem.NONE && faculty == Problem.NONE
                    && year == Problem.NONE && budgetMin == Problem.NONE && budgetMax == Problem.NONE;
        }
    }

    private final ProfileRepository repository;
    private final MutableLiveData<Integer> step = new MutableLiveData<>(1);
    private final MutableLiveData<State> state = new MutableLiveData<>(State.LOADING);
    private final MutableLiveData<FieldProblems> fieldProblems = new MutableLiveData<>();

    public ProfileSetupViewModel(ProfileRepository repository) {
        this.repository = repository;
    }

    public LiveData<Integer> step() {
        return step;
    }

    public LiveData<State> state() {
        return state;
    }

    public LiveData<FieldProblems> fieldProblems() {
        return fieldProblems;
    }

    /** The name from sign-up; step 1 asks for it only when this is empty. */
    public String signUpName() {
        return repository.signUpName();
    }

    /** Reads what is already saved and opens the first unsaved step, or DONE when everything is saved. */
    public void start() {
        state.setValue(State.LOADING);
        repository.loadProgress(new ProfileRepository.ProgressCallback() {
            @Override
            public void onLoaded(SetupProgress progress) {
                if (progress.isComplete()) {
                    state.setValue(State.DONE);
                } else {
                    step.setValue(progress.nextStep());
                    state.setValue(State.IDLE);
                }
            }

            @Override
            public void onError(ProfileRepository.Problem problem) {
                state.setValue(stateFor(problem));
            }
        });
    }

    /** Step 1. {@code fullName} is the sign-up name, or what the student typed when that is missing. */
    public void submitBasics(String fullName, String gender, String faculty, Integer year) {
        if (busy()) {
            return;
        }
        Problem name = fullName == null || fullName.trim().isEmpty() ? Problem.REQUIRED : Problem.NONE;
        FieldProblems check = new FieldProblems(name, ProfileCheck.gender(gender), ProfileCheck.faculty(faculty),
                ProfileCheck.year(year), Problem.NONE, Problem.NONE);
        fieldProblems.setValue(check);
        if (check.isValid()) {
            save(next -> repository.saveBasics(fullName, gender, faculty, year, next));
        }
    }

    /** Step 2. Budgets arrive as typed ringgit and are stored as integer sen; no attendance information is needed. */
    public void submitPreferences(String budgetMin, String budgetMax, List<String> lifestyleTags,
            boolean hasCar) {
        if (busy()) {
            return;
        }
        Long minSen = Ringgit.toSen(budgetMin);
        Long maxSen = Ringgit.toSen(budgetMax);
        Problem minProblem = minSen == null && budgetMin != null && !budgetMin.trim().isEmpty()
                ? Problem.OUT_OF_RANGE : ProfileCheck.budgetMin(minSen);
        Problem maxProblem = maxSen == null && budgetMax != null && !budgetMax.trim().isEmpty()
                ? Problem.OUT_OF_RANGE : ProfileCheck.budgetMax(minSen, maxSen);
        FieldProblems check = new FieldProblems(Problem.NONE, Problem.NONE, Problem.NONE, Problem.NONE,
                minProblem, maxProblem);
        fieldProblems.setValue(check);
        if (check.isValid()) {
            save(next -> repository.savePreferences(minSen, maxSen, lifestyleTags, hasCar, next));
        }
    }

    /**
     * Step 3: the privacy switches as the screen shows them (on = hidden). Saving them finishes setup. They are
     * stored the way the Security Rules read them, so "hide phone" becomes {@code showPhoneToConnections = false}.
     */
    public void submitPrivacy(boolean hidePhone, boolean hideBudget, boolean photoInitialsOnly) {
        if (!busy()) {
            save(next -> repository.savePrivacy(!hidePhone, !hideBudget, photoInitialsOnly, next));
        }
    }

    /** The back button on steps 2 and 3. Saved steps stay saved. */
    public void back() {
        Integer current = step.getValue();
        if (!busy() && current != null && current > 1) {
            fieldProblems.setValue(null);
            step.setValue(current - 1);
        }
    }

    /** The screen calls this after it has shown an error message, so it is not shown twice. */
    public void consumeError() {
        State current = state.getValue();
        if (current == State.NETWORK || current == State.UNKNOWN || current == State.SIGN_IN_REQUIRED) {
            state.setValue(State.IDLE);
        }
    }

    private interface SaveCall {
        void run(ProfileRepository.Callback next);
    }

    private void save(SaveCall call) {
        state.setValue(State.SAVING);
        call.run(new ProfileRepository.Callback() {
            @Override
            public void onSaved() {
                int current = step.getValue() == null ? 1 : step.getValue();
                if (current >= SetupProgress.STEPS) {
                    state.setValue(State.DONE);
                } else {
                    fieldProblems.setValue(null);
                    step.setValue(current + 1);
                    state.setValue(State.IDLE);
                }
            }

            @Override
            public void onError(ProfileRepository.Problem problem) {
                state.setValue(stateFor(problem));
            }
        });
    }

    private boolean busy() {
        State current = state.getValue();
        return current == State.SAVING || current == State.LOADING;
    }


    private static State stateFor(ProfileRepository.Problem problem) {
        switch (problem) {
            case NETWORK:
                return State.NETWORK;
            case SIGN_IN_REQUIRED:
                return State.SIGN_IN_REQUIRED;
            default:
                return State.UNKNOWN;
        }
    }
}
