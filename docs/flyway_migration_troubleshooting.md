# Flyway Migration Troubleshooting

<!-- TOC -->

* [Flyway Migration Troubleshooting](#flyway-migration-troubleshooting)
    * [Checksum Mismatch](#checksum-mismatch)
        * [Cause](#cause)
        * [Fix: Repair in Production](#fix-repair-in-production)
    * [Testing a Migration Against a Production Backup](#testing-a-migration-against-a-production-backup)
        * [Step 1 — Dump the Production Database](#step-1--dump-the-production-database)
        * [Step 2 — Restore Locally](#step-2--restore-locally)
        * [Step 3 — Run Migrations Against the Restored Database](#step-3--run-migrations-against-the-restored-database)
        * [Step 4 — Verify](#step-4--verify)
        * [Step 5 — Clean Up](#step-5--clean-up)
    * [Writing Idempotent Migrations](#writing-idempotent-migrations)

<!-- TOC -->

---

## Checksum Mismatch

### Cause

Flyway stores a CRC32 checksum of each migration file when it is applied. If the file is modified afterward (even whitespace or comments), Flyway detects the mismatch on the next startup and refuses to proceed:

```
FlywayException: Validate failed:
Migration checksum mismatch for migration version 36
-> Applied to database : 123456789
-> Resolved locally    : 987654321
```

This commonly happens when:
- A migration file is edited after being committed and deployed.
- A migration is run manually against a database using a different version of the file than what ends up in source control.

### Fix: Repair in Production

`FlywayConfig.java` contains a conditional strategy that calls `flyway.repair()` before migrating. It is activated by setting an environment variable.

**1. Enable repair on the next deploy:**

```bash
fly secrets set FLYWAY_REPAIR_ON_MIGRATE=true -a wcc-backend
```

**2. Deploy:**

```bash
./gradlew clean bootJar
fly deploy
```

On startup, Flyway will recalculate and store the correct checksum for all migration files, then run any pending migrations.

**3. Disable repair immediately after (setting a secret triggers a redeploy):**

```bash
fly secrets unset FLYWAY_REPAIR_ON_MIGRATE -a wcc-backend
```

> **Important:** Only use repair when you are certain the migration file reflects what was actually applied to the database. Repair updates the recorded checksum — it does not re-run the migration.

---

## Testing a Migration Against a Production Backup

Before running a risky migration in production, restore a copy of the production database locally and run migrations against it.

### Step 1 — Dump the Production Database

Open a WireGuard proxy to the production Postgres instance and dump only the application schema:

```bash
# Start proxy in the background
fly proxy 15432:5432 -a wcc-postgres-prod &
sleep 3

# Dump the public schema only (excludes Fly.io internals like repmgr)
PGPASSWORD=<prod-db-password> pg_dump \
  -h localhost \
  -p 15432 \
  -U <prod-db-user> \
  -d <prod-db-name> \
  --schema=public \
  --no-acl \
  -Fc \
  -f prod_backup.dump
```

To retrieve the production DB credentials:

```bash
fly ssh console -a wcc-backend -C "env | grep SPRING_DATASOURCE"
```

> **Why `--schema=public`?** Fly.io managed Postgres includes a `repmgr` schema for high-availability replication. Dumping without this flag causes restore errors for extensions and tables that do not exist in a plain local Postgres instance.

### Step 2 — Restore Locally

Make sure your local Postgres container is running, then restore into a new database:

```bash
# Start local Postgres if not already running
docker compose -f docker/docker-compose.yml up postgres -d

# Create a clean target database
docker exec -it postgres psql -U postgres -c "DROP DATABASE IF EXISTS wcc_prod_test;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE wcc_prod_test;"

# Restore
PGPASSWORD=MFpFnhhICniFNPA pg_restore \
  -h localhost \
  -p 5432 \
  -U postgres \
  -d wcc_prod_test \
  --no-owner \
  --no-privileges \
  --schema=public \
  prod_backup.dump
```

### Step 3 — Run Migrations Against the Restored Database

Point the application at the restored database and enable repair if needed:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wcc_prod_test \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=MFpFnhhICniFNPA \
FLYWAY_REPAIR_ON_MIGRATE=true \
./gradlew bootRun
```

Watch the logs to confirm:
- Flyway repair runs (if enabled) and fixes any checksum mismatches.
- Pending migrations execute without errors.

### Step 4 — Verify

Connect to the restored database and confirm the migration results:

```bash
docker exec -it postgres psql -U postgres -d wcc_prod_test
```

```sql
-- Confirm migrations applied
SELECT version, script, success FROM flyway_schema_history ORDER BY installed_rank;

-- Spot-check your specific table changes
SELECT column_name, column_default, is_nullable
FROM information_schema.columns
WHERE table_name = 'mentee_applications';
```

### Step 5 — Clean Up

```bash
# Stop the Fly.io proxy
kill %1

# Remove the local backup file
rm prod_backup.dump
```

---

## Writing Idempotent Migrations

Migrations that drop or add constraints should be safe to run on any environment regardless of whether the change was already applied manually.

**Drop a constraint only if it exists:**

```sql
ALTER TABLE your_table
    DROP CONSTRAINT IF EXISTS constraint_name;
```

**Add a column only if it does not exist:**

```sql
ALTER TABLE your_table
    ADD COLUMN IF NOT EXISTS column_name data_type;
```

**Backfill nulls before setting NOT NULL:**

```sql
UPDATE your_table SET column_name = default_value WHERE column_name IS NULL;
ALTER TABLE your_table ALTER COLUMN column_name SET NOT NULL;
```

> **Rule of thumb:** if there is any chance a migration could be run manually on an environment before the migration file is deployed, write it with `IF EXISTS` / `IF NOT EXISTS` guards.
