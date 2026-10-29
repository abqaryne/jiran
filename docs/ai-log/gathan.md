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
