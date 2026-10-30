# Routes provider adapter implementation plan

> Execute inline with superpowers:executing-plans and test-driven-development; independent review at batch end.

**Goal:** Implement Aaron's server-side Routes integration boundary for FR4.1 without enabling a live endpoint or making paid requests.

**Spec:** PRD FR4.1–FR4.3; team/aaron.md; docs/research/maps-readiness.md. Current feature branch is clean at 4b94a29; retain this authorized continuation workspace.

**Architecture:** `functions/src/routes-client.js` is a small Node 22 fetch client with a fixed Google HTTPS endpoint, explicit server credential injection, strict inputs, bounded fetch/body read, narrow response projection and sanitized errors. Caller owns Firebase authorization, listing/profile resolution and sourced cost assumptions. It never caches, logs, reads environment keys, writes data or exports a callable. Inject transport/clock only for server tests.

## Decisions

- Request one WALK, TRANSIT or DRIVE route using validated lat/lng and an explicit departure timestamp. No arbitrary URL/place query/proxy. Normalize time to UTC. Limit transit to documented -7/+100 days; other modes disallow past departure. Explicit `trafficAware` only for DRIVE; no unsupported arrivalTime for roads or traffic options on transit.
- Default 2500ms deadline (configurable 1..10000ms), including body read. Max 128KiB response; no retries. Timeout aborts request; cap overflow cancels body. Non-2xx errors never parse/return raw provider text or credentials. HTTP 429 maps resource-exhausted; other provider/transport failures unavailable; timeout deadline-exceeded.
- Field mask: duration, distance, warnings, transit fare and fallback information. No wildcard, polylines, descriptions, addresses or route tokens. Region MY, metric units, language en; no alternatives, toll or waypoint features. Traffic-aware DRIVE explicitly opts into its pricing tier.
- Normalize duration with ceiling to whole seconds (up to 48h), distance as nonnegative integer metres. Missing required scalars are malformed, not zero. Empty/missing routes means missing_route. Exactly one route supported by this no-alternatives boundary.
- Parse MYR Money with integer/BigInt arithmetic, rounding fractional sen half-up; unsupported currency/absent fare stays null, never free. Invalid present Money makes provider data unavailable. Warnings remain required downstream display data; preserve bounded strings. Fallback flags remain visible so callers do not label fallback results as traffic-aware.
- Return provider data only; fare null does not hide valid route time/distance. No fabricated e-hailing/parking/fuel/emission values. Connecting this adapter to production monthly calculation requires approved fare/config, credential/billing and schema/timetable inputs. Real probe timing/fare/UM shuttle coverage remains unverified.

## Tasks

- [x] Write request/response/error tests first, observe failure against stub, implement client. Include invalid coordinates/calendar/offsets, time windows, unsupported combinations, zero vs missing fare, subsecond duration, currency and integer rounding, warnings/fallback projection.
- [x] Add resource/deadline/transport tests using real Response streams and injected fetch: non-2xx, redirection policy, invalid JSON, overflow, body stall, request stall; verify no raw provider errors/secret leakage or retries.
- [x] Run full Jest and syntax checks; independent review; fix important findings.
- [x] Document server interface/remaining production gates in API contracts, Aaron status, README and AI log; record evidence; credential scan/diff checks; local commit.

## Review focus

No hidden paid request, SSRF or retry; deadlines include response streaming; absent fare is not zero; transport errors never leak credentials; fallback/warnings survive normalization. A fake response test proves adapter behavior, not real API acceptance, transit coverage or NFR1.

## Ledger

- Previous goal turn was progress: match core 5540d09 and official Maps research 4b94a29. Rechecked current worktree/history and Aaron checklist. No live activation or missing team approvals have become available.

- TDD: observed all 60 initial adapter tests failing against empty implementation; then passed. Independent review found no important defects and verified the REST request against the official reference. Added its exact transit-window and fractional 48h boundary coverage; 62 adapter tests now pass.
- Final suite initially exposed two transient 15s child-process import timeouts in existing no-live-export tests. Focused rerun passed; measured normal SDK import cost ~1.1s. Added a failing assertion that unsafe environments must not load SDKs/fixtures, then moved those imports inside the existing emulator guard. Ruling: avoid unnecessary SDK loads outside the only supported runtime rather than increase test timeouts. Reviewer found no regression in this change.
- Latest full backend verification: 8 suites / 169 tests pass (1.326s), all syntax checks pass. Real emulator regression is recorded below after completion. Android sources/configuration unchanged in this batch.
- Read-only GitHub audit: connected tool returned404 for Actions; CLI used the existing Git credential in memory without printing/persisting it. Main push run40368773164 and PR run40368601798 both report failure; Android setup attempts retired SDK package tools. Branch-protection query returned404, so its status is not proven. CI repair is the next batch.

- Real emulator regression after conditional imports: explicit demo-jiran Auth/Firestore/Functions/Storage run exited0; all prior auth/rules/diagnostic/commute/match checks passed. Functions discovery and SDK initialization remain functional inside the safe local guard. No Maps transport, live Firebase resource or paid API used. Source/secret scans and staged whitespace checks passed before commit.
