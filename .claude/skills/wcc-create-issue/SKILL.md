---
name: wcc-create-issue
description: Create a GitHub issue in the Women-Coding-Community/wcc-backend upstream repository, add it to the Backend Platform project board, and set Epic, Status, and other project fields. Use when the user asks to create a GitHub issue, ticket, or task for this project.
argument-hint: "[title] or describe the issue interactively"
allowed-tools: Bash(gh *)
---

# Create GitHub Issue

Create an issue in the **upstream** repository and add it to the project board with the correct fields set.

## Project configuration

Load [project-config.md](project-config.md) before starting — it contains all field IDs, option IDs, labels, and milestones needed.

> **Critical**: Always use `Women-Coding-Community/wcc-backend` as the repo. Never create issues in a contributor's personal fork.

## Step 1 — Gather issue details

If `$ARGUMENTS` was provided, use it as the issue title and ask only for missing required fields. Otherwise ask the user for:

| Field     | Required | Notes                                               |
| --------- | -------- | --------------------------------------------------- |
| Title     | Yes      | Prefix with `feat:`, `fix:`, `chore:`, `docs:` etc. |
| Body      | Yes      | Use the template below                              |
| Labels    | Yes      | One or more from project-config.md labels list      |
| Epic      | Yes      | Choose from Epics table in project-config.md        |
| Status    | Yes      | Default: `Todo`                                     |
| Priority  | No       | Default: none                                       |
| Size      | No       | Default: none                                       |
| Milestone | No       | `MVP` or `Phase 2`                                  |

### Issue body template

```markdown
## Context
[Why this issue exists and what problem it solves]

## Changes Required
[What needs to be implemented — use bullet points or sub-sections]

## Acceptance Criteria
- [ ] [Criterion 1]
- [ ] [Criterion 2]

## Dependencies
[Other issues or tickets this depends on, if any]
```

## Step 2 — Create the issue

Run `gh issue create` with all gathered fields:

```bash
ISSUE_URL=$(gh issue create \
  --repo Women-Coding-Community/wcc-backend \
  --title "<title>" \
  --body "<body>" \
  --label "<label1>,<label2>" \
  [--milestone "<MVP or Phase 2>"])
echo "$ISSUE_URL"
```

## Step 3 — Add to project board and set fields

After creating the issue, run these steps in order:

### 3a — Get the issue node ID
```bash
ISSUE_NUMBER=$(echo "$ISSUE_URL" | grep -o '[0-9]*$')
ISSUE_ID=$(gh api "repos/Women-Coding-Community/wcc-backend/issues/$ISSUE_NUMBER" --jq '.node_id')
```

### 3b — Add issue to project
```bash
ITEM_ID=$(gh api graphql \
  -f query='mutation($pid:ID!,$cid:ID!){addProjectV2ItemById(input:{projectId:$pid,contentId:$cid}){item{id}}}' \
  -f pid="PVT_kwDOChRKeM4Ahm6k" \
  -f cid="$ISSUE_ID" \
  --jq '.data.addProjectV2ItemById.item.id')
```

### 3c — Set project fields

Use this mutation pattern for each field to set. Replace `FIELD_ID` and `OPTION_ID` with the values from project-config.md:

```bash
gh api graphql \
  -f query='mutation($pid:ID!,$iid:ID!,$fid:ID!,$val:String!){updateProjectV2ItemFieldValue(input:{projectId:$pid,itemId:$iid,fieldId:$fid,value:{singleSelectOptionId:$val}}){projectV2Item{id}}}' \
  -f pid="PVT_kwDOChRKeM4Ahm6k" \
  -f iid="$ITEM_ID" \
  -f fid="<FIELD_ID>" \
  -f val="<OPTION_ID>" > /dev/null
```

Always set at minimum: **Status** and **Epic**. Set Priority and Size only if the user provided them.

## Step 4 — Confirm

After all steps succeed, output:

```
Issue created: <ISSUE_URL>
Added to project board with:
  Epic: <epic name>
  Status: <status name>
  Priority: <priority name or "not set">
  Size: <size name or "not set">
  Milestone: <milestone name or "none">
```

## Error handling

- If `gh issue create` fails: report the error and stop — do not attempt to add to the project
- If project mutations fail: report which step failed; the issue already exists and can be added to the project manually
- If the user provides an invalid epic/label/milestone name: list the valid options from project-config.md and ask again