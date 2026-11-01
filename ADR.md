# Architecture Decision Records

Append-only log of decisions for Jiran that are hard to reverse or would surprise a future reader. Each entry records the
context, the decision and the consequences, not only the outcome, so the team can tell whether the reasoning still holds.

Add a new entry at the bottom with the next number. Never edit or delete the decision of a past entry. If it is replaced,
add a new entry and mark the old one `Superseded` with a link forward. Task files in `TASKS/` cite these numbers.

Status values: `Proposed`, `Accepted`, `Superseded`, `Deprecated`.

Template:

```
## ADR-000X: <short title>

- **Status:** Accepted
- **Date:** YYYY-MM-DD
- **Decided by:** name

### Context
What prompted this decision and what constraints applied.

### Decision
What was decided.

### Consequences
What this makes easier or harder, and which alternatives lost and why.
```

---

## ADR-0001: Native Android in Java; server code in JavaScript

- **Status:** Accepted
- **Date:** 2026-10-25
- **Decided by:** Ary, with the team

### Context
The first plan used another mobile stack. The course asks for a mobile application and the team's Android work is graded on
the code the members can explain.

### Decision
The app is native Android in Java with XML layouts and Material Components (MVVM). Server code is JavaScript (Node.js). No
Kotlin app code, Flutter or new backend.

### Consequences
Firebase does not support Java for server functions, so Aaron and Ilham write server code in JavaScript with Jest tests. The
rest of the app stays Java with JUnit tests. Stories E1 to E10 follow this stack.

## ADR-0002: The free plan only

- **Status:** Accepted
- **Date:** 2026-10-27
- **Decided by:** Ary (user confirmation of the budget)

### Context
The team has no budget. The first design used paid Google services (Routes API, Cloud Functions, Firebase Storage).

### Decision
Free plans only, with no billing account, upgrade or billable provider. Firebase Auth and Firestore stay on Spark. The
server is a Cloudflare Worker on the free plan. Routing uses openrouteservice and the Prasarana GTFS timetables; the map is
MapLibre; photos are URLs from a free host.

### Consequences
No live traffic data, so peak times are an assumption (E4.4). Firestore triggers are not available, so deletion and expiry
jobs need the Worker or a check on read (E2.3, E7.2). Evidence is in `docs/research/no-billing-production.md`,
`docs/research/maps-readiness.md` and `docs/research/free-transit-evidence.md`.

## ADR-0003: Money is stored as integer sen

- **Status:** Accepted
- **Date:** 2026-10-27
- **Decided by:** team (Aaron, Gathan)

### Context
Room price splits, expense balances and budget comparisons must never create or lose money through floating-point rounding.

### Decision
Every amount is a safe integer in sen (45000 = RM450.00). Field names end in `Sen` (`totalRentSen`, `roomPriceSen`,
`utilitiesShareSen`). Percentages and weights that need fractions are integer basis points (10000 = 100%).

### Consequences
Screens format sen as ringgit. Rounding rules are written once per calculation (half-up for fares and scores, remainder to the
master room for price splits) and tested with fixed inputs.

## ADR-0004: The match score is computed by code, never by the AI

- **Status:** Accepted
- **Date:** 2026-10-27
- **Decided by:** Ary, Aaron

### Context
The course allows AI only where rules cannot do the job, and a score has to be transparent and testable. A score the AI can
change cannot be defended.

### Decision
`rankMatches` in `backend/functions/src/match.js` computes the score from seven factors with explicit versioned weights. The AI
only writes the reasons, with a rule-based fallback. The same holds for CO₂ and every commute figure.

### Consequences
Results are deterministic for identical inputs and unit-tested. Every AI output carries an AI-generated label and a fallback.
AI features each need a justification and an evaluation on sample data (E8.5).

## ADR-0005: The contract check is private

- **Status:** Accepted
- **Date:** 2026-10-27
- **Decided by:** Ary

### Context
A tenancy agreement is confidential. Showing a check on a Listing would reveal who is renting what to the public.

