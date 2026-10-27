const {execFileSync} = require('node:child_process');
const path = require('node:path');

test.each([
  {FUNCTIONS_EMULATOR: '', GCLOUD_PROJECT:'jiran-mad', FIRESTORE_EMULATOR_HOST:''},
  {FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT:'jiran-mad', FIRESTORE_EMULATOR_HOST:'127.0.0.1:8080'},
  {FUNCTIONS_EMULATOR: 'true', GCLOUD_PROJECT:'demo-jiran', FIRESTORE_EMULATOR_HOST:''},
])('real module exports no diagnostic/fixture endpoints outside safe local environment %j', environment => {
  const result = execFileSync(process.execPath,
    ['-e', 'process.stdout.write(JSON.stringify(Object.keys(require("./index"))))'], {
      cwd:path.resolve(__dirname, '..'), encoding:'utf8', timeout:15000,
      env:{...process.env, ...environment},
    });
  expect(JSON.parse(result)).toEqual([]);
});

test('unsafe environment does not load Firebase SDKs or fixture handlers at all', () => {
  const script = `
    const Module = require('node:module');
    const originalLoad = Module._load;
    Module._load = function(request, ...args) {
      if (request.startsWith('firebase-') || request.includes('-preview')) {
        throw new Error('SDK/fixture initialization outside emulator guard');
      }
      return originalLoad.call(this, request, ...args);
    };
    process.stdout.write(JSON.stringify(Object.keys(require('./index'))));
  `;
  const result = execFileSync(process.execPath, ['-e',script], {
    cwd:path.resolve(__dirname,'..'),encoding:'utf8',timeout:15000,
    env:{...process.env,FUNCTIONS_EMULATOR:'',GCLOUD_PROJECT:'jiran-mad',FIRESTORE_EMULATOR_HOST:''},
  });
  expect(JSON.parse(result)).toEqual([]);
});
