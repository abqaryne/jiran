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
- [ ] **Graded deliverable drafts.** Prepare the350-word architecture draft and
  Aaron's400-word implementation contribution based on actual code/evidence;
  prepare technical demo narration/timing. Explicitly label incomplete features
  and dependencies; no invented team implementation or user study results.
- [ ] **Handoff and actual-hours record.** Review PR/evidence, document unresolved
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
