# Jiran: Product Requirements Document (PRD)

| | |
|---|---|
| **Product** | Jiran (Malay: "neighbour") |
| **Platform** | Native Android (Java, Android Studio), Firebase backend |
| **Context** | WIA2007 Mobile Application Development, Sem 1 2026/2027, Universiti Malaya (UM) |
| **SDG** | 11 Sustainable Cities and Communities: main target 11.1 (adequate, safe and affordable housing), supporting target 11.2 (affordable, sustainable transport, especially public transport) |
| **Status** | Draft v0.2, converted from the group proposal (24 Oct 2026), revised through team discussion |
| **Source** | `Jiran — WIA2007 Group Project Proposal.pdf`, `MAD Group Project.docx` |

> Terms such as Unit, Room, Listing, Host, Match, Connection, Group and Household have fixed meanings: see [GLOSSARY.md](GLOSSARY.md).

> Items marked **[DRAFT]** (priorities, acceptance criteria, success-metric baselines) were written during the proposal-to-PRD conversion and are not in the original proposal. The team should confirm them.

---

## 1. Summary

Jiran helps UM students who live off campus find a room, find compatible housemates, and run their shared household. The core idea: **where a student lives decides how they travel to UM**, so the app puts the commute (travel time and cost, plus a small CO₂ estimate) next to every housing choice.

### Problem
- UM has about 12,067 campus places; over 10,000 senior students are pushed off campus (UMSU, 2022 reports; to be re-validated by our 2026 survey).
- Students find rooms through unverified Telegram channels: no filters, no verification, no way to judge a housemate.
- Cheaper rooms are farther away, so students choose between long public-transport trips and driving alone, adding petrol, toll, parking cost and the most CO₂ per trip.
- Students renting alone pay the most per person; some are asked for three months' rent in advance and sign tenancy agreements without reading them.

### Vision
A verified, UM-only app where a student can choose where to live knowing the **full monthly cost and the carbon cost of getting to class**, and where lower-carbon options (train + UM shuttle, shared units) are the cheaper and easier ones.

### Why this is an SDG 11 product
Target wording below is paraphrased; use the official UN wording in the report.

| Target | How Jiran serves it |
|---|---|
| **11.1** Access to adequate, safe and affordable housing | Verified UM-only listings; true monthly cost per room with per-room prices capped at the unit's rent; contract checker that flags risky clauses; roommate matching and shared-unit renting to lower cost per person; household tools so shared living works. |
| **11.2** Access to safe, affordable and sustainable transport, especially public transport | The commute options card compares walking, bus/LRT plus UM shuttle, e-hailing and driving, with step-by-step public transport routes and peak vs off-peak times; the travel-time filter steers students toward areas with good public transport; the optional UM planning dashboard gives anonymised data for shuttle planning. |
| Additional impact (not the target) | CO₂ estimates per travel mode raise awareness of the climate cost of commuting (SDG 13.3); the optional shared commute groups (Module 7, P2) would also record CO₂ saved per shared trip. |


---

## 2. Goals and non-goals

### Goals
1. Show **true monthly cost** (rent + utilities + commute) and **travel time** on every Room card, and a dedicated **commute options card** with per-mode time, cost and CO₂ estimate on the listing detail page.
2. Let verified UM students find rooms and housemates with explainable matching.
3. Help housemates run a shared household: split rent and bills fairly, rotate chores and agree house rules.
4. Give a safe, privacy-first experience for people meeting strangers.
5. Use AI only where rules-based approaches fail (free-text search, contract reading, match explanations).

### Non-goals (v1)
- No payments or deposits processed in-app (cost split is calculated and displayed only).
- Shared commute groups (shared car or train + shuttle trips) are an optional P2 module (Module 7), not part of the core scope.
- No iOS app in v1: the app is Android only.
- No landlord-facing web app (future work).
- No live train arrival times (future work).
- No legal advice: the contract checker is advisory only.
- The AI never acts on its own: it proposes filters and flags; the user decides.

---

## 3. Users and stakeholders

### Personas
| Persona | Needs | Key modules |
|---|---|---|
| **Mei Ling**: Year 2 FSKTM student who missed a college place | Room under budget, short low-carbon commute, quiet housemates, safe contract | 1, 2, 3, 4, 6 |
| **Student searching for housing**: new intake or missed college place | Find a verified room quickly | 2, 4, 6 |
| **Housemates (current residents)** | Split rent and bills fairly, share chores, agree house rules | 5 |

### Stakeholders
| Stakeholder | Interest | Engagement |
|---|---|---|
| UMSU | Campaigned on housing shortage | Interview, concept feedback |
| BHEP / off-campus student unit | Student welfare and housing | Optional (extra credit): one short email to validate the problem |
| UM transport / shuttle management | Plans shuttle routes and times | One short email asking for the shuttle stops and timetable |
| UMSDC | Campus emissions, SDG reporting | Optional (extra credit): one short email on the CO₂ approach |
| Landlords / agents | Reliable tenants | Indirect: listing requirements, contract checker scope |
| Telegram channel admins | Run the informal market | Interview; possible launch channel |

