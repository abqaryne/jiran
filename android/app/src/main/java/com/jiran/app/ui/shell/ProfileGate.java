package com.jiran.app.ui.shell;

import com.jiran.app.data.ProfileRepository;
import com.jiran.app.domain.profile.SetupProgress;

/**
 * Profile gate (E1.3, E1.4, FR1.3): the tabs open only after the profile check answers that every required setup step
 * is saved. It fails closed: when the check itself fails (no connection, session ended) the answer is
 * {@link Listener#onCheckFailed}, never "open the tabs". The activity shows a retry screen for that case.
 * Pure Java, so it is tested with a fake {@link ProfileRepository}.
 */
final class ProfileGate {

    /** What the shell does with the answer. Called on the main thread, and not after {@link #cancel()}. */
    interface Listener {
        void onOpenTabs();

        void onNeedsSetup();

        void onCheckFailed(ProfileRepository.Problem problem);
    }

    private final ProfileRepository repository;
    private final Listener listener;
    private int generation;

    ProfileGate(ProfileRepository repository, Listener listener) {
        this.repository = repository;
        this.listener = listener;
    }

    /** Starts a check. A newer call or {@link #cancel()} makes an older answer be ignored. */
    void check() {
        int mine = ++generation;
        repository.loadProgress(new ProfileRepository.ProgressCallback() {
            @Override
            public void onLoaded(SetupProgress progress) {
                if (mine != generation) {
                    return;
                }
                if (progress.isComplete()) {
                    listener.onOpenTabs();
                } else {
                    listener.onNeedsSetup();
                }
            }

            @Override
            public void onError(ProfileRepository.Problem problem) {
                if (mine != generation) {
                    return;
                }
                listener.onCheckFailed(problem);
            }
        });
    }

    /** The screen is gone: ignore any answer that has not arrived yet. */
    void cancel() {
        generation++;
    }
}
