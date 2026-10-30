# E0.0: Story title (same as the ClickUp task)

- **ID:** E0.0
- **FR and priority:** FR0.0, P0 (the PRD requirement this story serves)
- **Owner:** name (helpers: names, and what they do)
- **Due:** date
- **Depends on:** stories or decisions this waits for
- **Blocks:** stories that wait for this one
- **ClickUp:** link to the story task
- **Branch / PR:** `feature/<short-name>`, PR link

## Why

Two or three sentences: which student problem or PRD requirement this serves, and what is missing if it is not built.
Link the PRD section instead of copying it.

## Where it stands (dated)

One paragraph with the date: what exists, what is merged, what is not built, what is not verified. ClickUp is the
source for status; this paragraph explains it.

## What to build

Numbered deliverables. Mark each: ✅ done, ⚠️ done with a gap (say the gap), ⬜ not started.

1. ⬜ Deliverable: what the user sees or what the system does, in one or two lines.

## Acceptance (how QA checks it)

Checks that someone other than the owner can run on an emulator or with `./gradlew testDebugUnitTest`. Take them from the
PRD acceptance criteria and add the screen-level ones. Each line is pass or fail.

- [ ] Check.

## Files and tests

Where the code lives and which tests cover it. Name the classes. Say what is covered only by hand.

## Deliberately not in scope, and why

What this story leaves out on purpose, with the reason, and which story picks it up.

## Decisions taken

Date, who decided, what, why. When a decision is replaced, keep the old line struck through and link the new one. Do not
delete history.

## Open questions

Each with an owner and what it blocks.

## Demo for the video

Which FR this shows, the steps to demonstrate it, and the data it needs.
