# Project board cards (Trello)

Everything Dafi needs to set up the board: columns, labels, fields, a card template, and every card to create.
Source: [PRD.md](../product/PRD.md) (the FRs), [TEAM_PLAN.md](TEAM_PLAN.md) (owners, waves, gates) and
[ASSUMPTIONS.md](../product/ASSUMPTIONS.md) (numbers). Update this file when the plan changes.

## 1. Board setup

**Columns:** Backlog · Ready · In progress · In review · QA · Done

| Column | Means | Who moves it |
|---|---|---|
| Backlog | planned, not started | Dafi |
| Ready | requirements clear, nothing blocking it | owner |
| In progress | someone is working on it (max 2 cards per person) | owner |
| In review | PR open | owner |
| QA | merged, waiting for Aly's check | Ary after merge |
| Done | merged, QA passed, hours logged | Aly |

**Labels:** priority `P0` (red), `P1` (orange), `P2` (grey) · module `Mod1` Auth, `Mod2` Listings, `Mod3` Matching, `Mod4` Commute,
`Mod5` Household, `Mod6` AI · type `Infra`, `Design`, `Research`, `Report`, `Video`, `Test`, `Gate` · state `Blocked`, `WAIT`
(waiting for another card).

**Fields (Custom Fields power-up or the card description):** Owner (members) · FR · Estimate (h) · Actual (h) · Due date ·
PR link · AI log link.

**Rules**
- Log your **actual hours on the card** (minimum 20 hours each; this is the evidence for the peer evaluation).
- Title format: `[ModN][FRN.N] short title` for FR cards, `[Infra]`, `[Report]`, and so on for the rest.
- A card is Done only when: merged by PR, Lint, Checkstyle and tests pass in CI, reviewed, hours logged, QA passed.
- If you used AI for the card, add the entry to your `docs/ai-log/<name>.md` and link it on the card.
- Take a screenshot of the board every week (evidence for the report).

## 2. Card template (paste into the description)

```
**FR / requirement:** FRn.n (link to PRD section)
**Priority:** P0 / P1 / P2     **Module:** ModN
**Owner:** @name     **Reviewer:** @name (backend: Aaron, schema and rules: Gathan, final merge: Ary)
**Due:** date (wave or gate)     **Estimate:** h
**Depends on / WAIT:** card or person, and what to use meanwhile (mock data, stub)

**Done when (acceptance):**
- [ ] behaviour from the PRD acceptance criteria works on a real device or emulator
- [ ] unit tests for the logic (JVM) and a test case written for QA
- [ ] uses only design system components and tokens (docs/design/DESIGN.md)
- [ ] Lint, Checkstyle and CI are green; PR reviewed; merged by Ary
- [ ] hours logged; AI log entry added if AI was used
```

## 3. FR cards (19 cards)

Dates follow the waves in the team plan: Wave A ends 29 Nov, Wave B ends 13 Dec (Gate 3, P0 code freeze). "Backend" means the
Cloud Function or rules; "UI" means the Android screens.

