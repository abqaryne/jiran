# Jiran agent instructions

For every Firebase task, first find and use the appropriate official Firebase
agent skills from https://github.com/firebase/agent-skills. Read `firebase-basics`
for app registration/configuration, `firebase-auth-basics` for authentication,
`firebase-firestore` for database/SDK work, and `firestore-rules-creation` before
authoring rules. Use Firebase MCP tools where available and the official CLI.

Read `team/aaron.md` and preserve the Java Android / JavaScript Functions stack.
The existing live project is `jiran-mad`; its Android app is named Jiran with
application ID `jiran.com`. Do not create duplicate apps/projects. Use explicit
`--project jiran-mad` for authorized live operations. Default local development
uses `demo-jiran`. Never commit Firebase configuration keys or service accounts.

Budget is free-only (user confirmed 27 October 2026). Do not enable billing,
upgrade plans, or activate billable providers. Research a no-billing production
alternative retaining Firebase Auth and Firestore; preserve the Java/JavaScript
code while documenting proposed runtime/provider changes before adopting them.
