# Local foundation architecture

Status: developer foundation with live Firebase connection, 27 October 2026.
Live project `jiran-mad`; registered package `jiran.com`; Java namespace `com.jiran.app`.
Auth Email/Password is enabled. Firestore `(default)` is Standard edition in Singapore.

The Java Android app uses Firebase SDKs. Debug source initializes `demo-jiran` and
connects to loopback-hosted Auth, Firestore, Storage and Functions via the AVD host
alias `10.0.2.2`. Both `liveDebug` and release source initialize only the registered Firebase
configuration. Only debug permits cleartext HTTP and includes emulator routing.
On Android 17, the debug manifest declares local-network permission and the
developer screen requests it before connecting; release declares no such permission.
See [Android's local-network permission guidance](https://developer.android.com/privacy-and-security/local-network-permission).

The `sessionCheck` callable is an emulator-only diagnostic: authenticated,
email-verified callers receive their token UID and trigger an Admin SDK write to
`_diagnostics/{uid}`. It is exported only for `demo-jiran` when both Functions and
the expected loopback Firestore emulator are configured. Payload identity is ignored. This is not a production UM
verification gate. The integration test provisions a local account using emulator
admin access; that access is never available to the Android app.

Firestore prototype rules permit only verified users' own `_connectionChecks/{uid}`
document with a server `checkedAt` timestamp and no extra fields. Every product
collection and all Storage access remain denied. The Email/Password setup flow
does not implement UM affiliation checks or profiles; it is a handoff to Ary.
These rules are not a substitute for Gathan's application rules or NFR2 evidence.
Admin SDK Functions bypass rules, so each future endpoint must authorize the
caller and resource ownership before reading/writing. Never return another user's
address, timetable, phone or contract based on a request's user ID.

Planned feature flow: Java UI → authenticated callable → authorization/domain
logic → Firestore and server-side Routes/AI adapters → public response DTO.
Aaron owns commute calculations and deterministic match scoring; Ilham generates
explanations, never scores. Ary owns shared product screens, Gathan schema/rules.
Maps and AI credentials stay server-side; no provider credentials ship in Android.

Before implementing route caching, agree allowed provider storage/retention,
location precision, time buckets, invalidation and quota limits. Shared area/faculty
cache entries must not contain user identities or private schedules. NFR1 is p95
≤3 seconds at 100 concurrent users, verified by Aly's eventual load test.

Open decisions: Functions region; UI shell agreement; schema; UM domain policy; monthly trip/rounding rules;
Maps coverage and fares; emission factors; match scales/weights; AI provider.
