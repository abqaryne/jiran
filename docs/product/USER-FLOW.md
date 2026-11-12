# Jiran: User Flows

Flowcharts of how a student moves through the app. Terms follow [GLOSSARY.md](GLOSSARY.md) and rules follow [PRD.md](PRD.md). The charts are Mermaid, so GitHub draws them directly.

Legend: rounded boxes are start or end points, diamonds are decisions, plain boxes are screens or actions.

## 1. Getting started

```mermaid
flowchart TD
    A(["Open app"]) --> B{"Has an account?"}
    B -- "No" --> C["Sign up with a UM student email"]
    C --> D["Open the verification link in the email"]
    D --> E["Build profile: faculty, Room budget, lifestyle tags, owns a car"]
    E --> F["Choose what stays hidden until a Connection"]
    F --> G["Home"]
    B -- "Yes" --> H["Log in"]
    H --> G
    G --> I["Rooms tab: find a room"]
    G --> J["Housemates tab: find people, create or join a Group"]
    G --> K["Unit tab: create or join a Household, then run it"]
    G --> L["Post button: list a room"]
```

## 2. Find a room and move in

```mermaid
flowchart TD
    A["Rooms tab: type a request in plain language or use filters"] --> B["Room cards: monthly Room rent; separate one-way commute time/fare"]
    B --> C["Room details: Host, rooms in the Unit, commute options card"]
    C --> D["See one-way time/fare and basic transit instructions; full route map and timeline in P1"]
    C --> S["Save the room"]
    D --> G["Send a Connection request to the Host with a short message"]
    G --> H{"Host accepts?"}
    H -- "No" --> I["Request ends"]
    I --> A
    H -- "Yes" --> J["Connection: chat opens, Unit address shown, phone only if shared"]
    J --> K["Arrange a viewing (+ menu in the chat)"]
    J --> CC["Host shares the agreement privately in the chat"]
    J --> UC["Upload a contract from the + menu in the chat"]
    UC --> CK
    CC --> CK["Optional: Contract check, only you can see it, clauses marked Red flag, Check or OK"]
    K --> L{"Take the room?"}
    CK --> L
    L -- "No" --> A
    L -- "Yes" --> M["Sign the tenancy outside the app"]
    M --> N["Host marks the Room occupied"]
    N --> O["You confirm your move-in"]
    O --> P(["You join the Household and the Unit tab unlocks"])
```

No timetable or campus-day count is needed. The origin is the Listing being considered; the destination is the profile faculty. Monthly commute totals and true monthly cost are removed (PRD FR4.1–4.3, Ary, 11 Nov 2026).

## 3. Host posts a Listing

```mermaid
flowchart TD
    A["Post button"] --> B["Add photos"]
    B --> C["Pick the building from place search"]
    C --> D["Enter Unit rent and deposit"]
    D --> E["Add each vacant Room: type and price, with a suggested split of 1.3 / 1.0 / 0.7"]
    E --> F{"Room prices add up to more than the Unit rent?"}
    F -- "Yes" --> G["Blocked: app shows how much is left to allocate"]
    G --> E
    F -- "No" --> H["Listing published: one Room card per vacant Room"]
    H --> I["Connection requests arrive"]
    I --> J{"Accept?"}
    J -- "Yes" --> K["Connection: chat and viewing"]
    J -- "No" --> L["Decline"]
    K --> M["Mark the Room occupied after the tenancy is signed"]
    M --> N{"Any vacant Room left?"}
    N -- "No" --> O(["Listing closes"])
    N -- "Yes" --> I
    H --> P{"30 days since posted or renewed?"}
    P -- "Yes, not renewed" --> O
```

## 4. Housemates: find people, create or join a Group, then a Household

```mermaid
flowchart TD
    A["Housemates tab"] --> B{"What do you want?"}
    B -- "Find people" --> C["Answer the compatibility questions: Matches with score and reasons"]
    C --> D["Send a Connection request"]
    D --> E{"Accepted?"}
    E -- "No" --> C
    E -- "Yes" --> F["Connection: chat opens"]
    B -- "Create a group" --> G["Name (optional) and maximum members, 2 to 8"]
    F -- "+ menu: Form a group" --> G
    G --> H["Invite: share a 6-character code or a link, or pick a Connection"]
    B -- "Join with a code" --> I["Enter the code or open the link: a join request"]
    I --> J{"Creator accepts?"}
    J -- "No" --> K(["Request declined"])
    J -- "Yes" --> L["Member of the Group"]
    H --> L
    L --> M["Pick a Target Unit from a Listing or an Evaluation"]
    M --> N["Agree a Room price split that equals the Unit rent exactly"]
    N --> O{"Everyone accepts?"}
    O -- "No" --> N
    O -- "Yes" --> P{"A member leaves before signing?"}
    P -- "Yes" --> Q["Acceptances reset: agree a new split or invite a replacement"]
    Q --> R{"Fewer than 2 members?"}
    R -- "Yes" --> S(["Group dissolves"])
    R -- "No" --> N
    P -- "No" --> T["Sign the tenancy outside the app: the signer becomes Host"]
    T --> U(["Group becomes the Household with the same members and prices"])
```

