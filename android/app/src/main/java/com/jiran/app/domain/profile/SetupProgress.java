package com.jiran.app.domain.profile;

import java.util.Collections;
import java.util.Map;

/**
 * Which profile setup steps are already saved, read from the documents that exist (E1.3 constraint: no extra schema
 * field). Step 1 basics live in {@code users/{uid}}; steps 2 and 3 in {@code private/profile} (Room budget,
 * then the {@code privacy} map). There is no class timetable (decided 10 Nov 2026).
 */
public final class SetupProgress {

    /** Number of setup screens. */
    public static final int STEPS = 3;

    private final boolean basics;
    private final boolean preferences;
    private final boolean privacy;

    public SetupProgress(boolean basics, boolean preferences, boolean privacy) {
        this.basics = basics;
        this.preferences = preferences;
        this.privacy = privacy;
    }

    /** Accept both old profiles and new profiles without deprecated attendance data. */
    public static SetupProgress fromFields(Map<String, ?> publicFields, Map<String, ?> privateFields) {
        Map<String, ?> pub = publicFields == null ? Collections.emptyMap() : publicFields;
        Map<String, ?> profile = privateFields == null ? Collections.emptyMap() : privateFields;
        boolean basics = pub.containsKey("gender") && pub.containsKey("faculty") && pub.containsKey("year");
        return new SetupProgress(basics, profile.containsKey("budgetMaxSen"), profile.containsKey("privacy"));
    }

    /** The first step that is not saved yet, 1 to {@value #STEPS}; {@value #STEPS} + 1 when everything is saved. */
    public int nextStep() {
        if (!basics) {
            return 1;
        }
        if (!preferences) {
            return 2;
        }
        return privacy ? STEPS + 1 : 3;
    }

    /** True when the student may reach the tabs (FR1.3 acceptance: required steps done). */
    public boolean isComplete() {
        return nextStep() > STEPS;
    }
}
