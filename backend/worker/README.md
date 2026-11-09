# E2.1 subtask 1: Listing drafts and photos

**Needs human review.** JavaScript Worker for FR2.1/NFR2; prepared locally,
not deployed. Existing Functions and the feasibility experiment are unchanged.

## Contract

Both routes require `Authorization: Bearer <Firebase ID token>`.

| Route | Input | Success |
|---|---|---|
| `POST /listings` | JSON: `areaId`, `buildingName`, optional `placeId`, general Building `lat`/`lng`, integer `totalRentSen`/`depositSen`, `genderPref`, `amenities`, `exactAddress` | 201 `{listingId, status: "draft"}` |
| `POST /listings/{listingId}/photos` | Raw JPEG/PNG bytes and matching Content-Type | 201 `{photoUrl}` after upload **and** Firestore save |

Draft creation atomically commits `listings/{id}` and its `private/location`.
The manual picker also sends `coordinateSource: "area_center_estimate"`.
It persists beside `lat`/`lng`; these are estimated Area-centre coordinates,
not a verified Building location. The source cannot be removed/changed by an
update. Legacy documents without the field have unspecified provenance.
The Host types `exactAddress` only for the private location document (Gathan
approved this fallback on 9 Nov 2026).
The decoded JWT subject is an **untrusted hint**, not authentication. Firestore
verifies the caller's token and enforces `ownerUid == request.auth.uid` before
accepting the commit. No service account or Admin SDK bypasses Rules.

Draft reads are Host-only. Query own Listings with `ownerUid == currentUid`;
public search must constrain `status == active`. Drafts cannot receive
Connection requests or become active yet. Publication with a priced vacant Room
and 30-day expiry belongs to E2.2. Residency changes, deletion and later status
transitions await their lifecycle stories.

Uploads first read the draft using the caller's token. Only a successful
Host-only draft read permits a provider call. Fixed parameters are signed with
SHA-256; only local image bytes are uploaded, and only the returned HTTPS URL
is saved. An update-time precondition prevents concurrent writes overwriting
each other. Provider and persistence failures return errors, never success.

## Run and configure

From `backend/functions`: `npm test` and `npm run check`.
From `backend`, with a supported JDK:

```powershell
npx --yes firebase-tools@15.34.1 emulators:exec --project demo-jiran --only auth,firestore "node scripts/firestore-rule-check.cjs"
```

From this directory, bundle without deploying:

```powershell
npx --yes wrangler@4.153.0 deploy --dry-run --outdir build
```

Default config uses `demo-jiran`, loopback requests only, and Firestore at
`127.0.0.1:8080`. Production is pinned to `jiran-mad`. Emulator must load these
Rules. Automated upload tests intercept provider requests and need no keys.
**A local Worker with real Cloudinary credentials would upload real assets.**

Before live use, Aaron reviews the gateway and configures the existing Free
account's `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`,
`CLOUDINARY_API_SECRET`, and `CLOUDINARY_UPLOAD_PRESET` as Worker secrets.
No values belong in the app, tracked config or logs. Use a signed preset
limited to JPEG/PNG, with size limits and metadata stripping; disable paid
add-ons. Configure free-account abuse/quota controls before release.
Local `.dev.vars` files are ignored by Git.

Engineering bounds for review (not market assumptions): 2 MiB/photo,
5 photos/Listing, 10 amenities of 40 characters, Building name 200 characters,
exact address 500 characters. Header checks are preliminary; Cloudinary must
decode the image and enforce its preset. Coordinates are general Building
coordinates, never the Unit's precise door location.

## Limits and human review

- Cloudinary photo URLs are public assets; draft document privacy does not make
  a known image URL private. Never send contracts through this route.
- A provider success followed by a database failure can leave an orphan image.
  There is no automatic retry. Cleanup/quota policy needs review before release.
- Live tokens, actual Cloudinary preset/Free-account behavior, Worker CPU,
  device integration and NFR1 load are **not verified**.
- E1.3 readiness is not established. Building picker, screens, Rooms, price
  rules and publication are outside this subtask.
- Read the two `listing-*.js` domain files, `listings.js` and Listing rules.
  Explain atomic writes, whole-document privacy, ownership checks, the untrusted
  JWT hint, server timestamps and failed-upload behavior before merge.

References: [Firestore REST authentication](https://firebase.google.com/docs/firestore/use-rest-api)
and [Cloudinary signatures](https://cloudinary.com/documentation/authentication_signatures).
