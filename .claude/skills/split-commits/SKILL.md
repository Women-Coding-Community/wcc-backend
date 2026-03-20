---
name: split-commits
description: Analyse all uncommitted changes, group them by logical concern into atomic commits, commit each group in sequence, then suggest which groups warrant separate pull requests.
---

# Split Commits Skill

> **Canonical runbook**: `.ai/skills/split-commits.md`
> This file is the Claude Code adapter. The workflow logic is defined in the canonical skill
> so it can be shared with other agents (Codex, Copilot, Cursor). Any changes to the workflow
> should be made in `.ai/skills/split-commits.md` first.

## Workflow

1. Gather local changes in parallel:
   - `git status --short`
   - `git diff HEAD --stat` — per-file summary
   - `git diff HEAD` — full content diff
   - `git log --oneline -5` — recent commit context

2. **Scan for sensitive data** — abort immediately if found (same rules as the `commit` skill).

3. **Group files by logical concern** using three signals in priority order:
   - **Domain / feature boundary**: files for the same entity or feature stay together (service + test + integration test); DB migrations + domain model they enable; config/skill files as their own group
   - **Change type**: do not mix `feat`/`fix` production changes with `chore`/`refactor`/`test` changes unless inseparable
   - **PR separability**: mark each group as independent PR candidate, depends-on another group, or not a PR candidate

4. **Present the proposed split** — for each group show:
   - Conventional commit subject (50–72 chars)
   - Change type (`feat`, `fix`, `refactor`, `test`, `chore`, `docs`)
   - Files in the group
   - PR candidate status and why
   - One sentence explaining why these files belong together

   Then ask: **"Does this split look right? Reply yes to commit group by group, or describe adjustments."**
   Do NOT write any commits until the user confirms.

5. **Commit each group in sequence** — for each confirmed group:
   - Re-scan for sensitive data in this group's files
   - Draft a conventional commit message (subject + body ≥ 2 sentences, lines ≤ 72 chars, no Co-Authored-By)
   - Show the draft and wait for confirmation before staging
   - Stage by explicit file name (`git add <files>` — never `git add .` or `git add -A`)
   - Commit using a heredoc to preserve formatting
   - Run `git status` to confirm before moving to the next group

6. **Final summary and PR suggestions** — list all commits created and suggest which to bundle into each PR, referencing `/open-pr` for generating descriptions.

## Rules

- Never use `git add .` or `git add -A`
- Never include Co-Authored-By or AI attribution in commit messages
- Always write a commit body (not just a subject line)
- Stop immediately if sensitive data is detected — do not commit any group
- Never force-push
- If only one logical group exists, tell the user and suggest `/commit` instead
