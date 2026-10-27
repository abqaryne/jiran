# Aaron AI usage record — draft for Ilham's appendix

Date: 27 October 2026. Tool: Codex in the shared repository. Human owner: Aaron.

## Android match-score handoff continuation

User steering: "continue" and "continue again", under the persistent Aaron goal.
Used official Firebase Auth guidance and the existing Java/JavaScript fixture
contract. No production backend migration or paid service. Added typed immutable
match parsing, a gated Java Firebase SDK repository and labelled developer preview
for score/reasons, overlap days, missing answers, weights and empty data.

Three positive parser tests failed against the stub before implementation;
all22 JVM tests then passed. Native checks initially hit the CLI's default10s
Functions discovery timeout, diagnosed from the precise log and resolved with
CI's existing30s startup setting. A subsequent real native red run passed the
existing4 checks and failed the2 new unwired SDK/UI checks. After implementation,
all6 passed through the actual demo SDK and bound TextViews; full-content native
capture was inspected. Extended native UI missing/empty checks are tracked in the
batch plan. Lint/Checkstyle/debug/live/test builds passed. Independent review found
no blockers and noted that safe private-field projection is inspection evidence,
not a claimed serialization test. Ary still owns the final shared Match UI;
survey weights, UM policy and production candidate permissions remain open.

After pushing77c6b51, investigated missing current-head CI and found PR conflicts
with newly advanced main. Fetched main through875eee6 and preserved its design
system/shell/private-contract changes. The sole manifest conflict was resolved
as the union of internal preview and product-shell declarations, preserving the
launcher/themes/export flags; independently reviewed. Rechecked the merged Android
build/static/unit/native scope; details are in the Android match plan. This is
integration of existing team work, not newly authored product UI.

## Local Worker feasibility continuation

Same persistent user objective and free-only research instruction. Created an
isolated JavaScript experiment using official Firebase Auth skills, Firebase's
third-party token verification guidance, Google public-key format documentation
and Cloudflare/JOSE tooling docs. Existing Android/Functions code is reused without
changing its runtime. Initial workerd stub had22 failing tests out of23; after
implementation those passed. Additional key-URL/kid and reviewer regression tests
bring the local runtime suite to25. RSA keys are generated only in memory and all
outbound HTTP is synthetic/blocked; no cloud account, deploy or live writes.

Dependency audit identified three high package entries in the stable Miniflare4
tooling. Inspected current Wrangler's dependency and migrated the experiment to
its pinned Miniflare5 beta/workerd release; updated the removed fetchMock API to
an exported compatibility converter and a strict test outbound service. Fresh
locked audit reports zero known findings. This is a prerelease development tool,
not production adoption. Independent review caught an unsupported JWT critical
header returning503; a failing regression drove its correct401 classification.
Current25 runtime tests,277 existing Jest tests and syntax/build scans pass.
See the Worker plan and experiment README for evidence and unresolved gates.

## Authorized commute service and free-only research continuation

User steering: "free only", followed by "Research a no-billing production alternative".
Continued the existing authorized Aaron implementation; preserved Java Android,
JavaScript domain code, Firebase Auth/Firestore, and local Functions fixtures.
Read official Firebase Auth/Firestore skills; researched only primary provider
documentation. No account/key created, billing activated or production deployment.
Candidate runtime/providers, limits and remaining team decisions are in
`docs/no-billing-production.md`; research is not adoption approval.

Implemented the internal mandatory authorization/config/quota/provider service,
pickup-aware request sharing, privacy projection, cancellation and per-mode
emissions mapping. Added `vehicle_unavailable` throughout JS/Java and native text.
Default Google transport is blocked; injected transports remain trusted tests.
Observed failing tests before implementation. Final checks exposed synchronous
deadline gaps; regression tests drove monotonic and per-dispatch checks.
Independent review caught the latter dispatch issue. Full backend277 tests and
Android lint/Checkstyle/build/JVM checks passed; see service plan for scope.

Follow-up research downloaded three public Prasarana GTFS ZIPs without credentials,
within the government rate limit. Read-only Python standard-library CSV/ZIP joins
checked stop/trip/route/service references; SHA256 hashes and precise boundaries
are in docs/free-transit-evidence.md. No production Python dependency or database
write was introduced. Found campus routes, missing fare files and service expiry
before Wave A in the feeder snapshot; did not infer live journey availability.
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

## Module 4 continuation

Exact user instruction:

> continue from the last passover session, keep implementing thoroughly

Context: Git fetch found the prior foundation merged as PR #1, so implementation
continues from merged `main` on `feature/aaron-commute-core`. Aaron's role and PRD
define FR4.1–4.3. Missing provider/schema assumptions were kept explicit; no fares,
attendance or published emission factors were invented.

Output: pure JavaScript calculator, authenticated emulator fixture callable,
immutable Java parser/repository, local preview UI and regression/integration tests.
Tests were written first and observed failing before implementation. Independent
review found no blocking issue. The server remains the selection authority; the
client checks response types, timing and money consistency without duplicating
the cheapest/tie algorithm. Final evidence is in the commute batch plan.

## Match backend continuation

Exact active user objective:

> until the aaron.md is completely finished, keep implementing and ensure to the highest quality

Implemented the next independent Aaron P0 requirement, FR3.2's pure deterministic
match engine and verified-auth emulator callable. Proposed normalized answer
scales and similarity rules are documented as implementation choices for review,
not approved questionnaire fields. No survey weights or private production schema
were invented; sample weights/profiles are labelled synthetic. No live endpoint,
Firestore profile reader or AI scoring was introduced.

