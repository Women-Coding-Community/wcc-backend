# .ai — Agent-Agnostic Skill Definitions

This folder contains workflow runbooks that any AI coding agent can follow.
They are written in plain markdown with no tool-specific syntax so they can be
consumed by Claude Code, OpenAI Codex, GitHub Copilot, Cursor, Windsurf, or
any agent that reads repository context.

For OpenAI Codex in this repository, `AGENTS.md` is the main adapter. It is
expected to enumerate the available skills and point back to these canonical
runbooks under `.ai/skills/`.

## Structure

```
.ai/
  skills/
    commit.md             ← safe commit workflow
    open-pr.md            ← PR title/description workflow
    pr-review.md          ← PR review workflow
    pre-commit-review.md  ← local diff review workflow
    pull-request.md       ← pull request preparation workflow
    split-commits.md      ← atomic commit grouping workflow
  README.md         ← this file
```

## Available canonical skills

- `commit` — safe commit with secret scanning and conventional commits
- `pr-review` — inline GitHub PR review with repository-specific checks
- `pre-commit-review` — local diff review before committing
- `open-pr` — generate a PR title and description from the current branch diff
- `pull-request` — broader PR preparation workflow
- `split-commits` — group local changes into atomic commits

## How each agent uses these skills

| Agent | Entry point | How it reads .ai/ skills |
|-------|-------------|--------------------------|
| **Claude Code** | `.claude/skills/<name>/SKILL.md` | Thin wrapper that includes the canonical skill |
| **OpenAI Codex** | `AGENTS.md` (repo root) | Lists skills and points to `.ai/skills/` |
| **GitHub Copilot** | `.github/copilot-instructions.md` | References skills as instructions |
| **Cursor** | `.cursor/rules/*.mdc` | Imports skill content as rules |
| **Windsurf** | `.windsurf/rules/` | Reads markdown rules |

The canonical source of truth lives here in `.ai/skills/`.
Each agent wrapper is a thin adapter — no duplication of logic.

## Codex usage notes

Codex should discover repository skills through `AGENTS.md`, then read the
canonical runbook from `.ai/skills/<name>.md` before executing the workflow.

Recommended pattern:

1. Match the user's request to one of the documented skills
2. Open the corresponding `.ai/skills/<name>.md` file
3. Follow the runbook steps exactly, while still respecting repository safety
   rules and any active user constraints

## Adding a new skill

1. Write the runbook in `.ai/skills/<name>.md`
2. Add an entry in `AGENTS.md` so Codex knows about it
3. Add `.claude/skills/<name>/SKILL.md` pointing to the canonical skill
4. Optionally add a Cursor rule or Copilot instruction referencing it
