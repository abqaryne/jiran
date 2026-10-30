# Work breakdown: Initiative, Epics, Stories, Subtasks

How the work is organised in ClickUp. Four levels, from big to small:

```
Initiative     Jiran: a working app, a video and a report, submitted by 3 and 10 January 2027
 └─ Epic       one FLOW of the app (E0 is the foundation, E1 to E10 follow the flows in build order)
     └─ Story  one deliverable someone can finish and demo (this is a ClickUp task)
         └─ Subtask   one small step with one person (this is a subtask of the task)
```

Sources: [PRD.md](../product/PRD.md) (FRs), [USER-FLOW.md](../product/USER-FLOW.md) and the Figma flows (what each epic builds),
[TEAM_PLAN.md](TEAM_PLAN.md) (owners, gates) and [ASSUMPTIONS.md](../product/ASSUMPTIONS.md) (numbers). Update this file when
the plan changes. Estimates (hours) are left empty on purpose: fill them in at sprint planning.

**Three places, one source each.** This file lists every story and its subtasks (what ClickUp holds). The root [TASK.md](../../TASK.md) is the master list of tasks with milestone, status and category. Each task has a file in `TASKS/<epic>/<ID>.md`, modelled on [E1.1](../../TASKS/E1/E1.1.md): description, work needed, constraints, acceptance, what is left out and why, resolution, decisions and open questions. Decisions that are hard to reverse go in [ADR.md](../../ADR.md). Write the task file before the first line of code and keep its status in step with ClickUp.

## 1. How the order works

**Epics E0 to E10 are done in order, one flow after another.** Each epic is one flow of the app built end to end: screens, data,
rules and tests, with the whole team working on that flow. When an epic is merged and tested, the next one starts.

- **E0 first.** It holds what every flow needs: the repo, Firebase, the design system and shell, the data schema and rules, and
  the function contracts. Gate 2 (15 Nov) closes E0.
- **E1 to E8 are the P0 flows.** E1 to E7 are built and merged by the P0 code freeze (13 Dec); the contract check (E8) and the AI
  evaluation finish by 20 Dec.
