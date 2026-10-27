# Aly: QA Engineer and UX Researcher

## You own
- **Module 5** FR5.2 chore rotation and FR5.3 house rules (with Dafi, both P1)
- The survey, interviews and user study
- The test plan, integration tests, the load test (NFR1), and the usability test
- The release checklist and the video editing

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Epics E7 and E8 are yours, plus E5.2 and E5.3 with Dafi and the video editing in E9.2.

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E7.1** Survey and interviews: put [SURVEY.md](../product/SURVEY.md) into a Google Form (English and Malay), pilot with 5 people and time it (8 minutes or less), launch, reach **100+ responses** across faculties and areas (not only FSKTM; ask Telegram admins for permission), plan 5 to 8 interviews (20 minutes) and a consent form, review the wording with Ary | P0 | launch 31 Oct, 100+ responses 8 Nov | none |
| **E8.1** Test plan: one test case per FR, plus the NFR1 and NFR2 tests; Espresso scaffolding | P0 | 15 Nov (plan), 29 Nov (scaffolding) | none |
| **E7.2** Survey analysis: room prices to Gathan, peak hours and fares to Aaron, feature importance (H1) to Dafi, the "finding, FR it shaped" table with Dafi | P0 | 15 Nov | E7.1 |
| **E7.3** (part) Numbers: #1 room price weights (C5 to C7) and #7 utilities (C9) by 10 Nov, #2 peak hours (E5, E6) by 12 Nov, #3 e-hailing fares (E7 plus a shared sheet where everyone logs Grab estimates for about 10 routes) and #9 match weights (F2) by 13 Nov | P0 | 10 to 13 Nov | E7.1 |
| **E5.2** Recurring chores (FR5.2): rotation logic in plain Java with JUnit, then the screens and reminders | P1 | 13 Dec | E5.1; FCM from Gathan |
| **E5.3** House rules (FR5.3), with Dafi: the rules model and screen; changing a rule resets everyone's acceptance | P1 | 13 Dec | E5.1 |
| **E8.2** NFR1 load test: the k6 script in `backend/tests/load`, 100 concurrent users, 95% under 3 s | P0 | 27 Dec | working search |
| **E8.4** Usability test: 8 to 10 UM students, five tasks, SUS at least 68 and 80% task success | P0 | 27 Dec | working app |
| **E8.5** Release check: integration tests for each main flow, no P0 bug left before recording | P0 | 27 Dec | all P0 stories |
| **E9.2** Record, assemble and edit the video; Ary checks it against the rubric | P0 | 28 Dec to 2 Jan | everyone's captures |

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

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
