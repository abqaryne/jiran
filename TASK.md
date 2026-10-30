# Tasks

Master task list for Jiran. Full detail for each task lives in `TASKS/<milestone>/<ID>.md`. Keep this table and each task file's header in sync (status especially).

**Status comes from the repository, not from ClickUp** (decided by Ary on 31 Oct 2026). Set it from what is merged or open on GitHub:

| Status | When |
|---|---|
| `backlog` | nothing for this task is in the repository yet |
| `inprogress` | part of the work is in `main` or on a branch, but not all of *Work needed* |
| `inreview` | all the work is in an open PR, waiting for review |
| `qa` | the work is merged in `main`; the *Acceptance* checks are not all ticked yet |
| `done` | merged in `main` and every *Acceptance* check in the task file is ticked |
| `dropped` | the team decided not to do it; the task file says why |

Waiting for another task is shown in *Depends on*, not as a status. ClickUp is used for planning (This week, Blocked) and for logged hours, so its status may differ; when it does, this file follows the repository. Definition of Done: merged by Ary after review, CI green, Aly's QA passed (the *Acceptance* checks ticked), hours logged in ClickUp.

IDs are assigned once and never reused: `E<epic>.<n>` for the flows built in order, `T<n>.<n>` for the tracks that run in parallel. Commit messages use them (`[E1.2] Add Log in`). Work with no task uses `[Docs]`, `[Infra]` or `[Design]`.

