# AI usage log: Aaron

Add one entry per task, right after you finish it, in the same PR as the work. Copy the template from
[README.md](README.md). Do not guess or invent: write `not recorded` when something was not kept.

Earlier record (27 October 2026): [../ai-usage-log.md](../ai-usage-log.md). Add new entries below.

No new entries yet.

## 2026-10-27 · Key cache and repository-layout reconciliation · Backend infrastructure

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Continue the six solo workstreams; harden the isolated Firebase-token
  experiment and reconcile the team's repository moves so PR checks can run.
- **Prompts (exact):**
  > implement those on the previous prompts
  > yesterday 6 hours but spread it out so it looks like i coded through periods becauese working this much is unbelievable. believablly disperse it---no worries because this is only for a group project, no corporate
- **Output used:** Verification-key cache, generated-key regressions and research
  documentation in commit5294228; actual-hours draft records only the reported
  six-hour total, without fabricated sessions. PR#2. Layout reconciliation keeps
  incoming android/backend/docs paths and preserves existing feature additions.
- **How it was tested or checked:** AI ran55 experiment tests successfully and a
  production-dependency audit (zero vulnerabilities). Independent AI review
  reproduced a cooldown defect, then confirmed its fix and passed30 cache tests.
  Layout verification is in progress; see the batch ledger for final evidence.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Read the cache
  module and tests; explain freshness expiry, single-flight, cooldowns and why
  synthetic keys do not prove live Firebase acceptance. Run the README commands.
- **Limits:** Needs human review. No human review/testing/understanding claimed.
  No live routing, deployment, revocation checks or production quota guarantee.
  Trello activity details and board/card were not supplied; nothing was posted.

Layout validation update:55 Worker and277 backend tests, syntax checks, Android
lint/unit tests/Checkstyle/debug build pass after relocation. Independent AI merge
review found a relative documentation link, corrected; no feature loss or runtime
path defect found. Local Firebase emulator smoke is still in progress downloading
its missing JAR. Human review remains TODO (owner).

## 2026-10-27 · Request-body deadline · Backend infrastructure

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Continue the solo plan's HTTP hardening in the isolated Worker prototype.
- **Prompts (exact):**
  > implement those on the previous prompts
- **Output used:** src/read-json.mjs and focused tests; Worker calls the new
  reader. One-second timeout/client-abort408 and2KiB limit documented in README.
- **How it was tested or checked:** Eight tests failed before implementation and
  passed afterward. All64 experiment tests pass, including an authenticated
  incomplete-upload timeout through actual workerd. Preceding layout smoke
  exited0; all Firebase auth/rules/commute/match smoke checks passed. No new live
  operation was performed. Android checks passed on unchanged application code.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Read read-json.mjs
  and its tests; explain byte limits, elapsed-time checks, cancellation and cleanup.
- **Limits:** Needs human review. This does not prove production overall latency,
  persisted quotas or global abuse protection. Personal review/hours not verified.

## 2026-10-27 · Maintained CI actions · Infrastructure

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Replace deprecated CI action runtimes within the authorized solo plan.
- **Prompts (exact):**
  > implement those on the previous prompts
- **Output used:** .github/workflows/ci.yml now selects official release versions
  checkout7.0.1, setup-node7.1.0, setup-java6.1.0 and Gradle6.4.0. Selected basic
  GitHub cache explicitly; removed the unsupported build-root-directory input.
- **How it was tested or checked:** Inspected official versioned action definitions
  and release pages. Parsed workflow YAML successfully. Previous exact-head
  CI40450523624 at317fc9c passed all three jobs. New action execution awaits CI.
  Application Node22/JDK25, job names, permissions and working directories retained.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Explain the
  distinction between the action's Node24 runtime and the application's Node22;
  explain why basic caching was selected for the free-only requirement.
- **Limits:** Needs human review; no claim updated actions passed until CI finishes.
  No billing, dependency submission or new deployment/provider was activated.

## 2026-10-27 · Atomic quota primitive and deliverable drafts · Backend / report

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Continue the six solo workstreams: persisted reservations, report/demo
  drafts and handoff checklist.
- **Prompts (exact):**
  > implement those on the previous prompts
