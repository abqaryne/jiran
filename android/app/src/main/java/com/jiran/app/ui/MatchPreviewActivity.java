package com.jiran.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.BuildConfig;
import com.jiran.app.R;
import com.jiran.app.data.FirebaseSetup;
import com.jiran.app.data.MatchRepository;
import com.jiran.app.data.MatchSummary;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Synthetic score/reason handoff only; Ary owns the final Match product UI. */
public final class MatchPreviewActivity extends Activity {
    private static final String[] SCENARIOS = {"ranked", "missing-answers", "empty"};
    private Spinner scenario;
    private Button calculate;
    private TextView status;
    private LinearLayout rows;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            finish();
            return;
        }
        setContentView(R.layout.activity_match_preview);
        scenario = findViewById(R.id.match_scenario);
        scenario.setAdapter(ArrayAdapter.createFromResource(this, R.array.match_scenarios,
                android.R.layout.simple_spinner_dropdown_item));
        calculate = findViewById(R.id.match_calculate);
        status = findViewById(R.id.match_status);
        rows = findViewById(R.id.match_rows);
        calculate.setOnClickListener(view -> load());
        findViewById(R.id.match_account).setOnClickListener(view ->
                startActivity(new Intent(this, EmailPasswordActivity.class)));
    }

    private void load() {
        rows.removeAllViews();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            status.setText(R.string.match_sign_in);
            return;
        }
        if (!FirebaseSetup.prepareConnection(this)) {
            status.setText(R.string.allow_local_network);
            return;
        }
        String requestedUid = user.getUid();
        calculate.setEnabled(false);
        scenario.setEnabled(false);
        status.setText(R.string.match_loading);
        new MatchRepository().preview(SCENARIOS[scenario.getSelectedItemPosition()]).addOnCompleteListener(task -> {
            if (isDestroyed() || isFinishing()) {
                return;
            }
            calculate.setEnabled(true);
            scenario.setEnabled(true);
            FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
            if (current == null || !current.isEmailVerified() || !requestedUid.equals(current.getUid())) {
                status.setText(R.string.match_sign_in);
                return;
            }
            if (task.isSuccessful()) {
                render(task.getResult());
            } else {
                status.setText(R.string.match_error);
            }
        });
    }

    private void render(MatchSummary summary) {
        String versions = getString(R.string.match_versions, summary.formulaVersion, summary.weightVersion);
        status.setText(summary.matches.isEmpty() ? versions + "\n" + getString(R.string.match_empty) : versions);
        int rank = 1;
        for (MatchSummary.Match match : summary.matches) {
            String score = match.scoreBasisPoints == null ? getString(R.string.match_insufficient) : percent(match.scoreBasisPoints);
            StringBuilder text = new StringBuilder(getString(R.string.match_candidate, rank++, match.candidateId, score));
            if (!match.missingFactors.isEmpty()) {
                List<String> missing = new ArrayList<>();
                for (String factor : match.missingFactors) {
                    missing.add(factorName(factor));
                }
                text.append('\n').append(getString(R.string.match_missing, String.join(", ", missing)));
            }
            text.append('\n').append(getString(R.string.match_overlap, overlap(match.overlappingClassDays)));
            text.append("\n\n").append(getString(R.string.match_rule_heading));
            for (MatchSummary.Reason reason : match.reasons) {
                text.append('\n').append(getString(R.string.match_reason, factorName(reason.factor), reasonName(reason.code)));
            }
            text.append("\n\n").append(getString(R.string.match_weights_heading));
            for (MatchSummary.Factor factor : match.factors) {
                text.append('\n').append(getString(R.string.match_breakdown, factorName(factor.name),
                        percent(factor.weightBasisPoints), percent(factor.similarityBasisPoints)));
            }
            TextView row = new TextView(this);
            row.setText(text.toString());
            row.setTextSize(16);
            row.setLineSpacing(0, 1.15f);
            int padding = (int) (16 * getResources().getDisplayMetrics().density);
            row.setPadding(0, padding, 0, padding);
            rows.addView(row);
        }
    }

    private String factorName(String factor) {
        return getResources().getStringArray(R.array.match_factors)[MatchSummary.FACTORS.indexOf(factor)];
    }

    private String reasonName(String reason) {
        switch (reason) {
            case "similar": return getString(R.string.match_similar);
            case "different": return getString(R.string.match_different);
            default: return getString(R.string.match_partial);
        }
    }

    private String overlap(List<Integer> days) {
        if (days == null) {
            return getString(R.string.match_unknown);
        }
        if (days.isEmpty()) {
            return getString(R.string.match_none);
        }
        String[] labels = getResources().getStringArray(R.array.match_weekdays);
        List<String> names = new ArrayList<>();
        for (int day : days) {
            names.add(labels[day - 1]);
        }
        return String.join(", ", names);
    }

    private String percent(Integer value) {
        return value == null ? getString(R.string.match_unknown)
                : BigDecimal.valueOf(value).movePointLeft(2).stripTrailingZeros().toPlainString() + "%";
    }
}
