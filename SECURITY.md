# KeyDrop security model

## Security properties

- No `INTERNET` or `ACCESS_NETWORK_STATE` permission. Both CI and the release
  workflow dump the merged permissions of the built APK and fail if a network
  permission ever appears.
- No runtime third-party dependencies, native libraries, WebView, dynamic code,
  IPC API, content provider, broadcast receiver, or deep link. JUnit is a
  test-only dependency and is not part of any shipped artifact.
- Backups are disabled; the only persisted data is the button's edge, relative
  vertical position, and accessibility-disclosure consent state in app-private
  `SharedPreferences`.
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

The release signing configuration reads that key from a git-ignored
`keystore.properties` or from `KEYDROP_KEYSTORE_*` environment variables, so no
signing material enters the repository or the build logs. Publishing jobs pass
`-PrequireReleaseSigning=true`, which fails the build outright when signing
material is missing rather than emitting an unsigned artifact. The release
workflow additionally runs `apksigner verify --print-certs` on the built APK.
[RELEASING.md](RELEASING.md) covers key generation, backup, and Play App Signing.

## Reporting a vulnerability

Send security reports privately to **James@yakstacks.com** with `KeyDrop security` in
the subject. Include the affected version, reproduction steps, impact, and any
suggested mitigation. Do not include sensitive findings in a public issue.

YakStacks will acknowledge a report within seven days, investigate it, and coordinate
disclosure and a fix when the report is confirmed.

## Residual test requirements

- Dynamic testing on supported Android/OEM versions and keyboards.
- Verify no overlay appears on the lock screen or after keyboard dismissal.
- Re-run Android lint and inspect merged release permissions on every release.
- Perform a third-party mobile security assessment before public distribution if
  the threat model, permissions, dependencies, or data handling expands.
