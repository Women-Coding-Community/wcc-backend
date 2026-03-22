# Skill: pre-commit-review

Local code review workflow that analyses staged and unstaged changes before a commit, fixes style violations automatically, then outputs a summary of any remaining findings that require human judgement.

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

## Step 3 — Fix style violations automatically

For every changed Java file, scan for the style violations below and **edit the file directly** to fix them. Do not report these as findings — just fix and move on. Only report issues that require a human decision (logic, architecture, missing tests).

### Auto-fix: line length (100-char limit)

- Any line exceeding 100 characters must be broken. Use the appropriate strategy:
  - **`@DisplayName` strings**: break at natural Given / when / then boundaries using string concatenation, with `when` and `then` in **lowercase**:
    ```java
    @DisplayName(
        "Given a valid member and an external URL, "
            + "when saving the profile picture, "
            + "then stores and returns it")
    ```
  - **Method chains**: each `.method()` on its own indented line
  - **`@Operation(summary = "…")`** and other annotation strings: same string concatenation approach

### Auto-fix: `@DisplayName` casing

- Keywords `when` and `then` must be **lowercase** throughout the description string. If the existing text uses `When`/`Then` in Title Case, normalise it to lowercase while keeping the rest of the sentence intact.

### Auto-fix: local variable declarations in tests

- Replace explicit type declarations with `var` for local variables in test methods where the type is already obvious from the right-hand side:
  ```java
  // fix
  var result = service.doSomething();
  // keep explicit only when the type isn't clear from the RHS
  List<String> items = new ArrayList<>();
  ```

### Auto-fix: field-by-field assertions

- When multiple `assertThat(result.getX()).isEqualTo(x)` lines assert properties of the same object and the class has an `equals` implementation, replace them with a single full-object comparison:
  ```java
  // fix
  assertThat(result).isEqualTo(expected);
  ```
- For collections, use `containsExactly(…)` or `containsExactlyInAnyOrder(…)` instead of asserting element by element.

### Auto-fix: useless `eq(…)` wrappers

- Remove `eq(literal)` in Mockito `when()`/`verify()` calls when **all** arguments in that call are literals or specific values (no other matchers). Keep `eq()` only when it appears alongside `any()`, `argThat()`, or similar matchers in the same call:
  ```java
  // fix — drop eq()
  when(service.findById(1L)).thenReturn(result);
  // keep — mixed with matcher
  when(service.update(eq(1L), any(Dto.class))).thenReturn(result);
  ```

### Auto-fix: `.get(0)` → `.getFirst()`

- Replace `.get(0)` on `List` with `.getFirst()` (Java 21) in new or changed test code.

## Step 4 — Review for non-auto-fixable issues

After applying all auto-fixes, check for issues that require human judgement. Apply only the checks relevant to what was actually changed.

### Java / Spring Boot
- Java 21 idioms: records, pattern matching, sealed classes, text blocks
- Spring Boot conventions: correct use of `@Service`, `@Repository`, `@Transactional`
- Error handling: proper exception types, global handler, no swallowed exceptions
- Naming: consistent with existing codebase style
- No business logic in controllers; no data access in services directly
- Lombok usage aligns with project patterns
- **`@SuppressWarnings` in `src/main`**: never suppress PMD or other warnings in production code without explicit user approval — find an architectural fix instead (extract method, delegate to another service, reduce class complexity)

### Frontend (React / TypeScript)
- Component boundaries and single responsibility
- Typing quality — avoid `any`, prefer explicit interfaces
- State and effect hygiene — no unnecessary re-renders, correct dependency arrays
- Project style conventions

### Tests (Java)
- New logic paths have corresponding unit tests
- Integration tests exist for database/API changes
- Mocks used only where appropriate; integration tests hit real infrastructure
- **Naming**: method names must use the `should` prefix, not `test`
- **`@DisplayName`**: every test must have one in Given-when-then format
- **Assertions**: use AssertJ for complex/multi-property assertions; JUnit 5 (`assertEquals`, `assertTrue`, `assertThrows`) for simple single-value or boolean checks

## Step 5 — Output format

### Auto-fixes applied
List every file edited and the type of fix applied (one line per file). If nothing was fixed, say so.

### Overall Summary
A short paragraph (2-5 sentences) covering what the change does, the most important remaining risks, and whether it is safe to commit.

### Per-file findings
Only for issues that were **not** auto-fixed and require human attention:

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
- `[INFO]` — optional improvement (architecture, readability, future-proofing)

### What looks good
Briefly call out 1-3 things done well — keep the developer motivated.

## Step 6 — Finding quality bar

Every reported finding must:
- State the **concrete risk** (what could go wrong)
- Point to the **exact scenario** where it fails
- Suggest a **minimal fix** direction
- Include a **code snippet** or patch example when feasible
- Keep tone **factual and collaborative**

Do not invent issues. If something cannot be verified from the diff and nearby code, say so explicitly.
