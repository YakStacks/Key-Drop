# KeyDrop privacy policy

**Effective date:** August 1, 2026

KeyDrop is developed by **YakStacks**. This policy explains the limited on-device
access KeyDrop needs to provide its hide-keyboard button and how its local settings
are handled.

## Accessibility access

Android requires `canRetrieveWindowContent="true"` before an accessibility service
may call `getWindows()` or receive window-change events. KeyDrop uses that capability
only to determine whether a window of type `TYPE_INPUT_METHOD` is present.

KeyDrop does not traverse accessibility node trees, inspect fields, read or store
typed text, take screenshots, or record input. It subscribes only to window-state
events, checks window types, and suppresses its overlay while the device is locked.

Before opening Android Accessibility Settings for the first time, KeyDrop shows a
separate in-app disclosure explaining this access and asks the user to choose
**Agree** or **Not now**. Choosing **Not now** opens no settings and grants no access.

## Information stored on the device

KeyDrop stores only these preferences in its private app storage:

- the screen edge and relative vertical position of the floating button; and
- whether the user accepted the accessibility disclosure.

These settings never leave the device.

## Collection and sharing

KeyDrop has no internet or network-state permission, analytics, advertising SDK,
user account, cloud component, WebView, or runtime third-party software. It does not
collect, transmit, sell, or share user or device data.

## Retention and deletion

The local preferences remain until the user clears KeyDrop's app storage or
uninstalls KeyDrop. Either action deletes them. Because KeyDrop does not collect or
transmit data, YakStacks has no server-side KeyDrop data to retain or delete.

## Security

Android backups are disabled for KeyDrop, cleartext traffic is denied, and local
preferences are stored in app-private storage. The accessibility service is guarded
by Android's `BIND_ACCESSIBILITY_SERVICE` permission and can be disabled at any time
in Android Settings.

## Children

KeyDrop is a general-purpose utility and is not directed at children. It does not
collect data from children or any other users.

## Changes to this policy

If KeyDrop's data practices change, this policy and the in-app policy will be updated
before the changed version is distributed.

## Contact

Privacy questions may be sent to **James@yakstacks.com**.
