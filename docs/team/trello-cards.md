# Work breakdown: Initiative, Epics, Stories, Subtasks

How the work is organised on the project board (Trello or any tool). Four levels, from big to small:

```
Initiative     Jiran: a working app, a video and a report, submitted by 3 and 10 January 2027
 └─ Epic       one big area of work (a module, research, testing, video, report)
     └─ Story  one deliverable someone can finish and demo (this is the card)
         └─ Subtask   one small step with one person (this is a checklist item on the card)
```

Sources: [PRD.md](../product/PRD.md) (FRs), [TEAM_PLAN.md](TEAM_PLAN.md) (owners, waves, gates) and
[ASSUMPTIONS.md](../product/ASSUMPTIONS.md) (numbers). Update this file when the plan changes. Estimates (hours) are left
empty on purpose: fill them in at sprint planning.

## 1. How to set it up on the board

| Level | On the board |
|---|---|
| **Initiative** | the board itself (name it "Jiran") |
| **Epic** | a **colour label** (`E1 Auth` ... `E10 Report`) and one card in a list called **Epics** that holds the epic's goal and its stories as a checklist |
| **Story** | a **card** with the epic label. Title format `E1.2 Log in, log out, reset password` |
| **Subtask** | an item in the card's **checklist** `Subtasks`, written as `(Owner) what` |

**Columns for stories:** Backlog · Ready · In progress · In review · QA · Done. A story is **Done** only when all its
subtasks are ticked, the PR is merged by Ary, CI is green, QA (Aly) passed and the hours are logged.

**Labels:** priority `P0` `P1` `P2` · `Blocked` · `WAIT` (waiting for another story). **Fields or description lines:** Owner · FR ·
Estimate (h) · Actual (h) · Due · PR link · AI log link.

**Rules**
- Log your **actual hours on the story card** (minimum 20 hours each; this is the evidence for the peer evaluation).
- Max two stories In progress per person.
- If AI helped, add the entry to `docs/ai-log/<name>.md` and link it on the card.
- Screenshot the board every week (evidence for the report).
- Priorities follow the PRD: build every **P0** first, **P1** only after P0 works end to end, **P2** only if time is left.

**Story card template** (paste into the description)

```
Epic: E_   FR: FRn.n   Priority: P_   Owner: @name   Reviewer: @name   Due: date   Estimate: h
Goal: one sentence in plain words ("A student can ...")
WAIT: what this needs from someone else, and what to use meanwhile (mock data, stub)
Done when: the behaviour in the PRD works on a device; unit tests; design system only; CI green; PR merged; hours logged
```

## 2. Initiative and milestones

**Initiative:** Jiran, a verified UM-only app for off-campus rooms, housemates and commute (SDG 11), delivered as a working
Android app, a 10 to 15 minute video (12:00, 3 Jan 2027) and a report (12:00, 10 Jan 2027).

| Milestone | Date | Owner | Done when |
|---|---|---|---|
| Gate 1 | 8 Nov | Dafi | survey has 100+ responses, repo runs, Firebase project exists |
| Gate 2 | 15 Nov | Dafi | requirements frozen, app shell and schema merged, Auth works, every Cloud Function has an agreed stub |
| End of Wave A | 29 Nov | each owner | basic version of Modules 1, 2, 4, 5 and 6 works against mock data |
| Gate 3: P0 code freeze | 13 Dec | Aaron | every P0 story works on a real device with real data |
| Gate 4: testing done | 27 Dec | Aly | NFR1 and NFR2 evidence collected, no P0 bug, SUS at least 68 |

## 3. Epics and their stories

**Who builds screens:** each module owner builds their own module's screens using Ary's components. Ary builds the screens of Modules 1 and 3, the Room details and commute card, and reviews the rest.

Legend: **P0** must work for the graded demo · **P1** should have · **P2** if time allows. `(Name)` is the person who does the
subtask. UI means Android screens, backend means Cloud Functions and rules.

---

### E0 · Foundations and setup (Infra)
Goal: the repo, CI, Firebase and the app shell are ready so everyone can build. Owners: Aaron, Ary, Dafi.

**E0.1 Repo and CI are healthy** · Aaron · due 2 Nov
- (Ary) Add everyone as collaborators on GitHub
- (Aaron) Fix the CI Android SDK step so the `android` job is green
- (Aaron) Merge `main` into the commute branch and fix paths after the folder change
- (Ary) Turn on branch protection: PR required, final check by Ary
- (Ary) Email the lecturer: Appendix 1, native Java OK, SDG 11 OK, what the AI policy counts as your own code, is a GitHub link needed

