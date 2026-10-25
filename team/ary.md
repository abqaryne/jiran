# Ary: Group leader, Frontend Lead, Lead UI/UX

## You own
- **Module 1** Authentication and verified profile (FR1.1, FR1.2, FR1.3)
- **Module 3** Roommate matching screens (FR3.1 questionnaire, FR3.2 match list with score and reasons, FR3.3 connect and group)
- Figma, the design system, the Android app shell (Activities, Fragments, bottom navigation), theme and shared components
- Final check and merge of **every PR**
- Main contact with the lecturer

You do not write the match score (Aaron) or the match reason text (Ilham). You build the screens that show them.

## Right now: Step 0 (26 Oct to 8 Nov)
- [ ] Add everyone as collaborators on the GitHub repo (collect their usernames in the group chat)
- [ ] Email the lecturer: ask for Appendix 1 of the brief and confirm native Android (Java) is fine
- [ ] Figma: tokens (navy `#14213D`, gold `#FFC72C`, green `#1E6B41`, grey `#F4F6FA`), type scale, buttons, cards, inputs
- [ ] Design the 5 tabs: Home, Rooms (list, room details with the commute options card), Match, House, Ask, plus the Evaluate unit screen (P1)
- [ ] Review the survey wording with Aly so questions match the screens

## Next
**Step 1 (2 to 15 Nov):** app shell with the 5-tab bar, theme, and shared components (primary button in gold with navy text, cards, listing card, status chips with words not only colour, empty and error states). Publish the component list so others can build their screens with it.

**Wave A (16 to 29 Nov): Module 1**
- Sign-up only with a UM student email, with a verification link (FR1.1)
- Log in, log out, reset password (FR1.2)
- Profile: faculty, class timetable (day, start, end, building), monthly budget, lifestyle tags, owns a car, and which details stay hidden until a mutual match (FR1.3)

**Wave B (30 Nov to 13 Dec)**
- Room details screen with the commute options card (waits for Aaron's commute API; use the stub until it lands)
- Module 3 screens: questionnaire, ranked match list, connect and accept, group of 2 to 4 with room price split (P1)

## Hand-offs
| You wait for | From | Use meanwhile |
|---|---|---|
| Field names for users and listings | Gathan (by 15 Nov) | Mock data |
| Commute API response | Aaron (by 15 Nov, contract) | Stub JSON |
| Match score and reasons | Aaron and Ilham | Fake scores |

## Your PR check-in routine
1. CI is green and the PR links a Trello card.
2. Backend or rules PRs already have Aaron's or Gathan's review.
3. UI follows the design system: contrast 4.5:1, touch targets at least 48 dp, status shown with words.
4. Merge, and tell the owner so QA can pick it up.

If PRs queue for more than a day, let Aaron merge backend-only PRs.

## Your parts in the graded deliverables
- **Video:** segment 1 overview (1:30), your part of the demo (sign-up, profile, roommate matching), segment 3 design and usability (1:00)
- **Report:** section 1 Introduction (300 words), the UI and colours part of section 3, final review
- **Risk:** you are the bottleneck for screens and merges. If the load is too heavy, hand the Module 3 screens to Ilham or Gathan.

## Rules for everyone

- Minimum **20 hours** each, logged on your Trello cards. Commits and Trello are the evidence for the report and the peer evaluation.
- Commit message format: `[Mod2][FR2.1] short description`.
- Every change goes through a PR. **Ary does the final check and merges.** Backend PRs are reviewed by Aaron first, schema and rules PRs by Gathan first.
- Never commit API keys or service account files.
- Definition of Done: merged by PR, Android Lint, Checkstyle and tests pass in CI, reviewed, Trello card moved with hours, checked by QA (Aly).
- Anything marked **[DRAFT]** in the PRD is a team decision still to confirm.

Full context: [PRD.md](../PRD.md) (what we build), [TEAM_PLAN.md](../TEAM_PLAN.md) (who does what, timeline), [SURVEY.md](../SURVEY.md).
