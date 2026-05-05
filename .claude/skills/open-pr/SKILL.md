---
name: open-pr
description: Generate a PR title and description following the WCC repo template, infer change types from the diff, and print the GitHub URL to open the PR in the browser. Use when asked to open a PR or create a pull request.
disable-model-invocation: true
allowed-tools: Bash, Read, Glob
---

> **Scope**: Local — wcc-backend only

# Open PR Skill

> **Canonical runbook**: `.ai/skills/open-pr.md`
> This file is the Claude Code adapter. The workflow logic is defined in the canonical skill
> so it can be shared with other agents (Codex, Copilot, Cursor). Any changes to the workflow
> should be made in `.ai/skills/open-pr.md` first.

## Workflow

1. Gather context in parallel:
   - `git log main..HEAD --oneline` — commits in this PR
   - `git diff main...HEAD --stat` — files changed
   - `git branch --show-current` — current branch name
   - Read `.github/PULL_REQUEST_TEMPLATE.md`

2. Determine change types by mapping conventional-commit prefixes and the nature of the diff:

   | Conventional prefix | PR Change Type |
   |---|---|
   | `feat` | New Feature |
   | `fix` | Bug Fix |
   | `refactor` | Code Refactor |
   | `docs` / `doc` | Documentation |
   | `test` | Test |
   | `chore` / `ci` / `build` | Other |

   Only keep the change types present in this PR — remove the rest from the template.

3. Suggest a PR title using Conventional Commits format: `<type>: <short imperative description>` (50–72 chars). When multiple types apply, use the most significant: `feat` > `fix` > `refactor` > `test` > `docs` > `chore`.

4. Generate the filled-in PR description. Pre-check only the applicable change type boxes; remove the inapplicable ones entirely. Write all prose sections (Description, Related Issue) as flowing paragraphs where **each paragraph is a single unbroken line** — do NOT insert newlines within a paragraph for any reason. Blank lines between paragraphs are fine; newlines inside a paragraph are not. When the user pastes the description into GitHub, mid-sentence newlines are preserved literally in the text area and break the visual layout.

5. **Screenshots section** — decide based on changed files:
   - Frontend changes (`admin-wcc-app/**`, `*.tsx`, `*.css`, components, pages): include the section and list the specific screenshots needed (before/after UI, error states, label/title changes, GIF for interactions). Emphasise that screenshots are required for the reviewer to assess visual changes.
   - Backend API changes (new/changed controllers or endpoints): include the section asking for Swagger UI screenshots (`/swagger-ui/index.html`).
   - Only `test`, `docs`, `chore`, `ci`, or `refactor` with no endpoint changes: **omit the Screenshots section entirely**.

6. **Pull request checklist** — always include the contributor guide checkbox. Include "I have tested my changes locally" only for `feat`, `fix`, `refactor`, or frontend changes — omit it for pure `docs`, `chore`, or `ci` changes.

7. Derive the contributor's GitHub username from the `origin` remote:
   ```bash
   git remote get-url origin
   # https://github.com/<username>/wcc-backend.git  or  git@github.com:<username>/wcc-backend.git
   ```
   Extract `<username>` from the URL (path segment before `/wcc-backend`).

   Print:
   - **Suggested PR title** (plain text)
   - **PR description** (markdown code block)
   - **GitHub compare URL**: `https://github.com/Women-Coding-Community/wcc-backend/compare/main...<username>:<branch-name>`
   - Remind the user to add screenshots before submitting if required

## Rules

- **Each prose paragraph must be a single unbroken line** — never wrap text at 72, 80, or any column width. The only newlines inside prose sections must be blank lines separating paragraphs. Mid-sentence newlines paste literally into the GitHub text area and break the rendered layout.

  ✅ Correct: `This PR adds email notifications. Notifications are sent to both mentor and mentee when an application changes.`

  ❌ Wrong: `This PR adds email notifications.\nNotifications are sent to both mentor\nand mentee when an application changes.`

- Always target `main` on `Women-Coding-Community/wcc-backend` (upstream)
- Never use `gh pr create` — fork permissions require the user to open the PR through the browser
- If the issue number cannot be found in branch name or commits, use `#?` and ask the user
- Never include change type checkboxes for types not present in the diff
- Never include the Screenshots section for pure test/docs/chore/ci changes
- Never include "I have tested my changes locally" for pure docs/chore/ci changes