| # | Card title | Priority | Owner | Reviewer | Due | Depends on / WAIT |
|---|---|---|---|---|---|---|
| 1 | `[Mod1][FR1.1]` Register with a UM student email and verify it | P0 | Ary (UI), Aaron (auth backend) | Aaron | 15 Nov (auth works, Gate 2), UI 29 Nov | UM domain policy decision |
| 2 | `[Mod1][FR1.2]` Log in, log out, reset password | P0 | Ary | Aaron | 29 Nov | card 1 |
| 3 | `[Mod1][FR1.3]` Profile: faculty, timetable, budget, lifestyle tags, owns a car, hidden fields | P0 | Ary | Gathan | 29 Nov | Gathan's `users` fields (15 Nov) |
| 4 | `[Mod2][FR2.1]` Post a unit with photos, building (Places), rent, deposit and priced rooms | P0 | Gathan | Aaron | 29 Nov | schema; room price rule (sum of rooms must not exceed rent) |
| 5 | `[Mod2][FR2.2]` Search and filter vacant rooms (budget, type, area, gender, amenities, travel time) | P0 | Gathan | Aaron | 13 Dec | card 4, card 12 (travel-time filter) |
| 6 | `[Mod2][FR2.3]` Area page and moderated building reviews | P1 | Gathan | Ary | 13 Dec | cards 4, 5 |
| 7 | `[Mod2][FR2.4]` Evaluate unit: any unit, room price split, each member's true cost | P1 | Gathan (data), Ary (screens) | Aaron | 13 Dec | cards 4, 12 |
| 8 | `[Mod3][FR3.1]` Compatibility questionnaire (7 questions) | P0 | Ary | Aaron | 13 Dec | card 3 |
| 9 | `[Mod3][FR3.2]` Ranked matches with score and reasons (score by code, reasons by AI) | P0 | Ary (screens), Aaron (score), Ilham (reasons) | Aaron | 13 Dec | cards 8, 17 |
| 10 | `[Mod3][FR3.3]` Connection request, chat, and group of 2 to 4 with room price split | P0 (connect), P1 (group) | Ary (screens), Gathan (chats, rules) | Gathan | 13 Dec | cards 3, 9 |
| 11 | `[Mod4][FR4.1]` Commute time and monthly cost per mode (walk, transit + shuttle, e-hailing, drive) | P0 (peak and off-peak: P1) | Aaron | Ary | 29 Nov | Maps coverage (#5, #11), fares (#3, #4), shuttle timetable (Dafi) |
| 12 | `[Mod4][FR4.2]` Room card shows true monthly cost and travel time | P0 | Aaron (backend), Ary (card) | Gathan | 13 Dec | cards 4, 11 |
| 13 | `[Mod4][FR4.3]` Commute options card and the public transport route page | P0 (toggle and route steps: P1) | Aaron (API), Ary (screens) | Ary | 13 Dec | card 11 |
| 14 | `[Mod5][FR5.1]` Household: shared expenses, who owes whom, rent by room price | P0 | Dafi | Aly | logic 29 Nov, screens 13 Dec | household data (Gathan), shared components (Ary) |
| 15 | `[Mod5][FR5.2]` Recurring chores with rotation and reminders | P1 | Aly | Dafi | 13 Dec | card 14, FCM (Gathan) |
| 16 | `[Mod5][FR5.3]` House rules with member acceptance | P1 | Aly (with Dafi) | Dafi | 13 Dec | card 14 |
| 17 | `[Mod6][FR6.1]` Plain-language search (English or Malay) turned into editable filters | P0 | Ilham | Aaron | 13 Dec | card 5 (real filters; use mock filters first) |
| 18 | `[Mod6][FR6.2]` Private contract check: Red flag, Check or OK | P0 | Ilham (AI), Ary (screens) | Aaron | 20 Dec | 10 sample agreements; chat attachment rules (Gathan) |
| 19 | `[Mod6][FR6.3]` AI-generated labels, "not legal advice", report a wrong result | P0 | Ilham | Ary | 20 Dec | cards 9, 17, 18 |

**Optional P2 (create only if the P0 and P1 cards are on track):** `[Mod7]` Shared commute groups · impact dashboard · UM
planning dashboard · "Paste listing" AI shortcut for Evaluate unit. Label P2, Backlog column.

## 4. Setup cards (Step 0, 26 Oct to 8 Nov)

| Card title | Labels | Owner | Due |
|---|---|---|---|
| `[Infra]` Add everyone to the GitHub repo (collect usernames) | Infra | Ary | 30 Oct |
| `[Research]` Email the lecturer: Appendix 1, Java OK, SDG 11 OK, AI policy questions, GitHub link needed? | Research | Ary | 30 Oct |
| `[Design]` Review the survey wording with Aly so questions match the screens | Design, Research | Ary | 2 Nov |
| `[Infra]` Trello board, 19 FR cards, sprint 1 plan, weekly sync time | Infra | Dafi | 30 Oct |
| `[Research]` Find the UM shuttle timetable (routes, stops, times, days); email UM transport only if needed | Research, Mod4 | Dafi | 8 Nov |
| `[Research]` Estimate commute time and cost for example routes with Aaron | Research, Mod4 | Dafi, Aaron | 13 Nov |
| `[Infra]` Fix CI (Android SDK step), merge `main` into the commute branch, check paths | Infra | Aaron | 2 Nov |
| `[Infra]` Maps API key, billing alert, quota check (#11); transit coverage (#5) | Infra, Mod4 | Aaron | 8 Nov |
| `[Mod2]` Firestore schema from PRD section 8, plus `chats` | Mod2 | Gathan | 8 Nov (draft), 15 Nov (merged) |
| `[Mod2]` First draft of Security Rules and emulator tests | Mod2, Test | Gathan | 8 Nov |
| `[Mod2]` Reference prices by hand and 10 to 15 fictional seed listings | Mod2, Research | Gathan | 13 Nov |
| `[Mod6]` Collect 10 sample tenancy agreements and their risky clauses | Mod6, Research | Ilham | 8 Nov |
| `[Mod6]` Compare Gemini and Claude on the contracts; recommend one (#10) | Mod6, Research | Ilham | 8 Nov |
| `[Research]` Survey in Google Form (English and Malay), pilot with 5, launch | Research | Aly | 31 Oct (launch) |
| `[Research]` Reach 100+ survey responses across faculties and areas | Research, Gate | Aly | 8 Nov |
| `[Research]` Plan 5 to 8 interviews and a consent form | Research | Aly | 8 Nov |
| `[Test]` Start the test plan: one test case per FR, plus NFR1 and NFR2 | Test | Aly | 15 Nov |
| `[Report]` Everyone starts their file in `docs/ai-log/` | Report | everyone | 30 Oct |

## 5. Number cards (from ASSUMPTIONS.md)

| Card title | Owner | Due |
|---|---|---|
| `[Research]` #1 Room price weights (master, medium, small) from survey C5 to C7 | Aly, then Gathan to config | 10 Nov |
| `[Research]` #7 Utilities estimate per person from survey C9 | Aly, Gathan | 10 Nov |
| `[Research]` #2 Peak hour windows | Aly (survey), Aaron | 12 Nov |
| `[Research]` #3 E-hailing fare formula and peak ratio | Aaron, Aly | 13 Nov |
| `[Research]` #4 Car cost: fuel, fuel use, tolls, parking | Aaron, Dafi | 13 Nov |
| `[Research]` #5 Public transport fares and coverage (bus, LRT, UM shuttle) | Aaron, Dafi | 8 Nov (coverage), 13 Nov (data) |
| `[Research]` #6 CO₂ emission factors per mode | Aaron | 13 Nov |
| `[Research]` #8 Data retention periods vs PDPA 2010 | Gathan, Dafi | 13 Nov |
| `[Research]` #9 Match score weights from survey F2 | Aaron, Aly | 13 Nov |
| `[Research]` #10 AI provider, cost and quota | Ilham | 8 Nov |
| `[Research]` #11 Google Maps Platform cost and quota | Aaron | 8 Nov |

## 6. Gate cards

| Card | Date | Owner | Done when |
|---|---|---|---|
| `[Gate]` Gate 1 | 8 Nov | Dafi | survey has 100+ responses, repo runs, Firebase project exists |
| `[Gate]` Gate 2 | 15 Nov | Dafi | requirements frozen, app shell and schema merged, Auth works, every Cloud Function has an agreed stub |
| `[Gate]` Gate 3: P0 code freeze | 13 Dec | Aaron | every P0 feature works on a real device with real data |
| `[Gate]` Gate 4: testing done | 27 Dec | Aly | NFR1 and NFR2 evidence collected, no P0 bug left, SUS score at least 68 |

## 7. Deliverable cards

**Video** (due 12:00, 3 Jan 2027, 10 to 15 minutes; recording 28 Dec to 2 Jan)

| Card | Presenter | Length |
|---|---|---|
| `[Video]` 1 Project overview (SDG 11) | Ary | 1:30 |
| `[Video]` 2 System demo, one part each and name the FRs | all six | 6:30 |
| `[Video]` 3 Design and usability | Ary | 1:00 |
| `[Video]` 4 Technical implementation and AI data flow | Aaron (AI flow: Ilham) | 1:30 |
| `[Video]` 5 NFR verification (k6 results, rules tests) | Aly (NFR1), Gathan (NFR2) | 1:30 |
| `[Video]` 6 Stakeholder validation (survey, interviews, user testing) | Aly | 1:00 |
| `[Video]` 7 Team contributions (Trello hours, commit summary) | Dafi | 0:30 |
| `[Video]` 8 Closing summary | Dafi | 0:45 |
| `[Video]` Record, edit and final cut; Ary checks against the rubric | Aly, Ary | 28 Dec to 2 Jan |

**Report** (due 12:00, 10 Jan 2027, at most 15 pages and 2,500 words; drafting starts during the build)

| Card | Owner | Words |
|---|---|---|
| `[Report]` 1 Introduction and problem context | Ary | 300 |
| `[Report]` 2a User study findings | Aly | 350 |
| `[Report]` 2b Functional requirements (19 FRs in tables) | Dafi, each module owner writes the justification | tables + 250 |
| `[Report]` 2c Non-functional requirements | Gathan, Aly (NFR1) | 150 |
| `[Report]` 3 Architecture and design | Aaron, with Ilham, Ary, Gathan | 350 |
| `[Report]` 4 System implementation and AI usage statement | Aaron, Ilham (AI statement) | 400 |
| `[Report]` 5 Testing and validation | Aly, with Ilham, Gathan, Dafi | 300 |
| `[Report]` 6 Impact, limitations, future work | Ilham, Ary | 150 |
| `[Report]` 7 Team roles and contributions (attach Git and Trello logs) | Dafi, everyone writes their entry | table |
| `[Report]` 8 References and final style pass | Dafi | list |
| `[Report]` Appendix: AI Usage Log (Appendix 2(b) template) | Ilham, everyone logs their own | not counted |

## 8. Weekly routine (Dafi)

- Daily: check the board for Blocked and WAIT cards.
- Twice a week: a short sync.
- Weekend: demo, retro, update risks, post the hours summary and a screenshot of the board.
