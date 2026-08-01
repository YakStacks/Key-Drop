# Device testing checklist

Run `./gradlew testReleaseUnitTest lint` first; it covers the consent flow and the
overlay geometry. Everything below has to be verified on real devices, because the
accessibility and keyboard-window behavior differs across OEM Android builds and
third-party keyboards. Use a `hardenedTest` build for the device pass.

- Android 7, 8, 10, 12, 14, and 15 where available.
- Gboard, Samsung Keyboard, and SwiftKey.
- Gesture navigation and three-button navigation.
- Portrait/landscape rotation and split screen.
- Password fields, search fields, chat composers, browsers, and WebViews.
- Confirm the button disappears before a second tap could navigate Back.
- Confirm dragging persists across service and app restarts.
- Confirm a dragged position stays on screen, and on the same edge, after rotating
  and after entering split screen.
- Confirm hardware-keyboard and voice-input states do not leave a stale overlay.
- On a clean install, confirm **Not now** leaves the user in KeyDrop and opens no
  Android settings.
- On a clean install, confirm **Agree** opens Accessibility Settings and the
  disclosure is not repeated on later settings-button taps.

Also confirm the pre-release checks in step 4 of [RELEASING.md](RELEASING.md) before
tagging a release.

Known limitations:

- Some OEMs do not expose the input method as an accessibility window reliably.
- The global Back action follows the active app's behavior; the last-second
  visibility check reduces, but cannot eliminate, platform/OEM race conditions.
- The saved position keeps its edge and its relative height across screen changes,
  so it is preserved but not pixel-identical after a rotation.
