# Ilham: AI Engineer

## You own
- **Module 6** AI assistant and contract checker (FR6.1, FR6.2, FR6.3)
- The **match reason text** for Module 3 (FR3.2)
- AI evaluation, API key handling, and the **AI Usage Log** (mandatory appendix)

## Your stories (subtasks for each are in [trello-cards.md](trello-cards.md))

Epic E6 is yours, plus the match reasons in E3.2, the AI evaluation and the AI Usage Log appendix.

**Cloud Functions are Node.js (decided).** Firebase does not support Java, so you and Aaron write the functions in JavaScript: keep each one small and single-purpose, no big framework, and unit-test them with Jest. The rest of the app stays Java. You build your own AI screens in Android with Ary's components.

| Story | Priority | Due | Waits for |
|---|---|---|---|
| **E6.1** Choose and set up the AI provider (#10): compare Gemini and Claude on cost, quota, speed, Malay and English quality, keys in Cloud Functions secrets only, **start the AI Usage Log today** (Appendix 2(b): tool, exact prompt, how the output was used, tested and changed) | P0 | 8 Nov | none |
| **E6.3** (part) Collect **10 sample tenancy agreements** (anonymised) and write the known risky clauses of each; this is the ground truth for evaluation | P0 | 8 Nov | none |
| **E0.6** (part) Agree with Aaron the request and response shape of each AI function | P0 | 15 Nov | none |
| **E6.2** Plain-language search (FR6.1): turns English or Malay text into filters the user can see and edit before results load, in the Rooms search bar (no separate Ask tab); mock filters first; the 50 test requests written by surveyed students | P0 | 13 Dec | E2.2 for the real filters |
| **E6.3** Private contract check (FR6.2): upload a PDF or photo, a function marks each clause Red flag, Check or OK; the screens open only from a chat attachment or Profile, never from a listing | P0 | 20 Dec | Gathan's private file rules |
| **E6.4** AI labels and reporting a wrong result (FR6.3): every AI output labelled AI-generated, "not legal advice" on the contract result, a button to report a wrong result | P0 | 20 Dec | E6.2, E6.3 |
| **E3.2** (part) Match reasons from the score breakdown of Aaron's formula; rule-generated reasons if the AI fails | P0 | 13 Dec | Aaron's score function |
| **E6.5** AI evaluation for the report: 50 requests, 10 contracts, user ratings of the reasons | P0 | 20 Dec | E6.2, E6.3 |
| **E10.2** AI Usage Log appendix: merge everyone's logs, check nothing is invented | P0 | 4 to 9 Jan | everyone's logs |
| **P2** Listing reader that pre-fills Evaluate unit from pasted text or a screenshot | P2 | only if time | E2.4 |

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

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story card moved with hours on the board, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../product/PRD.md) (what we build), [TEAM_PLAN.md](TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../product/SURVEY.md).
