# Jiran: Product Requirements Document (PRD)

| | |
|---|---|
| **Product** | Jiran (Malay: "neighbour") |
| **Platform** | Native Android (Java, Android Studio), Firebase backend |
| **Context** | WIA2007 Mobile Application Development, Sem 1 2026/2027, Universiti Malaya (UM) |
| **SDG** | 11 Sustainable Cities and Communities: main target 11.1 (adequate, safe and affordable housing), supporting target 11.2 (affordable, sustainable transport, especially public transport) |
| **Status** | Draft v0.3, commute clarified by Ary on 11 Nov 2026; implementation migration pending |
| **Source** | `Jiran — WIA2007 Group Project Proposal.pdf`, `MAD Group Project.docx` |

> Terms such as Unit, Room, Listing, Host, Match, Connection, Group and Household have fixed meanings: see [GLOSSARY.md](GLOSSARY.md).

> Items marked **[DRAFT]** (priorities, acceptance criteria, success-metric baselines) were written during the proposal-to-PRD conversion and are not in the original proposal. The team should confirm them.

---

## 1. Summary

Jiran helps UM students, including first-years who do not yet know their timetable, evaluate off-campus rooms, find compatible housemates, and run their shared household. The core idea: **where a student lives decides how they travel to UM**, so the app puts the commute (travel time and cost, plus a small CO₂ estimate) next to every housing choice.

### Problem
- UM has about 12,067 campus places; over 10,000 senior students are pushed off campus (UMSU, 2022 reports; to be re-validated by our 2026 survey).
- Students find rooms through unverified Telegram channels: no filters, no verification, no way to judge a housemate.
- Cheaper rooms are farther away, so students choose between long public-transport trips and driving alone, adding petrol, toll, parking cost and the most CO₂ per trip.
- Students renting alone pay the most per person; some are asked for three months' rent in advance and sign tenancy agreements without reading them.

### Vision
A verified, UM-only app where a student can choose where to live knowing the **Room price, ways to reach their faculty, estimated travel time and fare per one-way trip**, and where lower-carbon options (train + UM shuttle, shared units) are the cheaper and easier ones.

### Why this is an SDG 11 product
Target wording below is paraphrased; use the official UN wording in the report.

| Target | How Jiran serves it |
|---|---|
| **11.1** Access to adequate, safe and affordable housing | Verified UM-only listings; Room prices and separately labelled per-trip commute estimates with per-room prices capped at the unit's rent; contract checker that flags risky clauses; roommate matching and shared-unit renting to lower cost per person; household tools so shared living works. |
| **11.2** Access to safe, affordable and sustainable transport, especially public transport | The commute options card compares walking, bus/LRT plus UM shuttle, e-hailing and driving, with step-by-step public transport routes, estimated for leaving now; the travel-time filter steers students toward areas with good public transport; the optional UM planning dashboard gives anonymised data for shuttle planning. |
| Additional impact (not the target) | CO₂ estimates per travel mode raise awareness of the climate cost of commuting (SDG 13.3); the optional shared commute groups (Module 7, P2) would also record CO₂ saved per shared trip. |


---

## 2. Goals and non-goals

### Goals
1. Help students, including first-years without a class schedule, compare a Room’s monthly rent with the separate time and estimated fare for one trip to their faculty. The detail page explains how to travel by each available mode; no combined monthly total is calculated.
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

**The P0 slice (what must work on a real device before 13 December):** sign-up with a UM email and profile (Module 1); post a Listing with priced Rooms, search with filters and Room cards showing rent and separate per-trip commute estimates (Module 2); the commute options card with walking, public transport instructions, e-hailing fare estimate and driving, estimated for leaving now (Module 4); the Match questionnaire, Matches with score and reasons, and Connection requests (Module 3); expenses and balances in the Unit tab (Module 5, FR5.1); plain-language search, contract check and AI labelling (Module 6). Everything marked P1 is built only after the P0 slice works end to end; anything not stable by 20 December is dropped.

| P1 (after the P0 slice works) | P2 (only if time allows) |
|---|---|
| A full transit map/timeline (Module 4); basic travel instructions are P0. Peak / Off-peak scenarios and e-hailing surge ranges were removed on 11 Nov 2026 | Shared commute groups (Module 7) |
| Evaluate unit and Group room price split (FR2.3, FR3.3) | Impact dashboard, UM planning dashboard |
| Group decision aids: split options, member status, pre-signing checklist, compare Units (FR3.3) | "Paste listing" AI shortcut for Evaluate unit |
| Chores and house rules (FR5.2, FR5.3) | AI trade-off summary and commute-adjusted split for a Group |

