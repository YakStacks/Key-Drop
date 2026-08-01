package com.yakstacks.keydrop;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.ImageButton;

import java.util.List;

public final class KeyDropAccessibilityService extends AccessibilityService {
    private static final String PREFS = "keydrop_overlay";
    private static final String RIGHT_EDGE = "right_edge";
    private static final String Y_FRACTION = "y_fraction";
    private static final String LEGACY_X = "x";
    private static final String LEGACY_Y = "y";
    private static final float DEFAULT_Y_FRACTION = 0.6f;
    private static final int BUTTON_SIZE_DP = 48;
    private static final int EDGE_INSET_DP = 8;
    private static final long DISMISS_RECHECK_MS = 500;
    private static final long VISIBILITY_RECHECK_MS = 100;

    private WindowManager windowManager;
    private DragImageButton button;
    private WindowManager.LayoutParams params;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable visibilityMonitor = this::monitorKeyboardVisibility;
    private boolean added;
    private boolean dismissRequested;
    private boolean snappedRight;
    private float verticalFraction = DEFAULT_Y_FRACTION;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createButton();
        refreshOverlay();
    }

    private void createButton() {
        button = new DragImageButton(this);
        button.setImageResource(R.drawable.ic_arrow_down);
        button.setBackgroundResource(R.drawable.floating_button_background);
        button.setContentDescription(getString(R.string.hide_keyboard));
        button.setPadding(dp(11), dp(11), dp(11), dp(11));
        button.setElevation(dp(8));

        params = new WindowManager.LayoutParams(
                dp(BUTTON_SIZE_DP), dp(BUTTON_SIZE_DP),
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;

        loadSavedPlacement();
        applyPlacement();

        button.setOnTouchListener(new DragTouchListener());
        button.setOnClickListener(view -> dismissKeyboardSafely());
    }

    /**
     * Reads the saved edge and vertical fraction, migrating positions written by earlier
     * versions that stored absolute pixels.
     */
    private void loadSavedPlacement() {
        SharedPreferences prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        OverlayPlacement placement = placement();

        if (prefs.contains(Y_FRACTION)) {
            verticalFraction = OverlayPlacement.clampFraction(
                    prefs.getFloat(Y_FRACTION, DEFAULT_Y_FRACTION));
        } else if (prefs.contains(LEGACY_Y)) {
            verticalFraction = placement.verticalFraction(prefs.getInt(LEGACY_Y, 0));
        } else {
            verticalFraction = DEFAULT_Y_FRACTION;
        }

        if (prefs.contains(RIGHT_EDGE)) {
            snappedRight = prefs.getBoolean(RIGHT_EDGE, false);
        } else {
            snappedRight = placement.snapsRight(prefs.getInt(LEGACY_X, placement.leftEdgeX()));
        }
    }

    private OverlayPlacement placement() {
        return new OverlayPlacement(
                screenWidth(), screenHeight(), dp(BUTTON_SIZE_DP), dp(EDGE_INSET_DP));
    }

    /** Recomputes pixel coordinates from the stored edge and fraction for the current screen. */
    private void applyPlacement() {
        if (params == null) return;
        OverlayPlacement placement = placement();
        params.x = placement.edgeX(snappedRight);
        params.y = placement.yForFraction(verticalFraction);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        if (type == AccessibilityEvent.TYPE_WINDOWS_CHANGED
                || type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            refreshOverlay();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Rotation, split screen, and display-size changes invalidate the pixel coordinates.
        applyPlacement();
        updateButtonLayout();
        refreshOverlay();
    }

    private void refreshOverlay() {
        if (windowManager == null || button == null) return;
        if (isDeviceLocked()) {
            dismissRequested = false;
            removeButton();
            return;
        }
        if (isInputMethodVisible()) {
            if (!added && !dismissRequested) {
                applyPlacement();
                try {
                    windowManager.addView(button, params);
                    added = true;
                    scheduleVisibilityMonitor();
                } catch (WindowManager.BadTokenException | SecurityException exception) {
                    added = false;
                }
            }
        } else {
            dismissRequested = false;
            removeButton();
        }
    }

    private boolean isInputMethodVisible() {
        List<AccessibilityWindowInfo> windows = getWindows();
        if (windows == null) return false;
        for (AccessibilityWindowInfo window : windows) {
            if (window != null && window.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeviceLocked() {
        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return keyguardManager != null && keyguardManager.isKeyguardLocked();
    }

    private void scheduleVisibilityMonitor() {
        handler.removeCallbacks(visibilityMonitor);
        if (added) handler.postDelayed(visibilityMonitor, VISIBILITY_RECHECK_MS);
    }

    private void monitorKeyboardVisibility() {
        if (!added) return;
        if (isDeviceLocked() || !isInputMethodVisible()) {
            dismissRequested = false;
            removeButton();
            return;
        }
        scheduleVisibilityMonitor();
    }

    private void dismissKeyboardSafely() {
        // Re-check immediately so a stale overlay cannot accidentally navigate Back.
        if (!isInputMethodVisible()) {
            removeButton();
            return;
        }
        dismissRequested = true;
        removeButton();
        vibrate();
        if (!performGlobalAction(GLOBAL_ACTION_BACK)) {
            dismissRequested = false;
            refreshOverlay();
            return;
        }
        handler.postDelayed(() -> {
            dismissRequested = false;
            refreshOverlay();
        }, DISMISS_RECHECK_MS);
    }

    @SuppressWarnings("deprecation")
    private void vibrate() {
        Vibrator vibrator = systemVibrator();
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(18, 80));
        } else {
            vibrator.vibrate(18);
        }
    }

    @SuppressWarnings("deprecation")
    private Vibrator systemVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager =
                    (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            return manager == null ? null : manager.getDefaultVibrator();
        }
        return (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void removeButton() {
        handler.removeCallbacks(visibilityMonitor);
        if (added && windowManager != null && button != null) {
            try {
                windowManager.removeView(button);
            } catch (IllegalArgumentException exception) {
                // The system may already have detached the accessibility overlay.
            }
            added = false;
        }
    }

    @Override
    public void onInterrupt() {
        removeButton();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        removeButton();
        super.onDestroy();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private int screenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int screenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    private void snapToNearestEdgeAndSave() {
        OverlayPlacement placement = placement();
        snappedRight = placement.snapsRight(params.x);
        verticalFraction = placement.verticalFraction(params.y);
        applyPlacement();
        updateButtonLayout();
        getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(RIGHT_EDGE, snappedRight)
                .putFloat(Y_FRACTION, verticalFraction)
                .remove(LEGACY_X)
                .remove(LEGACY_Y)
                .apply();
    }

    private void updateButtonLayout() {
        if (!added || windowManager == null || button == null) return;
        try {
            windowManager.updateViewLayout(button, params);
        } catch (IllegalArgumentException exception) {
            added = false;
            handler.removeCallbacks(visibilityMonitor);
        }
    }

    private static final class DragImageButton extends ImageButton {
        DragImageButton(Context context) {
            super(context);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }
    }

    private final class DragTouchListener implements View.OnTouchListener {
        private int startX;
        private int startY;
        private float downX;
        private float downY;
        private boolean dragged;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = params.x;
                    startY = params.y;
                    downX = event.getRawX();
                    downY = event.getRawY();
                    dragged = false;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    OverlayPlacement placement = placement();
                    int dx = Math.round(event.getRawX() - downX);
                    int dy = Math.round(event.getRawY() - downY);
                    if (Math.abs(dx) > dp(4) || Math.abs(dy) > dp(4)) dragged = true;
                    params.x = placement.clampX(startX + dx);
                    params.y = placement.clampY(startY + dy);
                    updateButtonLayout();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (dragged) {
                        snapToNearestEdgeAndSave();
                    } else {
                        view.performClick();
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    snapToNearestEdgeAndSave();
                    return true;
                default:
                    return false;
            }
        }
    }
}
