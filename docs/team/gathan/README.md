# Gathan: Backend and Database Developer

## You own
- **Module 2** Housing listings and area guide (FR2.1, FR2.2, FR2.3, FR2.4)
- The Firestore schema and **Security Rules (NFR2)** for all modules
- Storage rules, seed data, and FCM notifications

Your schema and rules are the first thing others wait for, so they land first.

## Your stories (subtasks for each are in its task file)

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance*, *Not in scope* and *Open questions*. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

### Do in this order (stories you lead: 12)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E0.5** Data schema and Security Rules | [E0.5.md](../../../TASKS/E0/E0.5.md) | P0 | draft 8 Nov, merged 15 Nov | - |
| 2 | **E2.1** Pick the building and enter rent and deposit | [E2.1.md](../../../TASKS/E2/E2.1.md) | FR2.1, P0 | 25 Nov | E0.5, E1.3 |
| 3 | **E2.2** Rooms and prices with the price rule | [E2.2.md](../../../TASKS/E2/E2.2.md) | FR2.1, P0 | 27 Nov | E2.1 |
| 4 | **E2.3** My listing: renew, requests and mark occupied | [E2.3.md](../../../TASKS/E2/E2.3.md) | FR2.1, P0 | 29 Nov | E2.2 |
| 5 | **E2.4** Fictional seed listings | [E2.4.md](../../../TASKS/E2/E2.4.md) | P0 | 29 Nov | may start early · E0.5 |
| 6 | **E3.1** Rooms screen and Room cards | [E3.1.md](../../../TASKS/E3/E3.1.md) | FR2.2, P0 | 2 Dec | E2.4 |
| 7 | **E3.2** Filters | [E3.2.md](../../../TASKS/E3/E3.2.md) | FR2.2, P0 | 3 Dec | E3.1 |
| 8 | **E7.2** Household data and rules | [E7.2.md](../../../TASKS/E7/E7.2.md) | FR5.1, P0 | 8 Dec | E0.5 |
| 9 | **E5.4** Notifications | [E5.4.md](../../../TASKS/E5/E5.4.md) | P1 | 13 Dec | E5.1 |
| 10 | **E9.1** Evaluate any unit | [E9.1.md](../../../TASKS/E9/E9.1.md) | FR2.4, P1 | 17 Dec | E3.4 |
| 11 | **E9.4** Area page and building reviews | [E9.4.md](../../../TASKS/E9/E9.4.md) | FR2.3, P1 | 20 Dec | E2, E3 |
| 12 | **T2.3** NFR2: privacy and security evidence | [T2.3.md](../../../TASKS/T2/T2.3.md) | P0 | 27 Dec | all rules |

### Stories where you do a subtask (14)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../../../TASKS/E0/E0.4.md) | 30 Oct |
| **T1.1** Survey and interviews | Aly | Share the survey link in your circles | [T1.1.md](../../../TASKS/T1/T1.1.md) | 8 Nov (launch 31 Oct) |
| **T1.3** Verify the assumed numbers | Dafi | #1 Room price weights, #7 Utilities estimate · 10 Nov; #8 Data retention against PDPA 2010 · 13 Nov | [T1.3.md](../../../TASKS/T1/T1.3.md) | 13 Nov |
| **E1.3** Profile setup and privacy | Ary | `users` fields and rules so hidden details stay hidden until a Connection | [E1.3.md](../../../TASKS/E1/E1.3.md) | 22 Nov |
| **E3.4** True monthly cost and travel time on the Room card | Aaron | Room card shows cost and travel time; "no mode reaches class on time" message | [E3.4.md](../../../TASKS/E3/E3.4.md) | 4 Dec |
| **E4.3** Send a request to the Host | Ary | Connection request data and rules | [E4.3.md](../../../TASKS/E4/E4.3.md) | 7 Dec |
| **E5.1** Requests inbox and Connection | Ary | `chats` data and rules for what each stage may see (contact details only after both accept) | [E5.1.md](../../../TASKS/E5/E5.1.md) | 7 Dec |
| **E6.1** Compatibility questions | Ary | `matchProfiles` data | [E6.1.md](../../../TASKS/E6/E6.1.md) | 8 Dec |
| **E5.2** Chat and viewing | Ary | A contract file shared in a chat is private to the two people | [E5.2.md](../../../TASKS/E5/E5.2.md) | 9 Dec |
| **E9.2** Group, split options and member status | Ary | `groups` data and rules | [E9.2.md](../../../TASKS/E9/E9.2.md) | 19 Dec |
| **E8.3** Upload and result screens | Ilham | Private file rules and deletion after 30 days | [E8.3.md](../../../TASKS/E8/E8.3.md) | 20 Dec |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; NFR verification (1:30) | [T3.1.md](../../../TASKS/T3/T3.1.md) | 20 Dec |
| **T4.1** Report sections | Dafi | 2c Non-functional requirements (150), database schema part of 3 | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../../../TASKS/T4/T4.2.md) | 9 Jan |