Who owns and builds each module is in [TEAM_PLAN.md](../team/TEAM_PLAN.md).

### Module 1: Authentication and verified profile
| ID | Requirement | Priority |
|---|---|---|
| FR1.1 | The user shall be able to register only with a UM student email address, confirmed through an email verification link. | P0 |
| FR1.2 | The user shall be able to log in, log out and reset their password. | P0 |
| FR1.3 | The user shall be able to create a profile with gender (male or female, required, shown on the profile), faculty, monthly Room budget, lifestyle tags and whether they own a car, and choose which details stay hidden until a Connection. | P0 |

**Acceptance criteria [DRAFT]**
- Registration with a non-UM domain is rejected with a clear message; an unverified account cannot reach any screen except "verify your email".
- No class timetable, class start time or campus attendance frequency is requested. Faculty is enough to estimate travel from a candidate Listing; setup must work before a first-year knows their timetable. The profile shows initials, not a photo, by default (Ary, 11 Nov 2026).
- Each hidden-field toggle takes effect immediately and is enforced by Security Rules, not just the UI.

### Module 2: Housing listings
| ID | Requirement | Priority |
|---|---|---|
| FR2.1 | The user shall be able to post a unit listing with photos, property or building name, total rent, deposit, area, gender preference and amenities, and add each vacant room (master, medium or small) with its own price. | P0 |
| FR2.2 | The user shall be able to search vacant rooms and filter them by budget, room type, area, gender preference, amenities and maximum travel time to their own faculty. | P0 |
| FR2.3 | A group of friends who already live together or plan to shall be able to evaluate any unit, including one found on another site, by filling a short form (property or building name, total rent, number of rooms by type, and optionally deposit), and the system shall set a price for each room type (master, medium, small), show each member's Room price and separate per-trip commute estimates, and show each member's commute options card to their own faculty. | P1 |

**Acceptance criteria [DRAFT]**
- A listing cannot be published without property or building name, total rent, deposit, area, at least one photo and at least one vacant room with a price.
- The Host chooses the area from the seeded area list and types the property or building name (no Google Places, which needs billing; openrouteservice geocoding only if its free quota fits; decided 31 Oct 2026, E2.1). The commute calculation uses the area centre as an estimated location until geocoding is available.
- Manual Listing fallback (Gathan approved 9 Nov 2026, accepted by Ary 10 Nov 2026): the Host enters the Building's street address (public, so students know where the apartment is) and, in the private location field, only the exact Unit (block, floor, door number). The selected Area centre is stored with `coordinateSource: area_center_estimate` and labelled as an estimate, never a verified Building location. Downstream commute screens must preserve that uncertainty; geocoding remains deferred pending assumption #11.
- Listings and evaluations in the same building share the building name.
- The host sets each room's price. The sum of all room prices in the unit (vacant and occupied) must not exceed the unit's total rent, so nobody profits from subletting; the app rejects a higher total and shows how much is left to allocate.
- **Default room price split:** the app divides the unit's total rent by room-type weights (master 1.3, medium 1.0, small 0.7) and rounds to the nearest RM1, giving any rounding remainder to the master room. Example: a RM1,500 unit with one room of each type suggests master RM650, medium RM500, small RM350. The weights are [DRAFT] assumptions to be checked against survey answers on what students actually pay, and are kept in config so they can change without a release.
- The host (Flow A) or group (Flow B/C) can edit any suggested price, but the total can never exceed the unit's total rent (Flow A) or must equal it exactly (Flow B/C).
- Utilities are not priced anywhere in Jiran (Ary, 12 Nov 2026): the real bill depends on use and many rooms are all-in, so no estimate is shown on a Listing, an Evaluation or a Room card. Students who live together add the real shared bills in the Household (FR5.1).
- Search results show one **Room card** per vacant room with five items (Ary, 11 Nov 2026): the monthly Room price, the apartment (Building) name, its street address, the one-way time and distance to the user's faculty (for example “12 min (500 m) to your faculty”), and when it was listed (“Listed 3d ago”). The room type is a badge on the photo. The Building's street address is public (Ary, 11 Nov 2026) so a student knows where the apartment is; only the exact Unit (block, floor, door number) stays hidden until a Connection. Fare, mode and CO₂ are on the details page; there is no combined total.
- **Host rules:** a Host is a verified UM student who holds or is about to hold the tenancy; there is at most one active Host per unit, and landlords have no account. If the Host leaves, they hand the Host role to another member of the household. If nobody accepts it, the Listing closes automatically and the household stays.
- Filters combine with AND logic; the travel-time filter uses the commute calculation from Module 4.
- Users can report a listing.
- A Listing closes by itself 30 days after it was posted or last renewed, unless the Host renews it; it also closes when its last vacant Room is marked occupied.
- The Evaluate unit form needs only four things: property or building name (place search), total rent, number of rooms by type, and optionally deposit. It works without AI, so it cannot fail during a demo.
- **Optional AI shortcut (P2):** a "Paste listing" button lets the user paste the listing text or upload a screenshot, and AI pre-fills the same fields. The user must confirm or edit them before any calculation runs; fields the AI could not read stay empty and highlighted. The user supplies text or an image, never a link for the server to fetch, so Jiran does not scrape other sites.
- An Evaluation is private to the Group or student who made it and never becomes a Listing. It can be attached to a Group as its Target Unit; when the tenancy starts, its building, Unit rent and rooms with prices become the Household's Unit record, so nothing is typed again. An Evaluation that is not attached to a Group is deleted after 90 days.
- In Evaluate unit (FR2.3), each member picks the room they would take; the form blocks confirmation while two members pick the same room or the prices do not add up to the total rent.

