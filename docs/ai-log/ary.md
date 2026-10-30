# AI usage log: Ary

Tool: **Claude Code** (desktop app, Claude models) in the repository `abqaryne/jiran`, plus Figma and Mobbin tools called by
Claude. Period: 25 to 27 October 2026. Human owner: Ary. The entries below are a first draft written by the assistant from
the conversation; every field marked `TODO (Ary)` must be filled in by Ary before the report. The full conversation is
exported by the Claude Code app and must be kept as the complete prompt record. These are excerpts.

## 2026-10-25 · Requirements and planning documents · all modules

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** turn the project proposal and the course brief into a PRD, a team plan, survey questions, glossary, user flows and per-person task files; refine scope with a grilling round (domain questions).
- **Prompts (exact, selected):**
  > ubah proposal project jadi PRD deh
  > hapus semua konsep driver dari PRD, termasuk FR5.x, skema data, dan journey
  > niatnya juga kayak ada listing seperti propertyguru gitu, menurut kamu gimana?
  > kenapa emangnya klo scraping
  > oke sekarang kasih tau aku secara garis besar fitur fitur yang ada disini apa?
  > coba aturannya lebih kayak role di perusahaan deh…
  > coba tambahin section di TEAM_PLAN flow step by stepnya siapa yang kerja dlu atau ini bisa kerja bareng?
  > please create a .MD for each person
  > coba buat USER-FLOW.MD biar lebih kebayang deh akunya dalam bentuk flowchart bisa ga ?
  > proposal pdf abaikan aja soalnya fokus kita di PRD dan grilling udh gausah. dan buat kebenaran angka tolong kasi task itu ke role yang sesuai…
- **Output used:** `docs/product/*` (PRD, GLOSSARY, USER-FLOW, SURVEY, ASSUMPTIONS), `docs/team/*`.
- **How it was tested or checked:** documents reviewed in chat; links checked by script after the folder move. TODO (Ary): who in the team reviewed them.
- **What the human changed or rejected:** Ary removed the student-driver concept, decided scope questions (listing model, no scraping, team roles, Java stack, Node.js for Functions), dropped the proposal PDF and the CO₂ emphasis, and asked for numbers to be assigned to owners. TODO (Ary): list anything rewritten by hand.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** all figures in ASSUMPTIONS.md are guesses until their owners verify them with sources.

## 2026-10-26 · Design system and Figma screens · Lead UI/UX

- **Human owner:** Ary
- **AI tool and model:** Claude Code with the Figma and Mobbin tools
- **Task:** design system (colour, type, components), Figma variables, text styles, components and about 60 mobile screens grouped into flows.
- **Prompts (exact, selected):**
  > /design … use #065BC3 as the brand color and ignore all of the secondary/accent… Search mobbin for cash app… nextdoor for the real estate components reference
  > i dont like the design system u made shit ass UI. change the reference. Search mobbin for nextdoor… Use geist as the font and keep the current color
  > coba yang paling maksimal semibold deh jangan bold
  > coba ganti ke material symbols dlu deh aku pengen liat
  > can u please convert and start designing in figma?
  > tolong UI designny di lengkapin dari login, onboarding dan semua flownya
  > coba tambahin app bar battery dan wifi seterusnya
  > di setiap flow, coba kasih page starting pointnya biar tau ini dia masuk dari mana sebelum ke flow inti
  > kayaknya contract better gausah di munculin deh di detail listing atau apapun itu. dia bakal dikasi secara private
- **Output used:** the Jiran Figma file (variables, components, screens), `docs/design/DESIGN.md`.
- **How it was tested or checked:** screenshots of each screen row compared by the assistant; Ary reviewed in Figma and corrected direction. TODO (Ary): what you checked yourself.
- **What the human changed or rejected:** Ary rejected the first visual direction and chose Nextdoor as reference, Geist, semibold at most, Material Symbols icons, pill buttons, the tab names (Home, Rooms, Match, Unit), the private contract check, and the flow headers.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** screens use example data; the logo is a placeholder.

## 2026-10-27 · Android project structure, design system code and components · Frontend

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** project folder structure, design system resources (tokens, styles, fonts, icons), reusable component views, a debug component gallery, document and repository reorganization, PRs.
- **Prompts (exact, selected):**
  > setup project ini biar langsung rapih dari folder dan lain lainnya. layaknya best practice struktur folder aplikasi android
  > menurut kamu mending aku ngerjain frontendnya semuanya atau biarin mereka generate?
  > yaudh buat componentnya sekarang
  > namanya gausah ary-components deh components aja
  > buat docs juga tolong dirapihin deh ke folder /docs
  > tolong jangan langsung push, coba rearrange folder biar tau folder mana yang utama
