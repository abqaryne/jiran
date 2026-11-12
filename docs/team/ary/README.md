# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own

**11 Nov 2026:** [commute clarification and file handoff](../../plans/2026-11-11-commute-per-trip.md): remove the profile campus-day requirement and true-monthly-cost UI; build per-trip guidance in E4.2, with basic transit instructions in P0 and full map/scenario view in E4.4 (P1).
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance* and *Not in scope*. These are final: if something is unclear, settle it with Ary and write the answer into *Constraints* or as a subtask. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

### Do in this order (stories you lead: 18)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E0.3** Design system and app shell | [E0.3.md](../../../TASKS/E0/E0.3.md) | P0 | 15 Nov | - |
| 2 | **E1.1** Sign up with a UM email and verify it | [E1.1.md](../../../TASKS/E1/E1.1.md) | FR1.1, P0 | 19 Nov | UM domain list |
| 3 | **E1.2** Log in, log out and reset password | [E1.2.md](../../../TASKS/E1/E1.2.md) | FR1.2, P0 | 20 Nov | E1.1 |
| 4 | **E1.3** Profile setup and privacy | [E1.3.md](../../../TASKS/E1/E1.3.md) | FR1.3, P0 | 22 Nov | `users` fields (E0.5) |
| 5 | **E1.4** Home and the four tabs after sign-in | [E1.4.md](../../../TASKS/E1/E1.4.md) | P0 | 22 Nov | E1.2 |
| 6 | **E4.1** Room details screen | [E4.1.md](../../../TASKS/E4/E4.1.md) | FR2.2, P0 | 4 Dec | E3.1 |
| 7 | **E4.2** Commute options card | [E4.2.md](../../../TASKS/E4/E4.2.md) | FR4.3, P0 | 7 Dec | E3.3 (use the stub until it lands) |
| 8 | **E4.3** Send a request to the Host | [E4.3.md](../../../TASKS/E4/E4.3.md) | FR3.3, P0 | 7 Dec | E5.1 |
| 9 | **E5.1** Requests inbox and Connection | [E5.1.md](../../../TASKS/E5/E5.1.md) | FR3.3, P0 | 7 Dec | E1.3 |
| 10 | **E6.1** Compatibility questions | [E6.1.md](../../../TASKS/E6/E6.1.md) | FR3.1, P0 | 8 Dec | E1.3 |
| 11 | **E5.2** Chat and viewing | [E5.2.md](../../../TASKS/E5/E5.2.md) | FR3.3, P0 | 9 Dec | E5.1 |
| 12 | **E5.3** Move-in confirmation | [E5.3.md](../../../TASKS/E5/E5.3.md) | FR3.3, FR5.1, P0 | 9 Dec | E5.2 |
| 13 | **E6.4** Match list, match profile and connect | [E6.4.md](../../../TASKS/E6/E6.4.md) | FR3.2, FR3.3, P0 | 11 Dec | E6.2 (fake scores meanwhile), E5.1 |
| 14 | **E10.1** Profile and privacy settings screens | [E10.1.md](../../../TASKS/E10/E10.1.md) | FR1.3, P1 | 18 Dec | E1.3 |
| 15 | **E9.2** Group, split options and member status | [E9.2.md](../../../TASKS/E9/E9.2.md) | FR3.3, P1 | 19 Dec | E5.2 |
| 16 | **E10.2** Polish: empty states, errors, accessibility | [E10.2.md](../../../TASKS/E10/E10.2.md) | P1 | 20 Dec | all P0 flows |
| 17 | **E9.3** Compare units | [E9.3.md](../../../TASKS/E9/E9.3.md) | FR3.3, P1 | 20 Dec | E9.1, E9.2 |
| 18 | **T3.1** Script and screen captures | [T3.1.md](../../../TASKS/T3/T3.1.md) | P0 | 20 Dec | - |

### Stories where you do a subtask (10)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../../../TASKS/E0/E0.4.md) | 30 Oct |
| **E0.1** Repo and CI are healthy | Aaron | Add everyone as collaborators on GitHub; Turn on branch protection: PR required, final check by Ary | [E0.1.md](../../../TASKS/E0/E0.1.md) | 2 Nov |
| **T1.1** Survey and interviews | Aly | Review the survey wording so the questions match the screens; Share the survey link in your circles | [T1.1.md](../../../TASKS/T1/T1.1.md) | 8 Nov (launch 31 Oct) |
| **E5.4** Notifications | Gathan | Notifications screen | [E5.4.md](../../../TASKS/E5/E5.4.md) | 13 Dec |
| **E4.4** Public transport route page | Aaron | Public transport route page with the MapLibre map and timeline | [E4.4.md](../../../TASKS/E4/E4.4.md) | 14 Dec |
| **E9.1** Evaluate any unit | Gathan | Evaluate form and result screens | [E9.1.md](../../../TASKS/E9/E9.1.md) | 17 Dec |
| **T3.2** Record, edit and final cut | Aly | Check against the rubric | [T3.2.md](../../../TASKS/T3/T3.2.md) | 2 Jan |
| **T4.1** Report sections | Dafi | 1 Introduction (300 words), UI and colours part of 3 | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

## Hand-offs
| You wait for | From | Use meanwhile |
|---|---|---|
| Field names for users and listings | Gathan (by 15 Nov) | Mock data |
| Commute API response | Aaron (by 15 Nov, contract) | Stub JSON |
| Match score and reasons | Aaron and Ilham | Fake scores |

## Your PR check-in routine
1. CI is green and the PR links a ClickUp task.
2. Backend or rules PRs already have Aaron's or Gathan's review.
3. UI follows the design system: contrast 4.5:1, touch targets at least 48 dp, status shown with words.
4. Merge, and tell the owner so QA can pick it up.

If PRs queue for more than a day, let Aaron merge backend-only PRs.

## Your parts in the graded deliverables
- **Video:** segment 1 overview (1:30), your part of the demo (sign-up, profile, roommate matching), segment 3 design and usability (1:00)
- **Report:** section 1 Introduction (300 words), the UI and colours part of section 3, final review
- **Risk:** you are the bottleneck for screens and merges. If the load is too heavy, hand the Module 3 screens to Ilham or Gathan.

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
