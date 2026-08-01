# Changelog

All notable changes to KeyDrop are recorded here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[semantic versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-01

First production release. The app's behaviour is unchanged from the MVP apart from the
fixes below; the rest of the work made it shippable.

### Added

- Release signing configuration sourced from a local `keystore.properties` or from
  environment variables, with `-PrequireReleaseSigning=true` to make missing signing
  material a build failure.
- Tag-triggered release workflow that builds a signed AAB and APK, audits the merged
  permissions and the APK signature, and publishes the artifacts with the R8 mapping file.
- Adaptive launcher icon with a themed (monochrome) layer, round variant, and bitmap
  fallbacks for Android 7.0–8.0 launchers, plus Play Store icon and feature graphic.
- JUnit unit tests run by Gradle, covering the consent flow and overlay geometry.
- [RELEASING.md](RELEASING.md) and [docs/store-listing.md](docs/store-listing.md) covering
  keystore handling, the Play Console declarations, and store copy.

### Fixed

- The floating button no longer ends up clipped or on the wrong edge after a rotation,
  split-screen change, or display-size change. Its position is now stored as an edge plus a
  fraction of the available vertical travel, and the service repositions the button when the
  configuration changes. Positions saved by earlier versions are migrated.
- Haptic feedback uses `VibratorManager` on Android 12+ instead of the deprecated system
  service.
- Opening Accessibility Settings no longer crashes on OEM builds that lack the standard
  settings screen; the app shows an explanatory message instead.
- The enablement indicator matches KeyDrop's own service rather than any service in the
  package, and tolerates a null accessibility manager.

## [0.1.3] - 2026-07-31

MVP published for testing only; never distributed.

### Added

- Keyboard-agnostic floating button shown while an input-method window is visible, with
  drag-to-move, edge snapping, and a saved position.
- Keyboard visibility re-check immediately before the global Back action, so a stale button
  tap cannot navigate away.
- Accessibility consent disclosure shown before Accessibility Settings are opened.
- Hardened, non-debuggable test variant for on-device testing.

[1.0.0]: https://github.com/YakStacks/Key-Drop/releases/tag/v1.0.0
