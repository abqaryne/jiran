const {matchPreview} = require('../src/match-preview');
const verified = {auth:{uid:'local-user',token:{email_verified:true}}};
const run = scenarioId => matchPreview({...verified,data:{scenarioId}});
test('rejects missing auth before parsing body', () => {
  expect(()=>matchPreview({data:{scenarioId:'ranked'}})).toThrow('Authentication required');
  expect(()=>matchPreview({auth:{uid:'u',token:{}},data:{scenarioId:'ranked'}})).toThrow('Verified email required');
});
test.each([undefined,null,[],{}, {scenarioId:'__proto__'}, {scenarioId:'unknown'},
  {scenarioId:'ranked',userId:'victim'}, {scenarioId:'ranked',weights:{}}, {scenarioId:'ranked',candidateIds:['victim']}])(
  'rejects invalid selection and identity/weight overrides %j',data=>
    expect(()=>matchPreview({...verified,data})).toThrow());
test('returns fixed ranked sample breakdown and no private fields or identity', () => {
  const result = run('ranked');
  expect(result).toMatchObject({dataSource:'fixture',scenarioId:'ranked',formulaVersion:'normalized-v1',weightVersion:'synthetic-v1'});
  expect(result.matches.map(m=>[m.candidateId,m.score])).toEqual([['sample-a',100],['sample-b',50]]);
  expect(result.matches[0].overlappingClassDays).toEqual([1,3]);
  expect(JSON.stringify(result)).not.toMatch(/email|phone|timetable|answers|local-user/);
  expect(result).toEqual(run('ranked'));
});
test('missing answers stay unscored and empty candidate scenario stays empty', () => {
  expect(run('missing-answers').matches[0]).toMatchObject({status:'insufficient_data',score:null,missingFactors:['smoking']});
  expect(run('empty').matches).toEqual([]);
});
