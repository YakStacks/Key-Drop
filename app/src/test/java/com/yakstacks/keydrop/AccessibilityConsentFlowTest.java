package com.yakstacks.keydrop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class AccessibilityConsentFlowTest {
    @Test
    public void setupWithoutConsentShowsDisclosure() {
        AccessibilityConsentFlow.Action action =
                AccessibilityConsentFlow.requestSetup(false);

        assertEquals(AccessibilityConsentFlow.Action.SHOW_DISCLOSURE, action);
    }

    @Test
    public void agreeingRemembersConsentAndOpensSettings() {
        AccessibilityConsentFlow.ConsentResult result =
                AccessibilityConsentFlow.respondToDisclosure(true);

        assertTrue(result.shouldRememberConsent());
        assertEquals(AccessibilityConsentFlow.Action.OPEN_ACCESSIBILITY_SETTINGS,
                result.getAction());
    }

    @Test
    public void decliningDoesNotRememberConsentOrOpenSettings() {
        AccessibilityConsentFlow.ConsentResult result =
                AccessibilityConsentFlow.respondToDisclosure(false);

        assertFalse(result.shouldRememberConsent());
        assertEquals(AccessibilityConsentFlow.Action.STAY_IN_APP, result.getAction());
    }

    @Test
    public void returningUserCanOpenSettingsWithoutAnotherPrompt() {
        AccessibilityConsentFlow.Action action =
                AccessibilityConsentFlow.requestSetup(true);

        assertEquals(AccessibilityConsentFlow.Action.OPEN_ACCESSIBILITY_SETTINGS, action);
    }
}