---

## 4. Scope: modules and functional requirements

Priority: **P0** = required for the graded demo, **P1** = should have, **P2** = if time allows. **[DRAFT]**

**The P0 slice (what must work on a real device before 13 December):** sign-up with a UM email and profile (Module 1); post a Listing with priced Rooms, search with filters and Room cards showing true monthly cost (Module 2); the commute options card with walking, public transport, e-hailing estimate and driving, at the user's class start time (Module 4); the Match questionnaire, Matches with score and reasons, and Connection requests (Module 3); expenses and balances in the Unit tab (Module 5, FR5.1); plain-language search, contract check and AI labelling (Module 6). Everything marked P1 is built only after the P0 slice works end to end; anything not stable by 20 December is dropped.

| P1 (after the P0 slice works) | P2 (only if time allows) |
|---|---|
| Peak and off-peak columns, e-hailing surge, step-by-step public transport route (Module 4) | Shared commute groups (Module 7) |
| Area page and building reviews (FR2.3) | Impact dashboard, UM planning dashboard |
| Evaluate unit and Group room price split (FR2.4, FR3.3) | "Paste listing" AI shortcut for Evaluate unit |
| Group decision aids: split options, member status, pre-signing checklist, compare Units (FR3.3) | AI trade-off summary and commute-adjusted split for a Group |
| Chores and house rules (FR5.2, FR5.3) | |

Who owns and builds each module is in [TEAM_PLAN.md](../team/TEAM_PLAN.md).

### Module 1: Authentication and verified profile
| ID | Requirement | Priority |
|---|---|---|
| FR1.1 | The user shall be able to register only with a UM student email address, confirmed through an email verification link. | P0 |
| FR1.2 | The user shall be able to log in, log out and reset their password. | P0 |
| FR1.3 | The user shall be able to create a profile with faculty, class timetable, monthly budget, lifestyle tags and whether they own a car, and choose which details stay hidden until a Connection. | P0 |

**Acceptance criteria [DRAFT]**
- Registration with a non-UM domain is rejected with a clear message; an unverified account cannot reach any screen except "verify your email".
- Timetable entries store day, start, end and building; profile shows initials, not a photo, by default.
- Each hidden-field toggle takes effect immediately and is enforced by Security Rules, not just the UI.

### Module 2: Housing listings and area guide
| ID | Requirement | Priority |
|---|---|---|
| FR2.1 | The user shall be able to post a unit listing with photos, property or building name, total rent, deposit, area, gender preference and amenities, and add each vacant room (master, medium or small) with its own price. | P0 |
| FR2.2 | The user shall be able to search vacant rooms and filter them by budget, room type, area, gender preference, amenities and maximum travel time to their own faculty. | P0 |
| FR2.3 | The user shall be able to view an area page with student-reported rent ranges and moderated building reviews (water, internet, security, noise). | P1 |
| FR2.4 | A group of friends who already live together or plan to shall be able to evaluate any unit, including one found on another site, by filling a short form (property or building name, total rent, number of rooms by type, and optionally deposit), and the system shall set a price for each room type (master, medium, small), show each member's true monthly cost, and show each member's commute options card to their own faculty. | P1 |