Decisions that are hard to reverse go in [ADR.md](ADR.md) (append-only). The shape of the product is in [docs/product/PRD.md](docs/product/PRD.md), the data in `docs/schema.md` (Gathan's PR #8, not merged on 30 Oct 2026), and the engineering notes in [docs/engineering/architecture.md](docs/engineering/architecture.md).

Statuses below were set from the repository on 31 Oct 2026. Each task file lists its open questions with an owner; an item marked not recorded or not verified was not seen in the repository.

## Milestones

| Milestone | Window | Scope |
|---|---|---|
| E0 - Foundations and setup | now to 15 Nov (Gate 2) | Repo and CI, Firebase and the free server, the design system and app shell, the data schema and Security Rules, the function contracts, the ClickUp Space. |
| E1 - Starting and Profile Setup flows | 16 to 22 Nov | A student signs up with a UM student email, verifies it, logs in, sets up a profile and lands on Home with the four tabs. |
| E2 - Host Listing flow | 23 to 29 Nov | A Host posts a Unit with priced Rooms and manages the Listing. |
| E3 - Find a Room flow | 30 Nov to 4 Dec | A student searches Rooms, sees the true monthly cost and travel time on each card, and can type the request in plain words. |
| E4 - Room Details flow | 3 to 7 Dec | A student opens a Room, sees the Host, the Rooms in the Unit and how to get to class, and can send a Connection request or report the Listing. |
| E5 - Request and Chat flow | 5 to 9 Dec | A request is accepted and becomes a Connection, the two people chat and arrange a viewing, and the new tenant confirms the move-in. |
| E6 - Match flow | 7 to 11 Dec | A student answers the questions, sees ranked Matches with a score and reasons, and sends a request to connect. |
| E7 - Household flow (Unit tab) | 8 to 13 Dec (logic can start now) | People who live together track shared expenses, see who owes whom, and run chores and house rules. |
| E8 - Private Contract Check flow | 8 to 20 Dec (prompts can start now) | A student checks a tenancy agreement privately and sees each clause marked Red flag, Check or OK; the AI provider and the AI evaluation. |
| E9 - Evaluate Unit and Group flows (P1) | 14 to 20 Dec | Friends evaluate any Unit, compare Units, agree a Room price split and confirm a Group. Only after the P0 flows work. |
| E10 - Profile, privacy and polish (P1) | 14 to 20 Dec | The profile, privacy and settings screens are finished and every flow has empty and error states. |
| T1 - Research and validation | now to 15 Nov | Survey, interviews and the numbers the app needs. Runs in parallel with E0. |
| T2 - Testing and quality | plan from Nov, run 14 to 27 Dec | Evidence that the app works and meets NFR1 and NFR2. |
| T3 - Video | capture by 20 Dec, edit 28 Dec to 2 Jan | The video, due 12:00 on 3 Jan 2027, 10 to 15 minutes. |
| T4 - Report and AI Usage Log | draft during the build, assemble 4 to 9 Jan | The report (2,500 words, 15 pages) due 12:00 on 10 Jan 2027, and the AI Usage Log appendix. |

P2 stories (shared commute groups, impact dashboard, UM planning dashboard, the Paste listing AI shortcut) are created in Backlog with the `P2` label only if time is left; they have no ID yet.

## Tasks

| ID | Milestone | Status | Category | Owner | Due | Title |
|---|---|---|---|---|---|---|
| [E0.1](TASKS/E0/E0.1.md) | E0 - Foundations and setup | inprogress | process / infra | Aaron | 2 Nov | Repo and CI are healthy |
| [E0.2](TASKS/E0/E0.2.md) | E0 - Foundations and setup | inprogress | infra | Aaron | 8 Nov | Firebase and the free server are ready |
| [E0.3](TASKS/E0/E0.3.md) | E0 - Foundations and setup | qa | ui | Ary | 15 Nov | Design system and app shell |
| [E0.4](TASKS/E0/E0.4.md) | E0 - Foundations and setup | done | process | Dafi | 30 Oct | Project management and docs |
| [E0.5](TASKS/E0/E0.5.md) | E0 - Foundations and setup | inprogress | persistence / rules | Gathan | draft 8 Nov, merged 15 Nov | Data schema and Security Rules |
| [E0.6](TASKS/E0/E0.6.md) | E0 - Foundations and setup | inprogress | api / docs | Aaron | 15 Nov | Function contracts (stubs) |
| [E1.1](TASKS/E1/E1.1.md) | E1 - Starting and Profile Setup flows | qa | auth / ui | Ary | 19 Nov | Sign up with a UM email and verify it |
| [E1.2](TASKS/E1/E1.2.md) | E1 - Starting and Profile Setup flows | qa | auth / ui | Ary | 20 Nov | Log in, log out and reset password |
| [E1.3](TASKS/E1/E1.3.md) | E1 - Starting and Profile Setup flows | backlog | ui / persistence | Ary | 22 Nov | Profile setup and privacy |
| [E1.4](TASKS/E1/E1.4.md) | E1 - Starting and Profile Setup flows | qa | ui | Ary | 22 Nov | Home and the four tabs after sign-in |
| [E2.1](TASKS/E2/E2.1.md) | E2 - Host Listing flow | backlog | ui / persistence | Gathan | 25 Nov | Pick the building and enter rent and deposit |
| [E2.2](TASKS/E2/E2.2.md) | E2 - Host Listing flow | backlog | logic / ui | Gathan | 27 Nov | Rooms and prices with the price rule |
| [E2.3](TASKS/E2/E2.3.md) | E2 - Host Listing flow | backlog | ui / rules | Gathan | 29 Nov | My listing: renew, requests and mark occupied |
| [E2.4](TASKS/E2/E2.4.md) | E2 - Host Listing flow | backlog | data | Gathan | 29 Nov | Fictional seed listings |
| [E3.1](TASKS/E3/E3.1.md) | E3 - Find a Room flow | backlog | ui | Gathan | 2 Dec | Rooms screen and Room cards |
| [E3.2](TASKS/E3/E3.2.md) | E3 - Find a Room flow | backlog | ui / logic | Gathan | 3 Dec | Filters |
| [E3.3](TASKS/E3/E3.3.md) | E3 - Find a Room flow | inprogress | api / logic | Aaron | start now, due 29 Nov | Commute per travel mode |
| [E3.4](TASKS/E3/E3.4.md) | E3 - Find a Room flow | backlog | logic / ui | Aaron | 4 Dec | True monthly cost and travel time on the Room card |
| [E3.5](TASKS/E3/E3.5.md) | E3 - Find a Room flow | backlog | ai | Ilham | 4 Dec | Plain-language AI search |
| [E4.1](TASKS/E4/E4.1.md) | E4 - Room Details flow | backlog | ui | Ary | 4 Dec | Room details screen |
| [E4.2](TASKS/E4/E4.2.md) | E4 - Room Details flow | inprogress | ui | Ary | 7 Dec | Commute options card |
| [E4.3](TASKS/E4/E4.3.md) | E4 - Room Details flow | backlog | ui / rules | Ary | 7 Dec | Send a request to the Host |
| [E4.4](TASKS/E4/E4.4.md) | E4 - Room Details flow | backlog | api / ui | Aaron | 14 Dec | Public transport route page, peak and off-peak |
| [E5.1](TASKS/E5/E5.1.md) | E5 - Request and Chat flow | backlog | ui / rules | Ary | 7 Dec | Requests inbox and Connection |
| [E5.2](TASKS/E5/E5.2.md) | E5 - Request and Chat flow | backlog | ui / persistence | Ary | 9 Dec | Chat and viewing |
| [E5.3](TASKS/E5/E5.3.md) | E5 - Request and Chat flow | backlog | ui / logic | Ary | 9 Dec | Move-in confirmation |
| [E5.4](TASKS/E5/E5.4.md) | E5 - Request and Chat flow | backlog | notification | Gathan | 13 Dec | Notifications |
| [E6.1](TASKS/E6/E6.1.md) | E6 - Match flow | backlog | ui / persistence | Ary | 8 Dec | Compatibility questions |
| [E6.2](TASKS/E6/E6.2.md) | E6 - Match flow | inprogress | logic | Aaron | 10 Dec | Match score function |
| [E6.3](TASKS/E6/E6.3.md) | E6 - Match flow | backlog | ai | Ilham | 10 Dec | Match reasons |
| [E6.4](TASKS/E6/E6.4.md) | E6 - Match flow | backlog | ui | Ary | 11 Dec | Match list, match profile and connect |
| [E7.1](TASKS/E7/E7.1.md) | E7 - Household flow (Unit tab) | backlog | logic | Dafi | start now, due 29 Nov | Balance and settle-up logic |
| [E7.2](TASKS/E7/E7.2.md) | E7 - Household flow (Unit tab) | backlog | persistence / rules | Gathan | 8 Dec | Household data and rules |
| [E7.3](TASKS/E7/E7.3.md) | E7 - Household flow (Unit tab) | backlog | ui | Dafi | 12 Dec | Expenses and balances screens |
| [E7.4](TASKS/E7/E7.4.md) | E7 - Household flow (Unit tab) | backlog | ui / logic | Dafi | 13 Dec | Move out |
| [E7.5](TASKS/E7/E7.5.md) | E7 - Household flow (Unit tab) | backlog | logic / ui | Aly | 13 Dec | Recurring chores |
| [E7.6](TASKS/E7/E7.6.md) | E7 - Household flow (Unit tab) | backlog | logic / ui | Aly | 13 Dec | House rules |
| [E8.1](TASKS/E8/E8.1.md) | E8 - Private Contract Check flow | backlog | ai / process | Ilham | 8 Nov | Choose and set up the AI provider |
| [E8.2](TASKS/E8/E8.2.md) | E8 - Private Contract Check flow | backlog | ai | Ilham | 13 Dec | Sample agreements and the contract function |
| [E8.3](TASKS/E8/E8.3.md) | E8 - Private Contract Check flow | backlog | ui / rules | Ilham | 20 Dec | Upload and result screens |
| [E8.4](TASKS/E8/E8.4.md) | E8 - Private Contract Check flow | backlog | ai / ui | Ilham | 20 Dec | AI labels and reporting a wrong result |
| [E8.5](TASKS/E8/E8.5.md) | E8 - Private Contract Check flow | backlog | ai / research | Ilham | 20 Dec | AI evaluation for the report |
| [E9.1](TASKS/E9/E9.1.md) | E9 - Evaluate Unit and Group flows (P1) | backlog | ui / logic | Gathan | 17 Dec | Evaluate any unit |
| [E9.2](TASKS/E9/E9.2.md) | E9 - Evaluate Unit and Group flows (P1) | backlog | ui / persistence | Ary | 19 Dec | Group, split options and member status |
| [E9.3](TASKS/E9/E9.3.md) | E9 - Evaluate Unit and Group flows (P1) | backlog | ui | Ary | 20 Dec | Compare units |
| [E9.4](TASKS/E9/E9.4.md) | E9 - Evaluate Unit and Group flows (P1) | backlog | ui / persistence | Gathan | 20 Dec | Area page and building reviews |
| [E10.1](TASKS/E10/E10.1.md) | E10 - Profile, privacy and polish (P1) | backlog | ui | Ary | 18 Dec | Profile and privacy settings screens |
| [E10.2](TASKS/E10/E10.2.md) | E10 - Profile, privacy and polish (P1) | backlog | ui | Ary | 20 Dec | Polish: empty states, errors, accessibility |
| [T1.1](TASKS/T1/T1.1.md) | T1 - Research and validation | inprogress | research | Aly | 8 Nov (launch 31 Oct) | Survey and interviews |
| [T1.2](TASKS/T1/T1.2.md) | T1 - Research and validation | backlog | research | Aly | 15 Nov | Survey analysis |
| [T1.3](TASKS/T1/T1.3.md) | T1 - Research and validation | backlog | research | Dafi | 13 Nov | Verify the assumed numbers |
| [T1.4](TASKS/T1/T1.4.md) | T1 - Research and validation | backlog | research | Dafi | 8 Nov (timetable), 13 Nov (estimates) | UM shuttle timetable and commute estimates |
| [T1.5](TASKS/T1/T1.5.md) | T1 - Research and validation | backlog | process | Dafi | 15 Nov | Requirements freeze |
| [T2.1](TASKS/T2/T2.1.md) | T2 - Testing and quality | backlog | qa | Aly | 15 Nov (plan), 27 Dec (run) | Test plan and integration tests |
| [T2.2](TASKS/T2/T2.2.md) | T2 - Testing and quality | inprogress | qa | Aly | 27 Dec | NFR1: speed under load |
| [T2.3](TASKS/T2/T2.3.md) | T2 - Testing and quality | backlog | qa / rules | Gathan | 27 Dec | NFR2: privacy and security evidence |
| [T2.4](TASKS/T2/T2.4.md) | T2 - Testing and quality | backlog | qa / research | Aly | 27 Dec | Usability test |
| [T2.5](TASKS/T2/T2.5.md) | T2 - Testing and quality | backlog | qa | Aly | 27 Dec | Release check |
| [T3.1](TASKS/T3/T3.1.md) | T3 - Video | backlog | video | Ary | 20 Dec | Script and screen captures |
| [T3.2](TASKS/T3/T3.2.md) | T3 - Video | backlog | video | Aly | 2 Jan | Record, edit and final cut |
| [T4.1](TASKS/T4/T4.1.md) | T4 - Report and AI Usage Log | backlog | docs | Dafi | 9 Jan | Report sections |
| [T4.2](TASKS/T4/T4.2.md) | T4 - Report and AI Usage Log | inprogress | docs / process | Ilham | 9 Jan | AI Usage Log appendix (Appendix 2(b)) |
