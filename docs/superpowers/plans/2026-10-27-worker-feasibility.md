# Local Worker feasibility implementation plan

> Execute inline using executing-plans, TDD and official Firebase Auth guidance;
> independent review before committing. Continue on the existing authorized
> feature branch. No deployment or runtime adoption.

**Goal:** Prove that the existing JavaScript commute/match fixtures and maintained
JOSE signature verification can run together in a local Cloudflare workerd runtime.

**Architecture:** An isolated `experiments/worker-feasibility` package bundles the
existing pure CommonJS helpers into an ES module. Node's test runner drives
Miniflare/workerd with generated-in-memory RSA keys and a mocked fixed Google JWKS
endpoint. No Firebase Admin SDK, Firestore reader, service account or provider key.

**Spec:** `docs/no-billing-production.md` batch A, `team/aaron.md`, Firebase's
third-party ID-token verification requirements. This research does not adopt
Workers, change Android transport, approve schemas or satisfy NFR1.

## Constraints and design rulings

- Existing Java Android/Node Functions behavior stays unchanged. Research remains
  in its own npm package; pinned dependencies/lockfile, no deploy script/account.
- Worker accepts requests only at loopback with an explicit local-feasibility
  binding and `demo-jiran` project. Reject other hosts/project/mode before auth.
- `POST /commute-fixture` and `/match-fixture` accept exactly `{scenarioId}` via
  bounded JSON (2048 bytes), delegating to existing fixture helpers. `/health`
  reports local fixture status only. No arbitrary calculation input/UID override.
- Firebase verification: RS256 signature, recognized kid, exact project issuer
  and audience, required future exp, past iat/auth_time, bounded nonempty subject,
  verified email; reject unconfigured tenant. Return only minimal preverified auth
  context to domain helpers. No raw token/email/logs. No UM affiliation claim.
- Use JOSE remote-JWKS resolver at the fixed Google securetoken public JWK URL.
  All runtime tests intercept that endpoint; unrecognized outbound requests fail.
  Live key cache/rotation, revocation/disabled-account policy and real Firebase
  credentials are not validated by generated-key tests; remain production gates.
- Every HTTP response is JSON/no-store and errors are sanitized. Malformed auth
  rejects401, unverified email403, invalid body400, oversize413, bad method405.
- Local workerd is compatibility evidence, not Cloudflare Free CPU/account
  evidence. No telemetry or account login; never call deploy or paid provider.

## Tasks

- [x] Pin isolated esbuild/Miniflare/JOSE tools; add generated-key tests and stub
  Worker/auth functions. Observe expected failures under workerd.
- [x] Implement strict verifier, loopback/demo guard and bounded HTTP fixture
  wrapper; preserve existing domain outputs. Test signature/claim failures,
  header injection, unverified email, UID overrides, body limits and safe errors.
- [x] Run local runtime tests, build/import checks, dependency audit and existing
  backend regressions. Inspect bundle for unintended Admin/Functions/keys. Add
  reproducible commands and limitations to experiment README/evidence.
- [ ] Independent review/fixes; update Aaron/research/AI log, credential scans,
  commit/push PR#2 and inspect exact latest CI handle.

## Ledger

- Previous turn was progress: pushed c3b63de/15c7976 with authorized service and
  official GTFS snapshot. Worktree clean at start. CI40425717841 confirmed running;
  keep observing that handle until terminal, do not restart on observation delay.
- Ruling: use Node test runner plus Miniflare directly, not a second Vitest stack,
  to test the actual bundled CommonJS imports with minimal test-specific plumbing.
  The current Cloudflare Vitest plugin was researched but is unnecessary here.
- Baseline CI40425717841 at15c7976 passed both backend and Android jobs.
- Watched23 initial runtime tests against a503 stub:22 failed and the negative
  host guard passed. After implementation23 passed; added key-URL/kid test24.
- Ruling: audit of Miniflare4.20260730.0 reported3 high package entries through
  sharp/undici. Inspected Wrangler4.153.0's dependency on Miniflare5.20261023.0-beta
  (workerd1.20261023.1) and selected that exact development tool with zero audit
  findings. Its removed createFetchMock API and changed constructor were observed
  failing, then adapted via exported convertV4MiniflareOptions and a strict
  outboundService. Telemetry explicitly off, local listener/inspector only.
- Fresh `npm ci`, local workerd tests and dependency audit pass. Independent
  review found unsupported critical JWT headers classified503; a regression failed
  before adding ERR_JOSE_NOT_SUPPORTED to401 client auth errors. **25 runtime
  tests now pass**, including retained key-provider503 handling.
- Existing **277 Jest tests /11 suites** and expanded syntax checks pass. Bundle
  metafile/content inspection found no Admin/Functions SDK, Google config/private
  key, live routing adapter, Node built-in import or unsecured-JWT implementation.
  Generated bundle is ignored; reproducible npm scripts are tracked.
- The URL loopback check is a research safeguard, not proof of client origin.
  Real network isolation is provided by the local Miniflare listener and the
  synthetic outbound service in this harness. No public server or live keys tested.
