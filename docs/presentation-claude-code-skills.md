# AI as Your Learning Buddy: Claude Code Skills in Practice

**Women Coding Community — Bootcamp: How to Use AI Tools and Feel Confident with the Code You Deliver**
[Meetup event](https://www.meetup.com/women-coding-community/events/313257077)

---

## Slide 1 — The confidence gap

> "I wrote the code. I think it works. But I'm not 100% sure it's right."

Sound familiar?

- You are not alone — even experienced developers feel this
- Code review anxiety is real: "What will my reviewer think?"
- The gap between *writing code* and *feeling confident about it* is where AI tools can help most

---

## Slide 2 — What we'll cover today

1. What are Claude Code skills?
2. The `/commit` skill — commit with intention
3. The `/pr-review` skill — review your own PR before anyone else does
4. How to use AI as a learning buddy for Java (or any language)
5. Live demo on a real open-source project
6. How to add skills to your own project

---

## Slide 3 — Claude Code Skills: the idea

**Skills are custom slash commands for your project.**

- You type `/commit` or `/pr-review` in your terminal
- Claude follows a predefined workflow specific to *your* codebase
- They live in `.claude/skills/<name>/SKILL.md`
- Claude Code discovers them automatically

Think of them as *runbooks* your AI pair programmer follows every time.

```
.claude/
  skills/
    commit/
      SKILL.md       ← defines what /commit does
    pr-review/
      SKILL.md       ← defines what /pr-review does
```

---

## Slide 4 — The `/commit` skill

**Problem it solves:** Commits that say "fix" or "update stuff" — and accidentally leak secrets.

**What it does step by step:**

| Step | Action |
|------|--------|
| 1 | Runs `git status` + `git diff HEAD` |
| 2 | **Scans for sensitive data** — API keys, passwords, `.env` files → aborts if found |
| 3 | Drafts a commit message: subject (imperative, ≤72 chars) + body (the *why*) |
| 4 | Shows draft for your review |
| 5 | Stages files **by name** (never `git add .`) |
| 6 | Commits and confirms |

**Example output:**
```
feat: Add inline PR review skill for Claude Code

Introduce /pr-review skill so contributors can get automated
inline feedback before submitting for human review. This reduces
back-and-forth in reviews and helps new contributors learn
project conventions faster.
```

---

## Slide 5 — The `/pr-review` skill

**Problem it solves:** You open a PR and wonder "did I miss anything obvious?"

**What it does:**

- Fetches PR diff and metadata via GitHub CLI (`gh`)
- Reviews with the codebase in mind:
  - Java/Spring Boot: Java 21 idioms, Spring patterns, naming
  - Tests: coverage gaps, missing integration tests
  - Security: data handling, auth, edge cases
- Posts **inline comments on the exact changed lines** (not just a summary)
- Keeps tone factual, actionable, and encouraging

**How to run it:**
```bash
/pr-review 541
# or
/pr-review https://github.com/Women-Coding-Community/wcc-backend/pull/541
```

---

## Slide 6 — Live demo: the WCC Backend project

**The Women Coding Community backend is open source.**
Repository: `Women-Coding-Community/wcc-backend`

We will demo on branch `claude_skills`:

```bash
# 1. Show the skill files
cat .claude/skills/commit/SKILL.md
cat .claude/skills/pr-review/SKILL.md

# 2. Run /commit on real staged changes
/commit

# 3. Run /pr-review on a real PR
/pr-review <pr-number>
```

Watch what Claude does:
- How it reads the diff
- How it decides what's a risk
- How it writes comments on the code lines

---

## Slide 7 — AI as your Java learning buddy

**The learning loop:**

```
Write Java code
      ↓
/commit
  → Claude drafts: "why does this change exist?"
  → If the explanation feels wrong, your code may not express your intent
      ↓
/pr-review
  → Claude flags: Spring Boot conventions, Java 21 idioms, missing tests
  → You fix before your teammates even see it
      ↓
Submit PR
  → Human review focuses on architecture, not typos and style
      ↓
Merge with confidence ✓
```

---

## Slide 8 — What you learn from each tool

### From `/commit`

- How to write code that is **self-explanatory**
- What "explain *why*, not *what*" means in practice
- Security hygiene: never committing secrets

### From `/pr-review`

- Java 21 features your code could use (records, pattern matching, sealed classes)
- Spring Boot conventions for this specific project
- How to write tests that actually cover edge cases
- What reviewers look for (and worry about) in production code

**You are not just shipping code faster — you are building a mental model of what good code looks like.**

---

## Slide 9 — How to add skills to your own project

**3 files, 5 minutes:**

```bash
mkdir -p .claude/skills/my-skill
```

Create `.claude/skills/my-skill/SKILL.md`:

```markdown
---
name: my-skill
description: What this skill does in one sentence
---

# My Skill

## Step 1: Gather context
Run `git status` and explain what you see.

## Step 2: Do the thing
...

## Step 3: Confirm
...
```

**That's it.** Claude Code picks it up automatically. Run `/my-skill`.

---

## Slide 10 — Tips for writing good skills

| Do | Don't |
|----|-------|
| Define steps in order | Leave steps ambiguous |
| State abort conditions ("stop if X") | Assume Claude will figure it out |
| Include example commands | Be too vague about what tools to use |
| Keep scope narrow (one job per skill) | Cram multiple workflows in one skill |
| Tailor to your codebase conventions | Write generic skills with no project context |

---

## Slide 11 — Going further

**Other skills you could build:**

- `/test-coverage` — run tests, open coverage report, flag uncovered lines
- `/db-migration` — scaffold a new Flyway migration from a description
- `/security-check` — scan for OWASP Top 10 patterns in changed files
- `/release-notes` — generate changelog from commits since last tag
- `/explain` — explain a class or method in plain English for learning

**The pattern is always the same:**
> Define the workflow once → reuse it every time → customize per project

---

## Slide 12 — Resources

- **Claude Code docs**: https://docs.anthropic.com/en/docs/claude-code
- **This demo branch**: `Women-Coding-Community/wcc-backend` → branch `claude_skills`
- **Skills guide in this repo**: `docs/claude-code-skills.md`
- **Conventional Commits**: https://www.conventionalcommits.org/
- **WCC Community**: https://www.meetup.com/women-coding-community/

---

## Slide 13 — Join us

Women Coding Community is open source and welcomes contributors at all levels.

- Star the repo: `Women-Coding-Community/wcc-backend`
- Pick a good-first-issue
- Run `/pr-review` on your own PR before submitting
- Join the community: https://www.meetup.com/women-coding-community/

**You already have an AI buddy. Use it to learn, not just to ship.**

---

*Presentation created for the WCC Bootcamp — AI Tools session, 2026*