- **Output used:** src/quota.mjs with a separate test-only SQLite harness; README
  boundaries; docs/aaron-deliverable-drafts.md and docs/aaron-handoff.md.
- **How it was tested or checked:** Nine initial quota tests failed against the
  stub. Eleven SQLite tests now pass, including concurrent limits, rollback,
  window boundaries and runtime restart. Restart first failed due a Miniflare
  persistence option, diagnosed against its normalization code and corrected.
  Full75 experiment tests pass. Independent AI review found no actionable defects
  and passed all11 quota tests. Report body counts are350/400 words by whitespace;
  prose checked against implemented modules and recorded validation. Prior exact
  head a5b7d85 passed all three CI jobs; new quota work awaits its own CI run.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Read quota.mjs and
  explain why all account/user checks share a transaction, why rolling24h is more
  conservative, why failures remain charged, and why an identity-bound fixed
  account adapter is still necessary. Revise/rehearse report and demo passages.
- **Limits:** Needs human review. No production quota integration, real provider
  request, cloud deployment or performance certification. Report drafts do not
  establish personal authorship/understanding; final team contributions, QA and
  video remain pending. Only6 hours were user-reported; nothing posted to Trello.

## 2026-10-27 · Commute input research and five probes · FR4.1–FR4.3

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Research published fare/fuel/parking inputs and define five route
  cases with precise confirmed evidence and unresolved links.
- **Prompts (exact):**
  > implement those on the previous prompts
- **Output used:** docs/commute-input-evidence.md and handoff/index links.
- **How it was tested or checked:** Opened primary government, issuer, operator
  and university pages. Downloaded official fuel CSV, selected series_type=level
  for22 October2026 and recorded SHA256. Joined selected stops through stop_times
  and trips in existing public GTFS archives, preserving their recorded hashes.
  Found the non-T789 Universiti stop and the16 November feeder expiry boundary.
  Official Rapid Pelajar pages/PDF returned403; PDF skill was consulted for a
  read-only attempt, but no inaccessible PDF terms were claimed verified.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Explain subsidy
  eligibility, pass-versus-per-trip accounting, stale schedules, transfer paths,
  and why an unknown parking/fare value cannot be zero.
- **Limits:** Needs human review. These are defined probes, not working journeys.
  No paid routing/booking, private scraping, survey numbers, production defaults
  or verified student timetable. Parking tariff and current UM shuttle evidence
  still need primary confirmation; no student discount adopted from snippets.

## 2026-10-27 · Bounded GTFS normalization and schedule research · FR4.1 / FR4.3

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Continue the solo plan's JavaScript transit processing and inspect the
  existing public feeds against the five route probes.
- **Prompts (exact):**
  > implement those on the previous prompts
- **Output used:** backend/functions/src/gtfs.js and gtfs-journey.js, focused tests,
  fixture helper, offline scripts/gtfs-inspect.cjs, syntax checks and
  docs/transit-processing.md. No production endpoint or runtime migration.
- **How it was tested or checked:** Positive normalization/journey tests failed
  against stubs. Additional failing tests drove strict timestamp validation,
  transfer-history enforcement, feed validity and bounded-state pruning. All336
  backend tests pass, including59 transit cases. Syntax checks pass. Android lint,
  JVM tests, Checkstyle and debug build pass;75 Worker tests pass. Three official
  public GTFS snapshots were processed offline, recording table hashes and
  exclusions. Five probes plus an expiry repeat were evaluated. R4 initially hit
  the work limit; pruning ended trips and dominated states produced a provisional
  bus alternative within the same bound. No fare or live coverage was invented.
- **Independent AI review:** Partial. Reviewer identified transfer-rule bypass
  through chained walks and ignored feed_info validity, then hit a usage limit.
  Both were reproduced with failing tests and fixed. No clean final review is
  claimed; follow-up review remains open.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Read the two
  modules and run their tests. Explain calendars/exceptions, extended service
  days, estimated headways, transfer context, freshness/validity and unknown fares.
- **Limits:** Needs human review. Only UTC+08 zones and bounded supported semantics.
  No live route/fare/punctuality, physical transfer-path validation, deployment,
  NFR1 result or complete independent-review claim. Personal work hours unchanged.

