# Cloud Function contracts — draft for team agreement

Status: **DRAFT**, 27 October 2026. Emulator-only `sessionCheck` and the labelled
fixture versions of `getCommuteOptions` and `computeMatchScores` are implemented; production endpoints are
not exported. JSON uses example fixture values,
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

## getCommuteOptions — implemented local fixture interface

Availability: **only** the `demo-jiran` Functions emulator with the expected
loopback Firestore emulator. Requires authenticated, email-verified caller.
No live export, listing/profile reads, Maps calls or user-specific data.

```json
{"scenarioId":"on-time"}
```

The only accepted body field is `scenarioId`; valid IDs are `on-time`,
`no-on-time-route` and `missing-data`. Unknown IDs, production listing/unit
requests, `userId` and cost overrides return `invalid-argument`. Missing auth
returns `unauthenticated`; unverified email returns `permission-denied`.

Response includes `dataSource: "fixture"`, `assumptionVersion: "fixture-v1"`,
`scenarioId`, `isEstimate: true`, `status`, exactly four ordered `options`,
`selectedMode`, `trueMonthlyCostSen`, component amounts, `monthlyTrips`,
`departTime` and `classStartTime` (UTC ISO timestamps). Example summary:

```json
{
  "scenarioId":"on-time","dataSource":"fixture","assumptionVersion":"fixture-v1",
  "isEstimate":true,"status":"ok","selectedMode":"transit",
  "roomPriceSen":45000,"utilitiesShareSen":6200,"monthlyTrips":40,
  "trueMonthlyCostSen":61200,
  "departTime":"2026-11-16T00:00:00.000Z",
  "classStartTime":"2026-11-16T01:00:00.000Z"
}
```

The actual response also includes the ordered walking/transit/e_hailing/driving
options. Available options contain `durationSeconds`, `distanceMetres`,
`perTripCostSen`, `monthlyCostSen`, nullable `co2GramsPerTrip`/`emissionSource`,
`arrivalTime` and `arrivesOnTime`. Unavailable options contain `mode`,
`status: "unavailable"` and a reason: `missing_route`, `missing_schedule`,
`missing_fare_assumption` or `provider_unavailable`.

Rules verified by tests:

- Arrival must be **strictly before** class; arriving exactly at class start is
  ineligible. Offset timestamps compare absolute instants.
- Monthly commute = integer per-trip sen × explicit `monthlyTrips`. The preview
  does not infer attendance days, return trips, seasonal passes or parking frequency.
- Utilities input is the already-calculated equal share; occupancy division and
  rounding policy stay with the agreed schema/calculation contract.
- Choose lowest monthly cost among known-cost, available, on-time options; ties
  prefer shorter duration then walking/transit/e_hailing/driving order.
- No eligible option → `status: "no_on_time_route"`, null selection and total.
  Missing fares remain unavailable, never zero. Deposit is excluded.
- CO₂ = distance in km × explicitly supplied grams/km, rounded to whole grams,
  with a required source. Unknown factors stay null and do not become zero.
  The fixture's synthetic factor is **not** a published source or verified assumption.
- Inputs must have safe nonnegative integer units and valid calendar timestamps.
  Invalid/overflowing calculations fail rather than emitting NaN or imprecise totals.

This completes a calculation/fixture handoff, not production FR4 or NFR1 signoff.

## getCommuteOptions — proposed production contract, FR4.1–4.3

The following listing/unit interface is **not implemented**. The local endpoint
above deliberately rejects these request shapes until schema and provider inputs
are agreed.

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

### Implemented server provider boundary (not a callable)

`createRoutesClient({apiKey, fetchImpl?, timeoutMs?, now?}).computeRoute(input)`
in `functions/src/routes-client.js` performs one Routes Compute Routes request.
The caller must authenticate/authorize resources before invoking it and inject a
managed server credential. The module reads no environment credentials itself,
exports no live endpoint, caches/logs nothing and does not retry.

Input: `origin` and `destination` each `{latitude,longitude}`, `travelMode`
(`WALK`, `TRANSIT`, `DRIVE`), `departTime` (explicit-offset ISO timestamp), optional
boolean `trafficAware` (true only for DRIVE). Unknown top-level fields are rejected.
Times normalize to UTC; TRANSIT allows the documented -7/+100 day window, other
modes reject past departure. Input time precision is at most milliseconds.