**Acceptance criteria [DRAFT]**
- A listing cannot be published without property or building name, total rent, deposit, area, at least one photo and at least one vacant room with a price.
- The property or building name is picked from a place search (Google Places autocomplete), which also fills in the address and coordinates used by the commute calculation, so hosts and groups do not type an address by hand. A name that is not found can be entered as free text with the area chosen manually.
- Listings and evaluations in the same building share the building name, which links them to that building's moderated reviews and to the area page (FR2.3).
- The host sets each room's price. The sum of all room prices in the unit (vacant and occupied) must not exceed the unit's total rent, so nobody profits from subletting; the app rejects a higher total and shows how much is left to allocate.
- **Default room price split:** the app divides the unit's total rent by room-type weights (master 1.3, medium 1.0, small 0.7) and rounds to the nearest RM1, giving any rounding remainder to the master room. Example: a RM1,500 unit with one room of each type suggests master RM650, medium RM500, small RM350. The weights are [DRAFT] assumptions to be checked against survey answers on what students actually pay, and are kept in config so they can change without a release.
- The host (Flow A) or group (Flow B/C) can edit any suggested price, but the total can never exceed the unit's total rent (Flow A) or must equal it exactly (Flow B/C).
- Utilities are shared equally among all residents, not priced per room.
- Search results show one **Room card** per vacant room, so the true monthly cost is calculated for that room's price.
- **Host rules:** a Host is a verified UM student who holds or is about to hold the tenancy; there is at most one active Host per unit, and landlords have no account. If the Host leaves, they hand the Host role to another member of the household. If nobody accepts it, the Listing closes automatically and the household stays.
- Filters combine with AND logic; the travel-time filter uses the commute calculation from Module 4.
- Reviews are visible only after moderation; users can report a listing or review.
- A Building can be reviewed only by people who are, or were, members of a Household in a Unit of that Building, so reviews come from real residents.
- A Listing closes by itself 30 days after it was posted or last renewed, unless the Host renews it; it also closes when its last vacant Room is marked occupied.
- The Evaluate unit form needs only four things: property or building name (place search), total rent, number of rooms by type, and optionally deposit. It works without AI, so it cannot fail during a demo.
- **Optional AI shortcut (P2):** a "Paste listing" button lets the user paste the listing text or upload a screenshot, and AI pre-fills the same fields. The user must confirm or edit them before any calculation runs; fields the AI could not read stay empty and highlighted. The user supplies text or an image, never a link for the server to fetch, so Jiran does not scrape other sites.
- An Evaluation is private to the Group or student who made it and never becomes a Listing. It can be attached to a Group as its Target Unit; when the tenancy starts, its building, Unit rent and rooms with prices become the Household's Unit record, so nothing is typed again. An Evaluation that is not attached to a Group is deleted after 90 days.
- In Evaluate unit (FR2.4), each member picks the room they would take; the form blocks confirmation while two members pick the same room or the prices do not add up to the total rent.

**Listing flows**
- **Flow A, vacant room in an existing unit (P0):** the host already rents the unit and opens one or more vacant rooms. The host sets the room prices. Another student finds the room, sends a Connection request (FR3.3), and once the Host accepts it is a Connection: chat opens and they arrange a viewing.
- **Flow B, group renting a whole unit together (P1):** nobody holds the tenancy yet. A group of two to four forms through roommate matching and splits the unit's room prices among themselves (see FR3.3).
- **Flow C, friends who already have a group and need a unit (P1, FR2.4):** the group browses units posted in Jiran. If none fits, an optional **"Search on PropertyGuru"** button opens PropertyGuru's public search in the browser (area and budget prefilled where the site's URL allows); Jiran does not scrape or import PropertyGuru data. When the group finds a unit anywhere, they fill the short **Evaluate unit** form (property or building name, total rent, rooms by type, optional deposit; the name is picked from place search so no address is typed): the system applies the default room price split above, lets members edit it, and shows each member's monthly cost for the room they would take plus their own commute options card to their own faculty (peak and off-peak).

### Module 3: Roommate matching
| ID | Requirement | Priority |
|---|---|---|
| FR3.1 | The user shall be able to complete a compatibility questionnaire (sleep time, cleanliness, noise, cooking, smoking, pets, budget). | P0 |
| FR3.2 | The system shall show a ranked list of matches with a compatibility score and the reasons behind it, including overlapping class days. | P0 |
| FR3.3 | The user shall be able to send a Connection request to a Match or a room's Host (chat and contact details open only once both sides accept, which makes it a Connection) **(P0)**, and form a group of two to four who agree a room price split to rent a whole unit together, with decision aids that help the group talk it through (split options, member status, a pre-signing checklist and a side-by-side comparison of Units) **(P1)**. | P0 / P1 |

**Acceptance criteria [DRAFT]**
- The score comes from a transparent weighted formula (not the AI), is deterministic for identical inputs, and is unit-tested.
- The AI only writes the reasons; if the AI call fails, the app shows rule-generated reasons instead.
- Contact details are not readable by either side until the request becomes a Connection, i.e. both have accepted (NFR2). A Match on its own never unlocks anything.
- Groups hold two to four members.
- **Decision aids for a Group (P1).** The app never decides for the group and never shows one "best unit" score; it puts the same facts in front of everyone:
  - **Split options:** with one tap the group switches between equal split, split by room type (the default weights) and custom. Each member's amount updates immediately, and every option still adds up exactly to the Unit rent.
  - **Member status:** each member marks themselves In, Not sure or Needs changes, with one short note, so nobody silently disagrees. The Group can only be confirmed when everyone is In.
  - **Pre-signing checklist:** the contract has been checked, deposit and advance rent have been discussed, and the signer is chosen. It is a reminder, not a gate.
  - **Compare Units:** two or three Evaluations side by side, showing for each Unit the rent, deposit, every member's true monthly cost and every member's travel time to their faculty. This is built last of the four.
  - P2: an AI summary of the trade-offs in two or three sentences, written only from numbers already on screen (for example "Unit B costs Rina RM 90 less but is 20 min farther for Aisyah"), and a split that shifts rent toward people with shorter commutes.
