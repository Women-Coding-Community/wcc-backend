---
name: pre-commit-review
description: Review local staged and unstaged changes before committing. Outputs an overall summary and per-file line-level findings with severity levels. Use when asked to review code before a commit, check local changes, or suggest improvements on uncommitted work.
---

# Pre-Commit Review

> **Canonical runbook**: `.ai/skills/pre-commit-review.md`
> This file is the Claude Code adapter. The workflow logic is defined in the canonical skill
> so it can be shared with other agents (Codex, Copilot, Cursor). Any changes to the workflow
> should be made in `.ai/skills/pre-commit-review.md` first.

## Workflow

1. Gather local changes:
   - `git status --short`
   - `git diff HEAD` — all changes (staged + unstaged) vs last commit
   - `git diff --staged` — staged-only changes
   - `git log --oneline -5` — recent context

2. Review with a code-review mindset — same priority order as PR review:
   - Behavioral regressions, security, data integrity, convention violations, test coverage.
   - Validate assumptions by reading nearby repository code before commenting.

3. Check conventions and quality for changed technologies only:
   - **Java/Spring Boot**: Java 21 idioms, Spring Boot conventions, transaction/error-handling patterns, naming consistency, clean-code principles, Lombok alignment.
   - **Frontend (React/TypeScript)**: component boundaries, typing quality, state/effect hygiene, project style conventions.
   - **Tests (Java)**: coverage of new logic paths; method names use `should` prefix; every test has `@DisplayName("Given …, when …, then …")` — no `// Arrange / Act / Assert` comments; prefer `.getFirst()` over `.get(0)` (Java 21); mocks only where appropriate.

4. Output the review in two parts:

   **Overall Summary** — 2-5 sentences: what the change does, key risks, and whether it is safe to commit as-is.

   **Per-file findings** — for each file, list findings anchored to the changed line number:

   ```
   **<file-path>**

     Line <N>: [SEVERITY] <short title>
     Risk: <what could go wrong>
     Scenario: <exact condition where it fails>
     Suggestion: <minimal fix direction>

     ```suggestion
     <concrete code snippet>
     ```
   ```

   Severity levels:
   - `[CRITICAL]` — must fix before committing
   - `[WARNING]` — should fix
   - `[INFO]` — optional improvement

   **What looks good** — call out 1-3 positive things to keep the developer motivated.

## Comment Quality Bar

- State the concrete risk.
- Point to the exact condition/scenario where it fails.
- Suggest a minimal fix direction.
- Include a concrete code snippet or patch-style example when feasible.
- Keep tone factual and collaborative.
- Keep the contributor motivated and engaged to learn and keep contributing.
