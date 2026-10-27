# Aaron solo implementation workstreams

> Implement inline with executing-plans, TDD and official Firebase skills for
> relevant work. Review each substantial batch independently before publication.

**User objective:** "implement those on the previous prompts" — the six solo
workstreams listed after the Aaron status question. This covers all six below;
finishing one prototype does not complete the objective.

**Constraints:** Java Android/JavaScript Functions remain the application stack.
Firebase Auth/Firestore remain on existing `jiran-mad`/`jiran.com`; local work
uses `demo-jiran`. Free-only; no billing, paid calls, duplicate projects, cloud
deployment or silent runtime migration. Proposed Workers work remains isolated.
Ary performs the final merge. Do not fabricate approved assumptions, user hours,
live coverage, performance, report outcomes or QA evidence.

## Workstreams and completion evidence

- [ ] **Free-backend prototype hardening.** Implement bounded Firebase key fetching
  with provider cache expiry, single-flight and rotation/cooldown tests; atomic
  SQLite per-user/account quota reservations in workerd; timeout/error handling.
  Evidence: runtime tests and documented limits; no production adoption claim.
- [ ] **Commute input research.** Record primary published fares/fuel/parking and
  transit/shuttle sources, five sample route cases with precise known/unknown
  evidence and dates. No scraping private apps or pretending API coverage was
  measured without credentials. Keep factual research separate from approved
  production configuration; survey/eligibility gaps remain explicit.
- [ ] **Transit processing.** Implement reusable JavaScript feed normalization and
  bounded scheduled-journey evaluation: service dates/exceptions, >24h times,
  headways, stop sequence/transfers, unknown fares/expired feed behavior. Test
  synthetic edge cases and inspect real public-feed samples with provenance.
- [ ] **CI/performance preparation.** Update deprecated Actions against official
  releases, preserve app Node/JDK versions and check names; extend realistic
  load scenarios with authenticated failure/overload cases and reproducible
  commands. Local/fixture loads never certify NFR1 for the production system.
- [x] **Graded deliverable drafts.** Prepare the350-word architecture draft and
  Aaron's400-word implementation contribution based on actual code/evidence;
  prepare technical demo narration/timing. Explicitly label incomplete features
  and dependencies; no invented team implementation or user study results.
- [x] **Handoff and actual-hours record.** Review PR/evidence, document unresolved
  decisions and owners, prepare freeze checklist and actual-hours template.
  Trello writes require real user-supplied hours and an accessible board/card;
  do not count AI runtime as the user's personal work hours or send team messages.

## Batch A: Firebase verification key cache

Create `experiments/worker-feasibility/src/firebase-keys.mjs` and generated-key
tests. Keep the fixed Google securetoken JWK endpoint; JOSE handles cryptography
and local key selection. Reject invalid headers before network. Fetch cap64KiB,
timeout1000ms including streaming, manual redirect handling with rejection of
non200/private/invalid keys (Workers does not support fetch redirect:error).
Honor Cache-Control max-age/Age (bounded at6h, absent/invalid TTL => no reuse), no
stale-key use after expiry or failed refresh. Deduplicate refresh promises and
rate-limit unknown-kid refresh to30s without rate-limiting ordinary valid keys.
Tests use injected time/transport and in-memory keys only. Preserve existing
HTTP verifier status codes and all existing25 workerd tests.

## Ledger

- Batch A implemented and independently reviewed. Fresh full experiment run:
  **55 tests passed** (30 generated-key cache cases plus25 workerd HTTP/crypto
  checks); production-dependency audit found zero vulnerabilities. Observed
  failing rotation-race regression before fixing pending-refresh joining.
  Reviewer reproduced the zero/short-TTL unknown-key amplification defect;
  regression tests now enforce the retained-ID cooldown without stale-key use.
  Follow-up review independently passed all30 cache tests with no remaining
  actionable findings. No live keys, cloud deployment or production readiness
  is claimed. Atomic quota persistence and request-body deadlines remain next.
- Hours draft records only the user-reported6 hours on26 October; task notes and
  Trello destination remain missing. No invented sessions or external posting.

- Previous goal turn was a status explanation; revalidated clean worktree at
  `1b0125d`. CI40443193955 previously passed all3 jobs and PR#2 is mergeable/draft.
  Current broad objective is the six explicitly requested solo workstreams.
- Ruling: retain the existing feature branch and isolated experiment package;
  no reason to create another worktree or change the Android transport during
  research/hardening. User authorization covers reversible implementation and
  feature-branch publication, not paid activation or Ary's final merge.

## Layout reconciliation (incoming main6c2c3f8)

- Pushed cache batch5294228; PR2 had no checks because main's repository moves
  introduced conflicts. Merged current main into the feature branch, retaining
  android/backend/docs layout, current team rules and the free-only restriction.
- Kept every feature addition at its new path, including Java parser tests,
  Functions fixtures/config and the separate Worker experiment. Updated Worker
  imports and role/README links. Independent review found one relative Worker
  README link, now corrected; no executable path regressions or feature loss.
- Fresh55 Worker and277 Jest tests plus Functions syntax checks pass after moves.
  Android checks and the local emulator smoke test are still running.
- Requirements discrepancy: incoming AGENTS says3s at50 concurrent users and
  continued operation at100; canonical PRD NFR1 still says p95 under3s at100.
  Preserve the stricter100-user performance scenario pending owner resolution;
  do not silently reduce the existing Aaron/PRD target.

