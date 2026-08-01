# Alternative Android distribution readiness

This document tracks the work needed to publish KeyDrop outside Google Play. The
shared listing metadata lives in `fastlane/metadata/android/en-US/` so F-Droid,
IzzyOnDroid, and compatible tooling can read it directly from the source tree.

## F-Droid

KeyDrop is a strong technical fit for F-Droid:

- Apache-2.0 licensed source and copyright notices are present.
- The app has a unique package ID: `com.yakstacks.keydrop`.
- The release build uses no proprietary runtime library.
- The app contains no advertising, analytics, self-updater, or network permission.
- Gradle builds the app from the command line with JDK 17 and Android SDK 36.
- Descriptions, icon, feature graphic, and version-code changelog use F-Droid's
  supported Fastlane layout.

Remaining before submission:

1. Capture real phone screenshots and add them under
   `fastlane/metadata/android/en-US/images/phoneScreenshots/`.
2. Make the GitHub repository public. F-Droid must be able to inspect and clone the
   complete source history.
3. Merge the distribution metadata into `main`, then tag that exact commit as
   `v1.0.0`. F-Droid reads upstream metadata from release commits.
4. Prepare and validate `metadata/com.yakstacks.keydrop.yml` in a fork of
   `fdroid/fdroiddata`, then submit it as a New App merge request.

F-Droid builds and signs its own APK by default, so Google Play account verification
and the YakStacks upload key are not prerequisites for the initial F-Droid submission.
Reproducible builds using the YakStacks-signed APK can be added later.

References:

- https://f-droid.org/en/docs/Submitting_to_F-Droid_Quick_Start_Guide/
- https://f-droid.org/en/docs/Inclusion_Policy/
- https://f-droid.org/en/docs/All_About_Descriptions_Graphics_and_Screenshots/

## IzzyOnDroid

Do not submit KeyDrop to IzzyOnDroid without first disclosing its development history
and receiving confirmation that the project will be considered. IzzyOnDroid's current
inclusion policy says applications created fully or partly with generative AI tools
will most likely be rejected. KeyDrop was developed with AI assistance, so silently
submitting it would be inaccurate and disrespect the maintainer's policy.

If the maintainer explicitly agrees to review it, the remaining technical requirements
are a public source repository, the real screenshots above, and a developer-signed,
non-debuggable APK attached to a tagged GitHub Release.

References:

- https://izzyondroid.org/docs/general/AppInclusionPolicy/
- https://apt.izzysoft.de/fdroid/index/info
