function isLocalEnvironment(environment) {
  return environment.FUNCTIONS_EMULATOR === 'true'
    && environment.GCLOUD_PROJECT === 'demo-jiran'
    && environment.FIRESTORE_EMULATOR_HOST === '127.0.0.1:8080';
}
module.exports = {isLocalEnvironment};
