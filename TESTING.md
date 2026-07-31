# Device testing checklist

The accessibility and keyboard-window behavior must be verified on real devices;
OEM Android builds and third-party keyboards differ.

- Android 7, 8, 10, 12, 14, and 15 where available.
- Gboard, Samsung Keyboard, and SwiftKey.
- Gesture navigation and three-button navigation.
- Portrait/landscape rotation and split screen.
- Password fields, search fields, chat composers, browsers, and WebViews.
- Confirm the button disappears before a second tap could navigate Back.
- Confirm dragging persists across service and app restarts.
- Confirm hardware-keyboard and voice-input states do not leave a stale overlay.
- On a clean install, confirm **Not now** leaves the user in KeyDrop and opens no
  Android settings.
- On a clean install, confirm **Agree** opens Accessibility Settings and the
  disclosure is not repeated on later settings-button taps.

Known MVP limitations:

- Some OEMs do not expose the input method as an accessibility window reliably.
- The global Back action follows the active app's behavior; the last-second
  visibility check reduces, but cannot eliminate, platform/OEM race conditions.
- Saved coordinates may be clipped after rotation or display-size changes.
