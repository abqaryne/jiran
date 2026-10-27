# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Epics E1 and E3 are yours. You build the screens of Modules 1 and 3, the Room details and commute card, and you check every PR.
Everyone else builds their own module's screens with your components.

**This week (Step 0, until 8 Nov)**

| Story | Priority | Due |
|---|---|---|
| **E0.1** Repo and CI healthy: add collaborators, branch protection, email the lecturer | P0 | 30 Oct |
| **E7.1** (part) Review the survey wording with Aly so the questions match the screens | P0 | 2 Nov |
| **E0.3** Design system and app shell: Figma and components are done; announce the components, make the shell the first screen after sign-in, empty and error states | P0 | 15 Nov |

**Wave A (16 to 29 Nov): Module 1**

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E1.1** Sign up with a UM email and verify it (FR1.1) | P0 | 29 Nov (auth works 15 Nov) | UM email policy decision |
| **E1.2** Log in, log out, reset password (FR1.2) | P0 | 29 Nov | E1.1 |
| **E1.3** Profile and privacy (FR1.3) | P0 | 29 Nov | Gathan's `users` fields (E0.5) |

**Wave B (30 Nov to 13 Dec)**

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E4.3** Commute options card and the public transport route page (screens) | P0 | 13 Dec | Aaron's commute API (use the stub) |
| **E3.1** Compatibility questions (FR3.1) | P0 | 13 Dec | E1.3 |
| **E3.2** Ranked matches with score and reasons (screens) | P0 | 13 Dec | E3.1; Aaron's score, Ilham's reasons (fake scores meanwhile) |
| **E3.3** Connection request and chat (screens) | P0 | 13 Dec | Gathan's `chats` |
| **E3.4** Group and split decision aids | P1 | 13 Dec | E3.3 |
| **E4.4** Peak and off-peak toggle and route steps (screens) | P1 | 13 Dec | E4.3 |

**Always:** check and merge every PR, and help owners use the components. If the load gets too heavy, hand E3.4 or the Module 3 screens to Ilham or Gathan.

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

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
