# Skill: pre-commit-review

Local code review workflow that analyses staged and unstaged changes before a commit, outputs an overall summary and per-file line-level findings.

## When to apply

Run this workflow when the user asks to review local changes, check code before committing, or `/pre-commit-review`.

## Step 1 — Gather local diff

Run these in parallel:

```bash
git status --short
git diff HEAD          # all changes (staged + unstaged) vs last commit
git diff --staged      # staged-only changes
git log --oneline -5   # recent context
```

If there are no staged or modified files, tell the user there is nothing to review and stop.

## Step 2 — Review mindset

Prioritise findings in this order:

1. **Behavioral regressions** — does this break existing functionality?
2. **Security issues** — authentication, authorisation, input validation, data exposure
3. **Data integrity** — correct transactions, null handling, edge cases
4. **Convention violations** — stack-specific patterns (see Step 3)
5. **Test coverage** — are new paths exercised?

Validate your assumptions by reading nearby code in the repository before commenting.
Keep summaries short; lead with findings.

## Step 3 — Stack-specific checks

Apply only the checks relevant to what was actually changed.

### Java / Spring Boot
- Java 21 idioms: records, pattern matching, sealed classes, text blocks
- Spring Boot conventions: correct use of `@Service`, `@Repository`, `@Transactional`
- Error handling: proper exception types, global handler, no swallowed exceptions
- Naming: consistent with existing codebase style
- No business logic in controllers; no data access in services directly
- Lombok usage aligns with project patterns

### Frontend (React / TypeScript)
- Component boundaries and single responsibility
- Typing quality — avoid `any`, prefer explicit interfaces
- State and effect hygiene — no unnecessary re-renders, correct dependency arrays
- Project style conventions

### Tests
- New logic paths have corresponding unit tests
- Integration tests exist for database/API changes
- Test names follow Given-When-Then `@DisplayName` format (Java) or descriptive `it()`/`test()` (JS)
- Mocks used only where appropriate; integration tests hit real infrastructure

## Step 4 — Output format

Structure the review output as follows:

### Overall Summary
A short paragraph (2-5 sentences) covering:
- What the change does
- The most important risks or issues found
- Whether it is safe to commit as-is or needs fixes first

### Per-file findings

For each file that has findings, use this structure:

```
**<file-path>**

  Line <N>: [SEVERITY] <short title>
  Risk: <what could go wrong>
  Scenario: <exact condition where it fails>
  Suggestion: <minimal fix direction>

  ```suggestion
  <concrete code snippet or patch example>
  ```
```

Severity levels:
- `[CRITICAL]` — must fix before committing (security, data loss, broken contract)
- `[WARNING]` — should fix (likely bug, convention mismatch, missing test for changed path)
- `[INFO]` — optional improvement (style, readability, future-proofing)

### What looks good
Briefly call out 1-3 things done well — keep the developer motivated.

## Step 5 — Finding quality bar

Every finding must:
- State the **concrete risk** (what could go wrong)
- Point to the **exact scenario** where it fails
- Suggest a **minimal fix** direction
- Include a **code snippet** or patch example when feasible
- Keep tone **factual and collaborative** — keep the contributor motivated to learn and keep contributing

Do not invent issues. If something cannot be verified from the diff and nearby code, say so explicitly.