- **Output used:** PRs `#4` (components) and `#5` (docs structure) merged; layout change in a local branch.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (0 lint errors); the debug APK was installed on the Pixel_9 emulator and the component gallery was scrolled and compared with Figma by the assistant; one crash found this way (MaterialSwitch vs the theme switch style) was fixed. Not run: Espresso tests, GitHub CI.
- **What the human changed or rejected:** Ary chose branch names, asked for a PR instead of a direct push, and decided to merge #4 then #5. TODO (Ary): your own review of the code.
- **What the human understands and can explain:** TODO (Ary): the MVVM split, how a component view reads its XML attributes, and how the design tokens map to Android resources.
- **Limits:** components were verified on one emulator only; known differences from Figma are listed in `docs/design/DESIGN.md`.

## 2026-10-27 · App logo and launcher icon · Frontend

- **Human owner:** Ary
- **AI tool and model:** Claude Code with the Figma tool
- **Task:** use the logo drawn in Figma as the app logo and launcher icon.
- **Prompts (exact):**
  > coba update logonya make logo yang di figma dong
- **Output used:** `drawable/ic_logo.xml`, adaptive launcher icon (`mipmap-anydpi/ic_launcher*.xml`, foreground and monochrome vectors), manifest icon, DESIGN.md section 3.5. The vector paths come straight from the SVG exported from the Figma `logo` frame (Thumbnail page).
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed; debug APK installed on the Pixel_9 emulator; the logo in the component gallery and the launcher icons in the app drawer were compared with Figma by the assistant. Not checked: other launchers and icon shapes, Android 13 themed icon on a real device.
- **What the human changed or rejected:** Ary drew the logo in Figma and chose which frame is the master. TODO (Ary): anything you changed.
- **What the human understands and can explain:** TODO (Ary): how an adaptive icon uses a background layer, a foreground layer and a safe zone.
- **Limits:** the foreground was scaled to fit the safe zone, so it is smaller than the Figma frame; the door runs off the bottom edge on purpose.

## 2026-10-27 · Rules for listing data and seed data · Module 2

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** decide whether listings may be collected from PropertyGuru or other sites for the course, and write the rule.
- **Prompts (exact):**
  > ini kan cuma buat cours aja ya klo kita kumpulin 30 sampai 50 listings dari property guru atau web di online gt boleh ga
  > ya boleh deh
- **Output used:** rule in `docs/product/ASSUMPTIONS.md`, Gathan's seed data task, PRD cold-start wording. The assistant said it is not legal advice and did not read the sites' current terms.
- **How it was tested or checked:** documents only. TODO (Ary): check the terms of the sites you use, and ask the lecturer if unsure.
- **What the human changed or rejected:** Ary accepted the "reference prices by hand plus fictional seed listings" approach in place of collecting 30 to 50 real listings.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the assistant did not verify any site's terms; fictional listings still need realistic numbers from real reference prices.

## 2026-10-27 · Dafi's shuttle task wording · Module 4

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** reword Dafi's task: find the UM shuttle timetable, email only if needed (no live tracking in the app).
- **Prompts (exact):**
  > gausah udh apus aja nanti aku suruh dafi cari tau jadwal dan mungkin baru kira kira commute nya berapa
  > ya tulis ulang
- **Output used:** `docs/team/dafi.md`, ASSUMPTIONS #5 and TEAM_PLAN wording. An earlier research note was deleted at Ary's request and never pushed.
- **How it was tested or checked:** documents only. Not checked with UM.
- **What the human changed or rejected:** Ary rejected the research note and chose to brief Dafi directly. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** none of the shuttle data was verified.

## 2026-10-27 · Project board cards document · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** write the cards to put in the project management app (Trello).
- **Prompts (exact):**
  > coba buat md untuk kartu kartunya yang ditaro di project management app deh
  > tolong dibuat task-tasknya lebih kayak giini deh soalnya agak bingungin task task yang sekarang (with a screenshot of an Initiative, Epic, Story/Task, Subtask diagram)
- **Output used:** `docs/team/trello-cards.md`, first as a flat list of cards, then restructured into Initiative, Epics, Stories and Subtasks at Ary's request because the flat list was confusing. Also corrected "18 FRs" to 19 in `docs/team/dafi.md` and `docs/team/TEAM_PLAN.md` (the PRD has 19 FRs).
- **How it was tested or checked:** cards were derived from the PRD FR table, the team plan (owners, waves, gates, video and report tables) and ASSUMPTIONS.md; link check passed. The due dates for setup cards are proposals by the assistant. TODO (Ary, Dafi): confirm owners, estimates and dates with the team.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** estimates (hours) are left empty on purpose; owners of mixed UI and backend cards are suggestions.

