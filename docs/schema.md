# Firestore Database Schema Specification

This document defines the complete Cloud Firestore database schema for Jiran, covering all 15 core collections, subcollections, privacy subdocuments, and system configurations.

---

## 1. Privacy Architecture & Stage-Based Visibility

Firestore Security Rules evaluate permissions on the **entire document**, not on individual fields. To protect user privacy in compliance with Malaysia's **Personal Data Protection Act 2010 (PDPA 2010)** and **NFR2**, sensitive data is strictly isolated into separate subcollections and subdocuments.

### Stage Visibility Matrix

| Stage | What is visible | What is NOT visible | Isolated Storage Path |
|---|---|---|---|
| **Match** | Initials/first name, faculty, year, lifestyle tags, match score & reasons, overlapping class days | Full legal name, photo, phone number, exact address, detailed timetable | `users/{uid}` (Public profile) |
| **Connection Request** | Match profile + short request message | Full name, photo, phone number, exact unit address | `matches/{matchId}` |
| **Connection** (both accepted) | Chat, first name, exact unit address (for viewing with Host), phone number (if opted in) | Profile photo, detailed timetable | `listings/{id}/private/location`, `users/{uid}/private/contact` |
| **Household** | Member timetables, shared expenses, chores, rules, profile photos (subject to FR1.3 settings) | Private contract check uploads | `users/{uid}/private/timetable`, `households/{id}` |

---

## 2. Collections Specification

### 2.1 Collection: `users`
- **Purpose:** Primary student user profile. Stores non-sensitive public metadata accessible at Match/Request stage.
- **Document ID:** `{uid}` (Firebase Auth UID).
- **Used by:** Module 1 (FR1.1, FR1.2, FR1.3), Module 3 (FR3.2), all modules.

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `uid` | string | Yes | Owner (on create) | Firebase Auth UID. `E1.3` |
| `umEmail` | string | Yes | Owner / Function | Must end with `@siswa.um.edu.my` or `@um.edu.my`. `FR1.1`, `E1.2` |
| `name` | string | Yes | Owner | Display name / First name or initials. `FR1.3`, `E1.3` |
| `faculty` | string | Yes | Owner | UM Faculty (e.g., `"FSKTM"`, `"FBL"`). `FR1.3`, `E1.3` |
| `year` | number | Yes | Owner | Academic year (1–4). `FR1.3`, `E1.3` |
| `homeAreaId` | string | No | Owner | Foreign key to `areas/{areaId}`. `E1.3` |
| `budgetMin` | number | Yes | Owner | Minimum monthly room budget (RM). `FR1.3`, `E1.3` |
| `budgetMax` | number | Yes | Owner | Maximum monthly room budget (RM). `FR1.3`, `E1.3` |
| `lifestyleTags` | array<string> | Yes | Owner | Array of tags e.g. `["quiet", "non_smoker", "early_riser"]`. `FR1.3`, `E1.3` |
| `hasCar` | boolean | Yes | Owner | Car ownership flag. `FR1.3`, `E1.3` |
| `privacy` | map | Yes | Owner | Toggles e.g. `{ showPhoneToConnections: boolean, showPhotoToHouseholdOnly: boolean }`. `FR1.3` |
| `createdAt` | timestamp | Yes | Owner (on create) | Profile creation timestamp. `E1.3` |
| `updatedAt` | timestamp | Yes | Owner | Last update timestamp. `E1.3` |

#### Subcollection: `users/{uid}/private/contact`
- **Purpose:** Stores highly sensitive personal contact data. Isolated to prevent unauthorized access at Match stage.
- **Document ID:** `contact` (single document).
- **Access Rule:** Readable strictly by the owner, or by connected users (`matches` status = connected AND `privacy.showPhoneToConnections == true`), or household members.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `fullName` | string | Yes | Owner | Full legal name for contracts/verifications. |
| `phoneNumber` | string | No | Owner | Phone number. `FR1.3`, `NFR2` |
| `email` | string | Yes | Owner | Primary email. `FR1.1` |

