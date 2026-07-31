package com.yakstacks.keydrop;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public final class MainActivity extends Activity {
    private static final String CONSENT_PREFERENCES = "accessibility_consent";
    private static final String CONSENT_VERSION_1 = "consent_version_1";

    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = findViewById(R.id.status);
        Button settingsButton = findViewById(R.id.open_accessibility_settings);
        settingsButton.setOnClickListener(view -> requestAccessibilitySetup());
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

    private void requestAccessibilitySetup() {
        boolean hasConsented = getSharedPreferences(
                CONSENT_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(CONSENT_VERSION_1, false);
        handleConsentAction(AccessibilityConsentFlow.requestSetup(hasConsented));
    }

    private void showAccessibilityDisclosure() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.accessibility_disclosure_title)
                .setMessage(R.string.accessibility_disclosure_message)
                .setPositiveButton(R.string.accessibility_disclosure_agree,
                        (dialog, which) -> respondToDisclosure(true))
                .setNegativeButton(R.string.accessibility_disclosure_decline,
                        (dialog, which) -> respondToDisclosure(false))
                .show();
    }

    private void respondToDisclosure(boolean agreed) {
        AccessibilityConsentFlow.ConsentResult result =
                AccessibilityConsentFlow.respondToDisclosure(agreed);
        if (result.shouldRememberConsent()) {
            getSharedPreferences(CONSENT_PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(CONSENT_VERSION_1, true)
                    .apply();
        }
        handleConsentAction(result.getAction());
    }

    private void handleConsentAction(AccessibilityConsentFlow.Action action) {
        switch (action) {
            case SHOW_DISCLOSURE:
                showAccessibilityDisclosure();
                break;
            case OPEN_ACCESSIBILITY_SETTINGS:
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                break;
            case STAY_IN_APP:
                break;
        }
    }
}
