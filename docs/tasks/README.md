# Tasks

Master list of every story in Jiran. One row per ClickUp story task; the story IDs are the ones used in commit messages
(`[E1.2] ...`). Stories with a file in this folder have the full detail there (why, what to build, what is out of scope and
why, decisions, open questions). The subtasks of every story are in [work-breakdown.md](../team/work-breakdown.md).

**Status is not kept here.** ClickUp is the one place for status (Backlog, This week, In progress, Blocked, In review, QA, Done).
The "Where it stands" paragraph in a task file is dated; trust ClickUp if they disagree.

Files are grouped in one folder per epic (`E1/E1.1.md`). To write a new one, copy a finished file such as [E1/E1.1.md](E1/E1.1.md) and keep its sections. Rules: no invented numbers, mark what is not verified, keep
past decisions (struck through) instead of deleting them, and link to the PRD instead of copying it.

## E0 · Foundations and setup

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E0.1 | Repo and CI are healthy | - | P0 | Aaron | 2 Nov | - | [E0.1.md](E0/E0.1.md) (draft) |
| E0.2 | Firebase and the free server are ready | - | P0 | Aaron | 8 Nov | - | [E0.2.md](E0/E0.2.md) (draft) |
| E0.3 | Design system and app shell | - | P0 | Ary | 15 Nov | - | [E0.3.md](E0/E0.3.md) |
| E0.4 | Project management and docs | - | P0 | Dafi | 30 Oct | - | [E0.4.md](E0/E0.4.md) (draft) |
| E0.5 | Data schema and Security Rules | - | P0 | Gathan | draft 8 Nov, merged 15 Nov | - | [E0.5.md](E0/E0.5.md) (draft) |
| E0.6 | Function contracts (stubs) | - | P0 | Aaron | 15 Nov | - | [E0.6.md](E0/E0.6.md) (draft) |

## E1 · Starting and Profile Setup flows

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E1.1 | Sign up with a UM email and verify it | FR1.1 | P0 | Ary | 19 Nov | Waits for UM domain list | [E1.1.md](E1/E1.1.md) |
| E1.2 | Log in, log out and reset password | FR1.2 | P0 | Ary | 20 Nov | Waits for E1.1 | [E1.2.md](E1/E1.2.md) |
| E1.3 | Profile setup and privacy | FR1.3 | P0 | Ary | 22 Nov | Waits for `users` fields (E0.5) | [E1.3.md](E1/E1.3.md) |
| E1.4 | Home and the four tabs after sign-in | - | P0 | Ary | 22 Nov | Waits for E1.2 | [E1.4.md](E1/E1.4.md) |

## E2 · Host Listing flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E2.1 | Pick the building and enter rent and deposit | FR2.1 | P0 | Gathan | 25 Nov | Waits for E0.5, E1.3 | [E2.1.md](E2/E2.1.md) (draft) |
| E2.2 | Rooms and prices with the price rule | FR2.1 | P0 | Gathan | 27 Nov | Waits for E2.1 | [E2.2.md](E2/E2.2.md) (draft) |
| E2.3 | My listing: renew, requests and mark occupied | FR2.1 | P0 | Gathan | 29 Nov | Waits for E2.2 | [E2.3.md](E2/E2.3.md) (draft) |
| E2.4 | Fictional seed listings | - | P0 | Gathan | 29 Nov | can start early · Waits for E0.5 | [E2.4.md](E2/E2.4.md) (draft) |

