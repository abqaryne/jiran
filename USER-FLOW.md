# Jiran: User Flows

Flowcharts of how a student moves through the app. Terms follow [GLOSSARY.md](GLOSSARY.md) and rules follow [PRD.md](PRD.md). The charts are Mermaid, so GitHub draws them directly.

Legend: rounded boxes are start or end points, diamonds are decisions, plain boxes are screens or actions.

## 1. Getting started

```mermaid
flowchart TD
    A(["Open app"]) --> B{"Has an account?"}
    B -- "No" --> C["Sign up with a UM student email"]
    C --> D["Open the verification link in the email"]
    D --> E["Build profile: faculty, class timetable, budget, lifestyle tags, owns a car"]
    E --> F["Choose what stays hidden until a Connection"]
    F --> G["Home"]
    B -- "Yes" --> H["Log in"]
    H --> G
    G --> I["Rooms tab: find a room"]
    G --> J["Match tab: find housemates"]
    G --> K["Unit tab: run your household"]
    G --> L["Post button: list a room"]
```

## 2. Find a room and move in

```mermaid
flowchart TD
    A["Rooms tab: type a request in plain language or use filters"] --> B["Room cards: price, travel time, true monthly cost"]
    B --> C["Room details: Host, rooms in the Unit, commute options card"]
    C --> D["Switch Peak / Off-peak and open the route steps"]
    C --> S["Save the room"]
    D --> G["Send a Connection request to the Host with a short message"]
    G --> H{"Host accepts?"}
    H -- "No" --> I["Request ends"]
    I --> A
    H -- "Yes" --> J["Connection: chat opens, Unit address shown, phone only if shared"]
    J --> K["Arrange a viewing"]
    J --> CC["Host shares the agreement privately in the chat"]
    CC --> CK["Optional: Contract check, only you can see it, clauses marked Red flag, Check or OK"]
    K --> L{"Take the room?"}
    CK --> L
    L -- "No" --> A
    L -- "Yes" --> M["Sign the tenancy outside the app"]
    M --> N["Host marks the Room occupied"]
    N --> O["You confirm your move-in"]
    O --> P(["You join the Household and the Unit tab unlocks"])
```

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

## 4. Housemates: Match, Group and Household

```mermaid
flowchart TD
    A["Match tab: answer the compatibility questionnaire"] --> B["Matches: score and reasons, days with overlapping classes"]
    B --> C["Send a Connection request"]
    C --> D{"Accepted?"}
    D -- "No" --> B
    D -- "Yes" --> E["Connection: chat opens"]
    E --> F{"Rent a whole Unit together?"}
    F -- "No" --> G(["Stay connected, for example one of you Hosts a Room"])
    F -- "Yes" --> H["Form a Group of 2 to 4 people"]
    H --> I["Pick a Target Unit from a Listing or an Evaluation"]
    I --> J["Agree a Room price split that equals the Unit rent exactly"]
    J --> K{"Everyone accepts?"}
    K -- "No" --> J
    K -- "Yes" --> L{"A member leaves before signing?"}
    L -- "Yes" --> M["Acceptances reset: agree a new split or invite a replacement"]
    M --> N{"Fewer than 2 members?"}
    N -- "Yes" --> O(["Group dissolves"])
    N -- "No" --> J
    L -- "No" --> P["Sign the tenancy outside the app: the signer becomes Host"]
    P --> Q(["Group becomes the Household with the same members and prices"])
```

## 5. Found a Unit elsewhere: Evaluate unit

```mermaid
flowchart TD
    A["Find a Unit on another site, for example PropertyGuru"] --> B["Optional: Search on PropertyGuru button opens their site in the browser"]
    B --> A
    A --> C["Evaluate unit: building, Unit rent, number of rooms by type, optional deposit"]
    C --> D["App suggests a Room price split, members can edit it"]
    D --> E["Each member picks a Room"]
    E --> F["See each member's true monthly cost and commute options card"]
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

## 7. What unlocks at each stage

```mermaid
flowchart LR
    A["Match<br/>initials, faculty, year, tags, score and reasons, overlapping class days"] --> B["Connection request<br/>adds a short message"]
    B --> C["Connection<br/>chat, first name, Unit address with the Host, phone only if shared"]
    C --> D["Household<br/>timetables, expenses, chores, rules, profile photos"]
```

A Match on its own never unlocks anything. Users can hide any field at every stage in their privacy settings.

## One student's story (Mei Ling)

1. Signs up with her UM email and fills in her timetable (flow 1).
2. Searches "room under RM550, 30 min to FSKTM by train" and reads the Room cards (flow 2).
3. Opens a Room, sees the true monthly cost and the LRT plus shuttle route, and checks the tenancy agreement (flow 2).
4. The Host accepts her Connection request, they view the room and she moves in (flow 2).
5. In the Unit tab the household splits rent by Room price, adds bills, rotates chores and agrees house rules (flow 6).
