# Commute CO2 methodology and source catalogue

Source extraction checked **27 October 2026**. Version `uk-desnz-2026-co2-v1`.
These factors are **UK proxies**, not measured Malaysian fleet, occupancy or grid
intensities. They require explicit `allowUkProxy: true` in the calculation input.
They are not enabled by an environment default or live callable.

## Source selection

Selected the CO2 component in the [UK DESNZ 2026 conversion factors](https://www.gov.uk/government/publications/greenhouse-gas-reporting-conversion-factors-2026),
using the July-revised flat file and checking each value against the full workbook.
The Introduction (rows 34–36) distinguishes total CO2e from individual gas components.
CO2 has GWP 1; its component represents CO2 mass. The catalogue does not use the
total CO2e column or combine it with a CO2-only label.

Malaysian references were considered. [MITI's IESG starter kit](https://www.miti.gov.my/miti/resources/IESG/Booklet_Stater_Kit.pdf)
p. 70 (PDF page 78), section 3.2.6.3, labels its transport table CO2e and cites EPA 2022.
It is not a local measured CO2-only table. [MGTC's calculator](https://lcosbasic.mgtc.gov.my/)
uses DESNZ for fuel inventories and a Malaysian source for electricity. That does
not validate UK rail electricity factors for Rapid KL. A Malaysia-specific
replacement needs compatible gas, passenger basis, scope and documented fleet/grid
data; it must not silently mix incompatible factor boundaries.

## Selected values

All numbers below use the CO2-specific component. Original worksheet values are
kg per unit; code stores exact integer milligrams per unit. For one traveller in
a vehicle, vehicle-km has the same distance as their journey. Multiple travellers
require explicit allocation; passenger-km factors already contain passenger allocation.

| Catalogue ID | gCO2/unit | Basis | Revised flat row/cell | Full workbook check |
|---|---:|---|---|---|
| uk-average-petrol-car | 160.84 | vehicle-km | 1801 / J1801 | Passenger vehicles!I54 |
| uk-regular-taxi | 206.38 | vehicle-km | 5434 / J5434 | Business travel- land!E72 |
| uk-average-motorbike | 111.38 | vehicle-km | 1885 / J1885 | Passenger vehicles!E66 |
| uk-average-local-bus | 100.77 | passenger-km | 5454 / J5454 | Business travel- land!E81 |
| uk-light-rail | 20.93 | passenger-km | 5470 / J5470 | Business travel- land!E89 |
| uk-metro | 15.22 | passenger-km | 5474 / J5474 | Business travel- land!E90 |

Values, original kg strings, factor IDs, units, source cells, file URLs and SHA256
hashes are in [the catalogue](../functions/config/emission-factors.json). UK light
rail/tram and Underground are explicit proxies for a proposed LRT/MRT comparison,
not measurements of those Malaysian services. Motorbike is retained for source
research; it does not add a fifth mode to the four-mode product.

Walking zero is a separate **Jiran powered-operational boundary**, excluding food,
equipment and lifecycle emissions. It is not a walking factor attributed to DESNZ.
No upstream/WTT or lifecycle factors are added to the operational transport set.

## Taxi and occupancy limitations

The [2026 methodology](https://assets.publishing.service.gov.uk/media/6a2940543b15d05a7ce3202e/2026-GHG-conversion-factors-methodology-report.pdf),
p. 70, paragraphs 5.38–5.41, describes a 1.4 passenger average for regular-taxi
passenger-km factors and excludes cruising/empty running. Jiran selects the
**vehicle-km** row and requires explicit traveller occupancy rather than silently
using 1.4. For an individual e-hailing trip, a caller must deliberately supply one
fare-paying traveller, excluding the professional driver from that allocation.
Unknown pickup/cruising distance remains excluded and disclosed. No Grab-specific
measurement, local fleet match or pickup-distance estimate is inferred.

Vehicle occupants in the library are allocation inputs (1..100 is a numerical
bound, not a vehicle-capacity rule). The production adapter must validate the
actual mode/party: driving alone uses one traveller. Passenger-km factors reject
an occupancy override so they cannot be divided twice.

## Calculation and incomplete routes

`calculateTripEmissions({distanceMetres,legs,allowUkProxy})` in
[emissions.js](../functions/src/emissions.js) consumes normalized leg distances and
factor IDs. It does not infer a bus/rail split from a whole-route distance or assign
bus emissions to a walking leg. The caller must establish that leg IDs match the
real modes and actual distances; this function is not provider-route verification.

Sum exact rational emissions across legs using integer mass units, then round
half-up once to whole grams. Example: walking 1 km + bus 2 km + light rail 3 km gives
0 + 201.54 + 62.79 = 264.33 g, displayed as **264 g CO2** under the explicit UK proxy.
This is a calculation example, not a measured UM commute.

Every metre of the route must be accounted for by supplied legs. Unknown factor,
empty leg array, missing vehicle occupancy, disabled UK proxy, or a leg-distance sum different
from route distance yields null CO2/source and a specific unavailable reason.
There is no guessed rounding tolerance or leftover-distance allocation. Real
provider summary/step rounding needs inspection in route probes before activation.
An omitted/non-array `legs` field is malformed and fails; `legs: []` is the explicit
`missing_legs` state. Other malformed present values fail. Unknown emissions do not remove a costed on-time
route or turn its monthly total into zero.

The pricing layer validates and copies optional `emissionProfile` facts into the
monthly calculator. Legacy explicitly supplied `emissionFactor` remains supported
for fixtures/config, but cannot be combined with a leg profile. Published factors
do not change the existing synthetic fixture's 300 g result or claim it is verified.

## Refresh and provenance

Download the exact official flat/full file URLs recorded in the catalogue. Check
hashes, metric labels, record IDs and units; independently match the full-set cells.
Convert kg to integer mg with decimal arithmetic. Missing spreadsheet cells remain
missing, not zero. A changed source requires a new version and reviewed regression
results. The workbook files are unchanged and excluded from Git; no spreadsheet
authoring or production Python dependency was added.

Contains public sector information licensed under the
[Open Government Licence v3.0](https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/).