The fixed HTTPS endpoint gets a narrow mask for duration/distance/warnings/transit
fare/fallback. Region MY, metric units and English labels are explicit. Alternatives,
waypoints, toll computation and arrive-by requests are not enabled. Explicit
traffic-aware DRIVE uses the relevant paid SKU; no call runs without caller action.
See [official request reference](https://developers.google.com/maps/documentation/routes/reference/rest/v2/TopLevel/computeRoutes)
and [readiness/pricing gates](maps-readiness.md).

Available result example:

```json
{"status":"available","durationSeconds":1800,"distanceMetres":6000,
 "transitFareSen":250,"warnings":[],"trafficFallback":false,"source":"Google Maps"}
```

Duration rounds upward to whole seconds; local commuter-route sanity limit is 48h.
MYR Money converts to integer sen using exact half-up rounding. Absent/foreign
currency fare returns null; an explicit MYR zero remains zero. The adapter returns
route facts even with unknown fare; the cost layer must preserve missing-fare state.
No raw addresses, tokens, polylines or provider diagnostics are projected.

Empty routes return `{status:"unavailable",reason:"missing_route",source:"Google Maps"}`.
Malformed provider data/network/HTTP errors reject with sanitized `unavailable`;
HTTP 429 uses `resource-exhausted`, deadline uses `deadline-exceeded` and bad inputs
use `invalid-argument`. Missing credential/bad client config use `failed-precondition`.
The 2500ms default includes response streaming; configurable 1..10000ms, max 128KiB.
Cancellation is requested on timeout/oversize/non-success bodies; no raw error body
or credential is attached to thrown errors. Upstream request/candidate rate limits
still belong in the authorized callable before production activation.

Warnings must be displayed downstream, as plain text with the appropriate provider
attribution. `trafficFallback: true` must not be labelled a fully traffic-aware
result. This adapter has contract tests, not live API acceptance/coverage evidence.
Initial transit waiting/timing semantics and fares must be inspected in the five
real probes before feeding real class-time selections. Step-by-step transit and
seeded UM shuttle composition remain separate P1/data work.

### Proposed callable

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

## computeMatchScores — implemented local fixture interface, FR3.2

Availability: only the safe `demo-jiran` emulator environment, same verified-auth
policy as `getCommuteOptions`. Request has exactly one field:

```json
{"scenarioId":"ranked"}
```

Valid scenarios: `ranked`, `missing-answers`, `empty`. No `userId`, candidate IDs,
profile answers or weight overrides are accepted. These are synthetic profiles,
not recommendations from the live user pool. No Firestore access occurs.

Response fields: `dataSource: "fixture"`, `scenarioId`, `status: "ok"`,
`formulaVersion: "normalized-v1"`, `weightVersion: "synthetic-v1"`, `matches`.
Example match (actual result also has seven factor records and reason codes):

```json
{
  "candidateId":"sample-a","status":"scored",
  "scoreBasisPoints":10000,"score":100,"missingFactors":[],
  "overlappingClassDays":[1,3],
  "factors":[{"factor":"sleep","weightBasisPoints":2000,"similarityBasisPoints":10000}],
  "reasons":[{"factor":"sleep","code":"similar"}]
}
```

The pure `rankMatches({subject,candidates,config})` engine requires explicit
versioned weights. Seven named integer weights, each 0..10000, must sum to 10000.
There is no production default. Input profiles have `id`, normalized `answers`,
and nullable `classDays` (unique ISO weekdays 1..7). Proposed normalization:

| Factor | Input | Similarity before basis-point rounding |
|---|---|---|
| sleep | Minutes after midnight, 0..1439 | 1 − shortest cyclic difference / 720 |
| cleanliness, noise, cooking | Ordinal integer 0..4 | 1 − absolute difference / 4 |
| smoking, pets | Boolean | 1 for agreement, otherwise 0 |
| budget | `{minSen,maxSen}`, safe nonnegative integers, min ≤ max | Interval intersection / union; identical point intervals = 1; other zero overlap = 0 |

Each similarity is rounded half-up to 0..10000 basis points. Overall basis points
are the weighted sum divided by 10000, rounded half-up using integer arithmetic.
`score` = overall basis points / 100. Positive-weight missing answers on either
profile yield `status: "insufficient_data"`, null score/basis points, and the
missing factor names. Zero-weight missing answers do not block scoring. Invalid
present values fail even if their weight is zero; absence is never incompatibility.

Sort scored candidates by descending score, then ASCII candidate ID; unscored
candidates sort last by ID. Exclude the subject, reject duplicate IDs, cap the
candidate array at 100. IDs use 1..128 ASCII letters, digits, underscore or hyphen.
Day intersections are sorted; unknown weekdays produce null, known no overlap
produces `[]`. Class overlap does not alter the score.

Factor output contains weight and similarity only, never raw answers. Rule reason
codes for known positive-weight factors are `similar` (≥7500), `different`
(≤2500), otherwise `partial_overlap`. These codes support deterministic fallback
rendering and Ilham's explanation; they do not unlock contact access. No exact
class times, addresses, phone/email or other profile fields are projected.

Questionnaire mapping, interpretation of preferences, visibility of each factor
breakdown/day overlap, survey-derived weights and candidate eligibility need
Ary/Gathan/Aly/Ilham agreement before production use. Similarity is not a hard
smoking/pet preference filter. No production reader is implemented: an adapter must
load only authorized candidates and apply privacy policies before scoring or
returning any result. The core is not an authorization boundary.

## computeMatchScores — proposed production contract, FR3.2

Request: `{}` (preferred). If retaining the planned `userId` parameter for legacy
callers, require it to equal auth UID; it never grants cross-user access.

Fixture response:

```json
{"matches":[],"formulaVersion":null,"status":"configuration_required"}
```

The production endpoint is not implemented. After questionnaire scales/weights are agreed, return only permitted candidate
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