## 2026-10-27 · Align TEAM_PLAN and personal task files with the work breakdown · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** update TEAM_PLAN and the six personal task files so they use the same epics and stories as `docs/team/trello-cards.md`.
- **Prompts (exact):**
  > dan coba update juga secara keselurhan di docs docs yang lain related pembagian task ini di TEAM_PLAN dan .md personal masing masing
- **Output used:** `docs/team/TEAM_PLAN.md` (epic map, story IDs in the step tables), the task sections of `ary.md`, `dafi.md`, `aaron.md`, `gathan.md`, `ilham.md`, `aly.md`; two stories added to the breakdown (E0.5 data schema and rules, E0.6 function contracts); each module owner now builds their own screens with Ary's components.
- **How it was tested or checked:** documents only; story IDs and links cross-checked by script. Owners and due dates are the assistant's proposal from the existing plan. TODO (Ary, Dafi): confirm them with each person before the board is filled in.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** Aaron's file is also edited on his own branch, so a small merge conflict is likely when he merges `main`.

## 2026-10-27 · Epics reorganised as flows, in build order · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** change the work breakdown so epics are the flows of the app, done in order (E0 first, then E1 and so on), and update TEAM_PLAN and the personal files to match.
- **Prompts (exact):**
  > bisa dibuat kayak per flow aja gitu ga, E0 itu buat dikerjain awal kemudian E1 seterusnya dan seterusnya
- **Output used:** `docs/team/trello-cards.md` (11 flow epics E0 to E11 and 4 parallel tracks T1 to T4), the story tables in the six personal files, the epic table and the new Step 2 flow order in `docs/team/TEAM_PLAN.md`. The files were generated from one data list so IDs, owners and dates agree.
- **How it was tested or checked:** documents only; every story ID mentioned in the plan exists in the breakdown and links resolve. Owners and dates are the assistant's proposal. TODO (Ary, Dafi): check that building the flows in this order fits the 13 December P0 freeze, and confirm owners and dates with each person.
- **What the human changed or rejected:** Ary replaced the earlier module-based epics with flows in build order. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** Aaron's master prompt in `aaron.md` still mentions Wave A and Wave B (it is his own prompt record, so it was left as it is).

## 2026-10-27 · Remove the 20-hour minimum from the docs · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** remove the "minimum 20 hours" requirement from all team documents.
- **Prompts (exact):**
  > buat yang requirement minimal 20jam itu apus aja dari semua docs
- **Output used:** `docs/team/TEAM_PLAN.md`, the six personal files and `docs/team/trello-cards.md`. Logging actual hours on Trello cards is kept as evidence for the peer evaluation. The assistant noted that the course brief itself says each member is expected to contribute at least 20 hours; only our own documents were changed.
- **How it was tested or checked:** text search shows no remaining mention in the docs.
- **What the human changed or rejected:** Ary decided to drop the 20-hour wording from the documents.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the brief's expectation still applies to the team.


## 2026-10-27 · Commit message format uses the story ID · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** change the commit message format in the team documents from module and FR tags to the story ID.
- **Prompts (exact):**
  > commit message formatnya coba disesuain deh di folder team
  > tanpa tag fr aja deh kepanjangan klo ada fr nya
- **Output used:** `docs/team/TEAM_PLAN.md`, the six personal files, `AGENTS.md`, `README.md`, `docs/product/PRD.md`. The format is `[Story] short description`, for example `[E7.1] Add balance calculation`. Already on `main`.
- **How it was tested or checked:** text search shows no remaining `[ModN][FRN.N]` format in the docs.
- **What the human changed or rejected:** Ary dropped the FR tag because the line was too long. The FR stays on the story card.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** older commits keep the old format.

## 2026-10-27 · Review of Aaron's branch `feature/aaron-commute-core` · Quality assurance

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** check Aaron's branch: run his tests and checks and read the config changes.
- **Prompts (exact):**
  > coba cek kerjaan aaron di branch deh
- **Output used:** a review summary in chat only. Checks run in a temporary worktree: backend `npm test`, `npm run check`, `npm run test:load`; Android `lintDebug testDebugUnitTest checkstyleMain assembleDebug`. All passed. Three broken relative links were found in `docs/product/ASSUMPTIONS.md` and `docs/emission-methodology.md`; a fix commit was made locally and not pushed.
- **How it was tested or checked:** the commands above. The code itself was not read line by line. not verified: the Worker experiment, the GTFS processing logic and the emission values.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** this is not the code review that Aaron's PR needs from a teammate.

