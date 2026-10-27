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