- If a member leaves a Group before the tenancy starts, every acceptance of the price split is reset and the remaining members must agree a new split that again adds up exactly to the Unit rent. They may invite a replacement through a Connection request. A Group with fewer than two members is dissolved automatically.
- Group room prices must add up exactly to the unit's total rent, and every member must accept the split before the group is confirmed; changing any price resets the acceptances.

### Module 4: Commute planner and true monthly cost
| ID | Requirement | Priority |
|---|---|---|
| FR4.1 | The system shall calculate travel time and monthly cost from a listing to the user's faculty for walking, bus or LRT plus UM shuttle, e-hailing (e.g. Grab) and driving alone, based on the user's class times **(P0)**, and for both peak and off-peak departure times **(P1)**. | P0 / P1 |
| FR4.2 | The system shall show each Room card's true monthly cost and the travel time to the user's faculty. True monthly cost = the room price + the person's equal share of utilities + the commute cost of the cheapest travel mode that still reaches class on time at the user's real departure time; the detail page says which mode was used. | P0 |
| FR4.3 | When the user opens a listing's details, the system shall show a commute options card comparing each travel mode by travel time, monthly cost and estimated CO₂, and highlight the cheapest option that still gets the user to class on time **(P0)**; with a **Peak / Off-peak** toggle and the step-by-step public transport route for the bus or LRT plus UM shuttle option **(P1)**. | P0 / P1 |

**Acceptance criteria [DRAFT]**
- CO₂ per trip = distance (maps service) × a published emission factor per mode; the source of each factor is named in the app.
- Figures are labelled as estimates.
- The highlighted option must arrive before the user's class start time; if none do, the UI says so rather than highlighting a late option.
- If no travel mode reaches class on time, the Room card says so instead of showing a cost figure.
- Room cards in search results show only true monthly cost and travel time; CO₂ appears **only** in the commute options card on the detail page.
- E-hailing cost is estimated from a published fare formula (base fare + per-km rate), not live Grab prices; the card labels it as an estimate and names the assumption.
- **P0 behaviour:** the card shows a single column, with each mode's time worked out for the user's class start time (arrive by); there is no toggle and no route steps yet. UM shuttle times appear only if seed data exists.
- **Peak vs off-peak (P1):** the card shows both columns for every mode. Road modes (driving alone, e-hailing, bus) use traffic-aware travel time from the Routes API for the given departure time; transit modes use scheduled times. The peak windows (e.g. weekday morning and evening) are a config value the team still has to define.
- **(P1)** E-hailing at peak adds an assumed surge multiplier on top of the fare formula; the multiplier is an assumption, shown as a range or estimate, never as a live price.
- **(P1)** The "on time" check and the highlighted option use the user's actual departure time from their timetable (peak or off-peak), not an average.
- Because each listing now needs two departure-time lookups per mode, results for the same area and faculty pair are cached in Firestore to protect NFR1 and the Maps quota.
- **Public transport route (P1):** tapping the bus or LRT plus UM shuttle option opens the full route on its own page: walk to the first stop or station, each bus or LRT line with its boarding and alighting stops and any transfer, the UM shuttle leg, the final walk, and total time and fare. It shows the next departures that arrive before the user's class start, using the user's real class time.
- **(P1)** Public transport steps come from the Routes API transit mode. UM shuttle routes and timetables may not be in Google's transit data, so the team adds them as seed data (`areas.nearestStations[]` plus a shuttle stops and timetable table) and the card labels shuttle times as scheduled, not live.
- **(P1)** The route detail loads only when the user opens it, so listing search stays within NFR1.

