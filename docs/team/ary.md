# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Your stories (subtasks for each are in [work-breakdown.md](work-breakdown.md))

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`docs/tasks/<epic>/<ID>.md`) and read *Why*, *What to build*, *Acceptance*, *Not in scope* and *Open questions*. The subtasks are in [work-breakdown.md](work-breakdown.md); status is only in ClickUp. A task file marked *(draft)* was written for the owner by Ary's assistant: correct it first and tell Ary what was wrong. The index of every task file is [docs/tasks/README.md](../tasks/README.md).

### Do in this order (stories you lead: 18)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E0.3** Design system and app shell | [E0.3.md](../tasks/E0/E0.3.md) | P0 | 15 Nov | - |
| 2 | **E1.1** Sign up with a UM email and verify it | [E1.1.md](../tasks/E1/E1.1.md) | FR1.1, P0 | 19 Nov | UM domain list |
| 3 | **E1.2** Log in, log out and reset password | [E1.2.md](../tasks/E1/E1.2.md) | FR1.2, P0 | 20 Nov | E1.1 |
| 4 | **E1.3** Profile setup and privacy | [E1.3.md](../tasks/E1/E1.3.md) | FR1.3, P0 | 22 Nov | `users` fields (E0.5) |
| 5 | **E1.4** Home and the four tabs after sign-in | [E1.4.md](../tasks/E1/E1.4.md) | P0 | 22 Nov | E1.2 |
| 6 | **E4.1** Room details screen | [E4.1.md](../tasks/E4/E4.1.md) (draft) | FR2.2, P0 | 4 Dec | E3.1 |
| 7 | **E4.2** Commute options card | [E4.2.md](../tasks/E4/E4.2.md) (draft) | FR4.3, P0 | 7 Dec | E3.3 (use the stub until it lands) |
| 8 | **E4.3** Send a request to the Host | [E4.3.md](../tasks/E4/E4.3.md) (draft) | FR3.3, P0 | 7 Dec | E5.1 |
| 9 | **E5.1** Requests inbox and Connection | [E5.1.md](../tasks/E5/E5.1.md) (draft) | FR3.3, P0 | 7 Dec | E1.3 |
| 10 | **E6.1** Compatibility questions | [E6.1.md](../tasks/E6/E6.1.md) (draft) | FR3.1, P0 | 8 Dec | E1.3 |
| 11 | **E5.2** Chat and viewing | [E5.2.md](../tasks/E5/E5.2.md) (draft) | FR3.3, P0 | 9 Dec | E5.1 |
| 12 | **E5.3** Move-in confirmation | [E5.3.md](../tasks/E5/E5.3.md) (draft) | FR3.3, FR5.1, P0 | 9 Dec | E5.2 |
| 13 | **E6.4** Match list, match profile and connect | [E6.4.md](../tasks/E6/E6.4.md) (draft) | FR3.2, FR3.3, P0 | 11 Dec | E6.2 (fake scores meanwhile), E5.1 |
| 14 | **E10.1** Profile and privacy settings screens | [E10.1.md](../tasks/E10/E10.1.md) (draft) | FR1.3, P1 | 18 Dec | E1.3 |
| 15 | **E9.2** Group, split options and member status | [E9.2.md](../tasks/E9/E9.2.md) (draft) | FR3.3, P1 | 19 Dec | E5.2 |
| 16 | **E10.2** Polish: empty states, errors, accessibility | [E10.2.md](../tasks/E10/E10.2.md) (draft) | P1 | 20 Dec | all P0 flows |
| 17 | **E9.3** Compare units | [E9.3.md](../tasks/E9/E9.3.md) (draft) | FR3.3, P1 | 20 Dec | E9.1, E9.2 |
| 18 | **T3.1** Script and screen captures | [T3.1.md](../tasks/T3/T3.1.md) (draft) | P0 | 20 Dec | - |

