# KeyDrop release-readiness record

Copy this file for each release candidate or complete it in the release pull request.
Do not tag a production release until every required item is checked and linked to
evidence.

## Build and source

- [ ] The release commit is merged to `main`.
- [ ] CI passes unit tests, lint, APK/AAB builds, and the merged-permission audit.
- [ ] `versionCode` increased and `versionName` matches the intended tag.
- [ ] `CHANGELOG.md` describes the release.
- [ ] The final diff contains no keystore, password, token, APK, or AAB.

CI run: _add URL_

## Signing

- [ ] The unique upload keystore has two secure backups outside the repository.
- [ ] Play App Signing is enabled.
- [ ] All five signing values are configured only as `production` environment secrets.
- [ ] `KEYDROP_UPLOAD_CERT_SHA256` matches the backed-up upload key.
- [ ] The `production` environment is restricted to `v*` tags and requires manual approval
      before releasing its signing secrets.

Upload certificate SHA-256: _record the public fingerprint_

## Device evidence

| Device | Android | Keyboard | Navigation | Result | Tester/date | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| _add device_ | _version_ | _keyboard_ | _mode_ | _pass/fail_ | _name/date_ | _notes_ |

- [ ] Gboard tested.
- [ ] Samsung Keyboard tested on a Samsung device.
- [ ] SwiftKey tested.
- [ ] Gesture and three-button navigation tested.
- [ ] Rotation, split screen, lock screen, password fields, browser/WebView, hardware
      keyboard, and voice input tested.
- [ ] The stale-overlay double-tap case does not navigate away from the foreground app.
- [ ] Both disclosure choices were tested from a clean install.

## Store and policy

- [ ] At least two current phone screenshots are captured without personal information.
- [ ] The 512 × 512 icon and 1024 × 500 feature graphic are uploaded.
- [ ] The privacy policy is live at a public, non-editable HTML URL.
- [ ] The Data safety form says no data is collected or shared.
- [ ] The Accessibility API declaration and demonstration video are submitted.
- [ ] Content rating, target audience, ads, app access, and support contact are complete.
- [ ] The listing says “open source” only if the repository is public.
- [ ] The package name is registered to the verified YakStacks Play developer identity.
- [ ] Any required 12-tester, 14-day closed test is complete.

Privacy policy URL: https://yakstacks.github.io/Key-Drop/

Accessibility demonstration video: _add URL_

## Rollout

- [ ] The signed AAB is uploaded to internal testing first.
- [ ] Play's pre-launch report has no unresolved blocker.
- [ ] Closed-test feedback is reviewed and resolved.
- [ ] Production uses a staged rollout with Android vitals monitored.

Release decision and approver: _record go/no-go and date_
