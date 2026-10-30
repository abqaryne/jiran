# Jiran: Work Division (from the WIA2007 brief)

Source: the course brief (`MAD Group Project.docx`) and the [PRD](../product/PRD.md).
Work is divided **by module**, with one owner per module and specialists helping (see Section 2). Day-to-day work is tracked as Initiative, Epics, Stories and Subtasks: stories and subtasks are in the task files listed in [TASK.md](../../TASK.md).

## 1. What the brief requires (and what it is marked on)

| Deliverable | Due | Weight | Hard constraints |
|---|---|---|---|
| Video | 12pm Sun 3 Jan 2027 | 10% | 10–15 min; **every member presents their own part** |
| Report | 12pm Sun 10 Jan 2027 | 10% | ≤15 pages **and ≤2,500 words** (excl. appendices), 12pt Times New Roman, double-spaced (tables exempt) |
| Peer evaluation | after submission | multiplier | Final mark = (video + report) × contribution factor (0–1.0) |
| AI Usage Log | appendix to report | mandatory | Tool, exact prompts, how output was tested/modified. Missing/inaccurate log = penalties |

Other rules: **every member must take part in development** and no one may do *only* documentation; every member's tasks need evidence (Git commits / ClickUp); ≥5 modules × 3 FRs; ≤2 NFRs; late submission needs lecturer approval *before* the deadline (−5%/day).

**Video marking (100):** Overview 10 · **System demo 30** · Design/usability 10 · Technical implementation 10 · NFR verification 10 · Stakeholder validation 10 · Team contributions 10 · Closing 10
**Report marking (100):** Intro 10 · User study 10 · **FRs 15** · NFRs 5 · Architecture/design 10 · **System implementation 20** · Testing 10 · Impact/limits 5 · Team roles 10 · References/writing 5

## 2. Roles and module ownership

Work is divided **by module**: each module has one owner who builds it end to end (screens, logic, data, tests) with help from the specialists. Four core developers carry five of the six modules; the two support members (project manager, QA and researcher) take the lightest module together, so everyone still writes real code as the brief requires.

| Person | Role | Owns | Also responsible for |
|---|---|---|---|
| **Ary** | Group leader, Frontend Lead, Lead UI/UX | **Module 1** Auth and verified profile (FR1.1–1.3) and **Module 3** Roommate matching screens (FR3.1–3.3) | Figma and design system; Android app shell, theme and shared components; final check-in (approve and merge) of every PR; main contact with the lecturer |
| **Aaron** | Software Architect and Backend Lead | **Module 4** Commute planner and true cost (FR4.1–4.3) | Architecture, server (Worker) structure, CI and Firebase emulator, branching rules, integration freeze; the match score Cloud Function for Module 3 |
| **Gathan** | Backend and Database Developer | **Module 2** Listings and area guide (FR2.1–2.4) | Firestore schema and Security Rules (NFR2) for all modules, Storage rules, 10 to 15 fictional seed listings from reference prices (never copied from other sites), FCM notifications |
| **Ilham** | AI Engineer | **Module 6** AI assistant and private contract checker (FR6.1–6.3) | The match-reason text for Module 3 (FR3.2), AI evaluation, API key handling, **AI Usage Log** |
| **Dafi** | Project Manager | **Module 5** Shared house tools: FR5.1 expenses and balances | ClickUp and sprints, meetings and minutes, deadlines and risks, PRD and requirements, finding the UM shuttle timetable (an email to UM transport only if needed), report assembly, peer-evaluation evidence |
| **Aly** | QA Engineer and UX Researcher | **Module 5** FR5.2 chores and FR5.3 house rules (with Dafi) | Survey and interviews, test plan, integration tests and load test (NFR1), usability test (SUS), release checklist |

