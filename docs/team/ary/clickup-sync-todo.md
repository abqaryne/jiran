# ClickUp sync: left for the next day

The ClickUp connector stopped at its daily limit (100 calls) on 10 Nov 2026. These changes bring ClickUp in line with the task files; run them after the limit resets, then delete this file. IDs are ClickUp task IDs.

- status z8qgvc93jj → in progress (T4.2.1)
- status z8qgvc93jt → in progress (T4.1.5)
- status z8qgvc93kk → in progress (T2.3.1)
- status z8qgvc93m5 → in progress (T2.2.1)
- status z8qgvc93mg → in progress (T1.3.3)
- status z8qgvc93mh → in progress (T1.3.4)
- status z8qgvc93mq → in progress (E2.3.3)
- status z8qgvc93n0 → done (E3.3.3)
- status z8qgvc93q7 → in progress (E3.4.1)
- status z8qgvc93wy → in progress (E4.2.1)
- status z8qgvc93x3 → done (E4.3.2)
- status z8qgvc93xh → done (E5.1.2)
- status z8qgvc93xp → in progress (E5.2.2)
- status z8qgvc93z2 → in progress (E6.1.2)
- status z8qgvc93zk → in progress (T1.1.1)
- status z8qgvc9410 → in progress (E6.2.1)
- status z8qgvc94fu → in progress (E7.2.1)
- status z8qgvc94q0 → in progress (E8.3.2)
- status z8qgvc94qg → in progress (E9.1.1)
- status z8qgvc94r3 (E9.4.1) → do NOT set in progress: E9.4 is dropped (11 Nov 2026); close E9.4 and its subtasks
- status z8qgvc93h9 → done (manual)
- status z8qgvc93j3 → done (manual)
- status z8qgvc94qm → in progress (manual)
- rename z8qgvc93q8 → (Gathan) Room card shows monthly Room rent and a separate one-way commute time and fare
- rename z8qgvc93qf → (Ilham) Function that marks each clause Red flag, Check or OK
- rename z8qgvc93qg → (Ilham) Write 10 fictional tenancy agreements from common clauses and list the risky clauses of each (Aly checks)
- rename z8qgvc94p2 → (Ilham) Set up the Gemini API free tier in the Worker and record its quota and data-use terms (#10)
- create under E0.2 (z8qgvc906d): (Aaron) Deploy the Worker on the free plan as jiran-api and write its URL in the task file [backlog]
- create under E1.1 (z8qgvc906h): (Ary, Aly) Test on a real device with a real inbox before the video [backlog]
- create under E1.2 (z8qgvc906j): (Ary, Aly) Test the real email and the "too many attempts" case against the live project [backlog]
- create under E1.3 (z8qgvc906k): (Ary) Privacy toggles enforced by Security Rules (Rules side done in E0.5) [in progress]
- create under E1.3 (z8qgvc906k): (Ary) Initials, not a photo, by default [backlog]
- create under E1.3 (z8qgvc906k): (Ary) Profile gate: a verified account without a profile goes to profile setup [backlog]
- create under E1.3 (z8qgvc906k): (Ary) ProfileRepository interface and Firebase implementation [backlog]
- create under E1.4 (z8qgvc906n): (Ary) Post button opens the Post flow (wired in PR #15) [in progress]
- create under E1.4 (z8qgvc906n): (Ary) Profile gate after E1.3 [backlog]
- create under E10.2 (z8qgvc907u): (Ary, Aly) Find one Malay reviewer for the BM text [backlog]
- create under E2.1 (z8qgvc906m): (Gathan) UM verified Host check on the Post flow [done]
- create under E2.1 (z8qgvc906m): (Gathan) Compare building names without upper and lower case differences [backlog]
- create under E2.1 (z8qgvc906m): (Aaron, Gathan) Review the Worker, upload limits, abandoned photo cleanup and free quota before live activation [backlog]
- create under E2.1 (z8qgvc906m): (Gathan, Aaron) Seed the real area list and deploy the reviewed Worker [backlog]
- create under E3.3 (z8qgvc906v): (Aaron) Mark in ASSUMPTIONS.md which fare and transit checks (#3, #4, #5) are still open [backlog]
- create under T3.1 (z8qgvc9085): (Ary) Check the rubric items and the 10 to 15 minute limit in the brief [backlog]

Delete these ClickUp subtasks (Ary asked on 10 Nov 2026 to drop every "send to the group" task; the task files no longer have them):

- delete z8qgvc90dw (Ary) Announce the component list to the team and how to use it
- delete z8qgvc93h8 (Aaron) Post the contracts to the group so everyone can build against stubs
- delete z8qgvc90g3 (Gathan) Send the field list to the group by 15 Nov
- rename z8qgvc93z7 → (Aly) Write room prices, peak hours, fares and feature importance into docs/product/ASSUMPTIONS.md, where Gathan, Aaron and Dafi read them

- 11 Nov 2026 (Ary, PRD v0.3): no true monthly cost and no campus days. Also rename E3.4 "True monthly cost and travel time on the Room card" to "Per-trip commute on the Room card", E4.2 "Commute options card" to "Getting to your faculty", and replace the E1.3 subtasks about campus days with "Remove campus-day collection" (task files have the final text).

- 12 Nov 2026 (Ary): the Match tab is renamed Housemates and a Group can be created or joined there (maximum members 2 to 8 set by the creator, join with a code or link, creator accepts). Update E6.1 and E6.4 (tab name), E9.2 (new subtask: Housemates entries, Create a group, Invite to the group, join requests; new Rules and `requestToJoinGroup` points) and E5.2 ("Form a group" opens Create a group).

- 12 Nov 2026 (Ary): the utilities estimate is dropped. T1.3: remove #7 from subtask 1 (Aly, Gathan) and rename it "#1 Room price weights · 10 Nov"; E2.4: no utilities estimate in `config/app`.
