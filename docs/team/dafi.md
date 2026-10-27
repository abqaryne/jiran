# Dafi: Project Manager

## You own
- **Module 5** Shared house tools, FR5.1: shared expenses, who owes whom, rent split by room price (P0). Aly builds FR5.2 and FR5.3 with you.
- The project plan: Trello, sprints, meetings, deadlines, risks
- The PRD and requirements, report assembly, evidence for the peer evaluation

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Epics E0.4, E4.5, E5.1 and E10 are yours; E7.4 and a few numbers too.

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E0.4** Project management: build the board (initiative, epics, story cards, subtask checklists), sprint 1 plan, weekly sync, PRD upkeep, hours summary, start `docs/ai-log/dafi.md` | P0 | 30 Oct | none |
| **E4.5** UM shuttle timetable and commute estimates: find the timetable (a timetable is enough, no API or live tracking), email UM transport only if it cannot be found, then estimate commute time and cost with Aaron | P0 | 8 Nov (timetable), 13 Nov (estimates) | none |
| **E7.3** (part) Numbers #4 parking and #5 shuttle: the shuttle times and whether it is free, and the parking fee | P0 | 13 Nov | E4.5 |
| **E7.3** (part) #8 Data retention periods against PDPA 2010, with Gathan | P0 | 13 Nov | none |
| **E7.4** Requirements freeze: confirm P0 and P1 with Ary and freeze the PRD | P0 | 15 Nov | Aly's survey analysis (E7.2) |
| **E5.1** Expenses and balances (FR5.1): the balance and settle-up logic in plain Java with JUnit first, then the Unit tab screens | P0 | logic 29 Nov, screens 13 Dec | Gathan's household data; Ary's components |
| **E10** Report: assemble all sections, requirements table (19 FRs), team roles, references, final style pass | P0 | draft during the build; 4 to 9 Jan assembly | each owner's section |
| **E9.1** (part) Team contributions (0:30) and closing summary (0:45) for the video | P0 | 20 Dec | Trello hours, commit summary |

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens in E5.1.

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
