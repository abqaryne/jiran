const {session} = require('./session');
const {calculateCommute} = require('./commute');
const scenarios = require('../fixtures/commute-scenarios.json');

/** Local-only fixtures until the production listing/profile contract is agreed. */
function commutePreview(request) {
  session(request);
  const data = request.data;
  if (!data || typeof data !== 'object' || Array.isArray(data)
      || Object.keys(data).length !== 1 || typeof data.scenarioId !== 'string'
      || !Object.hasOwn(scenarios, data.scenarioId)) {
    const error = new TypeError('Provide one known scenarioId without overrides');
    error.code = 'invalid-argument';
    throw error;
  }
  return {...calculateCommute(scenarios[data.scenarioId]), scenarioId: data.scenarioId,
    dataSource: 'fixture', assumptionVersion: 'fixture-v1'};
}
module.exports = {commutePreview};
