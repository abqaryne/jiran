package com.jiran.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.jiran.app.data.CommuteRepository;
import com.jiran.app.data.CommuteSummary;
import com.jiran.app.data.FirebaseSetup;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Developer preview with synthetic server fixtures, never a live commute quote. */
public final class CommutePreviewActivity extends Activity {
    private static final String[] SCENARIOS = {"on-time", "no-on-time-route", "missing-data"};
    private Spinner scenario;
    private Button calculate;
    private TextView status;
    private LinearLayout options;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!BuildConfig.USE_FIREBASE_EMULATORS) {
            finish();
            return;
        }
        setContentView(R.layout.activity_commute_preview);
        scenario = findViewById(R.id.commute_scenario);
        scenario.setAdapter(ArrayAdapter.createFromResource(this, R.array.commute_scenarios,
                android.R.layout.simple_spinner_dropdown_item));
        calculate = findViewById(R.id.commute_calculate);
        status = findViewById(R.id.commute_status);
        options = findViewById(R.id.commute_options);
        calculate.setOnClickListener(view -> load());
        findViewById(R.id.commute_account).setOnClickListener(view ->
                startActivity(new Intent(this, EmailPasswordActivity.class)));
    }

    private void load() {
        options.removeAllViews();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified()) {
            status.setText(R.string.commute_sign_in);
            return;
        }
        if (!FirebaseSetup.prepareConnection(this)) {
            status.setText(R.string.allow_local_network);
            return;
        }
        calculate.setEnabled(false);
        scenario.setEnabled(false);
        status.setText(R.string.commute_loading);
        new CommuteRepository().preview(SCENARIOS[scenario.getSelectedItemPosition()])
                .addOnCompleteListener(task -> {
                    if (isDestroyed() || isFinishing()) {
                        return;
                    }
                    calculate.setEnabled(true);
                    scenario.setEnabled(true);
                    if (task.isSuccessful()) {
                        render(task.getResult());
                    } else {
                        status.setText(R.string.commute_error);
                    }
                });
    }

    private void render(CommuteSummary summary) {
        DateTimeFormatter clock = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", Locale.ENGLISH)
                .withZone(ZoneId.of("Asia/Kuala_Lumpur"));
        String timing = getString(R.string.commute_timing, clock.format(summary.departTime),
                clock.format(summary.classStartTime), summary.monthlyTrips);
        String total = summary.trueMonthlyCostSen == null ? getString(R.string.commute_none)
                : getString(R.string.commute_total, money(summary.trueMonthlyCostSen), modeName(summary.selectedMode));
        status.setText(getString(R.string.commute_summary_lines, total,
                getString(R.string.commute_components, money(summary.roomPriceSen),
                        money(summary.utilitiesShareSen)), timing));
        for (CommuteSummary.Option option : summary.options) {
            TextView row = new TextView(this);
            row.setTextSize(16);
            row.setPadding(0, 24, 0, 24);
            String title = modeName(option.mode);
            if (option.mode.equals(summary.selectedMode)) {
                title = getString(R.string.commute_recommended, title);
                row.setTypeface(null, Typeface.BOLD);
            }
            String detail;
            if (option.reason != null) {
                detail = unavailableReason(option.reason);
            } else {
                String emissions = option.co2GramsPerTrip == null ? getString(R.string.commute_unknown_co2)
                        : getString(R.string.commute_co2, option.co2GramsPerTrip, option.emissionSource);
                String route = getString(R.string.commute_option, (option.durationSeconds + 59) / 60,
                        money(option.monthlyCostSen), getString(option.arrivesOnTime
                                ? R.string.commute_before_class : R.string.commute_late));
                detail = getString(R.string.commute_row, route, emissions);
                detail = withMetadata(detail, option);
            }
            row.setText(getString(R.string.commute_row, title, detail));
            options.addView(row);
        }
    }

    private String withMetadata(String detail, CommuteSummary.Option option) {
        StringBuilder text = new StringBuilder(detail);
        if (option.routeSource != null) {
            text.append('\n').append(getString(R.string.commute_route_source, option.routeSource));
        }
        if (option.costSource != null) {
            text.append('\n').append(getString(R.string.commute_cost_source, option.costSource));
        }
        for (String warning : option.warnings) {
            text.append('\n').append(getString(R.string.commute_warning, warning));
        }
        if (Boolean.TRUE.equals(option.trafficFallback)) {
            text.append('\n').append(getString(R.string.commute_fallback));
        }
        return text.toString();
    }

    private String modeName(String mode) {
        switch (mode) {
            case "walking": return getString(R.string.commute_walking);
            case "transit": return getString(R.string.commute_transit);
            case "e_hailing": return getString(R.string.commute_e_hailing);
            case "driving": return getString(R.string.commute_driving);
            default: throw new IllegalArgumentException("Unknown commute mode");
        }
    }

    private String unavailableReason(String reason) {
        switch (reason) {
            case "missing_schedule": return getString(R.string.commute_missing_schedule);
            case "missing_fare_assumption": return getString(R.string.commute_missing_fare);
            case "provider_unavailable": return getString(R.string.commute_provider_unavailable);
            default: return getString(R.string.commute_missing_route);
        }
    }

    private String money(long sen) {
        return "RM " + BigDecimal.valueOf(sen, 2).toPlainString();
    }
}