### Decision
The contract check never appears on a Listing, Room card or room details page. It opens only from a chat attachment (the Host
shares the agreement privately) or from the student's own Profile, and its result is visible only to the uploader.

### Consequences
E8.3 depends on the chat (E5.2). A conflict is open: the schema says the file is processed in memory and not retained, while
the PRD says uploads are deleted after 30 days.

## ADR-0006: Only UM student email addresses may register

- **Status:** Accepted
- **Date:** 2026-10-30
- **Decided by:** Ary

### Context
On 29 Oct 2026 the team tried the opposite rule: any email could register and a UM email earned a "UM verified" badge needed
to post a Listing, so first-year students without a UM email could join. Ary reversed it the next day: every account should be
a verified UM student, because housing scams are the main trust risk.

### Decision
Sign-up accepts only `siswa.um.edu.my`, confirmed through an email verification link. Staff mail (`um.edu.my`) is not
accepted because Jiran is for students. Security Rules apply the same baseline: a verified email on that exact domain.

### Consequences
First-year students without Siswa Mail cannot join until they have it (PRD open question 9). Postgraduate students may have
another domain, which is unconfirmed. Replaces the 29 Oct open sign-up (`a719dec`, reverted in `3b4c331`). Code: `UmEmailPolicy`.

## ADR-0007: A `connections` document is the proof of a Connection

- **Status:** Accepted (31 Oct 2026)
- **Date:** 2026-10-30, updated 2026-10-31
- **Decided by:** Ary (review of Gathan's schema, PR #8; on 31 Oct 2026 Ary chose that the Worker creates the document). Aaron owns the Worker and has not confirmed: not recorded

### Context
Security Rules cannot run queries. They can only `get()` documents on paths built from the request or the document. Contact
details, the phone number and chat must open only when two people are connected.

### Decision
One document `connections/{pairId}` per pair, where `pairId` is the two Auth UIDs sorted and joined by `_`. A Host accepting a
listing request, or two students both accepting a match, produces it. Rules read it to open `contact`, `phone` and chat.

Only the Worker creates a `connections` document, with admin access, after it checks that the Host accepted the listing request, or
that both `statusA` and `statusB` of the match are accepted. Security Rules deny the app create and delete on `connections`.

### Consequences
Chat retention is counted from `connections.endedAt`. Firestore triggers are not on the free plan, so the accept action calls a
Worker endpoint instead of writing the document. Why not the app: a Rules check on the accepted request is easy to get wrong, and a
wrong rule would open a phone number or a chat to the wrong person (PDPA 2010, NFR2). Cost: the accept action needs that
endpoint (E5.1, Aaron) and the Worker must be reachable for a Connection to start. Who may end a Connection (set `ended`) and
which fields they may change stays as in the schema draft; Gathan writes those Rules and their emulator tests.

## ADR-0008: Task tracking lives in the repository as well as in ClickUp

- **Status:** Accepted
- **Date:** 2026-10-30
- **Decided by:** Ary

### Context
Task titles in ClickUp were one line, so owners and their AI assistants had no context, and the course asks for evidence of
who did what. A master `TASK.md` with a file per task, as used in other projects the team knows, works well for this.

### Decision
`TASK.md` at the root is the master list (milestones and tasks with status and category). `TASKS/<milestone>/<ID>.md` holds
the full detail of each task (description, work needed, constraints, acceptance, resolution, notes). ClickUp stays the board
and mirrors the statuses. IDs stay `E<epic>.<n>` and `T<n>.<n>` because they already appear in commits, AI logs and ClickUp.

### Consequences
Status is written in two places and must be changed in both on the same day. Open questions in each task file have an owner. Subtasks are in the task files (`work-breakdown.md` was removed on 31 Oct 2026).

**Update 2026-10-31 (Ary):** the status in `TASK.md` and the task files is set from the repository (what is merged in `main`,
on a branch or in an open PR, and which *Acceptance* checks are ticked), not copied from ClickUp. ClickUp keeps planning
states and logged hours and may show a different status. The statuses were re-derived on 31 Oct 2026 under these rules.
