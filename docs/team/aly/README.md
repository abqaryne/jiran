# Aly: QA Engineer and UX Researcher

## You own
- **Module 5** FR5.2 chore rotation and FR5.3 house rules (with Dafi, both P1)
- The survey, interviews and user study
- The test plan, integration tests, the load test (NFR1), and the usability test
- The release checklist and the video editing

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance* and *Not in scope*. These are final: if something is unclear, settle it with Ary and write the answer into *Constraints* or as a subtask. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

### Do in this order (stories you lead: 9)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **T1.1** Survey and interviews | [T1.1.md](../../../TASKS/T1/T1.1.md) | P0 | 8 Nov (launch 31 Oct) | - |
| 2 | **T1.2** Survey analysis | [T1.2.md](../../../TASKS/T1/T1.2.md) | P0 | 15 Nov | T1.1 |
| 3 | **T2.1** Test plan and integration tests | [T2.1.md](../../../TASKS/T2/T2.1.md) | P0 | 15 Nov (plan), 27 Dec (run) | - |
| 4 | **E7.5** Recurring chores | [E7.5.md](../../../TASKS/E7/E7.5.md) | FR5.2, P1 | 13 Dec | may start early · E7.2 (logic can start now) |
| 5 | **E7.6** House rules | [E7.6.md](../../../TASKS/E7/E7.6.md) | FR5.3, P1 | 13 Dec | E7.2 |
| 6 | **T2.2** NFR1: speed under load | [T2.2.md](../../../TASKS/T2/T2.2.md) | P0 | 27 Dec | working search |
| 7 | **T2.4** Usability test | [T2.4.md](../../../TASKS/T2/T2.4.md) | P0 | 27 Dec | working app |
| 8 | **T2.5** Release check | [T2.5.md](../../../TASKS/T2/T2.5.md) | P0 | 27 Dec | all P0 flows |
| 9 | **T3.2** Record, edit and final cut | [T3.2.md](../../../TASKS/T3/T3.2.md) | P0 | 2 Jan | T3.1 |

### Stories where you do a subtask (5)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../../../TASKS/E0/E0.4.md) | 30 Oct |
| **T1.3** Verify the assumed numbers | Dafi | #1 Room price weights · 10 Nov; #3 E-hailing fares, #9 Match score weights · 13 Nov | [T1.3.md](../../../TASKS/T1/T1.3.md) | 13 Nov |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; NFR verification (1:30); Stakeholder validation (1:00) | [T3.1.md](../../../TASKS/T3/T3.1.md) | 20 Dec |
| **T4.1** Report sections | Dafi | 2a User study findings (350), 5 Testing and validation (300) | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

## Your parts in the graded deliverables
- **Video:** the chores and house rules part of the demo, segment 5 NFR1 results, segment 6 survey and interview findings. You also record, assemble and edit the video (Ary reviews against the rubric).
- **Report:** section 2a User study findings (350 words), section 5 Testing and validation (300 words)
- **Keep as evidence:** anonymised survey results and charts, interview notes and quotes (with consent), test results, SUS scores, screenshots

## Numbers you find (see [ASSUMPTIONS.md](../../product/ASSUMPTIONS.md))
- **#1 Room price weights** from survey C5 to C7 (due 10 Nov)
- ~~#7 Utilities estimate~~ dropped on 12 Nov 2026; survey C9 is for the report only
- **#3 E-hailing fares:** survey E7 plus a shared sheet where every team member logs Grab fare estimates for about 10 real routes (due 13 Nov)
- **#9 Match weights** from survey F2 (due 13 Nov)

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
