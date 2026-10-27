# Commute input evidence and five route probes

Checked27 October2026. Research for FR4.1–FR4.3 and assumptions#3–#5; **needs
human review**. These findings are not approved production defaults. No paid API,
booking, account signup or private-app scraping was used. Distances, traffic,
transfer walking and arrival eligibility remain unmeasured until stated otherwise.

## Published costs and eligibility

| Input | Primary evidence | Application decision / missing evidence |
|---|---|---|
| My50 | The government's [Ihsan MADANI page](https://www.ihsanmadani.gov.my/inisiatif/pengangkutan/pas-perjalanan-tanpa-had-my50) identifies Malaysian citizenship and RM50 for eligible Prasarana rail/bus travel. [TNG Digital's pass page](https://www.tngdigital.com.my/gotravel/travel-pass/) describes30 days of eligible unlimited travel. | Do not assign this subsidy to every UM student. Verify eligibility, activation/validity and included legs. KTM, university shuttles and unrelated operators are not automatically included. |
| Student concession | The operator's Rapid Pelajar page and official December2025 FAQ PDF returned403 when opened. Search excerpts alone were insufficient to establish all current terms. | No blanket50% student discount adopted. Confirm current citizenship, card and expiry conditions with operator evidence before estimating. |
| Fuel | MOF's [official data catalogue](https://data.gov.my/data-catalogue/fuelprice), effective22 October2026, has separate general, BUDI95 and SKPS series. Catalogue update21 October23:59; next scheduled update28 October23:59. | Refresh before using a later travel date. A subsidized price needs verified eligibility; SKPS is not a generic student-car price. Vehicle consumption and fuel type still need explicit input. |
| UM parking | [UM Auxiliary Police SMART](https://smart.um.edu.my/) provides vehicle-sticker applications. Its public page supplies no general student parking tariff. | Parking and permission remain unknown. A sticker/application is not evidence of zero daily cost or a guaranteed space. Do not reuse hospital, visitor, another university or unofficial social-media rates. |
| E-hailing | [Grab's operator explanation](https://www.grab.com/my/improved-earnings-structure/) distinguishes passenger upfront fares from driver earnings and pickup compensation. | A driver-payment formula is not a passenger fare table. Base/km/surge and pickup assumptions need dated quotes or approved survey evidence. No booking was made and no universal numeric fare is claimed. |

The public [fuel CSV](https://storage.data.gov.my/commodities/fuelprice.csv) was
downloaded to ignored `.gradle/commute-inputs/fuelprice.csv`. Its SHA256 is
`79e31d85be6f5962cc8635f7feebb2528f7c8bc5b640351e76cec41dab634391`.
Selected record: `series_type=level`, `date=2026-10-22`, values in RM/litre:

| Column | Value |
|---|---:|
| ron95 | 4.32 |
| ron95_budi95 | 1.99 |
| ron95_skps | 2.05 |
| ron97 | 4.80 |

These are dated observations, not November prices or eligibility determinations.
The dataset is MOF-sourced, CC BY4.0. Its historical change rows are not price
levels; the extraction explicitly selected `level`. No fuel-price value entered
runtime configuration. Fuel consumption, parking and toll gaps still prevent a
complete driving estimate.

A pass is a once-per-validity-period cost, not an ordinary per-trip fare. The
current explicit per-trip pricing model does not implement pass selection,
validity overlap or sunk-cost allocation. Do not divide RM50 by an arbitrary
trip count and silently treat that as a universal fare. Team approval of the
monthly accounting period, return journeys and pass treatment is still needed.

## Shuttle evidence and freshness

UM's [Transportation & Travel page](https://www.um.edu.my/transportation-amp-travel)
names Pantai Dalam/Kerinchi/Vista and campus shuttle areas, but explicitly shows
**Last Update:30 December2019**. It supplies no current dated timetable here.
Its listed free special-event services do not establish that every campus commute
is free. Do not turn an old route description or a2026 blueprint proposal into
an operating2026 schedule. Dafi/Gathan still need current UM/operator evidence
for stops, eligibility, fares, headways, semester/holiday exceptions and access.

The [official GTFS inspection](free-transit-evidence.md) establishes public
Prasarana stops and route/calendar references, not the university's own shuttle
operation. The MRT-feeder archive's calendars expire before Wave A. Missing fare
files cannot become zero-cost legs.

## Five concrete public-stop probes

Origins are public stop proxies, **not student listing addresses**. All times are
Asia/Kuala_Lumpur. Departures below are chosen research inputs, not observed
student timetables; a09:00 class deadline is a test assumption only. Target
faculties/stops still need final walking/access validation. No probe currently
has a measured end-to-end time, confirmed price or on-time result.

| Probe | Public origin → destination / selected departure | Confirmed feed references | Unverified connection / expected boundary |
|---|---|---|---|
| R1 | Flat PKNS Kerinchi/KL Gateway `1000838` → UM Law `1000583`;16 Nov07:30 | Both join KL-bus `T7890` (T789). Origin3.113292,101.662546; destination3.118674,101.661071. | Ordered trip/direction, frequency wait, active date, path to faculty and fare must be evaluated. Route membership alone is insufficient. |
| R2 | Flat PKNS Kerinchi `1001650` → UM Computer Science, using library `1002427` as a possible campus interchange;16 Nov07:30 | Both listed bus stops join `T7890`; library3.1211198097451,101.6535801967. Computer-science feeder stop `12000802` is3.127692,101.650261. | Library is not the faculty. The final walk or onward shuttle needs actual evidence; no implicit transfer or library-to-faculty duration. |
| R3 | Kerinchi rail `KJ18` → Universiti `KJ19` → UM Law `1000583`;17 Nov07:30 | Both rail stops join `KJ`; T789 serves east-side Universiti bus stop `1001833` and Law. | A nearby similarly named stop `1001410` does **not** join T789 in this snapshot. Verify exit, road crossing, transfer walk and chronological departures. |
| R4 | KTM Pantai Dalam bus stop `1001045` → Kerinchi bus `1001807`, then possible rail/T789 connection to UM Law;16 Nov07:00 | Both bus stops join `T7910`; the origin also joins `U7828`. Kerinchi rail is separately `KJ18`. | Bus/rail stop proximity does not create a transfer. This is a multi-leg candidate with direction, transfer, fare and time gaps; no connection claimed usable. |
| R5 | MRT Phileo Damansara gate-A bus stop `12000750` → UM Computer Science `12000802`;28 Oct07:30, repeat16 Nov07:30 | Both join feeder `30000016` (T815). Origin3.128419,101.642747. |28 Oct is within the supplied weekday calendar, subject to ordered trip checks.16 Nov is outside it: return expired/missing service evidence, never fabricate an extension. |

Stop coordinates and memberships were joined directly from the three ignored
public ZIPs whose hashes and source URLs are recorded in
[free-transit-evidence.md](free-transit-evidence.md). The joins followed
`stop_times.trip_id → trips.route_id`; the coordinate-name filter excluded
similarly named institutions outside the UM area. Generated join notes remain
ignored in `.gradle/commute-inputs/route-evidence.json`.

Next, run bounded schedule evaluation on these feed references, refresh feeds for
the intended travel date, confirm physical transfer paths and current fares, and
collect real observed outcomes. Keep route availability, source freshness, fare
knowledge and arrival eligibility as separate fields. Assumption#5 remains open
until the relevant complete journeys and university shuttle evidence are verified.
