# Jiran: Work Division (from the WIA2007 brief)

Source: `MAD Group Project.docx` + `Jiran — WIA2007 Group Project Proposal.pdf`.
Work is divided **by module**, with one owner per module and specialists helping (see Section 2).

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
| **Ary** | Group leader, Frontend Lead, Lead UI/UX | **Module 1** Auth and verified profile (FR1.1–1.3) and **Module 3** Roommate matching screens (FR3.1–3.3) | Figma and design system; Android app shell, theme and shared components; final check-in (approve and merge) of every PR; main contact with the lecturer |
| **Aaron** | Software Architect and Backend Lead | **Module 4** Commute planner and true cost (FR4.1–4.3) | Architecture, Cloud Functions structure, CI and Firebase emulator, branching rules, integration freeze; the match score Cloud Function for Module 3 |
| **Gathan** | Backend and Database Developer | **Module 2** Listings and area guide (FR2.1–2.3) | Firestore schema and Security Rules (NFR2) for all modules, Storage rules, seed data (30–50 listings), FCM notifications |
| **Ilham** | AI Engineer | **Module 6** AI assistant and contract checker (FR6.1–6.3) | The match-reason text for Module 3 (FR3.2), AI evaluation, API key handling, **AI Usage Log** |
| **Dafi** | Project Manager | **Module 5** Shared house tools: FR5.1 expenses and balances | Trello and sprints, meetings and minutes, deadlines and risks, PRD and requirements, stakeholder meetings (UMSU, BHEP, UMSDC), report assembly, peer-evaluation evidence |
| **Aly** | QA Engineer and UX Researcher | **Module 5** FR5.2 chores and FR5.3 house rules (with Dafi) | Survey and interviews, test plan, integration tests and load test (NFR1), usability test (SUS), release checklist |

**Personal task files:** [Ary](team/ary.md), [Dafi](team/dafi.md), [Aaron](team/aaron.md), [Gathan](team/gathan.md), [Ilham](team/ilham.md), [Aly](team/aly.md).

Role titles to write in the report: Ary (project leader, frontend developer, UI/UX designer), Dafi (project manager, developer), Aaron (software architect, backend developer), Gathan (database developer), Ilham (AI developer), Aly (tester, researcher, developer).

Names are assigned to roles by my best guess, not known skill; swap people between roles if someone is stronger elsewhere.

### What the support members must code

So the two support members meet the brief's development rule, each has real code in the graded demo:
- **Dafi:** the expense and balance logic in Java (who owes whom, rent split by room price) with unit tests, and the expense screens of the House tab.
- **Aly:** the chore rotation (FR5.2) and house rules (FR5.3), plus automated tests (Espresso UI tests and k6 load scripts).

Module 5 is the lightest module (FR5.1 is P0, FR5.2 and FR5.3 are P1), so it fits next to their project and QA work.

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

- **Definition of Done:** merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, FR card in Trello moved with hours logged, and checked by QA.
- **PR check-in:** Ary does the final check and merge of every PR. Before that, Aaron reviews Cloud Functions and Maps PRs, Gathan reviews schema and rules PRs, and one other member reviews each PR so everyone has review evidence.
- **Tests:** every module owner writes unit tests for their module; Aly writes the integration tests and load test.
- **Sprints:** one-week sprints planned by Dafi, short sync twice a week, 30-minute demo and retro on weekends.
- **Commit messages and Trello cards** name the module and FR, e.g. `[Mod5][FR5.3] expense split`, so each person's contribution is traceable.
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
| Aly | Puts the survey live, starts interviews, begins the test plan |
| Dafi | Sets up the Trello board and sprint plan, requests the UMSU, BHEP and UMSDC meetings, keeps the PRD up to date |
| Ary | Figma: tokens, colours, the five main screens |
| Aaron | GitHub repo, branching rules, CI, Firebase project, Cloud Functions skeleton, Maps API key |
| Gathan | Firestore schema draft and a first version of the Security Rules |
| Ilham | Collects 10 sample tenancy agreements and tries prompts for the contract checker (needs nothing from anyone) |
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
| Ary | Module 1: sign-up, login, profile and timetable |
| Gathan | Module 2: post a unit with rooms, listings, search and filters, seed data |
| Aaron | Module 4: basic commute calculation (walking, public transport, driving, e-hailing estimate) using stub listings |
| Dafi | Module 5 FR5.1: the expense and balance logic in plain Java with JUnit tests (needs no one) |
| Ilham | Module 6: contract checker and plain-language search against mock filters |
| Aly | Integration test scaffolding, survey analysis, room-price weights from the survey, test plan |

