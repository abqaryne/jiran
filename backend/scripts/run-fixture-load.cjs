// Run only through the local Firebase Emulator Suite. Never prints tokens/passwords.
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const {randomBytes,createHash}=require('node:crypto');
const {execFileSync}=require('node:child_process');
async function main() {
  assert.equal(process.env.FIREBASE_AUTH_EMULATOR_HOST,'127.0.0.1:9099','Use the local demo-jiran Auth emulator');
  assert.equal(process.env.GCLOUD_PROJECT??'demo-jiran','demo-jiran');
  assert.ok(process.env.K6_BINARY,'Set K6_BINARY to the local official k6 binary');
  const seconds=Number(process.env.LOAD_PHASE_SECONDS||60);
  const {fixtureOptions}=await import('../tests/load/fixture-contract.mjs');
  fixtureOptions(seconds);
  const output=path.resolve(__dirname,'../.firebase');
  fs.mkdirSync(output,{recursive:true});
  const tokenFile=path.join(output,'load-accounts.json'),summary=path.join(output,'load-summary.json');
  const manifestFile=path.join(output,'load-manifest.json');
  const createdAt=Date.now(),runId=randomBytes(6).toString('hex'),password=randomBytes(24).toString('base64url');
  const auth='http://127.0.0.1:9099/identitytoolkit.googleapis.com/v1';
  async function post(operation,data,owner=false) {
    const response=await fetch(`${auth}/${operation}?key=local-fake-key`,{method:'POST',
      headers:{'Content-Type':'application/json',...(owner?{Authorization:'Bearer owner'}:{})},
      body:JSON.stringify(data),signal:AbortSignal.timeout(15000)});
    if(!response.ok) throw new Error(`Local Auth operation ${operation} failed (${response.status})`);
    return response.json();
  }
  const verified=Array(100);let next=0,expiresAt=Infinity;
  const identity=result=>({uid:result.localId,token:result.idToken});
  await Promise.all(Array.from({length:5},async()=>{
    while(next<100) {
      const index=next++,email=`load-${runId}-${index}@example.com`;
      const signup=await post('accounts:signUp',{email,password,returnSecureToken:true});
      await post('accounts:update',{localId:signup.localId,emailVerified:true},true);
      const login=await post('accounts:signInWithPassword',{email,password,returnSecureToken:true});
      verified[index]=identity(login);
      expiresAt=Math.min(expiresAt,Date.now()+Number(login.expiresIn)*1000);
    }
  }));
  const unverified=await post('accounts:signUp',{email:`load-${runId}-unverified@example.com`,password,returnSecureToken:true});
  expiresAt=Math.min(expiresAt,Date.now()+Number(unverified.expiresIn)*1000);
  const environment={...process.env};
  for(const key of Object.keys(environment)) if(key.startsWith('K6_')) delete environment[key];
  Object.assign(environment,{K6_NO_USAGE_REPORT:'true',HTTP_PROXY:'',HTTPS_PROXY:'',ALL_PROXY:'',NO_PROXY:'127.0.0.1,localhost,::1'});
  const sourceFiles=['../tests/load/commute-fixture.k6.js','../tests/load/fixture-contract.mjs','run-fixture-load.cjs',
    '../functions/index.js','../functions/src/environment.js','../functions/src/session.js',
    '../functions/src/commute.js','../functions/src/commute-preview.js','../functions/src/match.js',
    '../functions/src/emissions.js','../functions/config/emission-factors.json',
    '../functions/src/match-preview.js','../functions/fixtures/commute-scenarios.json',
    '../functions/fixtures/match-scenarios.json','../functions/package-lock.json','../firebase.json'];
  const manifest={researchOnly:true,startedAt:new Date().toISOString(),phaseSeconds:seconds,node:process.version,
    gitHead:execFileSync('git',['rev-parse','HEAD'],{cwd:__dirname,encoding:'utf8'}).trim(),
    dirty:Boolean(execFileSync('git',['status','--porcelain'],{cwd:__dirname,encoding:'utf8'}).trim()),
    k6:execFileSync(process.env.K6_BINARY,['version'],{encoding:'utf8',env:environment}).trim(),
    sourceHashes:Object.fromEntries(sourceFiles.map(file=>[file,
      createHash('sha256').update(fs.readFileSync(path.resolve(__dirname,file))).digest('hex')]))};
  fs.writeFileSync(manifestFile,JSON.stringify(manifest,null,2));
  let passed=false;
  try {
    fs.writeFileSync(tokenFile,JSON.stringify({mode:'local-fixture',projectId:'demo-jiran',createdAt,expiresAt,
      verified,unverified:identity(unverified)}),{mode:0o600});
    console.log(`Prepared100 distinct verified fixture users and1 unverified user; ${seconds}s plateaus.`);
    execFileSync(process.env.K6_BINARY,['run','--quiet','--no-usage-report','--summary-export',summary,
      '-e',`TOKEN_FILE=${tokenFile}`,'-e',`PHASE_SECONDS=${seconds}`,path.resolve(__dirname,'../tests/load/commute-fixture.k6.js')],
    {stdio:'inherit',env:environment,timeout:(seconds*2+240)*1000});
    passed=true;
    console.log('PASS: local fixture load thresholds and auth/validation contracts. Production NFR1 remains unverified.');
  } finally {
    if(fs.existsSync(tokenFile)) fs.unlinkSync(tokenFile);
    fs.writeFileSync(manifestFile,JSON.stringify({...manifest,finishedAt:new Date().toISOString(),passed},null,2));
  }
}
main().catch(error=>{console.error(error.message);process.exitCode=1;});
