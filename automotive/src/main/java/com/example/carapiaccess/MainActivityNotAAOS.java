package com.example.carapiaccess;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.carapiaccess.logic.CarDataLogic;

public class MainActivityNotAAOS extends AppCompatActivity {

    private TextView tv;
    private TextView tvResults;
    private TextView tvCountdown;

    //For Sensor Probe
/*
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Simple UI with countdown + results area
        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        sv.addView(container);

        tvCountdown = new TextView(this);
        tvCountdown.setTextSize(22f);
        tvCountdown.setText("Starting...");
        container.addView(tvCountdown);

        tvResults = new TextView(this);
        tvResults.setTextSize(14f);
        container.addView(tvResults);

        setContentView(sv);

        // Create logic instance
        final CarDataLogic logic = new CarDataLogic(this);

        // Start the repeating countdown + probe loop on a background thread
        new Thread(() -> {
            final int repeats = 10;
            final int countdownSeconds = 8;

            for (int iter = 1; iter <= repeats; iter++) {
                // Countdown 5..1, updating tvCountdown each second
                for (int s = countdownSeconds; s >= 1; s--) {
                    final int sec = s;
                    runOnUiThread(() -> tvCountdown.setText("Refresh in: " + sec));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        runOnUiThread(() -> tvCountdown.setText("Cancelled"));
                        return;
                    }
                }

                // Indicate refresh starting
                final int currentIter = iter;
                runOnUiThread(() -> {
                    tvCountdown.setText("Refreshing now... (run " + currentIter + " / " + repeats + ")");
                    tvResults.append("\n=== Refresh " + currentIter + " ===\n");
                });

                // Call the blocking probe (on background thread)
                CarDataLogic.Result r = null;
                try {
                    r = logic.exercise_oneSamplePerSensor_v3();
                } catch (Throwable t) {
                    final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                    final CarDataLogic.Result finalR = null;
                    runOnUiThread(() -> tvResults.append("Probe error: " + err + "\n"));
                    // continue to next iteration
                }

                // Render result when available
                final CarDataLogic.Result finalR = r;
                runOnUiThread(() -> {
                    if (finalR != null) {
                        tvResults.append(finalR.title + "\n");
                        for (String row : finalR.rows) {
                            tvResults.append(row + "\n");
                        }
                    } else {
                        tvResults.append("No result returned.\n");
                    }
                    tvResults.append("-----------------\n");
                });
            }

            // Done
            runOnUiThread(() -> tvCountdown.setText("Completed " + repeats + " refreshes"));
        }).start();

    }
*/

/* For Alarm Manager Probe
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simple UI with countdown + results area
        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        sv.addView(container);

        tvCountdown = new TextView(this);
        tvCountdown.setTextSize(22f);
        tvCountdown.setText("Starting...");
        container.addView(tvCountdown);

        tvResults = new TextView(this);
        tvResults.setTextSize(14f);
        container.addView(tvResults);

        setContentView(sv);

        final CarDataLogic logic = new CarDataLogic(this);

        // (Optional) print environment info once
        boolean isAuto = isAutomotiveDevice(this);
        tvResults.append(buildEnvironmentMessage(isAuto));
        tvResults.append("\n");

        // Start everything on a background thread
        new Thread(() -> {
            final int repeats = 10;
            final int countdownSeconds = 8;

            // 1) Schedule alarms ONCE (do not reschedule every refresh)
            CarDataLogic.Result init = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    init = logic.reflectExerciseAlarmManagerVisibleImpact();
                }
            } catch (Throwable t) {
                final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                runOnUiThread(() -> {
                    tvCountdown.setText("Init failed");
                    tvResults.append("Alarm init error: " + err + "\n");
                });
                return;
            }

            final CarDataLogic.Result initFinal = init;
            runOnUiThread(() -> {
                tvResults.append("\n=== Alarm Impact Initialized ===\n");
                if (initFinal != null) {
                    tvResults.append(initFinal.title + "\n");
                    for (String row : initFinal.rows) tvResults.append(row + "\n");
                }
                tvResults.append("--------------------------------\n");
            });

            // 2) Refresh snapshot 10 times with countdown
            for (int iter = 1; iter <= repeats; iter++) {

                for (int s = countdownSeconds; s >= 1; s--) {
                    final int sec = s;
                    runOnUiThread(() -> tvCountdown.setText("Refresh in: " + sec));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        runOnUiThread(() -> tvCountdown.setText("Cancelled"));
                        return;
                    }
                }

                final int currentIter = iter;
                runOnUiThread(() -> {
                    tvCountdown.setText("Refreshing now... (run " + currentIter + " / " + repeats + ")");
                    tvResults.append("\n=== Snapshot Refresh " + currentIter + " ===\n");
                });

                // Pull latest snapshot (does NOT reschedule alarms)
                CarDataLogic.Result snap = null;
                try {
                    snap = logic.getAlarmImpactSnapshot();
                } catch (Throwable t) {
                    final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                    runOnUiThread(() -> tvResults.append("Snapshot error: " + err + "\n"));
                }

                final CarDataLogic.Result snapFinal = snap;
                runOnUiThread(() -> {
                    if (snapFinal != null) {
                        tvResults.append(snapFinal.title + "\n");
                        for (String row : snapFinal.rows) {
                            tvResults.append(row + "\n");
                        }
                    } else {
                        tvResults.append("No snapshot returned.\n");
                    }
                    tvResults.append("--------------------------------\n");
                });
            }

            runOnUiThread(() -> tvCountdown.setText("Completed " + repeats + " refreshes"));

        }).start();
    }
*/

/* Blocked activity alarm manager
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simple UI with countdown + results area
        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        sv.addView(container);

        tvCountdown = new TextView(this);
        tvCountdown.setTextSize(22f);
        tvCountdown.setText("Starting...");
        container.addView(tvCountdown);

        tvResults = new TextView(this);
        tvResults.setTextSize(14f);
        container.addView(tvResults);

        setContentView(sv);

        final CarDataLogic logic = new CarDataLogic(this);

        // (Optional) print environment info once
        boolean isAuto = isAutomotiveDevice(this);
        tvResults.append(buildEnvironmentMessage(isAuto));
        tvResults.append("\n");

        // Start everything on a background thread
        new Thread(() -> {
            final int repeats = 10;
            final int countdownSeconds = 8;

            // 1) Schedule alarms ONCE (do not reschedule every refresh)
            CarDataLogic.Result init = null;
            try {
                init = logic.scheduleAlarmToNotifyAndTryOpenApp();
            } catch (Throwable t) {
                final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                runOnUiThread(() -> {
                    tvCountdown.setText("Init failed");
                    tvResults.append("Alarm init error: " + err + "\n");
                });
                return;
            }

            final CarDataLogic.Result initFinal = init;
            runOnUiThread(() -> {
                tvResults.append("\n=== Alarm Open-App Impact Initialized ===\n");
                if (initFinal != null) {
                    tvResults.append(initFinal.title + "\n");
                    for (String row : initFinal.rows) tvResults.append(row + "\n");
                }
                tvResults.append("--------------------------------\n");
            });

            // 2) Refresh snapshot 10 times with countdown
            for (int iter = 1; iter <= repeats; iter++) {

                for (int s = countdownSeconds; s >= 1; s--) {
                    final int sec = s;
                    runOnUiThread(() -> tvCountdown.setText("Refresh in: " + sec));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        runOnUiThread(() -> tvCountdown.setText("Cancelled"));
                        return;
                    }
                }

                final int currentIter = iter;
                runOnUiThread(() -> {
                    tvCountdown.setText("Refreshing now... (run " + currentIter + " / " + repeats + ")");
                    tvResults.append("\n=== Snapshot Refresh " + currentIter + " ===\n");
                });

                // Pull latest snapshot (does NOT reschedule alarms)
                CarDataLogic.Result snap = null;
                try {
                    snap = logic.getAlarmOpenAppImpactSnapshot();
                } catch (Throwable t) {
                    final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                    runOnUiThread(() -> tvResults.append("Snapshot error: " + err + "\n"));
                }

                final CarDataLogic.Result snapFinal = snap;
                runOnUiThread(() -> {
                    if (snapFinal != null) {
                        tvResults.append(snapFinal.title + "\n");
                        for (String row : snapFinal.rows) {
                            tvResults.append(row + "\n");
                        }
                    } else {
                        tvResults.append("No snapshot returned.\n");
                    }
                    tvResults.append("--------------------------------\n");
                });
            }

            runOnUiThread(() -> tvCountdown.setText("Completed " + repeats + " refreshes"));

        }).start();
    }
*/
@SuppressLint("SetTextI18n")
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Simple UI with countdown + results area
    ScrollView sv = new ScrollView(this);
    LinearLayout container = new LinearLayout(this);
    container.setOrientation(LinearLayout.VERTICAL);
    sv.addView(container);

