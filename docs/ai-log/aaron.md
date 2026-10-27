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
