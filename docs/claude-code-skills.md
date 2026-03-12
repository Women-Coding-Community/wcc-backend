# AI Skills — Developer Guide

This document explains the AI workflow skills configured in this repository and how to use them with any AI coding agent.

## The idea: agent-agnostic skills

A **skill** is a plain-markdown workflow runbook. It tells an AI agent exactly what steps to follow for a repeatable task — safely committing code, reviewing a PR, scaffolding a migration, and so on.

The canonical skills live in **`.ai/skills/`** and are written without any tool-specific syntax. Each AI agent then has a thin adapter that points to them:

| Agent | Adapter location | Invoke with |
|-------|-----------------|-------------|
| **Claude Code** | `.claude/skills/<name>/SKILL.md` | `/commit`, `/pr-review` |
| **OpenAI Codex** | `AGENTS.md` (repo root) | Natural language or slash commands |
| **GitHub Copilot** | `.github/copilot-instructions.md` | Natural language in chat |
| **Cursor** | `.cursor/rules/*.mdc` | Natural language or `@commit` rules |
| **Windsurf** | `.windsurf/rules/` | Natural language |

The workflow logic is defined **once** in `.ai/skills/`. Agent adapters are thin wrappers — no duplication.

---

## Available Skills

### `/commit`

**File:** `.claude/skills/commit/SKILL.md`

Safely stages and commits your changes with a well-structured commit message.

**What it does:**
1. Runs `git status` and `git diff` to gather context
2. Scans for sensitive data (API keys, passwords, private keys, `.env` files) — **aborts immediately** if any are found
3. Drafts a commit message following the conventional format (subject + body explaining *why*)
4. Shows the draft for your review before committing
5. Stages files by name (never `git add .`) to avoid accidental inclusion of sensitive files
6. Creates the commit and confirms success

**How to use:**
```bash
# Make your changes, then in Claude Code:
/commit
```

**Why this matters:**
- Prevents accidental credential leaks in commit history
- Enforces meaningful commit messages (not just "fix stuff")
- Follows the project convention of imperative mood + explanatory body
- Never adds AI co-author attribution lines

---

### `/pr-review`

**File:** `.claude/skills/pr-review/SKILL.md`

Reviews a pull request using the GitHub CLI and posts inline comments directly on the changed lines.

**What it does:**
1. Fetches PR metadata and diff via `gh` CLI
2. Reviews with a code-review mindset: prioritizes regressions, security issues, data integrity, and edge-case failures
3. Checks stack-specific conventions:
   - **Java/Spring Boot**: Java 21 idioms, Spring Boot patterns, transaction handling, naming
   - **Frontend (React/TypeScript)**: component boundaries, typing, state/effect hygiene
   - **Tests**: coverage around new logic, missing integration/unit tests
4. Posts inline comments anchored to the exact changed lines
5. Keeps comments concise, actionable, and encouraging

**How to use:**
```bash
# In Claude Code, referencing a PR number:
/pr-review 541

# Or with a full URL:
/pr-review https://github.com/Women-Coding-Community/wcc-backend/pull/541
```

**Why this matters:**
- Gives you a second pair of eyes before requesting human review
- Catches issues specific to this codebase's conventions
- Posts comments where reviewers (and you) expect them — on the code lines, not just a summary
- Keeps contributors motivated and learning

---

## How to Create a New Skill

### 1. Write the canonical runbook in `.ai/skills/`

Create `.ai/skills/<your-skill-name>.md`:

```markdown
# Skill: your-skill-name

Short description of when to apply this skill.

## When to apply
...

## Step 1 — ...
## Step 2 — ...
## Step N — ...

## Rules
- ...
```

Keep it plain markdown — no tool-specific syntax. This is the source of truth.

### 2. Add the Claude Code adapter

Create `.claude/skills/<your-skill-name>/SKILL.md`:

```markdown
---
name: your-skill-name
description: One-line description shown in Claude Code
---

# Your Skill Name

> **Canonical runbook**: `.ai/skills/your-skill-name.md`
> This file is the Claude Code adapter.

[paste or summarise the canonical runbook steps here]
```

### 3. Register with other agents

- **Codex**: add a section to `AGENTS.md`
- **Copilot**: add a line to `.github/copilot-instructions.md`
- **Cursor**: create `.cursor/rules/<your-skill-name>.mdc`

**Tips for writing good skills:**
- Be explicit about the order of steps
- State what to do when something goes wrong (abort conditions)
- Include example commands the skill should run
- Keep the scope focused — one skill, one job
- Write the canonical runbook first, adapters second

---

## Using Skills as a Learning Buddy

Skills are not just automation — they are a way to learn and build confidence in the code you deliver.

### Learning with `/commit`

When you run `/commit`, Claude reads your diff and writes a commit message explaining *why* the change exists. Read that explanation. If it doesn't match what you intended, your code may not express your intent clearly. This is a fast feedback loop to improve how you communicate through code.

### Learning with `/pr-review`

Before asking teammates to review your PR, run `/pr-review` on it yourself. Claude will:
- Point out patterns that deviate from Java 21 / Spring Boot conventions in this project
- Flag missing test coverage with concrete suggestions
- Explain *why* something is a risk, not just that it is

Use the feedback to fix the PR, then submit it to your team. You will arrive at reviews already having addressed common issues — and you will learn the patterns faster because the feedback is immediate and contextual.

### The learning loop

```
Write code
    ↓
/commit  →  Read the "why" Claude drafted — does it match your intent?
    ↓
/pr-review  →  Address inline feedback before human review
    ↓
Submit PR  →  Human review focuses on higher-level design
    ↓
Merge with confidence
```

This loop is especially powerful when learning a new language or framework. You get expert-level feedback on every change, not just when a senior developer has time to review.

---

## Repository structure

```
.ai/
  skills/
    commit.md           ← canonical commit workflow (any agent)
    pr-review.md        ← canonical PR review workflow (any agent)
  README.md             ← agent compatibility table

.claude/skills/         ← Claude Code adapters
  commit/SKILL.md
  pr-review/SKILL.md

AGENTS.md               ← OpenAI Codex entry point
.github/
  copilot-instructions.md   ← GitHub Copilot instructions
.cursor/rules/          ← Cursor rules
  commit.mdc
  pr-review.mdc
```

## References

- [Claude Code documentation](https://docs.anthropic.com/en/docs/claude-code)
- [OpenAI Codex — AGENTS.md spec](https://platform.openai.com/docs/codex)
- [GitHub Copilot custom instructions](https://docs.github.com/en/copilot/customizing-copilot)
- [Cursor rules documentation](https://docs.cursor.com/context/rules)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [WCC Backend — quality checks](./quality_checks.md)
- [WCC Backend — CLAUDE.md](../CLAUDE.md)
