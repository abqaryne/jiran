# Commute per trip: implementation handoff

Product decision: [PRD Module 4](../product/PRD.md#module-4-apartment-to-faculty-commute-guide), Ary, 11 Nov 2026. This file maps the work to files; it does not duplicate the requirements or claim implementation is finished. Needs human review.

## File responsibilities

| Layer / owner | Existing files | Required change |
|---|---|---|
| Product / Ary | `docs/product/PRD.md`, `GLOSSARY.md`, `USER-FLOW.md`, `ASSUMPTIONS.md` | Canonical per-trip journey and units; remove monthly total and attendance dependency. |
| Profile / Ary | `ui/profile/PreferencesStepFragment.java`, `ProfileSetupViewModel.java`; `domain/profile/ProfileCheck.java`, `SetupProgress.java`; `res/layout/fragment_profile_preferences.xml`, `strings_profile.xml`; profile tests | Remove campus-day prompt/validation and progress requirement. Keep faculty, Room budget, lifestyle and car ownership. |
| Profile data / Ary + Gathan | Android `data/ProfileRepository.java`, `FirestoreProfileRepository.java`; `docs/engineering/schema.md`; affected rules/tests if any | Stop writing/requiring `campusDaysPerWeek`; accept existing profiles without it. Leave existing private values unused until a reviewed migration. No destructive deletion in this docs change. |
| Server routes/pricing / Aaron | `backend/functions/src/commute-service.js`, `commute-pricing.js`, `commute.js`; related Jest tests/fixtures | Produce one-way duration/fare independently of rent, utilities, trips/month and class times. Preserve exact integer-sen pricing. Select only applicable available modes; keep partial/unavailable data explicit. |
| Transit / Aaron, timetable from Dafi | `backend/functions/src/gtfs.js`, `gtfs-journey.js`, provider adapters | P0 travel instructions from supported legs; P1 full map/timeline and departure scenarios. Do not invent a shuttle link or fare. |
| API and Android data / Aaron | `docs/engineering/api-contracts.md`; Android `data/CommuteSummary.java`, `CommuteRepository.java`, associated tests | Replace the legacy monthly/class-time DTO with the per-trip contract; coordinate consumer/producer migration before changing a deployed interface. |
| Room cards / Gathan, shared components by Ary | `ui/common/RoomCardView.java`, `res/layout/component_room_card.xml`, `ui/rooms/RoomsFragment.java`, `RoomsViewModel.java` | Replace the total-cost slot with a labelled one-way commute summary; retain separate monthly rent. The existing Rooms fragment/view model are placeholders; their product bindings belong to E3.1. |
| Room details / Ary | `ui/common/CommuteOptionView.java`, `res/layout/component_commute_option.xml`; detail screen files not created yet (E4.1/E4.2) | Remove true-monthly-cost headline/breakdown; show “Getting to [faculty]”, per-trip rows and basic instructions. Use existing tokens/styles. |
| Preview / Aaron + Ary | `CommutePreviewActivity.java`, `backend/functions/src/commute-preview.js`, `fixtures/commute-scenarios.json` | Replace before-class/monthly scenarios with per-trip, missing-data and first-year-without-schedule cases; do not present old fixture output as the new product. |
| Production / Aaron | `backend/worker/` | Wire only after the free-only provider/runtime handoff is verified. Preserve Java Android / JavaScript backend; this decision does not activate providers or move the current local libraries. |

Paths under `ui/`, `data/` and `domain/` above are relative to `android/app/src/main/java/com/jiran/app/`; `res/` is relative to `android/app/src/main/`. Existing files stay in place; do not bulk-rename or move working code just to match the new terminology. Extend the existing `ui/rooms/` placeholders (E3.1). Proposed new destinations are `ui/roomdetails/` (E4.1/E4.2) and `ui/commute/` (E4.4); those product screens have not been created.

## Work order and evidence

| Order | Story | Deliverable / evidence |
|---|---|---|
| 1 | E1.3, E0.5 | A new first-year completes setup without attendance days; old profiles still load. |
| 2 | E3.3 | Per-trip engine/DTO tests without rent, class start or monthly trips; fare units and missing-data cases. |
| 3 | E3.4, E3.1 | Room card shows monthly rent separately from a named mode’s one-way time/fare; source/scenario included in details. |
| 4 | E4.1, E4.2 | Detail UI matches the supplied layout structure with the old total removed; supported transit instructions; estimated e-hailing fare. |
| 5 | E4.4 | Full transit map/timeline and Peak / Off-peak only with supported data. |
| 6 | T1.4, T2.1/T2.2 | Sourced example routes/fares; Android/backend checks, real-device QA and existing performance targets. |

Implementation update, 11 Nov 2026: existing profile UI/validation/writes/progress no longer require campus days; RoomCardView uses `roomCommute` / `setCommute`, and the existing developer preview renders the already-validated one-way fare. Android checks (112 tests) and backend Jest (380 tests) pass. Product route wiring and backend/legacy DTO removal are still pending: the parser preserves legacy validation internally while exposing `perTripCostSen` for display. Ary explicitly limited this UI work to existing screens/components; no new detail page or new product screen was created. Historical AI logs and dated implementation reports remain unchanged.

Update, 11 Nov 2026 (later): the engine, the fixture callable, `createCommuteService`, the smoke/k6 fixtures, `api-contracts.md` and the Android `CommuteSummary` parser and preview now use the per-trip format; the legacy `calculateCommute` is removed. Still open: production exposure and the Worker (not wired), the Room card and details screens, supported transit instructions. Needs human review (Aaron).