### Open questions waiting for you (3)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E1.1](../../../TASKS/E1/E1.1.md): Gathan: Security Rules that require `email_verified == true` and the UM domain, with an emulator test. Blocks: the "enforced by Rules" claim for FR1.1 in the report.
- [E2.2](../../../TASKS/E2/E2.2.md): Aly and Gathan: survey check of the weights (T1.3 #1, results 13 Nov). Until then the app and the report call 1.3 / 1.0 / 0.7 an estimate.
- [T4.2](../../../TASKS/T4/T4.2.md): Everyone: fill the TODO fields before merging into the appendix.

### Decided on 31 Oct 2026 for your stories (25)

Read these before you start; the full text is under *Decisions taken* in each task file.

- [E0.5](../../../TASKS/E0/E0.5.md): Gender field: public `users/{uid}.gender`, string `"male"` or `"female"`, required when the profile is created (E1.3). Gathan adds it to the schema and the Rules.
- [E0.5](../../../TASKS/E0/E0.5.md): Timetable `day` is stored as the ISO weekday number 1 to 7 (Monday = 1), the same as `classDays` in `match.js`; the app shows Mon to Sun. `start` and `end` stay `"HH:mm"` in Malaysia time. Needs review by Gathan and Aaron.
- [E1.3](../../../TASKS/E1/E1.3.md): Gender: required at registration (profile setup right after sign-up), male or female, and shown on the profile so others see it. Rooms are not hidden automatically: the Rooms search and the housemate page have a filter chip the user picks (Male / Female / Any), default Any. PRD FR1.3 and section 8 updated.
- [E1.3](../../../TASKS/E1/E1.3.md): The gender field lives in the public `users/{uid}` document, because others must see it.
- [E1.3](../../../TASKS/E1/E1.3.md): Timetable day format: stored as ISO weekday numbers 1 to 7 (Monday = 1), matching `match.js`; `start` and `end` are `"HH:mm"` in Malaysia time, and the commute function uses `start` as the class start. Needs review by Gathan and Aaron.
- [E1.3](../../../TASKS/E1/E1.3.md): A Host needs no extra profile data: any student can be a Host; unit details live in the listing, and the phone number stays in the private document until a Connection.
- [E2.1](../../../TASKS/E2/E2.1.md): Photos: no Firebase Storage (it needs the Blaze plan). Photos go to Cloudinary Free through an upload signed by the Worker (the API secret stays in the Worker), and Firestore stores the URLs, as in the schema draft. The first subtask now says so.
- [E2.1](../../../TASKS/E2/E2.1.md): No Google Places autocomplete (it needs billing). The building picker uses the seeded area list plus a typed building name; openrouteservice geocoding is used only if its free quota fits (Aaron, assumption #11).
- [E2.3](../../../TASKS/E2/E2.3.md): Closing by itself is a check on read, not a scheduled job: each listing has `expiresAt` (renewed date + 30 days), searches filter `expiresAt > now`, and "My listing" shows Expired with a Renew button.
- [E2.4](../../../TASKS/E2/E2.4.md): Reference prices: hand-noted price facts from public advertisements (area, room type, price range, source and date), as the rules in `ASSUMPTIONS.md` allow, checked against the survey (T1.2). Gathan notes them, Aly checks.
- [E3.1](../../../TASKS/E3/E3.1.md): Before any search the Rooms tab shows the newest active listings first, with the filter chips on top. If the student saved a faculty, cards show travel time (E3.4). No location permission is needed.
- [E3.2](../../../TASKS/E3/E3.2.md): Gender filter: a chip "For: Male / Female / Any", default Any. Male shows `male_only` and `any` listings, Female shows `female_only` and `any`. Nothing is hidden without the user choosing it (see E1.3).
- [E4.1](../../../TASKS/E4/E4.1.md): Reported listings: reports go to a `reports` collection; Gathan moderates, Ary is the backup; the sheet says only "The team will review this" and promises no time; the item stays visible until a moderator hides it.
- [E4.3](../../../TASKS/E4/E4.3.md): The request message is at most 300 characters. A listing with no vacant room cannot receive requests: the button is disabled and says "No rooms free".
- [E5.1](../../../TASKS/E5/E5.1.md): An unanswered request expires after 14 days, checked on read like listings (`expiresAt`); the sender sees "Expired".
- [E5.2](../../../TASKS/E5/E5.2.md): Chat attachments are photos only, resized on the phone to at most 1600 px and 2 MB, uploaded through the same Cloudinary flow as listings (E2.1). Contract files never go into a chat.
- [E5.3](../../../TASKS/E5/E5.3.md): Joining a Household: after a Connection with the Host, or with an invite code made by a member (6 characters, valid 7 days, one Household). The code covers students who already live together.
- [E5.4](../../../TASKS/E5/E5.4.md): Notifications start in the app: a `notifications` collection and a badge, read while the app is open. Push through FCM from the Worker comes only if time remains after the freeze (P1).
- [E6.1](../../../TASKS/E6/E6.1.md): Gender is not part of the match score. The housemate page has the same filter chip as Rooms (Male / Female / Any, default Any).
- [E7.2](../../../TASKS/E7/E7.2.md): 6-month deletion: one daily cleanup job on the Worker (a Cron Trigger with admin access) deletes what is past its retention period; Aaron sets up the trigger and checks it is in the free plan (assumption #11), Gathan writes the queries.
- [E8.3](../../../TASKS/E8/E8.3.md): Contract files are not stored: the app sends the PDF or photo (at most 5 MB) to the Worker, which reads it in memory and discards it. Only the result is saved in `contractChecks`, deleted after 30 days by the daily cleanup job (E7.2).
- [E9.1](../../../TASKS/E9/E9.1.md): The 90-day deletion is part of the same daily cleanup job as E7.2.
- [E9.2](../../../TASKS/E9/E9.2.md): The group picks one member as Host; by default the person who created the group, and any member can hand it over before signing.
- [E9.4](../../../TASKS/E9/E9.4.md): Building reviews: reports go to a `reports` collection; Gathan moderates, Ary is the backup; the sheet says only "The team will review this" and promises no time; the item stays visible until a moderator hides it.
- [T2.3](../../../TASKS/T2/T2.3.md): Starter list of cases (Gathan completes it): a stranger cannot read `private/*`; at the Match stage only public fields are readable; contact and phone are readable only when `connections/{pairId}` exists; the app cannot create or delete `connections`; a non-member cannot read a Household; an unverified or non-UM account cannot write; `contractChecks` is readable only by its owner.

Your schema and rules (E0.5) land first because everyone waits for the field names.

## Privacy checklist (NFR2 and PDPA 2010)
- Only verified UM accounts can read anything
- Exact address, phone number and contract uploads visible only to the owner
- Contact details only after both sides accept
- Collect only what each feature needs; consent at sign-up

## Your parts in the graded deliverables
- **Video:** the posting and search part of the demo, segment 5 NFR2 evidence (rules tests, all denied)
- **Report:** section 2c Non-functional requirements (150 words), the database schema part of section 3, the rules tests in section 5

## Numbers you find or use (see [ASSUMPTIONS.md](../../product/ASSUMPTIONS.md))
- **#1 Room price weights** and **#7 Utilities estimate:** Aly finds them, you put them in config and seed data (by 10 Nov)
- **#8 Data retention periods:** you own the reasoning against the PDPA 2010 Retention Principle, with Dafi (due 13 Nov)

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