- **E9 and E10 are P1.** They start only after the P0 flows work end to end. Anything not stable by 20 Dec is dropped, not rushed.
- **Stories marked *can start early*** need nobody and are allowed to run before their epic starts (for example Aaron's commute
  engine, Dafi's balance logic, Ilham's contract prompts, Gathan's seed listings). They are the only exception to the order.
- **Tracks T1 to T4 run in parallel, by date, not by order:** research and numbers, testing, video, report. They are not flows.
- A story marked **WAIT** is not started until the thing it waits for exists. Use a stub or mock data meanwhile.

## 2. How to set it up in ClickUp

| Level | In ClickUp |
|---|---|
| **Initiative** | one **Space** named "Jiran" |
| **Epic** | one **List** per epic, in build order: `E0 Foundations`, `E1 Starting and Profile` ... `E10 Polish`. Put the flows in a Folder `Flows` and the tracks `T1` to `T4` in a Folder `Tracks`. The epic's goal goes in the List description. |
| **Story** | a **task** in its epic's List. Title format `E1.2 Log in, log out and reset password` |
| **Subtask** | a **subtask** of the story task, written `(Owner) what` |

**Statuses** (set them once for the whole Space so every List shares them; use Board view to see them as columns):

| Status | ClickUp type | Meaning |
|---|---|---|
| **Backlog** | Not started | not scheduled yet; stories of later epics wait here |
| **This week** | Not started | picked at sprint planning, not started |
| **In progress** | Active | one person is working on it |
| **Blocked** | Active | waits for a person or data (a story marked WAIT). Write who in a comment. |
| **In review** | Active | PR open; Aaron reviews backend, Gathan schema and rules, then Ary |
| **QA** | Active | Aly checks it on the emulator or a device |
| **Done** | Closed | all subtasks ticked, PR merged by Ary, CI green, QA passed, hours logged |

Only stories of the **current epic**, plus the *can start early* ones, belong in This week.

**Tags:** priority `P0` `P1` `P2` · `WAIT`. **Fields:** Assignee, Due date and Time estimate are built in; add Custom Fields
for FR (text), PR link (URL) and AI log link (URL).

**Rules**
- Log your **actual hours on the story task** with ClickUp time tracking (this is the evidence for the peer evaluation).
- Max two stories In progress per person.
- If AI helped, add the entry to `docs/ai-log/<name>.md` and link it on the task.
- Screenshot the Board view every week (evidence for the report).
- **Who builds screens:** each module owner builds their own screens using Ary's components. Ary builds the screens of Modules 1 and 3,
  the Room details and commute card, and reviews the rest.

**Story task template** (paste into the description)

```
Epic: E_   FR: FRn.n   Priority: P_   Owner: @name   Reviewer: @name   Due: date   Estimate: h
Goal: one sentence in plain words ("A student can ...")
WAIT: what this needs from someone else, and what to use meanwhile (mock data, stub)
Done when: the behaviour in the PRD works on a device; unit tests; design system only; CI green; PR merged; hours logged
```

## 3. Initiative and milestones

**Initiative:** Jiran, a verified UM-only app for off-campus rooms, housemates and commute (SDG 11), delivered as a working
Android app, a 10 to 15 minute video (12:00, 3 Jan 2027) and a report (12:00, 10 Jan 2027).

| Milestone | Date | Owner | Done when |
|---|---|---|---|
| Gate 1 | 8 Nov | Dafi | survey has 100+ responses, repo runs, Firebase project exists |
| Gate 2: E0 done | 15 Nov | Dafi | requirements frozen, app shell and schema merged, Auth works, every Cloud Function has an agreed stub |
| Gate 3: P0 code freeze | 13 Dec | Aaron | E1 to E7 work on a real device with real data |
| Gate 4: testing done | 27 Dec | Aly | NFR1 and NFR2 evidence collected, no P0 bug left, SUS at least 68 |

## 4. Epics at a glance

| Epic | Flow it builds | Window | Lead | Priority |
|---|---|---|---|---|
| **E0** Foundations and setup | Core Tabs, repo, Firebase, schema, contracts | now to 15 Nov (Gate 2) | Aaron, Ary, Dafi, Gathan | P0 |
| **E1** Starting and Profile Setup flows | Starting Flow, Profile Setup Flow, Core Tabs | 16 to 22 Nov | Ary | P0 |
| **E2** Host Listing flow | Host Listing Flow | 23 to 29 Nov | Gathan | P0 |
| **E3** Find a Room flow | Find a Room Flow | 30 Nov to 4 Dec | Gathan (commute: Aaron, AI search: Ilham) | P0 |
| **E4** Room Details flow | Room Details Flow | 3 to 7 Dec | Ary (commute data: Aaron) | P0 |
| **E5** Request and Chat flow | Request and Chat Flow | 5 to 9 Dec | Ary (data and rules: Gathan) | P0 |
| **E6** Match flow | Match Flow | 7 to 11 Dec | Ary (score: Aaron, reasons: Ilham) | P0 |
| **E7** Household flow (Unit tab) | Household Flow | 8 to 13 Dec (logic can start now) | Dafi (chores and rules: Aly) | P0 |
| **E8** Private Contract Check flow | Contract Check Flow | 8 to 20 Dec (prompts can start now) | Ilham | P0 |
| **E9** Evaluate Unit and Group flows (P1) | Evaluate Unit Flow, Group Flow | 14 to 20 Dec | Gathan (screens: Ary) | P1 |
| **E10** Profile, privacy and polish (P1) | Profile and Safety Flow | 14 to 20 Dec | Ary | P1 |
| **E11** Optional (P2) | only if P0 and P1 are on track | after 20 Dec | whoever has time | P2 |

| Track | Area | Window | Lead |
|---|---|---|---|
| **T1** Research and validation | runs in parallel | now to 15 Nov | Aly, Dafi |
| **T2** Testing and quality | runs in parallel | plan from Nov, run 14 to 27 Dec | Aly |
| **T3** Video | runs in parallel | capture by 20 Dec, edit 28 Dec to 2 Jan | Aly, Ary |
| **T4** Report and AI Usage Log | runs in parallel | draft during the build, assemble 4 to 9 Jan | Dafi, Ilham |

Legend: **P0** must work for the graded demo · **P1** should have · **P2** if time allows. `(Name)` is the person who does the
subtask. UI means Android screens, backend means server code (Worker) and rules.

## 5. Flow epics, in build order

### E0 · Foundations and setup
Flow: Core Tabs, repo, Firebase, schema, contracts · window: now to 15 Nov (Gate 2) · lead: Aaron, Ary, Dafi, Gathan.
Everything the flows need to start: repo and CI, Firebase and Maps, the design system and app shell, the data schema and Security Rules, the function contracts, and the ClickUp Space.

**E0.1 Repo and CI are healthy** (P0) · Aaron · due 2 Nov
Detail (draft, owner to confirm): [TASKS/E0/E0.1.md](../../TASKS/E0/E0.1.md)
- (Ary) Add everyone as collaborators on GitHub
- (Aaron) Fix the CI Android SDK step so the `android` job is green
- (Aaron) Merge `main` into the commute branch and fix paths after the folder change
- (Ary) Turn on branch protection: PR required, final check by Ary
- (Ary) Email the lecturer: Appendix 1, native Java OK, SDG 11 OK, what the AI policy counts as your own code, is a GitHub link needed

**E0.2 Firebase and the free server are ready** (P0) · Aaron · due 8 Nov
Detail (draft, owner to confirm): [TASKS/E0/E0.2.md](../../TASKS/E0/E0.2.md)
- (Aaron) Confirm the free-only stack with the team (Worker, openrouteservice, map tiles) and record it in the PRD
- (Aaron) openrouteservice key kept on the server, and the daily limits written down (#11)
- (Aaron) Worker skeleton with Firebase token check, and the smoke test passes in CI
- (Aaron) Check transit coverage for Pantai Dalam and Kerinchi to UM (#5)

**E0.3 Design system and app shell** (P0) · Ary · due 15 Nov
Detail (task file): [TASKS/E0/E0.3.md](../../TASKS/E0/E0.3.md) (why, what to build, acceptance, out of scope, decisions, open questions)
- (Ary) Figma tokens, components and all screens (done)
- (Ary) Components, shell and component gallery in code (done)
- (Ary) Announce the component list to the team and how to use it (done)
- (Ary) Empty and error states as components (done)

**E0.4 Project management and docs** (P0) · Dafi · due 30 Oct
Detail (draft, owner to confirm): [TASKS/E0/E0.4.md](../../TASKS/E0/E0.4.md)
- (Dafi) Create the ClickUp Space: statuses, a List per epic, a task per story with subtasks
- (Dafi) Sprint 1 plan, weekly sync time, a place for minutes
- (Dafi) Keep the PRD up to date as decisions change
- (everyone) Create your own file in `docs/ai-log/` and start the AI Usage Log

**E0.5 Data schema and Security Rules** (P0) · Gathan · due draft 8 Nov, merged 15 Nov
Detail (draft, owner to confirm): [TASKS/E0/E0.5.md](../../TASKS/E0/E0.5.md)
- (Gathan) Firestore schema from PRD section 8, plus `chats` for connect
- (Gathan) First draft of Security Rules: a user reads and writes only their own `users` and private files; household data only by members; contact details only after both sides accept
- (Gathan) Rules tests with the emulator: every unauthorised read and write is denied
- (Gathan) Send the field list to the group by 15 Nov

**E0.6 Function contracts (stubs)** (P0) · Aaron · due 15 Nov
Detail (draft, owner to confirm): [TASKS/E0/E0.6.md](../../TASKS/E0/E0.6.md)
- (Aaron) Write `docs/api-contracts.md`: request and response JSON for `getCommuteOptions`, `getTransitRoute`, `computeMatchScores`, `splitRoomPrices`
- (Aaron, Ilham) Agree the shape of each AI function
- (Aaron) Post the contracts to the group so everyone can build against stubs

---

### E1 · Starting and Profile Setup flows
Flow: Starting Flow, Profile Setup Flow, Core Tabs · window: 16 to 22 Nov · lead: Ary.
A student signs up with a UM email, verifies it, logs in, sets up a profile and lands on Home with the four tabs.

**E1.1 Sign up with a UM email and verify it** (FR1.1, P0) · Ary · due 19 Nov · WAIT UM domain list
Detail (task file): [TASKS/E1/E1.1.md](../../TASKS/E1/E1.1.md) (why, what to build, acceptance, out of scope, decisions, open questions)
- (Dafi, Ary) Decided 30 Oct 2026: UM email only. Only `siswa.um.edu.my` counts (students only, no staff mail). Still to confirm with UM or the lecturer: whether postgraduate students have another domain, and what first-year students without a UM email can do
- (Ary) Welcome, Sign up and Verify email screens (done)
- (Ary) Connect to the existing email and password repository; reject non-UM emails with a clear message (done)
- (Ary, Aaron) An unverified account can only reach the verify screen (done in the app by `StartRoute`; Aaron still to review)
- (Ary) Unit tests for the email check and the sign-up flow (done)

**E1.2 Log in, log out and reset password** (FR1.2, P0) · Ary · due 20 Nov · WAIT E1.1
Detail (task file): [TASKS/E1/E1.2.md](../../TASKS/E1/E1.2.md) (why, what to build, acceptance, out of scope, decisions, open questions)
- (Ary) Log in and Reset password screens (done)
- (Ary) Session handling and log out (done)
- (Ary) Tests for wrong password, unverified email and reset (done)

**E1.3 Profile setup and privacy** (FR1.3, P0) · Ary · due 22 Nov · WAIT `users` fields (E0.5)
Detail (task file): [TASKS/E1/E1.3.md](../../TASKS/E1/E1.3.md) (why, what to build, acceptance, out of scope, decisions, open questions)
- (Ary) Four profile steps: faculty and year, timetable, budget and lifestyle, privacy
- (Ary) Timetable entry (day, start, end, building)
- (Gathan) `users` fields and rules so hidden details stay hidden until a Connection
- (Ary) Tests for saving and loading a profile

**E1.4 Home and the four tabs after sign-in** (P0) · Ary · due 22 Nov · WAIT E1.2
Detail (task file): [TASKS/E1/E1.4.md](../../TASKS/E1/E1.4.md) (why, what to build, acceptance, out of scope, decisions, open questions)
- (Ary) Make the shell the first screen after sign-in (done)
- (Ary) Home, Rooms, Match and Unit tabs with placeholders and the Post button (done; the Post button does nothing until E2)

---

### E2 · Host Listing flow
Flow: Host Listing Flow · window: 23 to 29 Nov · lead: Gathan.
A host posts a unit with priced rooms and manages the listing.

**E2.1 Pick the building and enter rent and deposit** (FR2.1, P0) · Gathan · due 25 Nov · WAIT E0.5, E1.3
Detail (draft, owner to confirm): [TASKS/E2/E2.1.md](../../TASKS/E2/E2.1.md)
- (Gathan) `listings` data, photo upload to Storage and rules
- (Gathan) Building picker with place search
- (Gathan) Post screens, step 1 (Ary's components): photos, building, rent and deposit

**E2.2 Rooms and prices with the price rule** (FR2.1, P0) · Gathan · due 27 Nov · WAIT E2.1
Detail (draft, owner to confirm): [TASKS/E2/E2.2.md](../../TASKS/E2/E2.2.md)
- (Gathan) Room price rule in plain Java: the rooms never add up to more than the rent; default split 1.3 / 1.0 / 0.7 from config; JUnit tests
- (Gathan) Rooms and prices screen with the over-allocation error

**E2.3 My listing: renew, requests and mark occupied** (FR2.1, P0) · Gathan · due 29 Nov · WAIT E2.2
Detail (draft, owner to confirm): [TASKS/E2/E2.3.md](../../TASKS/E2/E2.3.md)
- (Gathan) My listing screen; renew after 30 days; the listing closes by itself
- (Gathan) Mark a room occupied (done with the move-in in E5.3)
- (Gathan) Rules tests: only the owner can write

**E2.4 Fictional seed listings** (P0) · Gathan · due 29 Nov · *can start early* · WAIT E0.5
Detail (draft, owner to confirm): [TASKS/E2/E2.4.md](../../TASKS/E2/E2.4.md)
- (Gathan) Note reference prices by hand (area, room type, price range) with the source and date; do not copy listings (rule in ASSUMPTIONS.md)
- (Gathan) Write 10 to 15 fictional but realistic listings and import them, labelled as sample data

---

### E3 · Find a Room flow
Flow: Find a Room Flow · window: 30 Nov to 4 Dec · lead: Gathan (commute: Aaron, AI search: Ilham).
A student searches rooms, sees the true monthly cost and travel time on each card, and can type the request in plain words.

**E3.1 Rooms screen and Room cards** (FR2.2, P0) · Gathan · due 2 Dec · WAIT E2.4
Detail (draft, owner to confirm): [TASKS/E3/E3.1.md](../../TASKS/E3/E3.1.md)
- (Gathan) Query vacant rooms; Rooms screen with Room cards (Ary's components)
- (Gathan) Saved rooms

**E3.2 Filters** (FR2.2, P0) · Gathan · due 3 Dec · WAIT E3.1
Detail (draft, owner to confirm): [TASKS/E3/E3.2.md](../../TASKS/E3/E3.2.md)
- (Gathan) Filters sheet: budget, room type, area, gender, amenities
- (Gathan) Travel-time filter that calls the commute function (E3.3)

**E3.3 Commute per travel mode** (FR4.1, P0) · Aaron · due start now, due 29 Nov · *can start early* · WAIT transit coverage and fares (#3, #4, #5), shuttle timetable (T1)
Detail (draft, owner to confirm): [TASKS/E3/E3.3.md](../../TASKS/E3/E3.3.md)
- (Aaron) openrouteservice adapter, GTFS transit data, pricing config and the e-hailing fare formula
- (Aaron) Shuttle timetable as scheduled data (from T1)
- (Aaron) Label every figure as an estimate and name its source
- (Aaron) Unit tests with fixed inputs

**E3.4 True monthly cost and travel time on the Room card** (FR4.2, P0) · Aaron · due 4 Dec · WAIT E3.1, E3.3
Detail (draft, owner to confirm): [TASKS/E3/E3.4.md](../../TASKS/E3/E3.4.md)
- (Aaron) True cost = room price + utilities share + cheapest mode that is on time
- (Gathan) Room card shows cost and travel time; "no mode reaches class on time" message
- (Aaron) Cache results for the same area and faculty (NFR1)

**E3.5 Plain-language AI search** (FR6.1, P0) · Ilham · due 4 Dec · *can start early* · WAIT E3.2 for the real filters (use mock filters first)
Detail (draft, owner to confirm): [TASKS/E3/E3.5.md](../../TASKS/E3/E3.5.md)
- (Ilham) Function that turns English or Malay text into filters (mock filters first)
- (Ilham) Editable filters shown before results load, in the Rooms search bar; manual filters when the AI is down
- (Ilham) 50 test requests written by surveyed students

---

### E4 · Room Details flow
Flow: Room Details Flow · window: 3 to 7 Dec · lead: Ary (commute data: Aaron).
A student opens a room, sees the Host, the rooms in the unit and how to get to class, and can message the Host or report the listing.

**E4.1 Room details screen** (FR2.2, P0) · Ary · due 4 Dec · WAIT E3.1
Detail (draft, owner to confirm): [TASKS/E4/E4.1.md](../../TASKS/E4/E4.1.md)
- (Ary) Room details: photos, price, true cost, Host, rooms in the unit, share and save
- (Ary) Report listing sheet

**E4.2 Commute options card** (FR4.3, P0) · Ary · due 7 Dec · WAIT E3.3 (use the stub until it lands)
Detail (draft, owner to confirm): [TASKS/E4/E4.2.md](../../TASKS/E4/E4.2.md)
- (Aaron) Response with time, cost and CO₂ per mode and the cheapest on-time option; CO₂ factors from a published source (#6)
- (Ary) Commute options card on the room detail page

**E4.3 Send a request to the Host** (FR3.3, P0) · Ary · due 7 Dec · WAIT E5.1
Detail (draft, owner to confirm): [TASKS/E4/E4.3.md](../../TASKS/E4/E4.3.md)
- (Ary) Send request sheet with a short message
- (Gathan) Connection request data and rules

**E4.4 Public transport route page, peak and off-peak** (FR4.1, FR4.3, P1) · Aaron · due 14 Dec · WAIT E4.2
Detail (draft, owner to confirm): [TASKS/E4/E4.4.md](../../TASKS/E4/E4.4.md)
- (Aaron) Peak windows (#2), surge range for e-hailing, step-by-step transit route
- (Ary) Public transport route page with the MapLibre map, and the Peak / Off-peak toggle

---

### E5 · Request and Chat flow
Flow: Request and Chat Flow · window: 5 to 9 Dec · lead: Ary (data and rules: Gathan).
A request is accepted, becomes a Connection, the two people chat and arrange a viewing, and the new tenant confirms the move-in.

**E5.1 Requests inbox and Connection** (FR3.3, P0) · Ary · due 7 Dec · WAIT E1.3
Detail (draft, owner to confirm): [TASKS/E5/E5.1.md](../../TASKS/E5/E5.1.md)
- (Ary) Messages screen with Requests: accept or decline
- (Gathan) `chats` data and rules for what each stage may see (contact details only after both accept)

**E5.2 Chat and viewing** (FR3.3, P0) · Ary · due 9 Dec · WAIT E5.1
Detail (draft, owner to confirm): [TASKS/E5/E5.2.md](../../TASKS/E5/E5.2.md)
- (Ary) Chat screen, viewing proposal card and the private agreement card
- (Gathan) A contract file shared in a chat is private to the two people

**E5.3 Move-in confirmation** (FR3.3, FR5.1, P0) · Ary · due 9 Dec · WAIT E5.2
Detail (draft, owner to confirm): [TASKS/E5/E5.3.md](../../TASKS/E5/E5.3.md)
- (Ary) Host marks the room occupied, the tenant confirms the move-in
- (Dafi) The Household is created with the same members (E7.2)

**E5.4 Notifications** (P1) · Gathan · due 13 Dec · WAIT E5.1
Detail (draft, owner to confirm): [TASKS/E5/E5.4.md](../../TASKS/E5/E5.4.md)
- (Gathan) FCM push for requests and chat messages
- (Ary) Notifications screen

---

### E6 · Match flow
Flow: Match Flow · window: 7 to 11 Dec · lead: Ary (score: Aaron, reasons: Ilham).
A student answers the questions, sees ranked matches with a score and reasons, and sends a request to connect.

**E6.1 Compatibility questions** (FR3.1, P0) · Ary · due 8 Dec · WAIT E1.3
Detail (draft, owner to confirm): [TASKS/E6/E6.1.md](../../TASKS/E6/E6.1.md)
- (Ary) Match tab start and the question screens
- (Gathan) `matchProfiles` data
- (Ary) Tests for saving answers

**E6.2 Match score function** (FR3.2, P0) · Aaron · due 10 Dec · *can start early* · WAIT match weights (#9), `matchProfiles` (E6.1)
Detail (draft, owner to confirm): [TASKS/E6/E6.2.md](../../TASKS/E6/E6.2.md)
- (Aaron) Transparent weighted formula, deterministic, unit-tested; the AI never computes the score

**E6.3 Match reasons** (FR3.2, P0) · Ilham · due 10 Dec · WAIT E6.2
Detail (draft, owner to confirm): [TASKS/E6/E6.3.md](../../TASKS/E6/E6.3.md)
- (Ilham) Reasons from the score breakdown; rule-generated reasons if the AI fails

**E6.4 Match list, match profile and connect** (FR3.2, FR3.3, P0) · Ary · due 11 Dec · WAIT E6.2 (fake scores meanwhile), E5.1
Detail (draft, owner to confirm): [TASKS/E6/E6.4.md](../../TASKS/E6/E6.4.md)
- (Ary) Match results and match profile screens
- (Ary) Connect request from a match (reuses E5.1)

---

### E7 · Household flow (Unit tab)
Flow: Household Flow · window: 8 to 13 Dec (logic can start now) · lead: Dafi (chores and rules: Aly).
People who live together track shared expenses, see who owes whom, and run chores and house rules.

**E7.1 Balance and settle-up logic** (FR5.1, P0) · Dafi · due start now, due 29 Nov · *can start early*
Detail (draft, owner to confirm): [TASKS/E7/E7.1.md](../../TASKS/E7/E7.1.md)
- (Dafi) `Expense`, `Household`, `computeBalances()`, a settle-up that minimises the number of transfers, in plain Java
- (Dafi) JUnit: balances sum to zero, rounding, 2 and 4 members, a member who paid nothing

**E7.2 Household data and rules** (FR5.1, P0) · Gathan · due 8 Dec · WAIT E0.5
Detail (draft, owner to confirm): [TASKS/E7/E7.2.md](../../TASKS/E7/E7.2.md)
- (Gathan) `households` and `expenses` data and rules, members only
- (Dafi) Household created from a Group or by invite code (with E5.3)

**E7.3 Expenses and balances screens** (FR5.1, P0) · Dafi · due 12 Dec · WAIT E7.1, E7.2
Detail (draft, owner to confirm): [TASKS/E7/E7.3.md](../../TASKS/E7/E7.3.md)
- (Dafi) Unit tab: add expense (equal, by amount, by percent), balances, mark as settled (Ary's components)
- (Dafi) Rent split by room price, utilities equally

**E7.4 Move out** (FR5.1, P0) · Dafi · due 13 Dec · WAIT E7.3
Detail (draft, owner to confirm): [TASKS/E7/E7.4.md](../../TASKS/E7/E7.4.md)
- (Dafi) Move-out screen: the room becomes vacant, unsettled balances stay owed
- (Dafi) Hand over the Host role if the Host leaves

**E7.5 Recurring chores** (FR5.2, P1) · Aly · due 13 Dec · *can start early* · WAIT E7.2 (logic can start now)
Detail (draft, owner to confirm): [TASKS/E7/E7.5.md](../../TASKS/E7/E7.5.md)
- (Aly) Rotation logic in plain Java with JUnit
- (Aly) Chores screen and reminders (FCM from E5.4)

**E7.6 House rules** (FR5.3, P1) · Aly · due 13 Dec · WAIT E7.2
Detail (draft, owner to confirm): [TASKS/E7/E7.6.md](../../TASKS/E7/E7.6.md)
- (Aly) Rules model; changing a rule resets everyone's acceptance
- (Aly) House rules screen

---

### E8 · Private Contract Check flow
Flow: Contract Check Flow · window: 8 to 20 Dec (prompts can start now) · lead: Ilham.
A student checks a tenancy agreement the Host shared in the chat, privately, and sees each clause marked Red flag, Check or OK.

**E8.1 Choose and set up the AI provider** (P0) · Ilham · due 8 Nov · *can start early*
Detail (draft, owner to confirm): [TASKS/E8/E8.1.md](../../TASKS/E8/E8.1.md)
- (Ilham) Compare Gemini and Claude on cost, quota, speed, Malay and English quality (#10)
- (Ilham) Keys in Worker secrets only
- (Ilham) Start the AI Usage Log and keep it up to date

**E8.2 Sample agreements and the contract function** (FR6.2, P0) · Ilham · due 13 Dec · *can start early* · WAIT E8.1
Detail (draft, owner to confirm): [TASKS/E8/E8.2.md](../../TASKS/E8/E8.2.md)
- (Ilham) Collect 10 sample agreements (anonymised) and the risky clauses of each
- (Ilham) Function that marks each clause Red flag, Check or OK

**E8.3 Upload and result screens** (FR6.2, P0) · Ilham · due 20 Dec · WAIT E5.2 (the file arrives in a chat)
Detail (draft, owner to confirm): [TASKS/E8/E8.3.md](../../TASKS/E8/E8.3.md)
- (Ilham) Upload and result screens (Ary's components); they open only from a chat attachment or Profile, never from a listing
- (Gathan) Private file rules and deletion after 30 days

**E8.4 AI labels and reporting a wrong result** (FR6.3, P0) · Ilham · due 20 Dec · WAIT E3.5, E6.3, E8.3
Detail (draft, owner to confirm): [TASKS/E8/E8.4.md](../../TASKS/E8/E8.4.md)
- (Ilham) "AI-generated" label on every AI output, "not legal advice", a button to report a wrong result

**E8.5 AI evaluation for the report** (FR6.1, FR6.2, P0) · Ilham · due 20 Dec · WAIT E3.5, E8.3
Detail (draft, owner to confirm): [TASKS/E8/E8.5.md](../../TASKS/E8/E8.5.md)
- (Ilham) Accuracy on the 50 requests (target 90%), flags found and false alarms on the 10 contracts, mean rating of the reasons

---

### E9 · Evaluate Unit and Group flows (P1)
Flow: Evaluate Unit Flow, Group Flow · window: 14 to 20 Dec · lead: Gathan (screens: Ary).
Friends evaluate any unit, compare units, agree a room price split and confirm a group. Only after the P0 flows work.

**E9.1 Evaluate any unit** (FR2.4, P1) · Gathan · due 17 Dec · WAIT E3.4
Detail (draft, owner to confirm): [TASKS/E9/E9.1.md](../../TASKS/E9/E9.1.md)
- (Gathan) `unitEvaluations` data, room price split, delete after 90 days
- (Ary) Evaluate form and result screens

**E9.2 Group, split options and member status** (FR3.3, P1) · Ary · due 19 Dec · WAIT E5.2
Detail (draft, owner to confirm): [TASKS/E9/E9.2.md](../../TASKS/E9/E9.2.md)
- (Gathan) `groups` data and rules
- (Ary) Group screen, split options (equal, by room type, custom), member status, pre-signing checklist, choose the Host

**E9.3 Compare units** (FR3.3, P1) · Ary · due 20 Dec · WAIT E9.1, E9.2
Detail (draft, owner to confirm): [TASKS/E9/E9.3.md](../../TASKS/E9/E9.3.md)
- (Ary) Compare two or three evaluations side by side

**E9.4 Area page and building reviews** (FR2.3, P1) · Gathan · due 20 Dec · WAIT E2, E3
Detail (draft, owner to confirm): [TASKS/E9/E9.4.md](../../TASKS/E9/E9.4.md)
- (Gathan) Area rent ranges and moderated reviews, only from residents
- (Ary) Area page and review screens

---

### E10 · Profile, privacy and polish (P1)
Flow: Profile and Safety Flow · window: 14 to 20 Dec · lead: Ary.
The profile, privacy and settings screens are finished and every flow has empty and error states.

**E10.1 Profile and privacy settings screens** (FR1.3, P1) · Ary · due 18 Dec · WAIT E1.3
Detail (draft, owner to confirm): [TASKS/E10/E10.1.md](../../TASKS/E10/E10.1.md)
- (Ary) Profile screen, privacy settings by stage, Contract check row

**E10.2 Polish: empty states, errors, accessibility** (P1) · Ary · due 20 Dec · WAIT all P0 flows
Detail (draft, owner to confirm): [TASKS/E10/E10.2.md](../../TASKS/E10/E10.2.md)
- (Ary) Empty and error states in every flow
- (Ary) Check touch targets, contrast and status words; EN and BM text

---

### E11 · Optional (P2)
Flow: only if P0 and P1 are on track · window: after 20 Dec · lead: whoever has time.
Shared commute groups, impact dashboard, UM planning dashboard, the "Paste listing" AI shortcut. Backlog with the `P2` label.

Create these as stories in Backlog with the `P2` label if time is left.


## 6. Tracks (run in parallel, by date)

### T1 · Research and validation
Track: runs in parallel with the flows · window: now to 15 Nov · lead: Aly, Dafi.
Survey, interviews and the numbers the app needs. Runs in parallel with E0.

**T1.1 Survey and interviews** (P0) · Aly · due 8 Nov (launch 31 Oct)
Detail (draft, owner to confirm): [TASKS/T1/T1.1.md](../../TASKS/T1/T1.1.md)
- (Aly) Put SURVEY.md into a Google Form, English and Malay; pilot with 5 people, aim for 8 minutes
- (Ary, Aly) Review the survey wording so the questions match the screens
- (everyone) Share the survey link in your circles
- (Aly) 100+ responses across faculties and areas (not only FSKTM); ask Telegram admins for permission
- (Aly) Plan 5 to 8 interviews and a consent form

**T1.2 Survey analysis** (P0) · Aly · due 15 Nov · WAIT T1.1
Detail (draft, owner to confirm): [TASKS/T1/T1.2.md](../../TASKS/T1/T1.2.md)
- (Aly) Send room prices to Gathan, peak hours and fares to Aaron, feature importance to Dafi
- (Aly, Dafi) Table "finding, FR it shaped"

**T1.3 Verify the assumed numbers** (P0) · Dafi · due 13 Nov · WAIT T1.1
Detail (draft, owner to confirm): [TASKS/T1/T1.3.md](../../TASKS/T1/T1.3.md)
- (Aly, Gathan) #1 Room price weights, #7 Utilities estimate · 10 Nov
- (Aly, Aaron) #2 Peak hours · 12 Nov
- (Aaron, Aly) #3 E-hailing fares, #9 Match score weights · 13 Nov
- (Aaron, Dafi) #4 Car cost, #5 Public transport (coverage 8 Nov, data 13 Nov)
- (Aaron) #6 CO₂ factors · 13 Nov, #11 Maps cost and quota · 8 Nov
- (Gathan, Dafi) #8 Data retention against PDPA 2010 · 13 Nov
- (Ilham) #10 AI provider · 8 Nov

**T1.4 UM shuttle timetable and commute estimates** (P0) · Dafi · due 8 Nov (timetable), 13 Nov (estimates)
Detail (draft, owner to confirm): [TASKS/T1/T1.4.md](../../TASKS/T1/T1.4.md)
- (Dafi) Find the shuttle timetable: routes, stops, times, days. A timetable is enough, no live tracking
- (Dafi) Email UM transport only if it cannot be found; also ask if the shuttle is free and the parking fee
- (Dafi, Aaron) Estimate commute time and cost for a few example routes; write them as estimates with sources

**T1.5 Requirements freeze** (P0) · Dafi · due 15 Nov · WAIT T1.2
Detail (draft, owner to confirm): [TASKS/T1/T1.5.md](../../TASKS/T1/T1.5.md)
- (Dafi) Confirm P0 and P1 with Ary and freeze the PRD

---

### T2 · Testing and quality
Track: runs in parallel with the flows · window: plan from Nov, run 14 to 27 Dec · lead: Aly.
Evidence that the app works and meets NFR1 and NFR2.

**T2.1 Test plan and integration tests** (P0) · Aly · due 15 Nov (plan), 27 Dec (run)
Detail (draft, owner to confirm): [TASKS/T2/T2.1.md](../../TASKS/T2/T2.1.md)
- (Aly) One test case per FR, plus NFR1 and NFR2
- (Aly) Espresso scaffolding and an integration test for each main flow

**T2.2 NFR1: speed under load** (P0) · Aly · due 27 Dec · WAIT working search
Detail (draft, owner to confirm): [TASKS/T2/T2.2.md](../../TASKS/T2/T2.2.md)
- (Aly) k6 script (`backend/tests/load`), 50 then 100 concurrent users, report the 95th percentile (target 3 s)

**T2.3 NFR2: privacy and security evidence** (P0) · Gathan · due 27 Dec · WAIT all rules
Detail (draft, owner to confirm): [TASKS/T2/T2.3.md](../../TASKS/T2/T2.3.md)
- (Gathan) Rules tests where every unauthorised read and write is denied
- (Gathan) Privacy checklist (PDPA 2010) with screenshots

**T2.4 Usability test** (P0) · Aly · due 27 Dec · WAIT working app
Detail (draft, owner to confirm): [TASKS/T2/T2.4.md](../../TASKS/T2/T2.4.md)
- (Aly) 8 to 10 UM students, five tasks, SUS at least 68 and 80% task success

**T2.5 Release check** (P0) · Aly · due 27 Dec · WAIT all P0 flows
Detail (draft, owner to confirm): [TASKS/T2/T2.5.md](../../TASKS/T2/T2.5.md)
- (Aly) No P0 bug left before recording; release checklist signed

---

### T3 · Video (due 12:00, 3 Jan 2027, 10 to 15 minutes)
Track: runs in parallel with the flows · window: capture by 20 Dec, edit 28 Dec to 2 Jan · lead: Aly, Ary.
A clear demo where every member presents their own part.

**T3.1 Script and screen captures** (P0) · Ary · due 20 Dec
Detail (draft, owner to confirm): [TASKS/T3/T3.1.md](../../TASKS/T3/T3.1.md)
- (everyone) Record your own screen capture and name the FRs you show
- (Ary) Overview (1:30), design and usability (1:00)
- (Aaron, Ilham) Technical implementation and AI data flow (1:30)
- (Aly, Gathan) NFR verification (1:30)
- (Aly) Stakeholder validation (1:00)
- (Dafi) Team contributions (0:30) and closing summary (0:45)

**T3.2 Record, edit and final cut** (P0) · Aly · due 2 Jan · WAIT T3.1
Detail (draft, owner to confirm): [TASKS/T3/T3.2.md](../../TASKS/T3/T3.2.md)
- (Aly) Assemble and edit
- (Ary) Check against the rubric

---

### T4 · Report and AI Usage Log (due 12:00, 10 Jan 2027, 2,500 words and 15 pages at most)
Track: runs in parallel with the flows · window: draft during the build, assemble 4 to 9 Jan · lead: Dafi, Ilham.
The report, written section by section as each part is built, and an accurate AI Usage Log.

**T4.1 Report sections** (P0) · Dafi · due 9 Jan · WAIT each owner's part
Detail (draft, owner to confirm): [TASKS/T4/T4.1.md](../../TASKS/T4/T4.1.md)
- (Ary) 1 Introduction (300 words), UI and colours part of 3
- (Aly) 2a User study findings (350), 5 Testing and validation (300)
- (Dafi) 2b Functional requirements (19 FRs in tables), 7 Team roles and contributions, 8 References and the final style pass
- (Gathan) 2c Non-functional requirements (150), database schema part of 3
- (Aaron) 3 Architecture (350), 4 System implementation (400)
- (Ilham) AI usage statement in 4, 6 Impact, limitations, future work (150)

**T4.2 AI Usage Log appendix (Appendix 2(b))** (P0) · Ilham · due 9 Jan · WAIT everyone's logs
Detail (draft, owner to confirm): [TASKS/T4/T4.2.md](../../TASKS/T4/T4.2.md)
- (everyone) Keep your own `docs/ai-log/<name>.md` current, with exact prompts
- (Ilham) Merge into the appendix; check that nothing is invented


## 7. Weekly routine (Dafi)
- Daily: look for `Blocked` and `WAIT` stories, and check that the current epic is moving.
- Twice a week: a short sync.
- Weekend: demo, retro, update risks, post the hours summary and a screenshot of the Board view.
