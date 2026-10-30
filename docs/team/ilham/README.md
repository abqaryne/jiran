# Ilham: AI Engineer

## You own
- **Module 6** AI assistant and contract checker (FR6.1, FR6.2, FR6.3)
- The **match reason text** for Module 3 (FR3.2)
- AI evaluation, API key handling, and the **AI Usage Log** (mandatory appendix)

## Your stories (subtasks for each are in [work-breakdown.md](../work-breakdown.md))

**How to use this section, for you and for your AI assistant.** Start from the top of "Do in this order". Before writing any code open the story's **task file** (`TASKS/<epic>/<ID>.md`) and read *Description*, *Work needed*, *Constraints*, *Acceptance*, *Not in scope* and *Open questions*. The master list with every task's milestone, status and category is [TASK.md](../../../TASK.md); the subtasks are in [work-breakdown.md](../work-breakdown.md); hours are logged in ClickUp. If a task file is wrong or out of date, fix it in the same PR and tell Ary. Decisions that are hard to reverse are in [ADR.md](../../../ADR.md).

### Do in this order (stories you lead: 8)

| # | Story | Task file | FR, priority | Due | Waits for |
|---|---|---|---|---|---|
| 1 | **E8.1** Choose and set up the AI provider | [E8.1.md](../../../TASKS/E8/E8.1.md) | P0 | 8 Nov | may start early |
| 2 | **E3.5** Plain-language AI search | [E3.5.md](../../../TASKS/E3/E3.5.md) | FR6.1, P0 | 4 Dec | may start early · E3.2 for the real filters (use mock filters first) |
| 3 | **E6.3** Match reasons | [E6.3.md](../../../TASKS/E6/E6.3.md) | FR3.2, P0 | 10 Dec | E6.2 |
| 4 | **E8.2** Sample agreements and the contract function | [E8.2.md](../../../TASKS/E8/E8.2.md) | FR6.2, P0 | 13 Dec | may start early · E8.1 |
| 5 | **E8.3** Upload and result screens | [E8.3.md](../../../TASKS/E8/E8.3.md) | FR6.2, P0 | 20 Dec | E5.2 (the file arrives in a chat) |
| 6 | **E8.4** AI labels and reporting a wrong result | [E8.4.md](../../../TASKS/E8/E8.4.md) | FR6.3, P0 | 20 Dec | E3.5, E6.3, E8.3 |
| 7 | **E8.5** AI evaluation for the report | [E8.5.md](../../../TASKS/E8/E8.5.md) | FR6.1, FR6.2, P0 | 20 Dec | E3.5, E8.3 |
| 8 | **T4.2** AI Usage Log appendix (Appendix 2(b)) | [T4.2.md](../../../TASKS/T4/T4.2.md) | P0 | 9 Jan | everyone's logs |

### Stories where you do a subtask (6)

| Story | Lead | Your subtask | Task file | Due |
|---|---|---|---|---|
| **E0.4** Project management and docs | Dafi | Create your own file in `docs/ai-log/` and start the AI Usage Log | [E0.4.md](../../../TASKS/E0/E0.4.md) | 30 Oct |
| **T1.1** Survey and interviews | Aly | Share the survey link in your circles | [T1.1.md](../../../TASKS/T1/T1.1.md) | 8 Nov (launch 31 Oct) |
| **T1.3** Verify the assumed numbers | Dafi | #10 AI provider · 8 Nov | [T1.3.md](../../../TASKS/T1/T1.3.md) | 13 Nov |
| **E0.6** Function contracts (stubs) | Aaron | Agree the shape of each AI function | [E0.6.md](../../../TASKS/E0/E0.6.md) | 15 Nov |
| **T3.1** Script and screen captures | Ary | Record your own screen capture and name the FRs you show; Technical implementation and AI data flow (1:30) | [T3.1.md](../../../TASKS/T3/T3.1.md) | 20 Dec |
| **T4.1** Report sections | Dafi | AI usage statement in 4, 6 Impact, limitations, future work (150) | [T4.1.md](../../../TASKS/T4/T4.1.md) | 9 Jan |

### Open questions waiting for you (8)

These come from the task files. Answer them in the task file (or in the PR) so the next person does not guess.

- [E0.6](../../../TASKS/E0/E0.6.md): Aaron and Ilham: agree the three AI function shapes. Blocks: E3.5, E6.3, E8.2.
- [E3.5](../../../TASKS/E3/E3.5.md): Ilham: provider choice (E8.1). Blocks: calling it.
- [E6.3](../../../TASKS/E6/E6.3.md): Ilham: prompt, provider and cost (E8.1). Blocks: the AI path.
- [E8.1](../../../TASKS/E8/E8.1.md): Ilham: a decision by 8 Nov. Blocks: E3.5, E6.3, E8.2.
- [E8.2](../../../TASKS/E8/E8.2.md): Ilham: where the sample agreements come from, and who anonymises them. Blocks: the evaluation.
- [E8.4](../../../TASKS/E8/E8.4.md): Ilham: where reports are stored and who reads them. Blocks: the button.
- [E8.5](../../../TASKS/E8/E8.5.md): Ilham and Aly: who rates the reasons and how many raters. Blocks: the rating figure.
- [T4.2](../../../TASKS/T4/T4.2.md): Everyone: fill the TODO fields before merging into the appendix.

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

## Numbers you find (see [ASSUMPTIONS.md](../../product/ASSUMPTIONS.md))
- **#10 AI provider, cost and quota:** compare Gemini and Claude on the sample contracts and requests, and recommend one (due 8 Nov)

## Rules for everyone

- Log your actual hours on your ClickUp tasks. Commits and ClickUp are the evidence for the report and the peer evaluation.
- Commit message format: `[Story] short description`, e.g. `[E7.1] Add balance calculation`. Use the story ID from the ClickUp task (the task shows the FR). Work with no story uses `[Docs]`, `[Infra]` or `[Design]`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, story task moved with hours logged in ClickUp, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../../product/PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../../product/SURVEY.md).
