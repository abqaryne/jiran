const {isLocalEnvironment} = require('../src/environment');

test.each([
  [{}, false],
  [{FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT: 'live-project'}, false],
  [{FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT: 'demo-jiran'}, false],
  [{FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT: 'demo-jiran', FIRESTORE_EMULATOR_HOST: 'remote:8080'}, false],
  [{FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT: 'demo-jiran', FIRESTORE_EMULATOR_HOST: '127.0.0.1:8080'}, true],
])('local diagnostic environment %j => %s', (environment, expected) => {
  expect(isLocalEnvironment(environment)).toBe(expected);
});