**Listing flows**
- **Flow A, vacant room in an existing unit (P0):** the host already rents the unit and opens one or more vacant rooms. The host sets the room prices. Another student finds the room, sends a Connection request (FR3.3), and once the Host accepts it is a Connection: chat opens and they arrange a viewing.
- **Flow B, group renting a whole unit together (P1):** nobody holds the tenancy yet. A group forms in the Housemates tab (created by one student and joined with a code or link, or made from Connections) and splits the unit's room prices among themselves (see FR3.3).
- **Flow C, friends who already have a group and need a unit (P1, FR2.3):** the group browses units posted in Jiran. If none fits, an optional **"Search on PropertyGuru"** button opens PropertyGuru's public search in the browser (area and budget prefilled where the site's URL allows); Jiran does not scrape or import PropertyGuru data. When the group finds a unit anywhere, they fill the short **Evaluate unit** form (property or building name, total rent, rooms by type, optional deposit; the name is picked from place search so no address is typed): the system applies the default room price split above, lets members edit it, and shows each member's Room price separately from their own commute options card to their own faculty (per-trip estimates for leaving now).

### Module 3: Roommate matching
| ID | Requirement | Priority |
|---|---|---|
| FR3.1 | The user shall be able to complete a compatibility questionnaire (sleep time, cleanliness, noise, cooking, smoking, pets, budget). | P0 |
| FR3.2 | The system shall show a ranked list of matches with a compatibility score and the reasons behind it. | P0 |
| FR3.3 | The user shall be able to send a Connection request to a Match or a room's Host (chat and contact details open only once both sides accept, which makes it a Connection) **(P0)**, and create or join a group (the creator sets the maximum size, 2 to 8; others join with a code or link) that agrees a room price split to rent a whole unit together, with decision aids that help the group talk it through (split options, member status, a pre-signing checklist and a side-by-side comparison of Units) **(P1)**. | P0 / P1 |

**Acceptance criteria [DRAFT]**
- The score comes from a transparent weighted formula (not the AI), is deterministic for identical inputs, and is unit-tested.
- The AI only writes the reasons; if the AI call fails, the app shows rule-generated reasons instead.
- Contact details are not readable by either side until the request becomes a Connection, i.e. both have accepted (NFR2). A Match on its own never unlocks anything.
- **Housemates tab and Groups (Ary, 12 Nov 2026).** The tab that was called Match is now **Housemates**: it holds everything about finding people to live with. The compatibility questions, the ranked Match list and Connection requests stay in it; a student with no Group also sees **Create a group** and **Join with a code** above the list. A student who is in a Group sees a "Your group" card (the members, a status such as "3 of 5 in · choosing a Unit", the spots left and an Open group button), and the list is titled "Find more housemates". A full Group replaces the list with "Your group is full". A Match who is already in another Group shows an "In a group" badge. In the chat + menu and the Unit tab, "Form a group" reads "Open group" for a student who already has one.
- **Group size and joining.** A Group has two to eight members; the creator sets the maximum when creating it (2 to 8, prefilled with 4) and can change it later, but never below the current number of members. The creator starts alone and invites others in two ways: a **6-character code** (valid 7 days; the creator can make a new one) or a **link** that carries the same code, and also from the student's Connections. A student who enters the code or opens the link sends a **join request**; the creator accepts or declines it in the Group screen, and a full Group refuses new requests. A student is in one Group at a time. Joining a Group does not open contact details: members see each other's Match-stage profile (initials, faculty, year, tags) and each other pair becomes a Connection only through the normal request (NFR2). A Group can be confirmed only with two or more members, all In. A Group of one is dissolved when its code expires unused.
- **Decision aids for a Group (P1).** The app never decides for the group and never shows one "best unit" score; it puts the same facts in front of everyone:
  - **Split options:** with one tap the group switches between equal split, split by room type (the default weights) and custom. Each member's amount updates immediately, and every option still adds up exactly to the Unit rent.
  - **Member status:** each member marks themselves In, Not sure or Needs changes, with one short note, so nobody silently disagrees. The Group can only be confirmed when everyone is In.
  - **Pre-signing checklist:** the contract has been checked, deposit and advance rent have been discussed, and the signer is chosen. It is a reminder, not a gate.
  - **Compare Units:** two or three Evaluations side by side, showing for each Unit the rent, deposit, every member's Room price and separate per-trip commute fare and every member's travel time to their faculty. This is built last of the four.
  - P2: an AI summary of the trade-offs in two or three sentences, written only from numbers already on screen (for example "Unit B costs Rina RM 90 less but is 20 min farther for Aisyah"), and a split that shifts rent toward people with shorter commutes.