The Housemates tab holds everything about finding housemates. A student with no Group sees **Create a group** and **Join with a code** above the Match list; a student in a Group sees a "Your group" card (members, status, spots left, Open group) and the list below it is for filling the remaining spots; a full Group hides the list. A Group has two to eight members, set by its creator. A student is in one Group at a time, so "Form a group" becomes "Open group" in the chat + menu and the Unit tab. A join request needs the creator's acceptance; joining does not open contact details (those still need a Connection).

## 5. Found a Unit elsewhere: Evaluate unit

```mermaid
flowchart TD
    A["Find a Unit on another site, for example PropertyGuru"] --> B["Optional: Search on PropertyGuru button opens their site in the browser"]
    B --> A
    A --> C["Evaluate unit: building, Unit rent, number of rooms by type, optional deposit"]
    C --> D["App suggests a Room price split, members can edit it"]
    D --> E["Each member picks a Room"]
    E --> F["See each member's Room price and separate per-trip commute options"]
    F --> G{"Go ahead?"}
    G -- "No" --> H(["Evaluation is deleted after 90 days"])
    G -- "Yes" --> I["Attach the Evaluation to a Group as its Target Unit"]
    I --> J(["Continue in the Group flow"])
```

## 6. Living together: the Unit tab

```mermaid
flowchart TD
    A["Unit tab"] --> B["Money"]
    A --> C["Chores"]
    A --> D["House rules"]
    A --> E{"Someone moves out?"}
    B --> B1["Add an expense: who paid and who shares"]
    B1 --> B2["See who owes whom"]
    B2 --> B3["Settle up: mark as settled, no payment happens in the app"]
    C --> C1["Chores rotate and the person whose turn it is gets a reminder"]
    C1 --> C2["Mark done"]
    D --> D1["Add a rule"]
    D1 --> D2["Each member accepts it"]
    D2 --> D3["Changing a rule resets every acceptance"]
    E -- "Yes" --> F["The Room becomes vacant at its previous price"]
    F --> G["Unsettled balance stays owed, the leaver keeps read-only history"]
    G --> H{"Host leaving?"}
    H -- "Yes" --> I{"Another member accepts the Host role?"}
    I -- "No" --> J["Listing closes, Household stays"]
    I -- "Yes" --> K["Host role is handed over"]
    H -- "No" --> L["Host may post a new Listing for the vacant Room"]
    E -- "Last member leaves or tenancy ends" --> M(["Household is closed and read-only for 6 months, then deleted"])
```

### Starting a Household without a Listing

```mermaid
flowchart LR
    A["Unit tab, no Household"] --> B{"Choose"}
    B -- "I already live with people" --> C["Create a Household: Unit name, optional area"]
    C --> D["Invite code: 6 characters, valid 7 days"]
    D --> E["Housemates enter it under Join with a code"]
    B -- "I have an invite code" --> E
    E --> F(["The Unit tab unlocks for everyone who joined"])
    B -- "Rent a Unit together" --> G["Form a Group (flow 4)"]
```

## 7. What unlocks at each stage

```mermaid
flowchart LR
    A["Match<br/>initials, faculty, year, tags, score and reasons"] --> B["Connection request<br/>adds a short message"]
    B --> C["Connection<br/>chat, first name, Unit address with the Host, phone only if shared"]
    C --> D["Household<br/>expenses, chores, rules, profile photos"]
```

A Match on its own never unlocks anything. Users can hide any field at every stage in their privacy settings.

## One student's story (Mei Ling)

1. Signs up with her UM email and builds her profile (flow 1).
2. Searches "room under RM550, 30 min to FSKTM by train" and reads the Room cards (flow 2).
3. Opens a Room, sees monthly rent separately from one-way fares and how to travel by LRT plus shuttle, and checks the tenancy agreement (flow 2).
4. The Host accepts her Connection request, they view the room and she moves in (flow 2).
5. In the Unit tab the household splits rent by Room price, adds bills, rotates chores and agrees house rules (flow 6).
