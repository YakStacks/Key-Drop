# KeyDrop MVP

**Keep your keyboard. Add the button Android forgot.**

KeyDrop is a keyboard-agnostic Android utility. Its accessibility service watches
for an input-method window, shows a draggable accessibility overlay, and dismisses
the keyboard when the button is tapped.

## MVP behavior

- Shows a floating button only while an input-method window is visible.
- Re-checks keyboard visibility immediately before sending Android's global Back
  action, preventing stale-button taps from navigating away.
- Saves the dragged overlay position locally.
- Uses a compact 48dp high-contrast button that snaps to the nearest screen edge.
- Removes the overlay before requesting keyboard dismissal for immediate feedback.
- Includes an enablement status, setup guide, and test text field.
- Requests no internet permission and does not collect or transmit data.

## Build

Open this directory in Android Studio, allow Gradle sync, and run the `app`
configuration on an Android 7.0+ device. The project expects Android SDK 35 and
JDK 17. From a terminal, use `gradlew.bat assembleDebug` on Windows or
`./gradlew assembleDebug` on macOS/Linux.

For safer device testing, build `assembleHardenedTest`. That variant is optimized
and non-debuggable while retaining the local test signature so it can update prior
test installs. It must not be distributed as a production release; see
[SECURITY.md](SECURITY.md).

## Enable

1. Open KeyDrop.
2. Tap **Open Accessibility Settings**.
3. Choose **KeyDrop floating button** and enable the service.
4. Return to KeyDrop and tap the test field.

Android requires a prominent accessibility warning because the MVP uses an
accessibility overlay, interactive-window metadata, and the global Back action.
KeyDrop requests window access only to identify `TYPE_INPUT_METHOD`; its code does
not traverse view trees or read text.

See [TESTING.md](TESTING.md) for device coverage and known limitations.

## Security and privacy

KeyDrop has no runtime dependencies or network permission. Backups and cleartext
traffic are disabled, and CI treats Android lint warnings as failures. See
[SECURITY.md](SECURITY.md) for the threat model, signing requirements, and residual
testing obligations, and [PRIVACY.md](PRIVACY.md) for the data-handling statement.

## License

Licensed under the [Apache License 2.0](LICENSE).
