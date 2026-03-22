# .ai — Agent-Agnostic Skill Definitions

This folder contains workflow runbooks that any AI coding agent can follow.
They are written in plain markdown with no tool-specific syntax so they can be
consumed by Claude Code, OpenAI Codex, GitHub Copilot, Cursor, Windsurf, or
any agent that reads repository context.

## Structure

```
.ai/
  skills/
    commit.md       ← safe commit workflow (any agent)
    pr-review.md    ← PR review workflow (any agent)
  README.md         ← this file
```

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

## Adding a new skill

1. Write the runbook in `.ai/skills/<name>.md`
2. Add an entry in `AGENTS.md` so Codex knows about it
3. Add `.claude/skills/<name>/SKILL.md` pointing to the canonical skill
4. Optionally add a Cursor rule or Copilot instruction referencing it
