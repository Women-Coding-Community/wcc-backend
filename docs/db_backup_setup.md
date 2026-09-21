# Production Database Backup Workflow

<!-- TOC -->

* [Production Database Backup Workflow](#production-database-backup-workflow)
    * [What this is](#what-this-is)
    * [Structure and flow](#structure-and-flow)
        * [Trigger](#trigger)
        * [Job steps](#job-steps)
        * [Concurrency](#concurrency)
        * [Storage and retention](#storage-and-retention)
        * [Failure notification](#failure-notification)
    * [Required secrets](#required-secrets)
        * [Provisioning the secrets](#provisioning-the-secrets)
    * [Running the job manually](#running-the-job-manually)
        * [Option A — GitHub UI](#option-a--github-ui)
        * [Option B — GitHub CLI](#option-b--github-cli)
        * [Downloading and inspecting a backup](#downloading-and-inspecting-a-backup)
    * [Running the script locally (troubleshooting)](#running-the-script-locally-troubleshooting)
    * [Troubleshooting a failed run](#troubleshooting-a-failed-run)
    * [Restoring from a backup](#restoring-from-a-backup)
    * [Out of scope](#out-of-scope)

<!-- TOC -->

---

## What this is

`.github/workflows/db-backup.yml` takes a full, compressed backup of the production
Postgres database on an unattended schedule and stores it as a GitHub Actions
artifact, so a bad migration or accidental deletion is recoverable without anyone
having remembered to run a manual backup. See
[specs/001-db-backup-workflow/spec.md](../specs/001-db-backup-workflow/spec.md) for
the full feature specification.

## Structure and flow

```text
 Backup Schedule (cron, 1st & 15th @ 03:00 UTC)
         │
         │  or: maintainer runs `workflow_dispatch` manually
         ▼
 ┌────────────────────────────────────────────────────────────┐
 │ Job: backup  (runs-on: ubuntu-latest)                       │
 │                                                              │
 │  1. Checkout code            (actions/checkout)              │
 │  2. Install flyctl           (superfly/flyctl-actions)       │
 │  3. Install postgresql-client (apt-get)                      │
 │  4. Run scripts/db-backup.sh                                 │
 │       a. Validate required secrets are present                │
 │       b. Open `flyctl proxy` tunnel to the Fly Postgres app   │
 │          (production DB is not publicly reachable)            │
 │       c. Wait for the tunnel to accept connections            │
 │       d. Run `pg_dump --format=custom --compress=9`           │
 │          against the forwarded local port                     │
 │       e. Close the tunnel (always, via trap on exit)          │
 │       f. Print the produced filename                          │
 │  5. Upload the .dump file as a workflow artifact              │
 │       (actions/upload-artifact, retention-days: 30)           │
 └────────────────────────────────────────────────────────────┘
         │
         ├─ success ──▶ Artifact appears under the run's "Artifacts" panel
         │
         └─ failure ──▶ GitHub's built-in workflow-failure notification
                         is emailed to the user who last edited the
                         workflow's schedule trigger
```

Any step failing (missing secret, unreachable database, `pg_dump` error, empty
dump, failed upload) makes the whole job fail loudly — nothing is allowed to
fail silently. Credential values are never echoed to the run log.

### Trigger

| Trigger | Fires | Inputs |
|---|---|---|
| `schedule` | `0 3 1,15 * *` — twice monthly, 03:00 UTC on the 1st and 15th | none |
| `workflow_dispatch` | On demand, by a maintainer | optional free-text `reason`, recorded in the run log for audit only |

### Job steps

The job has a single `backup` job with five steps, in order: checkout,
install `flyctl`, install `postgresql-client`, run `scripts/db-backup.sh`
(with DB and Fly credentials injected as `env` from repository secrets),
then upload the resulting `.dump` file as an artifact. See
[scripts/db-backup.sh](../scripts/db-backup.sh) for the backup logic itself —
it is also runnable standalone (see
[Running the script locally](#running-the-script-locally-troubleshooting)).

### Concurrency

`concurrency: { group: db-backup, cancel-in-progress: false }` ensures a
scheduled run and a manual run (or two manual runs) never execute at the
same time — a run that starts while another is in progress queues behind it
rather than corrupting/truncating a backup via concurrent writes.

### Storage and retention

Each successful run uploads one artifact named after the produced
`.dump` file (`wcc-prod-backup-<YYYY-MM-DD>.dump`), scoped to repository
collaborators (not public), with `retention-days: 30`. GitHub deletes
expired artifacts automatically — no custom cleanup step exists or is
needed, since the twice-monthly cadence keeps at most ~2 backups in storage
at steady state.

### Failure notification

No custom notification integration is used. A failed run reports **failure**
status, and GitHub Actions sends its built-in workflow-failure email
automatically — but only to the user who last edited the `schedule` trigger
in this workflow file, not to all repository maintainers or watchers. If
that person is no longer on the team, nobody is notified; treat this as a
single point of failure and check recent runs periodically
(`gh run list --workflow=db-backup.yml`) rather than relying solely on
email.

## Required secrets

Configure these as **repository secrets** (Settings → Secrets and variables →
Actions) before the workflow can run successfully:

| Secret | Purpose |
|---|---|
| `PROD_DB_HOST` | The Fly.io **Postgres app name** to proxy to (e.g. `wcc-postgres-prod`) — passed to `flyctl proxy -a`, not a hostname |
| `PROD_DB_NAME` | Database name to dump |
| `PROD_DB_USER` | Database user |
| `PROD_DB_PASSWORD` | Database password |
| `FLY_API_TOKEN_BACKUP` | A Fly.io API token scoped for proxy/connect access to the production Postgres app |

None of these are read by application code — they exist only for this
workflow and must never be committed to the repository or echoed to logs.

### Provisioning the secrets

```bash
# Database connection details (get these from the Fly Postgres app config,
# NOT from application-prod.yml — never copy secrets between files)
gh secret set PROD_DB_HOST --body "wcc-postgres-prod"
gh secret set PROD_DB_NAME --body "<prod-db-name>"
gh secret set PROD_DB_USER --body "<prod-db-user>"
gh secret set PROD_DB_PASSWORD --body "<prod-db-password>"

# A narrowly-scoped Fly API token, if Fly.io supports one for this app;
# otherwise fall back to the existing deploy token (see research.md decision 5)
fly tokens create deploy -a wcc-postgres-prod
gh secret set FLY_API_TOKEN_BACKUP --body "<token-output-above>"
```

> **Least privilege**: prefer a token scoped only to the Postgres app over
> reusing the deploy-scoped `FLY_API_TOKEN_PROD` secret already used by
> `fly-prod-deploy-backend.yml` — a read-only backup job doesn't need deploy
> permissions. If Fly.io doesn't support a narrower token, reusing
> `FLY_API_TOKEN_PROD` as `FLY_API_TOKEN_BACKUP`'s value is an acceptable,
> documented fallback (see [research.md](../specs/001-db-backup-workflow/research.md)
> decision 5).

## Running the job manually

Use this whenever you need an out-of-band backup — before a risky migration,
before a major deploy, or to recover from a missed/failed scheduled run.

### Option A — GitHub UI

**Actions → DB Backup → Run workflow** (optionally fill in the `reason`
field for the audit log) → **Run workflow**.

### Option B — GitHub CLI

```bash
gh workflow run db-backup.yml
# or, with a reason recorded in the run log:
gh workflow run db-backup.yml -f reason="pre-migration safety backup"

# Watch it run:
gh run watch --exit-status
```

### Downloading and inspecting a backup

```bash
# List recent runs and grab the run ID
gh run list --workflow=db-backup.yml

# Download the artifact from a specific run
# (gh nests it in a subdirectory named after the artifact, which is the
#  same as the dump filename — cd into it before the next command)
gh run download <run-id>
cd wcc-prod-backup-<date>.dump

# Confirm the dump is structurally valid (does not touch production)
pg_restore --list wcc-prod-backup-<date>.dump
```

## Running the script locally (troubleshooting)

`scripts/db-backup.sh` requires `flyctl` and `pg_dump` on `PATH`, and the
same five environment variables the workflow injects from secrets:

```bash
export PROD_DB_HOST=wcc-postgres-prod
export PROD_DB_NAME=<prod-db-name>
export PROD_DB_USER=<prod-db-user>
export PROD_DB_PASSWORD=<prod-db-password>
export FLY_API_TOKEN=<fly-api-token>

./scripts/db-backup.sh
```

On success it prints the produced filename
(`wcc-prod-backup-<YYYY-MM-DD>.dump`) and exits `0`. Use this to reproduce a
CI failure locally, or to take an ad-hoc backup without waiting on Actions.

## Troubleshooting a failed run

Open the failed run's log (**Actions → DB Backup → \<failed run\>**) and
check which step failed:

| Symptom in the log | Likely cause | Fix |
|---|---|---|
| `Missing required environment variable(s): ...` | A repository secret is unset or misnamed | Re-check [Required secrets](#required-secrets) and `gh secret list` |
| `flyctl proxy exited unexpectedly before becoming ready` | Invalid/expired `FLY_API_TOKEN_BACKUP`, or the Fly Postgres app name in `PROD_DB_HOST` is wrong | Verify the token with `fly auth whoami --access-token <token>`; confirm the app name with `fly apps list` |
| `Timed out waiting for flyctl proxy to become ready` | Fly.io network issue, or the runner can't reach Fly's edge | Re-run; if it persists, check [Fly.io status](https://status.flyio.net/) |
| `pg_dump failed (database unreachable, connection dropped, or invalid credentials)` | Wrong `PROD_DB_NAME`/`PROD_DB_USER`/`PROD_DB_PASSWORD`, or the database is down | Verify credentials; check the Fly Postgres app is running (`fly status -a wcc-postgres-prod`) |
| `Backup file ... was not created or is empty` | `pg_dump` produced a zero-byte file despite exiting 0 (rare) | Re-run; if it repeats, check runner disk space |
| Upload step fails after a successful dump | GitHub Actions storage issue | Re-run the job — the dump itself already succeeded, only the upload needs retrying |

The workflow never retries automatically (a stuck partial backup should not
silently overwrite a good one) — re-run manually via
[Option A or B above](#running-the-job-manually) once the root cause is
fixed.

## Restoring from a backup

Restoring is a separate, manual administrative procedure and is out of
scope for this workflow (see spec Assumptions). For restoring a downloaded
`.dump` into a local database for inspection or migration testing, see
[Testing a Migration Against a Production Backup](flyway_migration_troubleshooting.md#testing-a-migration-against-a-production-backup).

## Out of scope

- Restoring backups (see above) — this workflow only takes and retains them.
- Non-production environments — only the production database is backed up
  (FR-008).
- Selective/partial backups — every backup is a full dump of the `public`
  schema.
