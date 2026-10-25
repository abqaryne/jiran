# Aaron: Software Architect and Backend Lead

## You own
- **Module 4** Commute planner and true monthly cost (FR4.1, FR4.2, FR4.3)
- The architecture, Cloud Functions structure, Google Maps and Routes integration
- Repo, branching rules, CI, the Firebase emulator, and the integration freeze
- The **match score** Cloud Function for Module 3

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] Create the Flutter project in the repo and the folder structure
- [ ] Branching rules: feature branches, PR required, Ary approves and merges. Turn on branch protection
- [ ] GitHub Actions CI: `flutter analyze` and `flutter test` on every PR
- [ ] Create the Firebase project (Auth, Firestore, Storage, Functions, Messaging) and add everyone
- [ ] Cloud Functions skeleton (Node.js) and Firebase emulator setup
- [ ] Google Maps Platform key: restrict it, set a billing alert, never commit it
- [ ] **Check early (PRD open question 7):** does Google's transit data cover the bus, LRT and UM shuttle routes from Pantai Dalam and Kerinchi to UM? If the shuttle is missing, we seed shuttle stops and timetables from UM transport.

## Next
**Step 1 (by 15 Nov):** write `docs/api-contracts.md` with the request and response JSON of each Cloud Function, so everyone can build against stubs:
- `getCommuteOptions(listingId or unit, departTime)`
- `getTransitRoute(...)`
- `computeMatchScores(userId)`
- `splitRoomPrices(totalRent, rooms)`
- AI functions (agree the shape with Ilham)

**Wave A (16 to 29 Nov): Module 4 basic**
- Walking, bus or LRT plus UM shuttle, e-hailing estimate (fare formula), and driving alone
- Travel time and monthly cost from a listing to the user's faculty; true monthly cost = rent plus utilities plus commute
- CO2 per mode = distance times a published emission factor (pick and cite the source, PRD open question 4)
- Cache results per area and faculty pair so search stays under 3 seconds (NFR1)

**Wave B (30 Nov to 13 Dec)**
- Match score Cloud Function: transparent weighted formula, deterministic, unit-tested
- **P1:** peak and off-peak times (traffic-aware Routes API), peak surge assumption for e-hailing, step-by-step transit route

## Hand-offs
| You wait for | From | Use meanwhile |
|---|---|---|
| Listing and area fields | Gathan | Stub listings |
| Profile and timetable data | Ary and Gathan | Stub users |
| Survey data: peak windows, Grab fares | Aly (survey) | Config values to change later |

## Your parts in the graded deliverables
- **Video:** the commute card in the demo, segment 4 technical implementation (1:30, with Ilham 20 s on the AI flow)
- **Report:** section 3 Architecture and design (350 words), section 4 System implementation (400 words, you collect everyone's code segment)
- **You also lead** the P0 code freeze on 13 Dec and review all Cloud Functions and Maps PRs

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, `flutter analyze` and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../SURVEY.md).
