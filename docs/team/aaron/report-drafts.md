# Aaron report and demonstration drafts

**AI-assisted draft — needs human review.** Aaron must revise these passages,
complete his AI log, and explain the code before using them. They describe the
current prototype, not a completed production product. Counts exclude headings,
this notice and evidence notes. Section4 is Aaron's contribution; the final team
section still needs the other owners' reviewed contributions.

## Section3: Architecture and design

<!-- architecture-start -->
Jiran uses a native Android application written in Java and a JavaScript backend. Android code, Firebase configuration and project documentation occupy separate repository folders. This structure separates interface responsibilities from calculations and infrastructure, while preserving a shared API contract for the team. The existing Firebase project supplies Authentication and Firestore; local development uses the demo project and loopback emulators.

Authentication establishes identity through Firebase tokens rather than a user identifier supplied in request data. Local callable handlers reject anonymous and unverified requests. This verified email requirement does not establish Universiti Malaya affiliation. Production eligibility rules and access to listing, profile and timetable records still require agreed schemas and authorization. Private addresses and contact information must remain outside public responses until the required Connection exists.

The commute design separates route retrieval, sourced pricing, emissions and monthly aggregation. Travel duration includes waiting or pickup time before checking class arrival. The monthly calculator selects the cheapest available mode that arrives on time, then adds room rent and the occupant's utilities share. Missing fares or schedules remain explicit unavailable states. Deposit is separate. Published emissions factors retain provenance and geographic limitations, preventing synthetic examples or foreign proxies from appearing as measured Malaysian results.

Matching uses deterministic weighted factors and stable ranking. Missing answers are reported explicitly, and the response exposes bounded explanations rather than unrestricted candidate profiles. Survey approval of weights and the production candidate reader remain pending. Typed Java response models copy data into immutable structures and support developer previews for normal, incomplete and empty results.

The budget constraint changes deployment planning. Paid Functions and Google routing have not been activated. An isolated Worker experiment evaluates JavaScript compatibility, token verification, bounded requests and SQLite quota reservations. This candidate preserves Firebase Authentication and Firestore, but requires team approval before replacing the planned runtime. Continuous integration checks backend behavior, Android quality and the experiment separately. Local fixtures establish reproducibility; they do not establish live transit coverage, production security or the required search performance under concurrent load. These remaining gates are documented so implementation evidence remains distinguishable from architectural intent and team decisions still awaiting confirmation.
<!-- architecture-end -->

## Section4: Aaron's implementation contribution

<!-- implementation-start -->
The implemented backend foundation keeps calculations independent of Firebase transport and external routing providers. JavaScript modules handle commute timing, exact monetary arithmetic, emissions and match ranking. Emulator handlers expose synthetic scenarios so Android integration can be checked without paid services or private student data. Outside the guarded local environment, diagnostic and fixture endpoints are not exported.

The commute calculator compares walking, transit, e-hailing and driving against the class time. It distinguishes missing information from a valid route that arrives late. Monetary values use integer sen, with monthly trip assumptions and rounding. The selected on-time mode contributes to the room's true monthly cost; a missing qualifying mode produces an explanatory state instead of a fabricated total. The pricing layer requires explicit inputs for fares, fuel, parking, tolls and pickup time rather than supplying unsupported production defaults.

Emissions calculations use a versioned catalogue with publication provenance and gas-specific units. Mixed journeys sum available leg estimates and account for vehicle occupancy. Foreign factors require explicit acceptance of their proxy limitations. Missing distances or factors leave emissions unknown. Match scoring similarly stays deterministic: seven factors contribute weighted values, candidates receive stable ordering, and missing questionnaire answers remain visible. The formula is implemented, while survey-derived approval and production candidate authorization remain open.

On Android, typed repositories and immutable response models connect the local callables to developer previews. The previews display mode comparisons, provenance, warnings, selected costs and match breakdowns. Incomplete and empty responses clear prior content. These screens support integration testing; Ary's final product interface remains a separate handoff. Authentication checks and native tests exercise local SDK calls, while backend tests cover validation, projection and calculation edge cases.

The free-runtime experiment bundles shared calculations into local workerd. Firebase-style token verification uses generated RSA keys in tests, bounded public-key fetching, response freshness and rotation cooldowns. Request bodies have byte and time limits. SQLite reservation tests check concurrent account and user limits, rollback, window boundaries and persistence after runtime restart. No live routing request or production deployment is represented by these results.

Verification evidence includes automated backend tests, Android lint, Checkstyle, JVM tests and local emulator smoke checks. Updated continuous integration preserves Node22 and JDK25 while selecting basic GitHub caching. Production completion still depends on real route evidence, approved assumptions, secure data readers, measured load performance and team review. Aaron must inspect, modify where necessary, and explain this assisted implementation before claiming understanding or submitting the contribution.
<!-- implementation-end -->

## Evidence to attach after human review

- Requirements: [FR3.2 and FR4.1–FR4.3](../../product/PRD.md); the canonical PRD's
  NFR1 remains p95 below3s at100 concurrent users. The50/100-user wording in
  AGENTS differs; keep both scenarios until the owner resolves it.
- Implementation: [backend modules](../../../backend/functions/src),
  [Java data models](../../../android/app/src/main/java/com/jiran/app/data),
  [Worker experiment](../../../experiments/worker-feasibility/README.md).
- Verification and remaining gates: [solo ledger](../../plans/2026-10-27-aaron-solo-work.md),
  [API contracts](../../engineering/api-contracts.md), [AI log](../../ai-log/aaron.md).
- Aaron's personal changes, commands run and understanding: **TODO (owner)**.
  No survey, usability, QA or production performance outcome is claimed here.

## Technical implementation video:90-second rehearsal

| Time | Owner | Show and explain |
|---|---|---|
| 0:00–0:20 | Aaron | Open the local commute preview. Say: “This is synthetic emulator data. The backend compares four modes against class arrival time, then selects the cheapest qualifying mode.” Show selected mode, monthly total and unavailable states. |
| 0:20–0:40 | Aaron | Show pricing/emissions modules and one focused test. Explain integer sen, sourced factors, proxy labels and missing-data handling. Use an actual observed result; do not describe a fixture fare as a real fare. |
| 0:40–0:50 | Aaron | Show the typed Java repository and match breakdown. Explain that identity comes from Firebase and matching is deterministic; verified email alone does not prove UM eligibility. |
| 0:50–1:10 | Ilham | Reserved20-second AI-flow contribution. **TODO (Ilham): supply a verified implementation, AI-generated label, fallback and evaluation example.** If unavailable at recording, identify the gap instead of staging a working feature. |
| 1:10–1:30 | Aaron | Show actual CI results and the free-only research boundary. Say what is local, what is proposed, and what still needs route, authorization and load verification. End on the observed state; do not claim production completion. |

Before recording, Aaron rehearses the70-second portion with a timer, checks that
only synthetic data is visible, and replaces narration he cannot explain. Build
and emulator commands are in [README](../../../README.md). This is a script, not evidence
that a video was recorded or that another team member approved their segment.
