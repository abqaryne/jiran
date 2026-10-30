# Gathan: Backend and Database Developer

## You own
- **Module 2** Housing listings and area guide (FR2.1, FR2.2, FR2.3, FR2.4)
- The Firestore schema and **Security Rules (NFR2)** for all modules
- Storage rules, seed data, and FCM notifications

Your schema and rules are the first thing others wait for, so they land first.

## Your stories (subtasks for each are in [work-breakdown.md](work-breakdown.md))

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`docs/tasks/<epic>/<ID>.md`) and read *Why*, *What to build*, *Acceptance*, *Not in scope* and *Open questions*. The subtasks are in [work-breakdown.md](work-breakdown.md); status is only in ClickUp. A task file marked *(draft)* was written for the owner by Ary's assistant: correct it first and tell Ary what was wrong. The index of every task file is [docs/tasks/README.md](../tasks/README.md).

### Do in this order (stories you lead: 12)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E0.5** Data schema and Security Rules | [E0.5.md](../tasks/E0/E0.5.md) (draft) | P0 | draft 8 Nov, merged 15 Nov | - |
| 2 | **E2.1** Pick the building and enter rent and deposit | [E2.1.md](../tasks/E2/E2.1.md) (draft) | FR2.1, P0 | 25 Nov | E0.5, E1.3 |
| 3 | **E2.2** Rooms and prices with the price rule | [E2.2.md](../tasks/E2/E2.2.md) (draft) | FR2.1, P0 | 27 Nov | E2.1 |
| 4 | **E2.3** My listing: renew, requests and mark occupied | [E2.3.md](../tasks/E2/E2.3.md) (draft) | FR2.1, P0 | 29 Nov | E2.2 |
| 5 | **E2.4** Fictional seed listings | [E2.4.md](../tasks/E2/E2.4.md) (draft) | P0 | 29 Nov | may start early · E0.5 |
| 6 | **E3.1** Rooms screen and Room cards | [E3.1.md](../tasks/E3/E3.1.md) (draft) | FR2.2, P0 | 2 Dec | E2.4 |
| 7 | **E3.2** Filters | [E3.2.md](../tasks/E3/E3.2.md) (draft) | FR2.2, P0 | 3 Dec | E3.1 |
| 8 | **E7.2** Household data and rules | [E7.2.md](../tasks/E7/E7.2.md) (draft) | FR5.1, P0 | 8 Dec | E0.5 |
| 9 | **E5.4** Notifications | [E5.4.md](../tasks/E5/E5.4.md) (draft) | P1 | 13 Dec | E5.1 |
| 10 | **E9.1** Evaluate any unit | [E9.1.md](../tasks/E9/E9.1.md) (draft) | FR2.4, P1 | 17 Dec | E3.4 |
| 11 | **E9.4** Area page and building reviews | [E9.4.md](../tasks/E9/E9.4.md) (draft) | FR2.3, P1 | 20 Dec | E2, E3 |
| 12 | **T2.3** NFR2: privacy and security evidence | [T2.3.md](../tasks/T2/T2.3.md) (draft) | P0 | 27 Dec | all rules |

### Stories where you do a subtask (14)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../tasks/E0/E0.4.md) (draft) | 30 Oct |
| **T1.1** Survey and interviews | Aly | Share the survey link in your circles | [T1.1.md](../tasks/T1/T1.1.md) (draft) | 8 Nov (launch 31 Oct) |
| **T1.3** Verify the assumed numbers | Dafi | #1 Room price weights, #7 Utilities estimate · 10 Nov; #8 Data retention against PDPA 2010 · 13 Nov | [T1.3.md](../tasks/T1/T1.3.md) (draft) | 13 Nov |
| **E1.3** Profile setup and privacy | Ary | `users` fields and rules so hidden details stay hidden until a Connection | [E1.3.md](../tasks/E1/E1.3.md) | 22 Nov |
| **E3.4** True monthly cost and travel time on the Room card | Aaron | Room card shows cost and travel time; "no mode reaches class on time" message | [E3.4.md](../tasks/E3/E3.4.md) (draft) | 4 Dec |
| **E4.3** Send a request to the Host | Ary | Connection request data and rules | [E4.3.md](../tasks/E4/E4.3.md) (draft) | 7 Dec |
| **E5.1** Requests inbox and Connection | Ary | `chats` data and rules for what each stage may see (contact details only after both accept) | [E5.1.md](../tasks/E5/E5.1.md) (draft) | 7 Dec |
| **E6.1** Compatibility questions | Ary | `matchProfiles` data | [E6.1.md](../tasks/E6/E6.1.md) (draft) | 8 Dec |
| **E5.2** Chat and viewing | Ary | A contract file shared in a chat is private to the two people | [E5.2.md](../tasks/E5/E5.2.md) (draft) | 9 Dec |
| **E9.2** Group, split options and member status | Ary | `groups` data and rules | [E9.2.md](../tasks/E9/E9.2.md) (draft) | 19 Dec |
| **E8.3** Upload and result screens | Ilham | Private file rules and deletion after 30 days | [E8.3.md](../tasks/E8/E8.3.md) (draft) | 20 Dec |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; NFR verification (1:30) | [T3.1.md](../tasks/T3/T3.1.md) (draft) | 20 Dec |
| **T4.1** Report sections | Dafi | 2c Non-functional requirements (150), database schema part of 3 | [T4.1.md](../tasks/T4/T4.1.md) (draft) | 9 Jan |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | Ilham | Keep your own `docs/ai-log/<name>.md` current, with exact prompts | [T4.2.md](../tasks/T4/T4.2.md) (draft) | 9 Jan |