### Stories where you do a subtask (10)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../tasks/E0/E0.4.md) (draft) | 30 Oct |
| **E0.1** Repo and CI are healthy | Aaron | Add everyone as collaborators on GitHub; Turn on branch protection: PR required, final check by Ary; Email the lecturer: Appendix 1, native Java OK, SDG 11 OK, what the AI policy counts as your own code, is a GitHub link needed | [E0.1.md](../tasks/E0/E0.1.md) (draft) | 2 Nov |
| **T1.1** Survey and interviews | Aly | Review the survey wording so the questions match the screens; Share the survey link in your circles | [T1.1.md](../tasks/T1/T1.1.md) (draft) | 8 Nov (launch 31 Oct) |
| **E5.4** Notifications | Gathan | Notifications screen | [E5.4.md](../tasks/E5/E5.4.md) (draft) | 13 Dec |
| **E4.4** Public transport route page, peak and off-peak | Aaron | Public transport route page with the MapLibre map, and the Peak / Off-peak toggle | [E4.4.md](../tasks/E4/E4.4.md) (draft) | 14 Dec |
| **E9.1** Evaluate any unit | Gathan | Evaluate form and result screens | [E9.1.md](../tasks/E9/E9.1.md) (draft) | 17 Dec |
| **E9.4** Area page and building reviews | Gathan | Area page and review screens | [E9.4.md](../tasks/E9/E9.4.md) (draft) | 20 Dec |
| **T3.2** Record, edit and final cut | Aly | Check against the rubric | [T3.2.md](../tasks/T3/T3.2.md) (draft) | 2 Jan |
| **T4.1** Report sections | Dafi | 1 Introduction (300 words), UI and colours part of 3 | [T4.1.md](../tasks/T4/T4.1.md) (draft) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../tasks/T4/T4.2.md) (draft) | 9 Jan |

### Open questions waiting for you (23)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E0.1](../tasks/E0/E0.1.md): Ary: send the email to the lecturer. It is also what decides how the report describes AI use. Blocks: T4.2 wording.
- [E0.1](../tasks/E0/E0.1.md): Ary: confirm in GitHub settings that all six members are collaborators.
- [E0.3](../tasks/E0/E0.3.md): Ary and Aly: the 4.5:1 contrast of the real colours is not recorded anywhere in the repository. Measure it and note the result here.
- [E1.1](../tasks/E1/E1.1.md): Ary and Aly: real device and real inbox test before the video.
- [E1.2](../tasks/E1/E1.2.md): Ary and Aly: test the real email and "too many attempts" behaviour against the live project before the video.
- [E1.3](../tasks/E1/E1.3.md): **Gender (proposed, not decided).** In a brainstorm on 30 Oct 2026 Ary leaned to a required gender field, male or
- [E1.3](../tasks/E1/E1.3.md): **Where the gender field lives** (public `users` or `private/profile`): Gathan and Ary. Blocks: schema and Rules.
- [E1.3](../tasks/E1/E1.3.md): **Faculty list, lifestyle tags and the building list for the timetable:** the sources are not decided. Take the tags from the
- [E1.3](../tasks/E1/E1.3.md): **Timetable day format.** The schema draft stores `day` as `"Mon".."Sun"`, while the match function (`match.js`) takes
- [E1.3](../tasks/E1/E1.3.md): **Does a Host need extra profile data?** Decide with Gathan before E2.1.
- [E1.4](../tasks/E1/E1.4.md): Ary and Dafi: what Home should show for a student who has no Household yet. The PRD does not say; decide before E1.3 closes so the Home tab is not empty in the video.
- [E3.1](../tasks/E3/E3.1.md): Gathan and Ary: what the Rooms tab shows before any search (recent, nearby)? Blocks: the empty state.
- [E3.2](../tasks/E3/E3.2.md): Ary and Gathan: decide the gender rule before this story starts. Blocks: the gender filter.
- [E6.1](../tasks/E6/E6.1.md): Ary and Aly: review the survey wording so the questions match (T1.1).
- [E6.1](../tasks/E6/E6.1.md): Ary and Gathan: gender is a hard filter, not a score factor, if it is added (see E1.3).
- [E6.4](../tasks/E6/E6.4.md): Ary and Dafi: how many matches to show at most (the function takes up to 100 candidates). Blocks: paging.
- [E7.3](../tasks/E7/E7.3.md): Dafi and Ary: what the Unit tab shows with no Household yet. Blocks: the empty state.
- [E9.2](../tasks/E9/E9.2.md): Gathan and Ary: who is Host when several sign together (the group picks one). Blocks: the Host choice screen.
- [E9.3](../tasks/E9/E9.3.md): None recorded yet.
- [E10.2](../tasks/E10/E10.2.md): Ary and Aly: one reviewer for the Malay text. Blocks: the BM check.
- [T1.1](../tasks/T1/T1.1.md): Ary and Aly: do the questions match the final screens, including the gender question if the team adds it? Blocks: nothing, the survey is out.
- [T1.5](../tasks/T1/T1.5.md): Dafi and Ary: gender and the first-year student rule must be decided before the freeze.
- [T3.1](../tasks/T3/T3.1.md): Ary: confirm the rubric items and the maximum length (10 to 15 minutes).

**Always:** check and merge every PR, and help owners use the components. If the load gets too heavy, hand the Module 3 screens (E6, E9.2) to Ilham or Gathan.

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

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
