# Deterministic match backend implementation plan

> Use superpowers:executing-plans, test-driven-development and verification-before-completion.

**Goal:** Implement Aaron's FR3.2 deterministic matching engine and a verified-auth local callable with transparent breakdowns for Ilham. Production profile reads remain dependent on Gathan's schema and privacy agreement.

**Architecture:** Pure JavaScript scoring consumes explicit normalized questionnaire records and a versioned weight configuration. The callable selects server-owned synthetic fixtures only, under the existing emulator export guard. No AI, Firestore writes, private timetable or contact projection.

**Spec:** PRD FR3.1/FR3.2, team/aaron.md, ASSUMPTIONS #9, docs/engineering/api-contracts.md.

## Proposed normalization (implementation choices, not survey approval)

- Seven factors: sleep, cleanliness, noise, cooking, smoking, pets, budget. Explicit integer weights in basis points must cover exactly these factors, sum to 10000 and include a version. No default production weights.
- Sleep uses minutes after midnight and the shortest cyclic difference / 720. Three ordinal answers use 0..4 with absolute difference / 4. Smoking and pets are booleans with exact agreement. Budget uses integer sen intervals and intersection / union; identical zero-width intervals agree, other zero-overlap intervals do not.
- Similarities are rounded to basis points (0..10000). Weighted sum is rounded half-up using exact integer arithmetic. Output score basis points / 100 gives a 0..100 percentage with two decimal places. This is a proposal for questionnaire mapping, not a hidden schema decision.
- Missing required positive-weight answers yield insufficient_data and null overall score; zero-weight factors do not block. Invalid present data fails rather than becoming missing. Overlapping class weekdays are sorted unique ISO weekdays, not exact schedules, and are informational only.
- At most 100 candidates. Reject duplicate IDs; stable sort scored matches descending then ASCII ID, unscored last. Exclude caller by subject ID. Only candidate ID, score, safe factor breakdown and day intersections leave the core; caller adapters must authorize eligibility before invoking it.
- Rule reasons are stable factor agreement/difference codes; AI may explain these, never change scores. No raw answers/contact fields are returned. Fixture subjects are synthetic and explicitly labelled.

## Tasks

- [x] Test first: exact known scores, cyclic midnight, ordinal boundaries, budgets, weights, missing data, ties, self exclusion, input immutability, privacy projection and candidate limits. Observe red, implement `functions/src/match.js`, verify full Jest.
- [x] Test adapter auth/selection/overrides first. Implement `functions/src/match-preview.js`, fixture JSON and emulator-only `computeMatchScores` export. No live export, no client userId or weight overrides.
- [x] Add real emulator smoke assertions for auth, deterministic ranking, safe response, missing data and empty candidates; run existing complete REST checks.
- [x] Update API contract, Aaron status, README, AI log; independent review and fix findings; record evidence and commit on the feature branch.

## Review focus

Missing data must not look like incompatibility; no arbitrary default weights; candidate ties must be deterministic; exact private schedules must not leak; live environments must expose no fixture callable. Questionnaire mapping, survey weights and candidate visibility authorization remain production prerequisites.

## Evidence

- Previous goal turn made progress: commute batch aab9e02, verified tests and local commit. Current worktree was clean before this batch. Full Aaron completion is not proven: Maps/billing, real coverage/fares, shared schema/UI, production auth policy, CI/branch protection, QA/Trello and deliverables remain open.

- TDD: observed 34 pure-engine and 12 adapter tests failing against empty implementations, then passing. Independent review found no blocking/important defects. Added its suggested arithmetic/validation regression coverage: half-up boundary, large integer budgets, all ordinal extremes, invalid subject and zero-weight present values.
- Final backend evidence: 7 Jest suites / 106 tests pass; syntax checks and diff whitespace checks pass. Real Firebase CLI 15.34.1 emulators:exec with explicit demo-jiran exited 0, proving match auth/override errors, ranked/missing/empty scenarios, deterministic projection, and prior commute/Auth/Firestore checks. No Android source changed in this batch, so existing Android checks were not repeated.
- Live backend, keys, cloud settings, production schemas, weights and privacy rules were unchanged. This is domain and local callable progress; all production dependencies remain open.
