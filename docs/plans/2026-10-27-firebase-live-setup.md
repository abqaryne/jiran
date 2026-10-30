# Firebase live setup plan

> Use superpowers:executing-plans and the official Firebase basics, Auth and Firestore skills.

**Goal:** Connect the existing Java Android app to `jiran-mad` and provide a usable Email/Password setup flow with private Firestore access.

**Verified inputs:** Existing Android registration `1:157082520904:android:cc25cf096fa7c07391b77d`, application ID `jiran.com`, display name Jiran. User approved Standard Firestore `(default)` in Singapore `asia-southeast1` on 27 October 2026.

## Batches

- [x] Provision only Auth Email/Password and Firestore in the existing project; reuse downloaded ignored SDK config. Do not deploy Functions/Storage or change billing.
- [x] Match `applicationId` to registration while retaining the Java namespace. Keep ordinary debug on emulators and add an explicit `liveDebug` variant using real SDK configuration. Validate project/package at build time.
- [x] Add a Java Email/Password setup screen (register, sign in, verification/reload, reset, sign out) and a verified-owner Firestore connectivity check. This is infrastructure handoff to Ary, not completed Module 1/profile UI.
- [x] Test validation first, then implement. Test Auth create/sign-in and Firestore authorized/unauthorized operations in emulators; keep live user passwords out of scripts/logs. Build/lint/Checkstyle/unit/instrumentation checks, verify remote provisioning, document exact run steps.

## File boundaries and decisions

- `app/build.gradle`: `jiran.com` application ID, liveDebug build type and real config gate. Java namespace remains `com.jiran.app`; no source package rename required.
- `app/src/main/java/com/jiran/app/ui/EmailPasswordActivity.java`: basic setup UI; repository separates SDK access from screen. Debug retains existing local diagnostic screen and can open the auth flow for testing.
- `app/src/main/java/com/jiran/app/data/EmailPasswordRepository.java`: Auth and caller-owned `_connectionChecks/{uid}` write/read. Store only a server timestamp, no contact/profile data. No listing/schema assumptions.
- `firestore.rules`: deny all except verified callers' own valid connection-check documents. All product collections stay closed until Gathan's schema/rules handoff. Test unauthenticated, unverified, cross-user, malformed and valid writes, reads and updates.
- `.firebaserc`: default demo-jiran and explicit live alias. `firebase.json`: emailPassword provider plus database location; deploy with explicit project and selected products only.

## Review focus

Wrong project/package config must fail; no debug routing in liveDebug/release; unverified callers cannot access Firestore; users cannot act on another UID; UI survives blank input and async errors. Exact UM student domains remain a team requirement: the setup flow does not claim university affiliation verification or production onboarding readiness.

## Evidence

- CLI login and existing registration verified. Firestore API initially disabled. SDK config downloaded without creating another Android registration.
- Auth deployment succeeded: Email/Password enabled in `jiran-mad`. Initial deny-all rules deployed while provisioning the database. Firebase MCP then confirmed `(default)`, `STANDARD`, `asia-southeast1`, Firestore Native mode, free-tier enabled.
- Validation tests observed 3 failing assertions before implementation; final JVM suite is 6 tests. The isolated rules test observed a 403 for a valid owner create under deny-all rules, then passed with the narrow prototype rules. The latter were compiled and deployed explicitly to `jiran-mad`.
- `lintDebug lintLiveDebug testDebugUnitTest checkstyleMain assembleDebug assembleLiveDebug assembleDebugAndroidTest` passed. No lint errors; the pinned Gradle newer-version advisory remains. Existing backend Jest suite passed 9/9.
- Full local REST + Android smoke passed: verified-owner create/read/update and malformed/missing-field/cross-user/unverified/anonymous denials, diagnostic callable authorization, and 2 Android SDK tests. Local Auth test covers registration, verification email request, sign-in, reset request and sign-out with disposable emulator accounts. No real email was sent by tests.
- Read-only live Android probe passed with `connectedLiveDebugAndroidTest` and the `LiveFirebaseConnectionTest` class filter. It verified `jiran.com`/`jiran-mad`, expected login rejection and anonymous Firestore permission denial, without live account creation or writes. Real inbox delivery and completing a human verification/reset link are not tested.
- Independent review corrected two defects: CI aggregating credential-gated live variants, and Activity-scoped Task listeners leaving controls disabled after backgrounding. Follow-up review confirmed the fixes/rule scope and caught incompatible tests in shared instrumentation sources; explicit JUnit assumptions now skip them in other environments.
- Ruling: use existing registration `jiran.com`, retaining Java namespace `com.jiran.app`, rather than creating com.jiran.app/com.jiran.rabbit duplicates. Default CLI alias remains local `demo-jiran`; live deployments use explicit `--project jiran-mad` and product selectors. No billing upgrade, Functions/Storage deployment, team invitation, push or merge performed.
- Clean-checkout simulation: temporarily moved the ignored live config within the workspace, ran the exact credential-free Android CI command successfully, verified `verifyLiveFirebase` rejects absent configuration, and restored the file in `finally`. Config remains ignored by Git.
- Installed `liveDebug` on the API 37 AVD and cold-launched the app; Android reported `Status: ok` with `EmailPasswordActivity` as the displayed activity. Both debug and live APKs build; no live user credentials are used in commands or logs. The temporary headless AVD was shut down after verification.
- Final `git diff --check` passed. Live Auth and Firestore are configured and usable for this setup flow; mailbox link completion, UM-only onboarding, product schemas/rules, Maps, Functions/Storage, invitations and remote CI/merge remain separate work.

