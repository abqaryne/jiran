package com.jiran.app.ui.shell;

import static org.junit.Assert.assertEquals;

import com.jiran.app.data.ProfileRepository;
import com.jiran.app.domain.profile.SetupProgress;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public final class ProfileGateTest {

    private final List<String> answers = new ArrayList<>();
    private ProfileRepository.ProgressCallback pending;
    private int loads;
    private ProfileGate gate;

    @Before
    public void setUp() {
        ProfileRepository repository = new ProfileRepository() {
            @Override
            public String signUpName() {
                return "";
            }

            @Override
            public void loadProgress(ProgressCallback callback) {
                loads++;
                pending = callback;
            }

            @Override
            public void saveBasics(String fullName, String gender, String faculty, int year, Callback callback) {
            }

            @Override
            public void savePreferences(long budgetMinSen, long budgetMaxSen, List<String> lifestyleTags,
                    boolean hasCar, Callback callback) {
            }

            @Override
            public void savePrivacy(boolean phone, boolean budget, boolean photo, Callback callback) {
            }
        };
        gate = new ProfileGate(repository, new ProfileGate.Listener() {
            @Override
            public void onOpenTabs() {
                answers.add("tabs");
            }

            @Override
            public void onNeedsSetup() {
                answers.add("setup");
            }

            @Override
            public void onCheckFailed(ProfileRepository.Problem problem) {
                answers.add("failed:" + problem);
            }
        });
    }

    @Test
    public void nothingOpensBeforeTheCheckAnswers() {
        gate.check();
        assertEquals(1, loads);
        assertEquals(0, answers.size());
    }

    @Test
    public void aCompleteProfileOpensTheTabs() {
        gate.check();
        pending.onLoaded(new SetupProgress(true, true, true));
        assertEquals(List.of("tabs"), answers);
    }

    @Test
    public void anIncompleteProfileGoesToSetup() {
        gate.check();
        pending.onLoaded(new SetupProgress(true, false, false));
        assertEquals(List.of("setup"), answers);
    }

    @Test
    public void aFailedCheckNeverOpensTheTabs() {
        gate.check();
        pending.onError(ProfileRepository.Problem.NETWORK);
        assertEquals(List.of("failed:NETWORK"), answers);
        gate.check();
        pending.onError(ProfileRepository.Problem.SIGN_IN_REQUIRED);
        assertEquals(List.of("failed:NETWORK", "failed:SIGN_IN_REQUIRED"), answers);
    }

    @Test
    public void retryAfterAFailureCanOpenTheTabs() {
        gate.check();
        pending.onError(ProfileRepository.Problem.NETWORK);
        gate.check();
        pending.onLoaded(new SetupProgress(true, true, true));
        assertEquals(List.of("failed:NETWORK", "tabs"), answers);
    }

    @Test
    public void anAnswerAfterCancelIsIgnored() {
        gate.check();
        gate.cancel();
        pending.onLoaded(new SetupProgress(true, true, true));
        assertEquals(0, answers.size());
    }

    @Test
    public void anOlderAnswerIsIgnoredWhenANewCheckStarted() {
        gate.check();
        ProfileRepository.ProgressCallback first = pending;
        gate.check();
        first.onLoaded(new SetupProgress(true, true, true));
        assertEquals(0, answers.size());
        pending.onLoaded(new SetupProgress(false, false, false));
        assertEquals(List.of("setup"), answers);
    }
}
