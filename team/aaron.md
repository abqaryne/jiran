# Aaron: Software Architect and Backend Lead

## Start here: prerequisites and master prompt

Prepared on **27 October 2026**. The original preparation update created documentation only. Current local and live Firebase progress is tracked below and in the batch plans; remaining cloud work is identified explicitly. Keep the responsibilities and schedule below as the role checklist.

### Confirm the stack before downloading anything

The [PRD](../PRD.md) and [team plan](../TEAM_PLAN.md) specify **native Android in Java with Gradle**, and **Firebase Cloud Functions in Node.js/JavaScript**. The pasted Flutter setup advice was based on an unverified role file and does not apply to this repository. Flutter, Dart and FlutterFire CLI are not prerequisites. VS Code is optional; Android Studio is the main Android IDE. Cloud Functions use JavaScript by team decision; the Java runtime used by Firebase emulators does not change the Functions language.

### Downloads and local readiness

Read-only checks on this Windows machine found the following. Installed does not mean that the app, emulator or cloud connection has been tested.

| Tool | Purpose and what to prepare | Observed on 27 October 2026 |
|---|---|---|
| [Git for Windows](https://git-scm.com/downloads/win) | Branches and PRs; configure your own commit name and email. | Git 2.48.1; origin is `https://github.com/abqaryne/jiran.git`. Push/admin access not checked. |
| [Android Studio](https://developer.android.com/studio) | Java Android development. Use a Views-based Java template when scaffolding, coordinated with Ary. | Installation found at `C:\Program Files\Android\Android Studio`. |
| Android SDK, via Studio's SDK Manager | Platform matching the eventual `compileSdk`, Build Tools, Platform Tools, Command-line Tools, and Emulator plus a system image if using an AVD. A USB-debugging Android phone is an alternative to the AVD. | SDK and platform-tools/emulator/system-images folders exist under `%LOCALAPPDATA%\Android\Sdk`; individual versions, licences and a running device not checked. `adb.exe` exists but `adb` is not on PATH. |
| JDK | Use a JDK compatible with the chosen Android Gradle Plugin and Gradle wrapper; align IDE, terminal and CI. Firebase emulators also need Java; JDK 21 is a conservative emulator baseline. | Temurin 25.0.3 on PATH and Studio JBR 25.0.3 found. Do not assume JDK 25 is compatible with an Android build until the Gradle/AGP versions are chosen. Install another JDK only if needed. |
| [Node.js](https://nodejs.org/en/download) and npm | Propose Node **22** for local Functions and CI, pinned to the same major in the eventual Functions runtime configuration. Recheck supported runtimes before deployment. | Node 22.14.0 and npm 10.9.2 found. |
| [Firebase CLI](https://firebase.google.com/docs/cli) | Firebase project access, Functions and Emulator Suite. Install with `npm.cmd install -g firebase-tools` only if missing. | CLI 15.20.0 verified. Login and project access not checked. |

Gradle comes through the project's **Gradle wrapper**; do not install a separate global Gradle. Jest, Firebase SDKs, Checkstyle and Android test libraries belong in project configuration, not a separate manual download checklist. Docker, Python and a separate database server are not required by the current plan.

After reviewing local configuration, run these checks in PowerShell:

```powershell
git --version
node --version
npm.cmd --version
java -version
firebase.cmd --version
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" version
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
```

In Android Studio, confirm SDK packages/licences, select the build JDK and boot an AVD or connect a phone. Optionally add the SDK's `platform-tools` folder to your user PATH. After choosing the team's Google account, run `firebase.cmd login` and `firebase.cmd projects:list`; verify the intended project ID before initialization. The initial repository contained planning Markdown only. Local Android/Functions build and emulator files now exist; see [README.md](../README.md) for current commands and the [batch plan](../docs/superpowers/plans/2026-10-27-aaron-foundations.md) for verification evidence.

### Access and team decisions needed first

- **GitHub:** access to clone and push feature branches, plus repository administration rights for branch protection (or an owner who can configure it). Ary remains the final approver and merger.
- **Firebase/Google Cloud:** confirm whether the team already has a project before creating one. Agree its owner, project ID, Android application ID, Firestore location and Functions region with Ary and Gathan. Add team members with permissions appropriate to their work. Plan Auth, Firestore, Storage, Functions and Messaging; do not expect every product, especially FCM delivery, to have full local emulation.
- **Billing:** identify the team's billing owner. Cloud Functions deployment requires the Blaze plan; local emulator development can start before deployment. Maps requires its own enabled APIs and billing setup. Agree quotas and budget alerts before paid calls; a budget alert does not impose a spending cap.
- **Maps credentials:** enable only needed APIs (Routes, and Places/Maps SDK where the app uses them). Use separate credentials appropriate to Android and server calls. Restrict the Android key by package and signing certificate and restrict APIs; keep server credentials in managed secrets with supported restrictions. Never commit keys or service-account files.
- **Figma:** [Jiran UI design](https://www.figma.com/design/kjYgovwDJBU9ArWSfMa3OD/Jiran?node-id=5-3). Metadata access was confirmed for page `5:3`, including Room details `14:152` and its commute section `14:205`. Use it as the UI reference with Ary. Sample fares, times and totals are mock values, and iOS-named design assets do not change the Android platform decision.
- **Gathan:** agree listing/room coordinates and prices, utilities/occupancy, faculty destinations, profile/timetable fields, schema, rules and seed-data ownership. Use labelled stub fixtures until these are agreed.
- **Ary:** agree Android package structure (`ui`, `data`, `util`), shared components and API-facing states. **Ilham:** agree AI request/response shapes and score-to-explanation handoff. **Aly/Dafi:** obtain survey assumptions and UM shuttle information through their existing responsibilities.

Do not run `firebase init` over existing configuration or create duplicate cloud projects. Missing cloud access does not block writing contracts, pure calculation tests or using local stub data.

### Aaron's order of work

1. **Now, Step 0 (through 8 November 2026):** verify tools and access; coordinate the Java app foundation with Ary; establish feature branches/PR rules, CI, Firebase connection, small JavaScript Functions skeleton and Emulator Suite. CI must run Android lint and unit tests, include Checkstyle for the Definition of Done, and test Functions with Jest. Protect the default branch with required reviews/checks where the repository plan supports it.
2. **Before paid commute integration:** verify Maps pricing/quotas and transit coverage on real Pantai Dalam/Kerinchi-to-UM routes. Record evidence for assumptions #11 and #5 by 8 November. If UM shuttle coverage is missing, coordinate seeded stops/timetables with Dafi and Gathan; do not invent service availability.
3. **By 15 November:** write `docs/api-contracts.md` with agreed inputs, outputs, units, time zone, authentication/authorization, validation, errors and sample fixtures for `getCommuteOptions`, `getTransitRoute`, `computeMatchScores`, `splitRoomPrices` and AI functions agreed with Ilham. Names below are planned interfaces, not implemented endpoints.
4. **Wave A, 16–29 November:** Module 4 P0, including all four modes, class-time arrival checks, per-room true monthly cost and sourced CO₂ estimates. Monthly total is room price + equal utilities share + cheapest commute that arrives on time. Deposit is separate. If no mode arrives on time, show that state rather than a fabricated monthly figure. Decide monthly trip/day counts, return trips, rounding and missing-data behavior explicitly.
5. **Wave B, 30 November–13 December:** deterministic weighted match score with Jest tests, coordinated with Ary/Ilham. Add P1 peak/off-peak and transit steps after P0 works. Lead the P0 integration freeze on 13 December.

Use [ASSUMPTIONS.md](../ASSUMPTIONS.md) as the canonical numbered list: #2 peak windows with Aly due 12 November; #3 e-hailing, #4 car cost, #5 transit fares, #6 CO₂ and #9 match weights due 13 November.

**First technical readiness milestone:** the Java app runs on an Android device/emulator, connects to local Auth/Firestore/Functions, and successfully calls one small local Function; relevant Android and Jest checks pass. Verify the Android emulator's host connection (normally `10.0.2.2`) and use debug-only emulator routing. Never mark cloud setup, security or NFR performance complete from tool installation alone. NFR1 ultimately requires p95 ≤3 seconds at 100 concurrent users; NFR2 requires unauthorized-access denial tests with Gathan/Aly.

### Reusable master prompt — paste before each Aaron task

```text
You are helping me act as Aaron, Jiran's Software Architect and Backend Lead.

Read AGENTS.md if present, team/aaron.md, PRD.md, TEAM_PLAN.md,
ASSUMPTIONS.md, GLOSSARY.md and USER-FLOW.md before changing anything.
Read existing architecture/API/schema/build documentation and relevant code
when present. Inspect git status and preserve others' uncommitted work.
For Firebase tasks, always find and use the relevant official Firebase agent
skills as described in AGENTS.md. The existing live project is jiran-mad and
its registered Android application ID is jiran.com; never create duplicates.
Treat team/aaron.md as my role checklist; report conflicts with project
requirements instead of silently choosing a new stack or inventing decisions.

Fixed stack: native Android Java + Gradle; Firebase Auth, Firestore, Storage,
Functions and Messaging. Cloud Functions are small JavaScript functions on
Node.js, tested with Jest, without a large framework. Match local Node,
Functions runtime and CI versions; verify Gradle/AGP/JDK compatibility.
Do not introduce Flutter, Dart, Kotlin app code or a new backend stack.

My scope: architecture, Cloud Functions structure, emulator setup, CI and
branching, Maps/Routes integration, Module 4 FR4.1–4.3 end to end, Module 3's
deterministic match-score Function, backend/Maps reviews and integration freeze.
Ary owns shared UI and final PR merge; Gathan owns schema/security rules;
Ilham owns AI and match-reason generation; Aly owns QA/research; Dafi coordinates
delivery and UM transport information. Coordinate interfaces with them; use
explicit stub fixtures rather than silently defining their final schemas.

For the task below, first state the relevant FR/NFR, current repository state,
missing prerequisites and smallest useful next step. For preparation-only tasks,
stop after the requested documents/checks: do not scaffold, install or deploy.
For implementation tasks, work incrementally within the authorized scope.
Read existing Firebase configuration before initialization and verify project
identity before cloud changes; do not create duplicate projects.

Preserve P0 before P1/P2. Module 4 P0 compares walking, transit plus UM shuttle
where data exists, estimated e-hailing and driving at actual class times.
True monthly cost = room price + equal utilities share + cheapest on-time
commute. Keep deposit separate. Never recommend a late/unavailable route;
show an explicit no-on-time-route state. CO2 belongs on detail commute cards,
not Room search cards. Treat Figma numbers as fixtures, not verified data.
Use a transparent deterministic formula for match scores; AI only explains.

Keep fares, peak windows, monthly trip counts, emission factors and score
weights configurable and sourced. Mark unverified values as assumptions and
record findings in ASSUMPTIONS.md. Check current Maps pricing, coverage,
runtime support and API limitations using official sources. Design caching
around NFR1 and provider storage/caching terms; never cache private user data
in shared results. Enforce authorization inside Admin SDK functions too:
Firestore Security Rules do not protect Admin SDK access. Never trust a client
userId to establish identity. Keep secrets out of code, commits and logs.

Verify changed behavior with relevant Java unit tests, Android lint/Checkstyle,
Jest and emulator tests. Use the actual configured scripts; report checks that
cannot run and why. Do not claim NFR1 without load evidence or NFR2 without
authorization tests. Every change goes through a feature branch and PR;
Ary performs the final merge. Use [ModN][FRN.N] commit messages for feature
work; agree infrastructure labels with the team. Record hours on Trello and
retain exact AI prompts and testing/modification evidence for the AI Usage Log.
Do not send team messages, merge, or deploy unless the task authorizes it.

Finish with what changed, checks and results, remaining blockers/assumptions,
and the next Aaron action. Update this role checklist only with verified work.

Current task: <insert one specific Aaron task here>
Acceptance criteria: <insert what must be demonstrably true>
```

### Official setup references

- [Android Studio setup](https://developer.android.com/studio/install) and [Android build JDK selection](https://developer.android.com/build/jdks).
- [Firebase Functions setup and Blaze deployment requirement](https://firebase.google.com/docs/functions/get-started), [Node runtime configuration](https://firebase.google.com/docs/functions/manage-functions), and [Emulator Suite installation](https://firebase.google.com/docs/emulator-suite/install_and_configure).
- [Maps credential restrictions](https://developers.google.com/maps/api-security-best-practices). Recheck SDK, runtime and pricing details when implementing; they change independently of this role plan.

## You own
- **Module 4** Commute planner and true monthly cost (FR4.1, FR4.2, FR4.3)
- The architecture, Cloud Functions structure, Google Maps and Routes integration
- Repo, branching rules, CI, the Firebase emulator, and the integration freeze
- The **match score** Cloud Function for Module 3

## Foundation batch status — 27 October 2026

Branch: `feature/aaron-foundations`. See the [implementation plan and evidence](../docs/superpowers/plans/2026-10-27-aaron-foundations.md).

| Batch | Implemented locally | Verification / remaining boundary |
|---|---|---|
| 1: Functions and emulators | Node 22/Jest skeleton, emulator-only verified-auth diagnostic, demo Auth/Firestore/Functions/Storage configuration, deny-all baseline rules | 9 Jest tests pass; real emulator smoke verifies auth rejection, UID integrity, Function-to-Firestore write and denied client reads. Gathan's application rules remain pending. |
| 2: Android foundation | Java Views developer shell; `ui`, `data`, `util`; pinned Gradle wrapper; debug-only Firebase setup and Android 17 local-network permission | Initial foundation tests passed; live setup now adds Email/Password flow and `liveDebug`. Registered application ID confirmed as `jiran.com`; Java namespace stays `com.jiran.app`. Ary's final product UI remains pending. |
| 3: CI and handoff | GitHub Actions, PR template, setup/architecture documentation, draft API contracts and AI-use record | Local commands pass. Workflow has not run on GitHub; remote branch protection/reviews/merge and Trello/QA evidence remain pending. Draft contracts require team agreement. |

The first **local** readiness milestone is verified on the API 37 AVD. The existing
live project **jiran-mad** now has Email/Password Auth enabled, its existing Android
registration connected, and Standard Firestore `(default)` in Singapore
(`asia-southeast1`, approved by the user). Auth settings and tested prototype
Firestore rules were deployed explicitly; Functions and Storage were not deployed.
See [live setup instructions](../README.md#use-the-existing-live-firebase-backend)
and the [live setup plan](../docs/superpowers/plans/2026-10-27-firebase-live-setup.md).
Team invitations, billing for later paid features, restricted Maps credentials, transit coverage,
production UM verification, schema, commute/match formulas and full NFR tests are
not complete. The foundation branch was pushed and merged through PR #1;
continuation commits remain on the feature branch until reviewed by PR.

## Right now: Step 0 (26 Oct to 8 Nov)
### Module 4 continuation — calculation batch

The foundation PR #1 is merged on `main` (`6741b41`). Current continuation:
`feature/aaron-commute-core`; see the [calculation plan](../docs/superpowers/plans/2026-10-27-commute-core.md).

- [x] Pure commute calculation with explicit units/trips and cheapest-before-class selection.
- [x] Missing-route/fare and no-on-time states; deposit exclusion; optional explicitly sourced CO₂.
- [x] Verified-auth emulator-only `getCommuteOptions` fixture interface.
- [x] Typed Android response parsing and local developer preview; loading/error/retry states.
- [x] Real Android commute SDK integration verification: all three fixture scenarios pass through Firebase SDK → callable → typed parser.
- [ ] Real Maps routing/coverage and verified fares/emission factors; production schema/timetable integration.
- [x] Official Maps pricing/SKU, transit request limits and caching/display research recorded in [Maps readiness](../docs/maps-readiness.md); live quota/budget setup and coverage probes remain open.
- [x] Server Routes provider adapter: strict request validation, bounded deadline/body, MYR fare parsing, warnings/fallback and sanitized errors; transport contract tests only. See [adapter plan](../docs/superpowers/plans/2026-10-27-routes-adapter.md). Live activation and production calculation integration remain open.
- [ ] Ary's final Room card/details integration and NFR1 load evidence.

Sample fixture values are not verified assumptions or production estimates. The
UI says so, and the endpoint is not exported in a live environment. This advances
the FR4.1–4.3 calculation foundation; the complete production module stays open.

### Remaining Step 0 checklist
- [x] Create the Android Studio project (Java, Gradle) in the repo and the package structure (ui, data, util), with a Firebase connection (existing `jiran-mad`, package `jiran.com`; debug remains local)
- [ ] Branching rules: feature branches, PR required, Ary approves and merges. Turn on branch protection
- [ ] GitHub Actions CI: `./gradlew lint test` on every PR
- [ ] Finish service setup and team invitations in existing `jiran-mad` (Auth Email/Password and Singapore Standard Firestore are ready; Storage, Functions, Messaging and membership still need review)
- [x] Cloud Functions skeleton (Node.js) and Firebase emulator setup (local `demo-jiran`, real Android integration verified; production features pending)
- [ ] Google Maps Platform key: restrict it, set a billing alert, never commit it
- [ ] **Check early (PRD open question 7):** does Google's transit data cover the bus, LRT and UM shuttle routes from Pantai Dalam and Kerinchi to UM? If the shuttle is missing, we seed shuttle stops and timetables from UM transport.

### Module 3 continuation — deterministic scoring batch

See the [match backend plan](../docs/superpowers/plans/2026-10-27-match-core.md)
and [contract](../docs/api-contracts.md). Production formula approval is still open.

- [x] Pure deterministic seven-factor match scoring with explicit versioned weights and transparent basis-point breakdown.
- [x] Missing-answer state, stable ranking, self exclusion, bounded candidate count and rule reason codes.
- [x] Authenticated emulator-only `computeMatchScores` with synthetic ranked, incomplete and empty scenarios.
- [ ] Survey-derived weight approval and agreed questionnaire mapping with Aly/Ary.
- [ ] Production authorized candidate reader/privacy projection with Gathan; final Match UI and explanation handoff with Ary/Ilham.
- [ ] Production end-to-end and QA evidence; no matching completion claim from fixtures alone.

**Cloud Functions are Node.js (decided).** Firebase does not support Java, so you two write the functions in JavaScript: keep each one small and single-purpose, no big framework, and unit-test them with Jest. The rest of the app stays Java.

## Next
**Step 1 (by 15 Nov):** write `docs/api-contracts.md` with the request and response JSON of each Cloud Function, so everyone can build against stubs:
- `getCommuteOptions(listingId or unit, departTime)`
- `getTransitRoute(...)`
- `computeMatchScores(userId)`
- `splitRoomPrices(totalRent, rooms)`
- AI functions (agree the shape with Ilham)

**Wave A (16 to 29 Nov): Module 4 basic**
- Walking, bus or LRT plus UM shuttle, e-hailing estimate (fare formula), and driving alone
- Travel time and monthly cost from a listing to the user's faculty; true monthly cost = rent plus utilities plus commute
- CO2 per mode = distance times a published emission factor (pick and cite the source, PRD open question 4)
- Meet NFR1 with provider-term-compliant request reuse/architecture. Persistent area/faculty Google route caching is not assumed permitted; see [Maps readiness evidence](../docs/maps-readiness.md). Actual p95 ≤3s at 100 concurrent users still needs end-to-end load testing.

**Wave B (30 Nov to 13 Dec)**
- Match score Cloud Function: transparent weighted formula, deterministic, unit-tested
- **P1:** peak and off-peak times (traffic-aware Routes API), peak surge assumption for e-hailing, step-by-step transit route

## Hand-offs
| You wait for | From | Use meanwhile |
|---|---|---|
| Listing and area fields | Gathan | Stub listings |
| Profile and timetable data | Ary and Gathan | Stub users |
| Survey data: peak windows, Grab fares | Aly (survey) | Config values to change later |

## Your parts in the graded deliverables
- **Video:** the commute card in the demo, segment 4 technical implementation (1:30, with Ilham 20 s on the AI flow)
- **Report:** section 3 Architecture and design (350 words), section 4 System implementation (400 words, you collect everyone's code segment)
- **You also lead** the P0 code freeze on 13 Dec and review all Cloud Functions and Maps PRs

## Numbers you find (see [ASSUMPTIONS.md](../ASSUMPTIONS.md))
- **#11 Maps cost and quota** and **#5 transit coverage** (due 8 Nov)
- **#2 Peak hours** with Aly (due 12 Nov)
- **#3 E-hailing fare formula**, **#4 car cost**, **#5 public transport fares**, **#6 CO₂ emission factors**, **#9 Match score weights** (due 13 Nov)

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../SURVEY.md).