Tests first: 34 calculator cases and 12 adapter cases failed against empty
implementations before passing. Full suite and real emulator evidence, review
findings and remaining production dependencies are recorded in the match plan.

Maps follow-up under the same objective: inspected current official pricing,
SKU triggers, transit/reference, policy, service terms and security/budget guides.
Recorded sourced facts, illustrative request/cost arithmetic and five unrun route
probes in docs/maps-readiness.md. Corrected the role's unqualified cache directive;
the implementation must respect provider content retention terms. No API key,
billing change, live route call or real transit-coverage claim was made.

## Routes adapter continuation

Same active user objective: "until the aaron.md is completely finished, keep
implementing and ensure to the highest quality". Revalidated the clean worktree
at 4b94a29 and retained the authorized feature branch. Implemented the fixed-endpoint
Node Routes provider boundary, without wiring a live callable or using a key.
Official Compute Routes, Duration, RouteTravelAdvisory and FallbackInfo references
informed field masks and parsing. The transport was injected for tests.

Observed 60 tests failing against an empty client, then passing after implementation.
The full backend suite initially passed 166 tests. Deadline and size tests use real
Response/ReadableStream objects, including stalled transport/body and cancellation.
Review/final evidence is recorded in the adapter plan; no claim of live coverage,
Google acceptance, sourced fares/emissions or NFR1 is made.

CI follow-up under the same objective: read actual failed foundation runs through
GitHub CLI using the repository's existing Git credential in memory. Identified
the retired SDK `tools` package in Android setup before Gradle starts. Set the
action's supported packages explicitly; validated workflow YAML. CI repair plan
records sources, run links and remaining remote verification. Credentials were
not logged or persisted, and no main-branch merge/protection setting was changed.

The feature branch was pushed and draft PR #2 opened as required by Aaron's workflow.
Both actual GitHub jobs passed at 5c5ba1f, including the Android checks and real
Firebase emulator smoke on Ubuntu. Current credential has no admin/maintain rights;
branch protection is still unverified. Ary's merge and team QA/Trello gates remain open.

Commute fare continuation under the same objective: implemented explicit sourced
e-hailing and driving formulas, transit fallback, pickup timing, exact rational sen
rounding and the bridge to the real monthly calculator. Initial 22 tests failed
before implementation. Additional failing tests drove metadata preservation and
factor-only transit config; review caught lost route attribution, which was fixed
through pricing and monthly output. No survey value was promoted to an approved
rate. Final verification and boundaries are in the pricing plan.

## Android commute metadata continuation

Same active user objective: "until the aaron.md is completely finished, keep
implementing and ensure to the highest quality". Revalidated at959f7e0 and checked
run40407910952, which completed with both jobs successful. Used the official
Firebase Auth skill for the verified local SDK flow; no live Firebase setting changed.

Added bounded optional metadata parsing, copied immutable warnings, plain-text
source/warning/fallback rendering and synthetic server metadata. Two JVM metadata
tests failed against default-field stubs before implementation; a backend fixture
expectation failed before adding metadata. Real Android smoke initially passed
three SDK checks and failed the new native preview assertion because it dropped
metadata. After rendering was implemented, all four checks passed. Native
full-content capture was inspected; this is not viewport/scroll-gesture coverage.

Review found no important defects. Renamed the generic cost label to cover driving
costs accurately and removed redundant row clearing. Maps billing owner/budget was
asked asynchronously as a real activation prerequisite; silence is not approval.
See the Android metadata plan for final verification and remaining gates.

## Published emissions continuation

Same active user objective: "until the aaron.md is completely finished, keep
implementing and ensure to the highest quality". Used spreadsheet/PDF read-only
skills to inspect official UK2026 full/revised-flat workbooks and the Malaysian
MITI starter kit. Both UK workbook values/cells and their SHA256 hashes were
checked; MITI's table was visually inspected and reports EPA2022 CO₂e. Selected
gas-specific UK CO₂ components with explicit proxy caveats instead of relabelling
total CO₂e or presenting UK electricity as Malaysian rail data. Downloaded research
files remain ignored; no spreadsheet was edited/exported and no production Python
dependency was introduced.

Added a provenance catalogue, exact mixed-leg/occupancy calculation and pricing
to monthly-profile preservation. 24 tests failed against the empty calculator,
then passed. Two further failing integration tests drove profile copying and
malformed/conflict handling. Source selection and limits are recorded in
docs/emission-methodology.md; full verification/review evidence is in the batch plan.

## Solo work: verification-key hardening

Implemented the authorized six-workstream plan's first cache batch using the
Firebase Auth skill and generated in-memory RSA keys. Added bounded key retrieval,
provider freshness, refresh coalescing and unknown-key/failure cooldowns. Runtime
verification exposed unsupported Workers fetch redirect:error; manual handling
now rejects redirects. Failing regressions drove stale-stream cancellation,
fresh-key preservation during rotation outages and concurrent rotation joining.
Independent review found a zero/short-TTL amplification defect; added failing
regressions and retained only key IDs for cooldown checks. Follow-up review found
no remaining actionable defect. All55 experiment tests pass, including25 workerd
HTTP/crypto tests; production dependency audit reports zero vulnerabilities.

The reported6 personal hours on26 October are recorded as a draft. No session
times, dates, task breakdown or Trello evidence were fabricated or posted.