**Wave B (30 Nov – 13 Dec), depends on Wave A (marked WAIT):**

| Who | Builds | WAIT for |
|---|---|---|
| Ary | Room details with the commute card; Module 3 screens | Aaron's commute API (use the stub until it lands); Module 1 profile data |
| Aaron | Match score Cloud Function; P1 peak and off-peak, transit route detail | Gathan's profile and match fields |
| Ilham | Match-reason text; connect plain-language search to the real filters | Aaron's score function; Gathan's listing search |
| Dafi | House tab screens and rent split by room price | Gathan's rooms data; Ary's shared components |
| Aly | FR5.2 chores, FR5.3 house rules | Dafi's household data, Ary's components |
| Gathan | Reviews and area page (P1), FCM notifications, rules tests | none |

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
| 6 | Stakeholder validation (10): survey charts, interview quotes, UM unit feedback and what changed | Aly (survey), Dafi (UM units, 15 s) | 1:00 |
| 7 | Team contributions (10) | Built into segment 2; Dafi adds 30 s with the Trello hours and commit summary | 0:30 |
| 8 | Closing summary (10): achievements, lessons, adoption at UM | Dafi | 0:45 |

Editing and final cut: Aly records and edits; Ary reviews against the rubric. Everyone records their own screen capture.

## 4. Report division (≤2,500 words, so budget carefully)

| Section (marks) | Owner | Contributors | Word budget |
|---|---|---|---|
| 1. Introduction and problem context (10) | Ary | all: add evidence | 300 |
| 2a. User study findings (10) | Aly | Dafi (UM unit interviews), all distribute survey | 350 |
| 2b. Functional requirements (15): 6 modules × 3 FRs + SDG link | Dafi compiles | each module owner writes the justification for their FRs | tables + 250 |
| 2c. Non-functional requirements (5) | Gathan | Aly (NFR1) | 150 |
| 3. Architecture and design (10) | Aaron (architecture, AI flow with Ilham) | Ary (UI and colours), Gathan (database schema) | 350 |
| 4. System implementation (20) | Aaron | each member supplies 1 code segment and screenshots from their work; Ilham writes the AI usage statement | 400 |
| 5. Testing and validation (10) | Aly | Ilham (AI evaluation), Gathan (rules tests), Dafi (unit tests) | 300 |
| 6. Impact, limitations, future work (5) | Ilham | Ary | 150 |
| 7. Team roles and contributions (10) | Dafi | each member writes own entry; attach Git and Trello logs | table (exempt) |
| 8. References and academic writing (5) | Dafi | all; Dafi does the final style pass | refs list |
| **Appendix:** AI Usage Log (Appendix 2(b) template) | Ilham | everyone logs their own AI use as they go | not counted |

Rule: report ownership is *in addition to* code, never instead of it.

**Word-limit tactic:** the 2,500 cap is tight for 18 FRs. Put FRs in tables (double-spacing exempt) and move extended detail (full schema, all screenshots, survey data, FR to test matrix) to appendices.

## 5. Timeline (from the proposal)

| Dates | Phase | Lead | Done when |
|---|---|---|---|
| 26 Oct – 8 Nov | Survey (100+ responses) and 5–8 interviews | Aly (survey), Dafi (UM unit and UMSU meetings) | Anonymised results, quotes and consent kept |
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
3. **Member names** (Ary, Aaron, Dafi, Ilham, Gathan, Aly) still need filling in the proposal's team table (still `[Member 1–6]`).
4. **Proposal is out of date** against the PRD (SDG, modules, roles).
5. The brief's AI policy text refers to "parallel algorithms, thread synchronization" (copied from another course). The log requirement still applies to us in full.
