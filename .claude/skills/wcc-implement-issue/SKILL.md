---
name: wcc-implement-issue
description: Read a GitHub issue from Women-Coding-Community/wcc-backend, produce an implementation plan, and — on user confirmation — implement it following WCC project standards (unit tests, integration tests, PMD-clean, Javadoc on public methods, no inline comments, language-specific best practices).
argument-hint: "<issue-number>"
allowed-tools: Bash(gh *), Bash(git *), Bash(./gradlew *), Bash(npm *), Grep, Glob, Read, Write, Edit, Agent
---

> **Scope**: Local — wcc-backend only

# WCC Issue Implementer

Read a GitHub issue, analyse the codebase, produce a concrete implementation plan, and implement it on
confirmation — following all WCC quality standards.

> **Critical repo rules**
> - Always fetch issues from `Women-Coding-Community/wcc-backend`
> - Never push to `Women-Coding-Community/wcc-backend` directly — work on the contributor's fork
>   (the `origin` remote of the current repository)
> - Never create commits with AI attribution lines

---

## Step 1 — Read the issue

If `$ARGUMENTS` contains an issue number, fetch it immediately. Otherwise ask:

> Which GitHub issue number should I implement?

```bash
gh issue view "$ISSUE_NUMBER" --repo Women-Coding-Community/wcc-backend \
  --json number,title,body,labels,milestone,assignees,comments
```

Display a brief summary (title + key acceptance criteria extracted from the body) so the user can
confirm it's the right issue before proceeding.

---

## Step 2 — Check repository state

Run all checks in parallel:

```bash
# 2a — current branch and uncommitted changes
git branch --show-current
git status --short

# 2b — verify branch is based on upstream main
git fetch upstream main 2>/dev/null || git fetch origin main
git merge-base --is-ancestor HEAD upstream/main \
  || git merge-base --is-ancestor HEAD origin/main \
  || echo "WARN: branch may not be based on upstream main"

# 2c — diff summary
git diff --stat HEAD
```

**Branch validation rules:**

- If the current branch is `main` → stop and tell the user:
  > "You are on `main`. Please create a feature branch first:
  > `git checkout -b feat/<short-name>`"
- If the branch has diverged from upstream `main` by more than expected → warn and ask whether to
  continue.
- If there are uncommitted changes → warn and ask whether to stash or continue.

Only proceed once the branch is valid.

---

## Step 3 — Explore the codebase

Search for code directly relevant to the issue (controllers, services, repositories, domain models,
tests, migrations, frontend components). Use `Grep` and `Glob` in parallel. Collect:

1. **Files likely to change** — list with brief reason for each
2. **Files likely to need new tests** — both unit (`src/test/`) and integration (`src/testInt/`)
3. **Relevant patterns** — existing code snippets that demonstrate the conventions to follow
4. **DB migrations needed** — check `src/main/resources/db/migration/` for the next version number

---

## Step 4 — Produce the implementation plan

Present the plan clearly before writing any code. Structure:

```
## Implementation Plan — #<issue-number>: <title>

### Files to create
- `path/to/NewClass.java` — reason

### Files to modify
- `path/to/ExistingClass.java` — what changes and why

### Database migrations
- `V<N>__description.sql` — what it adds/changes (if needed)

### Tests
**Unit tests**
- `path/to/XxxTest.java` — class under test, scenarios to cover

**Integration tests**
- `path/to/XxxIntegrationTest.java` — HTTP endpoints / DB scenarios

### Quality checklist (will be run before finishing)
- [ ] `./gradlew pmdMain pmdTest` — zero violations
- [ ] `./gradlew test` — all unit tests pass
- [ ] `./gradlew testIntegration` — all integration tests pass
- [ ] No inline comments added (logic explained via method/variable naming)
- [ ] All new public methods have Javadoc
- [ ] Frontend: ESLint / TypeScript compiler clean (if applicable)
```

Then ask:

> Does this plan look correct? Reply **yes** to start implementation, or describe changes and I'll
> revise the plan.

