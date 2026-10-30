# Bounded GTFS schedule processor

**Needs human review.** Implements research components for FR4.1/FR4.3; no
production endpoint, live route guarantee or approved fare configuration.
JavaScript remains the backend language. GTFS parsing is offline preprocessing,
not work claimed to fit a Worker Free request's CPU allowance.

Modules: [normalization](../../backend/functions/src/gtfs.js),
[journey search](../../backend/functions/src/gtfs-journey.js), and
[offline inspection CLI](../../backend/scripts/gtfs-inspect.cjs).
The [official GTFS reference](https://gtfs.org/documentation/schedule/reference/)
defines calendars, service-day times, stop sequences and frequency semantics.

## Supported behavior and limits

- CSV supports BOM, CRLF, quoted commas/newlines and escaped quotes; rejects
  duplicate headers, inconsistent rows, malformed quotes and oversized input.
  Each table is at most16MiB/500,000 rows,128 columns and16,384 characters/field.
- Normalization namespaces IDs by source, uses agency UTC+08 time and preserves
  stop coordinates, pickup/drop-off restrictions and estimated timepoints.
  This processor supports Malaysia/Singapore zones, not arbitrary DST zones.
- Weekday calendars and date-specific additions/removals are evaluated separately.
  Service times may exceed24h, up to the explicit72h bound. Missing intermediate
  times, nonchronological trips, overlapping frequency windows and unsupported
  trip data are quarantined and reported; no interpolation or timetable fallback.
  Shifted frequency runs are also checked against72h; exact grids use their last
  actual start, while non-exact windows use the latest possible start.
- Exact frequency windows generate a grid of first-stop departures before the
  exclusive end time. Downstream stops retain their offsets. Non-exact headways
  use a conservative sample with one headway of waiting, labelled estimated;
  that sample is not a guaranteed maximum wait or an on-time certification.
- Search checks the current, previous two and next service days. Caller selects
  freshness and transfer-buffer policy explicitly. Freshness is distinct from
  calendar operation and optional `feed_info` validity. Coverage is applied
  conservatively to departure dates, service days and arrivals; it may exclude a
  late journey crossing the final declared validity date rather than certify it.
- Cross-stop transfers need explicit links; nearby names or coordinates do not
  create a walking route. Unverified links make the result estimated. GTFS
  prohibited/minimum transfers are enforced from the original alighting stop/time
  even across chained walking links. Timed, in-seat and trip/route-scoped transfer
  rules are conservatively blocked rather than incompletely interpreted.
- Pareto labels preserve both arrival and alighting time for each transfer
  context. Search stops explicitly at200,000 counted work steps,10,000 active
  labels, four transit boardings and six hours. Smaller limits are allowed.
  A search-limit outcome is not evidence that no physical route exists.
- Fares remain null. `onTime` is a comparison to supplied class time for a
  scheduled result only; estimated results return null. Scheduled is still
  timetable evidence, not a live departure, capacity or punctuality guarantee.

## Reproduce an offline inspection

Extract only the required public CSV tables from a verified GTFS archive into
an ignored local directory. No ZIP parser, downloader or provider credentials are
included in the runtime module. Put an inspection specification in an ignored
location such as `.gradle/commute-inputs/spec.json`:

```json
{
  "feeds": [{
    "directory": "../free-transit-research/rapid-bus-kl-tables",
    "source": {
      "id": "rapid-bus-kl",
      "url": "https://api.data.gov.my/gtfs-static/prasarana?category=rapid-bus-kl",
      "fetchedAt": "2026-10-27T00:18:03.775940+00:00"
    }
  }],
  "queries": [{
    "fromStopId": "rapid-bus-kl:1000838",
    "toStopId": "rapid-bus-kl:1000583",
    "departTime": "2026-11-16T07:30:00+08:00",
    "asOf": "2026-10-27T00:30:00Z",
    "maxFeedAgeSeconds": 86400,
    "minTransferSeconds": 120
  }]
}
```

The metadata above belongs to the recorded snapshot. Replace it with the actual
download's provenance; do not copy its date onto a refreshed or different file.
The freshness/buffer values are research choices, not team-approved defaults.
Directory paths are relative to the specification file. From repository root:

```powershell
node backend/scripts/gtfs-inspect.cjs .gradle/commute-inputs/spec.json
npm.cmd test --prefix backend/functions -- --runTestsByPath test/gtfs.test.js test/gtfs-journey.test.js
```

The CLI does no networking. It reports source metadata, table hashes, accepted
trip counts, bounded exclusion samples and probe results. Raw archives, extracted
tables and generated full output stay ignored; source hashes are in
[the snapshot record](../research/free-transit-evidence.md).

## Observed snapshot results

Ran the existing27 October public archives through the JavaScript processor.
Accepted trips: KL bus2,118; KL rail48; MRT feeder6,291. Three bus trip IDs
`weekend_T4069_T406902_0`, `_1`, `_2` were excluded because they have no
`stop_times` rows, despite having frequency records. Other valid trips remain
usable. None of these results establishes fare knowledge.

Used the [five defined probes](../research/commute-input-evidence.md), without inventing
walking transfers. Both the future and current-date queries used the recorded
27 October snapshot/as-of time; future dates are provisional schedule forecasts.

| Probe | Processor result | Meaning |
|---|---|---|
| R1: PKNS/KL Gateway → UM Law,16 Nov07:30 | Estimated arrival08:13:46 UTC+08 | Headway-based; on-time eligibility remains null. This was computed, not an observed bus journey. |
| R2: PKNS → Computer Science via library,16 Nov | No scheduled journey | The unverified final campus connection was not invented. |
| R3: Kerinchi rail → UM Law,17 Nov | No scheduled journey | No physical rail-to-bus walking link was supplied. |
| R4: Pantai Dalam → UM Law,16 Nov07:00 | Estimated arrival08:20:02 UTC+08 | Snapshot alternative uses T7910 → T7890 → a later T7890 run, through shared bus stop1008483 and Universiti east-side1001833. No rail walking link was invented; headway timing remains uncertain. |
| R5: Phileo gate-A → Computer Science,28 Oct07:30 | Scheduled arrival07:50:46 UTC+08 | A timetable computation only; no live vehicle, fare or walking-access validation. |
| R5 repeated16 Nov | No scheduled journey | The feeder calendar does not cover Wave A; other feeds do not extend its service. |

Normalizing feeds and obtaining these results does not close assumption#5. Current
UM shuttle evidence, refreshed calendars, verified access paths, fares and actual
end-to-end coverage checks remain necessary. Pantai Dalam initially hit the work
limit. Tests drove pruning of already-ended service days and dominated arrival
states; it now returns the provisional bus alternative above within the same
bound. General search-limit results remain possible; no NFR1 claim is made.