Layout check update: Android lintDebug, testDebugUnitTest, checkstyleMain and
assembleDebug passed after reconciliation. Local Firebase CLI is downloading its
missing Firestore emulator JAR; the observed smoke process remains running.
The existing ignored Android Firebase config was copied to its new ignored
android/app location without logging or staging its contents.

## Batch B: bounded request JSON reading

Ruling: extract the existing2KiB JSON reader into a small shared module, adding a
one-second wall-time deadline and client-abort handling. Keep fixture routes,
authentication ordering and response contracts unchanged except the explicit408
request-timeout response. Cancel stalled/oversized bodies without awaiting an
uncooperative stream's cancellation promise; clear timers/listeners and release
the reader. Check elapsed time after reads as well as a timer so a blocked event
loop cannot turn a late body into success. This is a request-body deadline, not a
production end-to-end latency/CPU guarantee. No Firebase settings or live calls.

Previous turn was progress: committed/pushed cache and layout reconciliation.
Fresh local emulator smoke exited0 with all auth, rules, commute and match checks
passing. CI40450523624 backend/Worker jobs passed; Android remains under observation.

Batch B verification: eight body-reader tests failed against the unimplemented
reader before implementation; all eight now pass. Full64 experiment tests pass,
including the real workerd authenticated partial-upload408 regression. No live
Firebase calls/settings, shared calculation behavior or Android changes.
Needs human review: read src/read-json.mjs and explain why byte limits alone do
not stop slow uploads, why cancellation is not awaited, and why the body deadline
is not a production p95 guarantee.

## Batch C: maintained CI action runtimes

Ruling: use inspected official checkout7.0.1, setup-node7.1.0, setup-java6.1.0 and
Gradle actions6.4.0 releases. Their actions run on Node24; application Node22 and
JDK25 remain explicit. Keep job/check names, triggers, contents:read permissions,
cache lockfile paths and android/backend working directories. Gradle6 no longer
has build-root-directory; the Gradle command's working-directory already selects
android. Set cache-provider:basic explicitly to retain open-source GitHub cache
and avoid the new commercial enhanced default. No paid integration is activated.
Configuration changes are verified by the actual three-job CI run, not a test
that mirrors YAML text. Live Firebase deployment stays disabled.

Sources inspected27 October2026:
- https://raw.githubusercontent.com/actions/checkout/v7.0.1/action.yml
- https://raw.githubusercontent.com/actions/setup-node/v7.1.0/action.yml
- https://raw.githubusercontent.com/actions/setup-java/v6.1.0/action.yml
- https://raw.githubusercontent.com/gradle/actions/v6.4.0/setup-gradle/action.yml

CI40450523624 at317fc9c completed successfully across all three jobs. New workflow
YAML parses successfully; current versioned action changes await exact-head CI.

## Batch D: atomic SQLite quota reservations

Ruling: implement a synchronous reservation primitive using the documented
Durable Object SQLite transactionSync API, tested against real local workerd
SQLite. One account object must own all its user reservations; account sliding
minute, account rolling24h and user rolling24h checks share one transaction.
Rolling24h is deliberately stricter than ORS's anchored daily reset, avoiding
unverified reset alignment. Limits are explicit server-owned configuration, not
request input; no production defaults or provider calls. Units count provider
requests, including failed dispatches; reservations are never automatically
refunded or retried after uncertain outcomes. A failed transaction cannot partly
consume a user's allowance. Persist a last accepted timestamp to fail closed on
clock regression. Parameterize SQL and prune expired rows on accepted writes.
Test concurrent account/user limits, exact window boundaries, invalid inputs,
rollback and storage failure. This primitive does not authenticate a public
request or prove deployment quotas; its future caller must use verified identity
and one fixed account object before each provider call.

Official API evidence:
https://developers.cloudflare.com/durable-objects/api/sqlite-storage-api/#transactionsync

Batch D verification:11 real SQLite quota tests pass, including30 concurrent
callers, atomic user/account limits, exact window boundaries, failed-commit
rollback, sanitized failure, backwards-clock rejection and runtime restart.
Nine initial tests failed against the stub. Restart initially failed because
Miniflare5 normalizes isolatedResourcePersistencePath to resourcePersistencePath
when shared storage is off; inspected that normalization and used the correct
persistent root. Full experiment suite now passes75 tests. Independent review
found no actionable defects and independently passed all11 quota cases.
The normal Worker still has no quota endpoint or live provider integration;
verified identity and fixed-account dispatch binding remain adoption gates.

CI40451044115 at a5b7d85 passed all three jobs with the updated action releases.

## Batch E: report, demonstration and handoff drafts

Prepared docs/aaron-deliverable-drafts.md with exactly350 architecture words and
400 implementation words (whitespace count within marked body sections). The
report describes verified local code and pending production gates, and includes
a90-second technical-demo script with Ilham's20 seconds reserved but not invented.
Aaron's final report wording, understanding and personal revisions remain TODO.
Prepared docs/aaron-handoff.md with named unresolved decisions, evidence links
and the13 December freeze gates. These complete the requested draft/checklist
artifacts, not the team's final report, freeze, Trello posting or QA. The hours
record remains6 user-reported hours on26 October with activities/card unspecified.

Remaining solo implementation: transit normalization/journey logic, official
commute input/five-route research and realistic load scenarios. Production
adoption and actual-hours/Trello details remain explicitly unresolved.
