# AGENTS.md — OpenAI Codex Workflow Guide

This file is read by OpenAI Codex (and compatible agents) to understand
how to work in this repository. Canonical skill definitions live in `.ai/skills/`.

## Project

WCC (Women Coding Community) Backend — Spring Boot 3.2.5, Java 21, PostgreSQL.

See `CLAUDE.md` for full architecture, build commands, and conventions.

## Skill Access

Repository-specific AI workflows live under `.ai/skills/` as canonical
markdown runbooks. When a user request matches one of the skills below, read
the corresponding file in `.ai/skills/` and follow that runbook.

Discovery pattern for Codex:

- Match the request to a skill name or workflow intent
- Open `.ai/skills/<skill-name>.md`
- Execute the runbook while still respecting this file's repository rules

## Skills

### commit

Safe commit with sensitive-data detection and conventional commit messages.

Full runbook: [`.ai/skills/commit.md`](.ai/skills/commit.md)

**Quick reference:**

- Scan diff for secrets before staging anything
- Conventional Commits format: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`
- Always write a body explaining *why*, not just what
- Stage by file name, never `git add .`

### pr-review

Inline code review posted via GitHub CLI.

Full runbook: [`.ai/skills/pr-review.md`](.ai/skills/pr-review.md)

**Quick reference:**

- Use `gh pr diff` and `gh api` for inline comments
- Prioritise: regressions → security → data integrity → conventions → tests
- Post comments on exact changed lines, not just a summary
- Java 21 idioms, Spring Boot conventions, Given-When-Then test names

### pre-commit-review

Local review of staged and unstaged changes before committing.

Full runbook: [`.ai/skills/pre-commit-review.md`](.ai/skills/pre-commit-review.md)

**Quick reference:**

- Review local diff before commit with regression-first mindset
- Auto-fix style issues in changed files where the runbook allows it
- Report only findings that require human judgement
- Enforce Java test naming and `@DisplayName` conventions

### open-pr

Prepare a pull request title and description from the current branch diff.

Full runbook: [`.ai/skills/open-pr.md`](.ai/skills/open-pr.md)

**Quick reference:**

- Derive PR title from the dominant conventional-commit change type
- Fill the repo PR template with only applicable sections
- Include Swagger screenshots for backend API changes
- Print the resulting GitHub PR URL for the user

### pull-request

Pull request workflow runbook for repository PR creation and description.

Full runbook: [`.ai/skills/pull-request.md`](.ai/skills/pull-request.md)

**Quick reference:**

- Use when the user asks for pull request preparation workflows
- Follow the canonical markdown runbook under `.ai/skills/`
- Keep PR output aligned with repository templates and conventions

### split-commits

Split local changes into atomic commits grouped by logical concern.

Full runbook: [`.ai/skills/split-commits.md`](.ai/skills/split-commits.md)

**Quick reference:**

- Analyse the full uncommitted diff and group files by concern
- Keep feature, refactor, test, and chore changes separate where possible
- Re-scan each proposed commit for sensitive data before committing
- Suggest which commit groups should become separate PRs

## Build commands

```bash
./gradlew test                  # unit tests
./gradlew testIntegration       # integration tests (Docker required)
./gradlew check                 # all checks including coverage (min 70%)
./gradlew pmdMain pmdTest       # static analysis
```

## Important conventions

- No AI co-author lines in commit messages
- `@DisplayName` with Given-When-Then format for all Java tests
- AssertJ (`assertThat`) for all assertions
- Constructor injection, never field injection
- No `@Value` in service fields — use `@ConfigurationProperties` for groups
