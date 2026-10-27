# Dafi: Project Manager

## You own
- **Module 5** Shared house tools, FR5.1: shared expenses, who owes whom, rent split by room price (P0). Aly builds FR5.2 and FR5.3 with you.
- The project plan: Trello, sprints, meetings, deadlines, risks
- The PRD and requirements, report assembly, evidence for the peer evaluation

You must also write real code (the brief says nobody does only documentation): the balance logic and the expense screens.

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] Trello board with columns: Backlog, Ready, In progress, In review, QA, Done
- [ ] One card per FR (18 cards), labelled P0, P1 or P2, with an owner and an hours field
- [ ] Sprint 1 plan (one-week sprints), plus a weekly sync time and a place for minutes
- [ ] Send **one short email to UM transport** asking for the UM shuttle stops and timetable (the commute card needs it). No formal meetings with UMSU, BHEP or UMSDC: the brief only gives extra credit for them, so skip. Optional if you have spare time: one short email each to BHEP and UMSDC, and keep any reply for the report.
- [ ] Keep docs/product/PRD.md up to date as decisions change
- [ ] Post a weekly hours summary in the group chat

## Next
**Step 1 (2 to 15 Nov):** run the requirements freeze. Combine the survey findings with the PRD and make a table "finding, FR it shaped". Confirm P0 and P1 with Ary.

**Wave A (16 to 29 Nov): needs nobody**
- Write the balance logic in plain Java: `Expense`, `Household`, `computeBalances()`, a settle-up that minimises the number of transfers
- Rent is split by each room's price; utilities and other expenses equally unless a custom split is chosen
- Unit tests: balances sum to zero, rounding, 2 and 4 members, a member who paid nothing

**Wave B (30 Nov to 13 Dec)**
- Unit tab screens: add expense, balances, mark as settled (waits for Gathan's `households` and `expenses` data, and Ary's shared components)

## Your PM routine
- Daily: check Trello for blocked cards
- Twice a week: short sync
- Weekly: demo and retro on the weekend, update risks
- Gates: 8 Nov (survey 100+, repo and Firebase running), 15 Nov (requirements frozen), 13 Dec (P0 code freeze), 27 Dec (testing done)
- Start drafting the report as soon as each owner finishes their part

## Your parts in the graded deliverables
- **Video:** the expenses part of the demo, segment 7 team contributions (30 s with Trello hours and commit summary), segment 8 closing (0:45)
- **Report:** section 2b Functional requirements (compile all 18), section 7 Team roles and contributions, section 8 References and the final style pass. Watch the **2,500 word** limit: put the FRs in tables.
- **Evidence:** weekly screenshot of Trello and the Git log per person

## Numbers you find (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- In your email to UM transport, also ask: shuttle stops and times, whether it is free, and the **parking fee** at UM (#4, #5; due 13 Nov)
- **#8 Data retention periods:** help Gathan, and ask the lecturer if still unsure (due 13 Nov)
- Check every row in the file has an owner and is on track at each weekly sync

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