- If a member leaves a Group before the tenancy starts, every acceptance of the price split is reset and the remaining members must agree a new split that again adds up exactly to the Unit rent. They may invite a replacement with the code or link. A Group left with fewer than two members after it had two or more is dissolved automatically.
- Group room prices must add up exactly to the unit's total rent, and every member must accept the split before the group is confirmed; changing any price resets the acceptances.

### Module 4: Apartment-to-faculty commute guide

The student asks: “If I rent this Room, how do I get from this Listing to my faculty, how long does it take, and roughly how much is one trip?” A class schedule and campus attendance frequency are not needed. Ary removed **true monthly cost** on 11 Nov 2026: rent is monthly, commute is per trip, and they are never added into an advertised total.

| ID | Requirement | Priority |
|---|---|---|
| FR4.1 | The system shall estimate one-way travel time and fare from a candidate Listing to the user's faculty for walking, public transport (bus/LRT and UM shuttle where supported), e-hailing such as Grab, and driving where applicable, without requiring a class timetable or campus-day count. | P0 |
| FR4.2 | Each Room card shall show its monthly Room price, Building name, area, the estimated one-way travel time and distance to the user's faculty, and the time since it was listed. Unknown commute data shall be labelled unavailable; the fare and mode are shown on Room details (FR4.3); no true monthly cost or monthly commute total is calculated. | P0 |
| FR4.3 | Room details shall show “Getting to [faculty]”, comparing available modes by one-way time, estimated fare and optional sourced CO₂ per trip, with the public transport row opening a route page that shows basic instructions **(P0)**; the full transit map/timeline is **P1**. There is no Peak / Off-peak choice (Ary, 11 Nov 2026): every estimate is for leaving now. | P0 / P1 |

**Acceptance criteria**
- There is no commute card on Home (Ary, 11 Nov 2026). Before a student has a home, commute is seen only per Room: time and distance on the Room card, details on Room details. Once the student's home is fixed (a member of a Household, so their Unit is known), the Unit tab shows the same "Getting to [faculty]" guide with the Unit as the origin. A student without a Household sees no commute in the Unit tab.
- Origin is the Listing being viewed, not the user's current location or future home. Destination is the selected faculty. No location permission, class time, attendance frequency or assumed trips per month is needed.
- If only an Area centre is known, label “From approximate area location”; do not imply door-to-door precision or reveal the exact Unit before a Connection. Show the destination faculty and reference departure/scenario.
- Display `min` and `RM … / trip` (one way). Keep Room rent `RM … / month` and deposit separate; remove the true-monthly-cost headline and combined rent/utilities/commute breakdown. Never multiply fares or CO₂ into a month.
- E-hailing shows a sourced fare estimate or range, never a live Grab quote or booking promise. Clarify whether duration includes pickup waiting; unavailable fare assumptions show “Fare estimate unavailable”, not RM 0. No mock fare becomes a production value.
- Basic public-transport instructions explain walk-to-stop, service/line, boarding/alighting, transfers and final walk using available route data. Include a UM shuttle leg only when its stops and schedule are supported; otherwise explain the missing data, without inventing a complete route.
- Every estimate is for leaving **now**, at the moment the student opens the screen, and is labelled “Estimated for leaving now”. Time and fare change with the clock only where the data supports it (scheduled transit departures and sourced fare rules); free routing has no live traffic, so road times are typical times and are labelled that way. Scheduled transit is distinguished from live arrivals. No “arrives before class”, “late” or recommended class departure logic. The detailed map/timeline loads only on demand (P1).
- Highlight the lowest known one-way fare among available applicable modes, breaking ties by shorter duration; include walking when available. If highlighting transit while walking is free, use an explicitly narrower label such as “Cheapest paid transport”. Missing routes/fares do not rank as free. Show partial data and reasons when comparison is incomplete.
- CO₂ is secondary, estimated per trip from sourced factors and route-leg distances; unknown is unavailable, never zero. It appears on details only, not Room cards.
- Shared route caching uses origin precision, faculty, mode, time-of-day bucket and data version, with no user identity, timetable or attendance count. Provider limits and NFR1 still apply.

