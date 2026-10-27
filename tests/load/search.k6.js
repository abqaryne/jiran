// k6 load test for NFR1: a page loads in under 3 s with 50 concurrent users (PRD section 5).
// Starter for Aly. Point TARGET_URL at the endpoint to test (a callable function URL or an HTTPS API).
// Run:  k6 run -e TARGET_URL=https://<endpoint> -e ID_TOKEN=<firebase id token> tests/load/search.k6.js
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
