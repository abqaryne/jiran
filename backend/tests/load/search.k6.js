// Production search starter only. Canonical PRD NFR1 requires p95<3s at100 users;
// AGENTS also calls for50-user latency and100-user operation. Reconcile with Aly.
// For executable local50/100 fixture scenarios, see commute-fixture.k6.js and README.md.
// Starter for Aly. Point TARGET_URL at the endpoint to test (a callable function URL or an HTTPS API).
// Run:  k6 run -e TARGET_URL=https://<endpoint> -e ID_TOKEN=<firebase id token> backend/tests/load/search.k6.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<3000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const res = http.post(
    __ENV.TARGET_URL,
    JSON.stringify({ data: {} }),
    { headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${__ENV.ID_TOKEN}` } },
  );
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(1);
}
