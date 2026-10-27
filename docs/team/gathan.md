# Gathan: Backend and Database Developer

## You own
- **Module 2** Housing listings and area guide (FR2.1, FR2.2, FR2.3, FR2.4)
- The Firestore schema and **Security Rules (NFR2)** for all modules
- Storage rules, seed data, and FCM notifications

Your schema and rules are the first thing others wait for, so they land first.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Work is organised as flows (epics E0 to E10, done in order) plus parallel tracks (T1 to T4). Full list with every subtask: [trello-cards.md](trello-cards.md).

**Stories you lead** (12)

| Story | FR, priority | Due | Waits for |
|---|---|---|---|
| **E0.5** Data schema and Security Rules | P0 | draft 8 Nov, merged 15 Nov |  |
| **E2.1** Pick the building and enter rent and deposit | FR2.1, P0 | 25 Nov | E0.5, E1.3 |
| **E2.2** Rooms and prices with the price rule | FR2.1, P0 | 27 Nov | E2.1 |
| **E2.3** My listing: renew, requests and mark occupied | FR2.1, P0 | 29 Nov | E2.2 |
| **E2.4** Fictional seed listings *(can start early)* | P0 | 29 Nov | E0.5 |
| **E3.1** Rooms screen and Room cards | FR2.2, P0 | 2 Dec | E2.4 |
| **E3.2** Filters | FR2.2, P0 | 3 Dec | E3.1 |
| **E5.4** Notifications | P1 | 13 Dec | E5.1 |
| **E7.2** Household data and rules | FR5.1, P0 | 8 Dec | E0.5 |
| **E9.1** Evaluate any unit | FR2.4, P1 | 17 Dec | E3.4 |
| **E9.4** Area page and building reviews | FR2.3, P1 | 20 Dec | E2, E3 |
| **T2.3** NFR2: privacy and security evidence | P0 | 27 Dec | all rules |

**Stories where you do a subtask** (14): open the story card to see yours.

| Story | Lead | Due |
|---|---|---|
| **E0.4** Project management and docs | Dafi | 30 Oct |
| **E1.3** Profile setup and privacy | Ary | 22 Nov |
| **E3.4** True monthly cost and travel time on the Room card | Aaron | 4 Dec |
| **E4.3** Send a request to the Host | Ary | 7 Dec |
| **E5.1** Requests inbox and Connection | Ary | 7 Dec |
| **E5.2** Chat and viewing | Ary | 9 Dec |
| **E6.1** Compatibility questions | Ary | 8 Dec |
| **E8.3** Upload and result screens | Ilham | 20 Dec |
| **E9.2** Group, split options and member status | Ary | 19 Dec |
| **T1.1** Survey and interviews | Aly | 8 Nov (launch 31 Oct) |
| **T1.3** Verify the assumed numbers | Dafi | 13 Nov |
| **T3.1** Script and screen captures | Ary | 20 Dec |
| **T4.1** Report sections | Dafi | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | 9 Jan |

Your schema and rules (E0.5) land first because everyone waits for the field names.

## Privacy checklist (NFR2 and PDPA 2010)
- Only verified UM accounts can read anything
- Exact address, phone number and contract uploads visible only to the owner
- Contact details only after both sides accept
- Collect only what each feature needs; consent at sign-up

## Your parts in the graded deliverables
- **Video:** the posting and search part of the demo, segment 5 NFR2 evidence (rules tests, all denied)
- **Report:** section 2c Non-functional requirements (150 words), the database schema part of section 3, the rules tests in section 5

## Numbers you find or use (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- **#1 Room price weights** and **#7 Utilities estimate:** Aly finds them, you put them in config and seed data (by 10 Nov)
- **#8 Data retention periods:** you own the reasoning against the PDPA 2010 Retention Principle, with Dafi (due 13 Nov)

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