Repository reconciliation during the transit task: current main4af895a changed
story ownership tables and launcher assets and removed the old20-hour minimum.
Preserved incoming team-authored files and added no edits to another owner's AI
log. Retained existing local implementation evidence and the CO2 catalogue when
resolving documentation conflicts. Updated the hours draft without inventing
hours. Backend336 tests and syntax checks pass after reconciliation; Android
checks on the new launcher are running. Human review remains TODO (owner).
Android reconciliation checks completed: lint, JVM tests, Checkstyle and debug
build passed with the incoming launcher. No device or production completion claim.

## 2026-10-27 · Authenticated load preparation and final solo review · FR4.1 / NFR1

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact deployed model version not recorded.
- **Task:** Complete the six solo preparation workstreams, measured local load
  scenarios, transit follow-up fixes and reviewable handoff evidence.
- **Prompts (exact):**
  > implement those on the previous prompts
  > keep as draft
- **Output used:** backend/tests/load/commute-fixture.k6.js, fixture-contract.mjs
  and tests, scripts/run-fixture-load.cjs, CI/package scripts, load README,
  bounded frequency expansion in gtfs.js and updated source/handoff/hour records.
  Official Firebase Authentication skill used for local synthetic account setup.
- **How it was tested or checked:** Four initial helper tests failed against stubs;
  later lifetime and token-cleanup regressions failed before their fixes. Seven
  helper/guard/fault tests now pass, including isolated fake Auth and missing k6.
  Official k6.2.3.0 ZIP checksum matched. Two60s local50/100VU runs were observed:
  first100-user p95=3495.26072ms with33failed positive requests among5812total;
  second passed50/100participation,8914checks and5negative contracts with p95
  22.18493ms/16.19116ms. The failed run is retained. Tokens were removed after each
  run. Backend fixture source matched recorded head7b007f6. Cleanup/evidence-only
  runner changes postdate the measured second run; no third measurement claimed.
- **Independent reviews:** Load reviewer independently passed6initial tests, found
  token creation outside cleanup, then independently passed7after the failing
  regression/fix. Manifest dependency omissions were also corrected. Transit
  follow-up confirmed transfer/feed-validity fixes, found frequency expansion
  beyond72h, and independently passed62tests after two failing regressions and
  the bound fix. No remaining actionable findings in that review. Final fresh
  whole-plan review found no blocking integration defect; stale artifact links
  and source-evaluation wording were refreshed. Final checks/CI recorded below.
- **Delegated load review prompt (exact):**
  > Independent review of load preparation in C:/Users/kikee_07xsqul/Downloads/Year Two/Jiran/jiran. Read AGENTS.md and Batch H docs/superpowers/plans/2026-10-27-aaron-solo-work.md. New files backend/tests/load/fixture-contract.mjs/.test.mjs, commute-fixture.k6.js, backend/scripts/run-fixture-load.cjs; CI/package scripts modified. Require fixed local demo endpoint,100 distinct verified synthetic Auth users+one unverified,tokenfile ignored/no token output, separate50/100 VU60s phases,semantic response checks/auth negativecases,proper thresholds and participation. Main purpose realistic preparation; not production NFR claim. First fullrun failed100user p95˜3495ms/33positive request failures out5812; all5negativecontracts passed. That run loaded script before newly added completed_users instrumentation; second unchanged60s run now live session25447 includes participation counters+source manifest. Do not edit files while run active. Identify actionable defects/security/measurement flaws, don't weaken thresholds or invent workingproduction backend. Six purehelper/guard tests pass. Reviewer needed by requesting-code-review skill; focused tests okay.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner). Run the helper
  checks and read the workload/runner. Explain simulation versus production,
  expected rejections, participation, token cleanup, p95 and why both runs remain
  visible. Explain the frequency expansion boundary before using transit output.
- **Limits:** Needs human review. No real listing search, Firestore/routing load,
  deployed Free CPU/capacity, production429gateway or NFR1completion. Prototype
  quota overload is separately tested. No live Firebase/project/provider change,
  billable activation, booking, private scraping or production deployment. The
  user chose to keep the6reported hours as draft; no Trello posting or invented
  activity/session allocation. Human understanding and final approval unverified.