Implementation ownership and file changes: [commute handoff](../plans/2026-11-11-commute-per-trip.md). Existing monthly/class-time code has not yet migrated.

### Module 5: Shared house tools
| ID | Requirement | Priority |
|---|---|---|
| FR5.1 | Users who live together shall be able to create a household (created when a Group's tenancy starts, or created directly by students who already live together and invite the others with a code, or joined by invite code), add shared expenses (rent, utilities, groceries) with who paid and who shares, and see who owes whom. | P0 |
| FR5.2 | The household shall be able to define recurring chores, and the system shall rotate the assignments, remind the person whose turn it is, and let them mark the chore done. | P1 |
| FR5.3 | The household shall be able to keep a list of house rules (e.g. quiet hours, guests, cleaning) that each member must accept, and shall see which members have accepted. | P1 |

**Acceptance criteria [DRAFT]**
- Rent is split by each room's price from the unit (Module 2); utilities and other shared expenses are split equally unless the user picks a custom split.
- Balances across all members always sum to zero (unit-tested edge cases: 2 members, 4 members, rounding).
- The app only calculates and tracks who owes whom; members mark a debt as settled. It does not process payments.
- Household data (expenses, chores, rules) is readable and writable only by that household's members.
- A person who found the Unit through a Listing becomes a household member **only after the Host marks their room occupied and they confirm their move-in**. Before that they only have a Connection with the Host, so shared expenses never include someone who has not moved in.
- Students who already live together can start a Household without a Listing or a Group (Ary, 11 Nov 2026): in the Unit tab a student with no Household chooses "I already live with people", names the Unit (the area is optional and only used for the commute to the faculty) and gets a 6-character invite code, valid 7 days, that each housemate enters under "Join with a code". The creator is the Host. One student is in one Household at a time. With no Listing there are no Room prices, so rent is split equally or by a custom amount.
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
- The contract check is never shown on a Listing, Room card or room details page. A tenancy agreement is confidential, so it reaches the student privately: the Host shares it in the Connection chat (a chat attachment visible only to the two people), and the student can run the check from there, upload their own copy from the chat's + menu, or upload it from Profile.

### Optional modules (P2)
- **Module 7: Shared commute groups (P2).** Students from the same area who go to campus on the same days form a recurring commute group (shared car or train + UM shuttle). Availability is supplied only if joining this optional P2 flow; it is not a profile requirement or an input to Module 4 estimates. There are no fixed driver or passenger roles: every member is equal and pays an equal share.
  - FR7.1: the user shall be able to create a recurring commute group with start area, departure time, days, maximum members and travel mode.
  - FR7.2: the system shall suggest commute groups by home area and campus days, and let a user request to join a group that its existing members accept or decline.
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
| **NFR2 Privacy and security** | Only verified UM accounts access the app. A user's phone number and contract uploads, and a Listing's exact Unit (block, floor, door number), are visible only to that user or to a Connection; a Building's street address is public; contact details only once a Connection exists (both sides have accepted). | Students share where they live, their phone and contracts with strangers; a leak is a real safety risk. | Firebase Security Rules unit tests in the emulator: every unauthorised read/write must be denied (target 100%). |

**What another person can see, by stage**

| Stage | Visible | Not visible |
|---|---|---|
| Match | initials, faculty, year, lifestyle tags, match score and reasons | name, photo, phone, address, budget |
| Connection request | same as Match, plus the short message the sender wrote | name, photo, phone, address |
| Connection | chat, first name, the Unit's exact address (for a viewing, only to someone connected to the Host); phone number only if its owner ticked "share my number" | photo |
| Household | shared expenses, chores and rules, profile photos | anything a member hid in their privacy settings (FR1.3) |

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
| Listing reader (FR2.3 shortcut, **P2**) | Read building name, total rent, deposit and rooms from pasted listing text or a screenshot into the same editable fields | Saves typing when the user already has a long listing text; the manual form remains the main path | 20 sample listings (text and screenshots, written by the team from real postings); share of fields extracted correctly | ≥ 90% [DRAFT] |

**Design rules**
- The compatibility **score** and **CO₂** are computed by code, never by the AI.
- All AI and routing calls run on the server (a Cloudflare Worker, see section 9); API keys never ship in the app.
- AI output is advice; the user always confirms.
- Failure of the AI service must degrade gracefully (manual filters, rule-based reasons, "try again" for the contract check).
- Provider: Gemini API or Claude API (decision pending, see Open questions).

---

## 7. User experience

### Navigation
A floating bottom bar with four tabs, **Home, Rooms, Housemates, Unit**, plus a separate blue **Post** button for listing a room. "Home" and "Unit" use different icons (house vs. door) so they are not confused.

There is no Ask tab. The AI features sit where they are used: plain-language search (FR6.1) is the Rooms search bar, and the contract check (FR6.2) is **private**: it opens only from the agreement the Host shares in a Connection chat, or from the Contract check row in Profile. It never appears on a listing, a Room card or the room details page.

| Screen | Purpose | Modules |
|---|---|---|
| Home | New rooms, new matches, getting started | 1, 2, 3, 4 |
| Rooms | Room grid with filters; the search bar takes plain-language requests (AI) | 2, 6 |
| Post | List a room: photos, building, room type, price | 2 |
| Evaluate unit | Short form for any unit's name, rent and rooms (optional "Paste listing" AI shortcut, P2), see the default room price split (master, medium, small), each member's Room price and separate one-way commute estimates | 2, 3, 4 |
| Room details | Listing and monthly Room rent; “Getting to [faculty]” with one-way time/fare estimates and the public transport row opens its own route page: basic instructions in P0, map and timeline in P1 | 2, 4, 6 |
| Contract check | Upload a tenancy agreement and see clauses marked Red flag, Check or OK. Private: opened from a Connection chat (the + menu, or an agreement shared there) or from Profile, never from a listing | 6 |
| Match | Match score, reasons, things to discuss, group rental | 3 |
| Unit | Shared expenses and who owes whom, chore rotation, house rules, and the "Getting to [faculty]" commute guide from the student's own Unit (only once they live in a Household) | 4, 5 |

### Design principles
1. **The commute is never hidden, but CO₂ stays out of the way:** monthly rent and the one-way time and distance to the faculty appear on every Room card; per-trip CO₂ lives in the commute options card on the detail page.
2. **One main action per screen**, in brand blue (Continue, Connect, Post).
3. **Accessible by default** for UM's diverse students.
4. **Privacy by default:** initials instead of photos; the exact Unit and phone hidden until a Connection exists (the Building's street address is public).

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
2. Selects her faculty and enters Room budget and habits, without a timetable or campus-day count, then searches "room under RM550, max 30 minutes to FSKTM by train, quiet housemates".
3. Each Room card shows monthly rent separately from the estimated one-way time and distance to her faculty. Opening one explains how to reach her faculty by available modes, including an estimated e-hailing fare; it never combines rent and commuting into a monthly total.
4. Uploads the tenancy agreement; AI flags risky clauses such as three months' rent in advance.
5. Finds a compatible housemate whose unit has a vacant small room at the price the host set; she sends a Connection request, the Host accepts, and chat opens so she can arrange a viewing.
6. After moving in, the household sets up the rent split by room price, adds the monthly bills, rotates chores, and agrees the house rules in the Unit tab. The Unit tab is also where she sees how to get from her own Unit to her faculty.

---

## 8. Data model

Cloud Firestore collections (the proposal lists nine; this PRD adds `rooms`, `unitEvaluations`, `listingRequests`, `chats` and `config`). Field-level detail, who may write each field and the retention schedule are in [schema.md](../engineering/schema.md); this table keeps only the key fields. **Money is stored as whole sen** (45000 = RM 450.00), in fields ending in `Sen`, so splits add up exactly.

| Collection | Key fields | Used by (module) |
|---|---|---|
| `users` | uid, initials, gender (male/female, required, decided 31 Oct 2026), faculty, year, lifestyleTags[], createdAt, updatedAt (public profile; the UM email is read from the sign-in token and not copied here) | M1, all |
| `users/{uid}/private/profile` | fullName, budgetMinSen, budgetMaxSen, hasCar, homeAreaId, privacy{} (owner only) | M1 |
| `users/{uid}/private/contact` | firstName (readable after a Connection and inside a Household) | M1, M3 |
| `users/{uid}/private/phone` | phoneNumber (readable after a Connection only if the owner opted in, and inside a Household) | M1, M3 |
| `users/{uid}/private/photo` | photoUrl (owner and Household only) | M1, M5 |
| `users/{uid}/private/membership` | householdId (owner only; Security Rules read it with `get()` to check Household membership) | M5 |
| `areas` | areaId, name, lat, lng, nearestStations[] | M2, M4 |
| `listings` | listingId, ownerUid, areaId, buildingName, buildingAddress, placeId, totalRentSen, depositSen, genderPref, amenities[], photoUrls[], isSample, status, createdAt, expiresAt | M2, M4 |
| `listings/{listingId}/private/location` | exactAddress (the Unit: block, floor, door number), residentUids[] (Host, residents and accepted requesters only) | M2 |
| `listingRequests` | requestId (`{listingId}_{requesterUid}`), listingId, hostUid, requesterUid, message, status (pending/accepted/rejected/cancelled), createdAt | M3 |
| `connections` | pairId (the two UIDs sorted), uidA, uidB, source (listingRequest/match), listingId, hostUid, status (active/ended), createdAt, endedAt (proof of a Connection; opens contact, phone and chat) | M3 |
| `rooms` | roomId, listingId, type (master/medium/small), priceSen, status (vacant/occupied) | M2, M4 |
| `matchProfiles` | uid, answers{sleep, cleanliness, noise, cooking, smoking, pets, budget}, updatedAt | M3 |
| `matches` | matchId, uidA, uidB, scoreBasisPoints, formulaVersion, weightVersion, factors[], reasons[], aiGenerated, statusA, statusB, createdAt | M3 |
| `groups` | groupId, memberUids[], targetListingId, maxMembers, createdByUid, name, inviteCode, inviteExpiresAt, joinRequests[], roomSplit[{memberUid, roomType, priceSen}], splitOption, memberStatus[{uid, status, note}], checklist{}, acceptedBy[], status | M3 |
| `unitEvaluations` | evalId, createdByUid, groupId (optional), buildingName, areaId or lat/lng, referenceUrl (optional, a note typed by the user, never fetched), totalRentSen, depositSen, rooms[{type, priceSen, assignedUid}], createdAt | M2, M3 |
| `households` | householdId, memberUids[], listingId, rules[{text, acceptedBy[]}], createdAt | M5 |
| `expenses` | expenseId, householdId, description, amountSen, paidByUid, splitAmong[{uid, shareSen}], date, settled | M5 |
| `chores` | choreId, householdId, task, frequency, rotation[uid], currentUid, dueDate, done | M5 |
| `chats` (+ `messages`) | chatId, participantUids[], listingId or matchId, lastMessageAt; messages: senderUid, text, sentAt | M3 |
| `config` | roomPriceWeights (read-only, seeded) | M2, M4 |
| `commuteGroups` (P2, Module 7, not in the P0/P1 schema yet) | groupId, createdByUid, startAreaId, departTime, days[], maxMembers, mode, costPerTrip | M7 |
| `commuteMembers` (P2, Module 7, not in the P0/P1 schema yet) | memberId, groupId, memberUid, date, status, sharePaid, co2SavedKg, ratingsGiven{} | M7, impact |
| `contractChecks` | checkId, uid, summary, flags[{level, clause, text}], aiGenerated, createdAt, deleteAfter | M6 |

Room photos are stored outside Firebase and referenced by URL: Firebase Storage needs a paid plan, so the plan is Cloudinary Free with signed uploads **[DRAFT]**. Contract files are sent to the check and not kept; only the summary and flags are stored **[DRAFT, Gathan to confirm the schema]**.

**Access rules (NFR2):** Security Rules judge a whole document, so each stage of sharing has its own document (the `private` ones above). A user reads and writes only their own `users` documents and `contractChecks`. Another user reads `contact` and `phone` only after a Connection: an active `connections` record exists for the two users (created when both sides accepted in `matches`, or when the Host accepted a `listingRequests` request). A listing's exact address follows the same rule for the Host and the accepted requester. Only members of a Household read or write that household's `households`, `expenses` and `chores`. `contractChecks` are deleted after 30 days.

---

**E2.1 clarification (Gathan, 9 Nov 2026; accepted by Ary 10 Nov 2026):** step 1 saves a Host-only Listing
with `status: draft`. It is excluded from public search and cannot receive
Connection requests. E2.2 publishes only after the required priced vacant Room
exists; the 30-day Listing expiry begins at publication. Drafts have no `expiresAt`.

## 9. System architecture

```
Android app (Java)
   ├─ Firebase Authentication
   ├─ Cloud Firestore
   ├─ Map: MapLibre Native with free OpenStreetMap-based tiles (no key, no billing)
   ├─ Room photos: Cloudinary Free (signed uploads)
   ├─ Firebase Cloud Messaging  (notifications → app)
   └─ HTTPS (Firebase ID token) ─► Cloudflare Worker (JavaScript, free plan)
                                    ├─ match scoring, cost split, CO₂
                                    ├─► openrouteservice (walking, road)
                                    ├─► Prasarana GTFS timetables (bus, rail), prepared offline
                                    └─► AI API (free tier)
```

- The app reads and writes Firebase directly through the SDK and receives notifications from Cloud Messaging.
- Anything needing a secret key or heavy logic goes through the Worker, which checks the user's Firebase sign-in token, calls the routing and AI services and returns the result. The whole stack is free: the team does not enable billing on any service.

| Layer | Tool |
|---|---|
| Mobile app | Native Android: Java, Android Studio, XML layouts with Material Components, Activities and Fragments, MVVM (ViewModel, LiveData), RecyclerView, Navigation component |
| Sign-in | Firebase Authentication |
| Database | Cloud Firestore |
| File storage | Cloudinary Free **[DRAFT]** (Firebase Storage needs a paid plan) |
| Server logic | Cloudflare Workers Free, JavaScript (Firebase Cloud Functions need a paid plan) **[DRAFT until Aaron's spike passes]** |
| Map in the app | MapLibre Native with free OpenStreetMap-based tiles |
| Routes | openrouteservice (walking, road), Prasarana GTFS (bus, rail), UM shuttle seed data |
| AI | Gemini API (or Claude API) |
| Notifications | Firebase Cloud Messaging |
| Design | Figma |
| Code and tasks | GitHub, ClickUp |
| Testing | JUnit and Mockito, Espresso, Firebase Emulator Suite, k6 |

**Engineering conventions:** feature branches merged by PR with one reviewer; Google Java Style (Checkstyle) and Android Lint must pass; commit messages name the story (e.g. `[E7.1] Add balance calculation`); the story task holds the FR.

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
| Unit | Expense split and balances, CO₂ calculation, match score formula | JUnit and Mockito (app), Jest (server code) | All pass |
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
| Inaccurate listings or scams | UM-verified posters only, report button |
| Safety when meeting housemates or commuting with strangers | Verified accounts, ratings, contact hidden until both accept (share-my-trip link if commute groups are built) |
| CO₂ figures are estimates | Label as estimates and name the emission factors |
| AI can be wrong | Editable filters, "not legal advice" label, report-a-wrong-result button |
| Expense splitting could be mistaken for a payment service (and, in P2, shared trips for an unlicensed ride service) | The app only calculates and tracks who owes whom, never moves money; shared-trip cost-sharing is capped at the trip's cost with no profit |
| Privacy breach | Security Rules tests, 30-day contract deletion, anonymised-only planning data, PDPA 2010 compliance |

---

## 14. Open questions

1. ~~SDG choice~~ **Decided:** SDG 11 (main target 11.1, supporting 11.2); the team has confirmed the brief allows any SDG, not only 6 or 13.
2. ~~Platform~~ **Decided:** native Android in Java, because the team learned Java in earlier semesters and the brief itself talks about "activities". 
3. Which AI provider: Gemini or Claude? (Cost, quota, Malay language quality.)
4. Which published emission factor source for each travel mode?
5. ~~Firestore collections~~ **Decided 29 Oct 2026:** the list in section 8 stands, including `listingRequests` (the proof of a Connection between a student and a Host), `chats` and `config`. `reports` and `notifications` stay optional (P2) and are not in section 8 yet. Gathan keeps `schema.md` in line with this table.
6. Evidence is from 2022 news; the 2026 survey must confirm the current situation before requirements freeze.
7. Does the Prasarana GTFS data cover the bus/LRT routes students actually use around UM? Check a few real routes (e.g. Pantai Dalam, Kerinchi) early. The UM shuttle is not in it, so the team seeds shuttle stops and timetables from UM transport.

8. ~~Server language~~ **Decided:** keep JavaScript, written small and simple by Aaron and Ilham (the rest of the app is Java). Updated 27 Oct 2026: the server is a Cloudflare Worker, because the team uses free plans only and Firebase Cloud Functions need billing. **[DRAFT]** until Aaron's feasibility spike passes.

9. ~~Who may register~~ **Decided 30 Oct 2026 (Ary): UM email only**, so that every account is a verified UM student and scams are harder. Domain: only `siswa.um.edu.my` (students only; staff mail is not accepted, decided 30 Oct 2026). Open: whether postgraduate students have another domain, and what first-year students without a UM email yet can do until they get one (Dafi asks UM when new students receive their address). **Decided 31 Oct 2026 (Ary):** first-year students wait until they receive their UM email; the sign-up screen says so and the report lists it as a limitation.

---

## 15. Future work

An iOS version; web version for landlords; Malay and Chinese interface options (English and Malay at launch); live train arrival times; adoption by BHEP as UM's official off-campus housing channel.
