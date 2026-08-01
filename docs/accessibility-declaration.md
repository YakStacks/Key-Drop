# Google Play Accessibility API declaration

Use this as the source for the Play Console declaration. Keep the final answers aligned
with the submitted app and recording.

## Core purpose

KeyDrop is a general-purpose utility that adds a floating Hide Keyboard button while an
on-screen keyboard is visible. It does not claim to be a disability accessibility tool,
and its service metadata sets `isAccessibilityTool="false"`.

## Why AccessibilityService is required

Android does not provide an ordinary app API that can both detect an input-method window
across other apps and dismiss that keyboard when the user taps an external floating
control. KeyDrop uses AccessibilityService to read window metadata, check only for a
`TYPE_INPUT_METHOD` window, display its accessibility overlay, and send the standard global
Back action after a fresh keyboard-visibility check when the user taps the button.

KeyDrop does not traverse accessibility node trees, inspect fields or screen content, read
or store typed text, take screenshots, record input, change settings automatically, or
perform autonomous actions. It has no internet permission and does not collect or share
data.

## Prominent disclosure and consent

Immediately before KeyDrop opens Android Accessibility Settings for the first time, it
shows a separate in-app dialog that names the Accessibility Service, explains the window
access, states exactly what KeyDrop does and does not do, and offers **Agree** and **Not
now**. Settings opens only after **Agree**. **Not now** leaves the user in KeyDrop and grants
no access.

## Demonstration video shot list

Record one continuous, legible video on a clean install:

1. Open KeyDrop and show the main screen.
2. Tap **Open Accessibility Settings** and hold on the complete disclosure long enough to
   read it.
3. Choose **Not now** and show that the app remains open and Settings does not launch.
4. Trigger the disclosure again, choose **Agree**, and enable KeyDrop in Android Settings.
5. Return to KeyDrop, focus the test field, and show the floating button appear.
6. Tap the floating button and show the keyboard disappear immediately.
7. Open the in-app **Privacy Policy** and briefly show the complete policy.

Use captions or narration to identify the keyboard-window check and user-triggered Back
action. Do not show notifications, account names, messages, or other personal content.
