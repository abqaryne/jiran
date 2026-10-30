# Aaron handoff and P0 freeze checklist

**Needs human review.** Prepared27 October2026; this is a checklist for the
13 December freeze, not a declaration that the freeze or QA has happened.
The live budget is free-only. No team messages or Trello entries were sent.

| Item | Evidence / next action | Owner / status |
|---|---|---|
| Reviewable work | [Draft PR2](https://github.com/abqaryne/jiran/pull/2); Java Android and JavaScript retained | Aaron reviews backend; Ary approves/merges |
| Calculation contracts | [API contracts](../../engineering/api-contracts.md), versioned fixtures, typed Java parsers | Implemented local foundation; team contract acceptance pending |
| Source values | [Commute inputs/five probes](../../research/commute-input-evidence.md), [emissions](../../research/emission-methodology.md), [transit snapshot](../../research/free-transit-evidence.md), [routing research](../../research/no-billing-production.md) | Aaron research; Aly confirms survey assumptions; Dafi confirms shuttle evidence |
| Real data access | Listings, rooms, profiles, timetable, private candidates and Connection privacy | Gathan/Ary; agreed schemas and authorized readers pending |
| Match weights | Versioned formula and missing-data behavior implemented | Aly/Ary approve survey mapping; Ilham reviews explanation input |
| Free backend choice | Isolated Worker/token/quota experiments; existing Firebase Auth/Firestore | Team approval required; no runtime migration or billing activation |
| Transit processor | [Offline GTFS normalization and probes](../../engineering/transit-processing.md);62 focused tests | Independent follow-up review passed; live coverage and physical transfers remain open |
| Product UI | Local commute/match previews and immutable response models | Ary integrates final screens; fixtures remain labelled |
| NFR1 | PRD specifies p95 below3s at100 users; AGENTS also specifies50-user latency and100-user operation | Aaron prepares both scenarios; Aly runs/reviews complete production load evidence |
| NFR2 | Local connection-rule checks pass; UM affiliation and production collections remain open | Gathan defines rules; Aly verifies denied accesses |
| Reports/video | [350/400-word drafts and90-second script](report-drafts.md) | Aaron revises/explains; other owners supply their reviewed segments |
| Personal work record | [Reported6 hours on26 October](work-log.md) | Draft retained at Aaron's request; actual activity notes remain TODO; no invented sessions or posting |
| AI disclosure | [Aaron AI log](../../ai-log/aaron.md), earlier [usage record](../../ai-log/aaron.md) | Aaron completes human-change and understanding fields |

Before freezing P0, check every gate against the final commit, not an older green
run. A local fixture demonstration does not satisfy a production acceptance gate.

- [ ] Owners approve API/schema, source assumptions, formula and runtime decisions.
- [ ] Final product flows work with authorized data and correct empty/error states.
- [ ] Fresh CI passes Android lint, JVM tests, Checkstyle, debug build, backend
  tests and experiment checks at the PR head. Record the actual run URL and SHA.
- [ ] Device integration and QA checks cover the final app; attach observed output.
- [ ] Real route/fare/shuttle coverage and NFR1/NFR2 evidence meet the PRD.
- [ ] No configuration keys, service accounts, private tokens or real student data
  enter the commit or recording. Confirm generated artifacts remain ignored.
- [ ] Aaron resolves backend review findings; Gathan reviews schema/rules; Aly
  records QA; Ary performs the final approval and merge.
- [ ] Actual hours are logged on the correct Trello cards, AI log fields are
  completed, and report/video wording matches verified behavior.

Current verification belongs in the [solo ledger](../../plans/2026-10-27-aaron-solo-work.md).
Outstanding team decisions do not prevent independent transit processing, input
research, load preparation or personal review of the code already implemented.
