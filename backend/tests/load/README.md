# Local authenticated load preparation

**Needs human review.** `commute-fixture.k6.js` measures the existing synthetic
commute callable, not listing search, Firestore reads, routing providers or an
Android page. Passing this workload never certifies production NFR1. The original
`search.k6.js` is still an incomplete starter for the eventual agreed search API.

The canonical PRD says p95 below3s at100 concurrent users; AGENTS also requests
50-user latency and100-user operation. This harness covers both50 and100 VUs
with the stricter p95 threshold. These are concurrent simulated users with
one-second think time, not100 continuously outstanding HTTP requests.

## Run locally

Install or extract the [official k6 release](https://github.com/grafana/k6/releases).
The inspected Windows binary was2.3.0; its ZIP matched the official release
checksum `112276d495e5741c968e2bc09ea6196099c1275bd6db9ee0875d173c7148ce43`.
The binary remains ignored, not a repository dependency. No Grafana Cloud account
or paid provider is needed. Node22, Java and the project's local Firebase tooling
are required as described in the [repository README](../../../README.md).

PowerShell, from repository root, using your verified local k6 executable:

```powershell
$env:K6_BINARY = (Get-Command k6).Source
$env:FUNCTIONS_DISCOVERY_TIMEOUT = '30'
cd backend
npx.cmd -y firebase-tools@15.34.1 emulators:exec --project demo-jiran --only auth,firestore,functions,storage 'node scripts/run-fixture-load.cjs'
```

For an extracted portable binary, set `K6_BINARY` to its absolute path instead.
For a short functional check, set `LOAD_PHASE_SECONDS=2`; the manifest records
that duration. Default full runs use60 seconds per measured plateau. A short
smoke run must not be reported as a full load result.

The runner creates100 distinct verified synthetic Auth accounts and one unverified
account. It refuses a non-loopback Auth host. The k6 endpoint is fixed to
`http://127.0.0.1:5001/demo-jiran/us-central1/getCommuteOptions` and follows no
redirects. Per-user tokens stay in ignored `.firebase/load-accounts.json`, are not
command-line arguments, and are removed after execution. A fault-injection test
checks that a missing binary cannot leave the file behind. Usage reporting and
inherited k6 output integrations are disabled for this local run.

The measured protocol has a5-user warmup, separate50/100-user plateaus, staggered
first requests and one-second think time. Users rotate on-time, late and
missing-data scenarios. Checks validate fixture labels, scenario identity and
expected totals/states, not only HTTP200. Participation counters require every
user to complete a first request. Five serial probes check anonymous, unverified,
invalid-token, invalid-scenario and caller-UID spoofing rejections.

`http_req_failed` expects the correct status for each operation, so intentional
401/403/400 responses are not counted as transport failures. All semantic checks
must pass. Each measured phase must have p95 below3000ms. The5 negative iterations
and50/100 participation counts have their own thresholds.

## Evidence and observed results

Full raw summaries, logs and manifests stay ignored. `load-summary.json` contains
metrics; `load-manifest.json` records time, toolchain, Git head/dirty state and
source hashes. Future manifests include the backend fixture calculation inputs,
not just the load generator. Preserve failed results when running comparisons.
The legacy JSON threshold booleans indicate failure (`true`), not success; process
exit status and the printed threshold checks are the primary result.

Both runs below used60-second50/100-user plateaus on this Windows machine with
Node22.14.0, k6 2.3.0 and Firebase CLI15.34.1. Emulator and generator shared the
machine; no controlled hardware or cloud capacity claim is made.

| Run on27 October2026 |50-user p95 |100-user p95 | Checks / outcome |
|---|---:|---:|---|
| First measured run |16.96846ms |3495.26072ms |5 rejection probes passed;33 positive requests failed out5,812 total. Semantic and100-user latency thresholds failed. |
| Second,08:39:49–08:42:14 UTC |22.18493ms |16.19116ms |50/100 participants accounted for; all8,914 checks and5 rejection probes passed; zero failed HTTP requests. All thresholds passed. |

The second run included the added participation counters; both retained the same
latency/concurrency targets. This variability is evidence against claiming
production reliability from the local result. The first run observed80 distinct
Functions runtime PIDs, but no controlled CPU/memory profile was recorded; its
specific bottleneck is not established. No latency threshold or workload size was
weakened to obtain a passing result.

The measured second run used Git head `7b007f6` with uncommitted load files; its
workload/helper SHA256 values are respectively
`811b5409d1db23ed9d58571f25b4aaaff182c32ba3e521d91443d7d277a590e7` and
`96e9062dc1a0794b21a5b10433cee7021bb26d5c40a5fd76504c10d52e54e257`.
Backend fixture calculation files were unchanged from that head. Subsequent
runner changes only extend token cleanup and evidence hashes; they were checked
with isolated tests, not represented as another full measured run.

## Overload and remaining production checks

The Functions fixture has no production quota endpoint. Separately,
[SQLite runtime tests](../../../experiments/worker-feasibility/test/quota.test.mjs)
send30 competing reservations and assert admitted/rejected counts, including
429 responses in the local test harness. This is primitive overload evidence,
not production gateway admission control. Do not invent production429 coverage.

Before reporting NFR1, agree the real search contract/data/architecture, verify
identity-bound quotas and provider limits, run representative real queries and
measure the complete app/backend flow at both loads. Include cold/warm behavior,
error/overload results, test duration, source commit and environment. UM eligibility,
Firestore privacy and real route coverage remain separate acceptance gates.

Fast regression checks run in CI without downloading k6 or starting a load run:

```powershell
npm.cmd run test:load --prefix backend/functions
```

Aaron should read the scenario/helper/runner, run the checks, and explain the
cohort sizes, semantic assertions, token lifecycle, expected errors and why the
failed first run remains in the evidence. Personal review is still TODO (owner).
