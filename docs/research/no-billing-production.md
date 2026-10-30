# No-billing production research

27 October 2026. User decision: **free only**, followed by **research a no-billing
production alternative**, retaining Firebase Authentication and Firestore.
This is a candidate architecture, not an adopted runtime migration or a deployed
production service. Java Android and existing JavaScript calculation code remain.

The strongest candidate is **Firebase Spark + a JavaScript Cloudflare Worker +
openrouteservice for walking/driving + a separately built timetable router**.
It can support a small deployment within quotas, but it is not yet a verified
replacement for every Aaron requirement. In particular, no verified free transit
router or UM shuttle feed has been integrated, and the original concurrency and
traffic requirements are unresolved. No account, key, billing or paid plan was
created or activated during this research.

## Services and limits

| Part | Candidate | Evidence and practical boundary |
|---|---|---|
| Email/password login | Existing Firebase Auth on Spark | Keep `jiran-mad` and `jiran.com`; no migration or duplicate app. [Firebase pricing](https://firebase.google.com/pricing) lists Spark without a payment method. Retain verification and abuse limits; no paid SMS dependency. |
| Database | Existing Standard Firestore on Spark | [Free quotas](https://firebase.google.com/docs/firestore/quotas): 1 GiB, 50,000 reads/day, 20,000 writes/day, 20,000 deletes/day, 10 GiB outbound/month. Only one free database/project; no billing-only backup/PITR/TTL features. Usage exhaustion must produce unavailable states. |
| Trusted calculations/API | Cloudflare Workers Free, JavaScript | [Pricing](https://developers.cloudflare.com/workers/platform/pricing/) and [limits](https://developers.cloudflare.com/workers/platform/limits/): 100,000 requests/day/account, 10 ms CPU/request, 128 MB, 50 subrequests. Network wait is outside CPU time. Free signup is offered in the [official workshop](https://developers.cloudflare.com/labs/workers). Account onboarding and full auth/calculation CPU benchmark are still needed. Use the free `workers.dev` hostname, no domain purchase. |
| Walking and road estimates | HeiGIT/openrouteservice Standard | [Official homepage](https://openrouteservice.org/) offers a free Standard key through HeiGIT; [current FAQ](https://giscience.github.io/openrouteservice/frequently-asked-questions) documents default 2,000 directions calls in a 24-hour window starting at the first request, and 40 per sliding 60 seconds. Confirm the actual owner's dashboard allowance before activation. [Profiles](https://giscience.github.io/openrouteservice/run-instance/configuration/engine/profiles/) include walking/car. No key or live Malaysian query tested. |
| Atomic quota coordination | SQLite-backed Durable Object on Workers Free, candidate only | [Official pricing](https://developers.cloudflare.com/durable-objects/platform/pricing/) permits SQLite-backed objects on Free: 100,000 requests/day, 13,000 GB-s/day duration, 5 million row reads/day, 100,000 row writes/day and 5 GB storage. Exceeding a Free allowance fails operations. KV-backed namespaces require Paid. No object, storage adapter or concurrent/persistence test is implemented yet. |
| Bus/rail timetable input | Malaysia official GTFS Static | [Government documentation](https://developer.data.gov.my/realtime-api/gtfs-static) provides Prasarana KL bus, MRT feeder and rail ZIP feeds, CC BY 4.0. These are schedules/geography, not a ready-to-call journey planner or complete fare engine. It also documents omitted bus trips due to data issues. Coverage/validity/fare completeness must be inspected. UM shuttle coverage is not established. |
| Opening directions | External Maps URL | [Google Maps URLs](https://developers.google.com/maps/documentation/urls/get-started) require no API key. Useful as a user navigation link; cannot supply numeric travel results to our calculator. No scraping or importing Maps UI results. |
| Listing photos, broader team impact | Cloudinary Free, candidate only | [Official free-plan FAQ](https://cloudinary.com/documentation/developer_onboarding_faq_free_plan) allows production within allowance without financial details; [billing docs](https://cloudinary.com/documentation/billing_and_plans) list 25 monthly credits shared across usage. Requires team agreement, authenticated signed uploads, deletion/ownership rules and size limits. Never expose upload signing secrets or put image blobs in Firestore. |

Firebase [Functions deployment requires Blaze](https://firebase.google.com/docs/functions/get-started).
Google [Routes requires billing](https://developers.google.com/maps/documentation/routes/usage-and-billing).
Firebase [Storage requires Blaze starting February 2026](https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024).
Their no-charge allowances do not satisfy the no-billing constraint. Existing
Functions/Storage emulator work remains useful locally; none is activated live.

## Security and compatibility design to prove

The proposed flow is Java Android -> authenticated HTTPS Worker -> authorized
Firestore reads and routing adapters -> existing deterministic calculations.
Keep Firebase SDK login. A Worker must verify the Firebase ID token's signature,
issuer/audience/project, expiry, subject and verified email using a maintained
JWT implementation and Google's published keys, following [Firebase token
verification requirements](https://firebase.google.com/docs/auth/admin/verify-id-tokens).
Decoding a JWT is insufficient. Verified email is not UM affiliation; the approved
UM policy and resource visibility still belong in authorization.

Forward the user's ID token to [Firestore REST](https://firebase.google.com/docs/firestore/use-rest-api)
so Gathan's Security Rules apply. Do not install an unrestricted Admin credential
or assume Firebase callable auth middleware exists in a Worker. The current
`session` helper consumes already-verified callable context; an HTTP adapter must
construct that context only after verification. SDK callable transport cannot be
repointed to an arbitrary Worker without a deliberate protocol/client change.
Audit matching candidate queries against privacy rules before implementation;
user-token reads may not expose the full private profile required by old drafts.

Provider keys stay in Worker secrets. Enforce per-user and account-wide quotas
atomically before calls, including minute limits and failed attempts. An
in-memory counter or eventually consistent KV counter is insufficient. Choose
and benchmark a free transactional store before adopting the runtime; do not
claim the existing injected quota adapter implements persistence. Limit request,
response, candidate count and route legs; reject overload, never upgrade plans.
No logs may contain tokens, coordinates, private schedules or provider keys.

The current [HeiGIT FAQ](https://giscience.github.io/openrouteservice/frequently-asked-questions)
states that each API key belongs to one person and recommends server-side
requests for applications. Assign an accountable individual owner; do not share
an account or embed a key in Java. Its FAQ confirms the minute limit as a rolling
window; a fixed calendar-minute counter would not enforce that provider limit.

Reuse pure JavaScript pricing, emissions and match modules through a tested
bundle; do not assume Firebase Admin/Functions SDKs work inside Workers. Validate
CommonJS bundling, WebCrypto/JWT overhead and total CPU on the real Free runtime.
The existing 2.8s service deadline is not a 10 ms CPU benchmark or NFR1 proof.

## Delivery tradeoffs and alternatives

1. **Worker + openrouteservice + curated transit (recommended feasibility spike):**
   preserves server-side numeric calculations and Firebase data. Walking/driving
   are plausible; transit needs preprocessing and a bounded journey algorithm,
   stop access legs, service dates/time zones, transfers, fares and source expiry.
   Parse large GTFS ZIPs offline during data preparation, not on each Worker
   request. Return `missing_schedule` where coverage cannot be proven. Static road
   estimates cannot be presented as live traffic or actual Grab fares. For now,
   retain e-hailing as a clearly labelled approved formula estimate.
2. **Firebase Spark with Android-only calculations:** fewer services, but cannot
   enforce trusted server scores/provider-secret quotas and conflicts with the
   specified backend ownership. Not recommended as an equivalent implementation.
3. **Self-hosted routing/Node on existing team hardware:** preserves more of the
   runtime, but public HTTPS, uptime, security, electricity and connectivity need
   an accountable host. Free software alone does not establish free production
   hosting. Do not rely on public demonstration routing servers as an SLA.

At the advertised Standard minute limit, 100 cold commute requests needing two
road/walk calls each would need 200 directions calls, exceeding 40/minute. Thus
that configuration cannot establish NFR1's p95 <=3s at 100 concurrent users.
Provider-authorized reuse, a higher genuinely free approved allowance, a bounded
precomputed coverage set, or a team requirement change is needed. No performance
requirement is marked complete. Current Google request-sharing policy also cannot
simply be transferred to a new provider without checking its terms/attribution.

## Next batches and acceptance gates

Initial public-feed inspection is now recorded in [free transit evidence](free-transit-evidence.md):
campus stop/trip references exist, but fare files are absent and some service
calendars expire before Wave A. These are specific feasibility findings, not a
working timetable router or route-coverage completion.

An isolated [local Worker experiment](../../experiments/worker-feasibility/README.md)
now verifies generated RSA-signed tokens and runs the existing commute/match
fixtures in workerd. Its 25 tests and bundle checks support code compatibility;
the live-key/cache/revocation, database/quota, Free CPU and deployment gates below
remain open. No Android transport or production runtime has been replaced.

- **A: local feasibility.** Inspect current HeiGIT signup/terms and quota; download
  public GTFS for actual coverage analysis with source hashes; build an isolated
  Worker HTTP/auth bundling spike. No deployment or service replacement yet.
- **B: authorized data boundary.** Agree normalized listing/profile/timetable
  contracts with Gathan/Ary, add rules and cross-user tests, select atomic free
  quota storage. Confirm UM policy and survey fares/trips/weights.
- **C: free-account verification.** Connect appropriately owned Free accounts without
  payment information (HeiGIT keys have an individual owner), securely provision
  free keys, run a small route sample and
  Free-runtime CPU checks. If signup requires billing, stop that candidate.
- **D: product integration.** Implement HTTP client/retry/error states in Java;
  display attribution, static/unknown coverage and freshness; QA both missing and
  available routes, quota exhaustion, token expiry and permissions. Resolve NFR1
  before claiming a complete production alternative. Keep PR review/merge with Ary.

Ilham's paid AI service choice and the team's photo/FCM flows need their own free
compatibility checks. This research does not certify the whole team's production
system. Research authorization does not record team approval of a runtime change.
