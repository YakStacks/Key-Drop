package com.yakstacks.keydrop;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public final class MainActivity extends Activity {
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = findViewById(R.id.status);
        Button settingsButton = findViewById(R.id.open_accessibility_settings);
        settingsButton.setOnClickListener(view ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        AccessibilityManager manager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabled = manager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        boolean active = false;
        for (AccessibilityServiceInfo info : enabled) {
            if (info.getResolveInfo() != null
                    && getPackageName().equals(info.getResolveInfo().serviceInfo.packageName)) {
                active = true;
                break;
            }
        }
        statusView.setText(active ? R.string.status_on : R.string.status_off);
        statusView.setBackgroundResource(active
                ? R.drawable.status_background_on
                : R.drawable.status_background_off);
    }
}
