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

## 2026-10-27 · E1.1 Sign up with a UM email: Welcome, Sign up, Verify email · Module 1 (FR1.1)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** build the first part of story E1.1: the UM email rule and form check in plain Java with unit tests, the two view models, and the three screens (Welcome, Sign up, Verify email) from the Figma Starting Flow, connected to the existing Firebase email and password repository.
- **Prompts (exact):**
  > yang bisa aku lakuin sekarang E1 ya?
  > lanjut task kita aja yang E1
- **Output used:** `domain/auth/UmEmailPolicy.java` and `SignUpCheck.java` (the rules); `data/AccountService.java` and `FirebaseAccountService.java`; `ui/auth/` (`AuthActivity`, `WelcomeFragment`, `SignUpFragment`, `VerifyEmailFragment`, `SignUpViewModel`, `VerifyEmailViewModel`, `AuthViewModelFactory`); layouts, `nav_auth.xml`, `strings_auth.xml`; a debug button "Sign up screens (Module 1)" on the developer screen. Tests: 11 for the rules, 12 for the view models.
- **How it was tested or checked:** `lintDebug testDebugUnitTest checkstyleMain assembleDebug` pass (47 unit tests). Run on the Pixel_9 emulator against the local Firebase Auth emulator: empty and wrong-domain forms show errors under the fields, a valid sign-up reaches Verify email with the resend countdown, opening the emulator's verification link and returning to the app opens the product shell, and signing up with the same email again shows the "already has an account" message. not verified: a real device, a real email inbox, the live `jiran-mad` project, screen rotation, TalkBack.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Read these first: `UmEmailPolicy` (why the domain must match exactly and why two `@` signs are rejected), `SignUpCheck` (one problem per field), and how `SignUpViewModel` decides when to call Firebase.
- **Limits:** the allowed domains (`siswa.um.edu.my` and `um.edu.my`) follow the Figma design and are still a team decision (story E1.1, with Dafi). The Figma tip said the link works for 24 hours; that was removed because it is not verified. Log in is a placeholder message until E1.2. After verification the product shell opens as a stand-in until profile setup (E1.3) exists. "Change email" signs the unverified account out but does not delete it from Firebase. The full name is stored as the Firebase display name only; the `users` profile document waits for Gathan's schema.

## 2026-10-27 · E1.2 Log in, reset password, session routing, Log out · Module 1 (FR1.2)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** build story E1.2 (Log in and Reset password screens, session handling, log out), make Welcome the first screen through a launcher that reads the saved session, and remove the "Examples only" label from Welcome.
- **Prompts (exact):**
  > ini ui nya emg belum diupdate ya buat login?
  > sekaligus dan apus label examples only di welcome
- **Output used:** `domain/auth/LogInCheck.java`; `data/AccountService` and `FirebaseAccountService` (sign in, password reset, session state); `ui/auth/` (`LogInFragment`, `ResetPasswordFragment`, `LogInViewModel`, `ResetPasswordViewModel`, `StartRoute`, `LauncherActivity`); layouts, `nav_auth.xml` and strings; a temporary Log out button on the Home tab; a debug-only "Jiran dev" launcher icon for the developer screen. Tests: 16 new (63 in total).
- **How it was tested or checked:** `lintDebug testDebugUnitTest checkstyleMain assembleDebug` pass. On the Pixel_9 emulator with the local Firebase Auth emulator: a wrong login shows one message for both a wrong email and a wrong password; Reset password shows the same "if this email has an account" message for an unknown address; sign up, opening the verification link and returning opens the product; Log out returns to Welcome; logging in again with the verified account opens the product; after killing the app, an unverified account reopens on Verify email; logging in with an unverified account goes to Verify email. not verified: a real device, a real inbox, the live project, rotation, TalkBack, and the "too many attempts" message from Firebase.
- **What the human changed or rejected:** Ary asked to remove the "Examples only" label. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Read first: `FirebaseAccountService.sendPasswordReset` (why an unknown address counts as success), `StartRoute` (who may reach which screen), and the sign-in error mapping.
- **Limits:** the "link works for 1 hour" text from Figma was left out because it is not verified. Log out sits on the Home placeholder until Profile and settings (E10.1). The product shell opens after verification as a stand-in for profile setup (E1.3). Aaron's earlier string `auth_reset_sent` existed, so the new text is named `auth_reset_link_sent`.

