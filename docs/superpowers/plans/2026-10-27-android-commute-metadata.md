# Android commute provenance and warnings plan

> Execute inline with test-driven-development, official Firebase Auth guidance for local SDK integration, and verification-before-completion; independent review at batch end.

**Goal:** Complete the Android half of the existing FR4 backend metadata pipeline: cost/route sources, provider warnings and traffic fallback remain visible with commute estimates.

**Spec:** team/aaron.md, PRD FR4.1–4.3, docs/api-contracts.md. This extends the existing developer preview; Ary still owns the shared Room/detail screens. No live routing or schema decisions are introduced.

**Design:** CommuteSummary.Option gets nullable bounded costSource/routeSource, immutable copied warnings and nullable Boolean trafficFallback (absence stays unknown, not false). Present null/wrong types/oversized values fail parsing. Absent fields retain backwards compatibility. Render sources and every warning as plain text in the available-mode row, with explicit fallback caveat only when true. Do not interpret provider strings as HTML or hyperlinks.

On-time server fixture gains explicit synthetic source/warning/fallback fields so the complete callable→Android parser→view path is testable. No Google attribution is falsely applied to synthetic data. Existing numeric results and three scenario IDs remain unchanged. Add native instrumentation assertions of visible sample provenance and fallback warning after an actual verified local request.

- [x] Add failing JVM metadata parsing/immutability/malformed tests against default-field stub, then implement bounded parsing.
- [x] Render metadata via string resources, update labelled server fixture and backend/Android integration assertions.
- [x] Add native activity instrumentation check, run JVM/Jest, lint/Checkstyle/build and full real emulator/Android checks; inspect captured preview if available.
- [x] Independent review/fixes, update contracts/Aaron/README/AI log and evidence; commit/push PR #2 and inspect latest CI.

## Ledger

- Prior goal turn made progress: bounded transport, pricing layer, CI repair, pushed draft PR #2. Revalidated worktree clean at959f7e0. Previous PR run40407910952 was still in progress at batch start, so no new CI success claim is inferred.
- Asked user asynchronously for billing owner/approved Maps budget while independent local work continues. No billing configuration or paid requests will be inferred from silence.

- TDD: initial SDK location preflight failed until explicit ANDROID_HOME was set (no source fix needed). Then14 JVM tests ran with2 expected metadata failures against stub defaults, before parser implementation. Backend on-time fixture expectation failed before metadata was added. Real Android run passed3 SDK checks and failed the new UI assertion because metadata was not rendered, then all4 passed after rendering.
- Review found no important defect. Fixed minor generic cost label and duplicate row clearing. Evidence limitation: native UI assertions inspect bound row text, including offscreen rows, rather than scroll gestures. Captured full-content Android render and visually inspected all4 rows, source/warning/fallback text and sample notice without clipping; this is not final shared-UI or Google-logo attribution signoff.
- Final checks after review fixes: lintDebug/lintLiveDebug, testDebugUnitTest, checkstyleMain, assembleDebug/assembleLiveDebug/assembleDebugAndroidTest all pass (124tasks;9s). JVM14tests including8parser tests pass. Lint0errors/1existing tool-version advisory. Backend209tests/syntax pass; source/fixture expectation has no new numerical assumptions.
- Explicit demo-jiran complete REST+Android smoke repeated after the review fixes: exit0, OK(4tests), including actual native preview row text. Final rendered capture inspected. Stopped only the headless AVD started for this batch; Firebase emulators shut down automatically. No live Firebase settings/data or paid Maps requests used.
- Prior pricing PR run40407910952 at959f7e0 completed successfully for both android/backend. This metadata batch needs its own new PR run; retain that distinction in the handoff.
