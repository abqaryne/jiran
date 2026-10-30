# Published CO2 factors and mixed-leg calculation plan

> Execute inline with test-driven-development and verification-before-completion; independent review at batch end. Source work uses spreadsheet/PDF read-only workflows.

**Goal:** Fulfil Aaron's source selection/extraction work for assumption #6 and enable FR4.3 CO2 estimates without mixing gases, distance bases or transit legs.

**Spec:** PRD FR4.3, ASSUMPTIONS #6, team/aaron/README.md. UK proxies are explicitly enabled by the caller, never silently presented as Malaysian measurements.

## Source decision and evidence

Official UK DESNZ 2026 full set and July-revised flat workbook downloaded unchanged from GOV.UK. Selected the CO2-specific component, not total CO2e. Cross-checked flat IDs/rows against full-set cells and Introduction gas definitions. MGTC cites DESNZ for fuel inventories, but that is not endorsement of UK rail factors for Malaysia. MITI starter kit p70 (PDF page78), visually inspected, uses EPA2022 CO2e factors; those cannot be relabelled CO2. Prefer a consistent current gas-specific table and expose regional limits.

Selected values in gCO2/unit: average petrol car160.84/vehicle-km, regular taxi206.38/vehicle-km, average local bus100.77/passenger-km, light rail20.93/passenger-km, Underground15.22/passenger-km, average motorbike111.38/vehicle-km. Walking zero is a separate Jiran powered-operational boundary, not a value claimed from that table. UK taxi methodology excludes empty running; per-vehicle basis avoids silently assuming its published1.4 passenger average.

## Architecture and choices

- `functions/config/emission-factors.json`: small versioned catalogue with original kg values, integer milligrams per unit, gas/basis, exact source IDs/rows/full-set cells, URLs and SHA256 provenance. No downloaded workbook/PDF committed.
- `functions/src/emissions.js`: pure `calculateTripEmissions({distanceMetres,legs,allowUkProxy})`. Up to50 legs; each has nonnegative integer metres and a factor ID. Vehicle-based factors require explicit positive integer occupants (1..100 mathematical input bound, not vehicle-capacity approval). Passenger factors must not be divided again; reject an occupants override. Walking is zero powered operational emissions, excluding food/lifecycle.
- Unknown factor, missing legs/occupancy, disabled proxy or incomplete distance coverage returns explicit unavailable with null grams/source. Malformed present inputs fail. Require exact sum of leg metres to route metres; no invented distance allocation/tolerance. Real provider rounding/leg mapping still needs coverage probes.
- Sum exact rational milligrams across all legs/occupancy using BigInt, then round half-up once to whole grams. Preserve dataset version, CO2-only operational scope, proxy status and named factors in a bounded display source. This is not total lifecycle CO2e; taxi driver pickup/cruising is excluded unless measured distance is explicitly part of the supplied journey.
- Integrate optional `emissionProfile` in monthly calculator, mutually exclusive with legacy explicit `emissionFactor`; missing emissions never change cost/arrival eligibility. No provider/callable activation, production leg mapping or hidden default config.

## Tasks

- [x] Write failing emissions/catalogue tests, observe red, implement catalogue/calculator with literal expected totals and missing-data policies.
- [x] Add failing real monthly-calculator integration for mixed-leg profile; implement without breaking existing fixture protocol.
- [x] Full Jest/syntax, independent review/fixes; update source research, assumptions/Aaron/contracts/README/AI evidence; stage scan and local commit/push to PR #2, inspect CI.

## Ledger

- Prior turn made progress:11ff90d native metadata display and device verification. Current worktree initially clean. CI run40409849123 was in progress at preflight; check terminal state later. Billing owner/budget question remains pending; no paid request or approval inferred.
- Flat workbook SHA256 a9a455ab396dae226d510c7be6233748416d490c41a5d20f3dc7a0c45feecd5e; full workbook b5c4f2f86cdc74707337b2fb013b44ded189a1b528f5e29ea99856ba65ae0205. Source spreadsheets read-only; no workbook export/edit made.

- TDD: all 24 initial cases failed against an empty emissions function, then passed. Two further integration cases failed because pricing dropped/ignored profiles; implemented validation and defensive leg copying. Added catalogue gas/unit/provenance invariants and explicit omitted-vs-empty leg coverage. Current emissions suite has 28 cases.
- Independent review found no important defect. Reviewer independently opened both unchanged workbooks and confirmed all six IDs/rows/metric labels/units/values/full-set cells exactly. Reviewed allocation, rational arithmetic and both integration boundaries; independently ran 27 cases before the final omitted-array regression. Clarified required-array vs empty-array behavior in docs.
- Final local evidence: 10 Jest suites /237 tests pass (1.834s), syntax and whitespace checks pass. No Android/fixture numeric/protocol change in this batch; Android checks were not needlessly repeated. PR CI performs the real emulator regression after push. Source/credential scan required before commit.
- Previous native-metadata run40409849123 at11ff90d completed successfully for both android/backend. New emissions source needs its own run. No live Maps, Firebase settings, billing or private data changed; original research files remain ignored and unchanged.
