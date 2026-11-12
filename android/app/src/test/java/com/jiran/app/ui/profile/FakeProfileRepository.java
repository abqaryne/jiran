package com.jiran.app.ui.profile;

import com.jiran.app.data.ProfileRepository;
import com.jiran.app.domain.profile.SetupProgress;
import java.util.ArrayList;
import java.util.List;

/** Records calls and keeps the callback, so a test decides when and how the backend answers. */
final class FakeProfileRepository implements ProfileRepository {

    String name = "Mei Ling Tan";
    ProgressCallback progressCallback;
    Callback pending;
    final List<String> calls = new ArrayList<>();
    boolean[] savedPrivacy;
    long savedMinSen;
    long savedMaxSen;

    @Override
    public String signUpName() {
        return name;
    }

    @Override
    public void loadProgress(ProgressCallback callback) {
        calls.add("loadProgress");
        progressCallback = callback;
    }

    @Override
    public void saveBasics(String fullName, String gender, String faculty, int year, Callback callback) {
        calls.add("saveBasics");
        pending = callback;
    }

    @Override
    public void savePreferences(long budgetMinSen, long budgetMaxSen, List<String> lifestyleTags,
            boolean hasCar, Callback callback) {
        calls.add("savePreferences");
        savedMinSen = budgetMinSen;
        savedMaxSen = budgetMaxSen;
        pending = callback;
    }

    @Override
    public void savePrivacy(boolean showPhoneToConnections, boolean showBudgetToConnections, boolean showPhotoToHouseholdOnly,
            Callback callback) {
        calls.add("savePrivacy");
        savedPrivacy = new boolean[] {showPhoneToConnections, showBudgetToConnections, showPhotoToHouseholdOnly};
        pending = callback;
    }

    void answerProgress(SetupProgress progress) {
        progressCallback.onLoaded(progress);
    }
}
