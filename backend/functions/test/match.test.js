const {rankMatches} = require('../src/match');
const factors = ['sleep','cleanliness','noise','cooking','smoking','pets','budget'];
const weights = {sleep:2000, cleanliness:2000, noise:1000, cooking:1000, smoking:1500, pets:1500, budget:1000};
const config = {version:'synthetic-v1', weights};
function profile(id, answers = {}, classDays = [5,1,3]) {
  return {id, answers:{sleep:1380, cleanliness:4, noise:2, cooking:2, smoking:false,
    pets:false, budget:{minSen:40000,maxSen:60000}, ...answers}, classDays,
  email:'private@example.com', phone:'secret', timetable:[{start:'08:00'}]};
}
const subject = () => profile('self');
const run = (candidates, configuration=config, owner=subject()) => rankMatches({subject:owner,candidates,config:configuration});

test('identical answers give exact transparent score and only safe projection', () => {
  const result = run([profile('a')]);
  expect(result).toMatchObject({formulaVersion:'normalized-v1',weightVersion:'synthetic-v1',status:'ok'});
  const match = result.matches[0];
  expect(match).toMatchObject({candidateId:'a',status:'scored',scoreBasisPoints:10000,score:100,overlappingClassDays:[1,3,5]});
  expect(match.factors.map(f=>f.factor)).toEqual(factors);
  expect(match.factors.every(f=>f.similarityBasisPoints===10000)).toBe(true);
  expect(JSON.stringify(result)).not.toMatch(/private@example|secret|08:00|answers|timetable|phone|email/);
});
test('sleep comparison wraps midnight; weighted mixed score has literal expected value', () => {
  const match = run([profile('a',{sleep:60,cleanliness:0,noise:4,cooking:2,smoking:true,
    pets:false,budget:{minSen:50000,maxSen:70000}}) ]).matches[0];
  // sleep 8333*20%, noise 5000*10%, cooking 10000*10%, pets 10000*15%, budget 3333*10%
  expect(match.scoreBasisPoints).toBe(5000);
  expect(match.score).toBe(50);
  expect(match.factors[0]).toMatchObject({similarityBasisPoints:8333,weightBasisPoints:2000});
  expect(match.reasons).toContainEqual({factor:'smoking',code:'different'});
});
test('opposite cyclic sleep boundary scores zero', () => {
  const only = {...weights}; for (const key of factors) only[key]=0; only.sleep=10000;
  expect(run([profile('a',{sleep:660})],{version:'sleep-only',weights:only}).matches[0].score).toBe(0);
});
test('ordinal opposite boundaries are independently zero', () => {
  const match=run([profile('a',{cleanliness:0,noise:0,cooking:0})],config,
    profile('self',{cleanliness:4,noise:4,cooking:4})).matches[0];
  for (const factor of ['cleanliness','noise','cooking']) {
    expect(match.factors.find(f=>f.factor===factor).similarityBasisPoints).toBe(0);
  }
});
test('weighted score rounds a literal half basis point upward', () => {
  const boundaryWeights=Object.fromEntries(factors.map(f=>[f,0]));
  boundaryWeights.cleanliness=2;
  boundaryWeights.pets=9998;
  const match=run([profile('a',{cleanliness:3,pets:true})],{version:'rounding',weights:boundaryWeights}).matches[0];
  expect(match.scoreBasisPoints).toBe(2); // 7500*2 / 10000 = 1.5
  expect(match.score).toBe(0.02);
});
test('large safe-integer budget endpoints preserve ratio without product overflow', () => {
  const max=Number.MAX_SAFE_INTEGER;
  const owner=profile('self',{budget:{minSen:0,maxSen:max}});
  const match=run([profile('a',{budget:{minSen:Math.floor(max/2),maxSen:max}})],config,owner).matches[0];
  expect(match.factors[6].similarityBasisPoints).toBe(5000);
  expect(match.scoreBasisPoints).toBe(9500);
});
test.each([
  [{minSen:40000,maxSen:60000},10000],
  [{minSen:60000,maxSen:70000},0],
  [{minSen:70000,maxSen:80000},0],
  [{minSen:50000,maxSen:50000},0],
])('budget interval overlap %j = %d basis points', (budget, expected) => {
  expect(run([profile('a',{budget})]).matches[0].factors[6].similarityBasisPoints).toBe(expected);
});
test('identical single-value budgets are compatible', () => {
  const budget={minSen:50000,maxSen:50000};
  expect(run([profile('a',{budget})],config,profile('self',{budget})).matches[0].score).toBe(100);
});
test('positive-weight missing data is unscored, zero-weight missing data is allowed', () => {
  const missing=profile('missing',{pets:null});
  const result=run([missing,profile('z')]);
  expect(result.matches.map(m=>m.candidateId)).toEqual(['z','missing']);
  expect(result.matches[1]).toMatchObject({status:'insufficient_data',score:null,scoreBasisPoints:null,missingFactors:['pets']});
  const adjusted={...weights,pets:0,sleep:3500};
  expect(run([missing],{version:'zero-pets',weights:adjusted}).matches[0].score).toBe(100);
});
test('missing subject answer also yields unscored; absent weekdays stay unknown', () => {
  expect(run([profile('a',{},null)],config,profile('self',{sleep:undefined},null)).matches[0])
    .toMatchObject({status:'insufficient_data',missingFactors:['sleep'],overlappingClassDays:null});
});
test('ranking is stable, excludes subject, and does not mutate inputs', () => {
  const input={subject:subject(),candidates:[profile('z'),profile('self'),profile('A'),profile('a')],config};
  const before=JSON.stringify(input);
  expect(rankMatches(input).matches.map(m=>m.candidateId)).toEqual(['A','a','z']);
  expect(JSON.stringify(input)).toBe(before);
  expect(rankMatches(input)).toEqual(rankMatches(input));
});
test('empty candidates return valid empty response', () => expect(run([]).matches).toEqual([]));
test.each([
  null, {}, {version:'',weights}, {version:'v',weights:{...weights,pets:-1}},
  {version:'v',weights:{...weights,pets:1501}}, {version:'v',weights:{...weights,pets:1499.5}},
  {version:'v',weights:{...weights,unknown:0}}, {version:'v',weights:{sleep:10000}},
])('rejects absent or malformed weight config %j', invalid => expect(()=>run([],invalid)).toThrow());
test.each([
  {sleep:1440},{sleep:-1},{cleanliness:5},{noise:1.5},{cooking:NaN},{smoking:'false'},
  {pets:0},{budget:{minSen:60000,maxSen:40000}},{budget:{minSen:0,maxSen:Number.MAX_SAFE_INTEGER+1}},
])('rejects malformed present answers %j', answers=>expect(()=>run([profile('a',answers)])).toThrow());
test.each([[8],[0],[1,1],['1']])('rejects invalid weekday list %j', days =>
  expect(()=>run([profile('a',{},days)])).toThrow());
test('bounds candidate set and validates all IDs and duplicates', () => {
  expect(()=>run(Array.from({length:101},(_,i)=>profile('p'+i)))).toThrow();
  expect(()=>run([profile('a'),profile('a')])).toThrow();
  expect(()=>run([profile('../unsafe')])).toThrow();
  expect(()=>run([profile('')])).toThrow();
  expect(()=>run('invalid')).toThrow();
});
test('validates subject and present answers even for zero-weight factors', () => {
  expect(()=>run([],config,{id:'self'})).toThrow();
  expect(()=>run([],config,profile('invalid/id'))).toThrow();
  expect(()=>run([profile('a',{budget:{minSen:-1,maxSen:1}})])).toThrow();
  expect(()=>run([profile('a',{pets:'no'})],{version:'zero-pets',weights:{...weights,pets:0,sleep:3500}})).toThrow();
});