**Personal task files** (each lists that person's stories with priority, due date and what they wait for): [Ary](ary/README.md), [Dafi](dafi/README.md), [Aaron](aaron/README.md), [Gathan](gathan/README.md), [Ilham](ilham/README.md), [Aly](aly/README.md). Starting an AI session? Use the [master prompt](master-prompt.md).

### How the work is tracked

The work is broken down into the **initiative** (Jiran, delivered by 3 and 10 January 2027), the **epics**, the **stories** (one task each) and the **subtasks** (checklist items, one person each). Story IDs such as `E1.2` are used in the personal files and below. Each story also has a task file in `TASKS/<epic>/<ID>.md`, listed in the master [TASK.md](../../TASK.md) (description, work needed, constraints, acceptance, resolution, notes); the personal files link to them.

**An epic is one flow of the app, and the epics are done in order: E0 first, then E1, E2 and so on.** Each flow is built end to end (screens, data, rules, tests) by the whole team together, then merged and tested before the next one starts. Stories marked *can start early* need nobody and may run ahead. Tracks T1 to T4 (research, testing, video, report) run in parallel by date.

| Epic | Flow it builds | Window | Lead |
|---|---|---|---|
| **E0** Foundations and setup | Core Tabs, repo, Firebase, schema, contracts | now to 15 Nov (Gate 2) | Aaron, Ary, Dafi, Gathan |
| **E1** Starting and Profile Setup flows | Starting Flow, Profile Setup Flow, Core Tabs | 16 to 22 Nov | Ary |
| **E2** Host Listing flow | Host Listing Flow | 23 to 29 Nov | Gathan |
| **E3** Find a Room flow | Find a Room Flow | 30 Nov to 4 Dec | Gathan (commute: Aaron, AI search: Ilham) |
| **E4** Room Details flow | Room Details Flow | 3 to 7 Dec | Ary (commute data: Aaron) |
| **E5** Request and Chat flow | Request and Chat Flow | 5 to 9 Dec | Ary (data and rules: Gathan) |
| **E6** Match flow | Match Flow | 7 to 11 Dec | Ary (score: Aaron, reasons: Ilham) |
| **E7** Household flow (Unit tab) | Household Flow | 8 to 13 Dec (logic can start now) | Dafi (chores and rules: Aly) |
| **E8** Private Contract Check flow | Contract Check Flow | 8 to 20 Dec (prompts can start now) | Ilham |
| **E9** Evaluate Unit and Group flows (P1) | Evaluate Unit Flow, Group Flow | 14 to 20 Dec | Gathan (screens: Ary) |
| **E10** Profile, privacy and polish (P1) | Profile and Safety Flow | 14 to 20 Dec | Ary |
| **E11** Optional (P2) | only if P0 and P1 are on track | after 20 Dec | whoever has time |
| **T1 to T4** Research, testing, video, report | parallel tracks | by date | Aly, Dafi, Ilham |

### Order of work, ClickUp and gates

Moved here from the former `work-breakdown.md` on 31 Oct 2026. The stories and their subtasks are in the task files
(`TASKS/<epic>/<ID>.md`), listed in [TASK.md](../../TASK.md).

#### How the order works

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

#### How it is set up in ClickUp

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
| **Done** | Done | all subtasks ticked, PR merged by Ary, CI green, QA passed, hours logged |

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

#### Initiative and gates

**Initiative:** Jiran, a verified UM-only app for off-campus rooms, housemates and commute (SDG 11), delivered as a working
Android app, a 10 to 15 minute video (12:00, 3 Jan 2027) and a report (12:00, 10 Jan 2027).

| Milestone | Date | Owner | Done when |
|---|---|---|---|
| Gate 1 | 8 Nov | Dafi | survey has 100+ responses, repo runs, Firebase project exists |
| Gate 2: E0 done | 15 Nov | Dafi | requirements frozen, app shell and schema merged, Auth works, every Cloud Function has an agreed stub |
| Gate 3: P0 code freeze | 13 Dec | Aaron | E1 to E7 work on a real device with real data |
| Gate 4: testing done | 27 Dec | Aly | NFR1 and NFR2 evidence collected, no P0 bug left, SUS at least 68 |

#### Weekly routine (Dafi)

- Daily: look for `Blocked` and `WAIT` stories, and check that the current epic is moving.
- Twice a week: a short sync.
- Weekend: demo, retro, update risks, post the hours summary and a screenshot of the Board view.

### What the support members must code

So the two support members meet the brief's development rule, each has real code in the graded demo:
- **Dafi:** the expense and balance logic in Java (who owes whom, rent split by room price) with unit tests, and the expense screens of the Unit tab.
- **Aly:** the chore rotation (FR5.2) and house rules (FR5.3), plus automated tests (Espresso UI tests and k6 load scripts).

Module 5 is the lightest module (FR5.1 is P0, FR5.2 and FR5.3 are P1), so it fits next to their project and QA work.

### UM unit engagement is optional (extra credit)

The brief gives **additional credit** (not required marks) for engaging a UM unit or office. The stakeholder validation marks can be earned with the survey, interviews and user testing alone. So we skip formal meetings with BHEP and UMSDC. What we keep, because it is cheap or practically needed:
- A UMSU representative among the 5 to 8 interviews (a student, easy to reach).
- The UM shuttle stops and timetable, which the commute card needs (Dafi). A timetable is enough: the shuttle is shown as scheduled, not live. Email UM transport only if it cannot be found.
- Optional, only if there is spare time: one short email each to BHEP and UMSDC. Any reply is a bonus for the report.

### Who decides what

| Decision | Decider | Consulted |
|---|---|---|
| Scope and priorities (P0/P1/P2) | Ary | Dafi, everyone |
| Schedule, task assignment, meetings | Dafi | Ary |
| Architecture, APIs, server code, branching, integration | Aaron | Ary, Gathan |
| UI/UX, design system, screen flow | Ary | Aly (user feedback) |
| Data model and security rules | Gathan | Aaron |
| AI prompts, model and evaluation | Ilham | Aaron |
| Release readiness (quality gate) | Aly | Ary |

### Working agreements

- **The server is JavaScript** on a Cloudflare Worker (free plan; Firebase Cloud Functions need billing); the rest of the app is Java. Aaron and Ilham write it, kept small and simple: one function per job, no framework, unit tests with Jest. Free plans only, no billing on any service.
- **Definition of Done:** merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, and checked by QA.
- **PR check-in:** Ary does the final check and merge of every PR. Before that, Aaron reviews server and routing PRs, Gathan reviews schema and rules PRs, and one other member reviews each PR so everyone has review evidence.
- **Tests:** every module owner writes unit tests for their module; Aly writes the integration tests and load test.
- **Sprints:** one-week sprints planned by Dafi, short sync twice a week, 30-minute demo and retro on weekends.
- **Commit messages** start with the story ID from its ClickUp task: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. No story: `[Docs]`, `[Infra]` or `[Design]`. The task holds the FR, so each commit traces to a card and a requirement.
- **Frontend:** Ary builds the shared components first; every module owner builds their own module's screens with them, and Ary reviews the UI.

### Honest risks of this split

- **Ary carries the most:** leader, UI/UX, frontend lead, PR check-in, plus Modules 1 and 3. If the load is too much, hand the Module 3 screens to Ilham or Gathan.
- **Gathan's schema and rules block everyone** in week 1, so they land before module work starts.
- **Aaron's Module 4 is the heaviest build** (peak and off-peak, transit route, e-hailing estimate, caching). Keep the P1 parts (peak and off-peak, route detail) behind the basic card.
- **The support members' evidence** must show real commits, since it feeds their peer-evaluation factor.

### Step-by-step workflow (who goes first, who works in parallel)

**Rule of thumb:** almost everything can start at the same time if each person works against an agreed stub (a sample JSON or a mock screen). Real waiting is limited to the few handoffs marked **WAIT** below.

#### Step 0: Research and setup (26 Oct – 8 Nov): everyone in parallel, nobody waits

| Who | Does |
|---|---|
| Aly | Puts the survey live, starts interviews, begins the test plan (T1.1, T2.1) |
| Dafi | Sets up the ClickUp Space and sprint plan, finds the UM shuttle timetable, keeps the PRD up to date (E0.4, T1.4) |
| Ary | Figma, design system and app shell are done; adds collaborators, announces the components (E0.1, E0.3) |
| Aaron | GitHub repo, branching rules, CI, Firebase project, Worker skeleton, openrouteservice key (E0.1, E0.2) |
| Gathan | Firestore schema draft, a first version of the Security Rules, fictional seed listings (E0.5) |
| Ilham | Picks the AI provider, collects 10 sample tenancy agreements and tries prompts for the contract checker (E8.1, E8.2) |
| Everyone | Share the survey link in their circles |

**Gate 1 (8 Nov):** survey has 100+ responses, the repo runs, and the Firebase project exists.

#### Step 1: Foundations (2 – 15 Nov): short chain, then parallel

1. **Gathan first:** schema and rules merged. **WAIT:** Ary (profile and listing screens), Aaron (server code that reads data) and Dafi (household data) need the field names.
2. **In parallel with Gathan:** Aaron finishes CI and the emulator; Ary builds the app shell, theme and shared components; Dafi freezes the requirements after the survey.
3. **Handoff contract:** by 15 Nov Aaron posts the request and response format of each Cloud Function, and Gathan posts the collection fields. After that everyone can build against mock data.

**Gate 2 (15 Nov):** requirements frozen (Dafi), app shell and schema merged, Auth working, every Cloud Function has an agreed stub.

#### Step 2: Build the flows, in order (16 Nov – 13 Dec)

The epics E1 to E7 are the P0 flows. Each one is built end to end by the team together; when it is merged and tested the next one starts. Stories marked *can start early* run ahead of their flow.

| Order | Epic (flow) | Window | Who does what |
|---|---|---|---|
| 1 | **E1** Starting and Profile Setup | 16 – 22 Nov | Ary: sign up, log in, profile, Home and the four tabs. Gathan: `users` fields and rules. |
| 2 | **E2** Host Listing | 23 – 29 Nov | Gathan: post a unit, rooms and prices, My listing, seed listings. |
| 3 | **E3** Find a Room | 30 Nov – 4 Dec | Gathan: Rooms screen and filters. Aaron: commute engine and true cost. Ilham: plain-language search. |
| 4 | **E4** Room Details | 3 – 7 Dec | Ary: Room details and the commute card. Aaron: commute data. Gathan: request data. |
| 5 | **E5** Request and Chat | 5 – 9 Dec | Ary: requests, chat, viewing, move-in. Gathan: `chats` data and rules. |
| 6 | **E6** Match | 7 – 11 Dec | Ary: questions and match screens. Aaron: score. Ilham: reasons. |
| 7 | **E7** Household | 8 – 13 Dec | Dafi: balances and screens. Gathan: household data. Aly: chores and house rules (P1). |
| 8 | **E8** Private Contract Check | 8 – 20 Dec | Ilham: contract function, screens, labels, evaluation. Gathan: private file rules. |

**Can start early (they need nobody):** Aaron's commute engine (E3.3), Dafi's balance logic (E7.1), Ilham's provider choice and contract prompts (E8.1, E8.2), Aly's chore logic (E7.5), Gathan's seed listings once the schema exists (E2.4).

**Why this order:** each flow needs the one before it (you cannot search rooms before a host can post one, or chat before a request exists). Working on one flow at a time keeps a flow demoable and reviewable, and makes the video easy to record.

**Gate 3 (13 Dec):** the P0 flows E1 to E7 work on a real device with real data; **P0 code freeze**, led by Aaron. E8 finishes by 20 Dec.

#### Step 3: AI polish and integration (7 – 20 Dec): overlaps Step 2

- Ilham finishes the AI evaluation (50 search requests, 10 contracts, E8.5) and logs every AI use.
- The P1 flows E9 and E10 are built only after the P0 flows work; anything not stable by 20 Dec is dropped, not rushed.
- Dafi starts drafting the report sections as each owner finishes their part.

#### Step 4: Testing (14 – 27 Dec): Aly leads, everyone fixes

1. Aly runs the integration tests, the load test (NFR1) and the usability test (SUS) with 8 to 10 students.
2. Gathan runs and reports the Security Rules tests (NFR2).
3. Owners fix the bugs found, in the priority order Aly sets.

**Gate 4 (27 Dec):** NFR1 and NFR2 evidence collected, no P0 bug left.

#### Step 5: Video (28 Dec – 2 Jan) and report (4 – 9 Jan)

- **Video:** record in parallel, each person captures their own segment; Aly assembles and edits, Ary reviews against the rubric.
- **Report:** Dafi assembles from the sections already drafted; Ary reviews; a final word-count and style pass.

#### What can always run in parallel

- Survey and interviews (Aly) with all coding.
- Unit tests, written by each owner alongside the code.
- Report drafting (Dafi) with the build phase.
- UI work against mock data, and backend work against stub requests.

## 3. Video division (10–15 min, target ≈ 13 min)

| # | Segment (marks) | Presenter | Time |
|---|---|---|---|
| 1 | Project overview (10): SDG 11 (target 11.1, supporting 11.2), the UM housing and commute problem | Ary | 1:30 |
| 2 | System demo (30): scenario "Mei Ling" from sign-up to setting up her household. **Each member presents the part they built and names its FRs:** Ary (sign-up, profile, roommate matching), Gathan (posting a unit with rooms, search), Aaron (commute card, peak vs off-peak), Ilham (plain-language search, contract checker), Dafi (expenses), Aly (chores and house rules) | all six | 6:30 (≈1 min each) |
| 3 | Design and usability (10): colours, 48 dp touch targets, EN and BM, privacy by default | Ary | 1:00 |
| 4 | Technical implementation (10): Android (Java), Firebase and server (Worker) diagram, AI data flow | Aaron (AI flow: Ilham, 20 s) | 1:30 |
| 5 | NFR verification (10): NFR1 k6 results; NFR2 rules tests all denied | Aly (NFR1), Gathan (NFR2) | 1:30 |
| 6 | Stakeholder validation (10): survey charts, interview quotes, user testing results and what changed | Aly | 1:00 |
| 7 | Team contributions (10) | Built into segment 2; Dafi adds 30 s with the ClickUp hours and commit summary | 0:30 |
| 8 | Closing summary (10): achievements, lessons, adoption at UM | Dafi | 0:45 |

Editing and final cut: Aly records and edits; Ary reviews against the rubric. Everyone records their own screen capture.

## 4. Report division (≤2,500 words, so budget carefully)

| Section (marks) | Owner | Contributors | Word budget |
|---|---|---|---|
| 1. Introduction and problem context (10) | Ary | all: add evidence | 300 |
| 2a. User study findings (10) | Aly | all distribute survey | 350 |
| 2b. Functional requirements (15): 19 FRs in 6 modules (Module 2 has four) + SDG link | Dafi compiles | each module owner writes the justification for their FRs | tables + 250 |
| 2c. Non-functional requirements (5) | Gathan | Aly (NFR1) | 150 |
| 3. Architecture and design (10) | Aaron (architecture, AI flow with Ilham) | Ary (UI and colours), Gathan (database schema) | 350 |
| 4. System implementation (20) | Aaron | each member supplies 1 code segment and screenshots from their work; Ilham writes the AI usage statement | 400 |
| 5. Testing and validation (10) | Aly | Ilham (AI evaluation), Gathan (rules tests), Dafi (unit tests) | 300 |
| 6. Impact, limitations, future work (5) | Ilham | Ary | 150 |
| 7. Team roles and contributions (10) | Dafi | each member writes own entry; attach Git and ClickUp logs | table (exempt) |
| 8. References and academic writing (5) | Dafi | all; Dafi does the final style pass | refs list |
| **Appendix:** AI Usage Log (Appendix 2(b) template) | Ilham | everyone logs their own AI use as they go | not counted |

Rule: report ownership is *in addition to* code, never instead of it.

**Word-limit tactic:** the 2,500 cap is tight for 19 FRs. Put FRs in tables (double-spacing exempt) and move extended detail (full schema, all screenshots, survey data, FR to test matrix) to appendices.

## 5. Timeline (from the proposal)

| Dates | Phase | Lead | Done when |
|---|---|---|---|
| 26 Oct – 8 Nov | Survey (100+ responses) and 5–8 interviews | Aly (survey, interviews including a UMSU representative) | Anonymised results, quotes and consent kept |
| 2 – 15 Nov | Requirements freeze, Figma, schema | Dafi (requirements), Ary (Figma), Gathan (schema) | FRs frozen, screens clickable, rules drafted |
| 16 Nov – 13 Dec | Build core modules (1–5) | each module owner | Each FR demoable on a device |
| 7 – 20 Dec | AI features and integration | Ilham, Aaron | FR6.x working, everything merged |
| 14 – 27 Dec | Testing and user testing (SUS ≥ 68) | Aly | NFR1 and NFR2 evidence captured |
| 28 Dec – 2 Jan | Record and edit video | Aly, Ary | Submitted before **12pm 3 Jan** |
| 4 – 9 Jan | Finish and format report | Dafi (assembly), Ary (review) | Submitted before **12pm 10 Jan** |

**Change:** start report drafting during the build phase (the proposal leaves 6 days after the video). Each owner drafts their section as soon as their part is done, so 4–9 Jan is just assembly, word-count trimming and formatting.

## 6. Issues to resolve now

1. **Group registration:** done (the brief's date, "Sunday 21 October 2026", is a Wednesday, so it was probably a typo for 25 Oct).
2. **Platform decided:** native Android in Java (the team knows Java; the brief talks about "activities"). The SDG is also decided: SDG 11.
3. **The PRD is the source of truth.** The original proposal PDF is not maintained and can be ignored.
4. **Numbers and assumptions** (room price weights, peak hours, e-hailing fares, emission factors, retention periods and others) each have an owner and a deadline in [ASSUMPTIONS.md](../product/ASSUMPTIONS.md). They are due before the requirements freeze on 15 Nov.
5. The brief's AI policy text refers to "parallel algorithms, thread synchronization" (copied from another course). The log requirement still applies to us in full.
