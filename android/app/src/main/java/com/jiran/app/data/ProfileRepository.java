package com.jiran.app.data;

import com.jiran.app.domain.profile.SetupProgress;
import java.util.List;

/**
 * What profile setup (E1.3) needs from the backend. Like {@link AccountService}, the screens and view models only see
 * this interface, so they are tested with a fake and the Firestore calls stay in {@link FirestoreProfileRepository}.
 * Field names follow {@code docs/engineering/schema.md}. All callbacks arrive on the main thread.
 */
public interface ProfileRepository {

    /** Why a profile call failed. Screens turn this into a message. */
    enum Problem { SIGN_IN_REQUIRED, NETWORK, UNKNOWN }

    /** Result of a save. */
    interface Callback {
        void onSaved();

        void onError(Problem problem);
    }

    /** Result of {@link #loadProgress}. */
    interface ProgressCallback {
        void onLoaded(SetupProgress progress);

        void onError(Problem problem);
    }

    /** The full name given at sign-up, or "" when it is missing (setup then asks for it). */
    String signUpName();

    /** Reads which setup steps are already saved, so setup continues where it stopped. */
    void loadProgress(ProgressCallback callback);

    /**
     * Step 1. Public {@code users/{uid}}: initials, gender, faculty, year. Private: {@code fullName} in
     * {@code private/profile} and {@code firstName} in {@code private/contact} (Connection stage only).
     */
    void saveBasics(String fullName, String gender, String faculty, int year, Callback callback);

    /**
     * Step 2. Public {@code lifestyleTags} on {@code users/{uid}}; monthly Room budget and car ownership in
     * {@code private/profile}. Commute estimates do not require attendance or a class timetable.
     */
    void savePreferences(long budgetMinSen, long budgetMaxSen, List<String> lifestyleTags,
            boolean hasCar, Callback callback);

    /**
     * Step 3. The {@code privacy} map in {@code private/profile}. Security Rules read {@code showPhoneToConnections};
     * the budget and photo flags are kept for the screens that will show those details.
     */
    void savePrivacy(boolean showPhoneToConnections, boolean showBudgetToConnections, boolean showPhotoToHouseholdOnly,
            Callback callback);
}
