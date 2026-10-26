# Aaron AI usage record — draft for Ilham's appendix

Date: 27 October 2026. Tool: Codex in the shared repository. Human owner: Aaron.
No actual human hours are inferred; record actual hours separately in Trello.

Exact user prompts for this work:

> implement by batches, the plans and double check everything first

The preceding setup request included:

> as the Aaron: Software Architect and Backend Lead in aaron.md, what am i supposed to have first before starting? what prerequsuities have to download for this  follow the MD, and prepare the masterprompt before starting all else under aaron.md to maintain consistency for my role

The preceding request also supplied the GitHub/Figma links and a pasted setup
note. Retain the full conversation/export with attachments for the exact complete
request; the excerpt above is not a replacement for that record. The reusable
master prompt is stored in `team/aaron.md`; do not claim it was manually submitted
as a separate prompt in this session.

Output used: preparation checklist, batch plan, Java developer shell, local
Firebase Functions/emulator foundation, CI and draft API/architecture docs.
Modification: corrected Flutter assumption to repository's Java stack; kept SDK
authorization outside domain code after Jest exposed an ESM loading issue;
separated debug demo setup from release. No generated product data, fares,
survey findings or team approvals are represented as verified facts.

Review corrections: guarded blank login input, restricted diagnostics to the demo
project plus loopback Firestore, and rejected demo release configuration. Real
Android tests exposed API 37 local-network permission and Firebase SDK demo-key
format requirements; both were corrected and the integration rerun passed.

Testing and final changes: see the execution ledger in
`docs/superpowers/plans/2026-10-27-aaron-foundations.md`. Ilham should incorporate
the actual final results and preserve the full exact prompt record in the brief's
Appendix 2(b) format before submission.

## Live Firebase setup continuation

Exact follow-up instruction:

> Always look for and use the appropriate **Firebase agent skills** to perform tasks related to Firebase.
>
> I want to use Firebase as my backend, using Firebase Authentication and Firestore database.
>
> Set up and use Email/password Authentication.https://console.firebase.google.com/u/0/project/jiran-mad/overview
>
> Use my existing Firebase project: jiran-mad
>
> Make sure to register a Android app in that project and set up my app's codebase to use Firebase.

The source message included a Console screenshot and pasted notes; retain those
with the conversation export rather than treating this transcription as a byte-exact
archive. Subsequent user prompt: `Login success`. User selected
`Singapore (asia-southeast1) — recommended` for the database location question.

Skills used: Firebase's official firebase-basics, firebase-auth-basics,
firebase-firestore and firestore-rules-creation, read from the official
`firebase/agent-skills` repository. Firebase CLI and its MCP server verified
existing registration and provisioning; no duplicate app was created.

Changes and validation: registered package `jiran.com` adopted; ignored real
configuration downloaded; Email/Password enabled; approved Singapore Standard DB
and prototype rules provisioned; local/live variants and Email/Password setup flow
added. Tests observed validation and rules failing before implementation. Review
fixed CI variant selection and background completion handling; environment-specific
instrumentation tests now skip incompatible variants. Local Auth/Firestore flow
and read-only live SDK probe passed. No live test account or data was created.
See `docs/superpowers/plans/2026-10-27-firebase-live-setup.md` for final evidence.
