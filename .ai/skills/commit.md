# Skill: commit

Safe commit workflow with sensitive-data detection and conventional commit messages.

## When to apply

Run this workflow when the user asks you to commit, stage and commit, or `/commit`.

## Step 1 — Gather context

Run in parallel:
- `git status --short`
- `git diff HEAD`
- `git log --oneline -5`

If there are no staged or modified files, tell the user there is nothing to commit and stop.

## Step 2 — Scan for sensitive data

Review ALL diff output carefully. Abort immediately if you find any of:

- API keys or tokens (`sk-`, `ghp_`, `xoxb-`, `AKIA`, `Bearer `, `api_key`, `apikey`, `token`)
- Passwords or credentials (`password`, `passwd`, `secret`, `credential`)
- Private keys or certificates (`-----BEGIN`, `.pem`, `.key`, `.p12`, `.pfx`)
- Environment files (`.env`, `.env.local`, `.env.production`, any `.env*`)
- AWS / cloud credentials (`aws_access_key`, `aws_secret`, `client_secret`)
- Database connection strings with credentials
- SSH private keys

**If sensitive data is found — stop and output:**

```
WARNING: Sensitive data detected — commit aborted.

Found in: <file(s)>
Issue: <brief description>

Please remove the sensitive data before committing.
Consider using environment variables or a secrets manager instead.
```

## Step 3 — Check Java test conventions (if Java test files are changed)

Scan the diff for any changed or added Java test files (`src/test/**`, `src/testInt/**`). For each, check:

| Rule | What to look for | Severity |
|---|---|---|
| `should` prefix | Method names starting with `test` instead of `should` | WARNING |
| `@DisplayName` present | Test methods missing `@DisplayName` annotation | WARNING |
| Given-When-Then format | `@DisplayName` text that does not follow `"Given …, when …, then …"` | WARNING |
| No inline phase comments | `// Arrange`, `// Act`, `// Assert` present in test body | INFO |
| `.getFirst()` preferred | `.get(0)` used on a `List` in new/changed test code | INFO |

If violations are found, list them clearly and ask the user whether to fix before committing or proceed as-is. Do not auto-fix without confirmation.

## Step 4 — Draft the commit message

Analyze the diff to write a commit message following Conventional Commits:

- **Subject**: `<type>: <description>` — 50–72 chars, imperative mood, no trailing period
  - Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `ci`
- **Blank line**
- **Body**: Explain *why* the change exists, not just what it does. Minimum 2–3 sentences covering context, motivation, and trade-offs.

Show the draft to the user before proceeding.

## Step 5 — Stage files

Stage by explicit file name:
```
git add <file1> <file2> ...
```

Never use `git add .` or `git add -A` — always name files to avoid accidentally including sensitive files.

## Step 6 — Create the commit

```
git commit -m "<subject>

<body>"
```

Do NOT add any co-author or AI-attribution trailer lines.

After committing, run `git status` to confirm.

## Rules

- Never `git add .` or `git add -A`
- Never include Co-Authored-By or AI attribution
- Always write a commit body, not just a subject
- Stop immediately on sensitive data
- Never force-push
