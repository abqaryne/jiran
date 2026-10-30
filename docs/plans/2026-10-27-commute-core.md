# Module 4 commute calculation and local preview plan

> Use superpowers:executing-plans, test-driven-development and verification-before-completion. Use the official Firebase Auth skill for callable authentication; no live Firebase/Maps changes in this batch.

**Goal:** Implement and prove FR4.1–4.3 calculation behavior with explicit inputs, plus an authenticated emulator callable and Android preview. Real routing/pricing and product schemas remain separate integrations.

**Spec:** `PRD.md` Module 4; `team/aaron/README.md`; `docs/engineering/api-contracts.md`; `ASSUMPTIONS.md`.

**Architecture:** Pure Node domain calculation consumes already-normalized route/cost inputs. An emulator-only `getCommuteOptions` adapter loads a named server-owned fixture after verified authentication. A Java repository/parser and developer preview consume the same response. Live endpoints, Maps keys, schema writes and guessed attendance/fare defaults are excluded.

## Explicit design decisions

- Inputs use integer sen, seconds, metres, explicit ISO 8601 offsets, and an explicit nonnegative integer `monthlyTrips`. Utilities are the caller's already-calculated equal share; no rounding policy for occupancy is silently introduced.
- Strictly arrive **before** class, following PRD wording: arrival exactly at class start does not qualify. Timings represent one outbound class journey; monthly trips are a separate explicit cost projection. The team can revise this boundary through the contract.
- Normalize four modes in stable order: walking, transit, e_hailing, driving. Missing route/fare yields an unavailable reason, never a zero estimate. Missing emission factor leaves CO₂ null without excluding a costed route.
- Cheapest on-time route wins. Ties choose shorter travel time, then the stable mode order. No on-time route means null selection and null monthly total. Deposit is never included.
- Validate finite safe integer money/distances/durations/counts, valid calendar timestamps, unique known modes and safe products/sums. Bad requests fail clearly rather than returning NaN/overflow or misleading totals.
- CO₂ grams/trip = metres / 1000 × supplied grams/km, rounded to whole grams, with the supplied nonempty source. This is a unit/rounding choice for the preview; no published factor is inferred.
- Fixture scenarios cover an on-time comparison, no on-time route and missing data. Every response/UI explicitly identifies fixtures; their values are not promoted into verified assumptions or production estimates.
- Android preview is reachable only in emulator debug, requires the existing verified local login, and does not replace Ary's final Room details screen.

## Batch 4: domain calculator

Files: `functions/src/commute.js`, `functions/test/commute.test.js`.
Interface: `calculateCommute(input)` returns `options`, `selectedMode`, `trueMonthlyCostSen`, component amounts, `monthlyTrips`, timestamps and `isEstimate`.

- [x] Add tests first for late cheaper modes, exact-time boundary, ties, missing routes/fares/factors, zero trips, correct unit math, malformed inputs, invalid calendar/offset dates, overflow and input immutability. Observe failure.
- [x] Implement smallest domain module. Run full Jest suite.

## Batch 5: emulator interface and Android preview

Files: `functions/fixtures/commute-scenarios.json`, `functions/src/commute-preview.js`, `functions/test/commute-preview.test.js`, `functions/index.js`, `scripts/commute-smoke.cjs`, `scripts/emulator-smoke.cjs`; Java `data/CommuteRepository.java`, `data/CommuteSummary.java`, `ui/CommutePreviewActivity.java`, JVM parser tests and Android instrumentation.

- [x] Test verified auth and fixture ID validation before implementing the adapter. Export the callable only inside the existing strict local-environment guard.
- [x] Add real emulator tests for auth/error mapping, all scenarios, no private fixture data, and deterministic literal results.
- [x] Add parser tests first for valid typed summaries and malformed payloads; implement immutable Java models and callable repository.
- [x] Add local-only preview with loading/error/no-on-time states, all four modes and explicit sample labels. Preserve error/retry state across backgrounding; require local verified session.
- [x] Run complete REST+Android SDK integration, Java/Jest tests, lint/Checkstyle and debug/live builds. Confirm no preview Function is exported in live configuration and no live resource mutation occurs.

## Batch 6: handoff and review

- [x] Update API contracts to distinguish the implemented local fixture request from proposed production listing/unit request. Document units, boundaries and pending assumptions.
- [x] Update README/Aaron/AI log; independent code review; fix important findings and record final verification. Do not mark Maps coverage, production Module 4 or NFR1 complete.

## Review focus

Late zero-cost walking must not mask viable transit; missing fares must not become free; unknown CO₂ must remain unknown; malformed backend data must not become a success in Android; fixture access must fail closed outside the demo emulator.

## Execution ledger

- Preflight: foundation commit `99e412d` has been merged through PR #1 as `6741b41` on origin/main, with identical code. Created `feature/aaron-commute-core` from that merged baseline, in the existing workspace. No additional approved schema/fare/emission/attendance inputs in handoff docs.
- Batch 4: tests observed 31 failures against an empty calculator before implementation; calculator suite then passed. Current full backend suite: 56 tests across 5 suites, including real-module no-live-export checks. Syntax checks passed.
- Batch 5: adapter tests observed 12 failures before implementation; immutable Java parser tests observed 5 failures before implementation. Full JVM suite passed 12 tests (6 new parser tests). REST emulator smoke passed authorization, payload rejection and all fixture calculations alongside existing Auth/Firestore checks.
- Android SDK integration: complete `emulators:exec --project demo-jiran ... "node scripts/emulator-smoke.cjs --android"` exited 0 with `OK (3 tests)`. The new integration test verifies on-time, no-on-time and missing-data scenarios through the actual Android SDK/repository/parser. All test accounts/data remained local and disposable.
- Independent review: no blocking findings. Advisory: parser does not re-run the server's cheapest/tie selection algorithm. Kept the server as that authority to avoid duplicated business logic; Android independently checks timing, mode completeness, numeric types, component sums and monthly-cost consistency. Server selection is pinned by calculator tests.
- No Firebase settings/rules/production Functions were deployed. No Maps calls, paid API calls or real user data used. Gathan's schema, Ary's final UI, verified assumptions, Maps coverage and NFR1 remain pending.

- Final Android verification: lintDebug, lintLiveDebug, testDebugUnitTest, checkstyleMain, assembleDebug and assembleLiveDebug completed successfully (94 tasks). Lint reports zero errors and one advisory about a newer Gradle version; the pinned toolchain remains unchanged. Final diff whitespace check passed.
