# Official GTFS inspection for the no-billing candidate

Downloaded 27 October 2026, 00:18 UTC, from Malaysia's official public GTFS API.
This is **feed-content evidence**, not an end-to-end journey or live service
verification. It advances feasibility research without changing production data.
The three public downloads used no key/payment details and remained within the
[documented four GTFS Static requests/minute](https://developer.data.gov.my/rate-limit).
Raw ZIPs and inspection JSON remain ignored under `.gradle/free-transit-research`.

Source owner: Prasarana via data.gov.my, CC BY 4.0, following the
[official feed documentation](https://developer.data.gov.my/realtime-api/gtfs-static).
All responses reported Last-Modified `Mon, 26 Oct 2026 19:17:24 GMT`.

| Download | ZIP bytes | stops.txt records | routes.txt records | SHA256 |
|---|---:|---:|---:|---|
| [KL buses](https://api.data.gov.my/gtfs-static/prasarana?category=rapid-bus-kl) | 1,697,052 | 4,106 | 139 | `8047eb4045dce80dc7d378fec003cd52d3183e36c3cd8298ea7115932ab0351c` |
| [KL rail](https://api.data.gov.my/gtfs-static/prasarana?category=rapid-rail-kl) | 80,289 | 187 | 8 | `3b00cf0debb428a3533f67c29f9c3c87e09ba9735c05569368a033c9a4491614` |
| [MRT feeder](https://api.data.gov.my/gtfs-static/prasarana?category=rapid-bus-mrtfeeder) | 2,022,514 | 2,117 | 91 | `87c87576b47f9a80c052a67b6e5d2d5e634773f6824d7fc354a7cee5fac5b4a8` |

## Relevant content found

Inspection joined `stops.stop_id -> stop_times.stop_id/trip_id -> trips.trip_id`
to obtain each stop's route/service IDs, then `routes.route_id` and `calendar`.
Names alone are insufficient: a name match elsewhere in the region was excluded
from the relevant candidates. No user listing or private coordinates were used.

- KL bus route `T7890`, public name `T789`, is labelled **Stesen LRT Universiti ~
  Universiti Malaya via Pantai Hillpark**. It serves `1000838` (KL1397 Flat PKNS
  Kerinchi/KL Gateway), `1001650` (KL2134 Flat PKNS Kerinchi), `1000583` (KL2137
  Fakulti Undang-Undang UM), `1002427` (KL1109 Perpustakaan UM), and additional
  campus stops. Joined service IDs are `weekday` and `weekend`.
- KL bus `T7910` serves `1001807` (KL1100 LRT Kerinchi) and `1001045` (KL2140 KTM
  Pantai Dalam); the latter also has `U7828`. This does not prove a timed onward
  connection to a faculty or a usable transfer.
- Rail stops `KJ18` Kerinchi and `KJ19` Universiti join to route `KJ`, labelled
  Kelana Jaya Line, with `MonFri`, `Sat`, `Sun` service IDs. Those calendars end
  **31 December 2026**. Other service calendars in the same archive are expired;
  a router must evaluate the referenced service, not the archive timestamp.
- MRT feeder route `30000016`, labelled `T815`, serves `12000797` Perpustakaan UM
  and `12000800` Pusat Sukan UM. Its joined calendars end **13 November 2026**
  (weekday) and **15 November 2026** (weekend). Its two calendar exceptions remove
  weekday service on9 November and weekend service on2 November; neither extends
  the calendars. These
  records do not establish service for Wave A starting16 November.
- KL bus weekday/weekend calendars end **31 March 2027**. KL bus and rail have
  `frequencies.txt` and no `calendar_dates.txt`; feeder has date exceptions and
  no frequency file. Journey logic must handle service dates, headways and
  exceptions rather than treating every stop-time row as a guaranteed departure.
- None of the three archives contains fare-prefixed files. This inspection
  therefore supplies **no verified fare**. Missing fares remain unavailable until
  separately sourced; they cannot silently become zero.

## Remaining boundary

The official docs flag some omitted KL bus trips due to data accuracy issues.
Prasarana campus stops are not proof of the university's own shuttle service.
UM-operated shuttle timetables and holiday/service exceptions remain with
Dafi/Gathan for sourcing. Refresh feeds before any target travel date, validate
coordinates/access paths, transfer feasibility and actual stop service with the
operator, then run the agreed five real origin-to-faculty probes. This snapshot
does not verify fares, traffic, accessibility, every faculty, Google coverage or
the NFR1 concurrency target. Do not mark assumption#5 fully verified.