## 2026-10-28 · Move the work breakdown from Trello to ClickUp · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** change the team documents from Trello to ClickUp, and write the status list and the ClickUp setup (Space, Lists, tasks, subtasks).
- **Prompts (exact):**
  > oke aku mau nanya berarti di clickup itu status di boardnya apa aj ya?
  > pakai clikcup dan update ya di branch yang sekarang aja ya biar barengan
- **Output used:** `docs/team/work-breakdown.md` (renamed from `trello-cards.md`; new section 2 with the statuses Backlog, This week, In progress, Blocked, In review, QA, Done), and the Trello wording changed to ClickUp in `TEAM_PLAN.md`, the personal files, `AGENTS.md`, `README.md`, `docs/README.md`, `docs/product/PRD.md` and the pull request template. Not changed: the history in `docs/ai-log/` and Aaron's own records (`docs/aaron-*.md`, `docs/ai-usage-log.md`).
- **How it was tested or checked:** text search shows no Trello mention left outside the records above; all relative links in `docs/` resolve. not verified: the ClickUp Space itself, which Dafi has to create; the status types and field names were written from general knowledge of ClickUp and should be checked in the real workspace.
- **What the human changed or rejected:** Ary chose ClickUp and asked for the change on this branch. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the statuses are a proposal, not agreed with Dafi yet.

## 2026-10-29 · Review of Gathan's schema PR (#8) · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** read `docs/schema.md` from PR #8 against the PRD, ASSUMPTIONS, `docs/api-contracts.md`, `match.js` and `firestore.rules`, and draft review comments.
- **Prompts (exact):**
  > dna coba cek pr gathan
  > yaudh deh tulis comment di pr review biar nanti gathan yang benerin
  > ok apus aja deh comment an aku yang review itu dan kirim ke chat sini biar nanti aku private chat si gathan
