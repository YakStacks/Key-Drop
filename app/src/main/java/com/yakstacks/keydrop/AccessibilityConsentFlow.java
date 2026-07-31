package com.yakstacks.keydrop;

public final class AccessibilityConsentFlow {
    public enum Action {
        SHOW_DISCLOSURE,
        OPEN_ACCESSIBILITY_SETTINGS,
        STAY_IN_APP
    }

    public static final class ConsentResult {
        private final boolean rememberConsent;
        private final Action action;

        private ConsentResult(boolean rememberConsent, Action action) {
            this.rememberConsent = rememberConsent;
            this.action = action;
        }

        public boolean shouldRememberConsent() {
            return rememberConsent;
        }

        public Action getAction() {
            return action;
        }
    }

    private AccessibilityConsentFlow() {
    }

    public static Action requestSetup(boolean hasConsented) {
        return hasConsented
                ? Action.OPEN_ACCESSIBILITY_SETTINGS
                : Action.SHOW_DISCLOSURE;
    }

    public static ConsentResult respondToDisclosure(boolean agreed) {
        if (agreed) {
            return new ConsentResult(true, Action.OPEN_ACCESSIBILITY_SETTINGS);
        }
        return new ConsentResult(false, Action.STAY_IN_APP);
    }
}
