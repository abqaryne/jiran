# Cloud Function contracts — draft for team agreement

Status: **DRAFT**, 27 October 2026. Except for emulator-only `sessionCheck`, none
of these endpoints is implemented/exported. JSON uses example fixture values,
not approved fares, weights or collections. Aaron coordinates approval with Ary,
Gathan and Ilham before clients depend on the shapes.

## Shared conventions

Use Firebase callable protocol (SDK clients pass the JSON object; raw HTTP wraps
it in `data`, successful responses in `result`). Require token authentication and
the agreed verified-UM policy for production endpoints. Derive caller UID from
auth, never from the body. Authorize referenced resources server-side.
Amounts below are integer MYR **sen**, durations seconds, distances metres and
CO₂ grams. Timestamps are ISO 8601 with explicit offsets; interpret class schedules
in `Asia/Kuala_Lumpur`. Validate finite nonnegative values and size limits.
Expected callable errors: `unauthenticated`, `permission-denied`,
`invalid-argument`, `not-found`, `failed-precondition`, `resource-exhausted`,
`unavailable`, `internal`. Sanitize provider errors. Never leak private data.
All limits, rounding, empty-data policies and versioning need contract review.

## sessionCheck — local diagnostic only

Request: `{}`. Response: `{"uid":"local-user-id","status":"ok"}`.
Missing auth → `unauthenticated`; email not verified → `permission-denied`.
No production export. This check does not validate UM affiliation.

## getCommuteOptions — FR4.1–4.3

Example request:

```json
{"listingId":"fixture-listing","roomId":"fixture-room","departTime":"2026-11-16T08:00:00+08:00"}
```

For an Evaluation, propose replacing `listingId` with `unit` coordinates/room
inputs; agree validation and ownership with Gathan before freezing that variant.
Load destination/class times from the caller's authorized profile. Validate that
the Room belongs to the Listing. Monthly attendance/trip assumptions are pending.

Example fixture response:

```json
{
  "options":[
    {"mode":"walking","status":"available","durationSeconds":1800,"distanceMetres":2000,"monthlyCostSen":0,"co2GramsPerTrip":0,"arrivesOnTime":true},
    {"mode":"transit","status":"unavailable","reason":"missing_schedule"},
    {"mode":"e_hailing","status":"unavailable","reason":"missing_fare_assumption"},
    {"mode":"driving","status":"unavailable","reason":"missing_cost_assumption"}
  ],
  "selectedMode":"walking",
  "trueMonthlyCostSen":51200,
  "roomPriceSen":45000,
  "utilitiesShareSen":6200,
  "isEstimate":true,
  "assumptionVersion":"fixture-only"
}
```

The displayed total excludes deposit. Select the cheapest **on-time available**
mode; if none qualifies, return `selectedMode: null`, `trueMonthlyCostSen: null`
and a no-on-time-route state. Never represent unknown fares as zero. P0 has one
class-time result; P1 adds peak/off-peak and route steps. Cite distance/emission
sources in detail responses; fixture zeros are not published emission factors.

## getTransitRoute — P1

Request:

```json
{"listingId":"fixture-listing","departTime":"2026-11-16T08:00:00+08:00"}
```

Fixture response:

```json
{"status":"unavailable","steps":[],"reason":"missing_schedule","isEstimate":true}
```

Available steps must specify walk/board/alight/transfer/shuttle legs, stops,
scheduled departures and arrivals, total time/fare and source. Label seeded UM
shuttle information as scheduled, not live; do not invent missing routes.

## computeMatchScores — FR3.2

Request: `{}` (preferred). If retaining the planned `userId` parameter for legacy
callers, require it to equal auth UID; it never grants cross-user access.

Fixture response:

```json
{"matches":[],"formulaVersion":null,"status":"configuration_required"}
```

After questionnaire scales/weights are agreed, return only permitted candidate
fields, deterministic scores, factor breakdown and overlapping class days. Never
return private contact data or raw timetables. Define candidate limits, missing
answers, tie sorting and weight normalization before implementation. Ilham's
explanation reads this breakdown; rule-generated reasons cover AI failure.

## splitRoomPrices — shared utility, FR2.1 / FR2.4

Fixture request:

```json
{"totalRentSen":150000,"rooms":[{"id":"r1","type":"master"},{"id":"r2","type":"medium"},{"id":"r3","type":"small"}]}
```

Fixture response using current draft 1.3/1.0/0.7 weights:

```json
{"rooms":[{"id":"r1","priceSen":65000},{"id":"r2","priceSen":50000},{"id":"r3","priceSen":35000}],"totalRentSen":150000,"assumptionVersion":"draft-room-weights"}
```

PRD rounds suggested prices to RM1 and assigns remainder to master. Agree absent
master/tied-master/tiny-rent behavior before implementing. Explicit allocations
must not exceed Unit rent for Listings; Evaluation/group splits must equal it.
Weights stay configurable. This function does not create listings or households.

## AI interfaces — proposed names, Ilham approval required

`parseSearchRequest` request `{"text":"quiet room near UM","language":"en"}`;
response `{"filters":{},"requiresConfirmation":true,"aiGenerated":true}`.
Filters are editable and must be confirmed before searching.

`checkContract` request `{"contractCheckId":"owned-upload-id"}`; response
`{"summary":"Fixture only","clauses":[],"aiGenerated":true,"notLegalAdvice":true}`.
Resolve only an uploader-owned file; agree file limits, asynchronous job/status,
retention/deletion and reporting with Ilham/Gathan. Never accept arbitrary URLs.

`explainMatch` request `{"matchId":"authorized-match-id"}`; response
`{"reasons":[],"aiGenerated":true,"fallbackUsed":false}`. Server loads the
approved score breakdown. Rule fallback sets `aiGenerated: false` and
`fallbackUsed: true`; no AI response can modify a score.

## Agreement checklist

- Ary: UI DTOs/states and Android application ID.
- Gathan: resource IDs/ownership, fields, UM policy and privacy projection.
- Aaron/Aly: monthly trip rules, formula configuration and source evidence.
- Ilham: names, AI payloads, limits, jobs and fallback format.
- Team: error limits, currency rounding, region and contract version.
