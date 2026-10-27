# Dafi: Project Manager

## You own
- **Module 5** Shared house tools, FR5.1: shared expenses, who owes whom, rent split by room price (P0). Aly builds FR5.2 and FR5.3 with you.
- The project plan: Trello, sprints, meetings, deadlines, risks
- The PRD and requirements, report assembly, evidence for the peer evaluation

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Work is organised as flows (epics E0 to E10, done in order) plus parallel tracks (T1 to T4). Full list with every subtask: [trello-cards.md](trello-cards.md).

**Stories you lead** (8)

| Story | FR, priority | Due | Waits for |
|---|---|---|---|
| **E0.4** Project management and docs | P0 | 30 Oct |  |
| **E7.1** Balance and settle-up logic *(can start early)* | FR5.1, P0 | start now, due 29 Nov |  |
| **E7.3** Expenses and balances screens | FR5.1, P0 | 12 Dec | E7.1, E7.2 |
| **E7.4** Move out | FR5.1, P0 | 13 Dec | E7.3 |
| **T1.3** Verify the assumed numbers | P0 | 13 Nov | T1.1 |
| **T1.4** UM shuttle timetable and commute estimates | P0 | 8 Nov (timetable), 13 Nov (estimates) |  |
| **T1.5** Requirements freeze | P0 | 15 Nov | T1.2 |
| **T4.1** Report sections | P0 | 9 Jan | each owner's part |

**Stories where you do a subtask** (7): open the story card to see yours.

| Story | Lead | Due |
|---|---|---|
| **E1.1** Sign up with a UM email and verify it | Ary | 19 Nov |
| **E5.3** Move-in confirmation | Ary | 9 Dec |
| **E7.2** Household data and rules | Gathan | 8 Dec |
| **T1.1** Survey and interviews | Aly | 8 Nov (launch 31 Oct) |
| **T1.2** Survey analysis | Aly | 15 Nov |
| **T3.1** Script and screen captures | Ary | 20 Dec |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | 9 Jan |

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens in E7.

## Your PM routine
- Daily: check Trello for blocked cards
- Twice a week: short sync
- Weekly: demo and retro on the weekend, update risks
- Gates: 8 Nov (survey 100+, repo and Firebase running), 15 Nov (requirements frozen), 13 Dec (P0 code freeze), 27 Dec (testing done)
- Start drafting the report as soon as each owner finishes their part

## Your parts in the graded deliverables
- **Video:** the expenses part of the demo, segment 7 team contributions (30 s with Trello hours and commit summary), segment 8 closing (0:45)
- **Report:** section 2b Functional requirements (compile all 19), section 7 Team roles and contributions, section 8 References and the final style pass. Watch the **2,500 word** limit: put the FRs in tables.
- **Evidence:** weekly screenshot of Trello and the Git log per person

## Numbers you find (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- **#4 Car cost (parking) and #5 Public transport (UM shuttle):** the shuttle timetable and whether it is free, plus the parking fee at UM (due 13 Nov)
- **#8 Data retention periods:** help Gathan, and ask the lecturer if still unsure (due 13 Nov)
- Check every row in the file has an owner and is on track at each weekly sync

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
