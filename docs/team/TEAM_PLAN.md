# Jiran: Work Division (from the WIA2007 brief)

Source: the course brief (`MAD Group Project.docx`) and the [PRD](../product/PRD.md).
Work is divided **by module**, with one owner per module and specialists helping (see Section 2). Day-to-day work is tracked as Initiative, Epics, Stories and Subtasks in [trello-cards.md](trello-cards.md).

## 1. What the brief requires (and what it is marked on)

| Deliverable | Due | Weight | Hard constraints |
|---|---|---|---|
| Video | 12pm Sun 3 Jan 2027 | 10% | 10–15 min; **every member presents their own part** |
| Report | 12pm Sun 10 Jan 2027 | 10% | ≤15 pages **and ≤2,500 words** (excl. appendices), 12pt Times New Roman, double-spaced (tables exempt) |
| Peer evaluation | after submission | multiplier | Final mark = (video + report) × contribution factor (0–1.0) |
| AI Usage Log | appendix to report | mandatory | Tool, exact prompts, how output was tested/modified. Missing/inaccurate log = penalties |

Other rules: ≥20 h per member; **every member must take part in development** and no one may do *only* documentation; every member's tasks need evidence (Git commits / Trello); ≥5 modules × 3 FRs; ≤2 NFRs; late submission needs lecturer approval *before* the deadline (−5%/day).

**Video marking (100):** Overview 10 · **System demo 30** · Design/usability 10 · Technical implementation 10 · NFR verification 10 · Stakeholder validation 10 · Team contributions 10 · Closing 10
**Report marking (100):** Intro 10 · User study 10 · **FRs 15** · NFRs 5 · Architecture/design 10 · **System implementation 20** · Testing 10 · Impact/limits 5 · Team roles 10 · References/writing 5

## 2. Roles and module ownership

Work is divided **by module**: each module has one owner who builds it end to end (screens, logic, data, tests) with help from the specialists. Four core developers carry five of the six modules; the two support members (project manager, QA and researcher) take the lightest module together, so everyone still writes real code as the brief requires.

| Person | Role | Owns | Also responsible for |
|---|---|---|---|
| **Ary** | Group leader, Frontend Lead, Lead UI/UX | **Module 1** Auth and verified profile (FR1.1–1.3, epic E1) and **Module 3** Roommate matching screens (FR3.1–3.3, epic E3) | Figma and design system; Android app shell, theme and shared components; final check-in (approve and merge) of every PR; main contact with the lecturer |
| **Aaron** | Software Architect and Backend Lead | **Module 4** Commute planner and true cost (FR4.1–4.3, epic E4) | Architecture, Cloud Functions structure, CI and Firebase emulator, branching rules, integration freeze; the match score Cloud Function for Module 3 |
| **Gathan** | Backend and Database Developer | **Module 2** Listings and area guide (FR2.1–2.4, epic E2) | Firestore schema and Security Rules (NFR2) for all modules, Storage rules, 10 to 15 fictional seed listings from reference prices (never copied from other sites), FCM notifications |
| **Ilham** | AI Engineer | **Module 6** AI assistant and private contract checker (FR6.1–6.3, epic E6) | The match-reason text for Module 3 (FR3.2), AI evaluation, API key handling, **AI Usage Log** |
| **Dafi** | Project Manager | **Module 5** Shared house tools: FR5.1 expenses and balances (E5.1) | Trello and sprints, meetings and minutes, deadlines and risks, PRD and requirements, finding the UM shuttle timetable (an email to UM transport only if needed), report assembly, peer-evaluation evidence |
| **Aly** | QA Engineer and UX Researcher | **Module 5** FR5.2 chores and FR5.3 house rules (E5.2, E5.3, with Dafi) | Survey and interviews, test plan, integration tests and load test (NFR1), usability test (SUS), release checklist |

