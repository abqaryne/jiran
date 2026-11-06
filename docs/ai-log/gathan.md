# AI usage log: Gathan

## 2026-10-27 · Schema and Security Rules Alignment · E0.5 / Firestore Schema

- **Human owner:** Gathan
- **AI tool and model:** Gemini
- **Task:** Realigned `docs/schema.md` with backend match specification (`backend/functions/src/match.js`), standardized monetary units to integer sen, removed paid stack dependencies, fixed privacy stage subdocuments, and added Security Rules lookup mappings.
- **Prompts (exact):**
  > Revise docs/schema.md ONLY. Firestore Security Rules cannot query collections: they can only read documents at paths they can build from the request. Fix these, then list exactly what you changed: 1. users/{uid}/private/contact holds ONLY firstName... 2. listingRequests: use deterministic document ID {listingId}_{requesterUid}... 3. Add a "Rules lookup map" table... 4. Remove email and umEmail from users/{uid}/private/identity... 5. Replace Google Maps Routes API with openrouteservice and Prasarana GTFS timetables... 6. Update stage visibility matrix.
  > Please review and execute revisions for PR #8 (E0.5 Data Schema & Security Rules) to address team review feedback from Ary: Free-Only Tech Stack Alignment, Contract File Retention, Questionnaire Keys Standardization, Matches Result Shape Alignment, Currency Standardization (sen), Privacy & Stage Isolation Matrix Fixes.
- **Output used:** Updated `docs/schema.md` for PR #8.
- **How it was tested or checked:** Cross-checked schema fields against `backend/functions/src/match.js` and `docs/api-contracts.md`. Ran `analyze_file` on `docs/schema.md` to verify syntax.
- **What the human changed or rejected:** Reviewed generated field structures, added [DRAFT] tags on unconfirmed stack choices, and verified integer sen conversions across collections.
- **What the human understands and can explain:** Understands how Firestore security rules use direct path lookups (`get()`) instead of collection queries, why currency is stored in integer sen to avoid floating-point bugs, and how subcollection isolation enforces PDPA 2010 privacy across match stages.
- **Limits:** Exact Cloudflare Worker routing and Cloudinary storage endpoints remain draft choices marked `[DRAFT]`.


## 2026-11-03 · Security Rules Implementation and Schema Alignment · E0.5 / Security Rules & NFR2

- **Human owner:** Gathan
- **AI tool and model:** Gemini (Gemini 2.5 Pro)
- **Task:** Closed schema gap (gender field, timetable ISO day format 1..7, ADR-0007 Worker-created connections), implemented full Firestore Security Rules in backend/firestore.rules enforcing strict siswa.um.edu.my baseline, stage privacy, and connection lookups, and created NFR2 rules test suite.
- **Prompts (exact):**
  > Okay so, can you start working on E0.5 first. Since i already made schema.md, use that one and change it, dont create a new one. Check the readme.md first at Gathan folder, then check the E0.5 md and start working on that. Make sure to do the exact task that was given
- **Output used:** Updated docs/schema.md, backend/firestore.rules, backend/scripts/product-rules-smoke.cjs, backend/scripts/emulator-smoke.cjs, backend/scripts/firestore-rule-check.cjs, backend/functions/package.json, TASKS/E0/E0.5.md.
- **How it was tested or checked:** Ran Android checks (lintDebug, testDebugUnitTest, checkstyleMain, assembleDebug all passed). Ran backend checks (npm test passed with 13 test suites / 339 tests, npm run test:load, npm run check).
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Local emulator run for product-rules-smoke requires running Firebase emulator (auth/firestore); in CI it runs automatically via emulator-smoke.cjs in firebase emulators:exec.


## 2026-11-06 · Address Ary PR Review on Privacy Rules · E0.5 / Security Rules & NFR2

- **Human owner:** Gathan
- **AI tool and model:** Antigravity (Gemini 2.5 Pro)
- **Task:** Fixed privacy loopholes identified in Ary's PR #8 review: prevented listingRequests self-acceptance by validating hostUid against listing ownerUid and ensuring hostUid != requesterUid; prevented stranger chat creation by requiring active connections (areParticipantsConnected helper); locked listing ownerUid against modification on update; updated schema documentation, task tracking status, and smoke tests.
- **Prompts (exact):**
  > Thanks Gathan, the rules cover every collection and the CI smoke test passes (25 denials, 27 permissions). Two privacy gaps need closing before this merges, because the PRD promises that the address and contact stay hidden until a Connection exists.
  > 1. A requester can accept their own request and read the exact address (backend/firestore.rules, listingRequests create, line 171)
  > 2. Anyone can open a chat with any student without a Connection (chats create, line 300)
  > Smaller items, not blocking:
  > The TASK.md row for E0.5 is not updated.
  > The Resolution heading in TASKS/E0/E0.5.md still says 30 Oct.
  > The listings update rule does not stop the owner from changing ownerUid.
  > Still waiting for Aaron's review.
- **Output used:** Updated backend/firestore.rules, backend/scripts/product-rules-smoke.cjs, docs/schema.md, TASK.md, TASKS/E0/E0.5.md, docs/ai-log/gathan.md.
- **How it was tested or checked:** Backend unit and contract tests (npm test, npm run check). Android local checks (lintDebug, testDebugUnitTest, checkstyleMain, assembleDebug).
- **What the human changed or rejected:** TODO (Gathan)
- **What the human understands and can explain:** TODO (Gathan)
- **Limits:** Live CI emulator execution runs on GitHub Actions pull request check Jiran checks / backend (pull_request).
