# Aly: QA Engineer and UX Researcher

## You own
- **Module 5** FR5.2 chore rotation and FR5.3 house rules (with Dafi, both P1)
- The survey, interviews and user study
- The test plan, integration tests, the load test (NFR1), and the usability test
- The release checklist and the video editing

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance*, *Not in scope* and *Open questions*. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

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
| **T1.3** Verify the assumed numbers | Dafi | #1 Room price weights, #7 Utilities estimate · 10 Nov; #2 Peak hours · 12 Nov; #3 E-hailing fares, #9 Match score weights · 13 Nov | [T1.3.md](../../../TASKS/T1/T1.3.md) | 13 Nov |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; NFR verification (1:30); Stakeholder validation (1:00) | [T3.1.md](../../../TASKS/T3/T3.1.md) | 20 Dec |
| **T4.1** Report sections | Dafi | 2a User study findings (350), 5 Testing and validation (300) | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

### Open questions waiting for you (10)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E1.1](../../../TASKS/E1/E1.1.md): Ary and Aly: real device and real inbox test before the video.
- [E1.2](../../../TASKS/E1/E1.2.md): Ary and Aly: test the real email and "too many attempts" behaviour against the live project before the video.
- [E2.2](../../../TASKS/E2/E2.2.md): Aly and Gathan: survey check of the weights (T1.3 #1, results 13 Nov). Until then the app and the report call 1.3 / 1.0 / 0.7 an estimate.
- [E3.4](../../../TASKS/E3/E3.4.md): Aly: the utilities estimate (T1.3 #7, survey results 13 Nov). Until then the card shows RM 62 labelled as an estimate.
- [E4.4](../../../TASKS/E4/E4.4.md): Aly and Aaron: the peak hours and factor (T1.3 #2, survey results 13 Nov). Until then the peak column is not shown (P1).
- [E6.1](../../../TASKS/E6/E6.1.md): Ary and Aly: review the survey wording so the questions match (T1.1).
- [E6.2](../../../TASKS/E6/E6.2.md): Aaron and Aly: the final weights from the survey (results 13 Nov). Blocks: claiming the score as validated.
- [E10.2](../../../TASKS/E10/E10.2.md): Ary and Aly: find one Malay reviewer (a Malaysian classmate) for the BM text. Blocks: the BM check.
- [T2.1](../../../TASKS/T2/T2.1.md): Aly: the plan by 15 Nov.
- [T4.2](../../../TASKS/T4/T4.2.md): Everyone: fill the TODO fields before merging into the appendix.

### Decided on 31 Oct 2026 for your stories (10)

Read these before you start; the full text is under *Open questions* in each task file.

- [E0.3](../../../TASKS/E0/E0.3.md): Contrast computed with the WCAG formula from `colors.xml`: every text pair is at least 4.5:1 (lowest: success text on success background 4.75:1; secondary text on fill 5.53:1; white on brand 6.38:1), and the strong border is 3.42:1 against white (non-text needs 3:1). Computed from the hex values, not measured on a device screen.
- [E2.4](../../../TASKS/E2/E2.4.md): Reference prices: hand-noted price facts from public advertisements (area, room type, price range, source and date), as the rules in `ASSUMPTIONS.md` allow, checked against the survey (T1.2). Gathan notes them, Aly checks.
- [E7.5](../../../TASKS/E7/E7.5.md): A member who joins mid-cycle shares only expenses dated on or after the day they join, and enters the chore rotation from the next cycle. Needs review by Aly.
- [E7.6](../../../TASKS/E7/E7.6.md): Starter rules (editable examples): "Quiet hours from 11 pm to 7 am", "Tell the Household before a guest stays overnight", "Clean the kitchen after you cook".
- [E8.2](../../../TASKS/E8/E8.2.md): No real contracts. The team writes 10 fictional tenancy agreements from common clauses (deposit, notice, repairs, utilities), so nothing needs anonymising. Ilham writes them, Aly checks.
- [E8.5](../../../TASKS/E8/E8.5.md): At least 5 UM students outside the team, recruited by Aly with consent, each rate 10 reasons on a 1 to 5 scale.
- [T1.1](../../../TASKS/T1/T1.1.md): The survey is out and stays as it is. Gender is asked in the app at registration (E1.3), so the survey needs no new question.
- [T1.2](../../../TASKS/T1/T1.2.md): Analysis tool: Google Sheets.
- [T2.2](../../../TASKS/T2/T2.2.md): Test data for the load test: 100 fictional accounts and 50 fictional listings, separate from the 10 to 15 demo listings, on the emulator or test data only.
- [T2.4](../../../TASKS/T2/T2.4.md): Testers use fictional accounts the team creates (no real names or phone numbers) and agree to a one-paragraph consent: purpose, what is recorded, and that they can stop at any time. No video of a tester without their consent.

You also record, assemble and edit the video (T3.2); Ary reviews it against the rubric.

## Your parts in the graded deliverables
- **Video:** the chores and house rules part of the demo, segment 5 NFR1 results, segment 6 survey and interview findings. You also record, assemble and edit the video (Ary reviews against the rubric).
- **Report:** section 2a User study findings (350 words), section 5 Testing and validation (300 words)
- **Keep as evidence:** anonymised survey results and charts, interview notes and quotes (with consent), test results, SUS scores, screenshots

## Numbers you find (see [ASSUMPTIONS.md](../../product/ASSUMPTIONS.md))
- **#1 Room price weights** from survey C5 to C7 (due 10 Nov)
- **#7 Utilities estimate** from survey C9 (due 10 Nov)
- **#2 Peak hours** from survey E5 and E6 (due 12 Nov)
- **#3 E-hailing fares:** survey E7 plus a shared sheet where every team member logs Grab fare estimates for about 10 real routes, peak and off-peak (due 13 Nov)
- **#9 Match weights** from survey F2 (due 13 Nov)

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
