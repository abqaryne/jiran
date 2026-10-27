# Jiran

Native Android **Java** app with Firebase **JavaScript / Node 22** Cloud Functions.
Read [Aaron's checklist and master prompt](docs/team/aaron.md), [PRD](docs/product/PRD.md) and
[team ownership](docs/team/TEAM_PLAN.md) before changing the foundation.
All documents are indexed in [docs/](docs/README.md).

## Repository layout

The repository has **three main parts**. Everything else is configuration.

```
jiran/
├── android/    The Android app (Java). Open THIS folder in Android Studio.
│   ├── app/             source code and resources (src/main, src/debug, src/test, src/androidTest)
│   ├── gradle/ gradlew  build tooling (Gradle wrapper)
│   └── config/          code style rules (Checkstyle)
├── backend/    Firebase and Cloud Functions (Node.js). Run Firebase commands here.
│   ├── functions/       the Cloud Functions and their Jest tests
│   ├── firebase.json, firestore.rules, storage.rules, firestore.indexes.json, .firebaserc
│   ├── scripts/         emulator smoke tests
│   └── tests/load/      k6 load test
├── docs/       Product, design and team documents. Start with docs/README.md.
├── .github/    CI workflows and the pull request template
└── README.md, AGENTS.md, .gitignore, .editorconfig
```

Working on screens, components or app logic: `android/`. Working on data, rules or AI and Maps calls: `backend/`.
Writing or reading requirements, design or the plan: `docs/`.

Budget is **free-only**. Keep Firebase Auth/Firestore on Spark; no billing upgrade
or paid provider activation. See [no-billing production research](docs/no-billing-production.md)
for the candidate Worker/openrouteservice architecture and unresolved transit,
capacity and team decisions. It has not replaced the current local Functions stack.

## Local setup

Use Android Studio with API 37, Build Tools 36.0.0, Platform Tools, and an Android
emulator. Build runtime: JDK 25; source compatibility: Java 17. The pinned wrapper
uses Gradle 9.6.0 / AGP 9.4.1. Install Node 22; Firebase commands below use its official CLI.
Open the **`android/` folder** in Android Studio; it generates ignored `android/local.properties`.
The repository has three parts: `android/` (the app), `backend/` (Firebase and Cloud Functions) and `docs/`.
For terminal builds set `ANDROID_HOME` to your SDK folder if not already set.

PowerShell, from the repository root (Android commands run in `android/`, Firebase commands in `backend/`):

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
npm.cmd ci --prefix backend/functions
npm.cmd test --prefix backend/functions
npm.cmd run check --prefix backend/functions
cd android
./gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --console=plain
cd ../backend
$env:FUNCTIONS_DISCOVERY_TIMEOUT = '30'
npx.cmd -y firebase-tools@latest emulators:exec --project demo-jiran --only auth,firestore,functions,storage 'node scripts/emulator-smoke.cjs'
```

On macOS/Linux use `npm`, `npx` and `bash ./gradlew` (from `android/`; run `firebase` and `node scripts/...` from `backend/`, where `firebase.json` lives); configure the SDK path
for your machine. The first emulator run downloads Java runtimes and may take time.
The emulators listen on loopback and require Java. No Firebase login or paid
project is needed for `demo-jiran`.

To try the app, start the emulators in a separate terminal:

```powershell
npx.cmd -y firebase-tools@latest emulators:start --project demo-jiran
```

Then run `node scripts/emulator-smoke.cjs` with emulator environment variables
set (or create a password account and mark its email verified in the Emulator UI
at `http://127.0.0.1:4000`). The smoke script prints its disposable local account.
Install/run the debug app on the Android emulator and enter that account's email
and password. Tap **Check backend connection**. On Android 17, allow the debug app's
local network permission and tap again after granting it. A verified call writes a local
Firestore diagnostic through Functions. Except for a verified user's own timestamp-only
connection check, Firestore/Storage client access is denied until Gathan supplies application rules. This check does not implement
UM registration or the production UI.

For a standalone smoke run against already running emulators:

```powershell
$env:FIREBASE_AUTH_EMULATOR_HOST = '127.0.0.1:9099'
$env:FIRESTORE_EMULATOR_HOST = '127.0.0.1:8080'
node scripts/emulator-smoke.cjs
```

For an automated Android SDK integration check, boot one AVD, build the test APK,
then run (the script installs both APKs and grants the debug local-network permission):

```powershell
./gradlew.bat assembleDebug assembleDebugAndroidTest
$env:FUNCTIONS_DISCOVERY_TIMEOUT = '30'
npx.cmd -y firebase-tools@latest emulators:exec --project demo-jiran --only auth,firestore,functions,storage 'node scripts/emulator-smoke.cjs --android'
```

Debug always uses `demo-jiran` and Android emulator host `10.0.2.2`. Physical
phones need an explicitly configured development connection; this initial setup
targets the AVD. Release requires the real team's ignored `android/app/google-services.json`;
it has no emulator routing. The registered application ID is `jiran.com`; the
Java namespace remains `com.jiran.app`. The Functions region remains provisional.
The developer callable is exported only under the Functions emulator.

## Use the existing live Firebase backend

Project: **jiran-mad**. Existing Android app: **Jiran**, package **jiran.com**,
Firebase app ID `1:157082520904:android:cc25cf096fa7c07391b77d`.
Email/Password Auth is enabled. Firestore `(default)` is Standard edition in
Singapore (`asia-southeast1`), using the user-approved location. No duplicate app
was created and no billing upgrade or Functions/Storage deployment was performed.

The ignored `android/app/google-services.json` has been downloaded on this machine.
Other teammates with project access can retrieve it using:

```powershell
npx.cmd -y firebase-tools@latest login
npx.cmd -y firebase-tools@latest apps:sdkconfig ANDROID 1:157082520904:android:cc25cf096fa7c07391b77d --project jiran-mad --out android/app/google-services.json
```

In Android Studio select **app → liveDebug** in Build Variants, start an AVD or
connect a phone, and Run. From PowerShell with a running device:

```powershell
./gradlew.bat installLiveDebug
```

Open Jiran. Create an account, open its verification email, then tap **I verified
my email · refresh**. The **Check my Firestore connection** button writes and
reads only your `_connectionChecks/{uid}` server timestamp. Sign-in, password
reset, verification resend and sign-out are available. This is a backend setup
screen for Ary to integrate; student-domain restrictions, profiles and final
product onboarding are not yet implemented. Do not enter a real password in
terminal commands, test scripts or chat.

Ordinary **debug** stays local; its **Open Email/Password setup** button exercises
the same flow with disposable emulator accounts. The two variants share package
`jiran.com`, so installing one replaces the other. Keep Google configuration out
of Git. Live builds fail if it belongs to another project or package.

Prototype Firestore rules allow only verified owners' connection checks, validate
the timestamp-only schema on create/update, and deny all product collections.
Gathan must review/extend the product rules before feature integration; this is
not full NFR2 signoff. Rules were tested locally and explicitly deployed to
`jiran-mad`. Future authorized deployments must name both project and products:

```powershell
npx.cmd -y firebase-tools@latest deploy --only auth,firestore:rules --project jiran-mad
```

CI builds only the emulator variant and needs no live configuration. Use
`lintLiveDebug`/`assembleLiveDebug` locally to validate the live variant. A read-only
live Android SDK probe is available with an AVD running:

```powershell
./gradlew.bat '-PfirebaseTestBuildType=liveDebug' '-Pandroid.testInstrumentationRunnerArguments.class=com.jiran.app.LiveFirebaseConnectionTest' connectedLiveDebugAndroidTest
```

It checks the configured project, rejection of a nonexistent login, and denial of
anonymous Firestore reads; it creates no live accounts or documents and sends no email.

## Code structure and design system

Product UI follows [DESIGN.md](docs/design/DESIGN.md): the tokens, styles and component layouts are in
`android/app/src/main/res` (`values/colors.xml`, `dimens.xml`, `type.xml`, `themes.xml`,
`styles_components.xml`, `layout/component_*.xml`, `font/`). Product screens use the
`Theme.Jiran` theme (set per activity; the foundation screens keep their temporary theme).

```
android/app/src/main/java/com/jiran/app/
  JiranApplication.java
  data/            Firebase and data access: FirebaseSetup, repositories (data/repository), remote, local, model
  domain/          pure Java rules with no Android imports: pricing, balance, match, commute (JVM unit tests)
  util/            small helpers (Resource, input checks)
  ui/
    shell/         ShellActivity: product shell with the floating tab bar (Home, Rooms, Match, Unit)
    home/ rooms/ match/ unit/             the four tabs (placeholders)
    auth/ onboarding/ profile/            Module 1
    roomdetails/ listing/                 Module 2 (with rooms/)
    group/ chat/                          Module 3 (with match/)
    commute/                              Module 4
    contract/                             Module 6 (private contract check)
    notifications/                        Profile and settings screens
    common/        design system views: BadgeView, AvatarView, RoomCardView, ListRowView, CommuteOptionView,
                   InfoBannerView, SectionHeaderView, SearchBarView, EmptyStateView, StepProgressView, JiranTabBar
    gallery/       (src/debug only) ComponentGalleryActivity, launcher icon "Jiran UI"
```

MVVM: Fragment draws, ViewModel holds LiveData state (no Views or Context), a Repository is the only door to
Firebase and Functions. `ShellActivity` is registered but not launched yet; the temporary developer screens stay
the launcher until Ary wires the shell in after sign-in. To add a feature: create `ui/<feature>/` with its
Fragment and ViewModel, add the destination to `res/navigation/nav_main.xml`, put every user-facing string in
`strings*.xml`, and use only design system styles.

## Workflow

Use feature branches and PRs. Ary approves and merges; Aaron reviews backend/Maps,
Gathan reviews schema/rules, and another member reviews each PR. Ask the repository
administrator to protect `main` with reviews and required CI jobs `android` and
`backend`. The workflow file alone does **not** enable remote branch protection.
Feature commits use `[ModN][FRN.N] short description`; agree infrastructure labels
with the team. Log actual Trello hours and AI use.

See [architecture](docs/architecture.md), [draft API contracts](docs/api-contracts.md),
[AI log](docs/ai-usage-log.md), and [batch plan/evidence](docs/superpowers/plans/2026-10-27-aaron-foundations.md).
The [live setup plan](docs/superpowers/plans/2026-10-27-firebase-live-setup.md) records
current setup evidence. Team invitations, Maps/billing/coverage, product schema,
scoring weights and remaining feature work still require their team inputs.

## Module 4 calculation preview

On branch `feature/aaron-commute-core`, the pure commute calculator and a local
Android preview implement the calculation behavior of FR4.1–4.3 against explicit
server-owned fixtures. Run the ordinary **debug** variant with local emulators,
sign in with a verified local account, and open **Commute calculator preview**.
Choose an on-time, no-on-time or missing-data scenario and tap **Calculate sample**.

The preview labels every figure as synthetic sample data, compares four modes,
and chooses only a known-cost mode that arrives before class. In the on-time
fixture, late free walking is rejected and transit gives RM612.00: room RM450.00,
utilities RM62.00 and commute RM100.00. If no option qualifies, no total appears.
Missing fares/schedules and unknown CO₂ stay explicit. Deposit is excluded.

The callable is emulator-only and accepts `{scenarioId}`; production listing/unit
requests remain draft. See the [API contract](docs/api-contracts.md) and
[implementation plan](docs/superpowers/plans/2026-10-27-commute-core.md).
There is no live Maps integration, approved local fare or Malaysian-calibrated
emissions dataset, occupancy rounding default or inferred attendance assumption.
Published UK proxy factors are sourced separately in `docs/emission-methodology.md`. Gathan/Ary/Aly
still need to agree the production inputs and UI integration.

The existing REST smoke checks callable authorization, all scenarios, selection
and cost arithmetic. Its `--android` mode runs six Android integration checks,
including the SDK/repository/parser flow for all scenarios and native preview
source/warning/fallback text after a verified local callable. The on-time fixture
includes explicitly synthetic provenance, a walking warning and driving fallback.
These are sample display states, not Google results. Optional sources/warnings are
validated and retained by the immutable Android model. Local instrumentation with
`capturePreview=true` writes a full-content native render to the app's external
files directory as `commute-preview.png` for layout QA.

## Deterministic match backend

`backend/functions/src/match.js` implements FR3.2's deterministic weighted scoring with
explicit versioned weights, seven factor breakdowns, stable ranking, overlapping
class weekdays and rule reason codes for AI fallback. Missing required answers
produce an unscored state. It returns no raw answers, private contact fields or
exact timetables. This core requires an authorized candidate set from its caller.

With the local emulators running, the callable `computeMatchScores` accepts only
`{"scenarioId":"ranked"}`, `missing-answers` or `empty`, from a verified local
account. The REST smoke exercises it automatically. The synthetic ranked fixture
returns sample-a at 100 and sample-b at 50; these are not live recommendations.
See [the contract](docs/api-contracts.md#computematchscores--implemented-local-fixture-interface-fr32)
for normalization, rounding and validation, and [the plan](docs/superpowers/plans/2026-10-27-match-core.md)
for evidence. Production questionnaire mapping, survey weights, candidate privacy
and the final Match screen remain team integration work.

The developer shell now opens a local Match score preview. `MatchRepository` and
immutable `MatchSummary` verify the SDK handoff with ranked, missing-answer and
empty fixture scenarios. The guarded screen shows sample scores, rule reasons,
overlap days and sample weights/similarity; it does not call AI or recalculate
scores. Native checks cover the bound data and missing/empty display states.
`capturePreview=true` also writes `match-preview.png`; these are full-content
render captures, not scroll-gesture coverage. See the [Android handoff plan](docs/superpowers/plans/2026-10-27-android-match-preview.md).

## Routes provider adapter

The server-only `backend/functions/src/routes-client.js` accepts explicit authorized
coordinates, mode and departure time, with an injected server credential. It
validates before sending a request, enforces a deadline and response size cap,
normalizes duration/MYR fare, and retains route warnings/fallback status. Tests
inject transport and use synthetic responses; no Google request runs during
ordinary tests, builds or emulator startup. No callable activates this client yet.

See [the server interface](docs/api-contracts.md#implemented-server-provider-boundary-not-a-callable)
and [plan/evidence](docs/superpowers/plans/2026-10-27-routes-adapter.md).
Billing, API/key restrictions, real route probes, approved cost inputs and final
schema/UI integration remain required. Never put a server Maps key in Android or Git.

`backend/functions/src/commute-pricing.js` turns explicit sourced per-mode assumptions
into exact per-trip sen for the monthly calculator. It covers e-hailing base/km/
multiplier plus pickup time, and driving fuel/tolls/parking. No production rate
defaults are supplied. Missing fares stay unavailable, and route attribution,
warnings and fallback status survive into the calculation response. See the
[pricing contract](docs/api-contracts.md#implemented-configurable-pricing-layer).

## Published emissions factors

The [versioned catalogue](backend/functions/config/emission-factors.json) records exact
UK DESNZ2026 CO₂-component factors and source workbook cells/hashes. The mixed-leg
calculator sums walking/bus/rail/vehicle emissions with exact integer mass units,
explicit vehicle occupancy and one final rounding step. UK proxies require opt-in;
missing factors or incomplete leg distances leave CO₂ unknown. Pricing preserves
the profile into the monthly calculation without changing known costs/timing.

These are operational CO₂ estimates with UK fleet/grid limitations, not total
lifecycle CO₂e or measured Malaysian emissions. Existing local fixture factors
remain synthetic. See [methodology and sources](docs/emission-methodology.md) and
[the contract](docs/api-contracts.md#implemented-published-factor-emission-profile).
