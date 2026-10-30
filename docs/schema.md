# Firestore Database Schema Specification

This document defines the complete Cloud Firestore database schema for Jiran, covering all core collections, subcollections, privacy subdocuments, and system configurations.

> **Infrastructure Note:** The project operates on free tier plans only (no paid Firebase Cloud Functions and no Firebase Storage). Server-side operations run on a **Cloudflare Worker** server `[DRAFT]`, external media assets (such as listing photos) use **Cloudinary Free** URLs `[DRAFT]`, and route/transit planning uses **openrouteservice** (walking and road) and **Prasarana GTFS timetables** (bus and rail) `[DRAFT]`. All unconfirmed tech stack choices are explicitly tagged as `[DRAFT]`.

---

## 1. Privacy Architecture & Stage-Based Visibility

Firestore Security Rules evaluate permissions on the **entire document**, not on individual fields. To protect user privacy in compliance with Malaysia's **Personal Data Protection Act 2010 (PDPA 2010)** and **NFR2**, sensitive data is strictly isolated into separate subcollections and subdocuments.

### Access baseline (UM only)

Only UM students may use Jiran (PRD FR1.1 and NFR2, decided 30 Oct 2026). Every read and write in every collection needs `request.auth.token.email_verified == true` and a `request.auth.token.email` on a UM domain, matched exactly (`siswa.um.edu.my` only: students, no staff mail, decided 30 Oct 2026; a postgraduate domain, if one exists, is still to confirm with UM). The rules below are checked on top of this baseline.

### Stage Visibility Matrix

| Stage | What is visible | What is NOT visible | Isolated Storage Path |
|---|---|---|---|
| **Match** | `initials`, `faculty`, `year`, `lifestyleTags`, match `score`, `reasons`, `overlappingClassDays` | Full legal name, photo, phone number, exact unit address, detailed timetable, email | `users/{uid}` (Public profile), `matches/{matchId}` |
| **Connection Request** | Match profile + short request message (`message` in `listingRequests` or `matches`) | Full name, photo, phone number, exact unit address | `matches/{matchId}`, `listingRequests/{listingId}_{requesterUid}` |
| **Connection** (Host accepts `listingRequests` for THAT listing, or pairwise `matches` connected) | Chat, `firstName`, exact unit address (for viewing with Host), `phoneNumber` (if opted in) | Profile photo, detailed timetable | `listings/{listingId}/private/location`, `connections/{pairId}`, `users/{uid}/private/contact`, `users/{uid}/private/phone` |
| **Household** | Member timetables, shared expenses, chores, rules, profile photo (`photoUrl`), exact unit address | Private contract check results | `users/{uid}/private/timetable`, `users/{uid}/private/photo`, `users/{uid}/private/contact`, `users/{uid}/private/phone`, `users/{uid}/private/membership`, `households/{householdId}`, `listings/{listingId}/private/location` |

### Proof of Connection (Single Source of Truth)

When Rules read `users/{targetUid}/private/contact` they know only the two UIDs, not which listing the Connection came from, so they cannot build the path `listingRequests/{listingId}_{uid}` there. Every Connection, student to student or student to Host, is therefore also recorded as one `connections/{pairId}` document. Its ID is the two UIDs sorted and joined with `_`, so Rules can build it from `request.auth.uid` and `targetUid` without a query (`pairId = request.auth.uid < targetUid ? request.auth.uid + "_" + targetUid : targetUid + "_" + request.auth.uid`).

1. **Student and Host:** the Host sets `listingRequests/{listingId}_{requesterUid}` to `"accepted"`, then a `connections/{pairId}` document is created with `source: "listingRequest"`. The `listingRequests` document stays the proof for the exact address, because the address document's path contains the `listingId`.
2. **Student and student:** both sides are `"accepted"` in `matches/{matchId}`, then a `connections/{pairId}` document is created with `source: "match"`.
3. **Active co-household members:** `users/{uid}/private/membership` document where `householdId` matches `households/{householdId}.memberUids`.

Contact and phone checks use `connections/{pairId}.status == "active"` for 1 and 2, in both directions, because the document is the same for both people.

### Rules Lookup Map

Firestore Security Rules cannot execute queries across collections; they can only evaluate permissions by reading specific documents at paths constructed directly from the request context (`request.auth.uid`, resource parameters, or document data). The table below specifies the target document path, lookup document path, and exact condition evaluated for each access rule.

| Access Rule / Data | Protected Target Document Path | Lookup Document Path(s) | Field Checked & Condition |
|---|---|---|---|
| **Timetable** | `users/{targetUid}/private/timetable` | `users/{targetUid}/private/membership`, `users/{request.auth.uid}/private/membership` | `request.auth.uid == targetUid` OR (`targetMembership.householdId != null` AND `targetMembership.householdId == requestMembership.householdId`) |
| **Photo** | `users/{targetUid}/private/photo` | `users/{targetUid}/private/membership`, `users/{request.auth.uid}/private/membership` | `request.auth.uid == targetUid` OR (`targetMembership.householdId != null` AND `targetMembership.householdId == requestMembership.householdId`) |
| **Contact (`firstName`)** | `users/{targetUid}/private/contact` | `connections/{pairId}` (`pairId` = the two UIDs sorted), `users/{targetUid}/private/membership`, `users/{request.auth.uid}/private/membership` | `request.auth.uid == targetUid` OR `connection.status == "active"` OR (`targetMembership.householdId != null` AND `targetMembership.householdId == requestMembership.householdId`) |
| **Phone (`phoneNumber`)** | `users/{targetUid}/private/phone` | `users/{targetUid}/private/profile`, `connections/{pairId}`, `users/{targetUid}/private/membership`, `users/{request.auth.uid}/private/membership` | `request.auth.uid == targetUid` OR (`targetProfile.privacy.showPhoneToConnections == true` AND `connection.status == "active"`) OR (`targetMembership.householdId != null` AND `targetMembership.householdId == requestMembership.householdId`) |
| **Exact Address** | `listings/{listingId}/private/location` | `listings/{listingId}` (field `ownerUid`), `listings/{listingId}/private/location` (field `residentUids`), `listingRequests/{listingId}_{request.auth.uid}` | `request.auth.uid == listing.ownerUid` OR `request.auth.uid in location.residentUids` OR `listingRequest.status == "accepted"` |
| **Chat Channel & Messages** | `chats/{chatId}`, `chats/{chatId}/messages/{messageId}` | `chats/{chatId}` (field `participantUids`) | `request.auth.uid in chat.participantUids` |
| **Household Data** (Expenses, Chores, Rules) | `households/{householdId}`, `expenses/{expenseId}`, `chores/{choreId}` | `households/{householdId}` (field `memberUids`) | `request.auth.uid in household.memberUids` |

