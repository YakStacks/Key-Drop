# Releasing KeyDrop

This is the end-to-end procedure for shipping a production build. Everything here assumes a
green `main`, JDK 17, and Android SDK 36.

## 1. One-time: create the upload key

The release key is never stored in this repository. Generate it once, on a machine you
control:

```bash
keytool -genkeypair -v \
  -keystore keydrop-release.jks \
  -alias keydrop \
  -keyalg RSA -keysize 4096 -validity 10000 \
  -dname "CN=KeyDrop, O=YakStacks, C=US"
```

Then:

- Back the `.jks` file and both passwords up somewhere durable and private (a password
  manager plus one offline copy). Losing the upload key means enrolling a replacement key
  with Google Play support; losing it *without* Play App Signing enabled means the app can
  never be updated.
- Keep the keystore outside the working tree, or rely on `.gitignore`, which already
  excludes `*.jks`, `*.keystore`, and `keystore.properties`.
- Enable **Play App Signing** when creating the app in Play Console. Google then holds the
  app signing key and this key becomes the upload key only, which is the recoverable
  arrangement.

## 2. One-time: wire up signing

**Locally**, create `keystore.properties` in the repository root (git-ignored):

```properties
storeFile=/absolute/path/to/keydrop-release.jks
storePassword=…
keyAlias=keydrop
keyPassword=…
```

Relative paths in `storeFile` resolve from the repository root.

**In CI**, add these environment secrets under
*Settings → Environments → production*:

| Secret | Value |
| --- | --- |
| `KEYDROP_KEYSTORE_BASE64` | `base64 -w0 keydrop-release.jks` |
| `KEYDROP_KEYSTORE_PASSWORD` | keystore password |
| `KEYDROP_KEY_ALIAS` | key alias (`keydrop`) |
| `KEYDROP_KEY_PASSWORD` | key password |
| `KEYDROP_UPLOAD_CERT_SHA256` | SHA-256 fingerprint of the upload certificate |

Get the fingerprint with `keytool -list -v -keystore keydrop-release.jks -alias keydrop`.
Copy the SHA-256 value exactly; the workflow accepts it with or without colons.

The repository's `production` environment is restricted to tags matching `v*` and requires
manual approval from `junkyard22`. Self-review is intentionally allowed because YakStacks
currently has one maintainer. The release job references this environment, so GitHub does
not make the signing secrets available to the job until that approval is granted. Keep the
five values in the environment only; do not duplicate them as repository secrets.

The same values can be supplied to a local build as the environment variables
`KEYDROP_KEYSTORE_FILE`, `KEYDROP_KEYSTORE_PASSWORD`, `KEYDROP_KEY_ALIAS`, and
`KEYDROP_KEY_PASSWORD`.

Without signing material the project still builds — `assembleRelease` simply produces an
unsigned APK. Passing `-PrequireReleaseSigning=true` (as the release workflow does) turns a
missing keystore into a build failure, so an unsigned artifact can never be published.

## 3. Prepare the version

1. Raise `versionCode` by one and set `versionName` in `app/build.gradle`. `versionCode`
   must increase on every upload to Play and can never be reused.
2. Add the release to [CHANGELOG.md](CHANGELOG.md).
3. Commit to `main` through a pull request and wait for CI.

## 4. Verify before tagging

```bash
./gradlew testReleaseUnitTest lint assembleRelease bundleRelease
```

Then complete the device pass in [TESTING.md](TESTING.md) using a `hardenedTest` build
(`./gradlew installHardenedTest`), which is optimized and non-debuggable like the release
build but signed with the local debug key. Confirm at minimum:

- The overlay appears only while a keyboard window is up, and never on the lock screen.
- Tapping the button dismisses the keyboard rather than navigating back.
- A dragged position survives a service restart *and* a rotation.
- Merged release permissions are `VIBRATE` only. The CI job asserts this, and you can check
  a local build with
  `"$ANDROID_HOME/build-tools/36.0.0/aapt2" dump permissions app/build/outputs/apk/release/*.apk`.

## 5. Tag and publish

```bash
git tag -a v1.0.0 -m "KeyDrop 1.0.0"
git push origin v1.0.0
```

The `Release` workflow then verifies the tagged commit belongs to `main`, verifies the tag
matches `versionName`, builds a signed AAB and APK, re-runs tests and lint, audits the APK's
permissions, verifies the exact upload-certificate fingerprint, and attaches the bundle,
APK, and R8 `mapping.txt` to a GitHub release. A mismatched tag, missing secret, unexpected
signer, or unexpected permission fails the job before anything is published.

After the tag is pushed, open the workflow run and approve its `production` deployment.
Review the tag and source commit before approving; the protected signing secrets are not
released to the runner until that point.

## 6. Upload to Google Play

Download the `.aab` from the GitHub release and upload it in Play Console. The listing
requires the following, and Play will not let a release out of review without them:

- **Store listing** — name, short and full description, icon, and feature graphic. Copy and
  assets are in [docs/store-listing.md](docs/store-listing.md) and
  [docs/store/](docs/store/). Screenshots must be captured on a device; Play requires at
  least two phone screenshots (see the checklist in `docs/store-listing.md`).
- **Privacy policy URL** — publish [PRIVACY.md](PRIVACY.md) as a public, non-editable HTML
  page and paste its URL. The same complete policy is available inside the app from the
  **Privacy Policy** button.
- **Data safety** — declare that no data is collected or shared. KeyDrop has no network
  permission, so there is no transmission to disclose. The button position and disclosure
  consent state remain on-device in app-private preferences.
- **Accessibility API declaration** — this is the one that gets utility apps rejected. Play
  requires apps that use `AccessibilityService` for a purpose other than assisting users
  with disabilities to justify the use and disclose it prominently. KeyDrop's justification:
  the service reads only window *types* to detect `TYPE_INPUT_METHOD` and issues the global
  Back action to dismiss the keyboard; it never traverses node trees or reads content. The
  in-app disclosure dialog shown before Accessibility Settings open satisfies the prominent
  disclosure requirement, and `android:isAccessibilityTool="false"` is already set because
  KeyDrop is a general utility, not an accessibility tool.
- **Content rating questionnaire**, **target audience** (not directed at children), and
  **ads declaration** (contains no ads).
- **App access** — no login is required; state that all functionality is available without
  an account.
- **Developer contact** — use `James@yakstacks.com` for the required support and privacy
  contact unless YakStacks designates a different monitored address before submission.

For a personal Play developer account created after November 13, 2023, production access
also requires a closed test with at least 12 testers opted in continuously for 14 days.
Create the app in Play Console early so `com.yakstacks.keydrop` is registered under the
verified YakStacks developer identity before wider distribution.

Roll out through **internal testing → closed testing → production**, and start production at
a staged percentage. The accessibility behaviour varies across OEM builds, so a staged
rollout is what makes a bad interaction on one vendor's keyboard recoverable.

## 7. After the release

- Keep the `mapping.txt` attached to the GitHub release, and upload it in Play Console for
  readable crash traces.
- Watch Play Console vitals for ANRs and crashes for the first days of rollout before
  widening it.
- Never publish a `debug` or `hardenedTest` build; both carry Android's public debug key.
