# Local Worker feasibility experiment

This is research for Aaron's **free-only** backend alternative. It runs the
existing JavaScript commute/match fixture calculations and Firebase-style token
verification in local Cloudflare `workerd`. It is not the app's backend and has
no deploy command, cloud account, production credentials or live database access.
The Java Android and Firebase Functions code/transport remain unchanged.

From the repository root, with Node 22:

```powershell
npm.cmd ci --prefix experiments/worker-feasibility
npm.cmd test --prefix experiments/worker-feasibility
npm.cmd audit --prefix experiments/worker-feasibility
```

Tests bundle shared CommonJS modules with esbuild, start local workerd through
Miniflare, and dispose their runtimes on completion. They generate RSA keys only
in memory; no test key, JWT or Firebase configuration is written to disk. Every
outbound request is intercepted by a fixed synthetic Google JWKS handler; other
requests fail and are asserted absent. Miniflare telemetry is explicitly disabled
and its listener/inspector bind to 127.0.0.1. No Cloudflare login is needed.

Worker guards require loopback, local-feasibility mode and `demo-jiran`. Local
health is labelled fixture data. Authenticated POST paths `/commute-fixture` and
`/match-fixture` accept only the existing scenario selector; no caller-supplied
UID, arbitrary coordinates, monetary inputs or candidate profiles are accepted.
Responses remain labelled fixture/research-only, use `Cache-Control: no-store`,
and omit verified token/email context. Bodies are capped at 2048 bytes.

The verifier uses JOSE/WebCrypto RS256, exact demo project issuer/audience,
required exp/iat/auth_time/sub and recognized kid, verified email and no
unconfigured tenant. It ignores caller-selected key URLs. Its fixed key URL uses
the [official Google service-account public JWK format](https://docs.cloud.google.com/iam/docs/service-account-creds),
for `securetoken@system.gserviceaccount.com`. Firebase's [third-party verification
requirements](https://firebase.google.com/docs/auth/admin/verify-id-tokens) define
the claims; JOSE's [remote resolver](https://github.com/panva/jose/blob/main/docs/jwks/remote/functions/createRemoteJWKSet.md)
performs signature/key selection. Generated-key tests exercise crypto and HTTP
behavior; they do not prove real Firebase token acceptance or live key retrieval.

## Toolchain finding

The initial Miniflare 4 stable tag passed tests but its dependency audit reported
three high-severity package entries through sharp/undici. The package now pins
Miniflare`5.20261023.0-beta`, the version required by Wrangler`4.153.0` at inspection.
It uses workerd`1.20261023.1`, compatibility date`2026-10-23` and the exported v4
options converter. Its test-only outbound handler replaces the removed fetchMock
API. This is an explicit **prerelease development dependency**, not an adopted
production runtime requirement. Fresh audit reports no known vulnerabilities for
the locked experiment dependencies; that is not a security-completion claim.

## What remains before adopting the alternative

- Real project token/key validation, key-rotation/error behavior and cache policy.
  This prototype sets JOSE cacheMaxAge to60 seconds and cooldown to30 seconds;
  it does not implement Firebase's response Cache-Control/max-age refresh policy.
  Resolve that before production. Verify revocation/disabled accounts, tenancy
  and approved UM affiliation policy rather than treating verified email as UM.
- Firestore authorization, private matching projection and per-user/global atomic
  quota persistence. No service-account/Admin SDK workaround is included.
- Approved fares, trips/weights, real routing, GTFS journey logic/freshness and
  UM shuttle evidence. Fixtures prove module compatibility only.
- HTTP deadlines/abuse controls and Android transport integration; this local
  fixture reader is byte-bounded but has no slow-request deadline.
- Actual Free-account signup/quotas, deployed CPU limits and NFR1 p95 at100
  concurrent users. Local test elapsed time is not billed CPU or a cloud benchmark.
- Team agreement on the proposed runtime/provider change and the broader
  photo/AI feature alternatives. Existing Node Functions work remains local.

See [candidate architecture](../../docs/no-billing-production.md),
[GTFS snapshot](../../docs/free-transit-evidence.md), and
[plan/evidence ledger](../../docs/superpowers/plans/2026-10-27-worker-feasibility.md).
