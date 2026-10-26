const {session} = require('./session');
const {rankMatches} = require('./match');
const fixtures = require('../fixtures/match-scenarios.json');

function matchPreview(request) {
  session(request);
  const data = request.data;
  if (!data || typeof data !== 'object' || Array.isArray(data)
      || Object.keys(data).length !== 1 || typeof data.scenarioId !== 'string'
      || !Object.hasOwn(fixtures.scenarios, data.scenarioId)) {
    const error = new TypeError('Provide one known match scenarioId without overrides');
    error.code = 'invalid-argument';
    throw error;
  }
  return {...rankMatches({subject:fixtures.subject,config:fixtures.config,candidates:fixtures.scenarios[data.scenarioId]}),
    dataSource:'fixture',scenarioId:data.scenarioId};
}
module.exports = {matchPreview};