Do **not** write any code until the user confirms.

---

## Step 5 — Implement

Work through the plan file by file. Follow these standards rigorously:

### Java / Spring Boot
- Use constructor injection; never field `@Value` injection in service/component classes
- For multiple related config properties, create a `@ConfigurationProperties` class in
  `configuration/`
- Use Lombok (`@Builder`, `@RequiredArgsConstructor`, `@Data`, `@Getter`, etc.) consistently with
  existing classes
- Use records for DTOs, requests, and responses
- All new public methods and classes **must** have Javadoc (`/** … */`) following the project's
  Javadoc conventions (see CLAUDE.md)
- **No inline `//` comments** — if logic is non-obvious, rename the method/variable or extract a
  well-named helper
- Follow the Controller → Service → Repository layering; no business logic in controllers
- Use `@Transactional` on service methods that write to the DB
- Follow `GlobalExceptionHandler` patterns for error responses
- Add `@Operation(summary = "…")` Swagger annotations on all new controller methods

### Tests (Java)
- Use `@DisplayName` with Given-When-Then format on every test method
- Test method names use `should` prefix (e.g. `shouldReturn404WhenMemberNotFound`)
- Use AssertJ (`assertThat`) for all assertions — never JUnit `assertEquals`
- Unit tests: mock all dependencies with Mockito; do not hit the database
- Integration tests: use Testcontainers (PostgreSQL); test real HTTP requests via `MockMvc` or
  `TestRestTemplate`
- Cover: happy path, not-found / 404, validation errors / 400, auth failure / 401

### Frontend (React / TypeScript) — if applicable
- Prefer functional components and hooks
- Type all props and return values; avoid `any`
- Follow existing component file structure and naming conventions
- Add or update Jest tests alongside component changes

### YAML / configuration files
- Keep indentation consistent with existing file style
- Add comments only for non-obvious configuration keys

### SQL migrations
- Name: `V<N>__<snake_case_description>.sql` using the next available version number
- Always include `IF NOT EXISTS` / `IF EXISTS` guards where appropriate
- Do not use `flyway:clean` operations

---

## Step 6 — Run quality checks

After implementation is complete, run checks sequentially and fix any failures before reporting done:

```bash
# PMD static analysis
./gradlew pmdMain pmdTest

# Unit tests
./gradlew test

# Integration tests (requires Docker)
./gradlew testIntegration
```

If PMD reports violations:
- Read the violation details
- Fix each violation — do **not** suppress with `@SuppressWarnings` unless the violation is a false
  positive, in which case explain why to the user
- Re-run `./gradlew pmdMain pmdTest` to confirm clean

If tests fail:
- Read the failure output
- Fix the root cause (never delete or skip failing tests)
- Re-run until green

For frontend changes, also run:
```bash
cd admin-wcc-app && npm test && npm run build
```

---

## Step 7 — Final report

When all checks pass, output:

```
## Implementation complete — #<issue-number>: <title>

### Changes made
- [list of created/modified files]

### Tests added
- [list of new test classes and scenarios]

### Quality checks
- PMD: ✅ clean
- Unit tests: ✅ <N> passed
- Integration tests: ✅ <N> passed
- Frontend: ✅ / N/A

### Next steps
- Review the changes: `git diff main`
- Commit: follow conventional commit format (`feat:`, `fix:`, etc.)
- Push to your fork and open a PR against Women-Coding-Community/wcc-backend main
```

---

## Error handling

- **Branch on main**: stop, instruct user to create a feature branch
- **PMD violation that cannot be cleanly fixed**: explain the trade-off and ask the user how to
  proceed
- **Test infrastructure unavailable** (Docker not running): warn the user, skip integration tests,
  note they must be run before opening a PR
- **Scope creep detected** (issue touches more than planned): pause, describe the additional scope,
  and ask the user whether to include it
- **Conflicting patterns found in codebase**: surface both patterns and ask the user which to follow
  before writing code