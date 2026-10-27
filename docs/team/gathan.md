# Gathan: Backend and Database Developer

## You own
- **Module 2** Housing listings and area guide (FR2.1, FR2.2, FR2.3, FR2.4)
- The Firestore schema and **Security Rules (NFR2)** for all modules
- Storage rules, seed data, and FCM notifications

Your schema and rules are the first thing others wait for, so they land first.

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Epic E2 is yours, plus the data and rules for every module. Your schema and rules land first because the others wait for the field names.

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E0.5** Data schema and Security Rules: schema from PRD section 8 plus `chats`, first rules draft, rules tests, reference prices and 10 to 15 fictional seed listings (do not copy listings, see the rule in [ASSUMPTIONS.md](../product/ASSUMPTIONS.md)), send the field list to the group | P0 | draft 8 Nov, merged 15 Nov | none |
| **E7.3** (part) #1 room price weights and #7 utilities (Aly finds them, you put them in config and seed data), #8 retention periods with Dafi | P0 | 10 to 13 Nov | Aly's survey |
| **E2.1** Post a unit with priced rooms (FR2.1): data, photos, building picker, the room price rule with JUnit, My listing, the post screens | P0 | 29 Nov | E0.5 |
| **E2.2** Search and filter rooms (FR2.2): query, Rooms screen with Room cards and the filters sheet, travel-time filter, import the seed listings | P0 | 13 Dec | E2.1; Aaron's commute function (E4.2) |
| **E3.3** (part) `chats` data and rules for what each stage may see; the contract file shared in a chat is private to the two people | P0 | 13 Dec | E0.5 |
| **E2.3** Area page and moderated building reviews | P1 | 13 Dec | E2.1, E2.2 |
| **E2.4** Evaluate any unit: `unitEvaluations`, room price split, screens, delete after 90 days | P1 | 13 Dec | E2.1, E4.2 |
| **E3.4** (part) `groups` data and rules | P1 | 13 Dec | E3.3 |
| **E5.1** (part) `households` and `expenses` data and rules, members only; FCM notifications | P0 | 13 Dec | E0.5 |
| **E8.3** NFR2 evidence: rules tests where every unauthorised access is denied, privacy checklist with screenshots | P0 | 27 Dec | all rules |
| **E6.3** (part) Private contract file rules and deletion after 30 days | P0 | 20 Dec | Ilham |

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
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
