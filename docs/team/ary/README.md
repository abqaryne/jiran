# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance*, *Not in scope* and *Open questions*. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

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
| **E0.1** Repo and CI are healthy | Aaron | Add everyone as collaborators on GitHub; Turn on branch protection: PR required, final check by Ary; Email the lecturer: Appendix 1, native Java OK, SDG 11 OK, what the AI policy counts as your own code, is a GitHub link needed | [E0.1.md](../../../TASKS/E0/E0.1.md) | 2 Nov |
| **T1.1** Survey and interviews | Aly | Review the survey wording so the questions match the screens; Share the survey link in your circles | [T1.1.md](../../../TASKS/T1/T1.1.md) | 8 Nov (launch 31 Oct) |
| **E5.4** Notifications | Gathan | Notifications screen | [E5.4.md](../../../TASKS/E5/E5.4.md) | 13 Dec |
| **E4.4** Public transport route page, peak and off-peak | Aaron | Public transport route page with the MapLibre map, and the Peak / Off-peak toggle | [E4.4.md](../../../TASKS/E4/E4.4.md) | 14 Dec |
| **E9.1** Evaluate any unit | Gathan | Evaluate form and result screens | [E9.1.md](../../../TASKS/E9/E9.1.md) | 17 Dec |
| **E9.4** Area page and building reviews | Gathan | Area page and review screens | [E9.4.md](../../../TASKS/E9/E9.4.md) | 20 Dec |
| **T3.2** Record, edit and final cut | Aly | Check against the rubric | [T3.2.md](../../../TASKS/T3/T3.2.md) | 2 Jan |
| **T4.1** Report sections | Dafi | 1 Introduction (300 words), UI and colours part of 3 | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

### Open questions waiting for you (7)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E0.1](../../../TASKS/E0/E0.1.md): Ary: send the email to the lecturer. It is also what decides how the report describes AI use. Blocks: T4.2 wording.
- [E1.1](../../../TASKS/E1/E1.1.md): Ary and Aly: real device and real inbox test before the video.
- [E1.2](../../../TASKS/E1/E1.2.md): Ary and Aly: test the real email and "too many attempts" behaviour against the live project before the video.
- [E6.1](../../../TASKS/E6/E6.1.md): Ary and Aly: review the survey wording so the questions match (T1.1).
- [E10.2](../../../TASKS/E10/E10.2.md): Ary and Aly: find one Malay reviewer (a Malaysian classmate) for the BM text. Blocks: the BM check.
- [T3.1](../../../TASKS/T3/T3.1.md): Ary: confirm the rubric items and the maximum length (10 to 15 minutes) from the brief.
- [T4.2](../../../TASKS/T4/T4.2.md): Everyone: fill the TODO fields before merging into the appendix.

### Decided on 31 Oct 2026 for your stories (21)

Read these before you start; the full text is under *Open questions* in each task file.

- [E0.1](../../../TASKS/E0/E0.1.md): All six members are collaborators: GitHub lists abqaryne (admin), aarntn, GathanAkhtar, IlhamSetiabudi365, Aly24212090 and dafiatha (write).
- [E0.3](../../../TASKS/E0/E0.3.md): Contrast computed with the WCAG formula from `colors.xml`: every text pair is at least 4.5:1 (lowest: success text on success background 4.75:1; secondary text on fill 5.53:1; white on brand 6.38:1), and the strong border is 3.42:1 against white (non-text needs 3:1). Computed from the hex values, not measured on a device screen.
- [E1.1](../../../TASKS/E1/E1.1.md): First-year students without a UM email wait until they receive it. The sign-up screen says so, and the report lists it as a limitation (PRD open question 9).
- [E1.3](../../../TASKS/E1/E1.3.md): Gender: required at registration (profile setup right after sign-up), male or female, and shown on the profile so others see it. Rooms are not hidden automatically: the Rooms search and the housemate page have a filter chip the user picks (Male / Female / Any), default Any. PRD FR1.3 and section 8 updated.
- [E1.3](../../../TASKS/E1/E1.3.md): The gender field lives in the public `users/{uid}` document, because others must see it.
- [E1.3](../../../TASKS/E1/E1.3.md): Sources: the faculty list is UM's official list of faculties and academies, kept in a string resource with its source link and date; the building list comes from the same place search as Listings (E2.1). Lifestyle tags come from the survey (T1); until 13 Nov use a short starter set and mark it as a draft.
- [E1.3](../../../TASKS/E1/E1.3.md): A Host needs no extra profile data: any student can be a Host; unit details live in the listing, and the phone number stays in the private document until a Connection.
- [E1.4](../../../TASKS/E1/E1.4.md): Home for a student without a Household shows two cards, "Find a room" (Rooms tab) and "Find housemates" (matches), and a link "Join a Household with a code" (E5.3). No empty screen.
- [E3.1](../../../TASKS/E3/E3.1.md): Before any search the Rooms tab shows the newest active listings first, with the filter chips on top. If the student saved a faculty, cards show travel time (E3.4). No location permission is needed.
- [E3.2](../../../TASKS/E3/E3.2.md): Gender filter: a chip "For: Male / Female / Any", default Any. Male shows `male_only` and `any` listings, Female shows `female_only` and `any`. Nothing is hidden without the user choosing it (see E1.3).
- [E4.1](../../../TASKS/E4/E4.1.md): Reported listings: reports go to a `reports` collection; Gathan moderates, Ary is the backup; the sheet says only "The team will review this" and promises no time; the item stays visible until a moderator hides it.
- [E4.3](../../../TASKS/E4/E4.3.md): The request message is at most 300 characters. A listing with no vacant room cannot receive requests: the button is disabled and says "No rooms free".
- [E5.1](../../../TASKS/E5/E5.1.md): An unanswered request expires after 14 days, checked on read like listings (`expiresAt`); the sender sees "Expired".
- [E5.2](../../../TASKS/E5/E5.2.md): Chat attachments are photos only, resized on the phone to at most 1600 px and 2 MB, uploaded through the same Cloudinary flow as listings (E2.1). Contract files never go into a chat.
- [E6.1](../../../TASKS/E6/E6.1.md): Gender is not part of the match score. The housemate page has the same filter chip as Rooms (Male / Female / Any, default Any).
- [E6.4](../../../TASKS/E6/E6.4.md): Show the top 20 matches, no paging. The function still scores up to 100 candidates.
- [E7.3](../../../TASKS/E7/E7.3.md): Unit tab without a Household: "You are not in a Household yet" and two buttons, "Find a room" and "Join with a code".
- [E9.2](../../../TASKS/E9/E9.2.md): The group picks one member as Host; by default the person who created the group, and any member can hand it over before signing.
- [E9.4](../../../TASKS/E9/E9.4.md): Building reviews: reports go to a `reports` collection; Gathan moderates, Ary is the backup; the sheet says only "The team will review this" and promises no time; the item stays visible until a moderator hides it.
- [T1.1](../../../TASKS/T1/T1.1.md): The survey is out and stays as it is. Gender is asked in the app at registration (E1.3), so the survey needs no new question.
- [T1.5](../../../TASKS/T1/T1.5.md): Both decided: gender is required at registration with a user-chosen filter (E1.3), and first-year students wait for their Siswa Mail (E1.1).

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

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
