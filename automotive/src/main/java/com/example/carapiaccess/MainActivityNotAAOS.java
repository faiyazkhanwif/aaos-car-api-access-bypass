package com.example.carapiaccess;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.carapiaccess.logic.CarDataLogic;

public class MainActivityNotAAOS extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simple UI
        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        sv.addView(container);
        tv = new TextView(this);
        container.addView(tv);
        setContentView(sv);

        // Detect environment dynamically
        boolean isAutomotive = isAutomotiveDevice(this);

        // Print actual environment details (not just hardcoded string)
        String envMessage = buildEnvironmentMessage(isAutomotive);
        tv.append(envMessage + "\n\n");


        CarDataLogic logic = new CarDataLogic(this); // this is Context
        new Thread(() -> {
            CarDataLogic.Result r = logic.exercise_oneSamplePerSensor_v3();
            runOnUiThread(() -> {
                tv.append(r.title + "\n\n");
                for (String row : r.rows) tv.append(row + "\n");
            });
        }).start();


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
            // MODE_TYPE_CAR is available on API 23+, use Configuration check as well
            try {
                int mode = uiModeManager.getCurrentModeType();
                // MODE_TYPE_CAR constant equals Configuration.UI_MODE_TYPE_CAR under the hood
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
        sb.append(" - UiModeManager currentType: ").append(ui == null ? "N/A" : String.valueOf(ui.getCurrentModeType())).append("\n");

        //sb.append("\nConclusion: Running on ").append(isAutomotive ? "an Automotive device (AAOS/car host)" : "a Phone/Tablet/Non-car device");
        return sb.toString();
    }
}
