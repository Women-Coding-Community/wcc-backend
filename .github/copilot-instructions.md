# GitHub Copilot Instructions

This repository is the WCC (Women Coding Community) Backend — Spring Boot 3.2.5, Java 21.

## Commit workflow

When asked to commit, follow the runbook in [`.ai/skills/commit.md`](../.ai/skills/commit.md):
- Scan the diff for sensitive data before staging
- Write a Conventional Commits message with a body explaining *why*
- Stage files by name, never `git add .`

## PR review workflow

When asked to review a pull request, follow [`.ai/skills/pr-review.md`](../.ai/skills/pr-review.md):
- Post inline comments on exact changed lines
- Check Java 21 idioms, Spring Boot conventions, and test coverage
- Keep comments concise, actionable, and encouraging

## Code conventions

- Java 21: prefer records, pattern matching, text blocks where appropriate
- Spring Boot: `@Service`, `@Repository`, `@Transactional` per layer conventions
- Tests: `@DisplayName("Given ..., when ..., then ...")` + AssertJ `assertThat`
- Constructor injection only — no `@Value` on fields, no `@Autowired` on fields
- No AI co-author lines in commit messages
