# Dafi: Project Manager

## You own
- **Module 5** Shared house tools, FR5.1: shared expenses, who owes whom, rent split by room price (P0). Aly builds FR5.2 and FR5.3 with you.
- The project plan: ClickUp, sprints, meetings, deadlines, risks
- The PRD and requirements, report assembly, evidence for the peer evaluation

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens.

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance*, *Not in scope* and *Open questions*. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

### Do in this order (stories you lead: 8)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E0.4** Project management and docs | [E0.4.md](../../../TASKS/E0/E0.4.md) | P0 | 30 Oct | - |
| 2 | **T1.4** UM shuttle timetable and commute estimates | [T1.4.md](../../../TASKS/T1/T1.4.md) | P0 | 8 Nov (timetable), 13 Nov (estimates) | - |
| 3 | **T1.3** Verify the assumed numbers | [T1.3.md](../../../TASKS/T1/T1.3.md) | P0 | 13 Nov | T1.1 |
| 4 | **T1.5** Requirements freeze | [T1.5.md](../../../TASKS/T1/T1.5.md) | P0 | 15 Nov | T1.2 |
| 5 | **E7.1** Balance and settle-up logic | [E7.1.md](../../../TASKS/E7/E7.1.md) | FR5.1, P0 | start now, due 29 Nov | may start early |
| 6 | **E7.3** Expenses and balances screens | [E7.3.md](../../../TASKS/E7/E7.3.md) | FR5.1, P0 | 12 Dec | E7.1, E7.2 |
| 7 | **E7.4** Move out | [E7.4.md](../../../TASKS/E7/E7.4.md) | FR5.1, P0 | 13 Dec | E7.3 |
| 8 | **T4.1** Report sections | [T4.1.md](../../../TASKS/T4/T4.1.md) | P0 | 9 Jan | each owner's part |

### Stories where you do a subtask (7)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **T1.1** Survey and interviews | Aly | Share the survey link in your circles | [T1.1.md](../../../TASKS/T1/T1.1.md) | 8 Nov (launch 31 Oct) |
| **T1.2** Survey analysis | Aly | Table "finding, FR it shaped" | [T1.2.md](../../../TASKS/T1/T1.2.md) | 15 Nov |
| **E1.1** Sign up with a UM email and verify it | Ary | Decided 30 Oct 2026: UM email only. Only `siswa.um.edu.my` counts (students only, no staff mail). Still to confirm with UM or the lecturer: whether postgraduate students have another domain, and what first-year students without a UM email can do | [E1.1.md](../../../TASKS/E1/E1.1.md) | 19 Nov |
| **E7.2** Household data and rules | Gathan | Household created from a Group or by invite code (with E5.3) | [E7.2.md](../../../TASKS/E7/E7.2.md) | 8 Dec |
| **E5.3** Move-in confirmation | Ary | The Household is created with the same members (E7.2) | [E5.3.md](../../../TASKS/E5/E5.3.md) | 9 Dec |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; Team contributions (0:30) and closing summary (0:45) | [T3.1.md](../../../TASKS/T3/T3.1.md) | 20 Dec |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

### Open questions waiting for you (7)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E1.1](../../../TASKS/E1/E1.1.md): Dafi: does UM give postgraduate students a different domain, and when do new students receive Siswa Mail? Blocks: the domain list.
- [E3.3](../../../TASKS/E3/E3.3.md): Dafi: the shuttle timetable (T1.4). Blocks: the shuttle leg.
- [E4.2](../../../TASKS/E4/E4.2.md): Dafi: shuttle timetable (T1.4). Blocks: the shuttle row.
- [E10.1](../../../TASKS/E10/E10.1.md): Dafi: the PDPA wording for data retention (T1.3 #8). Draft until then: "We keep your data only as long as the feature needs it: contract check results 30 days, a closed Household 6 months, an unattached evaluation 90 days, a listing 30 days."
- [T1.3](../../../TASKS/T1/T1.3.md): Dafi: collect the results on 13 Nov. Blocks: freeze (T1.5).
- [T1.4](../../../TASKS/T1/T1.4.md): Dafi: email UM transport only if the timetable cannot be found.
- [T4.2](../../../TASKS/T4/T4.2.md): Everyone: fill the TODO fields before merging into the appendix.

### Decided on 31 Oct 2026 for your stories (10)

Read these before you start; the full text is under *Decisions taken* in each task file.

- [E0.4](../../../TASKS/E0/E0.4.md): Minutes live in `docs/team/meetings/YYYY-MM-DD.md`, one file per meeting: who attended, decisions, actions with owner and date. Dafi writes them.
- [E1.1](../../../TASKS/E1/E1.1.md): First-year students without a UM email wait until they receive it. The sign-up screen says so, and the report lists it as a limitation (PRD open question 9).
- [E1.4](../../../TASKS/E1/E1.4.md): Home for a student without a Household shows two cards, "Find a room" (Rooms tab) and "Find housemates" (matches), and a link "Join a Household with a code" (E5.3). No empty screen.
- [E5.3](../../../TASKS/E5/E5.3.md): Joining a Household: after a Connection with the Host, or with an invite code made by a member (6 characters, valid 7 days, one Household). The code covers students who already live together.
- [E6.4](../../../TASKS/E6/E6.4.md): Show the top 20 matches, no paging. The function still scores up to 100 candidates.
- [E7.1](../../../TASKS/E7/E7.1.md): Remainder in sen: everyone gets the total divided by the number of members, rounded down; the sen left over (fewer than the number of members) go one each to members in join order, earliest first. RM 100.00 for three people is 3334, 3333 and 3333 sen. Core logic: needs review by Dafi.
- [E7.3](../../../TASKS/E7/E7.3.md): Unit tab without a Household: "You are not in a Household yet" and two buttons, "Find a room" and "Join with a code".
- [E7.4](../../../TASKS/E7/E7.4.md): Wording for the leaver (Dafi may edit): "Leave this Household? Your share of expenses up to today stays in the record. Settle what you owe before you leave. You will lose access to the chat, chores and rules."
- [T1.5](../../../TASKS/T1/T1.5.md): Both decided: gender is required at registration with a user-chosen filter (E1.3), and first-year students wait for their Siswa Mail (E1.1).
- [T4.1](../../../TASKS/T4/T4.1.md): Final style pass: 7 Jan 2027 (the report is due at 12:00 on 10 Jan 2027).

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens in E7.

## Your PM routine
- Daily: check ClickUp for blocked tasks
- Twice a week: short sync
- Weekly: demo and retro on the weekend, update risks
- Gates: 8 Nov (survey 100+, repo and Firebase running), 15 Nov (requirements frozen), 13 Dec (P0 code freeze), 27 Dec (testing done)
- Start drafting the report as soon as each owner finishes their part

## Your parts in the graded deliverables
- **Video:** the expenses part of the demo, segment 7 team contributions (30 s with ClickUp hours and commit summary), segment 8 closing (0:45)
- **Report:** section 2b Functional requirements (compile all 19), section 7 Team roles and contributions, section 8 References and the final style pass. Watch the **2,500 word** limit: put the FRs in tables.
- **Evidence:** weekly screenshot of ClickUp and the Git log per person

## Numbers you find (see [ASSUMPTIONS.md](../../product/ASSUMPTIONS.md))
- **#4 Car cost (parking) and #5 Public transport (UM shuttle):** the shuttle timetable and whether it is free, plus the parking fee at UM (due 13 Nov)
- **#8 Data retention periods:** help Gathan, and ask the lecturer if still unsure (due 13 Nov)
- Check every row in the file has an owner and is on track at each weekly sync

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
