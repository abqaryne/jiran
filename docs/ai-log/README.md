# AI Usage Log

The WIA2007 brief (UM AI policy, **Level 2: limited usage, specific tasks only**) requires an **AI Usage Log** as an appendix
of the final report. Missing or inaccurate logs, or work that is "predominantly AI-generated with minimal or no human
effort", carry penalties up to a failing grade. This folder is that log. Ilham merges it into Appendix 2(b) of the report.

## Rules

1. **Everyone logs their own AI use**, in `docs/ai-log/<name>.md` (one file per person, so there are no merge conflicts).
   Aaron's earlier record stays in [../ai-usage-log.md](aaron-earlier-record.md).
2. **Log it right away**, at the end of each task, in the same PR as the work. Not at the end of the semester.
3. **Never invent anything.** If a prompt, a result or a number was not recorded, write `not recorded`. Do not write
   "tested" or "reviewed" unless it happened.
4. Keep the **exact prompts** (copy them). A short excerpt is fine in the entry if the full conversation is exported and kept.
5. The **human owner** fills in what they personally verified, changed and understood. An AI assistant may draft the entry
   but must leave those fields for the human, marked `TODO (owner)`.
6. Hours are not guessed: real hours go on the ClickUp task.

## Entry template (matches Appendix 2(b): tool, prompts, how the output was used, tested and modified)

```markdown
## <YYYY-MM-DD> · <short task name> · <FR or area>

- **Human owner:** <name>
- **AI tool and model:** <for example Claude Code (Claude Sonnet 5.5), ChatGPT, Gemini, Codex>
- **Task:** <what you asked the AI to help with, one or two lines>
- **Prompts (exact):**
  > <prompt 1>
  > <prompt 2>
- **Output used:** <what was produced and where it went: files, PR link>
- **How it was tested or checked:** <commands run and results, screens compared, review by whom>
- **What the human changed or rejected:** <corrections, decisions, parts rewritten by hand>
- **What the human understands and can explain:** <the parts you can defend in the video or report>
- **Limits:** <anything not verified, assumptions, numbers that are still estimates>
```

## Files

| File | Who |
|---|---|
| [ary.md](ary.md) | Ary |
| [aaron.md](aaron.md) | Aaron (his earlier record is [../ai-usage-log.md](aaron-earlier-record.md)) |
| [aly.md](aly.md) | Aly |
| [dafi.md](dafi.md) | Dafi |
| [gathan.md](gathan.md) | Gathan |
| [ilham.md](ilham.md) | Ilham (also merges everything into Appendix 2(b) of the report) |
