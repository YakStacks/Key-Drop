# KeyDrop security model

## Security properties

- No `INTERNET` or `ACCESS_NETWORK_STATE` permission; final APK permissions are
  verified during the release audit.
- No runtime third-party dependencies, native libraries, WebView, dynamic code,
  IPC API, content provider, broadcast receiver, or deep link.
- Backups are disabled; the only persisted data is button edge/Y position in
  app-private `SharedPreferences`.
- Cleartext networking is denied as defense in depth.
- The accessibility service is protected by Android's signature-level
  `BIND_ACCESSIBILITY_SERVICE` permission.
- The overlay is hidden on the lock screen and never traverses accessibility node
  trees, reads field contents, takes screenshots, or records input.
- Release builds disable debugging and use R8 optimization/resource shrinking.
- GitHub Actions and the Gradle distribution are pinned to exact revisions or
  checksums.

## Required accessibility capability

Android requires `canRetrieveWindowContent="true"` and
`flagRetrieveInteractiveWindows` before `getWindows()` can return window metadata.
KeyDrop uses that API only to test for `TYPE_INPUT_METHOD`. This remains a powerful
capability and must be disclosed clearly to users.

## Signing

Debug and `hardenedTest` APKs use Android's public debug key and are only for local
testing. `hardenedTest` is optimized and non-debuggable, but its signature is not
suitable for distribution. Never publish or broadly distribute either test build.
A production APK/AAB must be signed with a unique, user-controlled release key
kept outside this repository and backed up securely. Do not commit keystores or
passwords.

## Residual test requirements

- Dynamic testing on supported Android/OEM versions and keyboards.
- Verify no overlay appears on the lock screen or after keyboard dismissal.
- Re-run Android lint and inspect merged release permissions on every release.
- Perform a third-party mobile security assessment before public distribution if
  the threat model, permissions, dependencies, or data handling expands.