    tvCountdown = new TextView(this);
    tvCountdown.setTextSize(22f);
    tvCountdown.setText("Starting...");
    container.addView(tvCountdown);

    tvResults = new TextView(this);
    tvResults.setTextSize(14f);
    container.addView(tvResults);

    setContentView(sv);

    final CarDataLogic logic = new CarDataLogic(this);

    // (Optional) print environment info once
    boolean isAuto = isAutomotiveDevice(this);
    tvResults.append(buildEnvironmentMessage(isAuto));
    tvResults.append("\n");

    /* For Schedulling
    new Thread(() -> {
        final int repeats = 10;
        final int countdownSeconds = 8;

        // Track what we've already printed so we only append new lines
        final java.util.concurrent.atomic.AtomicInteger printedLines = new java.util.concurrent.atomic.AtomicInteger(0);

        // 1) Initialize/schedule alarms ONCE
        try {
            CarDataLogic.Result init;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                init = logic.reflectExerciseAlarmManager_visibleImpact_noCancel();
            } else {
                init = null;
            }
            runOnUiThread(() -> {
                tvResults.append("\n=== Alarm Impact Initialized ===\n");
                if (init != null) {
                    tvResults.append(init.title + "\n");
                }
                tvResults.append("--------------------------------\n");
            });
        } catch (Throwable t) {
            final String err = t.getMessage() == null ? t.toString() : t.getMessage();
            runOnUiThread(() -> {
                tvCountdown.setText("Init failed");
                tvResults.append("Alarm init error: " + err + "\n");
            });
            return;
        }

        // 2) Refresh snapshots with countdown
        for (int iter = 1; iter <= repeats; iter++) {

            // Countdown
            for (int s = countdownSeconds; s >= 1; s--) {
                final int sec = s;
                runOnUiThread(() -> tvCountdown.setText("Refresh in: " + sec));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    runOnUiThread(() -> tvCountdown.setText("Cancelled"));
                    return;
                }
            }

            final int currentIter = iter;
            runOnUiThread(() -> {
                tvCountdown.setText("Refreshing now... (run " + currentIter + " / " + repeats + ")");
                tvResults.append("\n=== Snapshot Refresh " + currentIter + " ===\n");
            });

            // Pull snapshot
            CarDataLogic.Result snap = null;
            try {
                snap = logic.getAlarmImpactSnapshot();
            } catch (Throwable t) {
                final String err = t.getMessage() == null ? t.toString() : t.getMessage();
                runOnUiThread(() -> tvResults.append("Snapshot error: " + err + "\n"));
            }

            // Print only new lines since last time
            final CarDataLogic.Result snapFinal = snap;
            runOnUiThread(() -> {
                if (snapFinal == null || snapFinal.rows == null) {
                    tvResults.append("No snapshot returned.\n");
                    tvResults.append("--------------------------------\n");
                    return;
                }

                int startIdx = printedLines.get();
                int total = snapFinal.rows.size();

                tvResults.append(snapFinal.title + " (total lines=" + total + ", printed=" + startIdx + ")\n");

                if (total <= startIdx) {
                    tvResults.append("(No new events since last refresh)\n");
                } else {
                    for (int i = startIdx; i < total; i++) {
                        tvResults.append(snapFinal.rows.get(i) + "\n");
                    }
                    printedLines.set(total);
                }
                tvResults.append("--------------------------------\n");
            });
        }

        runOnUiThread(() -> tvCountdown.setText("Completed " + repeats + " refreshes"));
    }).start();
    */

