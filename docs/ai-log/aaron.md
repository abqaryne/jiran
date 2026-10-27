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
