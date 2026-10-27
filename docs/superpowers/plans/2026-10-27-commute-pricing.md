# Commute fare layer plan

> Execute inline with test-driven-development and verification-before-completion.

**Goal:** Connect provider-style route facts to Aaron's FR4 monthly calculator through explicit configurable e-hailing/driving formulas, without inventing survey rates or enabling paid requests.

**Spec:** PRD FR4.1–4.3; ASSUMPTIONS #3/#4/#5/#6; docs/api-contracts.md. This layer does not own production configuration publication or profile/listing authorization.

**Interface:** `priceRoutes({routes,assumptions})` returns `{assumptionVersion,modes}`. Routes use calculator mode names and available durationSeconds/distanceMetres, optional transitFareSen. Input may include provider-unavailable route states. At most four unique known modes; absent modes become missing_route.

**Explicit choices:** versioned config, each supplied mode config needs a nonempty source. No numeric production defaults. Walking is zero fare. Transit uses provided MYR fare or an explicitly sourced fallback perTripCostSen. E-hailing = (baseSen + distanceMetres × rateSenPerKm / 1000) × multiplierBasisPoints/10000; explicit pickupSeconds is added to duration. Driving fuel sen = distanceMetres × fuelMillilitresPerKm × fuelSenPerLitre / 1000000, plus explicit parkingSenPerTrip and tollsSenPerTrip. Round the final per-trip amount half-up to integer sen with BigInt rational arithmetic. Required zeros must be supplied, not inferred. Cost source is retained as metadata.

Optional mode emissionFactor passes to the calculator only if explicitly supplied with source; no factor is inferred from a route mode. The caller must substantiate mixed-transit factor/distance coverage and e-hailing occupancy assumptions. Missing cost config returns missing_fare_assumption, malformed present config fails; no source approval is implied by presence of a string. No provider calls, caching, weights, live schema, key or Firebase changes.

- [x] Write failing formula/missing-data/rounding/overflow/config tests and an integration test with the real monthly calculator; observe red.
- [x] Implement focused pure module; run full backend and syntax checks; independent review and fix findings.
- [x] Document formulas/config/sources and remaining gates, update Aaron checklist/AI log, commit after verified checks.

## Ledger

- Previous batch f578169 added bounded Routes transport; 5c5ba1f repaired Android CI SDK setup. Draft PR #2 is open, backend remote check succeeded, Android is still running at batch start. No live Maps credentials or approved rates became available.

- TDD: 22 initial cases failed against empty module, then passed. Additional red cases drove factor-only transit config, provider warnings/fallback and attribution preservation. Added nonidentity multiplier final-rounding coverage and safe-limit driving checks. Exact expected monthly integration: room45000+utilities6200+234*40=60560sen.
- Review: no formula/overflow defects found; reviewer identified the calculator dropping provider metadata and later route attribution. Fixed both through failing integration tests; calculator now validates and preserves costSource/routeSource/warnings/fallback. Final independent review ran72 targeted pricing/calculator tests and found no important remaining issue.
- Final local evidence: 9 Jest suites /209tests pass, syntax checks and whitespace checks pass.31pricing and41calculator tests include the real pricing-to-monthly pipeline. No Firebase config/callable/source activation changed, no rates or factors approved, and no provider request made. Android fixture payloads stay unchanged; shared UI rendering of provider metadata remains a production integration gate.
- Prior PR run40406767308 at5c5ba1f completed with android/backend success. Pricing changes require their new PR run; no previous run is used to claim new-source remote verification.
