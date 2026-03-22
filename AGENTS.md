# AGENTS.md — OpenAI Codex Workflow Guide

This file is read by OpenAI Codex (and compatible agents) to understand
how to work in this repository. Canonical skill definitions live in `.ai/skills/`.

## Project

WCC (Women Coding Community) Backend — Spring Boot 3.2.5, Java 21, PostgreSQL.

See `CLAUDE.md` for full architecture, build commands, and conventions.

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