**Personal task files** (each lists that person's stories with priority, due date and what they wait for): [Ary](ary.md), [Dafi](dafi.md), [Aaron](aaron.md), [Gathan](gathan.md), [Ilham](ilham.md), [Aly](aly.md).

### How the work is tracked

The full breakdown is in [trello-cards.md](trello-cards.md): the **initiative** (Jiran, delivered by 3 and 10 January 2027), 12 **epics** (E0 foundations, E1 to E6 the six modules, E7 research, E8 testing, E9 video, E10 report, E11 optional), 43 **stories** (one card each) and the **subtasks** (checklist items, one person each). Story IDs such as `E1.2` are used in the personal files and in the step tables below.

| Epic | Area | Lead |
|---|---|---|
| E0 | Foundations and setup (repo and CI, Firebase and Maps, design system and shell, project management, schema and rules, function contracts) | Aaron, Ary, Dafi, Gathan |
| E1 | Auth and profile (Module 1) | Ary |
| E2 | Listings and area guide (Module 2) | Gathan |
| E3 | Roommate matching (Module 3) | Ary (score: Aaron, reasons: Ilham, chat data: Gathan) |
| E4 | Commute and true monthly cost (Module 4) | Aaron (shuttle timetable: Dafi) |
| E5 | Shared house tools (Module 5) | Dafi, Aly |
| E6 | AI assistant and private contract check (Module 6) | Ilham |
| E7 | Research and validation (survey, numbers, requirements freeze) | Aly, Dafi |
| E8 | Testing and quality (test plan, NFR1, NFR2, usability, release) | Aly |
| E9 | Video | Aly, Ary |
| E10 | Report and AI Usage Log | Dafi, Ilham |
| E11 | Optional P2 | only if P0 and P1 are on track |

Role titles to write in the report: Ary (project leader, frontend developer, UI/UX designer), Dafi (project manager, developer), Aaron (software architect, backend developer), Gathan (database developer), Ilham (AI developer), Aly (tester, researcher, developer).

Names are assigned to roles by my best guess, not known skill; swap people between roles if someone is stronger elsewhere.

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
| Architecture, APIs, Cloud Functions, branching, integration | Aaron | Ary, Gathan |
| UI/UX, design system, screen flow | Ary | Aly (user feedback) |
| Data model and security rules | Gathan | Aaron |
| AI prompts, model and evaluation | Ilham | Aaron |
| Release readiness (quality gate) | Aly | Ary |

### Working agreements

- **Cloud Functions are Node.js** (Firebase does not support Java); the rest of the app is Java. Aaron and Ilham write them, kept small and simple: one function per job, no framework, unit tests with Jest.
- **Definition of Done:** merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card on the board moved with hours logged, and checked by QA.
- **PR check-in:** Ary does the final check and merge of every PR. Before that, Aaron reviews Cloud Functions and Maps PRs, Gathan reviews schema and rules PRs, and one other member reviews each PR so everyone has review evidence.
- **Tests:** every module owner writes unit tests for their module; Aly writes the integration tests and load test.
- **Sprints:** one-week sprints planned by Dafi, short sync twice a week, 30-minute demo and retro on weekends.
- **Commit messages and Trello story cards** name the module and FR (or the story ID), e.g. `[Mod5][FR5.3] expense split`, so each person's contribution is traceable.
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
| Aly | Puts the survey live, starts interviews, begins the test plan (E7.1, E8.1) |
| Dafi | Sets up the Trello board and sprint plan, finds the UM shuttle timetable, keeps the PRD up to date (E0.4, E4.5) |
| Ary | Figma, design system and app shell are done; adds collaborators, emails the lecturer, announces the components (E0.1, E0.3) |
| Aaron | GitHub repo, branching rules, CI, Firebase project, Cloud Functions skeleton, Maps API key (E0.1, E0.2) |
| Gathan | Firestore schema draft, a first version of the Security Rules, fictional seed listings (E0.5) |
| Ilham | Collects 10 sample tenancy agreements, tries prompts for the contract checker and picks the AI provider (E6.1, E6.3) |
| Everyone | Share the survey link in their circles |

**Gate 1 (8 Nov):** survey has 100+ responses, the repo runs, and the Firebase project exists.

#### Step 1: Foundations (2 – 15 Nov): short chain, then parallel

1. **Gathan first:** schema and rules merged. **WAIT:** Ary (profile and listing screens), Aaron (Cloud Functions that read data) and Dafi (household data) need the field names.
2. **In parallel with Gathan:** Aaron finishes CI and the emulator; Ary builds the app shell, theme and shared components; Dafi freezes the requirements after the survey.
3. **Handoff contract:** by 15 Nov Aaron posts the request and response format of each Cloud Function, and Gathan posts the collection fields. After that everyone can build against mock data.

**Gate 2 (15 Nov):** requirements frozen (Dafi), app shell and schema merged, Auth working, every Cloud Function has an agreed stub.

#### Step 2: Build core modules (16 Nov – 13 Dec): two waves

**Wave A (16 – 29 Nov), all in parallel, no one waits:**

| Who | Builds |
|---|---|
| Ary | Module 1: sign-up, login, profile and timetable (E1.1 to E1.3) |
| Gathan | Module 2: post a unit with rooms, listings, search and filters, seed data (E2.1, E2.2) |
| Aaron | Module 4: basic commute calculation (walking, public transport, driving, e-hailing estimate) using stub listings (E4.1) |
| Dafi | Module 5 FR5.1: the expense and balance logic in plain Java with JUnit tests (needs no one) (E5.1) |
| Ilham | Module 6: contract checker and plain-language search against mock filters (E6.2, E6.3) |
| Aly | Integration test scaffolding, survey analysis, room-price weights from the survey, test plan (E8.1, E7.2, E7.3) |

**Wave B (30 Nov – 13 Dec), depends on Wave A (marked WAIT):**

| Who | Builds | WAIT for |
|---|---|---|
| Ary | Room details with the commute card; Module 3 screens (E4.3, E3.1 to E3.4) | Aaron's commute API (use the stub until it lands); Module 1 profile data |
| Aaron | Match score Cloud Function; true cost; P1 peak and off-peak, transit route detail (E3.2, E4.2 to E4.4) | Gathan's profile and match fields |
| Ilham | Match-reason text; connect plain-language search to the real filters (E3.2, E6.2) | Aaron's score function; Gathan's listing search |
| Dafi | Unit tab screens and rent split by room price (E5.1) | Gathan's rooms data; Ary's shared components |
| Aly | FR5.2 chores, FR5.3 house rules (E5.2, E5.3) | Dafi's household data, Ary's components |
| Gathan | Reviews and area page (P1), chats, FCM notifications, rules tests (E2.3, E2.4, E3.3, E8.3) | none |

**Gate 3 (13 Dec):** every P0 feature works on a real device with real data; **P0 code freeze**, led by Aaron.

#### Step 3: AI polish and integration (7 – 20 Dec): overlaps Step 2

- Ilham finishes the AI evaluation (50 search requests, 10 contracts) and logs every AI use.
- Aaron and Ary merge the remaining P1 features; anything not stable by 20 Dec is dropped, not rushed.
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
| 4 | Technical implementation (10): Android (Java), Firebase and Cloud Functions diagram, AI data flow | Aaron (AI flow: Ilham, 20 s) | 1:30 |
| 5 | NFR verification (10): NFR1 k6 results; NFR2 rules tests all denied | Aly (NFR1), Gathan (NFR2) | 1:30 |
| 6 | Stakeholder validation (10): survey charts, interview quotes, user testing results and what changed | Aly | 1:00 |
| 7 | Team contributions (10) | Built into segment 2; Dafi adds 30 s with the Trello hours and commit summary | 0:30 |
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
| 7. Team roles and contributions (10) | Dafi | each member writes own entry; attach Git and Trello logs | table (exempt) |
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
2. **Platform decided:** native Android in Java (the team knows Java; the brief talks about "activities"). Confirm with the lecturer when asking for Appendix 1. The SDG is also decided: SDG 11.
3. **The PRD is the source of truth.** The original proposal PDF is not maintained and can be ignored.
4. **Numbers and assumptions** (room price weights, peak hours, e-hailing fares, emission factors, retention periods and others) each have an owner and a deadline in [ASSUMPTIONS.md](../product/ASSUMPTIONS.md). They are due before the requirements freeze on 15 Nov.
5. The brief's AI policy text refers to "parallel algorithms, thread synchronization" (copied from another course). The log requirement still applies to us in full.
