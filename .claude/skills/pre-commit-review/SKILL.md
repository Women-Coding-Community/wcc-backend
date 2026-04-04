---
name: pre-commit-review
description: Review local staged and unstaged changes before committing. Auto-fixes style violations (line length, @DisplayName casing, var, eq() wrappers, .get(0)), then outputs a summary of findings requiring human judgement.
---

> **Scope**: Local — wcc-backend only

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

2. **Auto-fix style violations** in every changed Java file — edit the files directly, do not just report:
   - **Line length > 100 chars**: break `@DisplayName` strings at Given/when/then boundaries using `+` concatenation; break long method chains; break long annotation strings
   - **`@DisplayName` casing**: normalise `When`/`Then` Title Case to lowercase `when`/`then`
   - **`var` for locals in tests**: replace explicit type declarations with `var` where the type is obvious from the RHS
   - **Field-by-field assertions**: replace multiple `assertThat(result.getX()).isEqualTo(x)` lines on the same object with `assertThat(result).isEqualTo(expected)` when `equals` is available; use `containsExactly` for collections
   - **Useless `eq(…)` wrappers**: remove `eq(literal)` in Mockito calls where all args are literals; keep only when mixed with other matchers in the same call
   - **`.get(0)` → `.getFirst()`**: in new or changed test code (Java 21)

3. Review with a code-review mindset for issues requiring human judgement — same priority order:
   - Behavioral regressions, security, data integrity, convention violations, test coverage.
   - Validate assumptions by reading nearby repository code before commenting.
   - **Java/Spring Boot**: Java 21 idioms, Spring Boot conventions, transaction/error-handling patterns, naming consistency, Lombok alignment. Never suggest `@SuppressWarnings` in `src/main` — report it as [WARNING] if found.
   - **Frontend (React/TypeScript)**: component boundaries, typing quality, state/effect hygiene, project style conventions.
   - **Tests (Java)**: coverage of new logic paths; method names use `should` prefix; every test has `@DisplayName("Given …, when …, then …")`; mocks only where appropriate.

4. Output the review in three parts:

   **Auto-fixes applied** — list each file and what was fixed (one line per file).

   **Overall Summary** — 2-5 sentences: what the change does, key remaining risks, and whether it is safe to commit.

   **Per-file findings** — only for issues not auto-fixed, anchored to the changed line number:

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