    logic.executeIntentStormAttackExtreme(); // IntentStorm
    logic.executeAudioSystemAttack(); // soundblast
    logic.executeLauncherAppsAttack(); // launches accessible launcher apps

}




    private static boolean isAutomotiveDevice(Context ctx) {
        // Primary check
        PackageManager pm = ctx.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            return true;
        }

        // Fallback: use UiModeManager / configuration
        UiModeManager uiModeManager = (UiModeManager) ctx.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            try {
                int mode = uiModeManager.getCurrentModeType();
                if (mode == Configuration.UI_MODE_TYPE_CAR) {
                    return true;
                }
            } catch (NoSuchFieldError | NoSuchMethodError ignored) {
            }
        }

        // Last fallback
        int uiMode = ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK;
        return uiMode == Configuration.UI_MODE_TYPE_CAR;
    }

    private String buildEnvironmentMessage(boolean isAutomotive) {
        StringBuilder sb = new StringBuilder();
        sb.append("Detected environment:\n");
        sb.append(" - FEATURE_AUTOMOTIVE: ")
                .append(getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)).append("\n");
        UiModeManager ui = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        sb.append(" - UiModeManager currentType: ")
                .append(ui == null ? "N/A" : String.valueOf(ui.getCurrentModeType()))
                .append("\n");
        return sb.toString();
    }
}