- **Output used:** a review comment posted on PR #8 under Ary's account, then hidden as "outdated" (GitHub does not allow deleting a submitted review); the same text was given to Ary to send to Gathan in a private chat. No repository file changed.
- **How it was tested or checked:** each finding was compared with the named file (field names against `match.js` and `api-contracts.md`, access rules against `firestore.rules`, retention periods against ASSUMPTIONS #8). not verified: the findings were not independently re-checked by a human; whether Aaron agrees with the sen proposal.
- **What the human changed or rejected:** Ary chose to hide the posted comment and send it privately. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain finding 5 (money in sen) and finding 6 (Rules apply per document, so contact data must be split).
- **Limits:** the review is an AI draft and may miss issues; the sen proposal is a team decision, not agreed.

## 2026-10-29 · Shared run configuration for LauncherActivity · Tooling (E1.2)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** make Run in Android Studio open `LauncherActivity` for everyone, because the debug build has three launcher activities and the default picks the design system gallery.
- **Prompts (exact):**
  > pas aku buka Jiran kok malah kayak design system ya di android studio aku
  > ok yaudh tambahin aja configurationnya biar semua orang otomatis run LauncherActivity
- **Output used:** `android/.run/app.xml`, copied from the run configuration Ary already set by hand in Android Studio (`specific_activity`, `com.jiran.app.ui.auth.LauncherActivity`).
- **How it was tested or checked:** the file is valid XML and is not git-ignored. not verified: that Android Studio loads the shared file on another machine, and that it replaces the automatically created `app` configuration instead of adding a second one.
- **What the human changed or rejected:** Ary set the Specified Activity in Android Studio first and asked for it to be shared. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Know why the default picks the gallery (the first launcher activity in the merged debug manifest).
- **Limits:** the gallery ("Jiran UI") and developer ("Jiran dev") icons still exist in debug builds by design.

## 2026-10-29 · Shared master prompt for team members · Project tooling

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** write one general prompt every member can paste into their AI assistant, so each session follows the AI policy, product rules and workflow of the repository.
- **Prompts (exact):**
  > tolong buatin general master prompt untuk setiap member biar bisa langsung enak ngepromptnya dan ngerjain tugasnya
- **Output used:** `docs/team/master-prompt.md` (the prompt, a one-task block, a pre-PR checklist); a link to it in `docs/team/TEAM_PLAN.md`; in `docs/ai-log/README.md` the old "Trello card" wording changed to "ClickUp task". The prompt follows `AGENTS.md`; Aaron's own prompt in `docs/team/aaron.md` is untouched.
- **How it was tested or checked:** the files it names exist and the links resolve. not verified: that Claude Code, Codex, ChatGPT and Gemini each follow every rule; nobody on the team has used the prompt yet.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to say why the prompt asks the assistant to teach and to leave the human fields in the AI log empty.
- **Limits:** the prompt repeats rules from `AGENTS.md`; if `AGENTS.md` changes, update both.

## 2026-10-29 · Open sign-up to any email, UM verified badge for posting · Module 1 (FR1.1)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** change the sign-up rule from "UM email only" to "any email may register; a UM email earns the UM verified badge, needed to post a Listing", in code, tests and documents, in the same pull request as E1.1 and E1.2.
- **Prompts (exact):**
  > kebijakan domain email um ini maksudnya gimana
  > kayaknya email apa aja boleh deh biar semua orang bisa daftar
  > kan bisa aja anak tahun pertama itu belum punya um mail jadi kayaknya mending dibuka buat semua nya dh
  > oke deh setuju ketika mau masang listing harus ada badge gitu tapi disatuin aja di PR #9 ya
- **Output used:** `SignUpCheck` no longer rejects non-UM domains (the `NOT_UM_EMAIL` problem was removed); `UmEmailPolicy.isUmEmail` stays and is the check for the badge; sign-up, log-in and reset texts no longer say "UM email", and the sign-up email field has a helper line about the badge; tests changed (`SignUpCheckTest`, `SignUpViewModelTest`, one new test). Documents: PRD (FR1.1 and its criteria, goals, Host rules, NFR2, risks, open question 9), `GLOSSARY.md` (new term UM verified), `USER-FLOW.md`, `docs/team/` (E1.1 title and wait removed, E2.1 gets a subtask for the badge check, Gathan's privacy checklist).
- **How it was tested or checked:** `lintDebug testDebugUnitTest checkstyleMain assembleDebug` pass (64 unit tests, 0 failures). not verified: the new sign-up text and helper line on a device or emulator; nothing yet shows the badge in the app (no profile screen exists) and nothing yet blocks posting (E2.1 is Gathan's); Security Rules for the badge are not written.
- **What the human changed or rejected:** Ary decided to open sign-up so first-year students without a UM email can join, and chose to require the badge only for posting a Listing. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why the badge is checked in Security Rules and not only on the screen, and what risk opening sign-up adds (people outside UM can search and connect).
- **Limits:** which UM domains count is still a draft; Gathan must change his rule text (his `schema.md` says Rules verify the UM domain for identity) and Dafi must hear about the policy change.

## 2026-10-30 · Back to UM-only sign-up · Module 1 (FR1.1)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** undo the "any email may register" change of the previous entry, because Ary decided that only UM emails should sign up, so that every account is a verified UM student and scams are harder.
- **Prompts (exact):**
  > tapi kayaknya better UM only ya biar terpercaya bahwa ini khusus pelajar UM doang, kan takut ada penipu atau apa klo semuanya boleh
- **Output used:** a revert of the previous commit (code, strings, tests, PRD, glossary, user flow, team files), keeping the earlier AI log entry as the record of what happened; PRD open question 9 now records the UM-only decision; `work-breakdown.md` and `ary.md` mark E1.1 as waiting for the UM domain list.
- **How it was tested or checked:** see the checks run before the push (listed in the pull request). not verified: the sign-up screen on an emulator after the revert.
- **What the human changed or rejected:** Ary reversed the earlier decision for trust and scam reasons. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say what each rule gives up: UM-only keeps accounts trusted but first-year students without a UM email cannot join yet.
- **Limits:** the first-year problem is not solved, only recorded as an open question. The two entries on 2026-10-29 and 2026-10-30 describe a decision made and then undone; both stay in the log because both happened.

## 2026-10-30 · Write up finished work on Ary's tasks · Module 1 (E1.1, E1.2)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** record which subtasks of E1.1 and E1.2 are finished, in `work-breakdown.md`, and draft the text Ary pastes into the ClickUp tasks.
- **Prompts (exact):**
  > oke coba tulis task yang udh kita kerjain buat task aku
- **Output used:** `(done)` marks on the finished subtasks of E1.1 and E1.2 in `docs/team/work-breakdown.md`; the ClickUp text was given in the chat and is not stored in the repository. Later in the same session, through the ClickUp connector and with Ary's request ("ya tolong update semua task saya (cuma task saya)"), Claude set E1.1 and E1.2 to *in review*, set the finished subtasks of E1.1, E1.2 and E0.3 to *done* (the subtask "unverified account can only reach the verify screen" to *in review*, waiting for Aaron), and wrote one comment on each of the three stories. Tasks that were not Ary's, and Ary's other tasks, were not changed. After Ary asked whether E0 and E1 were all done, Claude read the code again and also set both subtasks of E1.4 to *done* and E1.4 to *in review* (shell opens after sign-in, four placeholder tabs and a Post button stub exist); E1.3 (the `ui/profile` package is empty) and two E0.3 subtasks were left open. Claude's earlier statement that E1.4 was not started was wrong. Ary then said the last two E0.3 subtasks were done; Claude set them to *done* and E0.3 to *qa* on Ary's word (not verified in the repository: that the component list was announced, and that an error-state component exists). Last, at Ary's request ("ubah di pr ini dan push semua commit ya"), Claude changed `UmEmailPolicy.ALLOWED_DOMAINS` to `siswa.um.edu.my` only (staff mail `um.edu.my` is no longer accepted, because Jiran is for students), updated `UmEmailPolicyTest`, `SignUpCheckTest`, the two sign-up texts, PRD open question 9 and the E1.1 note in `work-breakdown.md`. The schema note on Gathan's branch (PR #8) still has to follow.
- **How it was tested or checked:** the marks were compared with the code in `android/app/src/main/java/com/jiran/app/ui/auth` and `domain/auth` and with the test classes beside them. not verified: the screens on an emulator after the UM-only revert; QA by Aly has not happened.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** "done" here means code and unit tests exist; the stories are not Done by the Definition of Done until Ary merges the PR, Aly passes QA and hours are logged in ClickUp.

## 2026-10-30 · Rework the task breakdown in the style of a reference project · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** make the tasks more detailed. Read how a reference repository writes a task (`TASK.md` master table and `TASKS/<ID>.md` files with header, why, what already exists, numbered deliverables with a done mark, deliberate omissions with reasons, decisions with superseded entries kept, open questions), copy that structure for Jiran, write the first five task files and update the ClickUp tasks.
- **Prompts (exact):**
  > oke kayaknya ini perlu dirombak deh clickup dan task breakdownnya, karena kurang detail, coba liat repo [nama repo dihapus] di github dan ikutin cara dia detailing task-tasknya dan tolong update rombak di clickup juga ya
  (At Ary's request on 2026-10-30 the name of the reference repository was removed from this prompt and from the commit messages; the rest of the prompt is as typed.)
- **Output used:** `docs/tasks/README.md` (master table of the 62 stories, generated from `work-breakdown.md`), `docs/tasks/_TEMPLATE.md`, and task files for E0.3, E1.1, E1.2, E1.3, E1.4; "Detail" links and an explanation in `docs/team/work-breakdown.md`. In ClickUp (through the connector, on Ary's request): the descriptions of the same five stories were replaced with the text of the task files, E1.3 moved from In progress to Blocked with a comment, because no code exists and it waits for the `users` fields of E0.5. The other stories were not changed.
- **How it was tested or checked:** relative links in `docs/tasks/` and `work-breakdown.md` all resolve. The facts in E0.3 to E1.4 come from the code, tests, PRD, the earlier entries of this log and the schema draft on PR #8; items that were not seen are marked "not verified". not verified: that the acceptance checks pass (nobody ran them), and the descriptions as shown in ClickUp.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to say why status is kept only in ClickUp and why a task file records what is left out and what is not verified.
- **Follow-up the same day** (prompt: `langsung buatin aja buat semua subtask`): Claude wrote draft task files for the other 57 stories (generated from `work-breakdown.md` plus text written from the PRD, the code and the schema draft, marked "draft, owner to confirm") and put a short version in 51 ClickUp descriptions. Six ClickUp updates (T2.4, T2.5, T3.1, T3.2, T4.1, T4.2) were refused because the daily limit of the ClickUp connector (100 calls) was reached; they are still the old one-line descriptions.
- **Limits:** the first five task files are detailed; the other 57 are drafts written without the owners and may contain wrong guesses (each lists open questions). The gender idea in E1.3 is written as a proposal, not a decision. The acceptance checks are drafts for Aly to run and correct.

## 2026-10-30 · Group the task files by epic and drop the template · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** delete `docs/tasks/_TEMPLATE.md` and move each task file into a folder named after its epic.
- **Prompts (exact):**
  > hapus template.md dan buat tambahin parent folder epic untuk masing masing subtasknya
- **Output used:** `docs/tasks/E0/` to `E10/` and `T1/` to `T4/` hold the 62 task files; links in `docs/tasks/README.md`, `docs/team/work-breakdown.md`, `docs/team/master-prompt.md` and inside the task files were updated. The earlier entry above still names `_TEMPLATE.md` and the old flat paths, because it describes what was true then.
- **How it was tested or checked:** a script checked that every relative link in `docs/tasks/`, `work-breakdown.md` and `master-prompt.md` resolves. not verified: the paths written in the ClickUp descriptions (`docs/tasks/E1.1.md`) are now out of date; they could not be updated because the ClickUp connector's daily limit was reached.
- **What the human changed or rejected:** Ary asked to remove the template; TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** until the ClickUp descriptions are updated, they point to the old flat paths.

## 2026-10-30 · Personal task files point to the task files · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** rewrite the "Your stories" section of each personal file in `docs/team/` so that a member and their AI assistant see the order of work, the task file of every story, their own subtasks in other people's stories, and the open questions waiting for them.
- **Prompts (exact):**
  > coba sekalian update md masing masing ya sesuai yang baru biar lebih enak dibacanya dan juga biar ai mereka langsung paham task apa aj ayng harus dikerjain dan jg biar refer ke folder terbaru yang tadi kamu buat
- **Output used:** the section was regenerated by a script (kept outside the repository) from `work-breakdown.md` and the task files, in `ary.md`, `aaron.md`, `dafi.md`, `gathan.md`, `ilham.md` and `aly.md`; a one-line pointer in `docs/README.md` and `TEAM_PLAN.md`. The prose that followed each old table (for example Aaron's note on Cloud Functions) was kept. The other parts of the personal files, including Aaron's long setup notes, were not changed.
- **How it was tested or checked:** every relative link in `docs/team/` resolves; counts match the old files (18, 7, 8, 12, 8 and 9 stories led). not verified: that the open questions assigned by name are the right ones; they were matched by a name at the start of each question in the task files, so a question that names two people appears in both files.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the sections are a snapshot of 30 Oct 2026. When a story changes owner or a task file changes, update the personal file by hand.

## 2026-10-30 · Manage tasks like a reference project: TASK.md, TASKS/, ADR.md · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** restructure the task management in the style of a second reference repository (a master `TASK.md` with a Milestones table and a Tasks table with status and category, one `TASKS/<ID>.md` per task with Description, Work needed, Constraints, Resolution and Notes, and an append-only `ADR.md`), and give every task more detail and context.
- **Prompts (exact):**
  > Ulang deh restruktur samain management tasknya kayak project itu ya dan tasknya juga kurang detail dan context buat dipahami coba di benerin
  
  Ary answered two questions: keep the IDs `E1.1`/`T3.1` (epics and tracks become the Milestone column), and put `TASK.md` at the root with the task files still grouped by epic folder.
- **Output used:** root `TASK.md` (14 milestones, 62 tasks with status and category), `TASKS/<epic>/<ID>.md` for all 62 tasks (moved from `docs/tasks/`, rewritten with Description, Work needed, Constraints, Acceptance, Not in scope, Resolution or Where it stands, Notes), root `ADR.md` with eight entries, a "Task tracking" rule in `AGENTS.md`, and updated links in `docs/team/`, `docs/README.md` and the personal files. The extra detail comes from `docs/schema.md` (PR #8), `docs/api-contracts.md`, the PRD, `ASSUMPTIONS.md`, `USER-FLOW.md` and Aaron's merged backend code; for the 57 tasks owned by others it is a draft marked "owner to confirm". Statuses are the ClickUp statuses read earlier on 30 Oct 2026.
- **How it was tested or checked:** a script checked 75 markdown files and found no broken relative link. not verified: the statuses in `TASK.md` against the live board today; the ClickUp descriptions still point to the old `docs/tasks/` path (the connector's daily limit was reached); every ADR date and "decided by" except the ones Ary stated in this conversation (UM-only, free-only, UM domain) comes from commit messages and documents, so Ary should confirm them.
- **What the human changed or rejected:** Ary chose the ID scheme and the layout. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why status is kept in both `TASK.md` and ClickUp and what makes an ADR different from a task.
- **Limits:** the draft task files may contain wrong guesses about other members' work; ADR-0007 is only proposed.
- **Follow-up the same day** (prompt: `udh langsung tulisin detail semua task ya`): Claude added more context to the thinnest task files (test and report tracks, Unit tab, notifications, AI labels) and wrote real *Resolution* and *Files and tests* sections for the tasks that already have code (E0.1, E0.2, E0.6, E3.3, E4.2 Aaron's part, E6.2), from the commit messages, the source files in `backend/functions/src` and the tests. `npm test` in `backend/functions` was run: 13 suites, 339 tests passed. The Resolution texts about other members' work were not read by those members. Two gaps found while writing them: the repository has no openrouteservice adapter (the provider adapter `routes-client.js` is the Google Routes one), and `backend/tests/load` already has k6 scripts but no recorded result.

## 2026-10-30 · Task files are final, draft markers removed · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** remove the "draft, owner to confirm" markers from the 62 task files, `TASK.md`, `work-breakdown.md`, the personal files, `AGENTS.md` and `ADR.md`, because Ary decided the task files are final and need no further correction round.
- **Prompts (exact):**
  > gausah pake tetep draf, udh langsung finalized aj gaperlu dikoreksi lagi biar sejalan
- **Output used:** the banner and the "(draft)" tags are gone; each task file keeps its Open questions with an owner and its "not recorded" or "not verified" marks.
- **How it was tested or checked:** a text search finds no draft marker left in those files; a script found no broken relative link. not verified: that the other members have read their task files; the text about their work was written by Claude from the repository and Ary has not asked them to confirm it.
- **What the human changed or rejected:** Ary decided to finalize without a correction round. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say that the task files describe plans and open questions, and that statements about other members' work come from the repository, not from the members.
- **Limits:** if a member finds an error in their task file, fix the file and `TASK.md` in the same PR.

## 2026-10-30 · Tidy the docs folder · Documentation

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** `docs/` had 13 files in its root, most of them Aaron's notes, and a folder named after a tool (`superpowers/plans`). Group them by topic and fix every link.
- **Prompts (exact):**
  > itu di folder / docs kurang rapih deh coba di rapihin dh banyak file aaron gimana ya
- **Output used:** `git mv` of 25 files: `engineering/` (architecture, api-contracts, transit-processing), `research/` (the five evidence files), `plans/` (13 plans, was `superpowers/plans/`), `team/aaron/` (handoff, work-log, report-drafts) and `ai-log/aaron-earlier-record.md` (was `docs/ai-usage-log.md`). A script rewrote the relative links and the `docs/...` paths in `AGENTS.md`, `README.md`, `TASK.md`, `TASKS/`, `ADR.md`, the product and team files and `experiments/worker-feasibility/README.md`. `docs/README.md` has the new tree. No file content was rewritten by hand except that README.
- **How it was tested or checked:** a script over every tracked markdown file found no broken relative link after the move (none before). A text search finds no old path left outside `docs/ai-log/`. not verified: the links on GitHub (not pushed at the time of writing); `./gradlew` and `npm test` were not run because no code changed.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to say what each folder holds.
- **Limits:** inside `docs/ai-log/` only markdown links were changed; paths written in prose there (for example `docs/api-contracts.md` in Aaron's earlier record and in older entries of this file) are kept as written, because they record the path at that time. In `docs/ai-log/aaron.md` one link target changed, its visible text still shows the old path. The moved files were not reviewed by Aaron. Open PR #8 adds `docs/schema.md` and edits `docs/ai-log/ary.md`, so it may need a sync with `main` after this merges.

## 2026-10-30 · One folder per person in docs/team, one AI log file for Aaron · Documentation

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** after PR #10 was merged, stop members from adding their own documents to the root of `docs/team/` (give each person a folder) and remove the duplicate AI log for Aaron.
- **Prompts (exact):**
  > udah aku merge, coba dibuat folder aja per masing masing orang karena takut mereka buat docs mereka sendiri di folder team dan tolong itu aaron di folder ai log kenapa ada dua jadi satu aja jangan ada yang duplikat
- **Output used:** `docs/team/<name>.md` moved to `docs/team/<name>/README.md` for the six members (Aaron's folder already held his working notes); links and the `docs/team/...` paths updated in `AGENTS.md`, `README.md`, the team files, the plans and `master-prompt.md`; `docs/ai-log/aaron-earlier-record.md` merged into `docs/ai-log/aaron.md` (its text sits under the heading "Earlier record", heading levels lowered by one, and the old file is deleted); a rule in `docs/README.md` that personal notes go in `team/<name>/`. This work sits on top of the previous "Tidy the docs folder" entry, whose commit was not yet in `main` when PR #10 was merged.
- **How it was tested or checked:** a script over every tracked markdown file found no broken relative link; a line-by-line comparison found that every non-empty line of both old Aaron files is in the merged file, except two lines that were replaced on purpose (the pointer to the second file and "No new entries yet."). not verified: the links on GitHub (not pushed at the time of writing); no code changed, so `./gradlew` and `npm test` were not run.
- **What the human changed or rejected:** Ary asked for the folders and for a single Aaron log. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say where a member puts their own notes and why the AI log has one file per person.
- **Limits:** `docs/ai-log/aaron.md` is Aaron's record and was edited without him: besides the merge, it was converted to UTF-8 (it mixed Windows-1252 and UTF-8, which showed as "Â·" in two entries), so Aaron should read it once. The character `˜` in one entry is kept as it was stored. Paths written as text in his older entries still show the old locations on purpose. Open PR #8 edits `docs/ai-log/ary.md`, so it may need a sync with `main`.

## 2026-10-31 · Decide who creates the connections document (ADR-0007) · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** Ary asked why Aaron's answer on `connections` was needed, then told Claude to take the best option instead of waiting. Claude had recommended that the Worker creates the document.
- **Prompts (exact):**
  > minta kepastian dari aaron buat apa?

  > pokokny yg terbaik lha kt ngikut aja
- **Output used:** `ADR.md` ADR-0007 changed from Proposed to Accepted with the decision "only the Worker creates `connections`, Rules deny the app create and delete" and the reason; the same decision in `TASKS/E0/E0.5.md`, `E4.3.md`, `E5.1.md` and the open-question lines of `docs/team/aaron/README.md` and `docs/team/gathan/README.md`.
- **How it was tested or checked:** a text search finds no "who creates connections" open question left outside the AI log. not verified: that Aaron agrees (Aaron's confirmation is listed as an open item in his file); no Rules or Worker code was written or run.
- **What the human changed or rejected:** Ary chose to follow the recommended option. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why Rules cannot query, why a deterministic `pairId` is used, and why the Worker and not the app creates the document.
- **Limits:** the choice rests on Claude's reasoning about the free plan (no Firestore triggers) and on the draft schema in PR #8, which is not merged. Core privacy logic: needs human review by Aaron and Gathan.

## 2026-10-31 · Point every AI assistant to the owner file in AGENTS.md · Documentation

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** `AGENTS.md` named only Aaron's owner file, so an assistant of another member might not find their stories. Add one general line.
- **Prompts (exact):**
  > itu MD masing masing gapapa README? agent ai mereka pasti ngerti kah?

  > di #8 ya
- **Output used:** one bullet at the start of "Task tracking" in `AGENTS.md`: every member has `docs/team/<name>/README.md`, read it first, then the task file; personal notes go in `docs/team/<name>/`.
- **How it was tested or checked:** not verified with another assistant. The titles of the six owner files name their person, and `docs/team/master-prompt.md` already uses the same path; a script found no broken relative link.
- **What the human changed or rejected:** Ary asked for the change to go in PR #8. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say how a member's assistant finds its work.
- **Limits:** whether other tools (Codex, Gemini, Copilot) read `AGENTS.md` first is not verified.

## 2026-10-31 · Answer the open questions in the task files · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** answer the 74 open questions in `TASKS/` so the team can move, and say which ones really need a human.
- **Prompts (exact):**
  > jawab semua open question itu deh biar semuanya bisa langsung gerak tapi emang itu butuh jawaban dari kita banget?

  Ary answered two questions: gender is "Filter saja tapi saat register isi gender wajib biar keliatan dia laki laki atau perempuan", and first-year students "Tunggu Siswa Mail (Recommended)".
- **Output used:** every *Open questions* section in `TASKS/` rewritten: 7 items decided by Ary (gender, first-year rule), 42 decided by Ary on Claude's recommendation (labelled so), 3 answered from a check, 27 left open and labelled (18 actions, 8 needing data such as the 13 Nov survey results or the shuttle timetable, 1 confirmation from Aaron). PRD FR1.3, the timetable line, the `users` row and open question 9 updated for gender and the first-year rule. The E2.1 subtask "photo upload to Storage" now says Cloudinary through the Worker (also in `work-breakdown.md`). The personal files list the still-open questions and the decisions for each person.
- **How it was tested or checked:** collaborators read from the GitHub API (six people); colour contrast computed with the WCAG formula from `colors.xml` (all text pairs at least 4.75:1), not measured on a device; a script found no broken relative link. not verified: the postgraduate email domain (the web search failed, left open for Dafi); that the Cloudflare free plan includes the Cron Trigger the cleanup job needs; the Gemini free-tier data terms.
- **What the human changed or rejected:** Ary chose the gender rule and the first-year rule. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain each decision in your own stories, and why the remainder rule in E7.1 keeps the total exact.
- **Limits:** the 42 recommended decisions were not discussed with their owners; numbers such as the 300-character message, 14-day request expiry, 2 MB chat photo and 5 MB contract file are product choices, not measured limits. E7.1 (balances), E7.5 and the Rules cases in T2.3 are core logic: needs human review. The ClickUp descriptions do not have these answers yet (the connector's daily limit was reached).

## 2026-10-31 · Remove work-breakdown.md · Documentation

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** delete `docs/team/work-breakdown.md`, because the task files already hold every story and subtask.
- **Prompts (exact):**
  > work breakdown apus aja kali ya karena task detailnya kan udh ada di folder task
- **Output used:** `docs/team/work-breakdown.md` deleted. Its parts that existed nowhere else (how the order works, the ClickUp set-up with statuses, tags, rules and the story template, the gates table, Dafi's weekly routine) moved into `docs/team/TEAM_PLAN.md` under "Order of work, ClickUp and gates"; the Done status type there now reads Done instead of Closed. Links in the six personal files, `master-prompt.md`, `TEAM_PLAN.md`, `ADR.md` (ADR-0008) and `docs/README.md` updated.
- **How it was tested or checked:** a script compared the 157 subtasks in `work-breakdown.md` with the task files and found every one of them there; a script found no broken relative link after the delete. Older entries of this log still name the file as it was at the time.
- **What the human changed or rejected:** Ary proposed the delete. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say where subtasks and the ClickUp rules now live.
- **Limits:** the epic table still appears in both `TEAM_PLAN.md` and the Milestones table of `TASK.md`.

## 2026-10-31 · Move the answers into Decisions taken · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** the previous entry put the 52 answers under *Open questions*, where a reader would take them for open items. Move them to *Decisions taken*, which every task file already has.
- **Prompts (exact):**
  > Ya, pindahkan
- **Output used:** in every task file the decided and answered items now sit under *Decisions taken* as `31 Oct 2026, Ary: ...`, `31 Oct 2026, Ary on Claude's recommendation: ...` or `31 Oct 2026, checked by Claude: ...`; *Open questions* keeps only the 27 open items, labelled **Action.**, **Needs data.** or **Needs confirmation.**, or says "None.". Two contradictions found while moving were fixed: the old E2.1 decision "Google Places autocomplete" is struck through and points to its replacement, and PRD section 4 (building picker) and E1.3 (photo host) now match the 31 Oct decisions. The personal files point to *Decisions taken*.
- **How it was tested or checked:** a text search finds no "Decided", "Answered" or "Still open" label left in `TASKS/`; a script found no broken relative link.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to say why Google Places was dropped (it needs billing).
- **Limits:** other places that may still name Google Places or Firebase Storage outside `TASKS/` and the PRD (for example the research notes and `TEAM_PLAN.md` "Storage rules") were not changed.

## 2026-10-31 · Mark E0.4 done after Ary's check · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** record that Ary checked E0.4 (project management and docs) and that it is done.
- **Prompts (exact):**
  > E0.4 tandain done karena udh aku cek
- **Output used:** `TASKS/E0/E0.4.md`: the four subtasks ticked, the four acceptance checks ticked with "Ary checked them on 31 Oct 2026", the Resolution updated, and two stale `docs/tasks/` references replaced. `TASK.md` and ClickUp already showed E0.4 as done.
- **How it was tested or checked:** ClickUp search shows the E0.4 task with status done; its subtasks were not read (the connector's daily limit). What Ary checked is not recorded.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the acceptance checks are normally Aly's; here Ary did them.
