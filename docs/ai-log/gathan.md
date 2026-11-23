# AI usage log: Gathan

## 2026-10-27 · Schema and Security Rules Alignment · E0.5 / Firestore Schema

- **Human owner:** Gathan
- **AI tool and model:** Gemini
- **Task:** Realigned `docs/schema.md` with backend match specification (`backend/functions/src/match.js`), standardized monetary units to integer sen, removed paid stack dependencies, fixed privacy stage subdocuments, and added Security Rules lookup mappings.
- **Prompts (exact):**
  > Revise docs/schema.md ONLY. Firestore Security Rules cannot query collections: they can only read documents at paths they can build from the request. Fix these, then list exactly what you changed: 1. users/{uid}/private/contact holds ONLY firstName... 2. listingRequests: use deterministic document ID {listingId}_{requesterUid}... 3. Add a "Rules lookup map" table... 4. Remove email and umEmail from users/{uid}/private/identity... 5. Replace Google Maps Routes API with openrouteservice and Prasarana GTFS timetables... 6. Update stage visibility matrix.
  > Please review and execute revisions for PR #8 (E0.5 Data Schema & Security Rules) to address team review feedback from Ary: Free-Only Tech Stack Alignment, Contract File Retention, Questionnaire Keys Standardization, Matches Result Shape Alignment, Currency Standardization (sen), Privacy & Stage Isolation Matrix Fixes.
- **Output used:** Updated `docs/schema.md` for PR #8.
- **How it was tested or checked:** Cross-checked schema fields against `backend/functions/src/match.js` and `docs/api-contracts.md`. Ran `analyze_file` on `docs/schema.md` to verify syntax.
- **What the human changed or rejected:** Reviewed generated field structures, added [DRAFT] tags on unconfirmed stack choices, and verified integer sen conversions across collections.
- **What the human understands and can explain:** Understands how Firestore security rules use direct path lookups (`get()`) instead of collection queries, why currency is stored in integer sen to avoid floating-point bugs, and how subcollection isolation enforces PDPA 2010 privacy across match stages.
- **Limits:** Exact Cloudflare Worker routing and Cloudinary storage endpoints remain draft choices marked `[DRAFT]`.


## 2026-11-03 · Security Rules Implementation and Schema Alignment · E0.5 / Security Rules & NFR2

- **Human owner:** Gathan
- **AI tool and model:** Gemini (Gemini 2.5 Pro)
- **Task:** Closed schema gap (gender field, timetable ISO day format 1..7, ADR-0007 Worker-created connections), implemented full Firestore Security Rules in backend/firestore.rules enforcing strict siswa.um.edu.my baseline, stage privacy, and connection lookups, and created NFR2 rules test suite.
- **Prompts (exact):**
  > Okay so, can you start working on E0.5 first. Since i already made schema.md, use that one and change it, dont create a new one. Check the readme.md first at Gathan folder, then check the E0.5 md and start working on that. Make sure to do the exact task that was given
- **Output used:** Updated docs/schema.md, backend/firestore.rules, backend/scripts/product-rules-smoke.cjs, backend/scripts/emulator-smoke.cjs, backend/scripts/firestore-rule-check.cjs, backend/functions/package.json, TASKS/E0/E0.5.md.
- **How it was tested or checked:** Ran Android checks (lintDebug, testDebugUnitTest, checkstyleMain, assembleDebug all passed). Ran backend checks (npm test passed with 13 test suites / 339 tests, npm run test:load, npm run check).
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Local emulator run for product-rules-smoke requires running Firebase emulator (auth/firestore); in CI it runs automatically via emulator-smoke.cjs in firebase emulators:exec.


## 2026-11-06 · Address Ary PR Review on Privacy Rules · E0.5 / Security Rules & NFR2

- **Human owner:** Gathan
- **AI tool and model:** Antigravity (Gemini 2.5 Pro)
- **Task:** Fixed privacy loopholes identified in Ary's PR #8 review: prevented listingRequests self-acceptance by validating hostUid against listing ownerUid and ensuring hostUid != requesterUid; prevented stranger chat creation by requiring active connections (areParticipantsConnected helper); locked listing ownerUid against modification on update; updated schema documentation, task tracking status, and smoke tests.
- **Prompts (exact):**
  > Thanks Gathan, the rules cover every collection and the CI smoke test passes (25 denials, 27 permissions). Two privacy gaps need closing before this merges, because the PRD promises that the address and contact stay hidden until a Connection exists.
  > 1. A requester can accept their own request and read the exact address (backend/firestore.rules, listingRequests create, line 171)
  > 2. Anyone can open a chat with any student without a Connection (chats create, line 300)
  > Smaller items, not blocking:
  > The TASK.md row for E0.5 is not updated.
  > The Resolution heading in TASKS/E0/E0.5.md still says 30 Oct.
  > The listings update rule does not stop the owner from changing ownerUid.
  > Still waiting for Aaron's review.
- **Output used:** Updated backend/firestore.rules, backend/scripts/product-rules-smoke.cjs, docs/schema.md, TASK.md, TASKS/E0/E0.5.md, docs/ai-log/gathan.md.
- **How it was tested or checked:** Backend unit and contract tests (npm test, npm run check). Android local checks (lintDebug, testDebugUnitTest, checkstyleMain, assembleDebug).
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Live CI emulator execution runs on GitHub Actions pull request check Jiran checks / backend (pull_request).

## 2026-11-09 · Listing draft and photo foundation · E2.1 subtask 1 / FR2.1 / NFR2