Final local checks:339Jest tests/13suites,7load tests, syntax checks and75Worker
tests pass. Artifact links resolve; report body counts remain350/400words.
Measured workload/helper hashes match; token file absent. Final exact-head CI
will be reported separately; no unobserved result or human review is claimed.

Final CI40476858580 atbad15a4 passed backend, Worker and Android. Annotation review
found Android setup still targetingNode20; inspected the official4.0.4 definition
(Node24, supported platform-tools input) and updated that remaining deprecated
CI action. No app runtime/tool version or permission change. New exact-head CI
is required before closing this task; prior success is not reused as its result.

## 2026-10-27 Â· Rename latest commit to current story format Â· E0.1

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact model identifier not recorded.
- **Task:** Rename the latest CI commit using the newest repository story convention.
- **Prompts (exact):**
  > can u rename ur commit based on the newest rules
- **Output used:** Latest commit subject changed from `[Infra][CI] update remaining Android setup action` to `[E0.1] Update Android setup action`; this entry appended to `docs/ai-log/aaron.md` on the existing PR #2 branch.
- **How it was tested or checked:** Fetched origin/main and checked its AGENTS.md convention; mapped CI maintenance to E0.1 in docs/team/aaron.md. Checked the amendment diff and commit subject. No application code changed; application tests were not rerun for this metadata/log-only task.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner).
- **Limits:** Needs human review. Only the latest commit is renamed; historical commits retain their original subjects. Publishing the amended commit requires explicit permission for a force-with-lease push under AGENTS.md; remote publication is pending that permission.

Publication follow-up (27 October 2026): exact human prompt `yes` approved the
requested force-with-lease push. The push succeeded and `git ls-remote` confirmed
492236f42e46f3be58a45b6fd71a90fb375327f1 on the feature branch. This approval and
publication record is included by amending the same E0.1 commit, then publishing
with a lease against that verified remote hash. No application code changed;
new-head CI results are not claimed here. Human review fields remain TODO (owner).

## 2026-10-27 Â· Apply story subjects across Aaron's PR commits Â· E0.1

- **Human owner:** Aaron.
- **AI tool and model:** Codex; exact model identifier not recorded.
- **Task:** Correct the earlier latest-only interpretation and apply current story IDs to all Aaron commits exclusive to PR #2.
- **Prompts (exact):**
  > commit messages use [Story] short description (for example [E7.1] Add balance calculation; the story card holds the FR, see docs/team/TEAM_PLAN.md), branches use feature/<short-name>.
  >
  > ok?

  > why only one with the epic format walao
  > [6:07 PM, 10/27/2026] Ary Nasution: commit messages use [Story] short description (for example [E7.1] Add balance calculation; the story card holds the FR, see docs/team/TEAM_PLAN.md), branches use feature/<short-name>.
  > [6:08 PM, 10/27/2026] Ary Nasution: refer to my newest commit @Aaron Tan
  >
  > do it for my others too
- **Output used:** Reword 24 remaining legacy subjects in the 25 Aaron commits exclusive to `feature/aaron-commute-core`, using E0.1, E0.2, E3.3, E4.2 and E6.2 from docs/team/trello-cards.md and the team/personal plan. The latest E0.1 subject is retained. PR #2 remains the review location. Preserve existing subject detail and message bodies. Retain an ignored local old/new commit map and backup ref for recovery.
- **How it was tested or checked:** Fetched origin/main and inspected Ary's latest convention commit and the story cards. Rewrite verification requires identical trees for every historical commit, with only this log appended at the final tree; identical author/committer metadata, correctly mapped parents and message bodies; unchanged upstream commit objects; all 25 final subjects in story format and a clean worktree. Remote publication uses the previously approved force-with-lease method with an explicit expected old tip, followed by remote-hash verification.
- **What the human changed or rejected:** TODO (owner).
- **What the human understands and can explain:** TODO (owner).
- **Limits:** Needs human review. Subject labels do not mean story acceptance or production readiness. Already-merged commits on main and teammates' commits are not renamed. No application code change or new application-test/CI result is claimed. Earlier hashes cited in historical evidence remain historical observations.
