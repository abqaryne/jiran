# Docs

Everything the team writes down lives here. Start with the PRD. Tasks are tracked outside this folder: the root `TASK.md` (master list) and `TASKS/<epic>/<ID>.md` (one file per task), with decisions in the root `ADR.md`.

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
├── engineering/    how the system is built
│   ├── architecture.md       Firebase and backend architecture
│   ├── api-contracts.md      draft request and response formats of the Functions
│   └── transit-processing.md bounded GTFS processor
├── research/       dated evidence behind the numbers and the free-only stack
│   ├── commute-input-evidence.md   fares, fuel, parking, five route probes
│   ├── emission-methodology.md     CO2 factors and sources
│   ├── free-transit-evidence.md    official GTFS snapshot
│   ├── maps-readiness.md           Maps pricing and limits
│   └── no-billing-production.md    free-only production candidate
├── plans/          implementation plans and verification notes (Aaron, 27 Oct 2026)
├── team/           who does what and when
│   ├── TEAM_PLAN.md      roles, timeline, workflow, order of work and ClickUp set-up
│   ├── master-prompt.md  prompt to start an AI assistant on your story
│   └── <name>/           one folder per person (aaron, ary, aly, dafi, gathan, ilham)
│       └── README.md     personal task file; put your own notes in your own folder
├── ai-log/         AI Usage Log (mandatory appendix of the report): README with the template, one file per person
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
| [engineering/architecture.md](engineering/architecture.md) | Firebase and backend architecture |
| [engineering/api-contracts.md](engineering/api-contracts.md) | Draft request and response formats of the Functions |
| [engineering/transit-processing.md](engineering/transit-processing.md) | Bounded JavaScript GTFS processor, actual snapshot probe results and supported/unsupported semantics |
| [research/commute-input-evidence.md](research/commute-input-evidence.md) | Dated fare/fuel/parking research and five public-stop route probes; confirmed inputs and gaps |
| [team/aaron/report-drafts.md](team/aaron/report-drafts.md) | Assisted 350/400-word report drafts and 90-second demo script; needs human review |
| [team/aaron/handoff.md](team/aaron/handoff.md) | Open decisions, owners and P0 freeze gates |
| [Local load preparation](../backend/tests/load/README.md) | Reproducible 50/100-user Auth emulator scenarios, both measured results and production limits |
| [ai-log/README.md](ai-log/README.md) | AI Usage Log: rules, template and one file per person (mandatory appendix) |
| [ai-log/aaron-earlier-record.md](ai-log/aaron.md) | Aaron's earlier AI record |

Rules for docs: shared documents go in the topic folders above; your own notes go in `team/<your name>/`, never in the root of `team/` or `docs/`. One topic per file, keep the PRD as the single source of truth, link to it instead of copying
it, and write relative links so they work on GitHub and in the editor. When you move a file, fix its links.
