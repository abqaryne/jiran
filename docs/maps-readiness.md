# Maps integration evidence and remaining setup

Checked against official sources on **27 October 2026**. This is documentation
research, not evidence that the project's billing, APIs, credentials or route
coverage have been configured/tested. No paid request was sent.

## Pricing baseline for assumption #11

Global pay-as-you-go prices in USD, first paid tier up to 100,000 monthly events:

| SKU | Monthly free events | USD / 1000 paid events |
|---|---:|---:|
| Compute Routes Essentials | 10,000 | 5.00 |
| Compute Routes Pro | 5,000 | 10.00 |
| Compute Routes Enterprise | 1,000 | 15.00 |
| Places Autocomplete Requests | 10,000 | 2.83 |
| Place Details Essentials | 10,000 | 5.00 |

Use per-SKU caps, not an assumed shared monthly credit. Totals aggregate across
projects on the billing account. Session autocomplete and field selection can
change billing, so the Places flow needs a separate request inventory before use.
[Official global price list](https://developers.google.com/maps/billing-and-pricing/pricing).

Illustrative arithmetic, not measured traffic: 100 active students × 10 listing
comparisons/day × 30 days × (walking + transit) = 60,000 Essentials requests/month;
one shared driving/e-hailing time lookup per comparison adds 30,000 Pro requests
if traffic-aware. Assuming no other account usage: (60000−10000)/1000×5 +
(30000−5000)/1000×10 = **USD500/month**, excluding Places, taxes and Firebase.
At 20 comparisons/day those assumed volumes double and reach higher pricing
tiers; do not extrapolate the first tier rate past its range. Reusing a driving
lookup is an architectural proposal, not an assertion about e-hailing pickup time.

## Request and quota choices

Compute Routes bills queries; Matrix bills origin×destination elements. Published
rate ceilings are 3000 queries/minute and 3000 matrix elements/minute. Transit and
traffic-aware-optimal matrices are limited to 100 elements. Actual project quotas
must be inspected. [Usage and billing](https://developers.google.com/maps/documentation/routes/usage-and-billing).

Traffic-aware road routing triggers Pro. Toll calculation triggers Enterprise;
requesting tolls is not a free addition. Request only the reviewed field mask and
features. [SKU triggers](https://developers.google.com/maps/billing-and-pricing/sku-details#compute-routes-pro).

The owner must select a monthly budget and notification recipients, inspect
project/service scope and test alert delivery. Alerts do not stop spending.
Project quotas and backend request limits need separate configuration.
[Budget documentation](https://docs.cloud.google.com/billing/docs/how-to/budgets).

## Transit constraints and coverage evidence for #5

Transit accepts arrival OR departure time, not both; its documented window is
7 days back to 100 days ahead. A missing total transit fare can mean not every
leg has fare data. Transit cannot accept intermediate waypoints, so adding a UM
shuttle stop to the request does not establish shuttle service.
[Transit guide](https://developers.google.com/maps/documentation/routes/transit-route).

Arrival-time requests are supported for transit, not arbitrary modes. For road
comparisons use a validated departure instant and check the resulting arrival
against class; do not pass an unsupported arrive-by field to all modes.
[Compute Routes reference](https://developers.google.com/maps/documentation/routes/reference/rest/v2/TopLevel/computeRoutes).

The general coverage page does not prove UM shuttle coverage. These proposed
five probes still need official place selection/coordinates and approved API use:

| Probe | Proposed origin → destination | Status |
|---|---|---|
| T1 | Pantai Dalam residential listing → FSKTM | Not run |
| T2 | Kerinchi residential listing → FSKTM | Not run |
| T3 | Bangsar South residential listing → UM faculty | Not run |
| T4 | Universiti LRT station → FSKTM | Not run |
| T5 | Kerinchi LRT station → UM faculty | Not run |

These are probe descriptions, not confirmed listings, stop connections or routes.
For each approved probe record date/time/timezone, endpoint coordinates or place
IDs, requested class/departure time, provider status, named operators/lines, fare
presence/currency, transfer/walk legs, and whether a UM-operated shuttle leg was
actually returned. Keep API content retention within the applicable terms.
If absent, Dafi obtains UM stops/timetables/fare evidence; absence from one request
does not prove the service does not exist. [Coverage page](https://developers.google.com/maps/documentation/routes/coverage).

## Caching and display architecture decision

Do not implement the role file's proposed persistent area/faculty route-response
cache as an assumed permission. The Routes policy permits stored place IDs and
requires attribution; most content caching remains restricted. Detail cards
using Google results need Google Maps attribution, with provider/seeded information
visually distinct. [Routes policies](https://developers.google.com/maps/documentation/routes/policies).

The non-EEA service terms explicitly allow Routes lat/lng caching up to 30 days;
that is not permission to cache arbitrary durations, fares, steps or full results
for 30 days. Confirm the billing account's applicable agreement before selecting
any broader cache. [Service terms, §19](https://cloud.google.com/maps-platform/terms/maps-service-terms).

Pending that review, the provider adapter should persist no Google response
content. Independently sourced approved shuttle data and Jiran-owned listing data
have their own provenance/privacy rules. NFR1 is still unproven: measure the real
authorized end-to-end search, including provider quotas, before promising p95 ≤3s
for 100 concurrent users. A local fixture benchmark cannot establish that claim.

## Credentials and live activation still required

Use the existing `jiran-mad` project. Keep server credentials out of Android/Git;
use a restricted server credential or supported OAuth identity. Android SDK keys
need package `jiran.com`, signing certificate and API restrictions, with separate
keys by application/environment. App Check and server authorization address
different parts of the request path. [Security guidance](https://developers.google.com/maps/api-security-best-practices).

Still required: billing owner/account and budget amount, APIs enabled, supported
credential restrictions for the deployment network, managed secret/access policy,
route probes above, approved fares/factors and schema/UI integration. The next code
batch can implement and contract-test the bounded provider adapter without a key;
activation and real coverage remain explicit gates.
