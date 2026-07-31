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
