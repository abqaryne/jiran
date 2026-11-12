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

## 2026-10-30 · Schema back to UM-only access · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** change `docs/schema.md` (PR #8) back to UM-only access after Ary reversed the "any email may register" decision.
- **Prompts (exact):**
  > tapi kayaknya better UM only ya biar terpercaya bahwa ini khusus pelajar UM doang, kan takut ada penipu atau apa klo semuanya boleh
- **Output used:** `docs/schema.md`: the "posting rule (UM verified)" under `listings` is replaced by one access baseline in section 1 (every read and write needs a verified email on a UM domain); the `profile` text says Rules check the UM domain from the token.
- **How it was tested or checked:** read only; no Security Rules or emulator test exists yet. not verified: that the baseline can be written as one Rules function and tested.
- **What the human changed or rejected:** Ary reversed the earlier decision. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the domain list is a draft; first-year students without a UM email cannot join yet (PRD open question 9).

## 2026-10-30 · Resolve the merge conflict of PR #8 with main · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** PR #8 conflicted with `main` after PR #9 was merged; bring `main` into Gathan's branch and keep both sets of AI log entries.
- **Prompts (exact):**
  > there's a conflict on PR#8 fix that
- **Output used:** a merge commit on `gathan/schema-draft` (no force-push). The only conflict was `docs/ai-log/ary.md` (both sides appended entries); both were kept. `docs/schema.md` access baseline now says `siswa.um.edu.my` only, to match PR #9.
- **How it was tested or checked:** no conflict markers remain; GitHub's mergeable state was `UNKNOWN` right after the push. not verified: that CI is green on the merge commit; Gathan has not read the merge.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** this branch is Gathan's. He should pull before his next push, and tell Aaron or Ary about the schema file change.

## 2026-10-31 · Resolve the second merge conflict of PR #8 with main · Module 2 (E0.5)

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** PR #8 (Gathan's schema draft) conflicted again with `main` after PRs #10 and #11 were merged, so that Gathan can continue the story.
- **Prompts (exact):**
  > oke coba benerin conflict di PR#8 dan nanti aku tinggal ngomong ke gathan buat lanjutin task nya
