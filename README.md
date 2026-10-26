# Jiran

Native Android **Java** app with Firebase **JavaScript / Node 22** Cloud Functions.
Read [Aaron's checklist and master prompt](team/aaron.md), [PRD](PRD.md) and
[team ownership](TEAM_PLAN.md) before changing the foundation.

## Local setup

Use Android Studio with API 37, Build Tools 36.0.0, Platform Tools, and an Android
emulator. Build runtime: JDK 25; source compatibility: Java 17. The pinned wrapper
uses Gradle 9.6.0 / AGP 9.4.1. Install Node 22; Firebase commands below use its official CLI.
Open the repository in Android Studio; it generates ignored `local.properties`.
For terminal builds set `ANDROID_HOME` to your SDK folder if not already set.

PowerShell, from the repository root:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
npm.cmd ci --prefix functions
npm.cmd test --prefix functions
npm.cmd run check --prefix functions
./gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --console=plain
$env:FUNCTIONS_DISCOVERY_TIMEOUT = '30'
npx.cmd -y firebase-tools@latest emulators:exec --project demo-jiran --only auth,firestore,functions,storage 'node scripts/emulator-smoke.cjs'
```

On macOS/Linux use `npm`, `npx` and `bash ./gradlew`; configure the SDK path
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
npx.cmd -y firebase-tools@latest emulators:exec --project demo-jiran --only auth,firestore,functions,storage 'node scripts/emulator-smoke.cjs --android'
```

Debug always uses `demo-jiran` and Android emulator host `10.0.2.2`. Physical
phones need an explicitly configured development connection; this initial setup
targets the AVD. Release requires the real team's ignored `app/google-services.json`;
it has no emulator routing. The registered application ID is `jiran.com`; the
Java namespace remains `com.jiran.app`. The Functions region remains provisional.
The developer callable is exported only under the Functions emulator.

## Use the existing live Firebase backend

Project: **jiran-mad**. Existing Android app: **Jiran**, package **jiran.com**,
Firebase app ID `1:157082520904:android:cc25cf096fa7c07391b77d`.
Email/Password Auth is enabled. Firestore `(default)` is Standard edition in
Singapore (`asia-southeast1`), using the user-approved location. No duplicate app
was created and no billing upgrade or Functions/Storage deployment was performed.

The ignored `app/google-services.json` has been downloaded on this machine.
Other teammates with project access can retrieve it using:

```powershell
npx.cmd -y firebase-tools@latest login
npx.cmd -y firebase-tools@latest apps:sdkconfig ANDROID 1:157082520904:android:cc25cf096fa7c07391b77d --project jiran-mad --out app/google-services.json
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
There is no live Maps integration, verified fare/emission dataset, occupancy
rounding default or inferred attendance assumption in this batch. Gathan/Ary/Aly
still need to agree the production inputs and UI integration.

The existing REST smoke checks callable authorization, all scenarios, selection
and cost arithmetic. Its `--android` mode runs four Android integration checks,
including the SDK/repository/parser flow for all scenarios and native preview
source/warning/fallback text after a verified local callable. The on-time fixture
includes explicitly synthetic provenance, a walking warning and driving fallback.
These are sample display states, not Google results. Optional sources/warnings are
validated and retained by the immutable Android model. Local instrumentation with
`capturePreview=true` writes a full-content native render to the app's external
files directory as `commute-preview.png` for layout QA.

## Deterministic match backend

`functions/src/match.js` implements FR3.2's deterministic weighted scoring with
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

## Routes provider adapter

The server-only `functions/src/routes-client.js` accepts explicit authorized
coordinates, mode and departure time, with an injected server credential. It
validates before sending a request, enforces a deadline and response size cap,
normalizes duration/MYR fare, and retains route warnings/fallback status. Tests
inject transport and use synthetic responses; no Google request runs during
ordinary tests, builds or emulator startup. No callable activates this client yet.

See [the server interface](docs/api-contracts.md#implemented-server-provider-boundary-not-a-callable)
and [plan/evidence](docs/superpowers/plans/2026-10-27-routes-adapter.md).
Billing, API/key restrictions, real route probes, approved cost inputs and final
schema/UI integration remain required. Never put a server Maps key in Android or Git.

`functions/src/commute-pricing.js` turns explicit sourced per-mode assumptions
into exact per-trip sen for the monthly calculator. It covers e-hailing base/km/
multiplier plus pickup time, and driving fuel/tolls/parking. No production rate
defaults are supplied. Missing fares stay unavailable, and route attribution,
warnings and fallback status survive into the calculation response. See the
[pricing contract](docs/api-contracts.md#implemented-configurable-pricing-layer).