### Module 5: Shared house tools
| ID | Requirement | Priority |
|---|---|---|
| FR5.1 | Users who live together shall be able to create a household (created when a Group's tenancy starts, or joined by invite code), add shared expenses (rent, utilities, groceries) with who paid and who shares, and see who owes whom. | P0 |
| FR5.2 | The household shall be able to define recurring chores, and the system shall rotate the assignments, remind the person whose turn it is, and let them mark the chore done. | P1 |
| FR5.3 | The household shall be able to keep a list of house rules (e.g. quiet hours, guests, cleaning) that each member must accept, and shall see which members have accepted. | P1 |

**Acceptance criteria [DRAFT]**
- Rent is split by each room's price from the unit (Module 2); utilities and other shared expenses are split equally unless the user picks a custom split.
- Balances across all members always sum to zero (unit-tested edge cases: 2 members, 4 members, rounding).
- The app only calculates and tracks who owes whom; members mark a debt as settled. It does not process payments.
- Household data (expenses, chores, rules) is readable and writable only by that household's members.
- A person becomes a household member **only after the Host marks their room occupied and they confirm their move-in**. Before that they only have a Connection with the Host, so shared expenses never include someone who has not moved in.
- A Group that agreed a room price split becomes the household when the tenancy starts, with the same members and the same room prices; nothing is re-entered.
- When a Household forms from a Group, the member who signs the tenancy becomes the Host. If several sign together, the Group picks one Host in the app before the tenancy starts.
- A Room may be occupied by someone with no Jiran account. In the household they appear only as "Occupant of the Master room": no name, phone or other personal data, no notifications, no access. The Host can include their share in an expense and mark it paid by hand.
- When a member moves out, their Room becomes vacant again at its previous price. The Host may change that price as long as the Room prices still do not add up to more than the Unit rent; the other members' prices only change if each of them accepts. The member's unsettled balance stays recorded as owed until it is marked settled, and they keep read-only access to their own balance history.
- When the last member leaves or the tenancy ends, the household is closed and kept read-only for 6 months so balances can be settled, then deleted.
- Chore rotation skips no one and repeats fairly; reminders arrive as push notifications.
- Changing a house rule resets the acceptances.

### Module 6: AI assistant and contract checker
| ID | Requirement | Priority |
|---|---|---|
| FR6.1 | The user shall be able to type a housing request in plain English or Malay, and the system shall turn it into search filters the user can see and edit before results load. | P0 |
| FR6.2 | The user shall be able to upload a tenancy agreement (PDF or photo) and receive a plain-language summary with clauses marked Red flag, Check or OK. | P0 |
| FR6.3 | The system shall label every AI output as AI-generated, state that the contract check is not legal advice, and let the user report a wrong result. | P0 |

**Acceptance criteria [DRAFT]**
- Filters are always displayed for editing before the search runs.
- Status is shown with words (Red flag, Check, OK) as well as colour.
- Uploaded contracts are visible only to their uploader and auto-deleted after 30 days; the user can delete the file and its result earlier.
- The contract check is never shown on a Listing, Room card or room details page. A tenancy agreement is confidential, so it reaches the student privately: the Host shares it in the Connection chat (a chat attachment visible only to the two people), and the student can run the check from there or upload their own copy from Profile.

### Optional modules (P2)
- **Module 7: Shared commute groups (P2).** Students from the same area with overlapping timetables form a recurring commute group (shared car or train + UM shuttle). There are no fixed driver or passenger roles: every member is equal and pays an equal share.
  - FR7.1: the user shall be able to create a recurring commute group with start area, departure time, days, maximum members and travel mode.
  - FR7.2: the system shall suggest commute groups by home area and timetable overlap, and let a user request to join a group that its existing members accept or decline.
  - FR7.3: the system shall split the trip's costs equally among all members, never above the trip's cost, record the CO₂ saved per trip compared with each member travelling alone, and let members rate each other.
  - Acceptance notes: a group cannot exceed its maximum members; cost split ≤ trip cost; share-my-trip link for every member; push notifications for join requests. Cold start is the main risk, since it needs enough students from the same area.
- **Impact dashboard:** each user's and UM's total CO₂ and money saved (depends on Module 7).
- **Group decision aids, extra:** the AI trade-off summary and the commute-adjusted split described under Module 3.
- **UM planning dashboard:** anonymised counts of where off-campus students live and when they travel, shown only as totals, for shuttle planning (supports target 11.2).

---

## 5. Non-functional requirements

| ID | Requirement | Why it matters at UM | Verification |
|---|---|---|---|
| **NFR1 Performance** | Listing search results, including commute estimates, load within 3 s for 95% of requests at 100 concurrent users. | Searches spike before each semester; a slow app sends users back to Telegram. | Load test with 100 simulated users (k6 or JMeter) against the search endpoint; report p95. |
| **NFR2 Privacy and security** | Only verified UM accounts access the app. A user's exact address, phone number and contract uploads are visible only to that user; contact details only once a Connection exists (both sides have accepted). | Students share where they live, their timetable and contracts with strangers; a leak is a real safety risk. | Firebase Security Rules unit tests in the emulator: every unauthorised read/write must be denied (target 100%). |

**What another person can see, by stage**

| Stage | Visible | Not visible |
|---|---|---|
| Match | initials, faculty, year, lifestyle tags, match score and reasons, days with overlapping classes | name, photo, phone, address, timetable details |
| Connection request | same as Match, plus the short message the sender wrote | name, photo, phone, address |
| Connection | chat, first name, the Unit's exact address (for a viewing, only to someone connected to the Host); phone number only if its owner ticked "share my number" | photo, full timetable |
| Household | members' timetables, shared expenses, chores and rules, profile photos | anything a member hid in their privacy settings (FR1.3) |

Users can hide any field at every stage through their privacy settings.

Additional constraints taken from the proposal:
- **Accessibility:** WCAG 2.1 AA contrast (4.5:1), touch targets ≥ 48 dp (Android guideline), status shown by words and colour, English and Malay at launch.
- **Data protection:** follow Malaysia's Personal Data Protection Act 2010; collect only what each feature needs; consent at sign-up.

---

## 6. AI requirements

| Feature | Behaviour | Why AI | Evaluation | Target |
|---|---|---|---|---|
| Natural-language search (FR6.1) | Free text (EN/MY) → structured filters | Fixed forms miss how students actually describe needs | 50 survey-written test requests; share converted to correct filters | ≥ 90% |
| Contract checker (FR6.2) | Summarise tenancy agreement; flag risky clauses | Contracts are long and legal; students sign unread | 10 sample agreements with known problem clauses; measure flags found and false alarms | Report both rates |
| Match explanations (FR3.2) | Write reasons for each roommate match | Makes the score understandable and trusted | User testing: do students agree reasons are accurate (5-point scale) | Report mean |
| Listing reader (FR2.4 shortcut, **P2**) | Read building name, total rent, deposit and rooms from pasted listing text or a screenshot into the same editable fields | Saves typing when the user already has a long listing text; the manual form remains the main path | 20 sample listings (text and screenshots, written by the team from real postings); share of fields extracted correctly | ≥ 90% [DRAFT] |

**Design rules**
- The compatibility **score** and **CO₂** are computed by code, never by the AI.
- All AI and Maps calls run in Cloud Functions; API keys never ship in the app.
- AI output is advice; the user always confirms.
- Failure of the AI service must degrade gracefully (manual filters, rule-based reasons, "try again" for the contract check).
- Provider: Gemini API or Claude API (decision pending, see Open questions).

---

## 7. User experience

### Navigation
A floating bottom bar with four tabs, **Home, Rooms, Match, Unit**, plus a separate blue **Post** button for listing a room. "Home" and "Unit" use different icons (house vs. door) so they are not confused.

There is no Ask tab. The AI features sit where they are used: plain-language search (FR6.1) is the Rooms search bar, and the contract check (FR6.2) is **private**: it opens only from the agreement the Host shares in a Connection chat, or from the Contract check row in Profile. It never appears on a listing, a Room card or the room details page.

| Screen | Purpose | Modules |
|---|---|---|
| Home | When to leave for the next class, new rooms near you, new matches, getting started | 1, 2, 3, 4 |
| Rooms | Room grid with filters; the search bar takes plain-language requests (AI) | 2, 6 |
| Post | List a room: photos, building, room type, price | 2 |
| Evaluate unit | Short form for any unit's name, rent and rooms (optional "Paste listing" AI shortcut, P2), see the default room price split (master, medium, small), each member's monthly cost and commute card | 2, 3, 4 |
| Room details | Listing, true monthly cost, and a **commute options card** (walk, bus/LRT + shuttle, e-hailing, drive alone) with time, monthly cost and CO₂ per mode, and a step-by-step public transport route that opens on its own page | 2, 4, 6 |
| Contract check | Upload a tenancy agreement and see clauses marked Red flag, Check or OK. Private: opened from a Connection chat or from Profile, never from a listing | 6 |
| Match | Match score, reasons, things to discuss, group rental | 3 |
| Unit | Shared expenses and who owes whom, chore rotation, house rules | 5 |

### Design principles
1. **The commute is never hidden, but CO₂ stays out of the way:** travel time and monthly cost appear on every Room card; per-mode CO₂ lives in the commute options card on the detail page.
2. **One main action per screen**, in brand blue (Continue, Connect, Post).
3. **Accessible by default** for UM's diverse students.
4. **Privacy by default:** initials instead of photos; address and phone hidden until a Connection exists.

### Colour scheme and type
Full tokens, components and rules are in [DESIGN.md](../design/DESIGN.md) (style reference: Nextdoor; font: Geist, semibold at most).

| Colour | Hex | Use |
|---|---|---|
| Brand blue | `#065BC3` | Main action, links, Post button |
| Ink | `#0B0B0C` | Text, strong secondary buttons |
| Secondary text | `#5B5F66` | Meta lines, hints |
| Ground | `#F3F4F6` | Grouped sections |
| Surface | `#FFFFFF` | Screens, cards |

### Key user journey (Mei Ling)
1. Signs up with her UM email.
2. Enters budget, timetable and habits, then searches "room under RM550, max 30 minutes to FSKTM by train, quiet housemates".
3. Each Room card shows true monthly cost and travel time. Opening one shows a commute options card comparing train + shuttle, e-hailing and driving alone by time, cost and CO₂.
4. Uploads the tenancy agreement; AI flags risky clauses such as three months' rent in advance.
5. Finds a compatible housemate whose unit has a vacant small room at the price the host set; she sends a Connection request, the Host accepts, and chat opens so she can arrange a viewing.
6. After moving in, the household sets up the rent split by room price, adds the monthly bills, rotates chores, and agrees the house rules in the Unit tab.

---

## 8. Data model

Cloud Firestore collections (the proposal lists nine; this PRD adds `rooms` and `unitEvaluations`; see Open questions):

| Collection | Key fields | Used by (module) |
|---|---|---|
| `users` | uid, umEmail, name, faculty, year, homeAreaId, budgetMin, budgetMax, lifestyleTags[], hasCar, timetable[{day, start, end, building}], privacy{} | M1, all |
| `areas` | areaId, name, lat, lng, rentLow, rentHigh, nearestStations[] | M2, M4 |
| `listings` | listingId, ownerUid, areaId, buildingName, placeId, totalRent, deposit, genderPref, amenities[], photoPaths[], status, createdAt | M2, M4 |
| `rooms` | roomId, listingId, type (master/medium/small), price, status (vacant/occupied) | M2, M4 |
| `reviews` | reviewId, listingId or buildingName, authorUid, water, internet, security, noise (1–5), text, moderated | M2 |
| `matchProfiles` | uid, answers{sleep, clean, noise, cook, smoke, pets}, updatedAt | M3 |
| `matches` | matchId, uidA, uidB, score, reasons[], statusA, statusB, createdAt | M3 |
| `groups` | groupId, memberUids[], targetListingId, spots, roomSplit[{memberUid, roomType, price}], splitOption, memberStatus[{uid, status, note}], checklist{}, acceptedBy[], status | M3 |
| `unitEvaluations` | evalId, groupId, buildingName, areaId or lat/lng, referenceUrl (optional, a note typed by the user, never fetched), totalRent, deposit, rooms[{type, price, assignedUid}], createdAt | M2, M3 |
| `households` | householdId, memberUids[], listingId, rules[{text, acceptedBy[]}], createdAt | M5 |
| `expenses` | expenseId, householdId, description, amount, paidByUid, splitAmong[{uid, share}], date, settled | M5 |
| `chores` | choreId, householdId, task, frequency, rotation[uid], currentUid, dueDate, done | M5 |
| `commuteGroups` (P2, Module 7) | groupId, createdByUid, startAreaId, departTime, days[], maxMembers, mode, costPerTrip | M7 |
| `commuteMembers` (P2, Module 7) | memberId, groupId, memberUid, date, status, sharePaid, co2SavedKg, ratingsGiven{} | M7, impact |
| `contractChecks` | checkId, uid, filePath, summary, flags[{level, clause, text}], createdAt, deleteAfter | M6 |

Files (room photos, contract uploads) are stored in Firebase Storage and referenced by path.

**Access rules (NFR2):** a user reads and writes only their own `users` and `contractChecks` records, and only members of a household read or write that household's `households`, `expenses` and `chores`. Contact fields on `users` are readable by another user only when a `matches` or `households` record links them with both sides accepted. `contractChecks` are deleted after 30 days.

---

## 9. System architecture

```
Android app (Java)
   ├─ Firebase Authentication
   ├─ Cloud Firestore
   ├─ Firebase Storage
   ├─ Firebase Cloud Messaging  (notifications → app)
   └─ HTTPS callable functions ─► Cloud Functions (Node.js)
                                    ├─ match scoring, cost split, CO₂
                                    ├─► Google Maps Routes API
                                    └─► AI API (Gemini or Claude)
```

- The app reads and writes Firebase directly through the SDK and receives notifications from Cloud Messaging.
- Anything needing a secret key or heavy logic goes through Cloud Functions, which call the maps and AI services and write results back to Firestore.

| Layer | Tool |
|---|---|
| Mobile app | Native Android: Java, Android Studio, XML layouts with Material Components, Activities and Fragments, MVVM (ViewModel, LiveData), RecyclerView, Navigation component |
| Sign-in | Firebase Authentication |
| Database | Cloud Firestore |
| File storage | Firebase Storage |
| Server logic | Cloud Functions (Node.js) |
| Maps and routes | Google Maps Platform (Routes API, Places API, Maps SDK for Android) |
| AI | Gemini API (or Claude API) |
| Notifications | Firebase Cloud Messaging |
| Design | Figma |
| Code and tasks | GitHub, Trello |
| Testing | JUnit and Mockito, Espresso, Firebase Emulator Suite, k6 |

**Engineering conventions:** feature branches merged by PR with one reviewer; Google Java Style (Checkstyle) and Android Lint must pass; commit messages name the module and FR (e.g. `M5 FR5.3: cost split`).

---

## 10. Success metrics

Product metrics are targets for the demo and pilot, not production KPIs. **[DRAFT]**

| Area | Metric | Target |
|---|---|---|
| Performance | p95 search response at 100 concurrent users | < 3 s (NFR1) |
| Security | Unauthorised rule attempts denied | 100% (NFR2) |
| AI: search | Requests converted to correct filters (n = 50) | ≥ 90% |
| AI: contracts | Problem clauses flagged / false alarms (n = 10 agreements) | Report both |
| AI: matches | Students agreeing reasons are accurate | Report 5-point mean |
| Usability | SUS score (8–10 UM students, 5 tasks) | ≥ 68; ≥ 80% task success |
| Requirements validation | Survey responses from off-campus UM students | ≥ 100 |
| Interviews | Students, UMSU rep, Telegram admin | 5–8 people |
| Impact (P2, needs Module 7) | CO₂ saved per shared trip, per user, and for UM overall | Tracked per commute member record |

---

## 11. Testing plan

| Test type | Covers | Tool | Pass criterion |
|---|---|---|---|
| Unit | Expense split and balances, CO₂ calculation, match score formula | JUnit and Mockito (app), Jest (Cloud Functions) | All pass |
| Widget / integration | Sign-up, posting a listing, search, connect, create a household and add an expense | Espresso (UI tests) | Each FR's main flow completes |
| Security rules (NFR2) | Unauthorised reads and writes on every collection | Firebase Emulator Suite | 100% denied |
| Load (NFR1) | Search with commute estimates, 100 concurrent users | k6 or JMeter | 95% under 3 s |
| AI evaluation | Search filters, contract flags, match reasons | Test sets (Section 6) | Targets in Section 6 |
| Usability | 8–10 UM students, five tasks | SUS + task success | SUS ≥ 68; 80% success |

Every FR has at least one test case; both NFRs have a measured test.

---

## 12. Release plan

| Dates | Milestone |
|---|---|
| 26 Oct – 8 Nov 2026 | Survey and interviews |
| 2 – 15 Nov | Requirements freeze and design (Figma, schema) |
| 16 Nov – 13 Dec | Build core modules (1–5) |
| 7 – 20 Dec | AI features (Module 6) and integration |
| 14 – 27 Dec | Testing and user testing |
| 28 Dec – 2 Jan 2027 | Record and edit demo video; **video due 12pm, 3 Jan 2027** |
| 4 – 9 Jan | Final report; **report due 12pm, 10 Jan 2027** |

**Cold-start plan:** seed the app with fictional but realistic sample listings modelled on reference prices (never copied from other sites); launch through UMSU, PEKOM and faculty channels, where consenting Telegram posters can list their own rooms.

---

## 13. Risks and mitigations

| Risk | Mitigation |
|---|---|
| Few listings at launch (cold start) | Fictional sample listings from reference prices; launch via UMSU, PEKOM and faculty channels |
| Inaccurate listings or scams | UM-verified posters only, report button, moderated reviews |
| Safety when meeting housemates or commuting with strangers | Verified accounts, ratings, contact hidden until both accept (share-my-trip link if commute groups are built) |
| CO₂ figures are estimates | Label as estimates and name the emission factors |
| AI can be wrong | Editable filters, "not legal advice" label, report-a-wrong-result button |
| Expense splitting could be mistaken for a payment service (and, in P2, shared trips for an unlicensed ride service) | The app only calculates and tracks who owes whom, never moves money; shared-trip cost-sharing is capped at the trip's cost with no profit |
| Privacy breach | Security Rules tests, 30-day contract deletion, anonymised-only planning data, PDPA 2010 compliance |

---

## 14. Open questions

1. ~~SDG choice~~ **Decided:** SDG 11 (main target 11.1, supporting 11.2); the team has confirmed the brief allows any SDG, not only 6 or 13.
2. ~~Platform~~ **Decided:** native Android in Java, because the team learned Java in earlier semesters and the brief itself talks about "activities". Confirm with the lecturer together with the Appendix 1 request.
3. Which AI provider: Gemini or Claude? (Cost, quota, Malay language quality.)
4. Which published emission factor source for each travel mode?
5. Firestore collections: the proposal says ten but lists nine; this PRD adds `rooms` and `unitEvaluations`, so it now lists fifteen (two of them only for the P2 commute groups). A store for chat (FR3.3) is still missing (candidate: `chats`), plus possibly `reports` and `notifications`.
6. Evidence is from 2022 news; the 2026 survey must confirm the current situation before requirements freeze.
7. Does Google's transit data cover the bus/LRT routes and UM shuttle that students actually use around UM? Check a few real routes (e.g. Pantai Dalam, Kerinchi) early; if the UM shuttle is missing, the team seeds shuttle stops and timetables from UM transport.

8. ~~Cloud Functions language~~ **Decided:** keep Node.js for Cloud Functions, written small and simple by Aaron and Ilham (the rest of the app is Java).
---

## 15. Future work

An iOS version; web version for landlords; Malay and Chinese interface options (English and Malay at launch); live train arrival times; adoption by BHEP as UM's official off-campus housing channel.
