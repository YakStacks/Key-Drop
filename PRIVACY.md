# Privacy

KeyDrop has no internet permission, analytics, advertising SDK, user account, or
cloud component. Cleartext traffic is denied and backups are disabled. Preferences
contain only the floating button's edge and Y position.

Android requires `canRetrieveWindowContent="true"` before an accessibility service
may call `getWindows()` or receive window-change events. KeyDrop requests that
capability only to determine whether a window of type `TYPE_INPUT_METHOD` is
present. Its code does not traverse accessibility node trees, inspect fields, or
read or store typed text. It subscribes only to window-state events, checks window
types, and suppresses its overlay while the device is locked.

Before opening Android Accessibility Settings for the first time, KeyDrop shows
a separate in-app disclosure explaining this access and asks the user to choose
**Agree** or **Not now**. Consent is stored locally as a single boolean so the
disclosure is not repeated after acceptance. Choosing **Not now** opens no
settings and grants no access.