### Open questions waiting for you (23)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E0.5](../tasks/E0/E0.5.md): Gathan: field names for gender if the team adds it (see E1.3). Blocks: E1.3 and E3.2.
- [E1.1](../tasks/E1/E1.1.md): Gathan: Security Rules that require `email_verified == true` and the UM domain, with an emulator test. Blocks: FR1.1's
- [E2.1](../tasks/E2/E2.1.md): Gathan: the subtask says 'photo upload to Storage', but the schema says Firebase Storage is not used and photos are URLs from a free host. Which one? Blocks: this story's first subtask.
- [E2.1](../tasks/E2/E2.1.md): Gathan and Aaron: Places autocomplete cost and quota on the free plan (#11).
- [E2.2](../tasks/E2/E2.2.md): Aly and Gathan: survey check of the weights (T1.3 #1). Blocks: calling the numbers anything but estimates in the report.
- [E2.3](../tasks/E2/E2.3.md): Gathan: closing 'by itself' needs a scheduled job on the free Worker or a check on read. Which one? Blocks: this story.
- [E2.4](../tasks/E2/E2.4.md): Gathan and Aly: where the reference prices come from (survey T1.2 or hand notes). Blocks: realistic ranges.
- [E3.1](../tasks/E3/E3.1.md): Gathan and Ary: what the Rooms tab shows before any search (recent, nearby)? Blocks: the empty state.
- [E3.2](../tasks/E3/E3.2.md): Ary and Gathan: decide the gender rule before this story starts. Blocks: the gender filter.
- [E4.1](../tasks/E4/E4.1.md): Gathan: who reviews reported listings, and how fast? Blocks: what the report sheet promises.
- [E4.3](../tasks/E4/E4.3.md): Gathan: the maximum message length and the rule for requests to a full listing. Blocks: the sheet's validation.
- [E5.1](../tasks/E5/E5.1.md): Aaron and Gathan: who writes `connections`, the Worker or the app? Blocks: the accept action.
- [E5.1](../tasks/E5/E5.1.md): Gathan: how long an unanswered request stays open. Blocks: the inbox empty state.
- [E5.2](../tasks/E5/E5.2.md): Gathan: file size limit for chat attachments on the free plan. Blocks: the attach button.
- [E5.3](../tasks/E5/E5.3.md): Dafi and Gathan: the invite code alternative for joining a Household (E7.2). Blocks: the move-in screens.
- [E5.4](../tasks/E5/E5.4.md): Gathan: FCM on the free plan needs a sender on the Worker. Blocks: this story.
- [E6.1](../tasks/E6/E6.1.md): Ary and Gathan: gender is a hard filter, not a score factor, if it is added (see E1.3).
- [E7.2](../tasks/E7/E7.2.md): Gathan: how the 6-month deletion runs on the free plan. Blocks: closing.
- [E8.3](../tasks/E8/E8.3.md): Gathan: file size limits and storage on the free plan. Blocks: private file rules.
- [E9.1](../tasks/E9/E9.1.md): Gathan: the daily deletion job on the free plan. Blocks: the 90-day rule.
- [E9.2](../tasks/E9/E9.2.md): Gathan and Ary: who is Host when several sign together (the group picks one). Blocks: the Host choice screen.
- [E9.4](../tasks/E9/E9.4.md): Gathan: who moderates and how fast. Blocks: the review flow.
- [T2.3](../tasks/T2/T2.3.md): Gathan: the list of cases. Blocks: the report.

Your schema and rules (E0.5) land first because everyone waits for the field names.

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

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
