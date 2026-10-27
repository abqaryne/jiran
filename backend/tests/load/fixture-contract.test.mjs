import {test} from 'node:test';
import assert from 'node:assert/strict';
import {fixtureOptions,fixtureResult,fixtureAccounts} from './fixture-contract.mjs';
import {spawnSync} from 'node:child_process';
import {fileURLToPath} from 'node:url';
import {existsSync} from 'node:fs';
import {mkdtemp,mkdir,copyFile,writeFile,rm} from 'node:fs/promises';
import {join,resolve,sep} from 'node:path';
const now=1000000;
function accounts() {return {mode:'local-fixture',projectId:'demo-jiran',createdAt:now,expiresAt:now+3600000,
  verified:Array.from({length:100},(_,i)=>({uid:`synthetic-${i}`,token:`token-${i}`})),
  unverified:{uid:'unverified',token:'unverified-token'}};}
test('load configuration preserves separate50/100 user plateaus and contract probes',()=>{
  const options=fixtureOptions(60);
  assert.equal(options.scenarios.baseline50.vus,50);
  assert.equal(options.scenarios.capacity100.vus,100);
  assert.equal(options.scenarios.baseline50.duration,'60s');
  assert.equal(options.scenarios.capacity100.startTime,'80s');
  assert.equal(options.scenarios.contracts.startTime,'145s');
  assert.ok(options.thresholds['http_req_duration{phase:capacity100}']);
});
test('invalid or unbounded phase durations are rejected',()=>{
  for(const value of [0,-1,1.5,301,NaN,'60']) assert.throws(()=>fixtureOptions(value));
});
test('fixture response validator requires scenario-specific semantic data',()=>{
  const body={result:{scenarioId:'on-time',dataSource:'fixture',selectedMode:'transit',trueMonthlyCostSen:61200,options:Array(4).fill({})}};
  assert.equal(fixtureResult(200,body,'on-time'),true);
  assert.equal(fixtureResult(200,{error:{status:'INTERNAL'}},'on-time'),false);
  assert.equal(fixtureResult(200,{result:{...body.result,trueMonthlyCostSen:1}},'on-time'),false);
  assert.equal(fixtureResult(500,body,'on-time'),false);
  assert.equal(fixtureResult(200,{result:{...body.result,dataSource:'production'}},'on-time'),false);
});
test('late/missing data cases cannot pass with stale successful values',()=>{
  const late={result:{scenarioId:'no-on-time-route',dataSource:'fixture',selectedMode:null,trueMonthlyCostSen:null,options:Array(4).fill({})}};
  assert.equal(fixtureResult(200,late,'no-on-time-route'),true);
  late.result.trueMonthlyCostSen=61200;
  assert.equal(fixtureResult(200,late,'no-on-time-route'),false);
  const missing={result:{scenarioId:'missing-data',dataSource:'fixture',options:[{},
    {reason:'missing_schedule'},{reason:'missing_fare_assumption'},{}]}};
  assert.equal(fixtureResult(200,missing,'missing-data'),true);
  missing.result.options[1]={};
  assert.equal(fixtureResult(200,missing,'missing-data'),false);
});
test('token set requires100 unique local identities and enough remaining lifetime',()=>{
  assert.equal(fixtureAccounts(accounts(),now,300).length,101);
  for(const lifetime of [undefined,NaN,0,-1]) assert.throws(()=>fixtureAccounts(accounts(),now,lifetime));
  for(const change of [value=>value.projectId='jiran-mad',value=>value.verified.pop(),
    value=>value.verified[1].uid=value.verified[0].uid,value=>value.expiresAt=now+1000,
    value=>value.createdAt=now+1,value=>value.unverified.token='',
    value=>value.unverified.uid=value.verified[0].uid]) {
    const value=accounts();change(value);assert.throws(()=>fixtureAccounts(value,now,300));
  }
});
test('account-preparation runner refuses a non-loopback Auth target before execution',()=>{
  const result=spawnSync(process.execPath,[fileURLToPath(new URL('../../scripts/run-fixture-load.cjs',import.meta.url))],
    {encoding:'utf8',env:{...process.env,FIREBASE_AUTH_EMULATOR_HOST:'auth.example.test'}});
  assert.equal(result.status,1);
  assert.match(result.stderr,/Use the local demo-jiran Auth emulator/);
});
test('failed binary setup cannot leave the generated token file behind',async()=>{
  const parent=fileURLToPath(new URL('../../.firebase/',import.meta.url));
  await mkdir(parent,{recursive:true});
  const directory=await mkdtemp(join(parent,'cleanup-test-'));
  try {
    await mkdir(join(directory,'scripts'));
    await mkdir(join(directory,'tests/load'),{recursive:true});
    const runner=join(directory,'scripts/run-fixture-load.cjs');
    await copyFile(fileURLToPath(new URL('../../scripts/run-fixture-load.cjs',import.meta.url)),runner);
    await copyFile(fileURLToPath(new URL('./fixture-contract.mjs',import.meta.url)),join(directory,'tests/load/fixture-contract.mjs'));
    const preload=join(directory,'fake-auth.cjs');
    await writeFile(preload,`global.fetch=async(url,options)=>{
      const body=JSON.parse(options.body),uid=body.email||body.localId;
      return {ok:true,json:async()=>({localId:uid,idToken:'synthetic-token-'+uid,expiresIn:'3600'})};
    };`);
    const result=spawnSync(process.execPath,['--require',preload,runner],{encoding:'utf8',env:{...process.env,
      FIREBASE_AUTH_EMULATOR_HOST:'127.0.0.1:9099',GCLOUD_PROJECT:'demo-jiran',K6_BINARY:'missing-k6-test-binary'}});
    assert.equal(result.status,1);
    assert.match(result.stderr,/ENOENT/);
    assert.equal(existsSync(join(directory,'.firebase/load-accounts.json')),false);
    assert.equal(result.stderr.includes('synthetic-token-'),false);
  } finally {
    assert.ok(resolve(directory).startsWith(resolve(parent)+sep));
    await rm(directory,{recursive:true,force:true});
  }
});
