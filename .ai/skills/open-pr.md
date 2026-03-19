# Skill: open-pr

Generate a pull request title and description following the WCC repository template, then print the
GitHub URL so the user can open the PR in the browser.

## When to apply

Run this workflow when the user asks to open a PR, create a pull request, or `/open-pr`.

## Step 1 — Gather context

Run in parallel:

- `git log main..HEAD --oneline` — commits included in this PR
- `git diff main...HEAD --stat` — files changed
- `git branch --show-current` — current branch name
- Read `.github/PULL_REQUEST_TEMPLATE.md` — the repo PR template

## Step 2 — Determine change types

Map each commit's conventional-commit prefix (or the nature of the diff) to the PR change type:

| Conventional prefix      | PR Change Type |
|--------------------------|----------------|
| `feat`                   | New Feature    |
| `fix`                    | Bug Fix        |
| `refactor`               | Code Refactor  |
| `docs` / `doc`           | Documentation  |
| `test`                   | Test           |
| `chore` / `ci` / `build` | Other          |

A single PR may have **multiple** change types — check all that apply.
Only include the change types that are actually present in the diff; remove the rest from the
template.

## Step 3 — Suggest a PR title

Derive the title from the dominant change type using Conventional Commits format:

```
<type>: <short imperative description>
```

Types: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`

Rules:

- 50–72 chars total
- Imperative mood, no trailing period
- If multiple types apply, use the most significant one (`feat` > `fix` > `refactor` > `test` >
  `docs` > `chore`)

Example: `feat: Add mentee skill proficiency to registration and list endpoint`

## Step 4 — Generate the PR description

Fill in the repo template. Only keep the change type checkboxes that apply — remove the ones that do
not:

```markdown
## Description

[2–5 sentences explaining what changed, why it was needed, and any notable trade-offs or decisions.]

## Related Issue

Closes #<number>

## Change Type

- [x] <only the applicable types, pre-checked>

## Screenshots

[Determined by Step 4a below]

## Pull request checklist

[Determined by Step 4b below]
```

## Step 4a — Screenshots section

**Evaluate whether screenshots are needed** by inspecting the changed files:

- **Frontend changes** (`admin-wcc-app/**`, `*.tsx`, `*.css`, `*.scss`, components, pages):
  - Include the Screenshots section and list **specific screenshots the reviewer needs**:
    - New or changed UI pages/components → before & after screenshots
    - CSS/layout changes → screenshot showing the visual result
    - Error states → screenshot of the error display
    - Title or label changes → screenshot of the updated text
    - Complex interactions → a GIF or screen recording is strongly encouraged
  - Add a note: _"Screenshots are required for this PR — reviewers cannot assess visual changes without them."_

- **Backend API changes** (new/changed endpoints in `controller/**`, new responses):
  - Include the Screenshots section with: _"Please add Swagger UI screenshots of the new/changed endpoints (`/swagger-ui/index.html`)."_

- **No visual or API impact** (only `test`, `docs`, `chore`, `ci`, `refactor` with no endpoint changes):
  - **Omit the Screenshots section entirely** from the description.

## Step 4b — Pull request checklist

Always include:
```markdown
- [x] I checked and followed the [contributor guide](../CONTRIBUTING.md)
```

Include the "tested locally" item **only if** the change has runnable behaviour (i.e. any `feat`, `fix`, `refactor`, or frontend change). Omit it for pure `docs`, `chore`, or `ci` changes:
```markdown
- [x] I have tested my changes locally.
```

## Step 5 — Print title, description, and URL

Output in this order:

1. **Suggested PR title** (plain text, ready to paste)
2. **PR description** (in a markdown code block, ready to paste)
3. **GitHub compare URL**:

```
https://github.com/Women-Coding-Community/wcc-backend/compare/main...dricazenck:<branch-name>
```

Tell the user to:

1. Open the URL above
2. Paste the title and description
3. Add screenshots before submitting (if required per Step 4a)
4. Submit the PR

## Rules

- Always target `main` on `Women-Coding-Community/wcc-backend` (upstream), not the fork
- Never attempt to create the PR programmatically via `gh` — fork permissions require the user to open it in the browser
- Issue number should come from branch name or commit messages; if not found, use `#?` and ask the user
- Never include change type checkboxes for types not present in the diff
- Never include the Screenshots section for pure test/docs/chore/ci changes
- Never include "I have tested my changes locally" for pure docs/chore/ci changes