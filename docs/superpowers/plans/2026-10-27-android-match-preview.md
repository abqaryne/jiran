# Android match-score handoff implementation plan

> Execute inline with TDD, official Firebase Auth guidance and independent review.
> Continue the existing feature branch; no live deployment or final product UI.

**Goal:** Verify the existing FR3.2 score backend through the Java Firebase SDK,
immutable typed parsing and a labelled local developer preview of scores/reasons.

**Spec:** team/aaron.md, PRD FR3.2, docs/api-contracts.md implemented fixture contract.
Keep Java Android/JavaScript Functions and free-only policy. Ary owns the final
Match product screen; survey weights, authorized production candidates and UM
policy remain open. No AI service or new production schema is introduced.

## Design and acceptance

- `MatchSummary` parses only the current fixture envelope and known scenarios.
  Bounds match count at100, validates candidate IDs/uniqueness, score basis points
  and percentage consistency, scored/insufficient-data states, seven unique
  weighted factors, missing factors/reason enums and optional ISO overlap days.
  Numeric parsing accepts Firebase integral doubles but rejects fractions/NaN.
  Copy all lists, project only known fields and preserve server rank order; no
  client scoring formula or private contact/profile fields.
- `MatchRepository.preview` calls existing `computeMatchScores` only when emulator
  build flag is enabled. No live SDK calls from fixture preview.
- Add a developer activity with three scenarios, verified-email sign-in gate,
  loading/error/retry, score/missing-answer/empty states, source/version notice,
  readable rule reasons, overlap days and sample weight/similarity breakdown.
  Plain TextViews, no raw HTML or server exception messages. Guard activity in
  liveDebug/release; no final shared UI or Figma translation is implied.
- JVM tests cover valid fractional score, missing/empty, immutability, bad
  envelope/IDs/scores/factors/reasons/weekday data. Verify private-field projection
  by inspecting the explicit typed-field whitelist and absence of raw-map storage.
  Real Android SDK integration covers all three server fixture scenarios. Native
  view assertions verify bound scores/reason labels and a screenshot is inspected.
- Verify lintDebug/lintLiveDebug, JVM tests, Checkstyle, debug/live/test builds and
  existing backend tests plus real demo emulator/Android smoke. No NFR completion
  claim from these fixture checks.

## Tasks

- [x] Add failing parser tests/stub, implement strict immutable model.
- [x] Implement gated repository/developer activity/resources; add failing native
  binding and SDK integration checks before wiring UI.
- [x] Run checks and real emulator tests, inspect render, independent review/fix.
- [ ] Update Aaron/contracts/AI log/evidence, scan, commit/push PR#2 and inspect CI.

## Ledger

- Prior batch is verified progress: Worker experiment pushed3e09d65, all three CI
  jobs passed run40427238562. Documentation follow-up pushed0c760d1. Worktree clean
  before this plan. Runtime/provider migration remains unadopted.
- Parser TDD:22 JVM tests ran,3 expected positive-state/immutability tests failed
  against the stub; all22 then passed with the parser. Checkstyle also passed.
- First native attempt failed during cold Functions discovery at the default10s;
  inspected the specific timeout, used CI's existing30s discovery environment
  setting and documented it in README. No backend code change was needed.
- Native red run then passed existing4 checks and failed the two new checks as
  expected: unwired repository and empty Match screen. After wiring, real local
  REST smoke and all6 Android checks passed. Extended native screen assertions to
  include missing answers/unknown overlap and empty-state row clearing.
- `lintDebug lintLiveDebug testDebugUnitTest checkstyleMain assembleDebug
  assembleLiveDebug assembleDebugAndroidTest` succeeded. Independent review found
  no blocking issues. Ruling: safe private-field projection is code-inspection
  evidence from explicit field reads/final typed values/no raw-map retention;
  do not claim a private-field serialization test that was not performed.
- Pulled and inspected `.gradle/match-preview.png`: clear sample notice,100%/50%
  scores, overlap days, rule reasons and sample factor breakdowns; no clipping in
  full-content native capture. This is not viewport/scroll-gesture coverage.
- Final rerun after extending native missing/empty assertions: all REST smoke
  checks and **6 real Android tests passed**, including ranked/missing/empty SDK
  parsing and native100%/50%, reasons, overlap, missing labels and empty row clearing.
  Existing backend regression: **277 Jest tests /11 suites and syntax checks pass**.
  Only the task-started headless AVD was shut down afterward; emulator services
  shut down through emulators:exec. All accounts/data were disposable demo data.
- Pushed match handoff `77c6b51`. GitHub then reported PR#2 CONFLICTING/DIRTY with
  no current-head checks. `git fetch origin main` found team changes through
  `875eee6` (design resources/product shell and private-contract docs). Merging
  main into the feature branch had one conflict: adjacent activity declarations.
  Retained both non-exported previews and incoming non-exported themed ShellActivity,
  with the same MainActivity launcher/application theme. No incoming team files
  were discarded and main itself was not modified. Independent review confirmed
  the resolution. This is branch integration, not claiming authorship of team work.
- Merged Android lintDebug/lintLiveDebug, JVM tests, Checkstyle and debug/live/test
  builds passed; incoming main's2 ResourceTest cases bring the total to **24 JVM
  tests**, all passing. The merged APKs then passed all REST smoke and **6 real
  Android checks**, including the extended missing/empty native assertions. Pulled
  the merged render for comparison and shut down only the task-started AVD.