## E3 · Find a Room flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E3.1 | Rooms screen and Room cards | FR2.2 | P0 | Gathan | 2 Dec | Waits for E2.4 | [E3.1.md](E3/E3.1.md) (draft) |
| E3.2 | Filters | FR2.2 | P0 | Gathan | 3 Dec | Waits for E3.1 | [E3.2.md](E3/E3.2.md) (draft) |
| E3.3 | Commute per travel mode | FR4.1 | P0 | Aaron | start now, due 29 Nov | can start early · Waits for transit coverage and fares (#3, #4, #5), shuttle timetable (T1) | [E3.3.md](E3/E3.3.md) (draft) |
| E3.4 | True monthly cost and travel time on the Room card | FR4.2 | P0 | Aaron | 4 Dec | Waits for E3.1, E3.3 | [E3.4.md](E3/E3.4.md) (draft) |
| E3.5 | Plain-language AI search | FR6.1 | P0 | Ilham | 4 Dec | can start early · Waits for E3.2 for the real filters (use mock filters first) | [E3.5.md](E3/E3.5.md) (draft) |

## E4 · Room Details flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E4.1 | Room details screen | FR2.2 | P0 | Ary | 4 Dec | Waits for E3.1 | [E4.1.md](E4/E4.1.md) (draft) |
| E4.2 | Commute options card | FR4.3 | P0 | Ary | 7 Dec | Waits for E3.3 (use the stub until it lands) | [E4.2.md](E4/E4.2.md) (draft) |
| E4.3 | Send a request to the Host | FR3.3 | P0 | Ary | 7 Dec | Waits for E5.1 | [E4.3.md](E4/E4.3.md) (draft) |
| E4.4 | Public transport route page, peak and off-peak | FR4.1, FR4.3 | P1 | Aaron | 14 Dec | Waits for E4.2 | [E4.4.md](E4/E4.4.md) (draft) |

## E5 · Request and Chat flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E5.1 | Requests inbox and Connection | FR3.3 | P0 | Ary | 7 Dec | Waits for E1.3 | [E5.1.md](E5/E5.1.md) (draft) |
| E5.2 | Chat and viewing | FR3.3 | P0 | Ary | 9 Dec | Waits for E5.1 | [E5.2.md](E5/E5.2.md) (draft) |
| E5.3 | Move-in confirmation | FR3.3, FR5.1 | P0 | Ary | 9 Dec | Waits for E5.2 | [E5.3.md](E5/E5.3.md) (draft) |
| E5.4 | Notifications | - | P1 | Gathan | 13 Dec | Waits for E5.1 | [E5.4.md](E5/E5.4.md) (draft) |

## E6 · Match flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E6.1 | Compatibility questions | FR3.1 | P0 | Ary | 8 Dec | Waits for E1.3 | [E6.1.md](E6/E6.1.md) (draft) |
| E6.2 | Match score function | FR3.2 | P0 | Aaron | 10 Dec | can start early · Waits for match weights (#9), `matchProfiles` (E6.1) | [E6.2.md](E6/E6.2.md) (draft) |
| E6.3 | Match reasons | FR3.2 | P0 | Ilham | 10 Dec | Waits for E6.2 | [E6.3.md](E6/E6.3.md) (draft) |
| E6.4 | Match list, match profile and connect | FR3.2, FR3.3 | P0 | Ary | 11 Dec | Waits for E6.2 (fake scores meanwhile), E5.1 | [E6.4.md](E6/E6.4.md) (draft) |

## E7 · Household flow (Unit tab)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E7.1 | Balance and settle-up logic | FR5.1 | P0 | Dafi | start now, due 29 Nov | can start early | [E7.1.md](E7/E7.1.md) (draft) |
| E7.2 | Household data and rules | FR5.1 | P0 | Gathan | 8 Dec | Waits for E0.5 | [E7.2.md](E7/E7.2.md) (draft) |
| E7.3 | Expenses and balances screens | FR5.1 | P0 | Dafi | 12 Dec | Waits for E7.1, E7.2 | [E7.3.md](E7/E7.3.md) (draft) |
| E7.4 | Move out | FR5.1 | P0 | Dafi | 13 Dec | Waits for E7.3 | [E7.4.md](E7/E7.4.md) (draft) |
| E7.5 | Recurring chores | FR5.2 | P1 | Aly | 13 Dec | can start early · Waits for E7.2 (logic can start now) | [E7.5.md](E7/E7.5.md) (draft) |
| E7.6 | House rules | FR5.3 | P1 | Aly | 13 Dec | Waits for E7.2 | [E7.6.md](E7/E7.6.md) (draft) |

## E8 · Private Contract Check flow

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E8.1 | Choose and set up the AI provider | - | P0 | Ilham | 8 Nov | can start early | [E8.1.md](E8/E8.1.md) (draft) |
| E8.2 | Sample agreements and the contract function | FR6.2 | P0 | Ilham | 13 Dec | can start early · Waits for E8.1 | [E8.2.md](E8/E8.2.md) (draft) |
| E8.3 | Upload and result screens | FR6.2 | P0 | Ilham | 20 Dec | Waits for E5.2 (the file arrives in a chat) | [E8.3.md](E8/E8.3.md) (draft) |
| E8.4 | AI labels and reporting a wrong result | FR6.3 | P0 | Ilham | 20 Dec | Waits for E3.5, E6.3, E8.3 | [E8.4.md](E8/E8.4.md) (draft) |
| E8.5 | AI evaluation for the report | FR6.1, FR6.2 | P0 | Ilham | 20 Dec | Waits for E3.5, E8.3 | [E8.5.md](E8/E8.5.md) (draft) |

## E9 · Evaluate Unit and Group flows (P1)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E9.1 | Evaluate any unit | FR2.4 | P1 | Gathan | 17 Dec | Waits for E3.4 | [E9.1.md](E9/E9.1.md) (draft) |
| E9.2 | Group, split options and member status | FR3.3 | P1 | Ary | 19 Dec | Waits for E5.2 | [E9.2.md](E9/E9.2.md) (draft) |
| E9.3 | Compare units | FR3.3 | P1 | Ary | 20 Dec | Waits for E9.1, E9.2 | [E9.3.md](E9/E9.3.md) (draft) |
| E9.4 | Area page and building reviews | FR2.3 | P1 | Gathan | 20 Dec | Waits for E2, E3 | [E9.4.md](E9/E9.4.md) (draft) |

## E10 · Profile, privacy and polish (P1)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| E10.1 | Profile and privacy settings screens | FR1.3 | P1 | Ary | 18 Dec | Waits for E1.3 | [E10.1.md](E10/E10.1.md) (draft) |
| E10.2 | Polish: empty states, errors, accessibility | - | P1 | Ary | 20 Dec | Waits for all P0 flows | [E10.2.md](E10/E10.2.md) (draft) |

## E11 · Optional (P2)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|

## T1 · Research and validation

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| T1.1 | Survey and interviews | - | P0 | Aly | 8 Nov (launch 31 Oct) | - | [T1.1.md](T1/T1.1.md) (draft) |
| T1.2 | Survey analysis | - | P0 | Aly | 15 Nov | Waits for T1.1 | [T1.2.md](T1/T1.2.md) (draft) |
| T1.3 | Verify the assumed numbers | - | P0 | Dafi | 13 Nov | Waits for T1.1 | [T1.3.md](T1/T1.3.md) (draft) |
| T1.4 | UM shuttle timetable and commute estimates | - | P0 | Dafi | 8 Nov (timetable), 13 Nov (estimates) | - | [T1.4.md](T1/T1.4.md) (draft) |
| T1.5 | Requirements freeze | - | P0 | Dafi | 15 Nov | Waits for T1.2 | [T1.5.md](T1/T1.5.md) (draft) |

## T2 · Testing and quality

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| T2.1 | Test plan and integration tests | - | P0 | Aly | 15 Nov (plan), 27 Dec (run) | - | [T2.1.md](T2/T2.1.md) (draft) |
| T2.2 | NFR1: speed under load | - | P0 | Aly | 27 Dec | Waits for working search | [T2.2.md](T2/T2.2.md) (draft) |
| T2.3 | NFR2: privacy and security evidence | - | P0 | Gathan | 27 Dec | Waits for all rules | [T2.3.md](T2/T2.3.md) (draft) |
| T2.4 | Usability test | - | P0 | Aly | 27 Dec | Waits for working app | [T2.4.md](T2/T2.4.md) (draft) |
| T2.5 | Release check | - | P0 | Aly | 27 Dec | Waits for all P0 flows | [T2.5.md](T2/T2.5.md) (draft) |

## T3 · Video (due 12:00, 3 Jan 2027, 10 to 15 minutes)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| T3.1 | Script and screen captures | - | P0 | Ary | 20 Dec | - | [T3.1.md](T3/T3.1.md) (draft) |
| T3.2 | Record, edit and final cut | - | P0 | Aly | 2 Jan | Waits for T3.1 | [T3.2.md](T3/T3.2.md) (draft) |

## T4 · Report and AI Usage Log (due 12:00, 10 Jan 2027, 2,500 words and 15 pages at most)

| ID | Story | FR | Priority | Owner | Due | Depends on | Detail |
|---|---|---|---|---|---|---|---|
| T4.1 | Report sections | - | P0 | Dafi | 9 Jan | Waits for each owner's part | [T4.1.md](T4/T4.1.md) (draft) |
| T4.2 | AI Usage Log appendix (Appendix 2(b)) | - | P0 | Ilham | 9 Jan | Waits for everyone's logs | [T4.2.md](T4/T4.2.md) (draft) |
