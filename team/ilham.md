# Ilham: AI Engineer

## You own
- **Module 6** AI assistant and contract checker (FR6.1, FR6.2, FR6.3)
- The **match reason text** for Module 3 (FR3.2)
- AI evaluation, API key handling, and the **AI Usage Log** (mandatory appendix)

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] **Start the AI Usage Log today.** Use the Appendix 2(b) template from the brief: which tool, the exact prompt, how the output was used, tested and changed. Everyone logs their AI use; you merge it.
- [ ] Collect **10 sample tenancy agreements** (anonymised) and write the list of known risky clauses for each (for example three months' rent in advance). This is the ground truth for evaluation.
- [ ] Try contract-checker prompts with Gemini and Claude on those agreements. Compare cost, quota, and Malay and English quality, then propose one provider (PRD open question 3)
- [ ] Keep keys out of the app: Cloud Functions secrets only

**Cloud Functions are Node.js (decided).** Firebase does not support Java, so you two write the functions in JavaScript: keep each one small and single-purpose, no big framework, and unit-test them with Jest. The rest of the app stays Java.

## Next
**Step 1 (by 15 Nov):** agree with Aaron the request and response shape for each AI function. After the survey, build the **50 test requests** written by surveyed students for plain-language search.

**Wave A (16 to 29 Nov): needs nobody**
- **FR6.2 contract checker:** upload a PDF or photo, a Cloud Function returns a plain-language summary with each clause marked **Red flag, Check or OK** (words, not only colours)
- **FR6.1 plain-language search** (English or Malay): turns text into filters that the user can see and edit before results load. Use mock filters first.
- **FR6.3:** every AI output labelled AI-generated; the contract result states it is not legal advice; a button to report a wrong result
- The AI screens in Android, using Ary's components: the plain-language search lives in the Rooms search bar (no separate Ask tab), and the contract check opens from a room's details page

**Wave B (30 Nov to 13 Dec)**
- Connect FR6.1 to the real listing filters (waits for Gathan's search)
- Match reasons: the prompt uses the score breakdown from Aaron's formula. If the AI fails, show rule-generated reasons.
- **P2:** listing reader that pre-fills Evaluate unit from pasted text or a screenshot

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

## Numbers you find (see [ASSUMPTIONS.md](../ASSUMPTIONS.md))
- **#10 AI provider, cost and quota:** compare Gemini and Claude on the sample contracts and requests, and recommend one (due 8 Nov)

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../SURVEY.md).
