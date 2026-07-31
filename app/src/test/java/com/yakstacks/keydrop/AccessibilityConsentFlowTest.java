package com.yakstacks.keydrop;

public final class AccessibilityConsentFlowTest {
    public static void main(String[] args) {
        setupWithoutConsentShowsDisclosure();
        agreeingRemembersConsentAndOpensSettings();
        decliningDoesNotRememberConsentOrOpenSettings();
        returningUserCanOpenSettingsWithoutAnotherPrompt();
    }

    private static void setupWithoutConsentShowsDisclosure() {
        AccessibilityConsentFlow.Action action =
                AccessibilityConsentFlow.requestSetup(false);

        assertEquals(AccessibilityConsentFlow.Action.SHOW_DISCLOSURE, action);
    }

    private static void agreeingRemembersConsentAndOpensSettings() {
        AccessibilityConsentFlow.ConsentResult result =
                AccessibilityConsentFlow.respondToDisclosure(true);

        assertTrue(result.shouldRememberConsent());
        assertEquals(AccessibilityConsentFlow.Action.OPEN_ACCESSIBILITY_SETTINGS,
                result.getAction());
    }

    private static void decliningDoesNotRememberConsentOrOpenSettings() {
        AccessibilityConsentFlow.ConsentResult result =
                AccessibilityConsentFlow.respondToDisclosure(false);

        assertFalse(result.shouldRememberConsent());
        assertEquals(AccessibilityConsentFlow.Action.STAY_IN_APP, result.getAction());
    }

    private static void returningUserCanOpenSettingsWithoutAnotherPrompt() {
        AccessibilityConsentFlow.Action action =
                AccessibilityConsentFlow.requestSetup(true);

        assertEquals(AccessibilityConsentFlow.Action.OPEN_ACCESSIBILITY_SETTINGS, action);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean value) {
        if (value) {
            throw new AssertionError("Expected false but was true");
        }
    }
}
