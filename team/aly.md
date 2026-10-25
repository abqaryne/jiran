# Aly: QA Engineer and UX Researcher

## You own
- **Module 5** FR5.2 chore rotation and FR5.3 house rules (with Dafi, both P1)
- The survey, interviews and user study
- The test plan, integration tests, the load test (NFR1), and the usability test
- The release checklist and the video editing

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] Put [SURVEY.md](../SURVEY.md) into a Google Form, in **English and Malay**
- [ ] **Pilot with 5 people** and time it (aim for 8 minutes or less); cut questions if needed
- [ ] Launch and share. Target **100 or more** responses by 8 Nov, spread across faculties and areas (not only FSKTM). Ask Telegram channel admins for permission before posting.
- [ ] Plan **5 to 8 interviews** (20 minutes each): 3 to 4 off-campus students, a UMSU representative, a Telegram admin. Prepare a consent form.
- [ ] Start the test plan: one test case per FR, plus the NFR1 and NFR2 tests

## Next
**Step 1 (by 15 Nov):** analyse the survey and send to the team:
- Room prices for master, medium and small (to set the 1.3 / 1.0 / 0.7 weights) to Gathan
- Peak hours and e-hailing fares to Aaron
- Feature importance (question H1) to Dafi, to confirm P0 and P1
- The "finding, FR it shaped" table with Dafi

**Wave A (16 to 29 Nov): needs nobody**
- Flutter `integration_test` scaffolding and the k6 script for the search endpoint (100 concurrent users, 95% under 3 s)
- Chore rotation logic in plain Dart with unit tests; house rules model

**Wave B (30 Nov to 13 Dec)**
- FR5.2 and FR5.3 screens (waits for Dafi's household data and Ary's components)

**Step 4 (14 to 27 Dec): you lead testing**
1. Integration tests for each FR's main flow
2. Load test (NFR1) and report the 95th percentile
3. **Usability test:** 8 to 10 UM students, five tasks, SUS score at least 68 and 80% task success
4. Collect the NFR2 evidence from Gathan's rules tests
5. Release checklist: no P0 bug left before the video

## Your parts in the graded deliverables
- **Video:** the chores and house rules part of the demo, segment 5 NFR1 results, segment 6 survey and interview findings. You also record, assemble and edit the video (Ary reviews against the rubric).
- **Report:** section 2a User study findings (350 words), section 5 Testing and validation (300 words)
- **Keep as evidence:** anonymised survey results and charts, interview notes and quotes (with consent), test results, SUS scores, screenshots

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, `flutter analyze` and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../SURVEY.md).
