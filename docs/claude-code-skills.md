# Claude Code Skills — Developer Guide

This document explains the Claude Code skills configured in this repository and how to use them in your daily workflow.

## What are Claude Code Skills?

Claude Code skills are custom slash commands you can invoke directly in your terminal with Claude Code. Instead of writing a long prompt every time, you type a short command like `/commit` or `/pr-review` and Claude follows a predefined, project-specific workflow.

Skills live in `.claude/skills/<skill-name>/SKILL.md` and are automatically discovered by Claude Code.

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

1. Create a directory: `.claude/skills/<your-skill-name>/`
2. Create `SKILL.md` with a YAML frontmatter block and a markdown body:

```markdown
---
name: your-skill-name
description: One-line description shown in Claude Code
---

# Skill Title

## Step 1: ...
## Step 2: ...
```

3. Claude Code discovers it automatically — invoke it with `/<your-skill-name>`

**Tips for writing good skills:**
- Be explicit about the order of steps
- State what to do when something goes wrong (abort conditions)
- Include example commands the skill should run
- Keep the scope focused — one skill, one job

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

## References

- [Claude Code documentation](https://docs.anthropic.com/en/docs/claude-code)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [WCC Backend — quality checks](./quality_checks.md)
- [WCC Backend — CLAUDE.md](../CLAUDE.md)