#### Subcollection: `users/{uid}/private/timetable`
- **Purpose:** Stores exact weekly class timetable (building, exact start/end times). Isolated because timetable details reveal daily physical presence.
- **Document ID:** `timetable` (single document).
- **Access Rule:** Readable strictly by the owner or household members (`households/{householdId}`).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `entries` | array<map> | Yes | Owner | `[{ day: string ("Mon".."Sun"), start: string ("09:00"), end: string ("11:00"), facultyCode: string, lat: number, lng: number }]`. `FR1.3` |

---

### 2.2 Collection: `areas`
- **Purpose:** Pre-seeded geographic area reference data around UM for searching, route planning, and area guides.
- **Document ID:** `{areaId}` (e.g., `"pantai_dalam"`, `"kerinchi"`, `"bangsar_south"`).
- **Used by:** Module 2 (FR2.2, FR2.3), Module 4 (FR4.1, FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `areaId` | string | Yes | Seed / Admin | Unique slug identifier. `E0.5` |
| `name` | string | Yes | Seed / Admin | Area display name (e.g., "Pantai Dalam"). `FR2.2` |
| `lat` | number | Yes | Seed / Admin | Latitude of area center. `FR4.1` |
| `lng` | number | Yes | Seed / Admin | Longitude of area center. `FR4.1` |
| `rentLow` | number | Yes | Seed / Admin / Function | Estimated low room price in area (RM). `FR2.3` |
| `rentHigh` | number | Yes | Seed / Admin / Function | Estimated high room price in area (RM). `FR2.3` |
| `nearestStations` | array<map> | Yes | Seed / Admin | `[{ stationName: string, mode: string ("LRT"/"MRT"/"Bus"), distanceMeters: number }]`. `FR4.1` |

---

### 2.3 Collection: `listings`
- **Purpose:** Property listings posted by student Hosts. Public document contains general property metadata and search attributes.
- **Document ID:** `{listingId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.1, FR2.2), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `listingId` | string | Yes | Host (on create) | Unique listing ID. `E2.1` |
| `ownerUid` | string | Yes | Host (on create) | Firebase Auth UID of the Host. `FR2.1`, `E2.1` |
| `areaId` | string | Yes | Host | Foreign key to `areas/{areaId}`. `FR2.1`, `E2.1` |
| `buildingName` | string | Yes | Host | Building/Property name from Google Places autocomplete. `FR2.1`, `E2.1` |
| `placeId` | string | Yes | Host | Google Places API Place ID. `FR2.1`, `E2.1` |
| `lat` | number | Yes | Host | General latitude for commute calculations. `FR4.1` |
| `lng` | number | Yes | Host | General longitude for commute calculations. `FR4.1` |
| `totalRent` | number | Yes | Host | Total monthly rent for the entire unit (RM). Price Rule: sum of all room prices <= `totalRent`. `FR2.1`, `E2.1` |
| `deposit` | number | Yes | Host | Deposit amount required (RM). `FR2.1`, `E2.1` |
| `genderPref` | string | Yes | Host | Preference: `"male_only"`, `"female_only"`, `"any"`. `FR2.1`, `E2.1` |
| `amenities` | array<string> | Yes | Host | Amenities list e.g. `["wifi", "aircon", "washing_machine"]`. `FR2.1`, `E2.1` |
| `photoPaths` | array<string> | Yes | Host | Firebase Storage relative paths to unit photos. `FR2.1`, `E2.1` |
| `status` | string | Yes | Host / Function | Status: `"active"`, `"closed"`, `"expired"`. `E2.3` |
| `transitCache` | map | No | Cloud Function | `{ nearestStation: string, peakFareRm: number, offPeakFareRm: number, co2GramsPerKm: number }`. |
| `createdAt` | timestamp | Yes | Host (on create) | Creation timestamp. `E2.1` |
| `updatedAt` | timestamp | Yes | Host | Last update timestamp. `E2.1` |
| `expiresAt` | timestamp | Yes | Host / Function | Auto-expiry date (30 days from creation/renewal). `PRD Section 8` |

#### Subcollection: `listings/{listingId}/private/location`
- **Purpose:** Stores exact street address and unit number. Isolated so public search cannot expose exact locations prior to a Connection.
- **Document ID:** `location` (single document).
- **Access Rule:** Readable strictly by the Host (`ownerUid`), or connected users with an active Connection (`chats`/`matches` connected) for viewing, or household members.

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `exactAddress` | string | Yes | Host | Full address with unit/door number (e.g. "A-12-03, Menara Bangsar"). `NFR2`, `E2.1` |
| `landlordContact` | string | No | Host | Optional landlord name/contact note for Host reference. |

---

### 2.4 Collection: `rooms`
- **Purpose:** Individual rooms within a posted listing. Room search operates on vacant room cards.
- **Document ID:** `{roomId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.1, FR2.2), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `roomId` | string | Yes | Host (on create) | Unique room ID. `E2.2` |
| `listingId` | string | Yes | Host (on create) | Foreign key to `listings/{listingId}`. `FR2.1`, `E2.2` |
| `type` | string | Yes | Host | Room type: `"master"`, `"medium"`, `"small"`. `FR2.1`, `E2.2` |
| `price` | number | Yes | Host | Monthly room rent price (RM). Price Rule: sum of all room prices for listing <= `listings.totalRent`. `FR2.1`, `E2.2` |
| `status` | string | Yes | Host | Room status: `"vacant"`, `"occupied"`. `FR2.1`, `E2.3` |
| `occupiedByUid` | string | No | Host | Auth UID of resident occupying the room (if applicable). |
| `createdAt` | timestamp | Yes | Host (on create) | Creation timestamp. `E2.2` |
| `updatedAt` | timestamp | Yes | Host | Last update timestamp. `E2.2` |

---

### 2.5 Collection: `reviews`
- **Purpose:** Student-submitted building reviews for area guides and building ratings.
- **Document ID:** `{reviewId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `reviewId` | string | Yes | Author (on create) | Unique review ID. `E9.4` |
| `buildingName` | string | Yes | Author | Primary key for building reviews. `FR2.3`, `E9.4` |
| `placeId` | string | No | Author | Google Places ID for building verification. |
| `listingId` | string | No | Author | Associated listing ID if reviewed via a listing. `PRD Section 8` |
| `authorUid` | string | Yes | Author (on create) | Auth UID of the reviewer (must be verified current/former resident). `FR2.3` |
| `water` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `internet` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `security` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `noise` | number | Yes | Author | Rating 1–5. `FR2.3` |
| `text` | string | Yes | Author | Detailed review commentary. `FR2.3` |
| `moderated` | boolean | Yes | Cloud Function / Admin | Default `false`. Only `true` reviews are public. `FR2.3` |
| `createdAt` | timestamp | Yes | Author (on create) | Submission timestamp. `E9.4` |

---

### 2.6 Collection: `matchProfiles`
- **Purpose:** Roommate compatibility questionnaire answers.
- **Document ID:** `{uid}` (Firebase Auth UID).
- **Used by:** Module 3 (FR3.1, FR3.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `uid` | string | Yes | Owner | Firebase Auth UID. `E6.1` |
| `answers` | map | Yes | Owner | Map of habits e.g. `{ sleep: string, clean: string, noise: string, cook: string, smoke: string, pets: string }`. `FR3.1`, `E6.1` |
| `updatedAt` | timestamp | Yes | Owner | Last update timestamp. `E6.1` |

---

### 2.7 Collection: `matches`
- **Purpose:** Pairwise compatibility score and Connection request status between two students.
- **Document ID:** `{matchId}` (Alphabetically sorted `"{uidA}_{uidB}"`).
- **Used by:** Module 3 (FR3.2, FR3.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `matchId` | string | Yes | Function / User | Unique pairwise ID (`"{uidA}_{uidB}"`). `E6.2` |
| `uidA` | string | Yes | Function / User | First user Auth UID. `FR3.2`, `E6.2` |
| `uidB` | string | Yes | Function / User | Second user Auth UID. `FR3.2`, `E6.2` |
| `score` | number | Yes | Cloud Function | Deterministic compatibility score (0–100%). Calculated by Cloud Function. `FR3.2` |
| `scores` | map | Yes | Cloud Function | Breakdown map `{ lifestyle: number, timetable: number, budget: number }`. |
| `reasons` | array<string> | Yes | Cloud Function | AI-generated (or rule fallback) match reasons. `FR3.2` |
| `statusA` | string | Yes | User A | User A response: `"none"`, `"requested"`, `"accepted"`, `"declined"`. `FR3.3`, `E5.1` |
| `statusB` | string | Yes | User B | User B response: `"none"`, `"requested"`, `"accepted"`, `"declined"`. `FR3.3`, `E5.1` |
| `status` | string | Yes | Function / User | Combined state: `"suggested"`, `"requested"`, `"connected"`, `"declined"`. |
| `createdAt` | timestamp | Yes | Function / User | Creation timestamp. `E6.2` |
| `updatedAt` | timestamp | Yes | Function / User | Last state change timestamp. `E5.1` |

---

### 2.8 Collection: `groups`
- **Purpose:** Group of 2–4 students forming a rental group to rent a whole unit together.
- **Document ID:** `{groupId}` (auto-generated ID).
- **Used by:** Module 3 (FR3.3), Module 2 (FR2.4).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `groupId` | string | Yes | Member (on create) | Unique group ID. `E9.2` |
| `memberUids` | array<string> | Yes | Member | Array of member Auth UIDs (2–4 members). `FR3.3`, `E9.2` |
| `targetListingId` | string | No | Member | Foreign key to `listings/{listingId}` if targeting a Jiran listing. `FR3.3` |
| `targetEvalId` | string | No | Member | Foreign key to `unitEvaluations/{evalId}` if targeting an evaluated unit. |
| `spots` | number | Yes | Member | Maximum group size (2–4). `FR3.3`, `E9.2` |
| `roomSplit` | array<map> | Yes | Member | `[{ memberUid: string, roomType: string, price: number }]`. Sum must equal unit total rent. `FR3.3`, `E9.2` |
| `splitOption` | string | Yes | Member | Option: `"equal"`, `"room_type"`, `"custom"`. `FR3.3`, `E9.2` |
| `memberStatus` | array<map> | Yes | Member | `[{ uid: string, status: string ("in"/"not_sure"/"needs_changes"), note: string }]`. `FR3.3`, `E9.2` |
| `checklist` | map | Yes | Member | Decision checklist e.g. `{ contractChecked: boolean, depositDiscussed: boolean, signerUid: string }`. `FR3.3`, `E9.2` |
| `acceptedBy` | array<string> | Yes | Member | Array of member UIDs who accepted the room price split. Reset when any price changes. `FR3.3` |
| `status` | string | Yes | Member | Group state: `"forming"`, `"confirmed"`, `"dissolved"`. `FR3.3` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `E9.2` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `E9.2` |

---

### 2.9 Collection: `unitEvaluations`
- **Purpose:** Private evaluation of any unit (internal or external) by an individual student or a group.
- **Document ID:** `{evalId}` (auto-generated ID).
- **Used by:** Module 2 (FR2.4), Module 3 (FR3.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `evalId` | string | Yes | Creator (on create) | Unique evaluation ID. `E9.1` |
| `createdByUid` | string | Yes | Creator (on create) | Auth UID of creator. |
| `groupId` | string | No | Creator / Member | Foreign key to `groups/{groupId}` if attached to a group. Private to creator/group members. `FR2.4`, `E9.1` |
| `buildingName` | string | Yes | Creator | Building name from Google Places autocomplete. `FR2.4`, `E9.1` |
| `areaId` | string | No | Creator | Foreign key to `areas/{areaId}`. `FR2.4` |
| `lat` | number | Yes | Creator | Latitude for commute calculation. `FR2.4` |
| `lng` | number | Yes | Creator | Longitude for commute calculation. `FR2.4` |
| `referenceUrl` | string | No | Creator | Text note typed by user (e.g., PropertyGuru note; never fetched by server). `FR2.4` |
| `totalRent` | number | Yes | Creator | Total monthly unit rent (RM). `FR2.4`, `E9.1` |
| `deposit` | number | No | Creator | Deposit amount (RM). `FR2.4`, `E9.1` |
| `rooms` | array<map> | Yes | Creator | `[{ type: string ("master"/"medium"/"small"), price: number, assignedUid: string }]`. Sum must equal `totalRent`. `FR2.4`, `E9.1` |
| `createdAt` | timestamp | Yes | Creator (on create) | Creation timestamp. `E9.1` |
| `expiresAt` | timestamp | Yes | Creator / Function | Auto-deletion timestamp (90 days if not attached to a Group). `PRD Section 8` |

---

### 2.10 Collection: `households`
- **Purpose:** Active shared household created when a group tenancy starts or joined via invite code.
- **Document ID:** `{householdId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.1, FR5.2, FR5.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `householdId` | string | Yes | Member (on create) | Unique household ID. `E7.2` |
| `memberUids` | array<string> | Yes | Member | Auth UIDs of active household members. `FR5.1`, `E7.2` |
| `hostUid` | string | Yes | Member | Auth UID of designated Host / tenancy holder. |
| `listingId` | string | No | Member | Associated listing ID (if converted from a Jiran listing). `PRD Section 8` |
| `inviteCode` | string | Yes | Member / Function | Secret invite code to join household. `FR5.1` |
| `rules` | array<map> | Yes | Member | `[{ text: string, acceptedBy: array<string> }]`. Modifying resets `acceptedBy`. `FR5.3`, `E7.2` |
| `status` | string | Yes | Member | Status: `"active"`, `"closed"`. `FR5.1` |
| `closedAt` | timestamp | No | Member / Function | Closure timestamp (retained read-only for 6 months). `PRD Section 8` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `E7.2` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `E7.2` |

---

### 2.11 Collection: `expenses`
- **Purpose:** Shared household expenses and debt tracking.
- **Document ID:** `{expenseId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.1).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `expenseId` | string | Yes | Member (on create) | Unique expense ID. `E7.1` |
| `householdId` | string | Yes | Member (on create) | Foreign key to `households/{householdId}`. `FR5.1`, `E7.1` |
| `description` | string | Yes | Member | Expense title (e.g., "Electricity Bill"). `FR5.1`, `E7.1` |
| `amount` | number | Yes | Member | Total expense amount in MYR (RM). `FR5.1`, `E7.1` |
| `paidByUid` | string | Yes | Member | Auth UID of member who paid. `FR5.1`, `E7.1` |
| `splitAmong` | array<map> | Yes | Member | `[{ uid: string, share: number }]`. Sum of shares must equal `amount`. `FR5.1`, `E7.1` |
| `date` | timestamp | Yes | Member | Expense date. `FR5.1`, `E7.1` |
| `settled` | boolean | Yes | Member | True when marked settled across all members. `FR5.1`, `E7.1` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `E7.1` |

---

### 2.12 Collection: `chores`
- **Purpose:** Household recurring chore assignments and rotation schedules.
- **Document ID:** `{choreId}` (auto-generated ID).
- **Used by:** Module 5 (FR5.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `choreId` | string | Yes | Member (on create) | Unique chore ID. `E7.2` |
| `householdId` | string | Yes | Member (on create) | Foreign key to `households/{householdId}`. `FR5.2`, `E7.2` |
| `task` | string | Yes | Member | Chore name (e.g., "Kitchen Cleaning"). `FR5.2`, `E7.2` |
| `frequency` | string | Yes | Member | Frequency: `"daily"`, `"weekly"`, `"biweekly"`. `FR5.2`, `E7.2` |
| `rotation` | array<string> | Yes | Member | Ordered array of member Auth UIDs in rotation. `FR5.2`, `E7.2` |
| `currentUid` | string | Yes | Member | Auth UID of member currently assigned. `FR5.2`, `E7.2` |
| `dueDate` | timestamp | Yes | Member | Next due timestamp. `FR5.2`, `E7.2` |
| `done` | boolean | Yes | Member | Marked done flag. `FR5.2`, `E7.2` |
| `createdAt` | timestamp | Yes | Member (on create) | Creation timestamp. `E7.2` |
| `updatedAt` | timestamp | Yes | Member | Last update timestamp. `E7.2` |

---

### 2.13 Collection: `contractChecks`
- **Purpose:** Private tenancy agreement upload metadata, AI summary, and clause risk flags.
- **Document ID:** `{checkId}` (auto-generated ID).
- **Used by:** Module 6 (FR6.2, FR6.3).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `checkId` | string | Yes | Owner (on create) | Unique contract check ID. `E8.3` |
| `uid` | string | Yes | Owner (on create) | Auth UID of uploader. Accessible strictly by owner (`uid == request.auth.uid`). `FR6.2`, `NFR2` |
| `filePath` | string | Yes | Owner | Firebase Storage relative path to PDF/image. `FR6.2`, `E8.3` |
| `summary` | string | Yes | Cloud Function | AI-generated plain-language summary. `FR6.2`, `E8.3` |
| `flags` | array<map> | Yes | Cloud Function | `[{ level: string ("red_flag"/"check"/"ok"), clause: string, text: string }]`. `FR6.2` |
| `createdAt` | timestamp | Yes | Owner (on create) | Upload timestamp. `E8.3` |
| `deleteAfter` | timestamp | Yes | Owner / Function | Scheduled auto-deletion timestamp (30 days from creation). `FR6.2`, `PRD Section 8` |

---

### 2.14 Collection: `chats`
- **Purpose:** Private messaging channels between connected users or Host/applicant pairs. (Addresses PRD Open Question 5: "A store for chat is still missing").
- **Document ID:** `{chatId}` (auto-generated ID or `"{uidA}_{uidB}"`).
- **Used by:** Module 3 (FR3.3), Module 6 (FR6.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `chatId` | string | Yes | Participant | Unique chat channel ID. `E5.2` |
| `participantUids` | array<string> | Yes | Participant | UIDs of the two chat participants. `FR3.3`, `E5.2` |
| `listingId` | string | No | Participant | Associated listing ID if chat originated from a room. `E5.2` |
| `matchId` | string | No | Participant | Associated match ID. `E5.2` |
| `createdAt` | timestamp | Yes | Participant | Channel creation timestamp. `E5.2` |
| `lastMessageAt` | timestamp | Yes | Participant | Timestamp of latest message. `E5.2` |

#### Subcollection: `chats/{chatId}/messages`
- **Purpose:** Chat messages and private file attachments (e.g. Host sharing tenancy agreement).

| Field Name | Type | Required | Who May Write | Notes |
|---|---|---|---|---|
| `messageId` | string | Yes | Sender (on create) | Unique message ID. `E5.2` |
| `senderUid` | string | Yes | Sender (on create) | Auth UID of sender. `E5.2` |
| `text` | string | Yes | Sender | Text content. `E5.2` |
| `attachmentType` | string | No | Sender | Type: `"contract"`, `"photo"`, `"none"`. `FR6.2` |
| `attachmentPath` | string | No | Sender | Firebase Storage path if attachment present. `FR6.2` |
| `sentAt` | timestamp | Yes | Sender (on create) | Sent timestamp. `E5.2` |

---

### 2.15 Collection: `config`
- **Purpose:** Read-only global system application configuration parameters.
- **Document ID:** `app` (single document in `config`).
- **Used by:** Module 2 (FR2.1, FR2.4), Module 4 (FR4.2).

| Field Name | Type | Required | Who May Write | Notes & FR / Story Reference |
|---|---|---|---|---|
| `roomPriceWeights` | map | Yes | Admin / Seed | `{ master: 1.3, medium: 1.0, small: 0.7 }`. Default price split weights. `ASSUMPTIONS #1` |
| `utilitiesEstimateRm` | number | Yes | Admin / Seed | Default utility estimate per person per month (RM 62 sample). `ASSUMPTIONS #7` |
| `updatedAt` | timestamp | Yes | Admin / Seed | Configuration update timestamp. |

---

### 2.16 Optional Collections

#### `reports`
- **Purpose:** User reporting mechanism for inappropriate listings, reviews, or chat messages.
- **Fields:** `reportId` (string), `reporterUid` (string), `targetType` (string: `"listing"`/`"review"`/`"user"`), `targetId` (string), `reason` (string), `createdAt` (timestamp), `resolved` (boolean).

#### `notifications`
- **Purpose:** In-app notification delivery log and FCM push history.
- **Fields:** `notificationId` (string), `recipientUid` (string), `title` (string), `body` (string), `type` (string), `data` (map), `read` (boolean), `createdAt` (timestamp).

---

## 3. Data Retention & Deletion Schedule

In compliance with the **Retention Principle of PDPA 2010** (personal data must not be retained longer than necessary for its purpose), automated cleanup schedules are specified below:

| Collection / Data | Retention Period | Deletion Mechanism | PDPA 2010 Justification |
|---|---|---|---|
| `contractChecks` | **30 days** | Scheduled Cloud Function / TTL | Analysis result is temporary. Tenancy agreements contain highly confidential terms; 30 days allows sufficient time for student review before permanent purge. |
| Unattached `unitEvaluations` | **90 days** | Scheduled Cloud Function | Evaluations not linked to an active Group expire after 90 days as market prices change and search intent ends. |
| Closed `listings` | **30 days** | Scheduled Cloud Function | Inactive/closed listings are purged 30 days after closing unless renewed by Host, preventing stale search results. |
| Closed `households` | **6 months** | Scheduled Cloud Function | Retained read-only for 6 months after household dissolution so former housemates can verify and settle lingering debt balances before complete purge. |
| `chats` & messages | Duration of Connection | Scheduled Cloud Function upon Connection termination | Chat channels are purged 30 days after a Connection is dissolved or closed by both parties. |

---

## 4. Document Conflicts & Ambiguities

During schema synthesis across `PRD.md`, `ASSUMPTIONS.md`, and `trello-cards.md`, the following conflicts were identified and resolved:

1. **Exact Unit Address Visibility vs. NFR2 Rule Statement:**
  - *Resolution:* Schema isolates exact address into `listings/{id}/private/location`, allowing access ONLY when `matches` status between requester and Host is `"connected"`.

2. **Profile Photo Visibility vs. Public User Document:**
  - *Resolution:* `photoUrl` is kept out of `users/{uid}` public document and exposed only via `users/{uid}/private/contact` or household context.

3. **`reviews` Primary Key (`listingId` vs. `buildingName`):**
  - *Resolution:* `reviews` uses `buildingName` (and `placeId`) as the primary key reference, with `listingId` marked optional.

4. **`unitEvaluations` Ownership (`groupId` vs. Individual Creator):**
  - *Resolution:* Added `createdByUid` as required, with `groupId` marked optional.

5. **Contract Check Shared Attachment Access:**
  - *Resolution:* Sharing in chat sends a new chat message attachment (`attachmentPath`). The applicant creates their *own* private `contractChecks` record referencing that file path.

---

## 5. Security Rules Enforceability & Cloud Function Requirements

Firestore Security Rules cannot execute multi-document aggregations, complex arithmetic across subcollections, or third-party API calls. The following logic **cannot** be enforced by rules alone and MUST be performed by Cloud Functions:

1. **Room Price Cap Rule Validation (FR2.1):**
  - *Function:* A Cloud Function validates or writes room collections atomically via transaction / callable function (`splitRoomPrices`).

2. **Deterministic Match Score & AI Reasons (FR3.2):**
  - *Function:* `computeMatchScores` Callable Function reads `matchProfiles`, calculates weighted score formula, calls Gemini API for reasons, and writes to `matches`.

3. **Automated Data Retention Cleanup (NFR2 & PDPA 2010):**
  - *Function:* Scheduled Cron Cloud Functions run daily to delete expired `contractChecks` (>30 days), unattached `unitEvaluations` (>90 days), closed `listings` (>30 days), and closed `households` (>6 months).

4. **Commute Calculation & Routes API Caching (FR4.1, FR4.2):**
  - *Function:* `getCommuteOptions` and `getTransitRoute` Callable Functions call Google Maps Routes API, calculate peak/off-peak fares and CO2 estimates, and return cached results.

5. **Atomic State Transitions:**
  - *Function:* Converting a `groups` + `unitEvaluations` into a live `households` record when a tenancy starts; issuing FCM push notifications on match requests or chore turn changes.
