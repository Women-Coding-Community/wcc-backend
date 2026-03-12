---
name: pr-review
description: Review pull requests using GitHub CLI. Use when asked to check a PR, perform code review, or leave review comments. Focus on bugs, regressions, security, code quality, best practices, and missing tests; post comments inline on the relevant changed lines and avoid top-level summary comments unless explicitly requested.
---

# PR Review

> **Canonical runbook**: `.ai/skills/pr-review.md`
> This file is the Claude Code adapter. The workflow logic is defined in the canonical skill
> so it can be shared with other agents (Codex, Copilot, Cursor). Any changes to the workflow
> should be made in `.ai/skills/pr-review.md` first.

## Workflow

1. Read PR metadata and diff with GitHub CLI:
- Choose target repo based on PR URL:
- `gh pr view <number> --repo <owner/repo> --json title,headRefName,baseRefName,author,body,url`
- `gh pr diff <number> --repo <owner/repo>`

1. Review with a code-review mindset:
- Prioritize behavioral regressions, security, data integrity issues, and edge-case failures.
- Validate assumptions against nearby repository/service code before commenting.
- Keep summaries short; findings come first.
- Evaluate best practices and conventions relevant to the changed stack only.

1. Check conventions and quality for changed technologies:
- Java/Spring Boot changes: enforce Java 21 idioms, Spring Boot conventions, transaction/error-handling patterns, naming consistency, and clean-code principles.
- Frontend changes (React/TypeScript): check component boundaries, typing quality, state/effect hygiene, and project style conventions.
- Testing changes: assess test coverage around new logic and regression paths; call out missing unit/integration tests when risk is not covered.
- Do not raise unrelated style/framework guidance for technologies not touched by the PR.

1. Use inline comments on correlated source lines:
- Prefer PR review comments attached to exact changed lines.
- Do not leave only general/top-level comments when line-level comments are possible.
- If a comment was posted in the wrong place, repost at the correct line and remove the incorrect one.

1. Respect user review preferences from this repo context:
- Use the context into the repo from the PR URL for the PR operations.
- Avoid rerunning local tests when the user says they already tested, unless they ask for test execution.
- Keep comments concise and actionable.

## Useful Commands

- List changed files with patches:
`gh api repos/<owner>/<repo>/pulls/<number>/files --paginate`

- Get PR head SHA (needed for line comments via API):
`gh api repos/<owner>/<repo>/pulls/<number> -q .head.sha`

- Create line-anchored inline comment:
`gh api repos/<owner>/<repo>/pulls/<number>/comments -X POST -f commit_id='<sha>' -f path='<file>' -F position=<diff-position> -f body='<comment>'`

- Delete misplaced comments:
`gh api repos/<owner>/<repo>/pulls/comments/<comment-id> -X DELETE`
`gh api repos/<owner>/<repo>/issues/comments/<comment-id> -X DELETE`

## Comment Quality Bar

- State the concrete risk.
- Point to the exact condition/scenario where it fails.
- Suggest a minimal fix direction.
- Include a concrete suggested code snippet (or patch-style example) when feasible.
- Keep tone factual and collaborative.
- Keep the contributor motivated and engaged to learn and improve also to keep contribuiting in the opensource project
