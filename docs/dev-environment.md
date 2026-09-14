# QA Environment — Fly.io

The WCC QA backend environment runs on Fly.io with machines that **stop automatically when idle** to save costs:

| App                | Purpose              | URL                    |
|--------------------|----------------------|------------------------|
| `wcc-qa`           | Spring Boot API - QA | https://wcc-qa.fly.dev |
| `wcc-postgres-dev` | PostgreSQL database  | internal (no public URL) |

All machines start automatically when needed and stop after a period of inactivity.

> **Note**: The standalone DEV environment (`wcc-backend-dev`) has been deprecated and decommissioned to reduce cloud infrastructure costs. For testing and development, use the local stack or the QA environment on Fly.io.

## Start Automatically

- [Access QA Swagger APIs](https://wcc-qa.fly.dev/swagger-ui/index.html)

When both machines (backend and database) are stopped, startup takes **2–4 minutes**:

1. An HTTP request arrives at `wcc-qa` → Fly.io starts the machine (~30s)
2. The backend starts and connects to Postgres → Fly.io starts the database machine (~60s)
3. Spring Boot runs Flyway migrations and finishes startup (~60s)
4. The first request is served

**If your first request times out or returns a 503, wait 2–3 minutes and retry.** The apps will be warm after that.

### Health Check

Hit the health endpoint to confirm the QA backend is up:

```bash
curl https://wcc-qa.fly.dev/actuator/health | jq '.'
```

## Fly.io Commands for QA Debugging

If you have the [Fly CLI](https://fly.io/docs/flyctl/install/) installed, follow these steps to manually manage machine status:

### Reliable Start (recommended before a testing session)

```bash
# 1. Start the database first
fly machine start 287356eb640348 -a wcc-postgres-dev

# 2. Wait ~60 seconds for Postgres to be ready, then start the QA backend
fly machine start -a wcc-qa

# 3. Check both are running
fly status -a wcc-postgres-dev
fly status -a wcc-qa
```

### How Auto-stop Works

- **QA Backend** (`wcc-qa`): stops after ~5 minutes with no HTTP traffic
- **Postgres** (`wcc-postgres-dev`): stops after ~5 minutes with no active TCP connections

Stopping the backend first causes Postgres to lose its connection and eventually stop too. You don't need to manually stop anything — just walk away.

### Manual Stop QA (if you want to stop immediately)

```bash
fly machine stop -a wcc-qa
fly machine stop 287356eb640348 -a wcc-postgres-dev
```

## Machine Details

| App                | Machine ID                | Region | Size                  |
|--------------------|---------------------------|--------|-----------------------|
| `wcc-qa`           | (managed by `fly deploy`) | lhr    | shared-cpu-1x / 512MB |
| `wcc-postgres-dev` | `287356eb640348`          | lhr    | shared-cpu-1x / 256MB |

### Configuration

Auto-stop is configured on `wcc-postgres-dev` machine (set via `fly machine update`):

- `autostop = stop`
- `autostart = true`

To reconfigure Postgres auto-stop:

```bash
fly machine update 287356eb640348 --autostop=stop --autostart -a wcc-postgres-dev -y
```
