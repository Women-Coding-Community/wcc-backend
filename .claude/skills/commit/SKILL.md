---
name: commit
description: Safely commit changes. Reviews staged/modified files for sensitive data, writes a commit message with body. Never adds co-author lines.
disable-model-invocation: true
allowed-tools: Bash
---

# Commit Skill

You are now in commit mode. Follow these steps **in order**, stopping if any check fails.

## Step 1: Gather status and diff

Run these in parallel:
- `git status --short`
- `git diff HEAD`
- `git log --oneline -5`

If there are no staged or modified files, tell the user there is nothing to commit and stop.

## Step 2: Scan for sensitive data

Review ALL output from Step 1 carefully. Look for any of the following in file names or diff content:

**Sensitive patterns to detect:**
- API keys, tokens, secrets (patterns like `sk-`, `ghp_`, `xoxb-`, `AKIA`, `Bearer `, `token`, `api_key`, `apikey`)
- Passwords or credentials (`password`, `passwd`, `secret`, `credential`)
- Private keys or certificates (-----BEGIN, `.pem`, `.key`, `.p12`, `.pfx`)
- Environment files (`.env`, `.env.local`, `.env.production`, any `.env*`)
- AWS/cloud credentials (`aws_access_key`, `aws_secret`, `client_secret`)
- Database connection strings with credentials
- Hard-coded IP addresses or internal hostnames with credentials
- SSH private keys

**If ANY sensitive data is detected:**

Stop immediately and output:

```
WARNING: Sensitive data detected — commit aborted.

Found in: <file(s)>
Issue: <brief description of what was found>

Please remove the sensitive data before committing. Consider using environment variables or a secrets manager instead.
```

Do NOT proceed further.

## Step 3: Draft the commit message

Analyze the diff and status to write a commit message:

- **Subject line**: 50-72 chars, imperative mood, no period at end
- **Blank line**
- **Body**: Explain *why* the change is being made, not just what. Describe context, motivation, and any trade-offs. Minimum 2-3 sentences.

Format:
```
<subject line>

<body explaining why and context>
```

**NEVER include any Co-Authored-By, Co-authored-by, or similar trailer lines.**

Show the draft commit message to the user before proceeding.

## Step 4: Stage files

Stage the specific files using `git add <files>` (never use `git add .` or `git add -A` — always add files by name to avoid accidentally including sensitive files).

## Step 5: Create the commit

Run:

```bash
git commit -m "$(cat <<'EOF'
<subject line>

<body>
EOF
)"
```

**Do NOT append any Co-Authored-By or similar lines to the commit message.**

After committing, run `git status` to confirm success.

## Rules summary

- Never use `git add .` or `git add -A`
- Never include Co-Authored-By or any co-author lines in commit messages
- Always write a commit body (not just a subject line)
- Stop immediately if sensitive data is detected
- Never force push