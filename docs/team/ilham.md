# Ilham: AI Engineer

## You own
- **Module 6** AI assistant and contract checker (FR6.1, FR6.2, FR6.3)
- The **match reason text** for Module 3 (FR3.2)
- AI evaluation, API key handling, and the **AI Usage Log** (mandatory appendix)

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Work is organised as flows (epics E0 to E10, done in order) plus parallel tracks (T1 to T4). Full list with every subtask: [trello-cards.md](trello-cards.md).

**Stories you lead** (8)

| Story | FR, priority | Due | Waits for |
|---|---|---|---|
| **E3.5** Plain-language AI search *(can start early)* | FR6.1, P0 | 4 Dec | E3.2 for the real filters (use mock filters first) |
| **E6.3** Match reasons | FR3.2, P0 | 10 Dec | E6.2 |
| **E8.1** Choose and set up the AI provider *(can start early)* | P0 | 8 Nov |  |
| **E8.2** Sample agreements and the contract function *(can start early)* | FR6.2, P0 | 13 Dec | E8.1 |
| **E8.3** Upload and result screens | FR6.2, P0 | 20 Dec | E5.2 (the file arrives in a chat) |
| **E8.4** AI labels and reporting a wrong result | FR6.3, P0 | 20 Dec | E3.5, E6.3, E8.3 |
| **E8.5** AI evaluation for the report | FR6.1, FR6.2, P0 | 20 Dec | E3.5, E8.3 |
| **T4.2** AI Usage Log appendix (Appendix 2(b)) | P0 | 9 Jan | everyone's logs |

**Stories where you do a subtask** (6): open the story card to see yours.

| Story | Lead | Due |
|---|---|---|
| **E0.4** Project management and docs | Dafi | 30 Oct |
| **E0.6** Function contracts (stubs) | Aaron | 15 Nov |
| **T1.1** Survey and interviews | Aly | 8 Nov (launch 31 Oct) |
| **T1.3** Verify the assumed numbers | Dafi | 13 Nov |
| **T3.1** Script and screen captures | Ary | 20 Dec |
| **T4.1** Report sections | Dafi | 9 Jan |

**Cloud Functions are Node.js (decided).** Firebase does not support Java, so you and Aaron write the functions in JavaScript: keep each one small and single-purpose, no big framework, and unit-test them with Jest. The rest of the app stays Java. You build your own AI screens with Ary's components. **Start the AI Usage Log today.**

## Design rules
- The compatibility score and CO2 are computed by code, never by the AI
- All AI calls run in Cloud Functions
- AI gives advice; the user always confirms
- If the AI is down, the app degrades gracefully (manual filters, rule-based reasons, a "try again")

## Evaluation (for the report)
| Feature | Test set | Target |
|---|---|---|
| Plain-language search | 50 requests | At least 90% converted to the correct filters |
| Contract checker | 10 agreements with known problem clauses | Report flags found and false alarms |
| Match reasons | User testing, 5-point scale | Report the mean |

## Your parts in the graded deliverables
- **Video:** the AI features in the demo, 20 s on the AI data flow in segment 4
- **Report:** section 6 Impact, limitations and future work (150 words), the AI usage statement in section 4, **Appendix: AI Usage Log**

## Numbers you find (see [ASSUMPTIONS.md](../product/ASSUMPTIONS.md))
- **#10 AI provider, cost and quota:** compare Gemini and Claude on the sample contracts and requests, and recommend one (due 8 Nov)

## Rules for everyone

- Log your actual hours on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