**E0.2 Firebase and Maps are ready** · Aaron · due 8 Nov
- (Aaron) Decide the Functions region and the billing plan with the team
- (Aaron) Maps API key, billing alert and quota check (#11)
- (Aaron) Cloud Functions skeleton and emulator smoke test pass in CI

**E0.3 Design system and app shell** · Ary · done, finish by 15 Nov
- (Ary) Figma tokens, components and screens ✅
- (Ary) Components, shell and component gallery in code ✅
- (Ary) Announce the component list to the team and how to use it
- (Ary) Make the shell the first screen after sign-in
- (Ary) Empty and error states as components

**E0.5 Data schema and Security Rules** · Gathan · draft 8 Nov, merged 15 Nov (everyone waits for the field names)
- (Gathan) Firestore schema from PRD section 8, plus `chats` for connect
- (Gathan) First draft of Security Rules: a user reads and writes only their own `users` and private files; household data only by members; contact details only after both sides accept
- (Gathan) Rules tests with the emulator: every unauthorised read and write is denied
- (Gathan) Reference prices by hand and 10 to 15 fictional seed listings (rule in [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- (Gathan) Send the field list to the group by 15 Nov

**E0.6 Function contracts (stubs)** · Aaron · due 15 Nov
- (Aaron) Write `docs/api-contracts.md`: request and response JSON for `getCommuteOptions`, `getTransitRoute`, `computeMatchScores`, `splitRoomPrices`
- (Aaron, Ilham) Agree the shape of each AI function
- (Aaron) Post the contracts to the group so everyone can build against stubs

**E0.4 Project management** · Dafi · due 30 Oct
- (Dafi) Create this board: labels, columns, epics, story cards with subtasks
- (Dafi) Sprint 1 plan, weekly sync time, a place for minutes
- (Dafi) Keep the PRD up to date as decisions change
- (everyone) Create your own file in `docs/ai-log/` and start the AI Usage Log

---

### E1 · Auth and profile (Mod1) · owner Ary

**E1.1 Sign up with a UM email and verify it (FR1.1, P0)** · due 29 Nov (auth working by 15 Nov)
- (Dafi, Ary) Decide the UM email policy (student and staff domains); ask the lecturer if unsure
- (Ary) Welcome, Sign up and Verify email screens
- (Ary) Connect to the existing email and password repository; reject non-UM emails with a clear message
- (Ary, Aaron) An unverified account can only reach the verify screen
- (Ary) Unit tests for the email check and the sign-up flow

**E1.2 Log in, log out and reset password (FR1.2, P0)** · due 29 Nov
- (Ary) Log in and Reset password screens
- (Ary) Session handling and log out
- (Ary) Tests for wrong password, unverified email and reset

**E1.3 Profile and privacy (FR1.3, P0)** · due 29 Nov · WAIT Gathan's `users` fields (15 Nov)
- (Ary) Profile setup screens: faculty and year, timetable, budget and lifestyle, privacy
- (Ary) Timetable entry (day, start, end, building)
- (Gathan) `users` fields and rules so hidden details stay hidden until a Connection
- (Ary) Profile screen and edit
- (Ary) Tests for saving and loading a profile

---

### E2 · Listings and area guide (Mod2) · owner Gathan

**E2.1 Post a unit with priced rooms (FR2.1, P0)** · due 29 Nov
- (Gathan) `listings` and `rooms` data, photo upload to Storage, rules
- (Gathan) Building picker with place search
- (Gathan) Room price rule: the rooms never add up to more than the rent; default split 1.3 / 1.0 / 0.7 from config, with JUnit tests
- (Gathan) Post screens (using Ary's components): building, photos, rent and deposit, rooms and prices
- (Gathan) My listing: renew after 30 days, mark a room occupied
- (Gathan) Rules tests: only the owner can write

**E2.2 Search and filter rooms (FR2.2, P0)** · due 13 Dec · WAIT E4.2 for the travel-time filter
- (Gathan) Query vacant rooms with filters (budget, type, area, gender, amenities)
- (Gathan) Rooms screen with Room cards and the filters sheet (Ary's components)
- (Gathan) Travel-time filter that calls the commute function
- (Gathan) Import 10 to 15 fictional seed listings (labelled sample data)

**E2.3 Area page and building reviews (FR2.3, P1)** · due 13 Dec
- (Gathan) Area rent ranges and moderated reviews, only from residents
- (Gathan) Area page and review screens

**E2.4 Evaluate any unit (FR2.4, P1)** · due 13 Dec · WAIT E4.2
- (Gathan) `unitEvaluations` data and the room price split
- (Gathan) Evaluate form and result screens
- (Gathan) Delete an unattached evaluation after 90 days

---

### E3 · Roommate matching (Mod3) · owner Ary

**E3.1 Compatibility questions (FR3.1, P0)** · due 13 Dec · WAIT E1.3
- (Ary) Match tab start and the question screens
- (Gathan) `matchProfiles` data
- (Ary) Tests for saving answers

**E3.2 Ranked matches with score and reasons (FR3.2, P0)** · due 13 Dec · WAIT E3.1
- (Aaron) Match score function from the survey weights (#9), with JUnit tests; the AI never computes the score
- (Ilham) Reasons for each match, with rule-based reasons when the AI is down
- (Ary) Match list and match profile screens (use fake scores until the function lands)

**E3.3 Connection request and chat (FR3.3, P0)** · due 13 Dec
- (Ary) Request sheet, requests inbox (accept or decline), chat and viewing screens
- (Gathan) `chats` data, rules for what each stage may see, notifications
- (Ary) Move-in confirmation links to E5.1

**E3.4 Group and split decision aids (FR3.3, P1)** · due 13 Dec
- (Ary) Group, split options, member status, pre-signing checklist, compare units
- (Gathan) `groups` data and rules

---

### E4 · Commute and true monthly cost (Mod4) · owner Aaron

**E4.1 Commute per travel mode (FR4.1, P0)** · due 29 Nov · WAIT Maps coverage (#5, #11), fares (#3, #4)
- (Aaron) Routes adapter, pricing config and the e-hailing fare formula
- (Aaron) Shuttle timetable as scheduled data (from E4.5)
- (Aaron) Label every figure as an estimate and name its source
- (Aaron) Unit tests with fixed inputs

**E4.2 True monthly cost on the Room card (FR4.2, P0)** · due 13 Dec · WAIT E2.1
- (Aaron) True cost = room price + utilities share + cheapest mode that is on time
- (Gathan) Room card shows cost and travel time; "no mode reaches class on time" message (data from Aaron)
- (Aaron) Cache results for the same area and faculty

**E4.3 Commute options card and route page (FR4.3, P0)** · due 13 Dec
- (Aaron) Response with time, cost and CO₂ per mode and the cheapest on-time option
- (Ary) Commute card on the room detail page and the public transport route page
- (Aaron) CO₂ factors from a published source (#6)

**E4.4 Peak and off-peak (FR4.1, FR4.3, P1)** · due 13 Dec
- (Aaron) Peak windows (#2) and a surge range for e-hailing
- (Ary) Peak and off-peak toggle and route steps

**E4.5 UM shuttle timetable and commute estimates** · Dafi · due 8 Nov (timetable), 13 Nov (estimates)
- (Dafi) Find the shuttle timetable: routes, stops, times, days. A timetable is enough, no live tracking
- (Dafi) Email UM transport only if it cannot be found; also ask if the shuttle is free and the parking fee
- (Dafi, Aaron) Estimate commute time and cost for a few example routes; write them as estimates with sources

---

### E5 · Shared house tools (Mod5) · owners Dafi and Aly

**E5.1 Expenses and balances (FR5.1, P0)** · Dafi · logic 29 Nov, screens 13 Dec · WAIT Gathan's household data, Ary's components
- (Dafi) Balance and settle-up logic in plain Java with JUnit (sums to zero, rounding, 2 and 4 members, a member who paid nothing)
- (Gathan) `households` and `expenses` data and rules (members only)
- (Dafi) Unit tab screens (Ary's components): add expense (equal, by amount, by percent), balances, mark as settled
- (Dafi) Household is created from the Group or by invite code; move out keeps balances

**E5.2 Recurring chores (FR5.2, P1)** · Aly · due 13 Dec · WAIT E5.1
- (Aly) Rotation logic in plain Java with JUnit tests
- (Aly) Chores screen and reminders (FCM from Gathan)

**E5.3 House rules (FR5.3, P1)** · Aly with Dafi · due 13 Dec · WAIT E5.1
- (Aly) Rules model; changing a rule resets everyone's acceptance
- (Aly) House rules screen

---

### E6 · AI assistant and private contract check (Mod6) · owner Ilham

**E6.1 Choose and set up the AI provider (#10)** · due 8 Nov
- (Ilham) Compare Gemini and Claude on cost, quota, speed, and Malay and English quality
- (Ilham) Keys in Cloud Functions secrets only
- (Ilham) Start the AI Usage Log and keep it up to date

**E6.2 Plain-language search (FR6.1, P0)** · due 13 Dec · WAIT E2.2
- (Ilham) Function that turns a request into filters (mock filters first)
- (Ilham) Editable filters shown before results load (in the Rooms search bar); manual filters when the AI is down
- (Ilham) 50 test requests written by surveyed students

**E6.3 Private contract check (FR6.2, P0)** · due 20 Dec
- (Ilham) Collect 10 sample agreements and their risky clauses
- (Ilham) Function that marks each clause Red flag, Check or OK
- (Ilham) Contract upload and result screens (Ary's components); entry from a chat attachment or Profile only
- (Gathan) Private file rules and deletion after 30 days

**E6.4 AI labels and reporting a wrong result (FR6.3, P0)** · due 20 Dec
- (Ilham) "AI-generated" label on every AI output, "not legal advice", report a wrong result

**E6.5 AI evaluation for the report** · due 20 Dec
- (Ilham) Accuracy on the 50 requests (target 90%), flags found and false alarms on the 10 contracts, mean rating of reasons

---

### E7 · Research and validation · owner Aly

**E7.1 Survey and interviews** · due 8 Nov
- (Aly) Survey in Google Form, English and Malay; pilot with 5 people; launch
- (Ary, Aly) Review the survey wording so the questions match the screens
- (everyone) Share the survey link in your circles
- (Aly) 100+ responses across faculties and areas
- (Aly) Plan 5 to 8 interviews and a consent form

**E7.2 Survey analysis** · due 15 Nov
- (Aly) Send room prices to Gathan, peak hours and fares to Aaron, feature importance to Dafi
- (Aly, Dafi) Table "finding, FR it shaped"

**E7.3 Verify the assumed numbers** ([ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- (Aly, Gathan) #1 Room price weights, #7 Utilities estimate · 10 Nov
- (Aly, Aaron) #2 Peak hours · 12 Nov
- (Aaron, Aly) #3 E-hailing fares, #9 Match score weights · 13 Nov
- (Aaron, Dafi) #4 Car cost, #5 Public transport (coverage 8 Nov, data 13 Nov)
- (Aaron) #6 CO₂ factors · 13 Nov, #11 Maps cost and quota · 8 Nov
- (Gathan, Dafi) #8 Data retention vs PDPA 2010 · 13 Nov
- (Ilham) #10 AI provider · 8 Nov

**E7.4 Requirements freeze** · Dafi · due 15 Nov
- (Dafi) Confirm P0 and P1 with Ary and freeze the PRD

---

### E8 · Testing and quality · owner Aly

**E8.1 Test plan and integration tests** · due 29 Nov (plan), 27 Dec (run)
- (Aly) One test case per FR, plus NFR1 and NFR2
- (Aly) Espresso scaffolding and an integration test for each main flow

**E8.2 NFR1: speed under load** · due 27 Dec
- (Aly) k6 script (`backend/tests/load`), 50 then 100 concurrent users, report the 95th percentile (target 3 s)

**E8.3 NFR2: privacy and security evidence** · Gathan · due 27 Dec
- (Gathan) Rules tests where every unauthorised read and write is denied
- (Gathan) Privacy checklist (PDPA 2010) with screenshots

**E8.4 Usability test** · due 27 Dec
- (Aly) 8 to 10 UM students, five tasks, SUS at least 68 and 80% task success

**E8.5 Release check** · due 27 Dec
- (Aly) No P0 bug left before recording; release checklist signed

---

### E9 · Video (due 12:00, 3 Jan 2027, 10 to 15 minutes) · owners Aly and Ary

**E9.1 Script and screen captures** · due 20 Dec
- (everyone) Record your own screen capture and name the FRs you show
- (Ary) Overview (1:30), design and usability (1:00)
- (Aaron, Ilham) Technical implementation and AI data flow (1:30)
- (Aly, Gathan) NFR verification (1:30)
- (Aly) Stakeholder validation (1:00)
- (Dafi) Team contributions (0:30) and closing summary (0:45)

**E9.2 Record, edit and final cut** · Aly, Ary · 28 Dec to 2 Jan
- (Aly) Assemble and edit
- (Ary) Check against the rubric

---

### E10 · Report and AI Usage Log (due 12:00, 10 Jan 2027, 2,500 words and 15 pages at most) · owner Dafi

Start drafting during the build, so 4 to 9 Jan is only assembly and trimming.

**E10.1 Sections** (words)
- (Ary) 1 Introduction (300), UI and colours part of 3
- (Aly) 2a User study findings (350), 5 Testing and validation (300)
- (Dafi) 2b Functional requirements (19 FRs in tables), 7 Team roles and contributions, 8 References and the final style pass
- (Gathan) 2c Non-functional requirements (150), database schema part of 3
- (Aaron) 3 Architecture (350), 4 System implementation (400)
- (Ilham) AI usage statement in 4, 6 Impact, limitations, future work (150)

**E10.2 AI Usage Log appendix (Appendix 2(b))** · Ilham
- (everyone) Keep your own `docs/ai-log/<name>.md` current, with exact prompts
- (Ilham) Merge into the appendix; check that nothing is invented

---

### E11 · Optional (P2): only if P0 and P1 are on track
Shared commute groups (Mod7), impact dashboard, UM planning dashboard, "Paste listing" AI shortcut for Evaluate unit.
Create these stories in Backlog with the `P2` label.

## 4. Weekly routine (Dafi)
- Daily: look for `Blocked` and `WAIT` stories.
- Twice a week: a short sync.
- Weekend: demo, retro, update risks, post the hours summary and a screenshot of the board.
