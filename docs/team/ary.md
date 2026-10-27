# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Work is organised as flows (epics E0 to E10, done in order) plus parallel tracks (T1 to T4). Full list with every subtask: [trello-cards.md](trello-cards.md).

**Stories you lead** (18)

| Story | FR, priority | Due | Waits for |
|---|---|---|---|
| **E0.3** Design system and app shell | P0 | 15 Nov |  |
| **E1.1** Sign up with a UM email and verify it | FR1.1, P0 | 19 Nov | UM email policy decision |
| **E1.2** Log in, log out and reset password | FR1.2, P0 | 20 Nov | E1.1 |
| **E1.3** Profile setup and privacy | FR1.3, P0 | 22 Nov | `users` fields (E0.5) |
| **E1.4** Home and the four tabs after sign-in | P0 | 22 Nov | E1.2 |
| **E4.1** Room details screen | FR2.2, P0 | 4 Dec | E3.1 |
| **E4.2** Commute options card | FR4.3, P0 | 7 Dec | E3.3 (use the stub until it lands) |
| **E4.3** Send a request to the Host | FR3.3, P0 | 7 Dec | E5.1 |
| **E5.1** Requests inbox and Connection | FR3.3, P0 | 7 Dec | E1.3 |
| **E5.2** Chat and viewing | FR3.3, P0 | 9 Dec | E5.1 |
| **E5.3** Move-in confirmation | FR3.3, FR5.1, P0 | 9 Dec | E5.2 |
| **E6.1** Compatibility questions | FR3.1, P0 | 8 Dec | E1.3 |
| **E6.4** Match list, match profile and connect | FR3.2, FR3.3, P0 | 11 Dec | E6.2 (fake scores meanwhile), E5.1 |
| **E9.2** Group, split options and member status | FR3.3, P1 | 19 Dec | E5.2 |
| **E9.3** Compare units | FR3.3, P1 | 20 Dec | E9.1, E9.2 |
| **E10.1** Profile and privacy settings screens | FR1.3, P1 | 18 Dec | E1.3 |
| **E10.2** Polish: empty states, errors, accessibility | P1 | 20 Dec | all P0 flows |
| **T3.1** Script and screen captures | P0 | 20 Dec |  |

**Stories where you do a subtask** (10): open the story card to see yours.

| Story | Lead | Due |
|---|---|---|
| **E0.1** Repo and CI are healthy | Aaron | 2 Nov |
| **E0.4** Project management and docs | Dafi | 30 Oct |
| **E4.4** Public transport route page, peak and off-peak | Aaron | 14 Dec |
| **E5.4** Notifications | Gathan | 13 Dec |
| **E9.1** Evaluate any unit | Gathan | 17 Dec |
| **E9.4** Area page and building reviews | Gathan | 20 Dec |
| **T1.1** Survey and interviews | Aly | 8 Nov (launch 31 Oct) |
| **T3.2** Record, edit and final cut | Aly | 2 Jan |
| **T4.1** Report sections | Dafi | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | 9 Jan |

**Always:** check and merge every PR, and help owners use the components. If the load gets too heavy, hand the Module 3 screens (E6, E9.2) to Ilham or Gathan.

## Hand-offs
| You wait for | From | Use meanwhile |
|---|---|---|
| Field names for users and listings | Gathan (by 15 Nov) | Mock data |
| Commute API response | Aaron (by 15 Nov, contract) | Stub JSON |
| Match score and reasons | Aaron and Ilham | Fake scores |

## Your PR check-in routine
1. CI is green and the PR links a Trello card.
2. Backend or rules PRs already have Aaron's or Gathan's review.
3. UI follows the design system: contrast 4.5:1, touch targets at least 48 dp, status shown with words.
4. Merge, and tell the owner so QA can pick it up.

If PRs queue for more than a day, let Aaron merge backend-only PRs.

## Your parts in the graded deliverables
- **Video:** segment 1 overview (1:30), your part of the demo (sign-up, profile, roommate matching), segment 3 design and usability (1:00)
- **Report:** section 1 Introduction (300 words), the UI and colours part of section 3, final review
- **Risk:** you are the bottleneck for screens and merges. If the load is too heavy, hand the Module 3 screens to Ilham or Gathan.

## Rules for everyone

- Log your actual hours on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the Trello card (the card shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
