# Aaron Foundations Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans to implement this plan task-by-task. Track verification below.

**Goal:** Establish a reproducible local Java Android/Firebase foundation before implementing Aaron's commute and matching features.

**Architecture:** A minimal Java Views application uses debug-only Firebase emulators. Small Node 22 callable Functions isolate authorization and domain behavior. No real project, key or Maps data is assumed.

**Tech Stack:** Android Java, AGP 9.4.1, Gradle 9.6.0, Java 25 build runtime / Java 17 source, Android API 37, Firebase, Node 22, Jest.

**Spec:** `team/aaron.md`, `PRD.md`, `TEAM_PLAN.md`, `ASSUMPTIONS.md`.

## Global constraints

- Preserve Java Android and JavaScript Functions; Ary owns UI and final merges.
- Use `demo-jiran` only for local integration. `com.jiran.app` is provisional until Ary confirms the registered application ID.
- Do not deploy, create billing resources, push, merge or fabricate team decisions.
- Preserve existing changes to Aaron's role document.
- Local foundations are authorized by the user's request to implement by batches. Work in place on `feature/aaron-foundations` so existing documentation stays visible; no worktree migration or automatic commits.

## Review focus

1. Missing/unverified auth must fail at the backend boundary.
2. Client-supplied identity must not grant access to another user.
3. Release must never connect to emulators or run with demo configuration.
4. Local integration must exercise real Auth, Firestore and callable Functions.
5. Missing project credentials must leave cloud work explicitly pending.

## Batch 1: backend and local integration

Files: `functions/package.json`, `functions/index.js`, `functions/src/session.js`, `functions/test/session.test.js`, `scripts/emulator-smoke.cjs`, `firebase.json`, `firestore.rules`, `storage.rules`, `.gitignore`.

Interface: `session(request)` returns `{uid, status: 'ok'}` only for a verified authenticated user. `sessionCheck` callable wraps it and writes a per-user local diagnostic record only in the emulator. No production feature endpoints yet.

- [x] Add Jest tooling and write rejection/identity tests before implementation; observe failure.
- [x] Implement authorization and callable export; verify Jest.
- [x] Configure loopback-only Auth/Firestore/Functions/Storage emulators with deny-all baseline rules (Gathan replaces these through review).
- [x] Run real REST integration: anonymous rejection, unverified rejection, verified caller success, own UID retained despite spoofed payload; confirm diagnostic record in Firestore using emulator-only admin access.

## Batch 2: Java Android shell

Files: root Gradle configuration and wrapper, `app/build.gradle`, manifests, Java application/UI/data/util packages, strings, layout, `config/checkstyle/checkstyle.xml`.

Interface: debug initializes demo Firebase options and routes Auth/Firestore/Functions to `10.0.2.2`; a developer connection button calls `sessionCheck`. Release requires the real team's `google-services.json` and never uses demo options. This is a developer shell, not Ary's final Figma screen.

- [x] Generate standard pinned Gradle wrapper; verify checksum and JDK compatibility.
- [x] Add Java app and Firebase SDK dependencies; keep local cleartext/debug routing out of release.
- [x] Run Android lint, Checkstyle, unit tests and APK assembly.
- [x] Install and launch on available AVD; report device verification separately from build success.

## Batch 3: CI, handoff and contracts

Files: `.github/workflows/ci.yml`, `.github/pull_request_template.md`, `README.md`, `docs/architecture.md`, `docs/api-contracts.md`, `docs/ai-usage-log.md`, `team/aaron.md`.

- [x] CI: Android lint/tests/Checkstyle/debug build; locked Node install/Jest; emulator smoke on demo project.
- [x] Document commands, branching and required checks for Ary; do not claim remote protection is configured.
- [x] Draft request/response JSON, errors and ownership for planned commute, transit, match, split and AI contracts; mark unapproved shapes as drafts and unimplemented endpoints as unavailable.
- [x] Review diff for secrets, scope, release separation and requirements; run final checks and record evidence.

## Later feature batches (gated by real inputs)

- Module 4 pure calculations and adapter tests after monthly trip/rounding rules and schema are agreed; no invented fares/emission factors.
- Maps coverage probe after restricted credentials and billing owner are available. Record assumptions #5/#11; confirm provider caching terms before implementation.
- Match scoring after agreed questionnaire scales and weights (#9); coordinate explanations with Ilham.
- Live Firebase provisioning and GitHub protection after project/repository permissions and team IDs are confirmed.

## Execution ledger

- Preflight: only planning Markdown; no baseline build/test suite. Existing `team/aaron.md` edit preserved. Installed AGP 9.4.1 / Gradle 9.6 / SDK 37 / JDK 25 inspected; official compatibility checked. No project credentials inferred.
- Batch 1: complete locally. Initial authorization assertions failed against an empty implementation, then passed; final Jest suite: 9/9. Real emulator smoke passed caller rejection, spoof resistance, Admin SDK write and denied client read. Emulator startup emitted upstream Java Unsafe deprecation warnings; the smoke exited 0.
- Ruling: keep authorization independent from Firebase SDK imports because Jest/Node 22 exposed the SDK's transitive ESM loader boundary; callable adapter maps domain errors to HttpsError. No test mocks hide the SDK integration.
- Batch 2: complete locally. `gradlew.bat lint test checkstyleMain assembleDebug assembleDebugAndroidTest --rerun-tasks --console=plain` passed all 81 tasks; Java tests 2/2. Lint: 0 errors, 1 version-update advisory for the deliberately pinned Gradle version. Checkstyle passed. Debug APK and Android test APK built.
- Android end-to-end: `firebase emulators:exec --project demo-jiran --only auth,firestore,functions,storage "node scripts/emulator-smoke.cjs --android"` exited 0 with `OK (1 test)` on API 37. This exercises real Android Auth and callable SDKs against local Functions/Firestore. Cold launch of MainActivity returned `Status: ok`; the temporary headless AVD was shut down after verification.
- Ruling: debug API 37 requires local-network runtime permission. The first device test failed on network access; added debug-only permission/request and automated test grant. A subsequent run exposed the SDK's synthetic-key format requirement; supplied a clearly synthetic 39-character value, then reran successfully. No real key or cloud project was used. Permission-dialog interaction itself was not automated.
- Independent review found three issues: blank login input crash, potentially live Firestore diagnostic writes, and demo release configuration acceptance. Added failing Java/environment regression tests, fixed the guards, then reran green. Reviewer rechecked fixes and reported no remaining important issues.
- Release checks: `assembleRelease` rejected absent configuration. A temporary ignored demo JSON fixture was also rejected by `:app:verifyReleaseFirebase` and removed in `finally`; real release compilation is not claimed.
- Batch 3: local artifacts complete. CI workflow, PR guidance, architecture, draft contracts, setup instructions and AI record added. Remote CI has not run; branch protection, merge/reviews, real project access, schema/contract signoff, Maps/billing/coverage and feature/NFR evidence remain pending.
- Final source/config/doc review and `git diff --check` passed. Work remains uncommitted on the feature branch; no push, merge, deployment or team message sent. Actual human hours and full AI conversation evidence still need the team's records.