---

## 2. Collections Specification

> **Monetary Standardization Note:** All monetary values across all collections, subdocuments, and API contracts are stored in safe integer **sen** (MYR cents, e.g., 45000 = RM 450.00) to eliminate floating-point rounding errors in room price splits, expense calculations, and budget comparisons.

### 2.1 Collection: `users`
- **Source:** PRD Section 8 (`users` collection) and PRD Module 1 (FR1.1, FR1.2, FR1.3).
- **Purpose:** Primary student user profile. Stores non-sensitive public metadata accessible at Match/Request stage (`initials`, `faculty`, `year`, `lifestyleTags`).
- **Document ID:** `{uid}` (Firebase Auth UID).
- **Used by:** Module 1 (FR1.1, FR1.2, FR1.3), Module 3 (FR3.2), all modules.

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `uid` | string | Yes | Owner (on create) | Firebase Auth UID. `E1.3` |
| `initials` | string | Yes | Owner | Student initials for Match stage display (e.g., `"M.L."`). `FR1.3`, `E1.3` |
| `faculty` | string | Yes | Owner | UM Faculty (e.g., `"FSKTM"`, `"FBL"`). `FR1.3`, `E1.3` |
| `year` | number | Yes | Owner | Academic year (1–4). `FR1.3`, `E1.3` |
| `lifestyleTags` | array<string> | Yes | Owner | Array of tags e.g. `["quiet", "non_smoker", "early_riser"]`. `FR1.3`, `E1.3` |
| `createdAt` | timestamp | Yes | Owner (on create) | Profile creation timestamp. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Owner | Last update timestamp. `[PROPOSED]` |

#### Subcollection: `users/{uid}/private/profile`
- **Source:** PRD FR1.1 (UM email verification), FR1.3 (profile settings & privacy toggles), and PRD Section 5 (NFR2 Privacy).
- **Purpose:** Consolidated private user profile document. Stores student full legal name, room search budget (in integer `sen`), vehicle ownership, and privacy toggles. Readable strictly by the owner. Firebase Auth already stores and verifies the user's email address (`request.auth.token.email`), so the email is not duplicated in Firestore or exposed on the public user document. Only UM emails may register (PRD FR1.1, decided 30 Oct 2026), so Rules check the domain from the token for every access (see the access baseline in section 1).
- **Document ID:** `profile` (single document).
- **Access Rule:** Readable strictly by the owner (`uid == request.auth.uid`).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `fullName` | string | Yes | Owner | Full legal name for verifications/contracts. `[PROPOSED]` |
| `budgetMinSen` | number | Yes | Owner | Minimum monthly room budget in integer sen (e.g. 30000 = RM 300). `FR1.3` |
| `budgetMaxSen` | number | Yes | Owner | Maximum monthly room budget in integer sen (e.g. 60000 = RM 600). `FR1.3` |
| `hasCar` | boolean | Yes | Owner | Car ownership flag. `FR1.3` |
| `homeAreaId` | string | No | Owner | Foreign key to `areas/{areaId}`. `E1.3` |
| `privacy` | map | Yes | Owner | Toggles e.g. `{ showPhoneToConnections: boolean, showPhotoToHouseholdOnly: boolean }`. `FR1.3` |

#### Subcollection: `users/{uid}/private/contact`
- **Source:** PRD Section 5 Stage Table (Connection stage visibility) and PRD FR1.3.
- **Purpose:** Stores stage-accessible first name (`firstName`).
- **Document ID:** `contact` (single document).
- **Access Rule:** `firstName` is readable by accepted connections (through an active `connections` document, in both directions) and household members (`users/{uid}/private/membership`).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `firstName` | string | Yes | Owner | First name for chat and viewing stage. `PRD Stage Table` |

#### Subcollection: `users/{uid}/private/phone`
- **Source:** PRD Section 5 Stage Table (Connection stage opt-in phone) and PRD FR1.3.
- **Purpose:** Stores user's phone number for opt-in connection sharing.
- **Document ID:** `phone` (single document).
- **Access Rule:** Readable strictly by the owner (`uid == request.auth.uid`), by users with an active `connections` document, in both directions, ONLY when `privacy.showPhoneToConnections == true` (the rule reads `users/{uid}/private/profile` with `get()`), and by household members.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `phoneNumber` | string | Yes | Owner | Phone number (opt-in share). `FR1.3`, `NFR2` |

#### Subcollection: `users/{uid}/private/membership`
- **Source:** PRD Section 5 Stage Table (Household stage permissions) and Security Rules lookup requirements.
- **Purpose:** Links user to their active household ID for Security Rules lookups without collection queries.
- **Document ID:** `membership` (single document).
- **Access Rule:** Readable strictly by the owner (`uid == request.auth.uid`). Written strictly by the server. Note: Backend Firestore Security Rules evaluating `get()` on target membership documents still operate with rule evaluation privileges to verify household membership during cross-user permission checks.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `householdId` | string | No | Server | Foreign key to `households/{householdId}` when user is an active household member; `null` otherwise. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Server | Last update timestamp. `[PROPOSED]` |

#### Subcollection: `users/{uid}/private/timetable`
- **Source:** PRD FR1.3 (class timetable) and PRD Section 5 (NFR2 timetable privacy).
- **Purpose:** Stores exact weekly class timetable (building, exact start/end times). Isolated because timetable details reveal daily physical presence.
- **Document ID:** `timetable` (single document).
- **Access Rule:** Readable strictly by the owner or household members (`users/{uid}/private/membership`).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `entries` | array<map> | Yes | Owner | `[{ day: string ("Mon".."Sun"), start: string ("09:00"), end: string ("11:00"), building: string }]`. `FR1.3` |

