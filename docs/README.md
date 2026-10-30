# Docs

Everything the team writes down lives here. Start with the PRD.

```
docs/
├── product/        what we build and why
│   ├── PRD.md            requirements, modules, FRs (source of truth)
│   ├── GLOSSARY.md       the words we use (Unit, Room, Host, Match, ...)
│   ├── USER-FLOW.md      flowcharts of every journey
│   ├── SURVEY.md         survey questions
│   └── ASSUMPTIONS.md    numbers still to verify, with owners and due dates
├── design/
│   └── DESIGN.md         design system, components, Figma file, Android resources
├── team/           who does what and when
│   ├── TEAM_PLAN.md      roles, timeline, workflow
│   ├── work-breakdown.md   work breakdown: initiative, epics, stories and subtasks in ClickUp
│   └── aaron.md ary.md aly.md dafi.md gathan.md ilham.md   personal task files
├── tasks/          one detailed task file per story, grouped by epic (E1/E1.1.md); README.md is the index
├── ai-log/         AI Usage Log (mandatory appendix of the report): README with the template, one file per person
├── architecture.md, api-contracts.md, ai-usage-log.md, superpowers/   engineering notes (Aaron)
└── licenses/       licences of bundled third-party assets (Geist font, Material Symbols)
```

| Read this | For |
|---|---|
| [product/PRD.md](product/PRD.md) | What we build (source of truth) |
| [product/GLOSSARY.md](product/GLOSSARY.md) | The words we use |
| [product/USER-FLOW.md](product/USER-FLOW.md) | Flowcharts |
| [product/ASSUMPTIONS.md](product/ASSUMPTIONS.md) | Numbers to verify |
| [design/DESIGN.md](design/DESIGN.md) | Design system and components |
| [team/TEAM_PLAN.md](team/TEAM_PLAN.md) | Roles, timeline, workflow |
| [architecture.md](architecture.md) | Firebase and backend architecture |
| [api-contracts.md](api-contracts.md) | Draft request and response formats of the Functions |
| [aaron-deliverable-drafts.md](aaron-deliverable-drafts.md) | Assisted350/400-word report drafts and90-second demo script; needs human review |
| [aaron-handoff.md](aaron-handoff.md) | Open decisions, owners and P0 freeze gates |
| [commute-input-evidence.md](commute-input-evidence.md) | Dated fare/fuel/parking research and five public-stop route probes; confirmed inputs and gaps |
| [transit-processing.md](transit-processing.md) | Bounded JavaScript GTFS processor, actual snapshot probe results and supported/unsupported semantics |
| [Local load preparation](../backend/tests/load/README.md) | Reproducible50/100-user Auth emulator scenarios, both measured results and production limits |
| [ai-log/README.md](ai-log/README.md) | AI Usage Log: rules, template and one file per person (mandatory appendix) |
| [ai-usage-log.md](ai-usage-log.md) | Aaron's earlier AI record |

Rules for docs: one topic per file, keep the PRD as the single source of truth, link to it instead of copying
it, and write relative links so they work on GitHub and in the editor. When you move a file, fix its links.
