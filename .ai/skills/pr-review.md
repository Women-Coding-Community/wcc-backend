# Skill: pr-review

Code review workflow that posts inline comments on a pull request using the GitHub CLI.

## When to apply

Run this workflow when the user asks you to review a PR, check a pull request, or `/pr-review`.

## Step 1 — Fetch PR data

```bash
gh pr view <number> --repo <owner/repo> \
  --json title,headRefName,baseRefName,author,body,url

gh pr diff <number> --repo <owner/repo>
```

If no PR number is given, ask the user to provide one or a GitHub PR URL.

## Step 2 — Review mindset

Prioritise findings in this order:

1. **Behavioral regressions** — does this break existing functionality?
2. **Security issues** — authentication, authorisation, input validation, data exposure
3. **Data integrity** — correct transactions, null handling, edge cases
4. **Convention violations** — stack-specific patterns (see Step 3)
5. **Test coverage** — are new paths exercised?

Validate your assumptions by checking nearby code in the repository before commenting.
Keep summaries short; lead with findings.

## Step 3 — Stack-specific checks

Apply only the checks relevant to what the PR actually changed.

### Java / Spring Boot
- Java 21 idioms: records, pattern matching, sealed classes, text blocks
- Spring Boot conventions: correct use of `@Service`, `@Repository`, `@Transactional`
- Error handling: proper exception types, global handler, no swallowed exceptions
- Naming: consistent with existing codebase style
- No business logic in controllers; no data access in services directly

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

## Step 4 — Post inline comments

Use `gh api` to post comments anchored to exact changed lines:

```bash
# Get head SHA
gh api repos/<owner>/<repo>/pulls/<number> -q .head.sha

# List files with positions
gh api repos/<owner>/<repo>/pulls/<number>/files --paginate

# Post inline comment
gh api repos/<owner>/<repo>/pulls/<number>/comments \
  -X POST \
  -f commit_id='<sha>' \
  -f path='<file>' \
  -F position=<diff-position> \
  -f body='<comment>'
```

Prefer line-level comments over top-level summary comments.

## Step 5 — Comment quality bar

Every comment must:
- State the **concrete risk** (what could go wrong)
- Point to the **exact scenario** where it fails
- Suggest a **minimal fix** direction
- Include a **code snippet** or patch example when feasible
- Keep tone **factual and collaborative** — keep the contributor motivated to learn and keep contributing

## Useful cleanup commands

```bash
# Delete a misplaced comment
gh api repos/<owner>/<repo>/pulls/comments/<id> -X DELETE
gh api repos/<owner>/<repo>/issues/comments/<id> -X DELETE
```