- **Output used:** merge of `main` into `gathan/schema-draft`. The only conflict was this file: both sides had appended entries at the end, so both were kept (the entries from `main` first, then the four PR #8 entries). One path in `docs/schema.md` changed from `docs/api-contracts.md` to `docs/engineering/api-contracts.md`.
- **How it was tested or checked:** a script found no broken relative markdown link after the merge; the entry count is 33 = 29 (main) + 4 (PR #8). not verified: CI on the merge commit at the time of writing.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to say why both sides of an append-only log are kept in a conflict.
- **Limits:** `docs/schema.md` stays in the root of `docs/` as Gathan placed it, many task files link to that path; whether it moves to `docs/engineering/` is a decision for Ary and Gathan. Task files still say "PR #8, not merged" until it merges. Paths written as text in `docs/ai-log/gathan.md` are kept as written.

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

## 2026-10-31 · Merge PR #12 and sync PR #8 with main · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** merge PR #12 (open questions, ADR-0007, removal of `work-breakdown.md`) at Ary's request, then bring `main` into Gathan's branch for PR #8.
- **Prompts (exact):**
  > ok push dan sync pr#8

  > Merge lalu sync
- **Output used:** PR #12 merged with a merge commit. Merge of `main` into `gathan/schema-draft` with two conflicts: `docs/ai-log/ary.md` (both sides appended entries; all kept, 29 shared + 5 + 5 = 39) and the data model table in `docs/product/PRD.md`, where PR #8's detailed table is kept and `gender` is added to the public `users` row, as decided on 31 Oct 2026.
- **How it was tested or checked:** a script found no broken relative link; the entry count matches both sides. not verified: CI on the merge commit at the time of writing.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** `docs/schema.md` itself does not have the `gender` field or the ISO weekday `day` yet; that is Gathan's work in E0.5.

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

## 2026-10-31 · Who does what in the shared stories · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** Ary asked whether AI assistants understand the short subtasks of stories shared by several people, and whether one person per story would be simpler. Claude advised keeping the split (one person writes all Security Rules, one owns the components) and making each person's part explicit; Ary agreed and asked to leave ClickUp for later.
- **Prompts (exact):**
  > ini kan ada subtask dan stories. dan di stories ini ada subtask yang dikerjain beberapa orang. apakah AI nya paham dengan subtask sesimple itu? apa better kita ganti aja satu orang kerjain satu story itu biar lebih gampang? gimana menurut kamu

  > oke deh boleh buat clickup nanti aja karena aku mau kamu hapus semua dan update rombak ulang ya
- **Output used:** a "Who does what" table (order, person, their part, where the hand-over is agreed) in the 14 flow stories that are shared: E3.4, E4.2, E4.3, E4.4, E5.1, E5.2, E5.3, E5.4, E6.1, E7.2, E8.3, E9.1, E9.2, E9.4. The table tells each person's AI to work only on its row and never invent field names. E2.1, E3.1 and E7.3 were left out because only one person does the work there (Ary supplies the components).
- **How it was tested or checked:** the rows were taken from each story's existing subtasks and the 31 Oct decisions; a script found no broken relative link. not verified: that the owners agree with the order; the field names named as hand-over points are not all in `docs/schema.md` yet (PR #8).
- **What the human changed or rejected:** Ary chose to keep shared stories and to rebuild ClickUp later. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why Security Rules stay with one person.
- **Limits:** ClickUp subtasks still have no assignee; deleting and recreating the ClickUp tasks would also delete logged hours and comments, which was pointed out to Ary.

## 2026-10-31 · Merge PR #13 and sync PR #8 again · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** merge PR #13 (E0.4 done, Who does what) at Ary's request, then bring `main` into Gathan's branch for PR #8.
- **Prompts (exact):**
  > ok mrge #13 aja dlu kemudian sync #8
- **Output used:** PR #13 merged with a merge commit after its three CI checks passed. Merge of `main` into `gathan/schema-draft`; the only conflict was this file, where both sides had appended entries, and all were kept.
- **How it was tested or checked:** entry count matches both sides; a script found no broken relative link. not verified: CI on the merge commit at the time of writing.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** none beyond the earlier entries.

## 2026-10-31 · Check Aaron's latest work and correct T2.2 · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** check Aaron's latest pull request and bring the progress in the task files up to date.
- **Prompts (exact):**
  > coba kamu cek PR updatean terakhir dari aaron dh dan update progressnya di file-file kita
- **Output used:** Aaron's latest pull request is PR #2 (merged 27 Oct 2026); all 27 of his commits are in `main` and no branch of his has unmerged work. The task files already described that work except one fact: `TASKS/T2/T2.2.md` said no load-test result was recorded, but `backend/tests/load/README.md` records two local runs on 27 Oct 2026 (the first failed the 100-user threshold, the second passed). T2.2 now states both runs, that they measure the synthetic commute fixture on the emulator and are not NFR1 evidence, and lists the files; its subtask is marked ⚠️. E3.3 lists the load harness. An earlier entry of this log (30 Oct, "backend/tests/load already has k6 scripts but no recorded result") was wrong on this point.
- **How it was tested or checked:** `gh pr list --author aarntn`, `git log --author=aarntn` across all branches, and the numbers copied from `backend/tests/load/README.md`; a script found no broken relative link. not verified: the runs themselves (not re-run).
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to say why a passing local fixture run is not NFR1 evidence.
- **Limits:** a deployed Worker is still not recorded anywhere in the repository (E0.2).

## 2026-10-31 · Status in TASK.md comes from the repository · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** Ary decided that the status in `TASK.md` must come from the code and documents in the repository, not from a copy of ClickUp. Write the rule and re-derive every status.
- **Prompts (exact):**
  > hei TASK.md itu jangan dari salinan clickup tapi dari kode yang udh dibuat
- **Output used:** a status table at the top of `TASK.md` (backlog: nothing in the repository; inprogress: part of *Work needed*; inreview: all of it in an open PR; qa: merged, *Acceptance* not all ticked; done: merged and every *Acceptance* check ticked; dropped). `todo` and `blocked` are no longer statuses: waiting is shown in *Depends on*, planning stays in ClickUp. 15 statuses changed in `TASK.md` and the task files: E0.1 and E0.2 done → inprogress (missing work listed in their resolution), E1.3 blocked → backlog, E2.1 to E2.4, E3.1, E3.2, E3.5 todo → backlog, E3.4 inprogress → backlog (no code; ask Aaron), E4.2, T1.1, T2.2, T4.2 backlog → inprogress. The rule is also in `AGENTS.md` and as an update under ADR-0008.
- **How it was tested or checked:** each status was set from the resolution text of the task file (written from the repository) and the ticked *Acceptance* boxes; only E0.4 has all of them ticked. not verified: local work that a member has not pushed.
- **What the human changed or rejected:** Ary chose the rule. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why E0.1 and E0.2 are not done although ClickUp says so.
- **Limits:** ClickUp now shows different statuses for some tasks; that is expected under the new rule.

## 2026-10-31 · E0.1 done, lecturer email dropped · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** Ary said the email to the lecturer is not needed and that every member has been added as a collaborator, so E0.1 is done.
- **Prompts (exact):**
  > jawaban dosen gaperlu itu, apus dan subtask 1 udh aku add semua jadi done
- **Output used:** `TASKS/E0/E0.1.md`: the lecturer email removed from *Work needed*, the subtasks and *Acceptance*; subtask 1 ticked; the two remaining *Acceptance* checks ticked as confirmed by Ary; status `done`, the resolution and a decision line added; the open question removed. `TASK.md` row set to done. The lecturer-email item also removed from Ary's file, `TEAM_PLAN.md` (Ary's step 0 and the platform note) and PRD open question 2.
- **How it was tested or checked:** collaborators were read from the GitHub API earlier the same day (six people); no broken relative link. not verified: a direct push to `main` being refused (not tried).
- **What the human changed or rejected:** Ary dropped the lecturer email. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** Ary's role "Main contact with the lecturer" in the personal file and the other mentions of the lecturer (data retention, UM domain) were kept, as they are not about this email.

## 2026-11-01 · Merge PR #14 and sync PR #8 again · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code
- **Task:** merge PR #14 (statuses from the repository, E0.1 done, T2.2 load runs) once its CI passed, then bring `main` into Gathan's branch for PR #8.
- **Prompts (exact):**
  > Push dan merge

  > Cek dan merge

  > cek dan merge
- **Output used:** PR #14 merged with a merge commit after its three CI checks passed (they were queued on GitHub for a while; auto-merge is not allowed in this repository). Merge of `main` into `gathan/schema-draft`; the only conflict was this file, where both sides had appended entries, and all were kept.
- **How it was tested or checked:** entry count matches both sides; a script found no broken relative link. not verified: CI on the merge commit at the time of writing.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** none beyond the earlier entries.

## 2026-11-05 · Review of Gathan's Security Rules on PR #8 · Review

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** check Gathan's latest commits on PR #8 (`a5b7cb0`, `004a3c0`, 3 Nov 2026) and post the findings as a review comment.
- **Prompts (exact):**
  > coba cek committan terbaru gathan

  > berarti ini udh oke kah?

  > command di pr itu ya

  > comment maksudnya
- **Output used:** one review comment (state COMMENTED, not an approval) on PR #8. Two privacy gaps in `backend/firestore.rules`, each with a suggested fix and a test: (1) a requester can set `hostUid` to themself, accept their own `listingRequests` document and then read `listings/{id}/private/location`; (2) `chats` create does not require a Connection. Non-blocking notes: the E0.5 row in `TASK.md` is not updated, the E0.5 Resolution heading still says 30 Oct, and the `listings` update rule lets the owner change `ownerUid`.
- **How it was tested or checked:** the rules and the smoke test were read; the CI log of run 41575153413 shows 25 denials and 27 permissions passing, and all three checks green. The two gaps were found by reading the rules; not verified: no emulator run reproduces them yet.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to explain why each gap breaks the PRD rule that the address and contact stay hidden until a Connection exists.
- **Limits:** a reading review only; Aaron's review of PR #8 is still missing.

## 2026-11-10 · Merge PR #8, check E0.5 and E2.1 against their tasks, move E0.5 to qa · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** check Gathan's new commits, approve and merge PR #8, check PR #15 (E2.1) and both task files against the code, then set E0.5 to `qa` and tidy the documents.
- **Prompts (exact):**
  > coba cek committan terbaru gathan

  > berarti PR#8 udh oke kah? reviewnya dari aku aja

  > Approve dan merge

  > PR#15 udh oke blm

  > klo cek PR, cek juga secara task apakah tasknya udh selesai semua

  > tapi tadi pr #8 udh oke kan?

  > but alrdy merge right pr #8

  > ya tolong dibenerin klo misalnya dari kode udh oke lanjut ke QA ya
- **Output used:** PR #8 approved in Ary's name and merged (merge commit `4d725d6`). PR #15 checked by reading (Worker, rules, Android gateway, task file); nothing posted or merged. In this branch: `docs/schema.md` moved to `docs/engineering/schema.md` and every link updated; E0.5 set to `qa` in its task file and `TASK.md`, with a new resolution; the stale "PR #8, not merged" text in E1.1, E1.3 and E5.1 updated; Aaron's "second review of PR #8" question removed (count 10 → 9); `schema.md` added to `docs/README.md`.
- **How it was tested or checked:** PR #8 CI (android, backend, worker-feasibility) green on `042bebb`; the CI log shows 29 denials and 28 permissions. Every E0.5 *Acceptance* item was compared with the test names in `product-rules-smoke.cjs`. No broken relative link in the repository after the move. not verified: the emulator tests were not run locally; the field list of every collection in `schema.md` was not checked one by one.
- **What the human changed or rejected:** Ary decided that Ary's review alone was enough to merge PR #8, and that a merged story whose code is fine moves to `qa`. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to say why E0.5 is `qa` and not `done` (Acceptance not ticked, subtask 4 open).
- **Limits:** Aaron did not review PR #8. PR #15 also edits `docs/schema.md`; a local `git merge-tree` of this branch with PR #15 shows no conflict, so Git follows the move.

## 2026-11-10 · Accept the E2.1 draft state and align the E2.1 task file · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** Ary accepted Gathan's Host-only draft (ADR-0009) and asked to tidy the E2.1 task file on PR #15 ourselves instead of asking Gathan.
- **Prompts (exact):**
  > oke berarti pr#15 udh aman atau blm

  > ya saya setuju PRD nya ya dan kita aja yang rapihin

  > sumpah yang masih decisions taken, kayknya finalize sekarang aj dh gaperlu decisions taken lagi atau apapun open pertanyaan itu dan requirement dan segala macemnya finalize sekarang aj a
- **Output used:** on Gathan's branch `gathan/e2.1-building-rent` (PR #15): Ary's acceptance added to the two PRD lines and as an update under ADR-0009; the PRD FR2.1 acceptance line about a place search that fills the address rewritten to the 31 Oct decision (seeded Area plus typed building name, Area centre as an estimate). `TASKS/E2/E2.1.md`: Description, Data, Work needed and the five *Acceptance* checks aligned with the draft state and the 31 Oct decision; the stale "Where it stands (30 Oct)" section removed; the resolution now says PR #8 is merged. In the final format agreed the same day, the *Decisions taken* and *Open questions* sections were folded into *Constraints* and three new subtasks (case-insensitive building names; Worker review before activation; real area seeds and deploy). No code changed.
- **How it was tested or checked:** read against the PR #15 code (`BuildingPickerViewModel.normalizeBuilding`, the listing Rules, the Worker); no broken relative link. not verified: whether Gathan agrees with the new *Acceptance* wording.
- **What the human changed or rejected:** Ary accepted the draft state and chose to tidy the file without Gathan. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why a draft is hidden until E2.2 adds a priced vacant Room.
- **Limits:** the owner READMEs are changed in PR #16, not here, to avoid a conflict.

## 2026-11-10 · Final task files: no more open questions or decision lists · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** Ary found the open questions and the cross-file dependencies too complicated and asked to finalize every requirement now, with no *Decisions taken* or *Open questions* sections.
- **Prompts (exact):**
  > sumpah yang open questions dan yang bergantungan satu sama lain ini ngeribetin deh, gimana ya

  > sumpah yang masih decisions taken, kayknya finalize sekarang aj dh gaperlu decisions taken lagi atau apapun open pertanyaan itu dan requirement dan segala macemnya finalize sekarang aj a
- **Output used:** in 61 task files (E2.1 was done in the commit before this one), committed to PR #15 because PR #16 was already merged: every *Decisions taken* line written into *Constraints* as a final rule, without dates or attribution, and history lines dropped (the 29 Oct any-email decision, the 30 Oct gender proposal, "status lives only in ClickUp"); both sections removed; the reverse *Blocks* header line removed (only *Depends on* stays). The 25 open questions were closed: 6 became new subtasks (E0.2 deploy the Worker, E1.1 and E1.2 real-device tests, E3.3 mark open checks, E10.2 Malay reviewer, T3.1 rubric), 9 became constraints (estimates until the 13 Nov survey for E2.2, E3.4, E4.4, E6.2; shuttle leg and row hidden until T1.4; final E10.1 retention text; T1.4; T4.2), and the rest were already answered or covered by a subtask (ADR-0007 final, E1.1 Rules merged with PR #8, E8.1 provider final as Gemini free tier, the postgraduate domain out of scope). E1.1 subtask 6 is now ✅ because Ary's review is enough. The "Open questions waiting for you" and "Decided on 31 Oct" lists were removed from the six owner READMEs; `AGENTS.md`, `master-prompt.md` and `TASK.md` describe the new rule.
- **How it was tested or checked:** a script did the move and its result was read on sample files (E1.1, E2.3, E3.3, E5.1, E10.1); a grep finds no *Decisions taken*, *Open questions* or *Blocks* left in `TASKS/`; no broken relative link. `ADR.md` was not edited (append-only).
- **What the human changed or rejected:** Ary chose to finalize everything at once. TODO (Ary): check the final choices listed above.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the choices that close open questions (postgraduate domain out of scope, Gemini as final provider, the retention text) were made by Claude on Ary's instruction and are not checked by their owners. Dates and names of who decided each rule now live only in this log, the git history and `ADR.md`.

## 2026-11-10 · ClickUp synced with the repository · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5), ClickUp connector
- **Task:** bring the 62 ClickUp stories in line with the repository.
- **Prompts (exact):**
  > oiya clickup kita belum terupdate ya, coba di update
- **Output used:** in the ClickUp list "Jiran", all 62 stories (not their subtasks) got the status from `TASK.md` on PR #15 (2 done, 5 qa, 9 in progress, 46 backlog) and a new short description: FR, owner, due date, *Depends on*, the first sentence of the task description, a link to the task file on GitHub and a note that the status follows the repository. The old descriptions (with "Draft, owner to confirm", "Open questions" and the old `docs/tasks/` paths) were replaced. 15 statuses changed, for example E0.2 done → in progress, E0.5 in review → qa, E1.3 blocked → backlog, E2.2 to E3.2 and E3.5 this week → backlog, E4.2, T1.1, T2.2 and T4.2 backlog → in progress.
- **How it was tested or checked:** every update call returned success. not verified: the ClickUp board was not opened to look at the result.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the about 180 ClickUp subtasks were not changed; their names and statuses may still be old (for example E2.1's "photo upload to Storage"). The task files are the source for subtasks. Hours logged in ClickUp were not touched.

## 2026-11-10 · Recheck every status against the repository and sync ClickUp subtasks · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5), ClickUp connector
- **Task:** recheck the real status of every task in the repository, update ClickUp from the latest task files (subtasks too), and add what is missing in ClickUp.
- **Prompts (exact):**
  > coba update semua clickup nya berdasarkan update task terbaru kita dan juga tolong di recheck lagi di repo kita gimana status aslinya. kalau misal ada task yang belum ada di clickup di tambahin aj ya
- **Output used:** repository (PR #15): the Rules and tests merged with PR #8 already cover Gathan's subtasks in E4.3 and E5.1 (✅) and part of E1.3, E2.3, E5.2, E6.1, E7.2, E8.3, E9.1, E9.2, E9.4 and T2.3 (⚠️); Aaron's commute code already computes the E3.4 true cost; Aaron's research and drafts cover part of T1.3, T3.1 and T4.1. Those 16 stories moved from backlog to inprogress, each with a "Rechecked 10 Nov 2026" line. Stale ticks were corrected: E3.3 subtask 1 (no openrouteservice adapter exists; `routes-client.js` is the Google adapter) and E0.2 subtasks 1, 3 and 4. E8.1 and E8.2 subtasks now match their constraints (Gemini; fictional contracts). Totals now: 2 done, 5 qa, 25 inprogress, 30 backlog. ClickUp: the 16 story statuses and 15 subtasks (status, and three names) were updated before the connector's daily limit (100 calls) stopped the run. What is left (23 subtask statuses, 4 renames, 16 new subtasks) is listed in `docs/team/ary/clickup-sync-todo.md`.
- **How it was tested or checked:** each change is backed by a file or a test name in the repository (`product-rules-smoke.cjs` case names, `commute.js`, `docs/research/`, `docs/team/aaron/report-drafts.md`). ClickUp subtasks were matched to task-file subtasks by text; four were matched by hand. not verified: work that members have not pushed; the ClickUp board was not opened.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Be able to say why a story counts as inprogress when only its Rules exist.
- **Limits:** "inprogress" follows the rule at the top of `TASK.md` (part of *Work needed* is in the repository), so several stories are inprogress only because E0.5 already wrote their Rules. ClickUp is only partly synced until the remaining list is run.

## 2026-11-10 · Merge PR #15 and start E1.3 with the profile field checks · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** merge PR #15 (E2.1 stays inprogress), then start E1.3 Profile setup with the smallest piece: the field checks in plain Java with unit tests.
- **Prompts (exact):**
  > ok berarti kita sekarang lanjut task kita dh, apa yang bisa kita kerjain

  > merge dlu kah? tasknya gathan yang E2.1 belum selesai kan tapi

  > Approve dan merge

  Ary's answers to three design questions: the timetable step "Boleh dilewati (Recommended)"; the name "Dari nama saat sign up (Recommended)"; setup progress "Dari dokumen yang sudah ada (Recommended)".
- **Output used:** PR #15 approved in Ary's name and merged (`50023f9`). Branch `feature/e1.3-profile`: the three decisions written into E1.3 *Constraints* (a skipped timetable is saved as an empty `entries` list so the step counts as passed); new package `domain/profile` with `DisplayName` (initials from the first two words, first name), `TimetableEntry` (ISO weekday 1 to 7, "HH:mm", end after start, building required) and `ProfileCheck` (gender male or female, faculty, year 1 to 4, budget in sen with min not above max); 12 JUnit tests in three test classes. E1.3 item 9 set to ⚠️.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed; 101 unit tests in the project, 0 failures.
- **What the human changed or rejected:** Ary chose the three design answers. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Needs human review: the initials rule (first two words) and the year range 1 to 4 from the schema.
- **Limits:** no screen, repository or Firestore write yet; the year range 1 to 4 comes from the schema and does not fit five-year programmes (for example medicine), not verified with UM.

## 2026-11-10 · Drop the "send to the group" subtasks · Project management

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** remove every subtask whose work is sending something to the team chat; results live in the repository instead.
- **Prompts (exact):**
  > berarti tadi task gathan yang kurang apa?

  > coba delete semua task yang kirim ke grup atau apapun itu
- **Output used:** removed E0.3 subtask 6 (announce the component list, already done), E0.5 subtask 4 and its work line (send the field list by 15 Nov; the schema is in `docs/engineering/schema.md`), E0.6 subtask 3 and its work line (post the contracts; they are in `docs/engineering/api-contracts.md`). T1.2 subtask 1 now says Aly writes the numbers into `docs/product/ASSUMPTIONS.md` instead of sending them to each person. Kept: survey outreach to students (T1.1 share the link, ask Telegram admins) and the email to UM transport (T1.4), because they reach people outside the team. The matching ClickUp deletions are added to `docs/team/ary/clickup-sync-todo.md`.
- **How it was tested or checked:** a grep for "to the group" and "Announce" finds nothing left in `TASKS/`.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** ClickUp is not changed yet (daily limit); the three subtasks are deleted there when the sync continues.

## 2026-11-10 · Build the rest of E1.3 Profile setup · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** finish E1.3 first; Ary will study the code afterwards.
- **Prompts (exact):**
  > E1.3 kelarin dlu deh nanti akhiran baru kita pahamin
- **Output used:** branch `feature/e1.3-profile`. `domain/profile`: `SetupProgress` (which step to resume, read from the documents that exist) and `Ringgit` (typed ringgit to integer sen). `data`: `ProfileRepository` and `FirestoreProfileRepository` (step 1 writes `users/{uid}` plus `private/profile.fullName` and `private/contact.firstName`; step 2 `private/timetable`; step 3 `lifestyleTags` and budget and car; step 4 the `privacy` map). `ui/profile`: `ProfileSetupActivity`, `ProfileSetupViewModel`, four step fragments, `Chips`, layouts, strings, arrays and one style built from tokens. `ShellActivity` now sends an account with an unfinished profile to setup (the profile gate, E1.3 item 7 and E1.4 item 4). E1.3 and E1.4 task files updated.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed: 116 unit tests, 0 failures, including 10 new `ProfileSetupViewModelTest` cases with a fake repository. Two lint warnings in the new layouts were fixed. not verified: the app was not run on a device or emulator; the Firestore writes were not tested against the emulator Rules; the two-account privacy check was not done.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Ary chose to finish the story first and study it afterwards. Needs human review: the profile gate keeps the shell open when the check fails, the initials rule, and the default privacy toggles.
- **Limits:** the faculty list in `res/values/arrays_profile.xml` was written from general knowledge because the UM site could not be opened; it is marked unverified and needs a source link. Lifestyle tags are a draft until the survey results. The web tools failed during this task, so nothing was checked online.

## 2026-11-10 · Walk through E1.3 on the emulator · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5)
- **Task:** try the new profile setup in the Android emulator before opening the PR.
- **Prompts (exact):**
  > Coba di emulator dulu
- **Output used:** the Firebase Auth and Firestore emulators (`demo-jiran`, the repository's `firestore.rules`) and the Pixel_9 Android emulator. Two fictional test accounts were made in the Auth emulator (passwords generated, not kept). Found and fixed: the tabs flashed for about a second before setup opened (the shell now stays hidden until the profile check answers), and the remove-class button was a heavy filled button (now the soft icon button). E1.3 items 1 and 5 and the resolution updated.
- **How it was tested or checked:** driven with `adb` and screenshots: step 1 empty shows "Choose one"; a class ending before it starts shows "The class must end after it starts"; min rent 600 above max 300 shows the max-field error; killing the app at step 4 reopens step 4; Finish opens the tabs; reopening goes straight to the tabs. The saved documents were read back from the emulator and match the schema. A second account reading `private/profile`, `private/timetable` and `private/contact` got 403; the public `users` document 200. The Android checks passed again after the two fixes.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** emulator only, no real phone; the phone opt-in toggle was not tried with a Connection; the first-time flow for an account without a name was not tried.

## 2026-11-10 · Remove the class timetable: campus days in the app, the docs and Figma · E1.3 (FR1.3), FR4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5), Figma connector
- **Task:** drop the class timetable from the product; the commute only needs to say which mode is fastest or cheapest to the faculty.
- **Prompts (exact):**
  > kayaknya jadwal kelas itu keribetan deh implementasinya gasi?

  > gausah ada jadwal kelas gini deh, buat ribet. kan tujuannya buat tau aja kalau misalnya mau commute ke fakultas naik apa yang paling cepet gitu gasi

  Ary's answers: arrival time "Tanpa jam sama sekali"; monthly trips "Ditanya: berapa hari ke kampus per minggu (Recommended)".

  > Tolong update juga buat screen kita yang masi ada jadwal kelas di figma ya (with the Figma link)
- **Output used:** app (branch `feature/e1.3-profile`): the timetable step, `TimetableEntry`, its layouts and tests removed; setup is now three steps; the budget step asks "Days a week you go to campus" (1 to 7) saved as `campusDaysPerWeek` in `private/profile`; the Welcome sample texts changed. Docs: PRD FR1.3, FR3.2, FR4.1 to FR4.3, their acceptance lines, the stage and data tables and Module 7; GLOSSARY (true monthly cost); USER-FLOW; schema (`campusDaysPerWeek` added, `private/timetable` marked removed, `overlappingClassDays` always null); DESIGN.md; task files E0.5, E1.3, E3.2, E3.3, E3.4, E4.2, E4.4, E6.2, E6.3, E6.4, E7.2, E10.1; Aaron's and Gathan's owner files. New subtasks for Aaron: E3.4 #4 (remove the class start time and on-time check from the commute code; trips from campus days; one fixed weekday-morning departure as an assumption) and E6.2 #2 (drop `classDays` from `match.js`). Figma (UI Design page): the "Profile 2 · Timetable" screen deleted; profile setup renumbered to three steps with campus-day chips on the budget step; the timetable rows removed from both privacy screens; 40 texts on 20 screens changed from class times and class overlap to faculty travel time, "Cheapest" and budget or habit reasons.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (111 unit tests, 0 failures); no broken relative link; a search of the Figma page finds no "10:00 class", "leave by", "Late for", "Class overlap" or "Class timetable" text left; screenshots of the changed Figma screens were checked. not verified: the three-step setup was not walked through on the emulator (Ary stopped that run to ask for the Figma update).
- **What the human changed or rejected:** Ary removed the timetable and chose no arrival time. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the commute no longer says "on time" and how campus days give the monthly cost.
- **Limits:** the commute code (Aaron) still expects a class start time until E3.4 subtask 4 is done; the `private/timetable` Rules are still in `firestore.rules`; the survey (already sent) still asks about the class timetable in F5; the Figma Design System page was not searched.

## 2026-11-10 · Make the profile setup screens match Figma · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Opus 5.5), Figma connector
- **Task:** Ary saw that the setup screens in the app did not match Figma; make them match.
- **Prompts (exact):**
  > Dan aku liat masi banyak UI di setup profile yang ga sesuai sama di figma deh. coba benerin
- **Output used:** read the Figma frames "Profile 1 · Faculty", "Profile 2 · Budget and commute" and "Profile 3 · Privacy" with the design-context tool, then rebuilt the Android layouts from the existing design-system styles: top bar with back and the centred "Set up profile", progress with "Step X of 3", footer with a top divider and "Next"/"Finish"; step 1 heading "Which faculty are you in?", a faculty search field that filters the chips (Popular shows nine, full name fills the field), Year 1 to 4 and Postgraduate (stored as year 5), gender kept below; step 2 budget from and to side by side, campus days on one row, a new reusable switch row for "I own a car", the eleven About you tags; step 3 three switch rows (phone, budget range, photo; on = hidden, all on) and the info banner. `privacy.showBudgetToConnections` added (repository, view model, fake, tests, schema). New files: `item_profile_switch_row.xml`, `bg_divider_top.xml`. In Figma: added the required Gender chips to "Profile 1 · Faculty" and removed "Skip" from step 2, because setup is required.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed. On the Pixel_9 emulator with the Firebase emulators and a fresh fictional account: each step was compared by screenshot with its Figma frame, picking FSKTM filled the field with the full name, Finish opened the tabs, and the saved documents hold `year` 2, `campusDaysPerWeek` 4, budget 40000 and 55000 sen, the two tags, and privacy `false`, `false`, `true`.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** chips keep the 48 dp touch area, so rows sit a little further apart than in Figma; the switch is the design-system switch, smaller than the Figma one; during the work "Profile 1 · Faculty" in Figma changed to "1 of 3" in the top bar while steps 2 and 3 still show "Step X of 3" under the progress, so the app keeps "Step X of 3" on all three until Ary decides.


## 2026-11-11 · Profile setup spacing · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** continue the existing profile UI work using the spacing supplied by Ary, verify locally, and commit.
- **Prompts (exact):**
  > Lanjutkan setup UI buat jarak profile
  >
  > **Belum dikerjakan (hasil pembacaan Figma terbaru):**
  > 1. **Jarak progress:** tambah `paddingBottom` 6 dp di bawah progress. Tinggi blok progress di Figma 10 dp, lalu gap 16 dp ke judul.
  > 2. **Jarak chip:** Figma memakai tinggi chip 36 dan gap 8 antar chip, tapi app memakai area sentuh 48 dp sehingga jarak vertikalnya jadi sekitar 56 dp.
  >    - Perbaikannya: di semua ChipGroup pakai `chipSpacingVertical="-4dp"`, `layout_marginTop="10dp"` dan `layout_marginBottom="-6dp"`. Area sentuh 48 dp tetap terjaga.
  >    - Khusus chip hari kampus (1–7) pakai `chipSpacingHorizontal="-8dp"` supaya muat satu baris.
  > 3. **Cek dan commit:** jalankan `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug`, bandingkan lagi dengan Figma di emulator, tambahkan entri AI log, lalu commit.
  >
  > Ada 6 commit lokal di `feature/e1.3-profile` yang belum di-push, ditambah perubahan label step ini yang belum di-commit.
- **Output used:** spacing in `activity_profile_setup.xml`, `fragment_profile_basics.xml`, `fragment_profile_preferences.xml`; preserved the existing pending top-bar step label change in `activity_profile_setup.xml` and `strings_profile.xml`. Updated `TASKS/E1/E1.3.md` and its `TASK.md` row. Local commit on `feature/e1.3-profile`; no push or merge. Unrelated pre-existing `android/gradle/gradle-daemon-jvm.properties` excluded.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (111 unit tests, 0 failures/errors). Initial sandbox run failed with `gradle-9.6.0-bin.zip.lck (Operation not permitted)`; rerun with approved local cache access succeeded. A temporary instrumentation harness rendered the three real profile fragments with a fake repository on Pixel_9 at 1024 px / 420 dpi (about 390 dp width); one preview check passed, asserting all seven campus chips share a row and each has at least 48 dp height. Screenshots inspected by the assistant, saved under `/private/tmp/jiran-profile-spacing/`; temporary harness removed and emulator resolution restored. `git diff --check` passed. Human verification: not recorded.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review. Figma tools are unavailable and opening the design URL failed, so direct comparison with current Figma is not verified; measurements come from Ary's prompt. Preview uses a fake repository, with no sign-in or persistence check; screenshots draw the root view with transparent background and use approximate system-bar padding. No real phone, accessibility hit-region audit or CI run. Existing chip style still enables the minimum touch target; the preview assertion checks height only.

## 2026-11-11 · Open interactive profile preview · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** open profile setup in the local Android emulator for Ary to inspect.
- **Prompts (exact):**
  > coba buka di simulator dong aku pengen liat
- **Output used:** locally installed debug APK with a temporary interactive `ProfilePreviewActivity`, using the existing profile layouts, fragments and view model with a fake in-memory repository. Temporary Java source removed and debug manifest restored after building; no production source change and no push.
- **How it was tested or checked:** `./gradlew assembleDebug` passed. Local ADB installation and activity launch; emulator window brought to the front. Direct shell launch of the production profile activity was denied because it is not exported, so the local preview was used. No backend reads/writes performed by the preview repository. Human visual review: not recorded.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review; preview data is not persisted and Finish returns to step 1. Temporary preview exists in the installed emulator APK only and is absent from repository source. Full lint/test suite not rerun for this temporary preview; it was run for the preceding spacing commit.

## 2026-11-11 · Faculty dropdown and Popular shortcuts · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** replace the faculty search Results chip section with a dropdown and retain Popular shortcut chips.
- **Prompts (exact):**
  > oke buat search fakultasnya kayaknya better dalam dropdown deh jangan buat results nya dalam bentuk section results baru dengan pill gitu
  > tapi mungkin tambahin baru aja section popular dengan pill buat pilihan cepat
- **Output used:** `BasicsStepFragment.java`, `fragment_profile_basics.xml`, `item_profile_faculty_dropdown.xml`, `strings_profile.xml`; task constraints/resolution in `TASKS/E1/E1.3.md`, its `TASK.md` row, and the picker pattern in `docs/design/DESIGN.md`. Native autocomplete matches full names and abbreviations, displays full names in rows at least 48 dp high, and retains Popular chips for an empty query. Typing after selection invalidates the old code; no-match text uses field helper text. No backend/schema changes, push or merge.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (111 unit tests, no failures). A local debug-only preview using the actual fragments/view model and a fake repository was built and installed on Pixel_9; typing `eng` displayed `Faculty of Engineering` in the dropdown (screenshot inspected), and selecting it populated the full name (UI hierarchy checked). Temporary preview source removed and debug manifest restored after building. `git diff --check` passed. Human review: not recorded.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review. Current Figma not read or updated; this UI adjustment follows Ary's request. Existing faculty data remains unverified against UM's official list. Runtime backend persistence, screen reader interaction and rotation not verified in this session. The installed emulator APK includes the temporary local preview; repository source does not.

- **Follow-up prompts (exact):**
  > tetep keep section popularnya jangan diapus, itu buat pilihan cepet buat orang yang gamau search
- **Follow-up adjustment:** Popular now always stays visible, including while searching and after selection; this supersedes the empty-query visibility described above. The no-match helper text was also checked in the emulator with `zzzzz`.


## 2026-11-11 · Review current profile task readiness · E1.3 (FR1.3)

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** assess whether the current profile setup work meets E1.3 and is ready to finish.
- **Prompts (exact):**
  > ok coba cek apakah task yang kita kerjain sekarang sudah oke?
- **Output used:** review findings in chat and a review resolution in `TASKS/E1/E1.3.md`; updated `TASK.md` row while retaining `inprogress`. No app code changed in this review.
- **How it was tested or checked:** read the E1.3 requirements, current faculty picker and chip helper, privacy screen, faculty resources and shell gate. Reused the immediately preceding full Gradle run because app code has not changed since it passed; read its JUnit XML (111 tests, 0 failures/errors/skips) and lint XML (warnings, no errors). `git diff --check` passed. No fresh backend, network failure or lifecycle runtime test in this review.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review. Static review shows that the shell exposes tabs on profile-load error and skips the gate on activity restoration; this conflicts with the setup-before-tabs acceptance. Privacy opt-in/toggle enforcement and faculty source verification remain outstanding per the task record. The emulator preview uses a fake repository. Current faculty UI changes remain uncommitted; no push or merge.

## 2026-11-11 · Clarify per-trip commute and retire true monthly cost · FR1.3, FR4.1–FR4.3

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** update the PRD and file/task structure so commute helps a student understand how to travel from a candidate apartment to their faculty, with estimated one-way time and fare, including e-hailing; remove true monthly cost following Ary's explicit clarification.
- **Prompts (exact):**
  > eh kayaknya ada miss comm deh, aku pengennya fitur commute itu biar si user itu tau gimana caranya commute dari apartemen yang dia pengen coba ke fakultas dia berapa lama. dan kalau pake grab berapa lama dan harga perkiraannya dari tempat dia ke fakulti berapa buat gambaran karena kan mahasiswa tahun pertama blm tau dia jadwalnya gimana
  >
  > Ini ui yang sekarang. coba diupdate PRD nya ya dan struktur filenya
  > hapus true monthly cost karena itu tidak fixed dan sangat relative tergantung pemakaian
- **Input reference:** Ary attached a Room-details UI screenshot with monthly rent, a true-monthly-cost headline/breakdown, “Getting to FSKTM”, Peak/Off-peak and transit/e-hailing/walking rows. Used only for layout context, not verified fares, route times or seed data. The second prompt above was the answer to the assistant's question about keeping monthly cost optional; Ary rejected it and requested removal instead.
- **Output used:** updated `docs/product/PRD.md` (v0.3, FR1.3 and FR4.1–FR4.3 plus affected journeys), `GLOSSARY.md`, `USER-FLOW.md`, `ASSUMPTIONS.md`; aligned design, target API contract, schema deprecation note and architecture. Added `docs/plans/2026-11-11-commute-per-trip.md` with existing/proposed file responsibilities, owners and migration order, linked from the docs index and repository README. Updated 13 affected task files/master rows and Ary/Aaron/Gathan/team ownership notes. Appended ADR-0010. Existing Java/JavaScript files were not moved or migrated in this documentation task; prior faculty dropdown edits remain in the working tree.
- **How it was tested or checked:** read the current commute/profile requirements, target/legacy contracts, task and ownership files and inspected the supplied screenshot. `git diff --check` passed. A temporary local Python check verified 52 added relative Markdown links (0 broken), 13 affected task/master statuses (0 mismatches), three retained Module 4 FR rows and removal of schedule/monthly-total requirements from the target PRD. No Android/backend tests rerun for this docs-only change; the earlier Android pass does not verify the new commute requirements.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review. This records target requirements and a migration handoff, not implemented or deployed functionality. Current profile still requires campus days; backend/Android legacy DTO and fixtures still use monthly totals/class-time selection until assigned migration work. Old fixture documentation/history is retained and labelled legacy. Target wire format still needs Aaron/Gathan agreement. No live Figma update, provider call, fare/route verification, Firebase rules change or destructive data migration. No push or merge; no fare, peak factor or emission value invented.

## 2026-11-11 · Update existing UI for per-trip commute · E1.3, E3.4, E4.2

- **Human owner:** Ary
- **AI tool and model:** Codex (GPT-6; exact model variant not recorded)
- **Task:** update existing profile UI, shared Room/commute components, Welcome text and previews after removing true monthly cost. Ary explicitly limited the work to existing screens/components.
- **Prompts (exact):**
  > oke coba update ui nya berdasarkan update terbaru ini
  > jangan buat baru, benerin yang ada dulu aja
- **Output used:** removed campus-day chips, validation, save argument and write from existing profile classes/layout; `SetupProgress.fromFields` accepts profiles with or without legacy attendance data. Budget screen now says Budget and lifestyle and uses monthly Room rent labels. Existing `RoomCardView`/XML use `roomCommute` and `setCommute` instead of the true-cost slot; existing commute component/gallery use one-way/unavailable fare labels and wrap long fare text. Welcome no longer advertises true monthly cost; gallery's old timetable example is now Faculty. Existing `CommuteSummary` exposes the server's already-validated `perTripCostSen`; existing developer preview renders it with departure/source/warnings, without monthly total/class eligibility. Updated existing regression tests, task/master resolutions, design/schema notes and file handoff. No new product page or screen was created. No push or merge.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (112 unit tests, 0 failures/errors). `npm test -- --runInBand` in `backend/functions` passed (16 suites, 380 tests). New regressions in the existing progress test cover profiles without campus days, legacy values and absent documents; existing view-model budget test verifies advance to privacy without attendance. Existing DTO tests check per-trip fare exposure and unavailable null. On Pixel_9, the actual profile preference fragment was rendered using a temporary local debug host and fake repository; screenshot inspected, then sample budget 400–550 advanced to “Choose what stays private” / “3 of 3” without days (UI hierarchy checked). Existing gallery screenshot inspected for commute rows and Room rent; an obsolete timetable example found there was then replaced and the full Android checks rerun. Temporary preview source removed and debug manifest restored. `git diff --check` and document-link/task-status checks passed. An initial shell invocation used the wrong working-directory-relative path and the sandbox blocked Gradle's cache lock; corrected commands with approved local cache access passed.
- **Skill/source used:** official [Firebase Firestore skill](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/SKILL.md) and its [Standard Android reference](https://github.com/firebase/agent-skills/blob/main/skills/firebase-firestore/references/standard/android_sdk_usage.md), read before repository changes. Existing project/Standard edition and Java stack retained; no provisioning, dependency upgrade, live Firebase operation or Rules change.
- **What the human changed or rejected:** TODO (owner)
- **What the human understands and can explain:** TODO (owner)
- **Limits:** needs human review, especially profile progress and backward compatibility. Runtime persistence with real Firestore/emulators not exercised; native preview used a fake repository. Existing Rules were inspected and contain no campus-day field requirement, but their enforcement was not newly integration-tested here. Backend fixture calculator/parser still validates legacy monthly/class-time fields internally; full server/DTO migration is pending. No real fare/route lookup or published fare invented; gallery unavailable fares are explicit. Current Figma and a real phone were not checked. No new detail screen, routing feature or profile-gate error fix was implemented; those task gaps remain. Temporary native-preview APK contained only a debug host for existing fragments, absent from final repository source.

## 2026-11-11 · Bring everything in line with PRD v0.3 (per-trip commute) · FR1.3, FR4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** Ary updated the PRD (no true monthly cost, no campus days, one-way time and fare per trip) and asked to update everything from it.
- **Prompts (exact):**
  > coba kamu cek update an prd terbaru aku, update semuanya berdasarkan update itu

  Earlier the same day: "1 of 3 di step bar liat figma ini: (Figma link) buat detail spacing terupdate".
- **Output used:** found that the working tree already followed PRD v0.3 for the task files, the profile code (no campus days), the room card (`setCommute`) and the plan `docs/plans/2026-11-11-commute-per-trip.md`; this session added: `docs/design/DESIGN.md` (term list, three-step profile flow), `docs/team/ary/clickup-sync-todo.md` (new ClickUp renames), the "N of 3" label in the profile top bar, and in Figma 28 texts changed from monthly cost to per-trip copy (Welcome, Room details, Send request, Commute options, Public transport route), the campus-days question and the Profile "Days on campus" row removed, Profile 2 renamed "Budget and lifestyle".
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed (112 unit tests). Figma screenshots of the profile and route screens checked. not verified: backend (`api-contracts.md` and the commute code still use the legacy monthly DTO, listed as Aaron's migration), the chip spacing details from the Figma update, the emulator after this change.
- **What the human changed or rejected:** Ary changed the product (PRD v0.3). TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the example fares in Figma (RM 1.15, RM 13, RM 6 per trip) are mock values, not sourced; Figma "Evaluate unit · Result" still shows "True monthly cost", "Your monthly cost" and "Share of utilities"; ClickUp is not changed; the survey already sent still mentions a class timetable and the true monthly cost.

## 2026-11-11 · Finish the PRD v0.3 follow-ups (spacing, Figma, per-trip engine) · FR1.3, FR4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** "beresin semuanya": finish what was left after the PRD v0.3 update.
- **Prompts (exact):**
  > beresin semuanya lha
- **Output used:** profile step 1 checked on the emulator against Figma (chip spacing, "1 of 3"); Figma "Evaluate unit · Result" no longer has "True monthly cost" (it shows rent, utilities estimate and commute per trip separately); `calculatePerTripCommute` and 11 Jest tests added next to the legacy `calculateCommute` (not wired to the callable, preview or Android parser); E3.3 subtask 6 added.
- **How it was tested or checked:** `npm test` in `backend/functions` passed (17 suites, 391 tests). The Android checks passed earlier the same day (112 tests); nothing in `android/` changed after that. not verified: the other steps and the colour of the Figma change on a device.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary). Core logic: be able to explain why an unknown fare is not ranked as free.
- **Limits:** the callable, `commute-preview.js`, `api-contracts.md` and the Android `CommuteSummary` parser still use the legacy monthly format (Aaron's migration); ClickUp could not be updated (daily limit, about 2 hours to reset); the Figma mock fares are not sourced.

## 2026-11-11 · Update all Figma screens to PRD v0.3 · FR4, FR2.4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** bring every Figma screen and the design system in line with the per-trip commute decision.
- **Prompts (exact):**
  > coba update juga ya semuanya di figma
- **Output used:** UI Design page: the six "true cost" lines on Room cards (Rooms, Home, Filters, Saved rooms, Start flows) now show one-way time and fare; Home commute line, Welcome tagline and the Profile 1 subtitle follow the new copy; the four housemate posts no longer mention classes; "Compare Units" rows "X pays" (a monthly total) became "X's room" with the Room price, and "travels" became "trip". Design System page: Room card and Commute option samples, and the two component descriptions, rewritten; "Upload your class timetable" changed to "Upload your tenancy agreement". Earlier the same day: profile frames, Evaluate unit result, route page.
- **How it was tested or checked:** a text search of both pages for "true", "monthly", "on time", "class", "timetable" and "leave by" finds no commute-related leftovers; screenshots of Rooms, Compare Units and the Housing components section checked. not verified: the other Start flow frames were checked by text only, not by screenshot.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** all fares in Figma (RM 1.15, 2.30, 13, 6 per trip) and the Compare Units room prices (385, 550, 715, 420, 600, 780) are mock values, not sourced; the Peak / Off-peak toggle stays in the Room details design although the PRD makes it P1.

## 2026-11-11 · "Rooms in this unit": chevron and a marker for the viewed room · E4.1 (FR2.2)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** Ary asked whether the cards should show Units instead of Rooms; after the answer (Room stays the main card, a Unit card belongs to the P1 "Whole units" tab) Ary chose to improve the "Rooms in this unit" section on Room details.
- **Prompts (exact):**
  > ini kok cuma ada room doang dh di figma, kenapa gaada yang unit, kan host listingnya unit gitu jadi yang ditampilin emang bagusan room gitu ya? ga lebih bagus unit gitu

  > kayak gini aja "Rooms in the Unit" kalau misalnya vacant itu dia ada chevron buat arahin ke room detail itu dan tambahin kayak pembeda yang ngasih tau bahwa yang lagi dia liat sekarang itu yang mana di section rooms in the unit itu
- **Output used:** Figma, four frames (Room details, Send request, Report listing, Start · Room details → message): the section has Master (occupied, no chevron), Medium (vacant, chevron) and Small (vacant, the viewed room: tinted row and "Vacant · viewing now"); prices set to RM 650, 400 and 450 so they add up to RM 1,500 and match the RM 450 headline; the vacant and viewed rows were detached from the List row component to hold the chevron. E4.1 task: constraint and one Acceptance check.
- **How it was tested or checked:** screenshots of the section in Room details. not verified: the other three frames by screenshot, and the Design System sample of this section, which was not changed.
- **What the human changed or rejected:** Ary chose this design over a Unit card. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the prices (Medium RM 400 below Small RM 450) are mock values; a Unit card for the "Whole units" tab is not designed.

## 2026-11-11 · Commute: the transit row opens the route page · FR4.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** decide when the "Commute options" page opens; Ary pointed out there are only four modes, so the room details should lead straight to the Public transport route page.
- **Prompts (exact):**
  > page commute options kebuka pas user klik apa

  > opsinya emang cuma 4 kan ya, jadi dari detail room langsung ngarah ke page public transport route gitu kali ya
- **Output used:** Figma: the frame "Commute options" deleted (it duplicated the card on Room details); prototype links from the "LRT + UM shuttle" row in "Room details" and "Start · Room details → message" to "Public transport route", and a Back action on that page's back button. Docs: DESIGN.md Room Details Flow, PRD FR4.3 and the Room details row, E4.2 subtask 2.
- **How it was tested or checked:** the links were set through the Figma API and the frame list was read back; not verified: the prototype was not played, and the transit row has no visible hint that it opens (no chevron yet).
- **What the human changed or rejected:** Ary decided the flow. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** P0 still needs the route page to show text instructions only; the map and timeline are P1 (E4.4).

## 2026-11-11 · Chevron on the transit row · FR4.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** show that the "LRT + UM shuttle" row can be opened.
- **Prompts (exact):**
  > tambahin chevron di baris transport
- **Output used:** Figma, four frames (Room details, Send request, Report listing, Start · Room details → message): a chevron at the end of the transit row (the row was detached from the Commute option component to hold it), the other mode rows get a 52 dp right padding so the price column lines up, and the prototype link to "Public transport route" was set again on the detached rows. E4.2 subtask 2 mentions the chevron.
- **How it was tested or checked:** screenshot of the commute list; the reaction count and destination of one row read back. not verified: the prototype was not played; the Android `CommuteOptionView` has no chevron yet.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the Design System "Commute option" component does not have the chevron, so new screens built from it will not either.

## 2026-11-11 · Undo the chevron on the transit row · FR4.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** Ary asked to go back to the version before the chevron was added in Figma.
- **Prompts (exact):**
  > gausah balikin ke sebelumnya dh

  > maksud aku balikin ke sebelum ditambahin chevron di figma
- **Output used:** Figma, four frames: the detached transit rows replaced by Commute option component instances (Selected = True, same texts), no chevron; the other rows' right padding back to 16; the prototype link to "Public transport route" kept. E4.2 subtask 2 no longer mentions the chevron. The "Rooms in this unit" chevrons and the viewing marker were not touched.
- **How it was tested or checked:** screenshot of the commute list; the texts of all four new instances read back and none holds a chevron. not verified: the prototype was not played; my first reply ("gausah balikin") misread the request and nothing was changed then.
- **What the human changed or rejected:** Ary rejected the chevron on the transit row. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the transit row now has no visible hint that it opens the route page.

## 2026-11-11 · Remove Peak / Off-peak: estimates are for leaving now · FR4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** Ary removed the Peak / Off-peak choice; the price and time depend on the time of use ("sekarang").
- **Prompts (exact):**
  > peak off peak apus aja soalnya ribet deh, jadi harganya itu tergantung waktu sekarang
- **Output used:** PRD (11 Nov 2026 decision): FR4.3 and the P0/P1 table without Peak / Off-peak and surge ranges; the acceptance line now says every estimate is for leaving now, labelled "Estimated for leaving now", that time and fare change with the clock only where the data supports it (scheduled transit, sourced fare rules), that road times without live traffic are typical times, and that the route cache key uses a time-of-day bucket. GLOSSARY ("Peak and off-peak" marked removed), ASSUMPTIONS (#2 dropped, #3 without the peak ratio, no reference departure), USER-FLOW, task files E3.3, E4.2, E4.4 (renamed "Public transport route page"), E0.2, T1.2, T1.3 (#2 dropped), TASK.md and the owner files of Ary, Aly and Aaron. Figma: the Peak / Off-peak toggles removed in Send request, Report listing, Public transport route, Start · Room details → message and the Design System sample; the Home badge "Peak" became "Leaving now"; "Estimate, surge at peak" became "Estimate, fare formula"; "Weekday morning · estimate" became "Estimated for leaving now".
- **How it was tested or checked:** a search of PRD, tasks, owner files, Figma (both pages) and the Android and backend code finds no Peak / Off-peak left except the lines that record its removal. Screenshots of Home and the route page. not verified: the survey already sent still asks about peak hours (E5 to E7); `api-contracts.md` was not changed (it describes a "reference departure" for the legacy format); Aly and Aaron were not told, so assumption #2 work may already have started.
- **What the human changed or rejected:** Ary removed Peak / Off-peak. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain how "leaving now" works when the free routing has no live traffic.
- **Limits:** the "leaving now" rule means the same Room card can show a different time later; whether the Room card list refreshes the commute or caches it by time bucket is not decided (Aaron, E3.3).

## 2026-11-11 · Saved rooms follow the latest Room card · FR2.2

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** make the content of the "Saved rooms" list match the newest Room card.
- **Prompts (exact):**
  > page saved room list nya tolong diupdate contentnya jadi kayak card room yang terbaru
- **Output used:** Figma "Saved rooms": each row now has the Room card fields: room type badge on the photo (Small, Medium, Master), the price without "/mo", the title with the building, the meta line, the blue one-way time and fare line and "Listed 3d ago"; the three rooms are RM 450, 480 and 550 from the Rooms screen. The badge got a white fill so it shows on the light photo. Also fixed on the "Rooms" screen: two cards showed the badge "Small" although their titles say Medium and Master.
- **How it was tested or checked:** screenshots of the Saved rooms frame. not verified: the Design System sample of the row card ("Listing cards") was not changed; the bookmark icon was kept as it was.
- **What the human changed or rejected:** TODO (Ary)
- **What the human understands and can explain:** TODO (Ary)
- **Limits:** the data are mock values; the row layout (96 dp photo, text column) was kept, it only follows the card's content.

## 2026-11-11 · Room card content: price, Building, area, time to faculty, listed time · FR2.2, FR4.2

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** change the content of the Room card (and the Saved rooms rows) to five items.
- **Prompts (exact):**
  > (pasted) RM 450 / Residensi Pantai Dalam / Jalan Oramga no32 / 12 min (500m) to your faculty / Listed 3d ago

  > Kontennya juga kayak diatas. Harga, Nama Apartement, Lokasi, Waktu untuk ke fakulti, Waktu di listingnya
- **Output used:** Figma (UI Design and Design System pages): every Room card and saved row now shows the price without "/mo", the Building name as the title (the room type stays as a badge on the photo), the area as the location, "N min (X km) to your faculty" and "Listed 3d ago"; the fare is gone from the card; badges hug their text. PRD (the search-results rule, FR4.2, the design principle and the user story), `DESIGN.md` Room card row, E3.1 and E3.4 updated.
- **How it was tested or checked:** screenshots of the Rooms and Saved rooms frames; a text rewrite counted 18 titles, 18 meta lines and 17 time lines. not verified: the Android `RoomCardView` still has the previous slots (price, title, meta, commute) and was not changed; the Design System sample of the row card was not changed; the other frames by screenshot.
- **What the human changed or rejected:** Ary decided the five items. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the location is the area name (for example "Pantai Dalam"), not the street and house number from the pasted example, because the PRD hides the address until a Connection; the distances (6.1, 4.8, 7.4, 8.9, 5.6 and 4.2 km) are mock values, and the 34 min in the card is the transit time while Room details also lists a walk of 48 min.

## 2026-11-11 · No commute card on Home; commute lives in the Unit tab · FR4, E7.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** Ary removed the "Next class" commute card from Home and asked to note that the commute detail is for the Unit tab once the student's home is fixed.
- **Prompts (exact):**
  > card next class aku apus ya, buat user kalau mau liat detail commute itu nanti di unit aja kalau udh fix dia tinggal dimana tolong dicatet ya
- **Output used:** found that Ary had already removed the card in Figma (Home and the Start frames) and changed the Room card content, so no Figma change was made. Docs: PRD (Unit row in the tab table, journey step 6, and an acceptance line: no commute card on Home; the Unit tab shows "Getting to [faculty]" with the Unit as origin only for a Household member), DESIGN.md (Core Tabs), E7.3 (new subtask 3 and a constraint), E1.4 (constraint).
- **How it was tested or checked:** a text search of the UI page finds no "Your commute · FSKTM" left; a screenshot of Home. not verified: the Unit tab screen was not designed; the other Start frames were not viewed.
- **What the human changed or rejected:** Ary decided where the commute is seen. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** "home is fixed" is taken to mean being a member of a Household (the only place the app knows the student's Unit); a student who lives somewhere not in Jiran has no commute guide for it. The Room cards in Figma now show "Jalan Oramga no32", a street and house number: that is Ary's edit and the PRD still says the address is hidden until a Connection (see the open point in the previous entry).

## 2026-11-11 · The Building's street address is public · FR2.1, NFR2

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** change the PRD so the Room card can show the apartment's address ("Jalan Oramga no32").
- **Prompts (exact):**
  > ubah prd, kan biar dia tau apartementnya itu alamatnya dimana bjir
- **Output used:** the rule is now: the Building's street address is public, only the exact Unit (block, floor, door number) is hidden until a Connection. PRD (Manual Listing fallback, Room card items, the approximate-location line, NFR2, design principle 4, data table with `buildingAddress`), GLOSSARY (Building), `AGENTS.md` (privacy rule), DESIGN.md (privacy by stage), schema (`buildingAddress` `[PROPOSED]`, `exactAddress` now the Unit), E3.1, E4.1 and a new E2.1 subtask 8 for Gathan and Aaron.
- **How it was tested or checked:** each replaced sentence was found exactly once; no code changed. not verified: the Rules, the Worker and the Post form do not have `buildingAddress` yet, so the Room card cannot show a real address until E2.1 subtask 8 is done; the PRD can still conflict with older mentions of "address" in files I did not search (for example the survey).
- **What the human changed or rejected:** Ary decided the address rule. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the Building address is public but the Unit is not, and what a stranger could still learn.
- **Limits:** a Host who is the only resident of a small building could be identified by its street address; this was not discussed.

## 2026-11-11 · Public `buildingAddress` in the Worker, Rules and Post form · FR2.1, NFR2, E2.1 subtask 8

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** implement the public Building street address in the code, after the PRD change.
- **Prompts (exact):**
  > ya coba update sekalian kodenya
- **Output used:** `backend/functions/src/listing-draft.js` (required text, up to 300 characters, written to the public listing and never to `private/location`), `backend/firestore.rules` (`validListing` allows an optional `buildingAddress` up to 300 characters, so older drafts still validate), Jest tests in `listing-draft.test.js` and `listing-worker.test.js`, new cases in `backend/scripts/product-rules-smoke.cjs`; Android `ListingForm`, Post form layout, strings, `PostListingFragment`, `PostListingViewModel.endSession`, `ListingFormTest`, `PostListingViewModelTest`, `PostListingScreenTest` (only edited, not run); E2.1 subtask 8, `schema.md` and the TASK.md row.
- **How it was tested or checked:** `npm test` in `backend/functions`: 17 suites, 395 tests pass. Emulator smoke suite (`firebase emulators:exec --only auth,firestore,functions`): passed, 83 denials and 43 permissions in the Rules check; the storage emulator was left out because port 9199 was held by another process. `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug`: build successful. not verified: the Post screen on an emulator or device; the instrumented `PostListingScreenTest`; the live Worker; storage-emulator part of the smoke test.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the Rules treat the field as optional but the Worker requires it, and which collection each of `buildingAddress` and `exactAddress` lives in.
- **Limits:** the Rules change **needs human review** by Gathan. Listings made before this change have no `buildingAddress`, so the Room card must handle a missing value. The Android Room card (`RoomCardView`) and the read side do not use the field yet.

## 2026-11-11 · Profile gate fails closed · FR1.3, E1.3 item 7

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** close the gap found in review: the shell opened the tabs when the profile check failed, and skipped the check when the activity was restored.
- **Prompts (exact):**
  > ok coba perbaiki yang no 1, dan intinya tuh aku pengen kalau misalnya secara kode itu udh oke berarti tinggal di test aja
- **Output used:** `ui/shell/ProfileGate.java` (new, decides tabs / setup / failed, ignores late answers), `ShellActivity.java` (uses it, hides the tabs until the answer, retry screen, check also on restore), `activity_shell.xml` (retry overlay), `strings_profile.xml` (three messages and the button), `ProfileGateTest` (7 tests), E1.3 task file.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug`: build successful, 119 unit tests, 0 failures. not verified: the retry screen and the restore case on an emulator or device; the layout was not viewed.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to explain why a failed check must not open the tabs, and why late answers are ignored after the screen is destroyed.
- **Limits:** the gate runs again on every activity recreation (for example rotation), so the tabs are briefly hidden then. The gate only protects the screens; the Security Rules still protect the data. Needs human review.

## 2026-11-11 · Rule: `qa` means the code is right; QA runs the tests · TASK.md, AGENTS.md

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** record how a task reaches `qa`.
- **Prompts (exact):**
  > intinya status naik ke qa itu kalau udh oke secara kode, yang berhubungan dengan testing itu harus dilakuin sama qa ya coba tolong catet ini
- **Output used:** `TASK.md` (the `qa` row and a "Who tests" paragraph), `AGENTS.md` (task tracking rule), E1.3 (status `inreview` because all its work is built and PR #17 is open; the manual checks are listed for QA).
- **How it was tested or checked:** text only; no code changed. not verified: whether Aly agrees with taking the manual checks; other task files with `inprogress` and only test gaps were not reviewed against the new rule.
- **What the human changed or rejected:** Ary decided the rule. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the Definition of Done is unchanged (Aly's QA passed, Acceptance ticked); only the point where the status becomes `qa` moved.

## 2026-11-11 · Check every task status against the new `qa` rule · TASK.md, E1.4

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** go through all tasks marked `inprogress` or `qa` and see which ones the rule "qa = the code is right, QA runs the tests" changes.
- **Prompts (exact):**
  > ya cek satu per satu dedngan aturan baru ini
- **Output used:** only E1.4 changed: `qa` to `inreview` because its profile gate is still in the open PR #17; its Post button item and acceptance line were out of date (the button opens New Listing since E2.1) and were corrected.
- **How it was tested or checked:** the subtask markers (✅ ⚠️ ⬜ ❓) of the 17 `inprogress` task files with a work item, the 7 tracks, and the 5 `qa` task files were read; the Post button was checked in `origin/main`'s `ShellActivity`. not verified: whether the ⚠️ and ⬜ markers in each task file are current (they were taken as written); the `backlog` tasks were not read.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** every other `inprogress` task still has at least one ⬜ item that is code or a document to write (for example E2.1 subtasks 5 to 7, E3.3 subtasks 2 and 5, the screens of E4.2, E5.x, E6.1, E9.x), so none moves to `qa`. E1.1 and E1.2 stay `qa`: their only ⬜ is a real-device test, which is QA's job.

## 2026-11-11 · Match the faculty list with UM's website · FR1.3, E1.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** compare the faculty list in the app with the 17 study areas Ary copied from UM's website.
- **Prompts (exact):**
  > coba cocokin lha sama um, ini list yang aku dapet dari web nya (with a screenshot of 17 study areas)
- **Output used:** `arrays_profile.xml`: "Nursing" added (key, label and full name, after Pharmacy); the file comment and the E1.3 constraint now say where the list comes from.
- **How it was tested or checked:** the 16 existing entries were compared one by one with the 17 names in the screenshot: all matched except that Nursing was missing. `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug`: build successful. not verified: the official full names ("Faculty of ...", "Academy of ..."); the screenshot has no source link; the Figma faculty list and the Nursing chip on a screen.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the website calls these "study areas", not faculties; a student of a faculty that sits inside one area (for example Computer Science and IT) picks the area's name. If UM has faculties that are not on this list, they are missing.

## 2026-11-11 · Source link for the faculty list · FR1.3, E1.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** read the page Ary gave and confirm the faculty list.
- **Prompts (exact):**
  > https://study.um.edu.my/undergraduate-faculties
- **Output used:** the page was fetched with curl and its text read: the same 17 names as the screenshot, "Last Updated: Jun 24, 2026", and links named `faculty-of-...` for 15 of them. "Nursing" is now "Faculty of Nursing"; the source link and date are in `arrays_profile.xml` and the E1.3 constraint.
- **How it was tested or checked:** read the page text and its link names. not verified: the official full names beyond the link names; "Academy of Islamic Studies" and "Academy of Malay Studies" (no faculty link for them on the page); the Android checks pass after this change.
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the page lists undergraduate faculties only; postgraduate students choose from the same list.

## 2026-11-11 · Wire the per-trip commute function into the callable, service, Android parser and docs · FR4.1-4.3, E3.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** replace the legacy monthly / before-class commute code with `calculatePerTripCommute`, which was written earlier but not used.
- **Prompts (exact):**
  > maksudnya backend per trip belum tersambung itu maksudnya apa

  > yaudh coba update dlu aja ke fungsi yang terbaru
- **Output used:** backend: `commute.js` (legacy `calculateCommute` removed), `commute-service.js` (request is `{listingId, roomId}` plus an optional reference departure, default now; no rent, utilities, class time or trips per month), `commute-preview.js`, fixtures (`per-trip`, `no-known-fare`, `missing-data`), tests (`commute.test.js` rewritten, pricing, emissions, preview and service tests changed), `commute-smoke.cjs`, k6 and fixture-contract files. Android: `CommuteSummary` (new parser with the lowest-fare and comparison checks), `CommutePreviewActivity`, strings, `CommuteSummaryTest` (rewritten), `CommuteIntegrationTest`. Docs: `api-contracts.md`, E3.3, E3.4, E4.2, the file-migration plan.
- **How it was tested or checked:** `npm test` in `backend/functions`: 17 suites, 377 tests pass; `node --test backend/tests/load/fixture-contract.test.mjs`: 7 pass; emulator smoke (auth, firestore, functions): all PASS including the commute callable; `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug assembleDebugAndroidTest`: build successful, 122 unit tests, 0 failures. not verified: the instrumented `CommuteIntegrationTest` was compiled but not run; the k6 load run; the preview screen on a device; the Cloudflare Worker (it does not use these files yet).
- **What the human changed or rejected:** TODO (Ary).
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the free walking route is selected whenever it is available (lowest known fare, as in the PRD), and what `comparisonLimited` means.
- **Limits:** this changes Aaron's code and the response shape of the local fixture callable; Aaron should read `commute.js` and `commute-service.js`. The scenario IDs changed (`on-time` and `no-on-time-route` are gone), so any other client that sends them now gets `invalid-argument`. The internal service is still not exported. Needs human review.

## 2026-11-11 · Drop the area page and building reviews (old FR2.3, E9.4) · PRD, schema, Rules, Figma

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** remove the area page and building reviews, which need data that does not exist before there are residents.
- **Prompts (exact):**
  > tapi itu agak susah gasi dapetin data datanya? apa better kita apus aja

  > ya tolong diapus
- **Output used:** PRD (FR2.3 removed, the old FR2.4 Evaluate unit renumbered to FR2.3 in every file except the AI logs, `reviews` and `areas.rentLowSen`/`rentHighSen` removed from the data table, risks), GLOSSARY, DESIGN.md, `schema.md` (section 2.6 replaced by a removal note), `firestore.rules` (the `reviews` rule removed, so the collection is denied by default), E9.4 rewritten as `dropped` with the reason, E2.1, E2.4, TASK.md, team READMEs, `clickup-sync-todo.md`. Figma: the frames "Area page" and "Building review", and the "Areas" tab in three Rooms frames and in the Design System tab row, were deleted.
- **How it was tested or checked:** `npm test` (377 pass) and the emulator smoke suite (83 denials, 43 permissions, all PASS) after the Rules change; text search for "area page" and "building review" finds only the dropped task and the removal notes; a screenshot of the Rooms frame shows the tabs "Rooms" and "Whole units". not verified: Android code and layouts were not searched for the area page (an earlier search of `android/` found none); the other Rooms frames were not viewed; ClickUp was not changed (limit); the report text and survey were not searched for "area page" beyond the docs folder.
- **What the human changed or rejected:** Ary decided to drop the feature. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why FR2.4 became FR2.3 and that Module 2 still has three requirements.
- **Limits:** this removes a requirement and a Rules block that Gathan wrote (E0.5), so Gathan must read the change. Deleted Figma frames can only be recovered from Figma's version history. The FR renumbering also changes the requirement ID used in Gathan's and Ary's task files.

## 2026-11-11 · Entry points for upload contract, form a group and arrange a viewing (chat + drawer) · FR3.3, FR6.2, E5.2

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** the group prompt card and the viewing / agreement cards appeared in the chat "out of nowhere"; add real entry points, as a drawer.
- **Prompts (exact):**
  > sama juga tolong tambahin pintu masuk buat upload kontrak, buat grup dan janjian appointment. mungkin kayak drawer gitu karena yang sekarang ada di figma itu out of no where ada notifikasi pop up di pagenya
- **Output used:** Figma (UI Design, Request and Chat Flow): a **+** button in the chat composer (three chat frames); a new drawer frame "Chat · Add to chat" (Arrange a viewing, Upload a contract, Form a group), a new sheet frame "Chat · Arrange a viewing" (three time options, Send proposal), both built from the Report listing sheet pattern; the "Rent a Unit together?" prompt card was removed from "Start · Chat → + → Form a group" and its tap highlight now points at the +; prototype links: + to the drawer, rows to the viewing sheet, Contract check · Upload and Group, close and scrim back; the flow header was made wider. Docs: PRD (contract check entry), DESIGN.md (flow table and a "Chat actions" paragraph), USER-FLOW, E5.2 (new work item, subtask 3 and an acceptance line), E8.3 and E9.2 (entry points).
- **How it was tested or checked:** screenshots of the Start group frame, the drawer and the viewing sheet. not verified: the prototype links were set but not played; the other chat frames (Messages, Confirm move-in) were not changed or viewed; the sample times ("Saturday, 10:00" and so on) are mock values; nothing was built in Android.
- **What the human changed or rejected:** Ary chose a drawer. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain what each of the three actions opens and why none is pushed as a card.
- **Limits:** I took "drawer" to mean a bottom sheet opened from a + button in the chat composer; if a side drawer or a menu on the top bar was meant, the frames need redoing. "Upload a contract" opens the private Contract check upload (your own copy, not shared in the chat), because E5.2 says contract files never go into a chat. The "Check contract" card for an agreement the Host shares is still in the chat frames. Group still opens the existing "Group" screen, not a new "choose people" step.

## 2026-11-12 · Start a Household without a Listing; more ways into a Group · FR5.1, E5.3, E7.2, E7.3

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** a Group should not only start from chat, and a student who only wants the Household features needs a way to create one.
- **Prompts (exact):**
  > sama juga mungkin buat group itu ga harus selau di chat gasi, mungkin ada juga user yang pengen make jiran buat fitur householdnya, nah itu gimana?

  > maksud usulan tampilan kamu apa, jelasin yang lengkap dong

  > ya kerjakan, area opsional, update di figma ya
- **Output used:** Figma (UI Design): new frames "Unit · No Household" (Core Tabs row; three cards: create, join, rent together), "Household · Create" (Unit name, optional area), "Household · Invite housemates" (6-character code, valid 7 days, Share code) and "Household · Join with a code"; prototype links between them, to Group and to Household · Welcome; two flow headers made wider. Docs: PRD FR5.1 and acceptance lines, DESIGN.md flow table, USER-FLOW (new diagram), E5.3, E7.2, E7.3, E9.2, E1.4, and `schema.md` (`households.name` and `areaId`, `[PROPOSED]`).
- **How it was tested or checked:** screenshots of the four new frames. not verified: the prototype links were not played; no Android code or Rules were changed, so Gathan has not checked that one student can create a Household alone; the sample code "K7M2QX" and unit name are mock values.
- **What the human changed or rejected:** Ary decided the area is optional. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain who becomes Host in a Household created directly and why rent there is split equally or by custom amounts.
- **Limits:** I did not add a "Form a group" button to Match results or the Match profile, although I first proposed it: those screens only offer "Connect", and a Group is made of people the student is already connected with, so Group stays in the chat + menu and the Unit tab card. The Unit tab card opens the existing Group screen, not a new "pick people" step. A Household made without a Listing has no Room prices; E7.3 already allows equal and custom splits, but this was not checked in code. The `inviteCode` already existed in the schema; its 6 characters and 7 days come from E5.3.

## 2026-11-12 · Rename the "LRT + UM shuttle" label to "Public transport"

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** use one label for the public transport mode.
- **Prompts (exact):**
  > sama tolong ganti semua text yang LRT+UM Shuttle jadi public transport
- **Output used:** Figma: ten text nodes "LRT + UM shuttle" (the Commute option component, the Welcome, Send request, Request · Host view, Report listing, Start · Room details and Public transport route frames, the Housing components instance) and "Commute · LRT + UM shuttle" in Evaluate unit · Result now say "Public transport"; one "Public Transportation" in Room details was made "Public transport" too. Repo: DESIGN.md and E4.2 (the row that opens the route page), `strings_auth.xml`, `component_commute_option.xml` (tools text) and the debug gallery layout.
- **How it was tested or checked:** a text search of the whole Figma file finds no "LRT + UM shuttle" left; a text search of the repo finds none outside the AI logs; `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passes. not verified: the frames were not viewed after the change.
- **What the human changed or rejected:** Ary decided the label. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** I left descriptive text unchanged: the PRD, tasks and survey still say "bus/LRT plus UM shuttle" where they describe what the mode includes, and the route steps on the Public transport route page ("UM shuttle to FSKTM", "Walk to Kerinchi LRT") are real steps, not the mode label.

## 2026-11-12 · What the Match tab shows when the student has a Group · FR3.3, E6.4, E9.2

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** design and write down the Match tab for a student who already formed a Group.
- **Prompts (exact):**
  > kalau misalnya dia udh buat group nanti berarti tampilan match nya ini gimana ya pas dia buka

  > Ya, kerjakan
- **Output used:** Figma: new frame "Match · Your group" (a "Your group · 3 of 4 in" card with avatars, "Choosing a Unit · 1 spot left" and an Open group button linked to the Group screen; the list titled "Find more housemates"; an "In a group" badge on one Match; Match flow header widened). Docs: PRD Module 3 (one Group at a time, the card, the full-Group state, the badge, "Open group" wording), DESIGN.md, USER-FLOW, E6.4, E9.2.
- **How it was tested or checked:** screenshot of the new frame. not verified: the prototype link was not played; the "Your group is full" state and the "Open group" wording in the chat drawer and Unit tab were written down but not drawn; no code or Rules were changed.
- **What the human changed or rejected:** Ary approved the proposal. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why a student can be in only one Group and what each Match tab state shows.
- **Limits:** "one Group at a time" is a new rule I proposed and Ary accepted by saying yes; Gathan has not checked that the Rules can enforce it. E6.4 still mentions class-day overlap, which is stale since the timetable was removed (E6.2 subtask 2 covers the code, not this task text).

## 2026-11-12 · Arrange a viewing: date picker and time picker instead of fixed options

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** replace the three fixed time options with real pickers.
- **Prompts (exact):**
  > Sama arrange a viewing itu buat jadi date picker sama time picker aja jangan fixed kayak gitu dong
- **Output used:** Figma (Request and Chat Flow): the "Chat · Arrange a viewing" sheet now has a Date field ("Saturday, 14 Nov", calendar icon) and a Time field ("10:00", clock icon); two new frames, "Chat · Pick a date" (a November 2026 calendar, past days greyed out, today outlined, 14 selected) and "Chat · Pick a time" (hour and minute wheels, 24-hour); prototype links from the fields to the pickers and from OK and Cancel back to the sheet; the flow header was widened. Docs: DESIGN.md and E5.2.
- **How it was tested or checked:** screenshots of the sheet and both pickers. not verified: the links were not played; the pickers are drawn as custom dialogs, so the Android build must choose between the Material date and time pickers (the dial style differs from the wheels drawn here); the "greyed out past dates" rule is a design choice, not in the PRD; the dates are mock values.
- **What the human changed or rejected:** Ary asked for pickers. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** a viewing cannot be proposed at a time that has passed, and I did not decide a latest date or office hours. The earlier "three time options" in the AI log entry of 11 Nov no longer apply.

## 2026-11-12 · Rearrange the Figma UI page into end-to-end journeys, and add the missing pages

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** the UI Design page was hard to read as a whole; arrange it as end-to-end flows without changing any screen, and add pages that were missing.
- **Prompts (exact):**
  > Kayaknya perlu dibuat flownya end to end biar lebih kebayang karena struktur di figma sekarang masih berantakan. Coba di ubah strukturnya ya di figma. Inget keep the design, dont change anything just rearrange. if theres a page that you think kita harus add, tambahin aja ya.
- **Output used:** Figma (UI Design): the existing frames, flow headers, START labels and arrows were moved (positions only) into six journey sections, each flow in a row with its screens in order and no gaps; the old stray items moved (the cover to the top, the reference screenshot into a References section); a Journey map frame at the top; one new row header "Household Setup Flow". New pages (cloned from existing screens, same components): "Messages · Chats", "Group · Invite a housemate", "Household · Settings", "Rooms · No results". New prototype links: Invite a housemate row to its page, the gear in both Unit frames to Household settings, Move out row to Move out, the Chats and Requests tabs to each other, a Chats row to the chat; the pickers' scrims now return to the Arrange a viewing sheet (they pointed at the drawer by mistake). Docs: DESIGN.md (Figma structure and flow table).
- **How it was tested or checked:** a script checked that every top-level frame is inside a section and that no frame changed size (all 390 wide, 844, 1480 or 1300 high); screenshots of sections 2 and 5, the four new pages and the Journey map. not verified: sections 1, 3, 4 and 6 were not viewed one by one; the new pages and the prototype links were not played; the rest of the prototype still has few links between screens.
- **What the human changed or rejected:** Ary asked for the rearrangement and the additions. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain which journey each flow belongs to and which four pages were added and why.
- **Limits:** the order of flows and the grouping into journeys are my judgment (for example Contract Check sits in journey 2 and Evaluate Unit in journey 4). The Journey map is a drawing, not linked to the sections. The four new pages are first drafts with sample text and need Ary's review; "Rooms · No results" is not linked from Filters yet. DESIGN.md says 14 flows, counting Household Setup as new.

## 2026-11-12 · Housemates tab and Groups with a code or link and a custom size

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** explain the Group flow, then make the Match tab more general (Housemates), let a Group be created there and joined with a code or link, and let the creator choose the maximum size instead of a fixed 4.
- **Prompts (exact):**
  > yang group flow masi agak bingungin, coba kamu jelasin
  >
  > kayaknya menu match kurang cocok deh, bisa lebih yang general ga. jad di menu ini bisa langsung buat group juga dan bisa invite menggunakan link atau kode.
  >
  > dan maksimal anggota  group jangan 4 dong, bisa custom berapa max nya
  
  Answers Ary chose to three questions: tab name "Housemates", the creator can set the maximum up to 8, and a join request needs the creator's acceptance.
- **Output used:** docs (PRD FR3.3 and Group rules, GLOSSARY Group and Group code, USER-FLOW section 4, DESIGN flows, schema `groups` fields and the proposed `requestToJoinGroup`, TASKS E5.2, E6.1, E6.4, E9.2, Gathan README row, clickup-sync-todo); Android strings and tab width (`tab_match`, `match_title`, `component_tab_bar.xml`); Figma: tab label in the Tab bar component, Create a group and Join with a code cards on Housemates · Start, new frames Group · Create, Group · Join requests, Group · Join with a code, Group · Request sent, a new row "Join a Group Flow", and edited Invite to the group, Your group and Group screens, with prototype links.
- **How it was tested or checked:** `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` passed; screenshots of the new and edited frames. not verified: the Join a Group row could not be screenshotted in place (the screenshot tool returned blank for frames below the old section edge), so its frames were checked after moving them temporarily; the prototype links were not played; the longer "Housemates" tab label was not seen on a device.
- **What the human changed or rejected:** Ary asked for the change and chose the three answers above. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain who can create, invite and accept, why 2 to 8, and why joining a Group does not open contact details.
- **Limits:** the 2 to 8 range comes from Ary's choice, not from a survey. "Joining does not open contact details; members become Connections only through the normal request" and "a Group of one is dissolved when its code expires" are my proposals, not yet agreed with Gathan. The Rules still allow any member to update `groups`; `maxMembers`, the code fields and `requestToJoinGroup` do not exist in the Rules or in code yet. The Journey map chip still says "Group Flow" for both rows.
## 2026-11-12 · Extra onboarding pages (tried, then dropped)

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Mobbin connector, Figma connector
- **Task:** add two onboarding screens that explain more features, in the style of the Welcome screen.
- **Prompts (exact):**
  > coba onboarding ditambah 2 screen lagi buat ngejelasin lebih banyak tentang fitur fitur jiran. aku suka design yang kamu buat untuk onboarding
  >
  > dibedain dong assetsnya coba search for mobbin for onboarding inspiration
  >
  > no, jelek banget itu bisa dibuat graphicnya lebih kayak page yang ketiga, make UI component yang kita punya aja jangan buat ilustrasi baru
  >
  > jangan stuck di card card gitu dong yang lain, ganti lagi untuk page 1 dan 2
  >
  > masih gasuka, udh deh satu aja biar simple yang page 3 ya
- **Output used:** none. Three versions of two intro frames were made in Figma (a card panel, drawn illustrations after two Mobbin searches, then component compositions) and all were rejected; the Figma frames and the doc changes were removed and the onboarding stays the single Welcome screen. Mobbin screens were only looked at, nothing was copied. These commits were later removed from the branch at Ary's request; this entry stays because the AI was used.
- **How it was tested or checked:** screenshots only. Nothing reached the repository.
- **What the human changed or rejected:** Ary rejected all three versions and chose the single Welcome page.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** none that remain in the repository.

## 2026-11-12 · Compare Units entry points and the Add another Unit page

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** answer where Compare Units is opened from, then add the missing entries and the missing "Add another Unit" page.
- **Prompts (exact):**
  > compare unit ini pintu masuknya dari man
  >
  > oke boleh sama page add another unit belum ada.
- **Output used:** Figma (Evaluate Unit Flow and Group Flow): a "Compare Units" row at the bottom of the Evaluate result ("Room prices"), a new frame "Group · Add another Unit" (Units in the comparison with Remove, Evaluate a Unit, Pick a Listing, a Compare 3 Units button), and prototype links: the Compare units button on Group and on the start frame, the new Result row, Add another Unit on the compare screen, Use Pantai Dalam back to Group, and the rows and buttons of the new page. The body spacing of the Result frame was reduced from 14 to 10 to fit the new row. Docs: TASKS/E9/E9.3.md (entries, page, one acceptance line), DESIGN.md Evaluate Unit Flow row.
- **How it was tested or checked:** screenshots of the new page and the Result screen. not verified: the links were not played; my first answer said "Use Pantai Dalam" and "Add another Unit" were on the Evaluate result, which was wrong, they are on the compare screen.
- **What the human changed or rejected:** Ary approved the proposal. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the "at most 3 Units" comes from the PRD ("two or three"); the sample Units on the new page reuse the names already in the compare screen. The row on the Result screen is always shown in the mockup; the proposal was to show it only when the group has two or more saved Units, which the app must decide.

## 2026-11-12 · Drop the utilities estimate

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5), Figma connector
- **Task:** discuss the "Share of utilities" line (it depends on use, so the data is hard to get), then remove it.
- **Prompts (exact):**
  > share of utilities itu kayaknya susah deh buat dapetin datanya karena itu bener bener tergantung pemakaian. menurut kamu gimana? coba discuss dlu jangan diapa apain
  >
  > hapus aja deh biar lebih simple
- **Output used:** Figma: the "Share of utilities" line is gone from the Evaluate result, "Utilities estimate RM 62 · deposit RM 900" became "Deposit RM 900" in the Send request and Report listing frames, a caption in the design system lost "utilities are split equally", and the sample switch "Utilities included" became "Furnished" (also in the Android debug component gallery). Docs: PRD (utilities not priced anywhere, `config` without `utilitiesEstimateSen`), schema.md (`utilitiesEstimateSen` removed), ASSUMPTIONS.md (#7 marked dropped), TASKS E0.6, E2.2, E2.4, T1.1, T1.2, T1.3, the Aly, Gathan and Aaron READMEs, clickup-sync-todo. The root README paragraph on the commute preview was still describing the old on-time scenario with rent, utilities and a RM612 total; it now describes the per-trip preview. Kept on purpose: the Household still splits real shared bills equally (FR5.1, E7), and the survey question C9 stays for the report.
- **How it was tested or checked:** screenshots of the Evaluate result and the Send request title block; `./gradlew lintDebug testDebugUnitTest checkstyleMain assembleDebug` (passed). not verified: ClickUp is not updated; Gathan has not seen the change to `config` in the schema.
- **What the human changed or rejected:** Ary chose to drop the estimate instead of replacing it with a yes or no "utilities included". TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the estimate was dropped and where utilities still appear (real bills in the Household).
- **Limits:** the ADR file still mentions `utilitiesShareSen` as an example field name because ADR.md is append-only; old plans in `docs/plans/` still describe the earlier calculation.

## 2026-11-12 · Fix the worker-feasibility CI check

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** CI check `worker-feasibility` failed on PR #17.
- **Prompts (exact):**
  > ya force push setelah itu fix CI ya
- **Output used:** the failed log showed two tests of `experiments/worker-feasibility` still calling the commute fixture with the old scenario `on-time` (removed in the per-trip migration, so the Worker returned 400) and one asserting `trueMonthlyCostSen` 61200. `test/worker.test.mjs` and `test/read-json.test.mjs` now use `per-trip` and assert no monthly total, `selectedMode` walking and the transit per-trip fare of 250 sen.
- **How it was tested or checked:** `npm test --prefix experiments/worker-feasibility`: 75 of 75 pass locally (before: 2 failed in CI). The CI result after the push is not recorded here.
- **What the human changed or rejected:** none. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why the Worker experiment broke when the fixtures changed.
- **Limits:** this is a fixture-only experiment; the production Worker does not use the per-trip format yet.

## 2026-11-12 · Squash the design and docs commits into one

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** many commits only recorded Figma and document updates; group them in one commit and keep the code commits separate.
- **Prompts (exact):**
  > coba kayaknya banyak commit ini sebenernya dalam bentuk design bukan dalam bentuk kode kan ya. coba klo cuma update design di figma, jadiin satu commit aja bisa ga
- **Output used:** the branch history was rebuilt so that 6 code commits keep only their code, and one "[Design]" commit holds every change to docs, task files, the Figma notes and the AI log. The final files are the same as before the rebuild except for this entry. The Figma file itself is not in git. The earlier history is kept in the local branch `backup/e1.3-before-design-squash`.
- **How it was tested or checked:** a diff of the code folders (android, backend, experiments) between the new and the old history was empty; the diff of everything else was only this entry. `npm test` and the Gradle checks were not re-run for this step because no file changed.
- **What the human changed or rejected:** Ary asked for the squash. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary). Be able to explain why code and design are separate commits.
- **Limits:** the task and docs updates of a code story now sit in the design commit, not next to its code commit; pushing needs a force-push.

## 2026-11-12 · Squash the whole PR history into four code commits and one design commit

- **Human owner:** Ary
- **AI tool and model:** Claude Code (Claude Sonnet 5.5)
- **Task:** the 25 older commits of PR #17 (10 to 11 Nov) had the same problem: 13 only changed docs or Figma notes, and the profile work contained steps that were later removed (a class timetable, then days on campus, then neither).
- **Prompts (exact):**
  > itu yang 25 commit 2 hari lalu gimana?
  >
  > Ya, siapkan
- **Output used:** the history of the branch was rebuilt on top of `main`: four code commits (`[E1.3]` profile setup, `[E2.1]` public Building address and the reviews rule, `[E3.3]` per-trip commute, `[E4.2]` label and tab renames) and one `[Design]` commit with every change to docs, task files, ADR, AGENTS and the AI log. The code files are identical to the previous history. The previous history is kept in the local branch `backup/e1.3-before-25-squash`.
- **How it was tested or checked:** a diff of android, backend and experiments between the new and the previous history was empty; everything else differs only by this entry. The tests were not re-run for this step because no file changed.
- **What the human changed or rejected:** Ary asked for the squash. TODO (Ary): anything else.
- **What the human understands and can explain:** TODO (Ary).
- **Limits:** the per-commit history of the profile work (timetable, campus days) is gone from git; the AI log and task files still describe it. Pushing needs a force-push.
