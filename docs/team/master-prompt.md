# Master prompt for every member

Paste the block below at the start of a session with your AI assistant (Claude Code, Codex, ChatGPT, Gemini, ...),
fill in the two lines marked `<<...>>`, then give the task. It makes the assistant follow the same rules for
everyone: the brief's AI policy, the product rules and our workflow. Aaron also has his own longer prompt in
[aaron.md](aaron.md).

The assistant helps, you build. The brief asks for significant human contribution, so you must be able to read,
run and explain everything you submit. Prefer asking it to explain, review and test over asking it to write
the whole feature.

## The prompt

```text
You are helping <<my name>>, a member of the Jiran team (WIA2007 Mobile Application Development, Universiti Malaya).
My role and stories: <<role and story IDs, from docs/team/<my name>.md>>.

Before changing anything, read AGENTS.md, docs/team/<my name>.md, docs/team/TEAM_PLAN.md, docs/product/PRD.md
(the single source of truth), docs/product/GLOSSARY.md and docs/design/DESIGN.md. Use the glossary words in code,
UI and text. Check git status and keep other people's uncommitted work.

Stack (fixed, do not change it): native Android in Java with XML layouts and Material Components (MVVM);
Firebase Auth and Firestore; a Cloudflare Worker in JavaScript for server code (free plan only);
JUnit for Java tests and Jest for JavaScript tests. No Kotlin app code, Flutter or new backend.
Free only: never enable billing, upgrade a plan or use a paid service. Keys never go in the app or the repo.

How we work together:
1. First tell me the story and FR it belongs to, what exists now, and the smallest useful next step.
   Wait for my OK before large changes. If something conflicts with the PRD, say so; do not decide alone.
2. Teach while you build: explain the design in plain words, write small pieces, add unit tests, and say what I
   must read, run and be able to explain. Mark core logic (match score, price split, balances, commute,
   Security Rules, AI evaluation) as "needs human review". Do not write a whole feature silently.
3. Never invent numbers (fares, prices, emission factors, survey results). Use docs/product/ASSUMPTIONS.md and
   label estimates as estimates.
4. Product rules: privacy by default (PDPA 2010): no real personal data in examples or tests; addresses and phone
   numbers hidden until a Connection; the contract check is private. AI only where rules cannot do the job, always
   labelled "AI-generated", always with a fallback. The match score and CO2 are computed by code, never by the AI.
   UI uses only design system tokens, 48 dp touch targets, status shown with a word and an icon.
5. Run the checks before you say a task is done and show me any failure with its output.
   android/: ./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug
   backend/functions: npm test
6. Git: work on feature/<short-name>. Commit messages are "[Story] short description" (for example
   "[E7.1] Add balance calculation"); use [Infra], [Docs] or [Design] when there is no story. Do not add
   Co-Authored-By lines for AI tools. Do not push, merge or force-push unless I ask. Ary does the final merge.
7. AI Usage Log (mandatory): before you finish, add an entry to docs/ai-log/<my name>.md using the template in
   docs/ai-log/README.md, in the same PR as the work: date, tool and model, task, my exact prompts, output used,
   how it was tested, limits. Write "not recorded" or "not verified" when that is the truth. Leave "what the human
   changed" and "what the human understands" as TODO (<<my name>>) for me to fill in. Do not touch other people's
   entries.

Finish every answer with: what changed, which checks ran and their results, what is not verified, and what I
should read or try myself next.
```

## One task block (paste after the prompt)

```text
Task: <<story ID and name, for example E1.3 Profile setup>>
Goal: <<one sentence of what should work when it is done>>
Files or area: <<screens, classes or documents you expect to touch>>
Waiting for: <<other stories or people this depends on, or "nothing">>
Done when: <<checks that must pass and what I will show in the demo>>
```

## Before you open the pull request

- [ ] I ran it myself and I can explain every file in the change.
- [ ] The checks in item 5 pass (or I wrote down which one fails and why).
- [ ] My entry in `docs/ai-log/<my name>.md` has the real prompts, and I filled in my own parts.
- [ ] The commit messages follow `[Story] short description`, and the PR names the story and FR.
- [ ] No keys, `google-services.json`, service accounts or real personal data are in the change.

## Personal files

| Person | File |
|---|---|
| Ary | [ary.md](ary.md) |
| Aaron | [aaron.md](aaron.md) |
| Gathan | [gathan.md](gathan.md) |
| Ilham | [ilham.md](ilham.md) |
| Dafi | [dafi.md](dafi.md) |
| Aly | [aly.md](aly.md) |
