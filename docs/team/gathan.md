# Gathan: Backend and Database Developer

## You own
- **Module 2** Housing listings and area guide (FR2.1, FR2.2, FR2.3)
- The Firestore schema and **Security Rules (NFR2)** for all modules
- Storage rules, seed data, and FCM notifications

Your schema and rules are the first thing others wait for, so they land first.

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] Firestore schema from PRD section 8: `users`, `areas`, `listings`, `rooms`, `reviews`, `matchProfiles`, `matches`, `groups`, `unitEvaluations`, `households`, `expenses`, `chores`, `contractChecks`. A store for chat is still missing (PRD open question 5); propose `chats`.
- [ ] First draft of Security Rules: a user reads and writes only their own `users` and `contractChecks`; household data only by household members; contact fields readable only after both sides accept
- [ ] **Seed data:** collect 30 to 50 listings from Telegram rental channels (ask the poster for consent): building name, area, rooms with type (master, medium, small) and price, total rent, deposit. Keep them in a spreadsheet.

## Next
**Step 1 (by 15 Nov):** merge the schema and rules first. Send the field list to the group. Write rules unit tests with the emulator (every unauthorised read and write must be denied).

**Wave A (16 to 29 Nov): Module 2**
- Post a unit: photos (Storage), property or building name from Google Places autocomplete (fills address and coordinates), total rent, deposit, area, gender preference, amenities, and each vacant room with its own price
- **Price rule:** the sum of all room prices in the unit must not exceed the unit's total rent. Default split by weights master 1.3, medium 1.0, small 0.7 (kept in config, to be checked against the survey)
- Search vacant rooms with filters: budget, room type, area, gender, amenities, maximum travel time (calls Aaron's commute function). One card per vacant room.
- Import the seed data

**Wave B (30 Nov to 13 Dec)**
- **P1:** area page and moderated building reviews (FR2.3)
- FCM notifications (match requests, chore reminders)
- `chats` for connect (FR3.3), scheduled function to delete `contractChecks` after 30 days
- **P1:** `unitEvaluations` for Evaluate unit

## Privacy checklist (NFR2 and PDPA 2010)
- Only verified UM accounts can read anything
- Exact address, phone number and contract uploads visible only to the owner
- Contact details only after both sides accept
- Collect only what each feature needs; consent at sign-up

## Your parts in the graded deliverables
- **Video:** the posting and search part of the demo, segment 5 NFR2 evidence (rules tests, all denied)
- **Report:** section 2c Non-functional requirements (150 words), the database schema part of section 3, the rules tests in section 5

## Numbers you find or use (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- **#1 Room price weights** and **#7 Utilities estimate:** Aly finds them, you put them in config and seed data (by 10 Nov)
- **#8 Data retention periods:** you own the reasoning against the PDPA 2010 Retention Principle, with Dafi (due 13 Nov)

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
