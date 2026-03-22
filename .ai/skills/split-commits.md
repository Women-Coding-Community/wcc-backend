# Skill: split-commits

Analyse all uncommitted changes, group them by logical concern, propose atomic
commits (one per group), commit each group in sequence, then suggest which
groups warrant separate pull requests.

## When to apply

Run this workflow when the user asks to split changes into separate commits,
wants help organising a large diff before committing, or invokes `/split-commits`.

---

## Step 1 — Gather the full diff

Run in parallel:

```bash
git status --short
git diff HEAD --stat     # per-file summary
git diff HEAD            # full content diff
git log --oneline -5     # recent commit context
```

If there are no staged or modified files, tell the user there is nothing to
split and stop.

---

## Step 2 — Scan for sensitive data

Before doing anything else, scan the full diff output for:

- API keys or tokens (`sk-`, `ghp_`, `xoxb-`, `AKIA`, `Bearer `, `api_key`)
- Passwords or credentials (`password`, `passwd`, `secret`, `credential`)
- Private keys (`-----BEGIN`, `.pem`, `.key`, `.p12`, `.pfx`)
- Environment files (`.env`, `.env.local`, `.env.production`, any `.env*`)
- AWS / cloud credentials (`aws_access_key`, `aws_secret`, `client_secret`)
- Database connection strings with embedded credentials
- SSH private keys

**If ANY sensitive data is found — stop and output:**

```
WARNING: Sensitive data detected — commit aborted.

Found in: <file(s)>
Issue: <brief description>

Please remove the sensitive data before committing.
Consider using environment variables or a secrets manager instead.
```

Do NOT proceed further.

---

## Step 3 — Group files by logical concern

Cluster the changed files into groups using these signals, in priority order:

### 3a — Domain / feature boundary
Files that belong to the same feature or entity should stay together:
- A service class + its unit test + its integration test = one group
- A controller + its test = same group as its service (if changed together)
- A DB migration + the domain model it enables = one group
- Config/skill files (`.ai/`, `.claude/`, `CLAUDE.md`) = their own group

### 3b — Change type (conventional commit prefix)
Do not mix `feat`/`fix` production changes with `chore`/`refactor`/`test`
changes unless they are inseparable:

| Files | Likely type |
|---|---|
| `src/main/**` with new behaviour | `feat` or `fix` |
| `src/main/**` restructured, same behaviour | `refactor` |
| `src/test/**` or `src/testInt/**` only | `test` |
| `src/main/resources/db/migration/**` | `feat` or `fix` |
| `.ai/**`, `.claude/**`, `CLAUDE.md` | `chore` |
| Documentation only | `docs` |

### 3c — PR separability
Mark a group as a **PR candidate** (independently openable as a PR) when:
- It is deployable / reviewable without depending on another group, **or**
- It has a different change type (`feat` vs `chore`) making review easier when
  separated

Mark a group as **depends on** another when it directly calls code introduced
in that group.

---

## Step 4 — Present the proposed split

Display a numbered list of groups. For each group:

```
Group <N> — <conventional-commit subject (50–72 chars)>
  Type:    feat | fix | refactor | test | chore | docs
  Files:   <list of files, one per line, indented>
  PR:      yes — independent | depends on Group <M> | no
  Why:     <one sentence explaining why these files belong together>
```

Then ask:

> Does this split look right? Reply **yes** to commit group by group, or
> describe adjustments and I will revise.

Do **not** write any commits until the user confirms.

---

## Step 5 — Commit each group in sequence

Work through confirmed groups one at a time. For each group:

### 5a — Sensitive data check
Re-scan only the files in this group. If sensitive data is found, skip this
group, warn the user, and continue with the next group.

### 5b — Draft the commit message

Write a Conventional Commits message:

- **Subject**: `<type>: <description>` — 50–72 chars, imperative mood, no
  trailing period
- **Blank line**
- **Body**: Explain *why*, not what. Minimum 2–3 sentences covering context,
  motivation, and trade-offs. Wrap every line at **72 characters**.

**Do NOT include Co-Authored-By, AI attribution, or any trailer lines.**

Show the draft to the user. If they suggest changes, revise and show again
before proceeding.

### 5c — Stage and commit

Stage files by explicit name — never `git add .` or `git add -A`:

```bash
git add <file1> <file2> ...
```

Commit using a heredoc to preserve formatting:

```bash
git commit -m "$(cat <<'EOF'
<subject line>

<body — each line ≤72 chars>
EOF
)"
```

Run `git status` after each commit to confirm success before moving to the
next group.

---

## Step 6 — Final summary and PR suggestions

After all groups are committed, output:

```
<N> commits created on branch <branch-name>:
  <short-sha>  <subject>
  <short-sha>  <subject>
  ...

PR suggestions:
  • <description of which commits to bundle into each PR and why>
  • Use /open-pr to generate the PR description for each branch.
```

If groups with `depends on` relationships exist, suggest the order in which to
open PRs and whether to stack them or wait for the dependency to merge first.

---

## Rules

- Never use `git add .` or `git add -A`
- Never include Co-Authored-By or AI attribution in commit messages
- Always write a commit body (not just a subject line)
- Stop immediately if sensitive data is detected
- Never force-push
- Do not create commits until the user confirms the grouping in Step 4
- If only one logical group exists, tell the user and suggest `/commit` instead