#### Subcollection: `users/{uid}/private/photo`
- **Source:** PRD FR1.3 ("profile shows initials, not a photo, by default") and Section 5 Stage Table (Household stage photo access).
- **Purpose:** Stores profile photo Cloudinary URL `[DRAFT]`.
- **Document ID:** `photo` (single document).
- **Access Rule:** Readable strictly by the owner and household members (`users/{uid}/private/membership`).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `photoUrl` | string | No | Owner | Cloudinary image URL `[DRAFT]`. `[PROPOSED]` |

---

### 2.2 Collection: `areas`
- **Source:** PRD Section 8 (`areas` collection) and PRD Module 2 / 4 (FR2.2, FR2.3, FR4.1).
- **Purpose:** Pre-seeded geographic area reference data around UM for searching, route planning, and area guides.
- **Document ID:** `{areaId}` (e.g., `"pantai_dalam"`, `"kerinchi"`, `"bangsar_south"`).
- **Used by:** Module 2 (FR2.2, FR2.3), Module 4 (FR4.1, FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `areaId` | string | Yes | Seed / Admin | Unique slug identifier. `PRD Section 8` |
| `name` | string | Yes | Seed / Admin | Area display name (e.g., "Pantai Dalam"). `FR2.2` |
| `lat` | number | Yes | Seed / Admin | Latitude of area center. `FR4.1` |
| `lng` | number | Yes | Seed / Admin | Longitude of area center. `FR4.1` |
| `rentLowSen` | number | Yes | Seed / Admin / Server | Estimated low room price in area in integer sen (RM). `FR2.3` |
| `rentHighSen` | number | Yes | Seed / Admin / Server | Estimated high room price in area in integer sen (RM). `FR2.3` |
| `nearestStations` | array<map> | Yes | Seed / Admin | `[{ stationName: string, mode: string ("LRT"/"MRT"/"Bus"), distanceMeters: number }]`. `FR4.1` |

---

### 2.3 Collection: `listings`
- **Source:** PRD Section 8 (`listings` collection) and PRD Module 2 (FR2.1, FR2.2).
- **Purpose:** Property listings posted by student Hosts. Public document contains general property metadata and search attributes.
- **Document ID:** `{listingId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.1, FR2.2), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `listingId` | string | Yes | Host (on create) | Unique listing ID. `PRD Section 8` |
| `ownerUid` | string | Yes | Host (on create) | Firebase Auth UID of the Host. `FR2.1`, `E2.1` |
| `areaId` | string | Yes | Host | Foreign key to `areas/{areaId}`. `FR2.1`, `E2.1` |
| `buildingName` | string | Yes | Host | Building/Property name (picked via place search / Cloudflare Worker place search proxy `[DRAFT]`). `FR2.1`, `E2.1` |
| `placeId` | string | No | Host | Optional place ID `[DRAFT]`. `FR2.1` |
| `lat` | number | Yes | Host | General latitude for commute calculations. `FR4.1` `[PROPOSED]` |
| `lng` | number | Yes | Host | General longitude for commute calculations. `FR4.1` `[PROPOSED]` |
| `totalRentSen` | number | Yes | Host | Total monthly rent for entire unit in integer sen (RM). Price Rule: sum of all room prices <= `totalRentSen`. `FR2.1`, `E2.1` |
| `depositSen` | number | Yes | Host | Deposit amount required in integer sen (RM). `FR2.1`, `E2.1` |
| `genderPref` | string | Yes | Host | Preference: `"male_only"`, `"female_only"`, `"any"`. `FR2.1`, `E2.1` |
| `amenities` | array<string> | Yes | Host | Amenities list e.g. `["wifi", "aircon", "washing_machine"]`. `FR2.1`, `E2.1` |
| `photoUrls` | array<string> | Yes | Host | Cloudinary image URLs `[DRAFT]`. `FR2.1`, `E2.1` |
| `isSample` | boolean | Yes | Host / Seed | True for fictional seed listings; app labels them as sample data. `[PROPOSED]`, `E2.4` |
| `status` | string | Yes | Host / Server | Status: `"active"`, `"closed"`, `"expired"`. `E2.3` |
| `createdAt` | timestamp | Yes | Host (on create) | Creation timestamp. `E2.1` |
| `updatedAt` | timestamp | Yes | Host | Last update timestamp. `[PROPOSED]` |
| `expiresAt` | timestamp | Yes | Host / Server | Auto-expiry date (30 days from creation/renewal). `PRD Section 8` `[PROPOSED]` |

#### Subcollection: `listings/{listingId}/private/location`
- **Source:** PRD Section 5 (NFR2 Privacy) and PRD Section 5 Stage Table (Connection stage).
- **Purpose:** Stores exact street address, unit number, and active resident UIDs. Isolated so public search cannot expose exact locations prior to an accepted connection.
- **Document ID:** `location` (single document).
- **Access Rule:** Readable strictly by the Host (`ownerUid`), by residents in `residentUids`, or by users whose request for THAT listing the host has accepted (`listingRequests/{listingId}_{requesterUid}` status == `"accepted"`). Everyone else sees only building name and area.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `exactAddress` | string | Yes | Host | Full address with unit/door number (e.g. "A-12-03, Menara Bangsar"). `NFR2`, `E2.1` |
| `residentUids` | array<string> | Yes | Host / Server | Array of Auth UIDs of residents currently living in this listing unit (including the Host). `NFR2`, `[PROPOSED]` |

---

### 2.4 Collection: `listingRequests` `[PROPOSED]`
- **Source:** `[PROPOSED]` (Needs Ary's confirmation for E4.3 / E5.1).
- **Purpose:** Stores listing-specific connection requests sent to a Host to request a viewing and unlock exact address. Proves Connection between applicant and Host when status is `"accepted"`.
- **Document ID:** `{listingId}_{requesterUid}` (deterministic ID combining `listingId` and applicant's Auth UID).
- **Used by:** Module 3 (FR3.3), Module 2 (FR2.1).
- **Access Rule:** Requester (`request.auth.uid == requesterUid`) may only create a request with `status == "pending"`, and may cancel it (`status == "cancelled"`). Only the Host (`request.auth.uid == hostUid`, where `hostUid == listings/{listingId}.ownerUid`) may set `status` to `"accepted"` or `"rejected"`.

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `requestId` | string | Yes | Requester (on create) | Deterministic ID `{listingId}_{requesterUid}`. `[PROPOSED]` |
| `listingId` | string | Yes | Requester (on create) | Foreign key to `listings/{listingId}`. `[PROPOSED]` |
| `hostUid` | string | Yes | Requester (on create) | Auth UID of listing Host. Must equal `listings/{listingId}.ownerUid`. `[PROPOSED]` |
| `requesterUid` | string | Yes | Requester (on create) | Auth UID of applicant (`request.auth.uid`). `[PROPOSED]` |
| `message` | string | Yes | Requester | Short introduction message sent with the viewing request. `FR3.3` `[PROPOSED]` |
| `status` | string | Yes | Host / Requester | Status: `"pending"`, `"accepted"`, `"rejected"`, `"cancelled"`. Requester creates as `"pending"` or sets `"cancelled"`; Host sets `"accepted"` or `"rejected"`. `FR3.3` `[PROPOSED]` |
| `createdAt` | timestamp | Yes | Requester (on create) | Creation timestamp. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Host / Requester | Last status change timestamp. `[PROPOSED]` |

---

### 2.4.1 Collection: `connections` `[PROPOSED]`
- **Source:** `[PROPOSED]` (Needs Ary's confirmation). Rules cannot find the listing from a user's contact document, so one document per pair of people is the proof of a Connection (see Proof of Connection in section 1).
- **Purpose:** Records that two people are connected: a student and a Host after the Host accepted a request, or two students after both accepted a Match. Opens `contact`, `phone` and chat for both.
- **Document ID:** `{pairId}` (the two Auth UIDs sorted alphabetically and joined with `_`, as in `matchId`).
- **Used by:** Module 3 (FR3.3), Module 2 (FR2.1).
- **Access Rule:** Readable only by the two people in it (`request.auth.uid == uidA || request.auth.uid == uidB`). Either person may set `status` to `"ended"`; nobody else may change it.

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `pairId` | string | Yes | On create | Same as the document ID. `[PROPOSED]` |
| `uidA` | string | Yes | On create | The smaller UID of the two. `[PROPOSED]` |
| `uidB` | string | Yes | On create | The larger UID of the two. `[PROPOSED]` |
| `source` | string | Yes | On create | `"listingRequest"` or `"match"`. `FR3.3` |
| `listingId` | string | No | On create | Set when `source == "listingRequest"`. `FR3.3` |
| `hostUid` | string | No | On create | Set when `source == "listingRequest"`; must equal `listings/{listingId}.ownerUid`. `FR3.3` |
| `status` | string | Yes | On create / either person | `"active"` or `"ended"`. `FR3.3` |
| `createdAt` | timestamp | Yes | On create | Creation timestamp. `[PROPOSED]` |
| `endedAt` | timestamp | No | Either person | Set when `status` becomes `"ended"`; starts the chat retention period in section 3. `[PROPOSED]` |

> **Who creates it `[OPEN QUESTION for Aaron]`:** Firestore triggers are not on the free plan, so either the Cloudflare Worker creates the document when a request or match is accepted, or the accepting person's app creates it and Rules allow the create only if the accepted `listingRequests` or `matches` document exists for exactly that pair (the Rules can read `listingId` from the new document's data).

---

### 2.5 Collection: `rooms`
- **Source:** PRD Section 8 (`rooms` collection) and PRD Module 2 (FR2.1, FR2.2).
- **Purpose:** Individual rooms within a posted listing. Room search operates on vacant room cards.
- **Document ID:** `{roomId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.1, FR2.2), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `roomId` | string | Yes | Host (on create) | Unique room ID. `PRD Section 8` |
| `listingId` | string | Yes | Host (on create) | Foreign key to `listings/{listingId}`. `FR2.1`, `E2.2` |
| `type` | string | Yes | Host | Room type: `"master"`, `"medium"`, `"small"`. `FR2.1`, `E2.2` |
| `priceSen` | number | Yes | Host | Monthly room rent price in integer sen (RM). Price Rule: sum of all room prices for listing <= `listings.totalRentSen`. `FR2.1`, `E2.2` |
| `status` | string | Yes | Host | Room status: `"vacant"`, `"occupied"`. `FR2.1`, `E2.3` |
| `occupiedByUid` | string | No | Host | Auth UID of resident occupying the room (if signed-in user). `[PROPOSED]` |
| `createdAt` | timestamp | Yes | Host (on create) | Creation timestamp. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Host | Last update timestamp. `[PROPOSED]` |

---

### 2.6 Collection: `reviews`
- **Source:** PRD Section 8 (`reviews` collection) and PRD Module 2 (FR2.3).
- **Purpose:** Student-submitted building reviews for area guides and building ratings.
- **Document ID:** `{reviewId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `reviewId` | string | Yes | Author (on create) | Unique review ID. `PRD Section 8` |
| `buildingName` | string | Yes | Author | Primary key for building reviews. `FR2.3`, `E9.4` |
| `placeId` | string | No | Author | Optional place ID `[DRAFT]` for building verification. `[PROPOSED]` |
| `listingId` | string | No | Author | Associated listing ID if reviewed via a listing. `PRD Section 8` |
| `authorUid` | string | Yes | Author (on create) | Auth UID of the reviewer (must be verified current/former resident). `FR2.3` |
| `water` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `internet` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `security` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `noise` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `text` | string | Yes | Author | Detailed review commentary. `FR2.3` |
| `moderated` | boolean | Yes | Server / Admin | Default `false`. Only `true` reviews are public. `FR2.3` |
| `createdAt` | timestamp | Yes | Author (on create) | Submission timestamp. `[PROPOSED]` |

---

### 2.7 Collection: `matchProfiles`
- **Source:** PRD Section 8 (`matchProfiles` collection) and PRD Module 3 (FR3.1). Aligned with `backend/functions/src/match.js`.
- **Purpose:** Roommate compatibility questionnaire answers using 7 standardized factors (`sleep`, `cleanliness`, `noise`, `cooking`, `smoking`, `pets`, `budget`).
- **Document ID:** `{uid}` (Firebase Auth UID).
- **Used by:** Module 3 (FR3.1, FR3.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `uid` | string | Yes | Owner | Firebase Auth UID. `PRD Section 8` |
| `answers` | map | Yes | Owner | Map of 7 standardized factors: `{ sleep: number (minutes after midnight 0..1439), cleanliness: number (0..4), noise: number (0..4), cooking: number (0..4), smoking: boolean, pets: boolean, budget: { minSen: number, maxSen: number } }`. `FR3.1`, `E6.1` |
| `updatedAt` | timestamp | Yes | Owner | Last update timestamp. `PRD Section 8` |

---

### 2.8 Collection: `matches`
- **Source:** PRD Section 8 (`matches` collection) and PRD Module 3 (FR3.2, FR3.3). Aligned with `backend/functions/src/match.js` and `docs/api-contracts.md`.
- **Purpose:** Pairwise compatibility score, factor similarities, AI/rule match reasons, overlapping class days, and Connection request status. Proves Connection between roommate matches when `status == "connected"`.
- **Document ID:** `{matchId}` (Alphabetically sorted `"{uidA}_{uidB}"`).

> **Deterministic Document ID Note:** The document ID `{matchId}` is constructed by sorting the two participant Firebase Auth UIDs lexicographically/alphabetically (`uidA < uidB ? "${uidA}_${uidB}" : "${uidB}_${uidA}"`). This ensures a deterministic path for Security Rules lookups via `get()` without querying collections.

- **Used by:** Module 3 (FR3.2, FR3.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `matchId` | string | Yes | Server / User | Unique pairwise ID (`"{uidA}_{uidB}"`). `PRD Section 8` |
| `uidA` | string | Yes | Server / User | First user Auth UID. `FR3.2`, `E6.2` |
| `uidB` | string | Yes | Server / User | Second user Auth UID. `FR3.2`, `E6.2` |
| `status` | string | Yes | Server | Combined state: `"match"`, `"requested"`, `"connected"`, `"rejected"`. `[PROPOSED]` |
| `scoreBasisPoints` | number | Yes | Server | Deterministic weighted compatibility basis points (0–10000). Server-written. `FR3.2` |
| `score` | number | Yes | Server | Compatibility score percentage (0.00–100.00, derived: `scoreBasisPoints / 100`). Server-written. `FR3.2` |
| `formulaVersion` | string | Yes | Server | Formula version string (e.g. `"normalized-v1"`). `FR3.2` |
| `weightVersion` | string | Yes | Server | Weight version string (e.g. `"synthetic-v1"` or `"draft-v1"`). `FR3.2` |
| `factors` | array<map> | Yes | Server | `[{ factor: string, weightBasisPoints: number, similarityBasisPoints: number }]` for all 7 factors. `FR3.2` |
| `reasons` | array<map> | Yes | Server | Deterministic rule reason codes: `[{ factor: string, code: string ("similar"/"different"/"partial_overlap") }]`. `FR3.2` |
| `aiReasons` | array<string> | No | Server | AI-generated explanation strings (if AI call succeeded). `FR3.2` |
| `aiGenerated` | boolean | Yes | Server | `true` if `aiReasons` are AI-generated; `false` if rule fallback was used. `FR3.2`, `FR6.3` |
| `fallbackUsed` | boolean | Yes | Server | `true` if rule-generated fallback reasons were used. `FR3.2` |
| `overlappingClassDays` | array<number> \| null | No | Server | ISO weekdays with overlapping classes e.g. `[1, 3]` (Mon, Wed), or `null` if uncalculated / incomplete profile timetable data. `FR3.2` |
| `statusA` | string | Yes | User A | User A response: `"none"`, `"requested"`, `"accepted"`, `"rejected"`. `FR3.3`, `E5.1` |
| `statusB` | string | Yes | User B | User B response: `"none"`, `"requested"`, `"accepted"`, `"rejected"`. `FR3.3`, `E5.1` |
| `messageA` | string | No | User A | Optional introduction message from User A when sending request. `FR3.3` |
| `messageB` | string | No | User B | Optional introduction message from User B when sending request. `FR3.3` |
| `createdAt` | timestamp | Yes | Server / User | Creation timestamp. `PRD Section 8` |
| `updatedAt` | timestamp | Yes | Server / User | Last state change timestamp. `[PROPOSED]` |

---

### 2.9 Collection: `groups`
- **Source:** PRD Section 8 (`groups` collection) and PRD Module 3 (FR3.3) / Module 2 (FR2.4).
- **Purpose:** Group of 2–4 students forming a rental group to rent a whole unit together.
- **Document ID:** `{groupId}` (auto-generated ID).
- **Used by:** Module 3 (FR3.3), Module 2 (FR2.4).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `groupId` | string | Yes | Member (on create) | Unique group ID. `PRD Section 8` |
| `memberUids` | array<string> | Yes | Member | Array of member Auth UIDs (2–4 members). `FR3.3`, `E9.2` |
| `targetListingId` | string | No | Member | Foreign key to `listings/{listingId}` if targeting a Jiran listing. `FR3.3` |
| `targetEvalId` | string | No | Member | Foreign key to `unitEvaluations/{evalId}` if targeting an evaluated unit. `[PROPOSED]` |
| `spots` | number | Yes | Member | Maximum group size (2–4). `FR3.3`, `E9.2` |
| `roomSplit` | array<map> | Yes | Member | `[{ memberUid: string, roomType: string, priceSen: number }]`. Sum must equal unit total rent in sen. `FR3.3`, `E9.2` |
| `splitOption` | string | Yes | Member | Option: `"equal"`, `"room_type"`, `"custom"`. `FR3.3`, `E9.2` |
| `memberStatus` | array<map> | Yes | Member | `[{ uid: string, status: string ("in"/"not_sure"/"needs_changes"), note: string }]`. `FR3.3`, `E9.2` |
| `checklist` | map | Yes | Member | Decision checklist e.g. `{ contractChecked: boolean, depositDiscussed: boolean, signerUid: string }`. `FR3.3`, `E9.2` |
| `acceptedBy` | array<string> | Yes | Member | Array of member UIDs who accepted the room price split. Reset when any price changes. `FR3.3` |
| `status` | string | Yes | Member | Group state: `"forming"`, `"confirmed"`, `"dissolved"`. `FR3.3` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `[PROPOSED]` |

---

### 2.10 Collection: `unitEvaluations`
- **Source:** PRD Section 8 (`unitEvaluations` collection) and PRD Module 2 (FR2.4).
- **Purpose:** Private evaluation of any unit (internal or external) by an individual student or a group.
- **Document ID:** `{evalId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.4), Module 3 (FR3.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `evalId` | string | Yes | Creator (on create) | Unique evaluation ID. `PRD Section 8` |
| `createdByUid` | string | Yes | Creator (on create) | Auth UID of creator. `[PROPOSED]` |
| `groupId` | string | No | Creator / Member | Foreign key to `groups/{groupId}` if attached to a group. Private to creator/group members. `FR2.4`, `E9.1` |
| `buildingName` | string | Yes | Creator | Building name (picked via place search / Cloudflare Worker place search proxy `[DRAFT]`). `FR2.4`, `E9.1` |
| `areaId` | string | No | Creator | Foreign key to `areas/{areaId}`. `FR2.4` |
| `lat` | number | Yes | Creator | Latitude for commute calculation. `FR2.4` |
| `lng` | number | Yes | Creator | Longitude for commute calculation. `FR2.4` |
| `referenceUrl` | string | No | Creator | Text note typed by user (e.g., PropertyGuru note; never fetched by server). `FR2.4` |
| `totalRentSen` | number | Yes | Creator | Total monthly unit rent in integer sen (RM). `FR2.4`, `E9.1` |
| `depositSen` | number | No | Creator | Deposit amount in integer sen (RM). `FR2.4`, `E9.1` |
| `rooms` | array<map> | Yes | Creator | `[{ type: string ("master"/"medium"/"small"), priceSen: number, assignedUid: string }]`. Sum must equal `totalRentSen`. `FR2.4`, `E9.1` |
| `createdAt` | timestamp | Yes | Creator (on create) | Creation timestamp. `PRD Section 8` |
| `expiresAt` | timestamp | Yes | Creator / Server | Auto-deletion timestamp (90 days if not attached to a Group). `PRD Section 8` `[PROPOSED]` |

---

### 2.11 Collection: `households`
- **Source:** PRD Section 8 (`households` collection) and PRD Module 5 (FR5.1, FR5.2, FR5.3).
- **Purpose:** Active shared household created when a group tenancy starts or joined via invite code. Proves Connection between active co-residents.
- **Document ID:** `{householdId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.1, FR5.2, FR5.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `householdId` | string | Yes | Member (on create) | Unique household ID. `PRD Section 8` |
| `memberUids` | array<string> | Yes | Member | Auth UIDs of active household members. `FR5.1`, `E7.2` |
| `hostUid` | string | Yes | Member | Auth UID of designated Host / tenancy holder. `[PROPOSED]` |
| `listingId` | string | No | Member | Associated listing ID (if converted from a Jiran listing). `PRD Section 8` |
| `inviteCode` | string | Yes | Member / Server | Secret invite code to join household. `FR5.1` `[PROPOSED]` |
| `rules` | array<map> | Yes | Member | `[{ text: string, acceptedBy: array<string> }]`. Modifying resets `acceptedBy`. `FR5.3`, `E7.2` |
| `status` | string | Yes | Member | Status: `"active"`, `"closed"`. `FR5.1` `[PROPOSED]` |
| `closedAt` | timestamp | No | Member / Server | Closure timestamp (retained read-only for 6 months). `PRD Section 8` `[PROPOSED]` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `PRD Section 8` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `[PROPOSED]` |

---

### 2.12 Collection: `expenses`
- **Source:** PRD Section 8 (`expenses` collection) and PRD Module 5 (FR5.1).
- **Purpose:** Shared household expenses and debt tracking in integer sen.
- **Document ID:** `{expenseId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.1).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `expenseId` | string | Yes | Member (on create) | Unique expense ID. `PRD Section 8` |
| `householdId` | string | Yes | Member (on create) | Foreign key to `households/{householdId}`. `FR5.1`, `E7.1` |
| `description` | string | Yes | Member | Expense title (e.g., "Electricity Bill"). `FR5.1`, `E7.1` |
| `amountSen` | number | Yes | Member | Total expense amount in integer MYR sen (RM). `FR5.1`, `E7.1` |
| `paidByUid` | string | Yes | Member | Auth UID of member who paid. `FR5.1`, `E7.1` |
| `splitAmong` | array<map> | Yes | Member | `[{ uid: string, shareSen: number }]`. Sum of `shareSen` must equal `amountSen`. `FR5.1`, `E7.1` |
| `date` | timestamp | Yes | Member | Expense date. `FR5.1`, `E7.1` |
| `settled` | boolean | Yes | Member | True when marked settled across all members. `FR5.1`, `E7.1` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `[PROPOSED]` |

---

### 2.13 Collection: `chores`
- **Source:** PRD Section 8 (`chores` collection) and PRD Module 5 (FR5.2).
- **Purpose:** Household recurring chore assignments and rotation schedules.
- **Document ID:** `{choreId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `choreId` | string | Yes | Member (on create) | Unique chore ID. `PRD Section 8` |
| `householdId` | string | Yes | Member (on create) | Foreign key to `households/{householdId}`. `FR5.2`, `E7.2` |
| `task` | string | Yes | Member | Chore name (e.g., "Kitchen Cleaning"). `FR5.2`, `E7.2` |
| `frequency` | string | Yes | Member | Frequency: `"daily"`, `"weekly"`, `"biweekly"`. `FR5.2`, `E7.2` |
| `rotation` | array<string> | Yes | Member | Ordered array of member Auth UIDs in rotation. `FR5.2`, `E7.2` |
| `currentUid` | string | Yes | Member | Auth UID of member currently assigned. `FR5.2`, `E7.2` |
| `dueDate` | timestamp | Yes | Member | Next due timestamp. `FR5.2`, `E7.2` |
| `done` | boolean | Yes | Member | Marked done flag. `FR5.2`, `E7.2` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `[PROPOSED]` |

---

### 2.14 Collection: `contractChecks`
- **Source:** PRD Section 8 (`contractChecks` collection) and PRD Module 6 (FR6.2, FR6.3).
- **Purpose:** Private tenancy agreement check metadata, AI summary, and clause risk flags. Uploaded contract files (PDF or photo) are sent directly to the check service (Cloudflare Worker), processed ephemerally in memory, and **NOT retained** on disk, database, or storage.
- **Document ID:** `{checkId}` (auto-generated ID).
- **Used by:** Module 6 (FR6.2, FR6.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `checkId` | string | Yes | Owner (on create) | Unique contract check ID. `PRD Section 8` |
| `uid` | string | Yes | Owner (on create) | Auth UID of uploader. Accessible strictly by owner (`uid == request.auth.uid`). `FR6.2`, `NFR2` |
| `summary` | string | Yes | Server | AI-generated plain-language summary. Server-written. `FR6.2`, `E8.3` |
| `flags` | array<map> | Yes | Server | `[{ level: string ("red_flag"/"check"/"ok"), clause: string, text: string }]`. Server-written. `FR6.2` |
| `aiGenerated` | boolean | Yes | Server | `true`. Always flagged as AI-generated per FR6.3. `FR6.3` |
| `createdAt` | timestamp | Yes | Owner (on create) | Check submission timestamp. `PRD Section 8` |
| `deleteAfter` | timestamp | Yes | Owner / Server | Scheduled auto-deletion timestamp (30 days from creation). `FR6.2`, `PRD Section 8` |

---

### 2.15 Collection: `chats` `[PROPOSED]`
- **Source:** `[PROPOSED]` (Addresses PRD Open Question 5: "A store for chat is still missing").
- **Purpose:** Private messaging channels between connected users or Host/applicant pairs.
- **Document ID:** `{chatId}` (auto-generated ID or `"{uidA}_{uidB}"`).
- **Used by:** Module 3 (FR3.3), Module 6 (FR6.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `chatId` | string | Yes | Participant | Unique chat channel ID. `E5.2` `[PROPOSED]` |
| `participantUids` | array<string> | Yes | Participant | UIDs of the two chat participants. `FR3.3`, `E5.2` `[PROPOSED]` |
| `listingId` | string | No | Participant | Associated listing ID if chat originated from a room. `E5.2` `[PROPOSED]` |
| `matchId` | string | No | Participant | Associated match ID. `E5.2` `[PROPOSED]` |
| `createdAt` | timestamp | Yes | Participant | Channel creation timestamp. `E5.2` `[PROPOSED]` |
| `lastMessageAt` | timestamp | Yes | Participant | Timestamp of latest message. `E5.2` `[PROPOSED]` |

#### Subcollection: `chats/{chatId}/messages`
- **Source:** `[PROPOSED]` (PRD FR3.3 / FR6.2 chat messaging).
- **Purpose:** Chat messages between connected users.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `messageId` | string | Yes | Sender (on create) | Unique message ID. `E5.2` `[PROPOSED]` |
| `senderUid` | string | Yes | Sender (on create) | Auth UID of sender. `E5.2` `[PROPOSED]` |
| `text` | string | Yes | Sender | Text content. `E5.2` `[PROPOSED]` |
| `attachmentType` | string | No | Sender | Attachment type: `"photo"`, `"none"`. (Note: contract files are processed ephemerally from local file selection and never stored as chat attachments). `FR6.2` `[PROPOSED]` |
| `attachmentUrl` | string | No | Sender | Cloudinary image URL `[DRAFT]` if photo attachment is present. `FR6.2` `[PROPOSED]` |
| `sentAt` | timestamp | Yes | Sender (on create) | Sent timestamp. `E5.2` `[PROPOSED]` |

---

### 2.16 Collection: `config` `[PROPOSED]`
- **Source:** `[PROPOSED]` (PRD FR2.1 / FR2.4 default room price weights, ASSUMPTIONS #1, #7).
- **Purpose:** Read-only global system application configuration parameters in integer sen.
- **Document ID:** `app` (single document in `config`).
- **Used by:** Module 2 (FR2.1, FR2.4), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `roomPriceWeights` | map | Yes | Admin / Seed | `{ master: 1.3, medium: 1.0, small: 0.7 }`. Default price split weights. `ASSUMPTIONS #1` `[PROPOSED]` |
| `utilitiesEstimateSen` | number | Yes | Admin / Seed | Default utility estimate per person per month in integer sen (6200 sen = RM 62 sample). `ASSUMPTIONS #7` `[PROPOSED]` |
| `updatedAt` | timestamp | Yes | Admin / Seed | Configuration update timestamp. `[PROPOSED]` |

---

### 2.17 Optional Collections `[PROPOSED]` & Deferred Collections

#### `reports` `[PROPOSED]`
- **Source:** `[PROPOSED]` (PRD Module 2 acceptance criteria: "users can report a listing or review").
- **Purpose:** User reporting mechanism for inappropriate listings, reviews, or chat messages.
- **Fields:** `reportId` (string), `reporterUid` (string), `targetType` (string: `"listing"`/`"review"`/`"user"`), `targetId` (string), `reason` (string), `createdAt` (timestamp), `resolved` (boolean).

#### `notifications` `[PROPOSED]`
- **Source:** `[PROPOSED]` (PRD Open Question 5 candidate collections).
- **Purpose:** In-app notification delivery log and FCM push history.
- **Fields:** `notificationId` (string), `recipientUid` (string), `title` (string), `body` (string), `type` (string), `data` (map), `read` (boolean), `createdAt` (timestamp).

#### Deferred: `commuteGroups` & `commuteMembers` (Module 7 - P2)
- **Scope Note:** Shared commute group collections (`commuteGroups` and `commuteMembers`) described in Module 7 are deferred to P2 and excluded from the core P0/P1 database schema.

---

## 3. Data Retention & Deletion Schedule `[DRAFT]`

In compliance with the **Retention Principle of PDPA 2010** (personal data must not be retained longer than necessary for its purpose), automated cleanup schedules are specified below:

| Collection / Data | PRD / ASSUMPTIONS Value | Schema Retention Policy Value | Deletion Mechanism `[OPEN QUESTION]` | PDPA 2010 Justification `[DRAFT]` |
|---|---|---|---|---|
| `contractChecks` | **30 days** (PRD FR6.2 / Section 8, ASSUMPTIONS #8) | **30 days** | `[OPEN QUESTION for Aaron]` | Analysis result is temporary. Tenancy agreements contain highly confidential terms; 30 days allows sufficient time for student review before permanent purge. |
| Unattached `unitEvaluations` | **90 days** (PRD FR2.4 acceptance criteria, ASSUMPTIONS #8) | **90 days** | `[OPEN QUESTION for Aaron]` | Evaluations not linked to an active Group expire after 90 days as market prices change and search intent ends. |
| Closed `listings` | **30 days** (PRD FR2.2 acceptance criteria: listing closes after 30 days if not renewed; ASSUMPTIONS #8) | **30 days** after closing/expiry | `[OPEN QUESTION for Aaron]` | Inactive/closed listings are purged 30 days after closing unless renewed by Host, preventing stale search results. |
| Closed `households` | **6 months** (PRD FR5.1 acceptance criteria, ASSUMPTIONS #8) | **6 months** after closure | `[OPEN QUESTION for Aaron]` | Retained read-only for 6 months after household dissolution so former housemates can verify and settle lingering debt balances before complete purge. |
| `chats` & messages | *Not explicitly specified in PRD* | **Duration of Connection + 30 days** (counted from `connections.endedAt`) | `[OPEN QUESTION for Aaron]` | Chat channels and messages are retained for 30 days after a Connection is closed/terminated to allow users to resolve pending viewing arrangements, deposit queries, or safety concerns before permanent purge in compliance with the PDPA 2010 Retention Principle. |

> **Retention Deletion Mechanism Open Question:** Scheduled Cloud Functions are unavailable on the free Firebase tier. Options for Aaron/Gathan to evaluate:
> 1. Client-triggered cleanup on query/write.
> 2. Scheduled Cloudflare Worker cron job calling Firestore REST API using Admin credentials.
> 3. Native Firestore TTL policy (if supported on free plan).

---

## 4. Document Conflicts & Ambiguities

During schema synthesis across `PRD.md`, `ASSUMPTIONS.md`, and `docs/team/work-breakdown.md`, the following conflicts were identified:

1. **Profile Photo Field Missing in PRD Table:**
   - *Conflict:* PRD Section 1.3 & Stage Table state profile photos are shown in the Household stage, but PRD Section 8 `users` table lists NO photo field.
   - *Resolution:* Added `users/{uid}/private/photo` (`photoUrl`), readable strictly by the owner and household members (`users/{uid}/private/membership`).

2. **`reviews` Primary Key (`listingId` vs. `buildingName`):**
   - *PRD Section 8 `reviews` table:* States `listingId or buildingName`.
   - *Problem:* Listings expire after 30 days, whereas building reviews must persist permanently on area pages (FR2.3).
   - *Resolution:* `reviews` uses `buildingName` (and `placeId` `[DRAFT]`) as the primary key reference, with `listingId` marked optional.

3. **`unitEvaluations` Ownership (`groupId` vs. Individual Creator):**
   - *PRD Section 8:* Lists `unitEvaluations` with `groupId`.
   - *FR2.4:* Mentions an evaluation can be made by an individual student before joining/forming a group, or attached to a group later.
   - *Resolution:* Added `createdByUid` as required, with `groupId` marked optional.

4. **Contract Check Shared Attachment Access & Storage:**
   - *PRD FR6.2:* States Host shares tenancy agreement in Connection chat, and applicant checks it from chat.
   - *Free Plan Constraint:* Firebase Storage is unavailable.
   - *Resolution:* Contract files/photos are uploaded directly to the Cloudflare Worker server for processing and immediately discarded (`filePath` removed). The server returns `summary` and `flags` saved to `contractChecks`. No contract file is retained on disk or database.

---

## 5. Security Rules Enforceability & Server (Cloudflare Worker) Requirements

Firestore Security Rules cannot execute multi-document aggregations, complex arithmetic across subcollections, or third-party API calls. The following logic **cannot** be enforced by rules alone and MUST be performed by the server (Cloudflare Worker):

1. **Room Price Cap Rule Validation (FR2.1):**
   - *Rule:* Sum of room prices (`rooms` collection) for a listing must not exceed `listings.totalRentSen`.
   - *Why Rules fail:* Security Rules cannot aggregate prices across multiple `rooms` documents when individual room documents are created or updated.
   - *Server Task:* Cloudflare Worker validates or writes room collections atomically (`splitRoomPrices`).

2. **Deterministic Match Score, AI Reasons & Class Overlap (`computeMatchScores`):**
   - *Server Task:* Reads `matchProfiles` and student timetables, calculates weighted score formula, derives `overlappingClassDays` array, calls Gemini/Claude API for reasons (or uses rule fallback), and writes or returns results for `matches`.
   - *Open Question for Aaron:* Does the Cloudflare Worker write `scoreBasisPoints`, `reasons`, and `overlappingClassDays` directly to Firestore using Admin credentials, or return them to the Android app for the app to write?

3. **AI Contract Analysis (`checkContract`):**
   - *Server Task:* Accepts contract PDF/image upload, processes text via Gemini/Claude API, and writes or returns `summary` and `flags` for `contractChecks`.
   - *Open Question for Aaron:* Does the Cloudflare Worker write `summary` and `flags` directly to Firestore, or return the result to the app?

4. **Household Invite Code Lookup (`joinHousehold`):**
   - *Server Task:* Validates secret household `inviteCode` and adds applicant's UID to `households.memberUids`.

5. **Commute Calculation & Route Directions (FR4.1, FR4.2):**
   - *Server Task:* `getCommuteOptions` and `getTransitRoute` endpoints use openrouteservice for walking and road travel times/distances, and Prasarana GTFS timetables for bus and rail schedules/routes, calculating peak/off-peak fares and CO2 estimates, and returning cached results.

6. **Connection documents `[OPEN QUESTION for Aaron]`:**
   - *Server Task:* when a Host accepts a `listingRequests` request or both students accept a `matches` document, create `connections/{pairId}`; or let Rules allow the app to create it after checking the accepted request (see section 2.4.1).

7. **Moderation & Listing Expiry:**
   - *Server Task:* Moderates review submissions (`moderated` boolean) and manages automated status updates for expired listings.
   - *Open Question for Aaron:* Does the Worker write `moderated` status directly to Firestore?
