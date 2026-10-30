# Jiran agent instructions

These rules apply to every AI assistant (Claude Code, Codex, ChatGPT, Gemini, Copilot, ...) working in this repository.
Jiran is a WIA2007 Mobile Application Development group project for Universiti Malaya (UM). The course brief sets
rules that the code, the documents and the way we use AI must follow.

## 1. AI Usage Log: write it as you work (mandatory)

The brief puts this course under UM's **Level 2 AI policy (limited usage, specific tasks only)**. An accurate **AI Usage
Log** is a required appendix of the report. Missing or inaccurate logs, or work that is "predominantly AI-generated with
minimal or no human effort", can lose marks, lower the grade or fail the course.

- **Before you finish a task, add or update the entry** in `docs/ai-log/<owner>.md` (one file per person). Do it in the same PR as the work. Do not wait to be asked.
- Use the template in [docs/ai-log/README.md](docs/ai-log/README.md): date, human owner, tool and model, task, **exact
  prompts**, output used (files, PR), how it was tested or checked, what the human changed, what the human can explain,
  limits.
- **Never invent.** If something was not recorded or not done, write `not recorded` or `not verified`. Never claim a
  human reviewed, understood or tested something unless that is true. Do not guess hours (they go on the ClickUp task).
- Leave the "what the human changed" and "what the human understands" fields as `TODO (owner)` for the human to fill in.
- Only append to your owner's file. Do not edit or remove other people's entries.

## 2. Human contribution comes first

The brief requires **significant and original human contribution** and a real understanding of the code. Therefore:

- Do not write the whole feature silently. For core logic (match score formula, room price split, balances, commute
  calculation, Security Rules, AI evaluation) explain the design, write small pieces, add unit tests, and tell the owner
  what they must read, run and be able to explain. Mark the output "needs human review".
- Prefer help that teaches and checks (explain, review, find bugs, suggest tests, refine wording) over bulk generation.
- Never present generated code, architecture or analysis as the owner's original work. The owner states in the report what
  the team verified, tested and understood.
- Run the checks before saying a task is done, and report failures with their output: `android/`:
  `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug`; `backend/functions`: `npm test`.

## 3. What the brief requires of the product

- The app must be **applicable to UM** and solve a real UM problem. Every screen, requirement and example must trace
  back to [docs/product/PRD.md](docs/product/PRD.md), the single source of truth. Do not change scope without the owner.
- The SDG and target are chosen in the PRD (SDG 11, with 11.1 as the main target). The brief lists SDG 6 and 13
  in its video section; if the team's choice is questioned, flag it, do not silently change it.
- The report needs **at least five modules with three functional requirements each**, up to two non-functional
  requirements, and every demonstrated feature is linked to its FR. Name each change after its requirement:
  commit messages use `[Story] short description` (for example `[E7.1] Add balance calculation`; the story task holds the FR, see docs/team/TEAM_PLAN.md), branches use `feature/<short-name>`.
- Non-functional targets that must be tested: a page loads within 3 seconds with 50 concurrent users, and the system keeps
  working with up to 100 (see `backend/tests/load`).
- **AI features must earn their place**: AI is allowed in the product only where rules cannot do the job (plain-language
  search, contract reading, match explanations). Each one needs a justification, a label **AI-generated**, graceful
  fallback when it fails, and an evaluation on sample data (50 search requests, 10 contracts, user ratings for reasons).
  The match score and CO₂ are computed by code, never by the AI. All AI and routing calls run on the server (a Cloudflare Worker, free
  plan only); keys never ship in the app.
- Use the terms in [docs/product/GLOSSARY.md](docs/product/GLOSSARY.md). UI follows
  [docs/design/DESIGN.md](docs/design/DESIGN.md): only design system tokens and styles, 48 dp touch targets, status with a
  word and an icon, estimates labelled as estimates.
- Privacy by default (PDPA 2010): no real personal data in examples or tests, the contract check is private, addresses and
  phones are hidden until a Connection exists.
- Never commit API keys, `google-services.json`, service accounts or keystores. Do not scrape other sites.
- Do not invent numbers (fares, prices, emission factors, survey results). Use the values in
  [docs/product/ASSUMPTIONS.md](docs/product/ASSUMPTIONS.md) and label them as estimates until their owner verifies them.

## 4. Workflow

- Work on a feature branch and open a pull request. **Do not push, merge or force-push unless the human asks.** Ary does
  the final check and merge; Aaron reviews backend and Maps, Gathan reviews schema and rules.
- Team decision: commits show only team members. Do **not** add `Co-Authored-By` lines for AI tools; AI involvement is
  recorded in the AI Usage Log instead.
- Documents for the report must stay short (report limit: 15 pages and 2,500 words, so use tables). Keep the PRD as the
  single source of truth and link to it instead of copying it.

### Task tracking

- Every member has an owner file at `docs/team/<name>/README.md` (role, stories in the order to do them, open questions). Find the
  file of the person you work for and read it first, then the task file of the story. Personal notes go in `docs/team/<name>/`.
- `TASK.md` is the master list, `TASKS/<epic>/<ID>.md` holds the detail of each task, `ADR.md` records decisions that are hard to
  reverse (append-only). Read the task file of your story before you code; if something in it is wrong or out of date, fix it in the same PR and tell Ary.
- When you finish or change a task, update its file (status, resolution) and its row in `TASK.md` in the same PR. The status follows
  what is in the repository (rules at the top of `TASK.md`), not ClickUp; hours are logged in ClickUp.
- Never invent task detail: write `not recorded` or `not verified`, like in the AI log.

## 5. Firebase and backend

For every Firebase task, first find and use the appropriate official Firebase
agent skills from https://github.com/firebase/agent-skills. Read `firebase-basics`
for app registration/configuration, `firebase-auth-basics` for authentication,
`firebase-firestore` for database/SDK work, and `firestore-rules-creation` before
authoring rules. Use Firebase MCP tools where available and the official CLI.

Read `docs/team/aaron/README.md` and preserve the Java Android / JavaScript Functions stack.
The existing live project is `jiran-mad`; its Android app is named Jiran with
application ID `jiran.com`. Do not create duplicate apps/projects. Use explicit
`--project jiran-mad` for authorized live operations. Default local development
uses `demo-jiran`. Never commit Firebase configuration keys or service accounts.

Budget is free-only (user confirmed 27 October 2026). Do not enable billing,
upgrade plans, or activate billable providers. Research a no-billing production
alternative retaining Firebase Auth and Firestore; preserve the Java/JavaScript
code while documenting proposed runtime/provider changes before adopting them.

Repository layout: `android/` is the Gradle project (run `./gradlew` there), `backend/` holds Firebase config,
`backend/functions` and the emulator scripts (run `firebase` and `node scripts/...` there), `docs/` holds all documents.
