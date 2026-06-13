# Dev/QA Environment — Fly.io

The WCC DEV/QA backend environments runs on Fly.io with two apps that **stop automatically when idle
** to save costs:

| App                | Purpose               | URL                             |
|--------------------|-----------------------|---------------------------------|
| `wcc-backend-dev`  | Spring Boot API - DEV | https://wcc-backend-dev.fly.dev |
| `wcc-qa`           | Spring Boot API - QA  | https://wcc-qa.fly.dev          |
| `wcc-postgres-dev` | PostgreSQL database   | internal (no public URL)        |

All machines start automatically when needed and stop after a period of inactivity.

## Start Automatically

- [Access DEV Apis](https://wcc-backend-dev.fly.dev/swagger-ui/index.html)
- [Access QA Apis](https://wcc-qa.fly.dev/swagger-ui/index.html)

When both machines (backend and database) are stopped, startup takes **2–4 minutes**

**If your first request times out or returns a 503, wait 2–3 minutes and retry.** The apps will be
warm after that.

### The first request will be slow

1. An HTTP request arrives at for example: `wcc-backend-dev` → Fly.io starts the machine (~30s)
2. The backend starts and connects to Postgres → Fly.io starts the database machine (~60s)
3. Spring Boot runs Flyway migrations and finishes startup (~60s)
4. The first request is served

### Check Healthy Check

Then hit the health endpoint to confirm the backend is up

- Dev

```bash
curl https://wcc-backend-dev.fly.dev/actuator/health | jq '.'
```

- QA

```bash
curl https://wcc-qa.fly.dev/actuator/health | jq '.'
```

## Flyio commands for debug

If you have the [Fly CLI](https://fly.io/docs/flyctl/install/) installed, proceed with the next
steps for DEV or QA envs.

Follow this steps to manually start and test the machines status

```bash
# 1. Start the database first
fly machine start 287356eb640348 -a wcc-postgres-dev

# 2. Wait ~60 seconds for Postgres to be ready, then start the dev backend
fly machine start -a wcc-qa

# 3. Check both are running
fly status -a wcc-postgres-dev
fly status -a wcc-qa

# 4. Check both are running
fly status -a wcc-postgres-dev
fly status -a wcc-qa
```

Healthy Check

```bash
curl https://wcc-qa.fly.dev/actuator/health | jq '.'

```

### Reliable start DEV

```bash
# 1. Start the database first
fly machine start 287356eb640348 -a wcc-postgres-dev

# 2. Wait ~60 seconds for Postgres to be ready, then start the dev backend
fly machine start -a wcc-backend-dev

# 3. Check both are running
fly status -a wcc-postgres-dev
fly status -a wcc-backend-dev
```

Then hit the health endpoint to confirm the backend is up:

```bash
curl https://wcc-backend-dev.fly.dev/actuator/health
```

## For QA, Backend and Frontend Teams

### First request will be slow

When both machines are stopped, startup takes **2–4 minutes**:

1. An HTTP request arrives at `wcc-backend-dev` → Fly.io starts the machine (~30s)
2. The backend starts and connects to Postgres → Fly.io starts the database machine (~60s)
3. Spring Boot runs Flyway migrations and finishes startup (~60s)
4. The first request is served

**If your first request times out or returns a 503, wait 2–3 minutes and retry.** The apps will be
warm after that.

### Reliable start (recommended before a testing session)

If you have the [Fly CLI](https://fly.io/docs/flyctl/install/) installed:

```bash
# 1. Start the database first
fly machine start 287356eb640348 -a wcc-postgres-dev

# 2. Wait ~60 seconds for Postgres to be ready, then start the backend dev or qa
fly machine start -a wcc-backend-dev
fly machine start -a wcc-qa


# 3. Check both are running
fly status -a wcc-postgres-dev
fly status -a wcc-backend-dev
fly status -a wcc-qa
```

Then hit the health endpoint to confirm the backend is up:

```bash
curl https://wcc-backend-dev.fly.dev/actuator/health | jq '.' 
```

### How auto-stop works

- **DEV Backend** (`wcc-backend-dev`): stops after ~5 minutes with no HTTP traffic
- **QA Backend** (`wcc-qa`): stops after ~5 minutes with no HTTP traffic
- **Postgres** (`wcc-postgres-dev`): stops after ~5 minutes with no active TCP connections

Stopping the backend first causes Postgres to lose its connection and eventually stop too. You don't
need to manually stop anything — just walk away.

### Manual stop DEV (if you want to stop immediately)

```bash
fly machine stop -a wcc-backend-dev
fly machine stop -a wcc-qa
fly machine stop 287356eb640348 -a wcc-postgres-dev
```

### Manual stop QA (if you want to stop immediately)

```bash
fly machine stop -a wcc-qa
fly machine stop 287356eb640348 -a wcc-postgres-dev
```

## For the Backend Team

### Machine details

| App                | Machine ID                | Region | Size                  |
|--------------------|---------------------------|--------|-----------------------|
| `wcc-backend-dev`  | (managed by `fly deploy`) | lhr    | shared-cpu-1x / 512MB |
| `wcc-postgres-dev` | `287356eb640348`          | lhr    | shared-cpu-1x / 256MB |

### Configuration

Auto-stop is configured as follows:

**`fly-dev.toml`** (backend):

```toml
[http_service]
auto_stop_machines = 'stop'
auto_start_machines = true
min_machines_running = 0
```

**`wcc-postgres-dev` machine** (set via `fly machine update`):

- `autostop = stop`
- `autostart = true`

To reconfigure Postgres auto-stop:

```bash
fly machine update 287356eb640348 --autostop=stop --autostart -a wcc-postgres-dev -y
```