## 2026-10-27 · Switch the documented stack to free plans only · Architecture documents

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** update the documents to the free-only stack: Cloudflare Worker for server code, openrouteservice and Prasarana GTFS for routes, MapLibre map in the app, Cloudinary for room photos. No "open in Google Maps" button.
- **Prompts (exact):**
  > ya perbaiki sama buat API ya kita ini kan cuma tugas kuliah, jadi pasti yang gratis aja ya
  > masa gabisa peta dalam app sih>
  > ya tolong update dan gausah pake buka link google maps kali kan kita udh nampilin?
- **Output used:** `docs/product/PRD.md` (sections 4, 8, 9 and open questions), `docs/product/ASSUMPTIONS.md` (rows 2 to 5 and 11), `AGENTS.md`, `docs/team/TEAM_PLAN.md`, `docs/team/trello-cards.md`. Based on Aaron's research in `docs/no-billing-production.md`. Changes that depend on an unproven choice are marked **[DRAFT]**.
- **How it was tested or checked:** documents only; a text search shows no remaining Google Maps Platform, Routes API or Cloud Functions mentions in the product documents. not verified: free limits and the Worker spike (Aaron's work), the MapLibre tile terms, and the Cloudinary plan.
- **What the human changed or rejected:** Ary chose free-only and an in-app map without a Google Maps link.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the peak factor for road travel time is a placeholder idea, since openrouteservice has no live traffic. Aaron and Gathan must confirm the server, photo storage and contract-file handling. The aaron.md master prompt still mentions Cloud Functions (his file).

## 2026-10-29 · PRD data model update for the new Firestore collections · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** bring PRD section 8 and open question 5 in line with Gathan's revised schema (PR #8) and Ary's decisions, so Gathan can refer to the PRD.
- **Prompts (exact):**
  > yang koleksi baru itu gimana coba
  > oke coba update PRD dan push ke branch gathan biar dia refer ke situ
- **Output used:** `docs/product/PRD.md` section 8 (new rows for the `private` documents, `listingRequests`, `chats`, `config`; money in sen; `filePath` removed from `contractChecks`) and open question 5 marked decided. The decision to accept `listingRequests`, to merge `identity` and `preferences` into one owner-only `profile` document, and to make `membership` owner-only is Ary's, taken from the AI's recommendation.
- **How it was tested or checked:** the link to `docs/schema.md` resolves; a search finds no old field names (`photoPaths`, `filePath`, `totalRent`, `budgetMin`) left in the PRD. not verified: that `docs/schema.md` and the PRD now agree (schema.md still lists `identity` and `preferences` separately, which Gathan has to merge); no Security Rules were written or tested.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to say why each stage of sharing needs its own document (Security Rules judge a whole document) and why `listingRequests` exists (`matches` only links two students, not a student and a Host).
- **Limits:** the PRD lists key fields only; field-level detail stays in `docs/schema.md`. `reports` and `notifications` are left out of section 8.

## 2026-10-30 · Fix the Connection proof and the UM posting rule in the schema · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** correct two problems in `docs/schema.md` (PR #8) that Gathan's last revision left: the Rules lookup for `contact` and `phone` could not be written as described, and the sign-up policy changed (any email may register, UM email earns the UM verified badge).
- **Prompts (exact):**
  > oke now coba check update-an terbaru gathan
  > langsung benerin aja deh
- **Output used:** `docs/schema.md`: new collection `connections/{pairId}` (the two UIDs sorted) as the one proof of a Connection, the Rules lookup rows for `contact` and `phone` rewritten to use it, a posting rule for `listings` that needs a verified email on a UM domain, the chat retention counted from `connections.endedAt`, and a server task marked as an open question for Aaron. `docs/product/PRD.md` section 8: a `connections` row and the access rule sentence.
- **How it was tested or checked:** read against the previous diff and the PRD only. not verified: no Security Rules or emulator test exists, so it is not proven that Rules can read `connections/{pairId}` the way the document says; whether the app or the Worker creates the document is open.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to say why Rules cannot find the listing from a user's contact document and why one document per pair of people solves it.
- **Limits:** this edits Gathan's document; Gathan has to read it, agree with it and be able to explain it. His own AI log entry for his last revision is still to be written by him. `connections` is marked `[PROPOSED]` until Ary confirms it.