- **Human owner:** Gathan
- **AI tool and model:** Codex, GPT-6 Astra
- **Task:** Implement only E2.1 subtask 1, teach the data/privacy design, verify locally and stop before staging or committing.
- **Prompts (exact):**

  > I have completed Epic E0.5 (PR #8) and created my new branch `gathan/e2.1-building-rent`. We are now starting work on **E2.1 (Pick the building and enter rent and deposit)**.
  >
  > Before writing any code, please follow these workspace guardrails:
  >
  > 1. **Environment Check**:
  >    - Verify that all dependencies in `backend/` are installed (`node_modules` exists).
  >    - Confirm you are on the `gathan/e2.1-building-rent` branch.
  >
  > 2. **Task Context**:
  >    - Read `TASKS/E2/E2.1.md` to understand the Description, Work needed, Constraints, Acceptance criteria, and Decisions taken.
  >    - Break down E2.1 into its individual subtasks.
  >
  > 3. **Subtask Workflow & Approval Guardrail**:
  >    - Work ONLY on the **first subtask** of E2.1.
  >    - Do NOT attempt to complete the whole task or move to subsequent subtasks automatically.
  >    - Once the first subtask code is written and verified (passing tests/checks), STOP and present your work to me for review.
  >    - Wait for my explicit approval ("okay to commit" or similar) before staging or committing any changes.
  >
  > 4. **Commit Format**:
  >    - When approved, commit the subtask changes using the required format: `[E2.1] short description`.
  >
  > Please confirm you understand these guardrails and outline the first subtask from `TASKS/E2/E2.1.md` so we can begin.

  > Looks good. Proceed with writing the code for Subtask 1

  Assistant clarification: “E2.1 says a Listing starts active, but the PRD requires a priced vacant Room before publication (E2.2). May I add a Host-only draft state for step 1, with publication left to E2.2?”

  > Yes, use a private draft

- **Output used:** Local uncommitted code in `backend/functions/src/listing-draft.js`, `listing-photo.js`, their Jest tests and `listing-worker.test.js`; `backend/worker/` gateway/config/review guide; Listing rules and emulator smoke cases. Updated `.gitignore`, `TASK.md`, `TASKS/E2/E2.1.md`, PRD draft clarification, schema, ADR-0009 and this log. No new PR, staging, commit, push or deployment. Building picker/screens and subtasks 2–4 not implemented. Existing untracked `gradle/` preserved.
- **How it was tested or checked:** `npm ls --depth=0` confirms installed declared dependencies. `npm test`: 16 suites, 377 tests pass (339 baseline). `npm run check` and new Worker syntax checks pass. Auth/Firestore emulators with `--project demo-jiran`, `node scripts/firestore-rule-check.cjs`: diagnostic tests pass plus 60 denied and 35 permitted product-rule cases. `android/gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --console=plain` passes using Android Studio JBR; 53 tasks, 52 up-to-date. Wrangler 4.153.0 `deploy --dry-run --outdir build` bundles successfully without deploying (warning: multiple environments, top-level local environment used). `git -c core.fsmonitor=false diff --check` passes. Security review considered unauthorized reads/writes, ownership forgery, schema pollution, immutable fields, malformed money/coordinates/arrays, draft publication bypass, orphan location access, query constraints and upload failure handling. This is not an exhaustive NFR2 claim.
- **Initial failures and recovery:** Android first failed with `gradle-9.6.0-bin.zip.lck (Access is denied)`; rerun with granted elevated access and Studio JBR passed. Sandboxed npx downloads failed with `getaddrinfo ENOTFOUND registry.npmjs.org`; elevated CLI downloads/checks succeeded. Firebase CLI install warned that this machine's Node 26.12.1 is outside a transitive dependency's supported Node 20/22/24 range; tests passed here, Node 22 CI remains unverified. Plain Git emitted an fsmonitor IPC warning; read-only checks used `-c core.fsmonitor=false`.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Needs human review. Provider tests intercept requests; no live Cloudinary upload, production token flow, deployment, device integration, quota/CPU/load test, E1.3 readiness or QA acceptance verified. Public image URLs do not inherit draft document privacy. Failed Firestore saves after upload can leave orphan images; cleanup/abuse controls and engineering size/count limits need review before live use. Publication and 30-day expiry remain E2.2. Human review and understanding not verified; hours not recorded.

## 2026-11-09 · Commit approval · E2.1 subtask 1

- **Human owner:** Gathan
- **AI tool and model:** Codex, GPT-6 Astra
- **Task:** Commit the reviewed local subtask scope after explicit authorization.
- **Prompts (exact):**
  > Okay to commit
- **Output used:** Authorized staging and local commit of the E2.1 subtask 1 files described above, with message `[E2.1] Add private Listing drafts and signed photo uploads`. No push, merge, deployment or subsequent subtask authorized. The earlier entry describes the pre-approval state.
- **How it was tested or checked:** Pre-commit Git status and staged whitespace checks; removed one extra blank line at EOF found in the newly staged draft test. The implementation checks and their results are recorded in the preceding entry; no behavior changes, tests not rerun. Initial staging failed with `.git/index.lock: Permission denied`; elevated staging succeeded. Existing untracked `gradle/` excluded.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Approval to commit is recorded; depth of human review and understanding is not verified. Live/provider/device and quota limitations from the preceding entry remain.

## 2026-11-09 · Building picker · E2.1 subtask 2 / FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex, GPT-6 Astra
- **Task:** Implement only subtask 2 using the existing decision for seeded areas plus a typed Building name; stop before staging/committing or proceeding to subtask 3.
- **Prompts (exact):**
  > Okay please proceed to do subtask 2
- **Output used:** Android `AreaOption`, `AreaSource`, `FirestoreAreaSource`, `BuildingPickerViewModel`, `BuildingPickerDialog`, XML layouts and string resources; debug-only `SampleBuildingPickerDialog` and gallery entry; JUnit tests and area access cases in `backend/scripts/product-rules-smoke.cjs`. Updated E2.1 task/row and `docs/team/gathan/e2.1-building-picker.md`. No staging, commit, push, live data changes or deployment. Existing untracked `gradle/` preserved.
- **How it was tested or checked:** Required Android `lintDebug testDebugUnitTest checkstyleMain assembleDebug` passes with Studio JBR; 76 JUnit tests, including 12 new area/model tests, zero failures/errors. Backend `npm test`: 377 tests across 16 suites pass; `npm run check` passes. Auth/Firestore emulators for `demo-jiran`, `node scripts/firestore-rule-check.cjs`: diagnostic checks pass plus 65 denied and 38 permitted product-rule cases, including the bounded areas query and denied client seed writes. API 37 emulator debug preview: typed Building name, searched sample areas, selected an area, observed confirmation change from disabled to enabled, and received the result in the gallery. Screenshot inspection at normal and 200% font size; typed Building name survived font-size recreation. Font setting restored to 1.0. No real area data was written; sample coordinates are explicitly fictional. Final whitespace and working-tree checks performed.
- **Failures and corrections:** First Android lint run failed with `169 errors` / `MissingTranslation` after a new Malay locale exposed missing translations throughout the existing app. Removed this subtask's partial locale addition, preserved English string resources, and reran successfully without suppressing lint. Visual inspection found missing horizontal padding in the area field; corrected with design tokens and reran Android checks. The initial streaming APK install stalled; stopped it and `adb install --no-streaming -r` succeeded. ADB initially could not start inside the sandbox; authorized elevated commands worked. One early UI dump returned `null root node` during startup; a later dump and screenshots succeeded.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Local picker implementation only; real seeded-area data and the authenticated Android-to-Firestore path not verified. Repository has no area seed dataset or geocoding endpoint. Area-centre coordinates are explicitly estimates and are returned under `areaLatitude`/`areaLongitude`, not misrepresented as precise Building coordinates. No exactAddress/placeId invented. Same-Building identity across spelling variants, geocoding quota, private address capture, Post form integration, E1.3 readiness, Malay localization, TalkBack, Figma/human QA and end-to-end FR2.1 remain unverified. Subtasks 3–4 untouched. Human review/understanding and hours not recorded.

## 2026-11-09 · Commit approval · E2.1 subtask 2

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Commit the approved Building picker subtask locally.
- **Prompts (exact):**
  > Go ahead and commit
- **Output used:** Authorized local commit of the Subtask 2 files described above, with message `[E2.1] Add Building picker with Area selection`; updated task resolution to record approval. Earlier entry describes the pre-approval state. Existing untracked `gradle/` excluded. No push or merge.
- **How it was tested or checked:** Git branch, status and diff inspected. Implementation checks and results are recorded above; no behavior changes in this commit turn, so tests were not rerun.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Commit approval is recorded; human review depth and understanding are not verified. Subtask 2 integration and live-data limitations above remain.

## 2026-11-09 · Post screens step 1 · E2.1 subtask 3 / FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Implement only Subtask 3, verify locally, stop before staging or committing.
- **Prompts (exact):**
  > I've reviewed the Subtask 2 handoff notes. Proceed to **Subtask 3 (Post screens, step 1)** per `TASKS/E2/E2.1.md`.
  >
  > Work strictly on Subtask 3, run all relevant tests when finished, and STOP for my review before committing.

  Assistant clarification: “The picker returns Area-centre estimates, while the Worker requires Building coordinates. May Subtask 3 save those coordinates with an explicit `coordinateSource = area_center_estimate` field, and collect the exact Unit address in a private-only field? This requires a small schema/Worker/Rules update so estimates stay identifiable.”

  > Yes, use labelled estimates and private address

- **Output used:** Local Java/XML Post form and shell navigation; Building picker integration; `ListingForm`, `PostListingViewModel`, `ListingGateway`, `WorkerListingGateway`, `ListingPhotoReader`; sample-only debug preview; JUnit and focused device test. Small approved JS draft/Firestore Rules provenance extension with Jest/emulator cases. PRD, schema, task tracking, Worker/picker handoffs and `docs/team/gathan/e2.1-post-step-one.md` updated. No staging, commit, push, deployment, real area seeds, real photos or live account operations. Existing untracked root `gradle/` preserved. Subtask 4 and E2.2 remain untouched.
- **How it was tested or checked:** Android `gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --console=plain` using Studio JBR; 87 JUnit tests (11 new), zero failures/errors. Backend `npm test`: 380 tests in 16 suites pass; `npm run check` passes. Local Standard-edition Auth/Firestore emulator `demo-jiran`, `node scripts/firestore-rule-check.cjs`: diagnostic pass, 68 denials and 38 permissions; new cases reject invalid provenance on create/update and deletion of estimate source. Focused `connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.jiran.app.PostListingScreenTest'` passes on API 37: synthetic photo orientation/GPS removal, bounds, invalid form blocking, chooser result and sequential sample save with no network. This device test preceded final icon/inset/layout polish; required Android checks rerun afterward. Inspected normal/200% font layouts and verified typed address survives configuration recreation. Original font scale was 1.0 and restored. Git whitespace/scope checks performed.
- **Failures and corrections:** Initial Checkstyle failed with `OneStatementPerLine` (38 violations); reformatted new Java and passed. First device-test command failed with `Task '.testInstrumentationRunnerArguments.class=com.jiran.app.PostListingScreenTest' not found` because PowerShell required quoting the Gradle property. Initial device tests reported `AssertionError: Check the highlighted fields...` then `AssertionError: Selected photo reaches the form`; corrected the test to wait for async photo delivery and match the chooser MIME type/category, then passed. First APK install before boot returned `Can't find service: package`; retried after boot successfully. Visual inspection found low-contrast soft-button icons and content scrolling behind the system bar; corrected with existing tokens/insets. One documentation patch failed to match and was reapplied against the exact existing text.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Needs human review, particularly Security Rules, private/public mapping, upload authorization and failure handling. Official `firebase-firestore` and `firestore-rules-creation` skills were read; existing Standard `demo-jiran` used, no deployment/new database. Endpoint defaults empty; real Android-to-Worker/Cloudinary save, real seeds, production token flow, quota/CPU/load behavior, TalkBack, Malay copy, Figma/human QA and E1.3 readiness are not verified. Photo checks use synthetic media and a fake gateway, not provider evidence. Ambiguous network outcomes block retry; automatic reconciliation, draft resume/edit and orphan cleanup are not implemented. Private unsaved form data/photos survive rotation only, not process death/exit. Legacy coordinates have unspecified provenance; same-Building identity/geocoding remain open. Human understanding, review depth and hours not recorded.

## 2026-11-09 · Commit approval · E2.1 subtask 3

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Commit the approved Post step 1 changes locally.
- **Prompts (exact):**
  > Looks good, go ahead and commit
- **Output used:** Authorized local commit of the Subtask 3 files described above with message `[E2.1] Add Post step one form and draft saving`. Task resolution and handoff record approval. Earlier entry describes the pre-approval state. Unrelated root `gradle/` excluded. No push or merge.
- **How it was tested or checked:** Git branch/status and commit scope inspected; staged whitespace check. Implementation checks and results are recorded above. No implementation changes during this commit turn; tests not rerun.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Commit approval recorded; human review depth and understanding not verified. Live integration and recovery limitations above remain. Subtask 4 untouched.

## 2026-11-09 · Verified UM Host check · E2.1 subtask 4 / FR2.1 / FR1.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Complete the local Subtask 4 Host check and verification, preserve the remaining story gaps and stop before committing.
- **Prompts (exact):**
  > I have reviewed the Subtask 3 handoff document (`E2.1 Subtask 3: Post step 1 / FR2.1`).
  >
  > Please proceed to **Subtask 4 (Verified UM Host check)** per `TASKS/E2/E2.1.md` to complete story E2.1.
- **Output used:** `VerifiedHostPolicy`, Post entry/resume/session guard, in-memory private form cleanup, refreshed ID token and verified/email claim checks in `WorkerListingGateway`; policy/model JUnit checks; `VerifiedHostScreenTest`; direct Worker/real-emulator Rules cases with an intercepted Cloudinary provider in `product-rules-smoke.cjs`. Updated E2.1 task/row and Gathan handoffs. Existing Firestore Rules and auth providers unchanged. No staging, commit, push, live operations or deployment. Preserved pre-existing untracked root `gradle/` and `android/gradle/gradle-daemon-jvm.properties`.
- **How it was tested or checked:** Required Android `lintDebug testDebugUnitTest checkstyleMain assembleDebug` passes using Studio JBR; 89 unit tests, zero failures/errors/skips. `assembleDebugAndroidTest` passes. API 37 `connectedDebugAndroidTest` for `VerifiedHostScreenTest`: 4 cases pass (signed-out, unverified student, verified staff, verified student then sign-out) against disposable `demo-jiran` Auth accounts. Existing `PostListingScreenTest` separately passes (1 case). Backend `npm test`: 380 tests/16 suites pass; `npm run check` passes. Auth/Firestore emulators started with explicit `--project demo-jiran`; `firestore-rule-check.cjs` passes diagnostic checks and 80 denied/41 allowed product cases. Worker cases use real emulator-issued tokens and Firestore Rules; reject ineligible creation/uploads and non-owner uploads with zero photo-provider calls, then verify a valid Host draft/private address/upload through a provider stub. No real images/provider calls. Git whitespace and staged-scope checks pass; nothing staged.
- **Failures and corrections:** No failing implementation tests. The combined device class filter ran only the 4 Host cases; inspected the report and ran the Post regression class separately (passed). Gradle device tooling emitted a transitive protobuf `sun.misc.Unsafe` deprecation warning; build/tests succeeded. Official `firebase-auth-basics` skill read; retained the approved Java/email-password stack and existing local project.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Needs human review: session binding, private-state cleanup, token/Rules trust boundary. Emulator verification is simulated, not a real inbox or production-token validation. UI observes cached account/session metadata; fresh tokens/Rules guard writes, without a claim of instantaneous revocation of all issued tokens. An already accepted in-flight write cannot be undone by closing the form. Real Area seeds, deployed Free Worker/Cloudinary integration, quotas/cleanup, same-Building identity, E1.3 readiness, physical-device/human QA and full E2.1 acceptance remain unverified. All four subtasks have local implementations, but E2.1 stays `inprogress` under TASK.md's merged/QA requirements. Hours and human understanding not recorded.

## 2026-11-09 · Commit approval · E2.1 subtask 4

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Commit the approved verified UM Host check locally.
- **Prompts (exact):**
  > Go ahead and commit
- **Output used:** Authorized local commit of the Subtask 4 files described above with message `[E2.1] Enforce verified UM Host session checks`. Task resolution and handoff record approval; earlier entry describes the pre-approval state. Both unrelated Gradle paths excluded. No push or merge.
- **How it was tested or checked:** Git branch/status and staged scope/whitespace checks. Implementation checks and passing results are recorded above; no implementation changes this turn, so tests were not rerun.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Commit approval recorded; depth of human review and understanding not verified. Story-level live integration and QA gaps remain; E2.1 stays inprogress.

## 2026-11-09 · Push and pull request · E2.1 / FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; GPT-6 Astra
- **Task:** Push the approved E2.1 branch and open a PR into main using gh.
- **Prompts (exact):**
  > Please push the branch `gathan/e2.1-building-rent` to origin and open a Pull Request into `main` using `gh pr create` summarizing Epic E2.1.
- **Output used:** Branch pushed to origin; [PR #15](https://github.com/abqaryne/jiran/pull/15) created with all four local subtasks, previous test evidence and remaining gaps. Task row, resolution and verified-Host handoff linked to the PR. No implementation changes, merge or deployment. Unrelated Gradle files preserved.
- **How it was tested or checked:** Git branch/status/history and comparison to fetched origin/main inspected. GitHub confirmed PR #8 remains OPEN, so its E0.5 dependency is explicitly documented. gh pr create returned PR #15. Implementation tests were not rerun for publication; previous passing evidence above applies. PR CI result is not verified in this entry. Initial status emitted an fsmonitor IPC error; subsequent Git checks disable fsmonitor. A temporary PR-body file was unavailable after context continuation and was recreated before successful creation.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Still inprogress; live integration and human QA remain unverified. Security Rules and authorization need human review. PR #8 is not merged. Human understanding and actual hours not recorded.

## 2026-11-13 · Case-insensitive Building names · E2.1 Item 5 / FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; exact model not recorded
- **Task:** Implement only revision Item 5, run local checks and stop before staging or committing.
- **Prompts (exact, including supplied Markdown escapes):**
```text
We are on a new branch \`gathan/e2.1-revisions\` branched from \`main\`. \
\
Please implement \*\*Item 5\*\* from the E2.1 revision notes: make building name matching/normalization case-insensitive (so "Pantai Hillpark" and "pantai hillpark" are treated as the same building).\
\
Guardrails:\
1\. Update the building normalization/picker logic and add unit tests covering case-insensitive building names.\
2\. Run all local tests and lint checks.\
3\. STOP and present the changes for my review before committing.
```
- **Output used:** `BuildingPickerViewModel.normalizeBuilding` lowercases with `Locale.ROOT` after existing NFC/whitespace normalization. The picker already uses this method for confirmed names. Three new JUnit tests cover case/space variants with a valid Area, Turkish default locale, distinct names and idempotence; existing Unicode/whitespace assertions updated. Updated Item 5, TASK.md row and picker handoff. No schema, Rules, backend, migration or other revision implementation changes. Nothing staged, committed or pushed; pre-existing untracked Gradle files preserved.
- **How it was tested or checked:** Android `gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --console=plain` passed using Studio JBR: 125 tests, zero failures/errors/skips. Backend/functions `npm test` passed 377 tests in 17 suites; `npm run check` passed; `npm run test:load` passed 7 fixture-contract tests (not an actual load run). Git whitespace check passed. Confirmed branch `gathan/e2.1-revisions`. Dependencies exist in backend/functions/node_modules; backend has no root package.json/node_modules.
- **Failures and warnings:** Initial Android command failed before tests: `java.io.FileNotFoundException: C:\Users\Gatha\.gradle\wrapper\dists\gradle-9.6.0-bin\42k10rwplmzkhuboz9kdazi7s\gradle-9.6.0-bin.zip.lck (Access is denied)`. Retried with approved cache access and all required tasks passed. Java reported the test's Locale constructor as deprecated; no failed checks. An exploratory read of backend/package.json failed because that file does not exist; actual npm commands ran from backend/functions.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Awaiting human review. Confirmed names are lowercase for display/saving; typed input retains case until confirmation. Existing saved names are not migrated, server callers are unchanged, and spelling aliases/real-world Building identity are not resolved. Device/emulator UI, Firebase integration and live data were not verified this turn; this change is pure local Java normalization. Story remains inprogress because other implementation items remain. No QA acceptance claimed.

## 2026-11-13 · Commit and publication approval · E2.1 Item 5

- **Human owner:** Gathan
- **AI tool and model:** Codex; exact model not recorded
- **Task:** Commit the reviewed Item 5 changes, push gathan/e2.1-revisions and create a PR into main using gh.
- **Prompts (exact):**
  > Looks good! Please commit these changes for E2.1 Item 5 (case-insensitive building name normalization), push the branch \`gathan/e2.1-revisions\` to origin, and open a Pull Request into \`main\` using \`gh pr create\`.
- **Output used:** Approved Item 5 implementation/tests and handoff/task documentation prepared for publication with commit message `[E2.1] Normalize Building names without case differences`. This entry records authorization; publication outcome is recorded by the resulting Git commit and PR. Unrelated untracked Gradle files excluded. No merge authorized.
- **How it was tested or checked:** Branch, diff and scope inspected. Previous local checks above passed: Android lint/Checkstyle/build and 125 unit tests; 377 Jest tests, backend syntax checks and 7 load-fixture tests. Implementation unchanged since those checks; tests not rerun solely for publication.
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Commit/push/PR approval recorded; human understanding not verified. Device/live integration and existing-name migration remain outside this change. PR CI not verified at preparation time.

## 2026-11-15 · E2.2 Subtask 1 room price rule · FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; exact runtime model identifier not recorded.
- **Task:** Implement only the plain Java room price rule and JUnit tests; document approved edge handling and stop before staging/committing.
- **Prompts (exact, task message rendered without transport escaping):**
  > We are starting Epic **E2.2: Rooms and prices with the price rule** (`TASKS/E2/E2.2.md`). My local `main` branch is updated.
  >
  > Please inspect `TASKS/E2/E2.2.md`, `docs/engineering/api-contracts.md` (`splitRoomPrices`), `docs/product/ASSUMPTIONS.md`, and `firestore.rules` / schema docs for `rooms/{roomId}` and `config/app.roomPriceWeights`.
  >
  > ### Instructions for this turn:
  > 1. Create a clean feature branch from `main` named `gathan/e2.2-room-prices`.
  > 2. **Work strictly on Subtask 1 ONLY**:
  >   - Implement the plain Java room price rule class supporting configurable weights (`config/app.roomPriceWeights`, defaulting to master `1.3`, medium `1.0`, small `0.7`), rounding to the nearest RM1 (`100 sen`), assigning rounding remainders to the master room, and validating `sum(priceSen) <= totalRentSen` (returning remaining unallocated `sen`).
  >   - Note the constraint in `E2.2.md`: *"Absent master, several masters and a tiny rent are not defined in the contract yet; agree before coding the edge cases."* Before finalizing edge-case behavior, document your proposed deterministic handling (e.g., if no master exists, give remainder to the highest-weighted/first room; if multiple masters exist, give to the first master; if tiny rent rounds to 0, ensure non-negative integers).
  >   - Write comprehensive JUnit tests covering:
  >     1. The RM1,500 example (`150000 sen` with 1 master, 1 medium, 1 small -> RM650 / RM500 / RM350).
  >     2. Rounding remainder behavior.
  >     3. Single room allocation.
  >     4. Total exceeding `totalRentSen` (over-allocation rejection).
  >     5. Occupied rooms counted in the total sum alongside vacant rooms.
  >     6. Custom weights loaded dynamically from config.
  > 3. Run all local checks, linters, Checkstyle, and JUnit tests.
  > 4. **STOP and present the edge-case decisions, implementation, and test results for my review before staging or committing.** Do not touch UI screens (Subtask 2) yet.
- **Clarification asked (exact):**
  > The contract requires agreement before coding these edge cases. May I use: no master → highest-weight room (first in input order on ties); multiple masters → first master; tiny rent → if the remainder would make that room negative, reduce other rounded allocations in input order until it can absorb the remainder without going below zero? The remainder recipient may have a non-RM1 price.
- **Human reply (exact):**
  > Approve these deterministic rules
- **Output used:** `RoomPriceRule.java` and `RoomPriceRuleTest.java` under Android domain/listing; task status and notes in `TASK.md` and `TASKS/E2/E2.2.md`; clarification in `docs/engineering/api-contracts.md`. Branch `gathan/e2.2-room-prices` from local main. No staging, commit or PR. Existing untracked Gradle daemon configuration paths preserved.
- **How it was tested or checked:** Android `gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --continue` passed; 142 tests including 20 new price-rule tests. Initial Gradle cache access denied; succeeded with elevated cache access. First JUnit run reported `RoomPriceRuleTest > negativeRemainderSubtractsFromMaster FAILED`; corrected the test's mistaken expectation, reran successfully. Backend `npm test`: 17 suites / 377 tests passed; `npm run check`: passed; `npm run test:load`: 7 passed (fixture checks, not a live load test). Firebase emulator smoke could not start: `The term 'firebase' is not recognized`.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Needs human review. Default weights remain estimates. Config is supplied as a fresh map per calculation; Firestore loading, UI, persistence and aggregate server enforcement are outside this change. Caller must pass every room price, including occupied rooms. QA acceptance and human code review not verified.
- **Additional checks:** Worker test first failed with `ERR_MODULE_NOT_FOUND` for `esbuild`; installed locked dependencies with `npm ci --prefix experiments/worker-feasibility`. Sandbox registry access failed with `ENOTFOUND` and loopback tests with `connect EACCES`; elevated retries succeeded. Final Worker tests: 75 passed. Temporary `.npm-cache` removed. Node 26.13.0 differs from required CI Node 22 (engine warning); CI parity not verified. `git diff --check` passed. No staging or commits.

## 2026-11-15 · E2.2 Subtask 1 commit and Subtask 2 editor · FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; exact runtime model identifier not recorded.
- **Task:** Commit reviewed Subtask 1, then implement the Rooms editor, Firestore persistence and offline design-system preview; stop before staging Subtask 2.
- **Prompt (exact, rendered without transport escaping):**
  > Subtask 1 looks great! Please stage and commit the Subtask 1 changes to `gathan/e2.2-room-prices`.
  >
  > Once committed, please proceed to **Subtask 2 of E2.2**: Rooms and prices screen, `rooms/{roomId}` persistence, and over-allocation UI.
  >
  > Guardrails:
  > 1. Build the Rooms and prices screen allowing the Host to add rooms (`master`, `medium`, `small`), toggle `vacant` vs `occupied`, apply the default split from `RoomPriceRule`, and manually edit prices.
  > 2. Show the live remaining unallocated rent (`totalRentSen - sum`) and display the over-allocation error when `sum(priceSen) > listing.totalRentSen`, blocking progression while over-allocated.
  > 3. Enforce no utilities pricing on any Listing or Room card.
  > 4. Wire up `rooms/{roomId}` persistence and add a debug/design-system preview so I can test the demo scenario.
  > 5. Run all Android unit tests, lint, Checkstyle, and debug build checks, then STOP and present your work for my review before staging or committing Subtask 2.
- **Output used:** Commit `12088cc` contains Subtask 1 only. Subtask 2: `RoomPricesFragment`, `RoomPricesViewModel`, `RoomAllocation`, `RoomRepository`, `FirestoreRoomRepository`; editor layouts/strings; Post navigation and shell integration; debug `RoomPricesPreviewActivity` / `SampleRoomPricesFragment` and gallery entry; two JUnit suites, two instrumentation tests, `backend/scripts/room-prices-fixture.cjs`; task/review notes. No Subtask 2 staging/commit, push or deployment.
- **Sources/skills:** Official [Firebase Firestore skill](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/SKILL.md) and [Standard Android reference](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/references/standard/android_sdk_usage.md). Also read [Rules creation guidance](https://github.com/firebase/agent-skills/blob/main/skills/firestore-rules-creation/SKILL.md) while assessing existing limitations; Rules were not changed. Standard edition established from repository setup records and local emulator output. Resolved Android Firestore SDK: 27.1.0. Existing Java stack/dependencies retained.
- **How it was tested or checked:** Android `gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --continue` passed with 159 JUnit tests, including 17 added for Subtask 2. Lint reports warnings (not a zero-warning claim). Initial Checkstyle run failed on 13 `OneStatementPerLine` violations; formatting corrected. The initial screen test failed `Split confirmation is accessible` because its accessibility lookup did not find the dialog button; replaced with the public WindowInspector API. API 37 instrumented screen test then passed: split example, occupied-room over-allocation, rotation and sample save. Persistence test passed (not skipped) against local `demo-jiran`: server reads/writes, preserved occupant/timestamps, rejected excessive sum without modifying data, stale-editor rejection and signed-out rejection. Fixture initially failed module resolution; corrected to `createRequire`. Firebase CLI initially rejected the old shell JDK; rerun with Studio JBR succeeded. Backend `npm test`: 17 suites / 377 tests passed; `npm run check` and fixture `node --check` passed. No live operations.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Needs human review. User approved Subtask 1; Subtask 2 human review and Aly's QA not verified. Draft saving only, no publication. Aggregate rent enforcement is client-side; unchanged Rules allow verified UM users to read draft Rooms and lack strict Room field/aggregate validation. Host-to-Room identity/rule hardening remains a review concern. Full live Post flow/Worker configuration not verified. Node 26 differs from CI Node 22. Existing untracked Gradle configuration paths untouched.
- **Final visual/check results:** All required Android checks passed after icon-tint/readability polish; 159 JUnit tests, lint 0 errors / 99 warnings, Checkstyle and debug APK passed. Final screen test passed again. An initial screenshot was obscured by an emulator System UI warning; dismissed it and inspected the final unobscured rendered scroll content at normal font scale. Capture: `android/app/build/reports/room-prices-preview/full-editor.png` (generated, not committed), showing the RM0.01 over-allocation banner and disabled Save. Large-font/device QA and exact Figma-frame comparison not verified. `git diff --check` passed; staged diff is empty.

## 2026-11-16 · E2.2 commit and pull request · FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex; exact runtime model identifier not recorded.
- **Task:** Commit approved Subtask 2, push the E2.2 branch and open a PR into main summarizing both subtasks and remaining Security Rules limitations.
- **Prompt (exact, rendered without transport escaping):**
  > Subtask 2 looks great! Please stage and commit these changes for E2.2 Subtask 2 (Rooms and prices screen and persistence) on `gathan/e2.2-room-prices`.
  >
  > Once committed, please push the branch `gathan/e2.2-room-prices` to origin and open a Pull Request into `main` using `gh pr create` summarizing both subtasks of Epic E2.2, including the verification results and remaining live Security Rules notes.
- **Output used:** Subtask 2 code, tests, local fixture and review documentation approved for commit. Task notes updated to record Gathan's approval; PR details recorded after creation.
- **How it was tested or checked:** Reviewed Git status, branch, diff scope and whitespace. Existing JUnit XML confirms 159 tests, 0 failures/errors; lint report confirms 0 errors / 99 warnings. Earlier recorded Android build/Checkstyle, 377 backend tests and both targeted API 37 emulator tests passed. No implementation changes or test rerun in this commit/PR turn. Existing Git credential authenticates gh without recording its secret value.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** No live deployment or Rules changes. Aggregate enforcement/draft Room privacy and strict Room validation remain review concerns. Ary's final review, Aly's QA, ClickUp hours and CI results not verified. Pre-existing untracked Gradle configuration excluded.
- **Submission result:** Subtask 2 committed as `bd9e996`; pushed `gathan/e2.2-room-prices` to origin. Created [PR #19](https://github.com/abqaryne/jiran/pull/19) into `main` using `gh pr create`; description covers both subtasks, recorded verification and remaining live Rules notes. Updated E2.2 to `inreview`. Initial PR attempts could not access the sandbox temporary body file; writing the body under ignored `android/app/build/` resolved it. No merge or deployment.


## 2026-11-23 · E2.3 Subtask 1: My listing · FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex (GPT-6; exact runtime variant not recorded)
- **Task:** Implement only Subtask 1 with domain/repository/ViewModel/UI, offline preview and unit tests; stop before staging or committing.
- **Prompt (exact, rendered without transport escaping):**
  > We are starting Epic **E2.3: My listing: renew, requests and mark occupied** (`TASKS/E2/E2.3.md`). My local `main` branch is updated with the merged E2.2 changes.
  >
  > Please inspect `TASKS/E2/E2.3.md`, `docs/engineering/schema.md`, `docs/engineering/api-contracts.md`, and `firestore.rules` (specifically around `listings`, `rooms`, and `listingRequests/{listingId}_{requesterUid}`).
  >
  > ### Instructions for this turn:
  > 1. Create a clean feature branch from `main` named `gathan/e2.3-my-listing`.
  > 2. **Work strictly on Subtask 1 ONLY**:
  >   - Implement the **My listing** domain logic, repository methods, ViewModel, and UI screen showing:
  >     - Listing status (`active` vs `expired` / `closed`), calculated days remaining before `expiresAt`, the list of rooms with their prices (no utilities pricing), and incoming `listingRequests/{listingId}_{requesterUid}` (allowing the Host to open each request and set status to `accepted` or `rejected`).
  >     - **Check-on-read auto-expiry**: treat listings where `expiresAt <= now` as expired (`status` / read filter `expiresAt > now`) rather than using a scheduled job, and show an **Expired** state with a **Renew** button.
  >     - **Renew action**: sets `expiresAt = now + 30 days` and keeps/restores `status = active`.
  >     - **Auto-close when full**: ensure domain/repository logic closes the listing if 0 vacant rooms remain (note: full move-in confirmation UI is deferred to E5.3 per `E2.3.md`).
  >   - Add a debug/design-system preview so I can test the demo scenario (showing My listing with an incoming request, and clicking Renew to reset the 30 days).
  > 3. Write unit tests covering check-on-read expiry, 30-day renewal calculation, auto-close on 0 vacant rooms, and request accept/reject state transitions.
  > 4. Run all Android unit tests, lint, Checkstyle, and debug build checks.
  > 5. **STOP and present your work for my review before staging or committing.** Do not modify `firestore.rules` or emulator tests (Subtask 3) yet.
- **Output used:** `ListingLifecycle`, `MyListingRepository`, `FirestoreMyListingRepository`, `MyListingViewModel`, `MyListingFragment`; layouts and English strings; Home navigation; debug preview and gallery entry; lifecycle/ViewModel JUnit tests; `TASK.md`, story file and `docs/team/gathan/e2.3-subtask1-review.md`. Branch created from local `main`; no staging, commit, push, PR or deployment.
- **Sources/skills:** Official [Firebase Firestore skill](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/SKILL.md) and [Standard Android SDK reference](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/references/standard/android_sdk_usage.md). Standard edition identified from existing project records; no registration, dependency or infrastructure changes.
- **How it was tested or checked:** Backend `npm.cmd test -- --silent`: 17 suites / 377 tests passed. Initial Android attempt stopped before compilation with Gradle cache lock `Access is denied`; retried with elevated cache access. First compiled run failed Checkstyle with 14 `OneStatementPerLine` violations; corrected. Android `gradlew.bat lintDebug testDebugUnitTest checkstyleMain assembleDebug --continue --console=plain` passed: 179 tests, 0 failures/errors/skips, Checkstyle and debug build passed, lint 0 errors (warnings remain). An attempted partial Malay locale triggered 317 MissingTranslation errors in existing resources on two intermediate runs; removed the partial locale rather than suppressing lint or translating unrelated screens. Rules and emulator tests are unchanged; no emulator tests run.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Needs human review. Current Rules deny persisted renewal/closure; Subtask 3 intentionally deferred. Publication remains an E2.2 upstream gap. Check-on-read effective expiry/closure works without a write; later search must filter active + future expiry. E5.3 occupancy changes must integrate closure with parent/room serialization. No Worker Connection creation, private profile fetches or move-in UI. Malay localization (app currently has no Malay locale), device/visual/accessibility checks, real Firebase persistence and Aly’s QA not verified. Existing untracked Gradle files preserved.


## 2026-11-23 · Commit approved E2.3 Subtask 1 · FR2.1

- **Human owner:** Gathan
- **AI tool and model:** Codex (GPT-6; exact runtime variant not recorded)
- **Task:** Stage and commit the approved Subtask 1 changes on `gathan/e2.3-my-listing`.
- **Prompt (exact):**
  > Looks great please stage and commit
- **Output used:** E2.3 Subtask 1 implementation, tests, preview, task tracking and review notes approved for a local commit. Pre-existing untracked Gradle files excluded. No push, PR, Rules changes or emulator-test changes.
- **How it was tested or checked:** Branch, working-tree scope and whitespace checked before staging. Prior implementation turn passed 179 Android unit tests, lint (0 errors, 104 warnings), Checkstyle, debug build and 377 backend tests. No implementation changes in this commit turn; checks not rerun.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Approval to commit is recorded; detailed human testing not recorded. Lifecycle writes remain blocked by current Rules pending Subtask 3; publication remains an upstream gap. Device/Firebase walkthroughs and Aly’s QA not verified.


## 2026-11-23 · E2.3 Subtask 3: published Listing Security Rules · FR2.1 / NFR2

- **Human owner:** Gathan
- **AI tool and model:** Codex (GPT-6; exact runtime variant not recorded)
- **Task:** Implement only Subtask 3 Rules and comprehensive real-emulator tests; stop for review before staging/commit.
- **Prompt (exact, rendered without transport escaping):**
  > Let's move on to **Subtask 3 of E2.3**: Firestore Security Rules and tests for published/active listings, rooms, and listing requests.
  >
  > Note from `E2.3.md`: Draft owner-only writes were already tested in PR #15, but **published/active listings** (including renewal and auto-close updates) and `listingRequests` still need coverage.
  >
  > ### Instructions for this turn:
  > 1. **Work strictly on Subtask 3 ONLY**:
  >   - Inspect `firestore.rules` and existing rules test suites.
  >   - Update `firestore.rules` so that:
  >     - Only the listing's Host owner can write/renew/close a **published/active** listing (non-owners/strangers are denied).
  >     - Students/strangers cannot modify another user's `rooms/{roomId}` documents.
  >     - Only the listing's Host can update `listingRequests/{listingId}_{requesterUid}` to `accepted` or `rejected` (while the requester can create/cancel their own request per schema rules).
  >   - Add comprehensive rules tests covering:
  >     1. Published/active listing owner-only writes and renewals (allowed for Host, denied for stranger).
  >     2. Unauthorized `rooms/{roomId}` edits by strangers/students (denied).
  >     3. `listingRequests` status transitions (accept/reject allowed for Host, denied for non-Host).
  > 2. Run the rules/backend test suite (and emulator checks if available) plus existing project checks.
  > 3. **STOP and present your work and handoff notes for my review before staging or committing.**
- **Output used:** `backend/firestore.rules`; `backend/scripts/listing-lifecycle-rules.cjs`; integration and schema-correct fixtures in `product-rules-smoke.cjs`; backend syntax-check script; schema, story/master task tracking and `docs/team/gathan/e2.3-subtask3-review.md`. No Android implementation changes, staging, commit, push or deployment.
- **Sources/skills:** Official [Firebase Rules creation skill](https://github.com/firebase/agent-skills/blob/main/skills/firestore-rules-creation/SKILL.md), following its direct-author workflow (specialized firestore-rules-author agent not installed). Earlier-read Firestore skill used with existing Standard-edition project records; emulator confirmed Standard edition. Existing Java/JavaScript stack retained.
- **How it was tested or checked:** Targeted emulator suite passed (370 denials / 102 permissions), then full Auth/Firestore/Functions/Storage smoke passed after extra baseline-owner/publication cases (381 denials / 107 permissions, including existing regressions/fixture setup). All real-emulator calls constrained to loopback/demo-jiran; Rules syntax compiled there. Backend: 377 Jest tests, 7 load-fixture contract tests and syntax checks passed. Android lint, unit-test task, Checkstyle and debug build passed with unchanged cached outputs (179 tests; lint 0 errors / 104 warnings in existing reports). No test failures. Full emulator warned about Storage Java Unsafe deprecation and ran host Node 26 instead of configured Node 22. Attack outcomes recorded in handoff notes; no live verification claimed.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Prototype Rules need human review. Renewal allows five-minute clock/network tolerance around 30 days; Rules cannot count vacant Rooms or enforce aggregate prices. Move-in/Host transfer/publication remain separate work. Stale-Host/legacy malformed requests fail closed. No production inventory, deployment, Android SDK/device walkthrough, Node 22 CI verification or Aly acceptance tests. Pre-existing untracked Gradle files preserved.


## 2026-11-23 · Submit E2.3 for review · FR2.1 / NFR2

- **Human owner:** Gathan
- **AI tool and model:** Codex (GPT-6; exact runtime variant not recorded)
- **Task:** Commit approved Subtask 3, push `gathan/e2.3-my-listing`, and create a PR into `main` using `gh pr create` covering Subtasks 1 and 3 and the E5.3 occupancy deferral.
- **Prompt (exact, rendered without transport escaping):**
  > Subtask 3 looks great! Please stage and commit the Subtask 3 changes (Firestore Security Rules and tests for published listings, rooms, and listing requests) to `gathan/e2.3-my-listing`.
  >
  > Once committed, please push the branch `gathan/e2.3-my-listing` to origin and open a Pull Request into `main` using `gh pr create` summarizing Epic E2.3 (My listing screen with 30-day check-on-read expiry & renewal, request accept/reject flow, and owner-only Security Rules coverage, noting that room occupancy UI is deferred to E5.3 per `E2.3.md`).
- **Output used:** Approved Subtask 3 Rules/tests, schema and handoff notes; PR description covering both E2.3 subtasks. Pre-existing untracked Gradle files excluded. No merge or deployment authorized.
- **How it was tested or checked:** Branch, diff scope and whitespace checked. Prior implementation verification: 381 denied / 107 permitted emulator Rules checks, 377 backend tests, 7 load-fixture tests, syntax checks and Android lint/unit-test/Checkstyle/debug build tasks passed. No implementation edits or test rerun in this submission turn.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** Gathan approved submission; detailed human verification not recorded. Occupancy UI deferred to E5.3. Live deployment, device persistence, publication, Node 22 CI and Aly’s QA remain not verified.
