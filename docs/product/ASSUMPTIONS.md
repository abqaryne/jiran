# Numbers and assumptions to verify

Every figure below is a guess in the [PRD](PRD.md) today. Each has an owner who finds the real value and writes it down here, with the source, before the **requirements freeze on 15 Nov**. Until then the app uses the "Current value" as a config value that can be changed without a release.

Rules for finding data:
- Prefer **our own survey** ([SURVEY.md](SURVEY.md)) and **published sources we can cite in the report**.
- Do not scrape sites or apps, and do not reverse-engineer an app's private API. It breaks terms of service and the data would break the demo.
- Write estimates as estimates in the app and name the source.
- **Listings and seed data.** Do not copy listings from PropertyGuru, iProperty or any other site, and do not copy anyone's photos, descriptions, names or phone numbers (site terms, copyright and PDPA 2010). What is allowed:
  1. Note **price facts by hand, in small numbers**: area, room type (master, medium, small) and price range. Keep a sheet with the source (site or channel) and the date, and cite it in the report as "reference prices from public advertisements, October 2026".
  2. Demo and seed listings are **fictional but realistic**: invented units in real areas (for example Pantai Dalam, Kerinchi) with prices modelled on those facts. About 10 to 15 are enough. The app labels them as sample data.
  3. A consenting Telegram poster may be asked for facts about their unit. Never show their name or contact in the app.
  4. The room price weights (#1) come from our own survey, not from advertisements.

| # | What | Current value | Where it is used | Owner | How to find it | Due |
|---|---|---|---|---|---|---|
| 1 | **Room price weights** (master, medium, small) | 1.3 / 1.0 / 0.7 | Default room price split (FR2.1, FR2.4) | **Aly**, then Gathan puts it in config | Survey C5 to C7: for each Unit with all three room types, take the price of each room and work out the ratio to the medium room; use the median across at least 15 Units. If there are fewer, keep the current weights and say so. | 10 Nov |
| 2 | **Peak hour windows** | not set (weekday morning and evening) | Peak vs off-peak columns (P1) | **Aly** (survey E5, E6), **Aaron** checks | Survey answers give when students leave. Aaron compares Routes API travel times at several departure hours on 2 or 3 real routes and sets the windows where times jump. | 12 Nov |
| 3 | **E-hailing fare**: base fare, rate per km and the peak/off-peak ratio | no values yet | E-hailing cost on the commute card | **Aaron** (formula), **Aly** (data) | Grab has no public fare API for us, and Google Maps does not return Grab prices. So: (a) survey E7 answers; (b) each team member opens the Grab fare estimate screen (no booking) for about 10 real routes from Pantai Dalam, Kerinchi, Bangsar South and PJ to UM, once around 8:00 and once around 14:00, and logs distance, time and price in a shared sheet; (c) fit a base fare plus a per-km rate, and take the peak/off-peak ratio as the surge. Show it as an estimate. | 13 Nov |
| 4 | **Car cost**: fuel price, fuel use per km, tolls, parking | no values yet | Drive-alone and shared car cost | **Aaron**, **Dafi** (parking) | Fuel: the official pump price published by the government (it changes, so keep it in config). Fuel use: state a typical figure and its source. Tolls: check whether the Routes API returns toll estimates. Parking: Dafi asks UM in the same email as the shuttle question. | 13 Nov |
| 5 | **Public transport fares and coverage** (bus, LRT, MRT, UM shuttle) | unknown | Public transport option, route steps | **Aaron**, **Dafi** (UM transport email) | Aaron tests Routes API transit on 5 real routes and notes whether fares and the UM shuttle appear. If a fare is missing, use the operator's published fare chart and seed it. Dafi's email to UM transport asks for shuttle stops, times and whether it is free. | 8 Nov (coverage), 13 Nov (data) |
| 6 | **CO₂ emission factors** per mode (petrol car, e-hailing car, bus, LRT/MRT, motorbike) | no values yet | CO₂ on the commute card | **Aaron** | Pick one published source we can cite in the report (a Malaysian government or agency figure if one exists, otherwise a well known international conversion-factor table), and use its figure for each mode. Write the source in the app. | 13 Nov |
| 7 | **Utilities estimate** (water, electricity, internet) per person per month | RM 62 (sample) | True monthly cost | **Aly** (survey C9), **Gathan** seeds | Median of survey answers, grouped by number of people in the Unit. | 10 Nov |
| 8 | **Data retention periods**: contract upload 30 days, closed Household 6 months, unattached Evaluation 90 days, Listing 30 days | as listed | Privacy rules, scheduled deletion | **Gathan**, **Dafi** | Read the Retention Principle of the Personal Data Protection Act 2010 (data is kept no longer than needed for its purpose) and write one line of reasoning per period. If still unsure, Dafi asks the lecturer in the next email. | 13 Nov |
| 9 | **Match score weights** (sleep time, cleanliness, noise, cooking, smoking, pets, budget) | not set | Match score formula | **Aaron** (formula), **Aly** (input) | Survey F2 ranks what matters most when choosing a housemate. Turn the ranking into weights. | 13 Nov |
| 10 | **AI provider, cost and quota** (Gemini or Claude) | not decided | All AI features | **Ilham** | Compare free-tier limits, price per request, speed and Malay quality on the 10 sample contracts and 20 sample requests. Recommend one. | 8 Nov |
| 11 | **Google Maps Platform cost and quota** (Routes, Places) | unknown | Commute, place search | **Aaron** | Check the free monthly credit and estimate calls per day, with caching. Set a billing alert before using the key. | 8 Nov |

## Status

Keep this updated as you go: put the value found and the source in the "Current value" column, and add "Verified" with the date at the end of the row.
