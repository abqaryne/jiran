# Jiran: Design System

How Jiran looks and behaves. This is the single guide for everyone who builds a screen: read it once, then use the components. The product rules are in [PRD.md](../product/PRD.md); the words we use are in [GLOSSARY.md](../product/GLOSSARY.md); the flows are in [USER-FLOW.md](../product/USER-FLOW.md).

**Owner:** Ary (Lead UI/UX). Changes to the design system go through Ary. Everyone else uses it and asks for what is missing.

| What | Where |
|---|---|
| Figma file (design system and all screens) | https://www.figma.com/design/kjYgovwDJBU9ArWSfMa3OD/Jiran |
| Android tokens, styles and example layouts | [android/app/src/main/res/](../../android/app/src/main/res/) |
| This guide | `docs/design/DESIGN.md` |

Contents: [1 Principles](#1-principles) · [2 Using the Figma file](#2-using-the-figma-file) · [3 Foundations](#3-foundations) · [4 Components](#4-components) · [5 Patterns](#5-patterns) · [6 Content](#6-content-and-language) · [7 Accessibility](#7-accessibility) · [8 Android guide](#8-android-implementation) · [9 Screen inventory](#9-screen-inventory) · [10 Adding or changing things](#10-adding-or-changing-things) · [11 Known gaps](#11-known-gaps)

---

## 1. Principles

Style reference: **Nextdoor**. White screens, thin dividers, people and places up front, one blue.

1. **Neighbourly, not flashy.** White screens, plain words, friendly questions. Nothing competes with the room and the people.
2. **Blue means go.** One blue action per screen (Continue, Connect, Post). Grey pills for small actions, ink (black) for strong secondary actions.
3. **People and places first.** Every listing shows who posted it, where it is, and how far it is from your faculty.
4. **Readable by everyone.** Text at 4.5:1 or more, 48 dp touch targets, status always with a word and an icon, English and Malay.
5. **Honest numbers.** Estimates are labelled estimates. AI output is labelled AI-generated. Scores come from code, never from the AI.

## 2. Using the Figma file

The file has three pages.

| Page | What is in it |
|---|---|
| 🎨 Design System | Foundations board, Core components board, Housing components board, and the **master components** (far below the boards, grouped together). |
| 📱 UI Design | Every screen, grouped in **15 flows** that sit inside **6 journey sections** (1 Get started, 2 Find a room and move in, 3 Post a room (Host), 4 Find housemates and rent together, 5 Live together, 6 Profile and safety), read top to bottom. Each flow has a gradient header, a **START** screen on the left that shows where the user comes from, and then the flow's screens in order. A **Journey map** frame at the top shows how the flows connect. A last section, References, keeps the supplied layout screenshot. Rearranged on 12 Nov 2026 (positions only, no screen was redesigned). |
| 🖼 Thumbnail | Cover. |

**For designers and front-end developers**

- Build screens from **instances** of the master components (Assets panel, search the name). Never draw a button by hand.
- Change a component with its **properties** (Label, Style, Size, Selected, State, Icon), not by detaching it.
- Colours, spacing and radius are **Figma variables**; text uses **text styles**; shadows use **effect styles**. Pick them from the lists, never type a hex or a number.
- In **Dev Mode** every variable shows its Android name (for example `@color/color_brand_primary`, `@dimen/spacing_16`). Those names exist in [android/app/src/main/res/values](../../android/app/src/main/res/values) exactly as shown.
- A component's Figma name is its Android style name (table in [section 4](#4-components)).
- Screens are 390 × 844. Long screens (Room details, Public transport route) are taller on purpose; they scroll.
- The status bar and notch on mockups are **phone chrome for the picture**, not an app component. On Android the system draws the real one (see [section 8](#8-android-implementation)).

**Sharing the library with the team.** If the Figma plan allows it, Ary publishes the master components as a team library (Assets panel → Libraries → Publish) so other files can use them. If not, share this file with *can view* and teammates copy components and paste them into their own file; variables and styles come along with the paste.

## 3. Foundations

### 3.1 Colour

Brand blue is **#065BC3** (`brand/600`). There is no secondary or accent colour. Greys give structure. Status colours carry meaning only.

**Always use the semantic token, never the primitive.** Primitives exist so that the semantic layer can point at them.

| Semantic token (Figma) | Android | Value | Use |
|---|---|---|---|
| `color/brand/primary` | `@color/color_brand_primary` | #065BC3 | Main action, links, selected progress |
| `color/brand/pressed` | `@color/color_brand_pressed` | #054A9E | Pressed state of the main action |
| `color/brand/tint` | `@color/color_brand_tint` | #EEF5FE | Info banners, hero blocks, brand badge |
| `color/brand/tint-strong` | `@color/color_brand_tint_strong` | #D9E8FC | Avatar circle |
| `color/brand/on-tint` | `@color/color_brand_on_tint` | #054A9E | Text and icons on the tints |
| `color/text/primary` | `@color/color_text_primary` | #0B0B0C | Body and headings |
| `color/text/secondary` | `@color/color_text_secondary` | #5B5F66 | Meta text, hints |
| `color/text/on-brand` | `@color/color_text_on_brand` | #FFFFFF | Text on blue or ink |
| `color/border/strong` | `@color/color_border_strong` | #868B94 | Input outline (3.4:1), chip border |
| `color/border/divider` | `@color/color_border_divider` | #E6E7EA | Lines between rows, card edges |
| `color/bg/surface` | `@color/color_bg_surface` | #FFFFFF | Screens, cards |
| `color/bg/ground` | `@color/color_bg_ground` | #F3F4F6 | Grouped sections (Household) |
| `color/bg/fill` | `@color/color_bg_fill` | #EDEEF0 | Soft buttons, icon circles, disabled |
| `color/bg/ink` | `@color/color_bg_ink` | #0B0B0C | Ink buttons, toasts, selected chip border |

Status pairs (text on background), used only in badges, banners and toasts:

| Status | Text | Background | Word to show |
|---|---|---|---|
| Success | `color_status_success_text` #0E7A3F | `color_status_success_bg` #E3F4EA | OK, Settled, Accepted |
| Warning | `color_status_warning_text` #8A5A00 | `color_status_warning_bg` #FFF1D6 | Check, Pending, Needs changes |
| Danger | `color_status_danger_text` #B42318 | `color_status_danger_bg` #FDE7E5 | Red flag, Declined, error |

The brand scale (`brand/50` to `brand/900`) is in `android/app/src/main/res/values/colors.xml`. Use `brand/300` and `brand/100` only for the allocation bar and chart-like fills.

**Rules**

- One blue action per screen. If you need a second, make it Soft (grey) or Ink.
- Never use colour alone to say something. Red flag needs the word "Red flag" and a flag icon.
- Do not add colours. If you think you need one, ask Ary.

### 3.2 Typography

**Geist.** Semibold is the heaviest weight (no bold). Slightly tight headings, plain regular body. Sizes in **sp**.

| Style (Figma) | Android | Weight | Size / line | Tracking | Use |
|---|---|---|---|---|---|
| Text/Display | `TextAppearance.Jiran.Display` | SemiBold | 32 / 38 | −3% | Welcome, big numbers |
| Text/Headline | `TextAppearance.Jiran.Headline` | SemiBold | 26 / 32 | −2.5% | Question titles |
| Text/Section | `TextAppearance.Jiran.Section` | SemiBold | 22 / 28 | −2% | "Faves", "Groups", sheet titles |
| Text/Title | `TextAppearance.Jiran.Title` | SemiBold | 17 / 22 | 0 | Top bar title, names |
| Text/Body | `TextAppearance.Jiran.Body` | Regular | 16 / 24 | 0 | Paragraphs, message text |
| Text/Label | `TextAppearance.Jiran.Label` | SemiBold | 16 / 20 | 0 | Buttons, list titles |
| Text/Label Small | `TextAppearance.Jiran.LabelSmall` | SemiBold | 14 / 20 | 0 | Small buttons, chips |
| Text/Meta | `TextAppearance.Jiran.Meta` | Regular | 14 / 20 | 0 | "2d · 3.1 km from FSKTM" |
| Text/Caption | `TextAppearance.Jiran.Caption` | Medium | 12 / 16 | 0 | Tab labels, badges |

Nothing smaller than 12 sp.

### 3.3 Spacing, radius, lines, elevation

| Token | Values |
|---|---|
| Spacing (`spacing/*`, `@dimen/spacing_*`) | 4 · 8 · 12 · 16 · 20 · 24 · 32 · 48 dp. **16 dp** is the screen margin; **24 dp** between sections; 48 dp is the touch target. |
| Radius (`radius/*`, `@dimen/radius_*`) | **8** photos and chips · **12** inputs, cards, banners · **16** sheets, commute option, hero · **pill** buttons, search, tab bar, avatars |
| Lines | 1 dp `border/divider` between rows and on card edges. 2 dp ink for selected chip, focused field. |
| Elevation | Lines before shadows. Only the **floating tab bar** and **Post button** have a shadow (`Shadow/Floating`: y 6, blur 20, ink 14%). `Shadow/Raised` (y 2, blur 8, ink 6%) is for a sheet handle area or a raised card, rarely. |

Layout: 16 dp side margin, 24 dp between sections, 12 dp inside a card between lines, 8 dp between a label and its field.

### 3.4 Icons

**Material Symbols Rounded**, weight 400, optical size 24, **outlined** by default. Filled only for the selected or active state (for example the active tab). Sizes: 24 dp standard, 18 dp inside badges and banners, 22 dp inside circles.

The icons we use are already in the project as vector drawables, named `ic_<symbol>` (for example `ic_home`, `ic_bed`, `ic_arrow_back`) in `android/app/src/main/res/drawable`. The four tab icons also have `_filled` versions and a selector (`ic_tab_home`, ...). Tint them in the layout (`app:tint`), never by editing the file. Need another icon? Add the Material Symbols Rounded vector from [google/material-design-icons](https://github.com/google/material-design-icons/tree/master/symbols/android) (weight 400, 24 dp) with the same `ic_<symbol>` name. Licence: Apache 2.0 (see `docs/licenses`). The names we use:

| Where | Symbols |
|---|---|
| Tabs | `home`, `bed`, `group`, `door_front`, `add` (Post) |
| Commute | `directions_walk`, `train`, `directions_bus`, `local_taxi`, `directions_car`, `swap_horiz` |
| Status | `check`, `error`, `flag`, `schedule`, `close`, `auto_awesome` (AI), `verified_user` |
| Listing | `bed`, `door_open`, `apartment`, `bookmark`, `share`, `location_on`, `image` |
| Household | `receipt_long`, `cleaning_services`, `gavel`, `delete`, `home`, `bolt` |

### 3.5 Logo

A **door with a smile** (Jiran means neighbour: your neighbour's door), white on `brand/600`, in a rounded square. The master is the frame named `logo` on the Thumbnail page of the Figma file.

| Use | File |
|---|---|
| In screens (splash, welcome, about) | `drawable/ic_logo.xml` (vector, 96 dp, exported from Figma) |
| Launcher icon | adaptive icon: `mipmap-anydpi/ic_launcher.xml` = blue background + `drawable/ic_launcher_foreground.xml` (door and smile in the 66 dp safe zone) + `drawable/ic_launcher_monochrome.xml` (themed icon) |

Keep clear space of at least a quarter of the logo width around it, never stretch it, and never place it on a background that is not white or brand blue. To change the logo, re-export the `logo` frame from Figma as SVG and update the three vector files; do not redraw it by hand.

## 4. Components

Master components live in Figma → 🎨 Design System. Each row: Figma component, its properties, the Android style or layout, and when to use it.

| Component (Figma) | Properties | Size | Android | Use it for |
|---|---|---|---|---|
| **Button** | Style: Primary, Ink, Soft, Disabled · Size: Large, Small · Label | Large 52 dp, Small 40 dp, pill | `Widget.Jiran.Button.Primary / .Ink / .Soft` (+ `.Small`). Disabled is just `enabled=false`. | Primary: the one forward action, full width at the bottom of a step. Ink: strong secondary (Edit profile, Remind). Soft: quiet actions. Small: top bar and inline. |
| **Icon button** | Style: Soft, Outline, Ink, Brand · Icon | 40 dp circle | `Widget.Jiran.IconButton.Soft / .Outline / .Ink / .Brand` | Back, close, share, save, filter. Give it a content description. |
| **Chip** | Selected: False, True · Chevron · Label | 36 dp high, 8 dp radius | `Widget.Jiran.Chip.Filter` | Filters, segmented choices, tags. Chevron = opens a sheet. Selected = ink border. |
| **Badge** | Status: Success, Warning, Danger, Brand, Neutral | 26 dp high, 8 dp radius | `BadgeView` (`app:badgeStatus`, `app:badgeIcon`); style `Widget.Jiran.Badge.*` for plain TextViews | One word plus an icon: OK, Check, Red flag, Vacant, AI-generated. |
| **Avatar** | Size: Small 32, Medium 40, Large 56 · Initial | circle | `AvatarView` (`app:avatarSize`, `app:avatarInitial`) | Initials on soft blue. Real photos only once people live together. |
| **Field** | State: Default, Focused, Error · Label · Value | 60 dp high, 12 dp radius | `Widget.Jiran.TextInputLayout` | Any text input. Label sits inside the box. |
| **Search bar** | Placeholder | 44 dp, pill | `SearchBarView` (`app:searchHint`, `app:searchAi`) | Rooms search (also takes plain-language requests; the sparkle icon marks AI). |
| **Tab item / Tab bar / Post button** | State: Active, Inactive | bar 60 dp, Post 60 dp | `JiranTabBar` (`setSelectedTab`, `setOnTabListener`) | Main navigation: Home, Rooms, Housemates, Unit + Post. |
| **Room card** | Monthly Room price · Building name (title) · Area (meta) · One-way time and distance to the faculty (commute) · "Listed 3d ago" · room type badge on the photo | 2 per row in search | `RoomCardView` (`setPrice`, `setTitle`, `setMeta`, `setCommute`, `getPhotoView`) | One card per vacant Room. Rent and one-way commute estimate stay separate. `roomCommute` / `setCommute` shows one-way time/fare; no monthly total. **No CO₂ here.** |
| **Section header** | Title · Action | 28 dp | `SectionHeaderView` (`app:headerTitle`, `app:headerAction`) | Heading of a list or carousel. |
| **List row** | Icon · Title · Subtitle · Trailing | min 64 dp | `ListRowView` (`app:rowIcon` or `app:rowAvatarInitial`, `rowShowChevron`, `rowShowDivider`) | Settings, expenses, steps, requests. Divider under every row but the last. |
| **Commute option** | Selected: False, True · Icon · Mode · Note · Time · Cost | 72 dp, 16 dp radius | `CommuteOptionView`, call `setSelected(true)` on the highlighted one | One travel mode with one-way time/fare. The lowest known available fare is Selected (blue border + blue note). |
| **Status bar** (mockups only) | | 28 / 44 | none | Picture only. Do not build it. |

Components made from several parts (use the same pieces, then compose):

| Pattern | Built from | Notes |
|---|---|---|
| **Top bar** | Icon button + Title + Small Ink button or brand text | 60 dp, [`component_top_bar.xml`](../../android/app/src/main/res/layout/component_top_bar.xml). Back on the left, one action on the right. |
| **Footer** | Top border `border/divider` + 1 to 2 Buttons (Large) | 16 dp padding, 24 dp at the bottom. Primary first, Soft second. |
| **Info banner** | `InfoBannerView` (`app:bannerKind`, `app:bannerText`) | Blue = info. Success, warning, danger use the status pairs. |
| **Bottom sheet** | `Widget.Jiran.BottomSheet` (16 dp top radius, handle 40×4) | Title in Section style, close top right, one Primary button at the bottom. |
| **Toast** | `JiranSnackbar.make(view, text)`: ink pill, white text, optional "Undo" | Near the bottom, above the tab bar. |
| **Empty state** | `EmptyStateView`: icon + one bold sentence + one line of help | "No chats yet". |
| **Timeline** (route) | Time column + dot and line + content | Dot is a ring for walking, blue for LRT/bus, ink for shuttle. Used in Public transport route. |
| **Segment bar** | Rounded track with `brand/600`, `brand/300`, `brand/100` | Room price allocation: total must not pass the Unit rent. |

## 5. Patterns

**Navigation.** A floating white pill with four tabs: **Home · Rooms · Housemates · Unit**, and a blue **Post** button beside it. There is no Ask tab: plain-language AI search lives in the Rooms search bar. Profile opens from the avatar on Home.

**Starting a flow.** Every flow in Figma shows its entry point (START screen, tapped control ringed in black). Build navigation so the same control opens the flow.

**Estimates and AI.**
- Commute costs, e-hailing fares and CO₂ are estimates: say so on the screen and name the source.
- Every AI output carries the **AI-generated** badge. The contract check also says "Not legal advice" and has "Report a wrong result".
- AI search shows the filters it understood **before** results load, and the user can edit them. If AI is down, show the manual filters and a "Try again".
- The match score and CO₂ are computed by code. The AI only writes the reasons.

**Contracts are private.** A tenancy agreement never appears on a listing, Room card or room details page, and the Room details share row has no contract action. It reaches the student in a Connection chat as a private file, or the student uploads their own copy from the chat's + menu or from Profile. Only the uploader sees the file and the result, which can be deleted at once and are removed after 30 days.

**Privacy by stage.** Match: initials, faculty, year, tags. Connection: first name, exact Unit (block, floor, door number) with the Host, phone only if shared. Household: timetables, expenses, photos. Show clearly what the other side will see before a user sends a request.

**Forms.** Label inside the field. Error: red outline, red 12 sp message with an icon, never only colour. Primary button at the bottom; disabled until valid. Money is always `RM 1,500` with a comma and a space; per month is `/mo`.

**Selection.** Single choice: radio rows (blue filled circle = selected) or chips. Multiple choice: checkbox rows. On/off settings: switch rows.

**Sheets vs pages.** Quick choices (filters, send a request, report, mark occupied) are bottom sheets. Anything with a map, a timeline or several sections is a full page (Public transport route).

**Loading and failure.** Skeleton blocks in the shape of the content; failures say what happened and what to do next ("AI search is not available right now. Pick the filters yourself or try again.").

## 6. Content and language

- English first, Malay supported. Keep sentences short and friendly. Say "you", not "the user".
- Names: **Room**, **Unit**, **Host**, **Match**, **Connection**, **Group**, **Household**, **Commute per trip** (see the glossary; "true monthly cost" was removed on 11 Nov 2026). Do not invent synonyms.
- Times are written in 24-hour style without AM or PM: `9:18` for a departure, `14:00` for a reference departure; durations `34 min`; distance `450 m`.
- Buttons are verbs: Continue, Connect, Send request, Mark done. Not "OK" or "Submit".
- Placeholder people and places in mockups use UM-realistic examples (Mei Ling, Rina, Aisyah, Pantai Dalam, Kerinchi, FSKTM). The app is for **all UM faculties**: show the user's own faculty, never hard-code FSKTM.

## 7. Accessibility

- Text contrast 4.5:1 or more; the input outline `#868B94` is 3.4:1 against white (non-text, passes 3:1).
- Touch targets 48 dp at least. A 36 dp chip or 40 dp button still gets a 48 dp tap area (`ensureMinTouchTargetSize`, or padding).
- Text in sp; test at 200% font size. Do not fix heights of text containers.
- Every icon-only button has a `contentDescription`. Decorative icons use `@null`.
- Status is a word plus an icon plus colour. Focus and selection use a border, not only colour.
- Do not rely on gestures only: every swipe action has a visible button.

## 8. Android implementation

Stack: Java, XML layouts, Material Components, MVVM. The design system is part of the app module: tokens, styles and component layouts live in [`android/app/src/main/res`](../../android/app/src/main/res). Open the project in Android Studio and use them; there is nothing to copy.

```
android/app/src/main/res/
  values/colors.xml            colour tokens (primitives + semantic)
  values/dimens.xml            spacing, radius, component sizes, elevation
  values/type.xml              TextAppearance.Jiran.*
  values/themes.xml            Theme.Jiran, shape appearances, default styles
  values/styles_components.xml Widget.Jiran.* (button, chip, badge, avatar, field, card, ...)
  values/strings*.xml          all user-facing text
  color/                       state lists (button, chip, field, tab, switch)
  drawable/bg_*.xml            badge, avatar, icon circle, tab bar, divider, commute option backgrounds
  layout/component_*.xml       Room card, List row, Commute option, Tab bar, Top bar
  font/                        Geist Regular, Medium, SemiBold (SIL Open Font License, see docs/licenses)
  navigation/nav_main.xml      main navigation graph (used by ShellActivity)
```

**Already set up**

- `Theme.Jiran` is applied to the product shell (`ui/shell/ShellActivity`). Product screens use it; the temporary foundation screens keep their old theme until they are replaced.
- Geist is bundled. `minSdk` is 26, so `android:lineHeight` in `type.xml` is ignored on Android 8 (API 26 and 27) and works from Android 9; set `app:lineHeight` on a TextView if that matters for a screen.
- Material Components 1.14, so `titleCentered` and the other attributes are available.
- Icons are bundled (about 75 Material Symbols, see section 3.4). The shell tab bar and Post button already use them.
- Add system bar insets as padding on the top bar and the footer of each screen (the shell already calls `EdgeToEdge.enable`).

**See every component at once.** In a debug build a second launcher icon called **Jiran UI** opens the component gallery (`ui/gallery/ComponentGalleryActivity`, debug source set only). It shows typography, buttons, chips, badges, avatars, fields, banners, list rows, progress, commute options, room cards, the empty state and the tab bar with real data, so a screen can be compared with the Figma frame. When you add a component, add it to the gallery (`src/debug/res/layout/activity_component_gallery.xml`).

**Use the components**

```xml
<!-- Primary button, full width, bottom of a step -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Jiran.Button.Primary"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="@string/continue_label" />

<!-- Soft small button -->
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Jiran.Button.Soft.Small"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/mark_done" />

<!-- Status badge: a word and an icon -->
<TextView
    style="@style/Widget.Jiran.Badge.Danger"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:drawableStart="@drawable/ic_flag"
    android:text="@string/red_flag" />

<!-- Avatar: layout size must equal the avatar dimen -->
<TextView
    style="@style/Widget.Jiran.Avatar.Medium"
    android:layout_width="@dimen/avatar_medium"
    android:layout_height="@dimen/avatar_medium"
    android:text="M" />

<!-- Field -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.Jiran.TextInputLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/um_email">
    <com.google.android.material.textfield.TextInputEditText
        style="@style/Widget.Jiran.TextInputEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress" />
</com.google.android.material.textfield.TextInputLayout>
```

Because `Theme.Jiran` sets the default `materialButtonStyle`, `chipStyle`, `textInputStyle`, `materialCardViewStyle` and `toolbarStyle`, a plain widget already looks right; the explicit `style=` is for the variants.

**Rules for code**

- No hard-coded colours, sizes, or fonts in layouts. Use `@color/color_*`, `@dimen/*`, `TextAppearance.Jiran.*`.
- One style per variant. If a screen needs something the styles do not cover, ask Ary before adding a local override.
- Use `dp` for sizes and `sp` for text only.
- RecyclerView rows reuse `component_list_row.xml` and `component_room_card.xml`; do not copy and edit them per screen.

**Profile faculty picker (Ary, 11 Nov 2026).** Always keep Popular chips visible as quick choices, including while typing or after selection. Typed queries show matching full faculty names in a dropdown under the field, matching both names and abbreviations; no Results chip section. Dropdown rows wrap and have a minimum 48 dp height. Figma update not verified.

**Where Android differs from the Figma picture** (accepted, not bugs)

| Figma | Android |
|---|---|
| Field label inside the box | Material outlined fields put the hint on the border. Matches the colours, radius and states; if the team wants the exact look we build a custom view later. |
| Selected chip has a 2 dp ink border | Chip stroke width cannot change by state; we use 1.5 dp for both and the ink colour shows selection. |
| Status bar and notch | Drawn by the phone. |

## 9. Screen inventory

The Figma page 📱 UI Design is grouped by flow. P0/P1 comes from the [PRD](../product/PRD.md).

| Flow | Starts from | Screens | FR |
|---|---|---|---|
| Core Tabs | After profile setup | Home · Rooms · Housemates (start) · Unit (commute to the faculty lives here once the student has a home; not on Home). The Unit tab with no Household is the start of the Household Setup Flow | nav |
| Starting Flow | Opening the app | Welcome · Sign up · Verify email · Log in · Reset password | FR1.1, 1.2 |
| Profile Setup Flow | Verification link opened | Faculty, year and gender · Budget and lifestyle · Privacy · Done (three steps, no timetable or campus days) | FR1.3 |
| Find a Room Flow | Home → Rooms tab | Plain-language search · Filters · **No results** (empty state) · AI unavailable · Saved rooms | FR2.2, 6.1 |
| Room Details Flow | Tap a room card | Room details (with the "Getting to [faculty]" card) · **Public transport route** (opens when the student taps the "Public transport" row; the other three modes do not open anything) · Send request · Report listing | FR4.1 to 4.3, 3.3 |
| Request and Chat Flow | Message the Host from a room | Messages (Requests) · **Messages (Chats)** · Chat (with the private agreement card and a + button) · **Add to this chat** (drawer) · **Arrange a viewing** (sheet) · **Pick a date** · **Pick a time** · Confirm move-in | FR3.3, 5.1 |
| Contract Check Flow | The + menu in a Connection chat, the agreement the Host shares there, or Profile | Contract upload · Contract result | FR6.2, 6.3 |
| Host Listing Flow | Tap Post | New listing · Pick building · Rooms and prices · My listing · Request (host view) · Mark room occupied | FR2.1 |
| Match Flow | Housemates tab → Start questions | Housemates (no Group: Create a group and Join with a code above the list) · Questions · Match results · **Housemates · Your group** (results with a Group card and an "In a group" badge) · Match profile · Connect request | FR3.1 to 3.3 |
| Evaluate Unit Flow (P1) | In a Group, choose the Target Unit | Pick a Unit · Evaluate form · Evaluate result (with a **Compare Units** row) · Compare Units (opened from the Group screen or an Evaluation result) · **Add another Unit** (the Units in the comparison, at most 3, with Remove; Evaluate a Unit; Pick a Listing) | FR2.3, 3.3 |
| Group Flow (P1) | The Housemates tab (Create a group or Join with a code), the + menu in a Connection chat, or the "Rent a Unit together" card in the Unit tab with no Household | **Create a group** (name, maximum members 2 to 8) · Group (members, spots left, join requests, Target Unit) · **Invite to the group** (code, link, or a Connection) · **Join a group with a code** · Split options · Before you sign · Choose Host | FR3.3 |
| Join a Group Flow (P1) | The Housemates tab, "Join with a code" (or a link opened from a friend) | **Join a group with a code** (6-character code, the creator accepts) · **Request sent** (waiting, no contact details shared) | FR3.3 |
| Household Setup Flow | The Unit tab with no Household | Unit · No Household · **Create a Household** · **Invite housemates** (code) · **Join with a code** | FR5.1 |
| Household Flow | Confirm your move-in, or joining with a code | Welcome · Unit · Add expense · Balances · Chores (P1) · House rules (P1) · **Household settings** (gear in the Unit tab: name, code, members, move out) · Move out | FR5.1 to 5.3 |
| Profile and Safety Flow | Avatar on Home | Profile (with Contract check) · Privacy · Notifications | FR1.3 |

Chat actions (Ary, 11 Nov 2026): the chat composer has a **+** button that opens a bottom drawer, "Add to this chat", with three actions: **Arrange a viewing** (a sheet with a Date field and a Time field that open a date picker and a time picker, no fixed time slots; past dates are disabled and time is 24-hour; sending posts the viewing card), **Upload a contract** (opens the private Contract check upload; only the student sees the result) and **Form a group** (opens Create a group with that Connection already invited; "Open group" if the student already has one). These actions are never pushed into the thread as prompt cards; the viewing card and the agreement card appear only as the result of an action or a shared file.

Room details commute (Ary, 11 Nov 2026; supplied screenshot is a layout reference): retain Room rent and deposit, remove the true-monthly-cost headline and combined rent/utilities/commute line. Keep “Getting to [faculty]”; rows show duration and `RM … / trip` (one way), with optional sourced CO₂ per trip. Basic transit instructions are P0. Label approximate origins and estimates; no schedule or campus-day prompt. Peak / Off-peak switches the same rows in P1; the full map/timeline page then shows reference departure, supported legs, transfers and one-way time/fare. No class arrival eligibility, monthly commute amount or live Grab-price claim. Highlighting must include free walking or explicitly say “Cheapest paid transport”. See [PRD Module 4](../product/PRD.md#module-4-apartment-to-faculty-commute-guide) and [file handoff](../plans/2026-11-11-commute-per-trip.md). Figma not updated in this docs change.

## 10. Adding or changing things

1. **Check first.** Is there already a component or pattern? Search Figma Assets and this guide.
2. **Ask Ary** before adding a colour, a type size, a new component, or a new variant.
3. **Design in Figma first** with instances and variables, then build. New components are built as a Figma component with properties, named in Title Case (`Room card`, not `roomCardV2`).
4. **Name the Android style the same** as the Figma component (`Widget.Jiran.<Component>.<Variant>`), and add its row to the table in section 4.
5. **Token changes:** change the Figma variable first, then the matching `@color/` or `@dimen/` value in the same PR. The names must stay identical in both places.
6. **PR checklist for UI work:** uses only tokens and styles · 48 dp touch targets · text in sp · status has a word and an icon · estimate and AI labels present · matches the Figma frame at 390 dp wide · screenshot attached. Ary does the final check and merge, as in [TEAM_PLAN.md](../team/TEAM_PLAN.md).

## 11. Known gaps

- **Light mode only.** There is a `Light` mode in the colour collection, ready for a `Dark` mode later.
- **Malay copy** is not drawn yet; screens use English.
- **Logo** is a placeholder until the door mark is finished.
- **Illustrations and photos** are grey placeholders; empty-state drawings are still to do.
- **Charts** (impact dashboard, P2) are not designed.
- **Android files have not been compiled yet.** They are written against Material Components 1.4+; if Android Studio flags an attribute, fix it in `android/app/src/main/res` and tell Ary so DESIGN.md stays right.
- Shuttle times, fares and CO₂ factors on the commute screens are example numbers until the owners in [ASSUMPTIONS.md](../product/ASSUMPTIONS.md) confirm them.
