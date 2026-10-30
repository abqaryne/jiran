# Authorized commute service orchestration plan

> Execute inline with test-driven-development and official Firebase Auth guidance; independent review and verification before completion.

**Goal:** Connect the existing provider, pricing, emissions and monthly calculation behind an authorization/quota/deadline boundary for FR4.1–FR4.3. This is internal service code, not a deployed endpoint or invented Firestore schema.

**Spec:** team/aaron.md, PRD FR4.1–FR4.3/NFR1/NFR2, docs/engineering/api-contracts.md. Retain existing emulator fixture callable; production schemas, verified UM policy, credentials/billing and approved rates remain required adapters/configuration.

## Interface and choices

`createCommuteService({loadAuthorizedContext,loadAssumptions,consumeQuota,routesClient,now?,timeoutMs?,trafficAware?,emissionProfileFor?})` returns an async callable-compatible function. All four adapters are mandatory; factory construction fails if absent. No SDK/database/key/environment initialization in the service. It consumes preverified callable auth context, not raw JWTs.

Request has exactly listingId,roomId,departTime. Derive UID from verified auth only. IDs are bounded ASCII repository IDs; explicit calendar departure must be future and within transit's100-day window. Authorizer must enforce verified UM and resource visibility server-side; return a normalized context with viewerUid/listingId/roomId/roomListingId bound to caller/request, origin/destination, roomPriceSen,utilitiesShareSen,monthlyTrips,classStartTime and explicit drivingAllowed. This is an adapter DTO, not a persisted field-list agreement.

Validate identity/membership, coordinates, class/money/trips and a copied pricing configuration before consuming quota or looking up routes. Class must start after departure. Return only calculation fields, config version and partial-provider-failure flag; never raw private context/coordinates/contact fields. Sanitize dependency errors; no logs/cache/retries.

Reserve quota with the exact maximum provider-call count after authorization/preflight. Walking and transit need one lookup each; driving only when allowed and configured, e-hailing only when configured. Share identical road lookup inputs only: zero pickup may share; positive pickup routes e-hailing at departTime+pickup and needs a separate road request if driving also requested. Reuse road data only within this request, never persist Google content. Max4 calls, parallel, no retry. Missing fare config stays unavailable; unavailable driving gets vehicle_unavailable, with Java parsing/render support.

Overall deadline defaults2800ms (configurable1..10000ms), including reads/quota/provider/calculation. Pass one abort signal to adapters; check it after every awaited stage so late reads never start provider calls. Extend Routes client to accept caller signal, canceling fetch/body as well as its own deadline. A timeout is not proof of NFR1 p95 at100 concurrent users.

Provider exceptions/malformed facts become per-mode provider_unavailable. If no usable route fact survives and any provider failed, reject unavailable rather than claim a transport impossibility. Partial results remain explicitly marked. Only known-cost on-time modes are selected. The optional synchronous trusted `emissionProfileFor({mode,route})` maps whitelisted facts after domain-mode assignment. It returns an explicit profile or undefined; malformed profiles fail closed. Raw provider profiles are ignored. This prevents a shared DRIVE lookup assigning the car factor to e-hailing. Without a mapper CO2 is unknown; the Google client does not infer bus/rail legs or UM shuttle data.

## Tasks

- [x] Add failing auth/input/identity/membership/quota/privacy/deadline/provider-sharing and real-provider-client integration tests; stub service then implement.
- [x] Add failing Routes caller-cancellation tests; implement shared pure preflight validation and abort propagation without extra requests or SDK behavior changes.
- [x] Add vehicle_unavailable reason across JS/Java contracts and native message, with failing parser/domain coverage before implementation.
- [x] Run full backend/Java/lint/Checkstyle/build checks; independent review/fixes; document exact internal interface/remaining production gates, update Aaron/AI log, commit/push PR #2 and inspect CI.

## Ledger

- Previous goal turn was progress:2b66c67 sourced CO2 catalogue/calculation and profile integration. Preflight worktree clean. Previous CI run40412579839 still in progress at start; poll that exact run. Billing owner/budget question remains pending, not permission to enable paid services.

- User answered **free only**, then requested **research a no-billing production alternative**. Recorded in AGENTS/Aaron and [candidate research](../research/no-billing-production.md); no deployment/runtime switch or billing activation. Google default transport now rejects before requests. Trusted injected transports remain local test seams, not absolute network enforcement.
- CI run40412579839 at2b66c67 completed successfully for backend and Android.
- Initial service tests failed against stub; after implementation29/30 passed until vehicle_unavailable was supported throughout. Java parser and free-only transport tests failed before their fixes. Three caller-abort tests failed before implementation. Added shared-road car/taxi emissions, late config/quota and malformed mapper checks.
- Final self-check exposed timer-only deadline behavior with synchronous adapter work: a10ms deadline plus25ms blocking read resolved before its timer fired. Added monotonic elapsed-time checks at stage boundaries. Independent review then identified late provider dispatch after blocking first transport; a failing regression observed3 calls, fixed to1 with a pre-dispatch deadline check. Synchronous work cannot be preempted; expired work is rejected at its next boundary.
- Full backend verification: **277 tests /11 suites pass**, syntax check includes service. Android `lintDebug lintLiveDebug testDebugUnitTest checkstyleMain assembleDebug assembleLiveDebug` succeeded;15 JVM tests pass. Prior four native SDK/render checks remain historical evidence, not rerun evidence for this batch.
- Independent review otherwise found no blockers. Future alternative-provider transit fares must replace the current Google-specific fare label during migration. No production authorizer/schema, persistent quota, provider activation or NFR1 completion is implied.
- Committed/pushed `c3b63de`; PR#2 updated. [CI40425407258](https://github.com/abqaryne/jiran/actions/runs/40425407258) backend passed; Android was still running when this record was written. Follow-up public GTFS inspection and its source hashes are in [free transit evidence](../research/free-transit-evidence.md); fare/coverage and date-validity gates remain open.
