# KeyDrop

**Keep your keyboard. Add the button Android forgot.**

KeyDrop is a keyboard-agnostic Android utility. Its accessibility service watches
for an input-method window, shows a draggable accessibility overlay, and dismisses
the keyboard when the button is tapped.

## Behavior

- Shows a floating button only while an input-method window is visible.
- Re-checks keyboard visibility immediately before sending Android's global Back
  action, preventing stale-button taps from navigating away.
- Uses a compact 48dp high-contrast button that snaps to the nearest screen edge.
- Saves the dragged position as an edge plus a fraction of the screen's vertical
  travel, so it survives rotation, split screen, and display-size changes.
- Removes the overlay before requesting keyboard dismissal for immediate feedback.
- Includes an enablement status, setup guide, and test text field.
- Requests no internet permission and does not collect or transmit data.

## Build

Open this directory in Android Studio, allow Gradle sync, and run the `app`
configuration on an Android 7.0+ device. The project expects Android SDK 36 and
JDK 17. From a terminal, use `gradlew.bat assembleDebug` on Windows or
`./gradlew assembleDebug` on macOS/Linux.

For safer device testing, build `assembleHardenedTest`. That variant is optimized
and non-debuggable while retaining the local test signature so it can update prior
test installs. It must not be distributed as a production release; see
[SECURITY.md](SECURITY.md).

Unit tests cover the consent flow and the overlay geometry:

```bash
./gradlew testReleaseUnitTest
```

## Release

Production builds are signed from a keystore that lives outside this repository, and
publishing is driven by pushing a version tag. The full procedure — key creation, CI
secrets, the pre-release device pass, and the Play Console declarations — is in
[RELEASING.md](RELEASING.md). Store copy and graphics are in
[docs/store-listing.md](docs/store-listing.md); regenerate the graphics with
`python3 tools/generate_icons.py`.

## Enable

1. Open KeyDrop.
2. Tap **Open Accessibility Settings**.
3. Read the Accessibility Service disclosure and choose **Agree** or **Not now**.
4. After agreeing, choose **KeyDrop floating button** and enable the service.
5. Return to KeyDrop and tap the test field.

Android requires a prominent accessibility warning because KeyDrop uses an
accessibility overlay, interactive-window metadata, and the global Back action.
KeyDrop requests window access only to identify `TYPE_INPUT_METHOD`; its code does
not traverse view trees or read text. KeyDrop is declared as a general utility,
not as a disability-focused accessibility tool.

See [TESTING.md](TESTING.md) for device coverage and known limitations.
The Play Console declaration text and recording plan are in
[docs/accessibility-declaration.md](docs/accessibility-declaration.md).

## Security and privacy

KeyDrop has no runtime dependencies or network permission. Backups and cleartext
traffic are disabled, CI treats Android lint warnings as failures, and every release
build is checked for network permissions before it can be published. See
[SECURITY.md](SECURITY.md) for the threat model, signing requirements, and residual
testing obligations, and [PRIVACY.md](PRIVACY.md) for the data-handling statement.
Record production evidence in
[docs/release-readiness.md](docs/release-readiness.md) before creating a release tag.

Release notes are in [CHANGELOG.md](CHANGELOG.md).

## License

Copyright © 2026 YakStacks.

Licensed under the [Apache License 2.0](LICENSE). See [NOTICE](NOTICE) for the
project copyright notice.
