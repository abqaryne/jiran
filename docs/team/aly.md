# Aly: QA Engineer and UX Researcher

## You own
- **Module 5** FR5.2 chore rotation and FR5.3 house rules (with Dafi, both P1)
- The survey, interviews and user study
- The test plan, integration tests, the load test (NFR1), and the usability test
- The release checklist and the video editing

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Work is organised as flows (epics E0 to E10, done in order) plus parallel tracks (T1 to T4). Full list with every subtask: [trello-cards.md](trello-cards.md).

**Stories you lead** (9)

| Story | FR, priority | Due | Waits for |
|---|---|---|---|
| **E7.5** Recurring chores *(can start early)* | FR5.2, P1 | 13 Dec | E7.2 (logic can start now) |
| **E7.6** House rules | FR5.3, P1 | 13 Dec | E7.2 |
| **T1.1** Survey and interviews | P0 | 8 Nov (launch 31 Oct) |  |
| **T1.2** Survey analysis | P0 | 15 Nov | T1.1 |
| **T2.1** Test plan and integration tests | P0 | 15 Nov (plan), 27 Dec (run) |  |
| **T2.2** NFR1: speed under load | P0 | 27 Dec | working search |
| **T2.4** Usability test | P0 | 27 Dec | working app |
| **T2.5** Release check | P0 | 27 Dec | all P0 flows |
| **T3.2** Record, edit and final cut | P0 | 2 Jan | T3.1 |

**Stories where you do a subtask** (5): open the story card to see yours.

| Story | Lead | Due |
|---|---|---|
| **E0.4** Project management and docs | Dafi | 30 Oct |
| **T1.3** Verify the assumed numbers | Dafi | 13 Nov |
| **T3.1** Script and screen captures | Ary | 20 Dec |
| **T4.1** Report sections | Dafi | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | 9 Jan |

You also record, assemble and edit the video (T3.2); Ary reviews it against the rubric.

## Your parts in the graded deliverables
- **Video:** the chores and house rules part of the demo, segment 5 NFR1 results, segment 6 survey and interview findings. You also record, assemble and edit the video (Ary reviews against the rubric).
- **Report:** section 2a User study findings (350 words), section 5 Testing and validation (300 words)
- **Keep as evidence:** anonymised survey results and charts, interview notes and quotes (with consent), test results, SUS scores, screenshots

## Numbers you find (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- **#1 Room price weights** from survey C5 to C7 (due 10 Nov)
- **#7 Utilities estimate** from survey C9 (due 10 Nov)
- **#2 Peak hours** from survey E5 and E6 (due 12 Nov)
- **#3 E-hailing fares:** survey E7 plus a shared sheet where every team member logs Grab fare estimates for about 10 real routes, peak and off-peak (due 13 Nov)
- **#9 Match weights** from survey F2 (due 13 Nov)

## Rules for everyone

- Log your actual hours on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the Trello card (the card shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
