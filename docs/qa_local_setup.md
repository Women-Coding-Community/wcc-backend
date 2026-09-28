# QA Local Setup — Backend with Seeded Accounts

This guide gets the WCC backend running on your machine with a set of ready-made user
accounts — one per role — so you can test role-based and mentorship flows without creating
any data by hand.

It covers two ways to run the stack:

* **the whole application** — backend, admin portal, public website and seed data in one
  command (`./scripts/app-stack.sh up`). This is what the Playwright suite in
  [wcc-qa](https://github.com/Women-Coding-Community/wcc-qa) expects.
* **the backend only** — `./scripts/docker-up.sh` (the developer stack,
  `docker/docker-compose.yml`) when you run the admin portal yourself or only need the API.
  Note it seeds **only** the admin account — the QA role accounts come from the QA stack.

**Read it in order.** *What you get* through *Using the API* takes you from nothing to a
running backend, working logins and a successful API call. Everything after that is reference —
come back to it when you need it.

<!-- TOC -->

* [QA Local Setup — Backend with Seeded Accounts](#qa-local-setup--backend-with-seeded-accounts)
  * [What you get](#what-you-get)
  * [Prerequisites](#prerequisites)
  * [Quick start](#quick-start)
  * [Seeded accounts](#seeded-accounts)
  * [Using the API](#using-the-api)
  * [Seed data and mentorship cycles](#seed-data-and-mentorship-cycles)
  * [Resetting the environment](#resetting-the-environment)
  * [Verifying the seed](#verifying-the-seed)
  * [Notes and caveats](#notes-and-caveats)
  * [Reference](#reference)
  * [For contributors](#for-contributors)
  * [Troubleshooting](#troubleshooting)
  * [Next steps](#next-steps)

<!-- TOC -->

## What you get

The QA stack (`docker/docker-compose.qa.yml`) is the whole application:

* **the backend**, on `http://localhost:8080`
* **PostgreSQL**, storing data in a Docker volume that survives restarts
* **MailHog**, a mock mail server — outgoing email is captured at `http://localhost:8025`
  instead of being sent, so password-reset and notification flows can be tested safely
* **the admin portal** (`admin-wcc-app`), on `http://localhost:3000`
* **the public website** (`wcc-frontend`), on `http://localhost:3001`

plus a **seed** step that creates everything you need to test with — see
[Seed data and mentorship cycles](#seed-data-and-mentorship-cycles):

* **six login accounts**: admin, mentorship admin, leader, a plain member, a **long-term**
  mentor and an **ad-hoc** mentor (both with ACTIVE mentor profiles, eligible for matching)
* the MENTORS page and an **open mentorship cycle** (long-term by default, switchable)

## Prerequisites

* **Docker Desktop** running (`docker ps` should succeed).
* Ports **8080** (API), **5432** (Postgres), **1025/8025** (MailHog) free on your machine —
  plus **3000** (admin portal) and **3001** (public website) for the full stack. If a local
  PostgreSQL already uses 5432, set `POSTGRES_PORT=5433` (or any free port).
* For the full stack, a checkout of
  [wcc-frontend](https://github.com/Women-Coding-Community/wcc-frontend) **next to** this
  repository (`../wcc-frontend`). Don't have one? Set
  `WCC_FRONTEND_CONTEXT=https://github.com/Women-Coding-Community/wcc-frontend.git` and it is
  cloned during the build.

You don't need Java, Gradle, Node, a database, or SDKMAN — everything runs in containers. Those
are only needed if you want to run the apps straight from your IDE.

### Apple Silicon (M1–M4)

Nothing extra to do. You'll see this warning as the stack starts:

```
mailhog The requested image's platform (linux/amd64) does not match the detected host
platform (linux/arm64/v8) and no specific platform was requested
```

It's harmless — MailHog only ships an amd64 image, so it runs under emulation. The inbox works
normally at `http://localhost:8025`.

## Quick start

### Whole application (recommended for QA)

From the repository root:

```shell
./scripts/app-stack.sh up
```

The first build takes several minutes — it compiles the backend and both Next.js apps inside
containers. The script waits until every service is healthy, seeds the data, and prints the
URLs:

* Admin portal: `http://localhost:3000`
* Public website: `http://localhost:3001`
* API base: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* MailHog inbox: `http://localhost:8025`

```shell
./scripts/app-stack.sh down            # stop, keep data
./scripts/app-stack.sh up --purge      # wipe the database and start fresh
./scripts/app-stack.sh --help          # every command and flag
```

### Backend and database only

If you only need the API (or want to run the admin portal from source with hot reload), start
just the backend services of the same stack:

```shell
docker compose -f docker/docker-compose.qa.yml up --build -d --wait springboot-app
```

That starts PostgreSQL, MailHog and the backend. Only `admin@wcc.dev` exists at that point —
the other QA accounts (including both mentors), the MENTORS page and the open cycle come from the seed,
so run it from the host (needs `curl`, `jq`, `psql` and `argon2` — `brew install argon2`; use
`PGPORT=5433` if you changed `POSTGRES_PORT`):

```shell
./scripts/init-local-env.sh
```

Stop with `./scripts/app-stack.sh down`.

## Seeded accounts

All accounts use the password **`wcc-admin`**.

| Email                      | Role             | Member type | Notes                                                                        |
|----------------------------|------------------|-------------|------------------------------------------------------------------------------|
| `admin@wcc.dev`            | ADMIN            | MEMBER      | Bootstrapped by the backend itself (`app.seed.users` in `application.yml`)   |
| `mentorship-admin@wcc.dev` | MENTORSHIP_ADMIN | MEMBER      | Can approve/reject mentors and manage matches                                |
| `leader@wcc.dev`           | LEADER           | LEADER      |                                                                              |
| `mentor@wcc.dev`           | MENTOR           | MENTOR      | **Long-term** mentor: ACTIVE profile, `longTerm` availability, no ad-hoc     |
| `mentor-adhoc@wcc.dev`     | MENTOR           | MENTOR      | **Ad-hoc** mentor: ACTIVE profile, available every month, no long-term       |
| `member@wcc.dev`           | VIEWER           | MEMBER      | Plain member (`VIEWER` role — shown as `MEMBER` / *Member In Community*)     |

All of them except admin come from [`scripts/seed-data/`](../scripts/seed-data) (see
[How the seeding works](#how-the-seeding-works)). There is no other sample data — no extra
mentors, members or mentees — so what you see in the portal is exactly this list. The disabled
`sonali.learn.ai@gmail.com` row you may notice in the database is shipped by a Flyway migration
and cannot log in.

The same accounts log into the admin portal (`admin-wcc-app`) once it's pointed at
`http://localhost:8080`.

## Using the API

The easiest way to explore the API by hand is Swagger UI, at
`http://localhost:8080/swagger-ui/index.html`. It lists every endpoint, remembers your
credentials once you've authorized, and saves you the quoting headaches that come with
copying `curl` commands around. If you'd rather stay in the terminal, everything here works
there too — see [Using curl instead](#using-curl-instead).

**1. Log in.** Find **`POST /api/auth/login`**, click **Try it out**, and send:

```json
{ "email": "admin@wcc.dev", "password": "wcc-admin" }
```

Any [seeded account](#seeded-accounts) will log in, but start with `admin@wcc.dev` — it has
the widest access. Endpoints check your role as well as your token, so a narrower account will
run into a `403` later on.

Getting a token back means the seed worked. If every account fails to log in, something went
wrong earlier — [Verifying the seed](#verifying-the-seed) will tell you what.

The response holds the `token`, the account `roles`, and the linked `member` profile:

```json
{
  "token": "…",
  "expiresAt": "…",
  "roles": ["ADMIN"],
  "member": { "id": 1, "fullName": "QA Admin", "email": "admin@wcc.dev", … }
}
```

**2. Copy the token** — the value only, without the surrounding quotes and without a
`Bearer ` prefix. Swagger adds the prefix itself.

**3. Authorize.** Click **Authorize** at the top of the page. There are **two fields, both
empty**:

* `apiKey` — type `local`
* `bearerAuth` — paste the token

Fill in both, then confirm. Swagger won't fill the API key in for you, and it's an easy one
to miss.

**4. Call anything.** The padlock icons switch from open to **closed** once you're
authorized.

**To switch accounts**, log in again and re-open **Authorize** — click **Logout** in the
dialog first, then paste the new token. Using a token for the wrong role gives:

```json
{
  "status": 403,
  "message": "Role denied. User roles: [Mentor In Community], Required any of: [Platform Administrator, Platform Leader]"
}
```

Helpfully, that message names both what you have and what the endpoint wants, so it tells you
which account to switch to.

Tokens last 60 minutes (`security.token.ttl-minutes` in
[`application.yml`](../src/main/resources/application.yml)). If calls that worked a while ago
start coming back as `403 Invalid authentication`, the token has simply aged out — log in
again and re-authorize. `GET /api/auth/me` will tell you which account a token belongs to.

> Paste **only JSON** into request-body fields. Copying a whole `curl` fragment — including
> the `'` quotes around `-d '{…}'` — fails with
> `400 Unexpected character (''' (code 39))`.

## Seed data and mentorship cycles

`./scripts/app-stack.sh up` runs [`scripts/init-local-env.sh`](../scripts/init-local-env.sh)
after the backend is healthy. It is **idempotent** — re-run it any time with
`./scripts/app-stack.sh seed`. It creates, in this order:

| Step | What | Why |
|------|------|-----|
| QA accounts | the [seeded accounts](#seeded-accounts) except admin — created as members / mentors from [`scripts/seed-data/`](../scripts/seed-data), roles set with `PUT /api/auth/users/{id}/roles`, password set to `wcc-admin` (see [How the seeding works](#how-the-seeding-works)) | The role accounts the admin portal and the Playwright suite log in as. `admin@wcc.dev` is not seeded here — the backend bootstraps it. |
| MENTORS page | `POST /api/platform/v1/page?pageType=MENTORS` with [`mentorsPage.json`](../src/main/resources/init-data/mentorsPage.json) | Without a `MENTORS` row in the `page` table, `GET /api/cms/v1/mentorship/mentors` serves a static fallback with an **empty** mentor list. |
| Mentorship cycle | [`scripts/seed-cycles.sh`](../scripts/seed-cycles.sh) (direct SQL) | There is no API to create or reopen a cycle, and the cycles shipped by Flyway have expired registration windows. |
| Mentors | `mentor@wcc.dev` (long-term) and `mentor-adhoc@wcc.dev` (ad-hoc), registered then **accepted** as admin | Listed on the public mentors page — one per `mentorshipTypes` filter value — and targets for mentee applications. |
| Mentees | none shipped — drop a `mentee-<name>.json` into `scripts/seed-data/` to add one (applications reference mentors by email) | Registers against the open cycle; skipped when the scenario is `none`. |

### Switching the cycle scenario

Mentee registration only works while a cycle is OPEN **and** today is inside its registration
window, and the mentee's `mentorshipType` must match that cycle. The seed opens a cycle that
covers today; which type is open is the **cycle scenario**:

```shell
./scripts/app-stack.sh cycle long-term   # LONG_TERM open, AD_HOC closed   (default)
./scripts/app-stack.sh cycle ad-hoc      # AD_HOC open for this month, LONG_TERM closed
./scripts/app-stack.sh cycle none        # everything closed → registration returns "cycle is closed"
./scripts/app-stack.sh cycle both        # both open — see the caveat below
./scripts/app-stack.sh cycle open 7      # one specific cycle by id (e.g. October's ad-hoc), all others closed
```

Switching is a one-second database update: no restart, no reseed, and existing mentors and
mentees are untouched. `./scripts/app-stack.sh up --cycle ad-hoc` (or
`CYCLE_SCENARIO=ad-hoc`) picks the scenario for the initial seed.

> **Open is not the same as current.** The backend's "current cycle"
> (`GET /cycles/current`, mentee registration, the public `openCycle`) is the cycle whose
> status is *open* **and** whose registration window contains today. Setting a cycle to *open*
> from the admin portal changes only the status — a cycle whose registration dates are next
> month stays "not current" until that month. Every `cycle` command above also moves the
> registration window to `today − 7 … today + 30`, which is what makes the cycle current.
> Each command prints the year's cycles with a `today_in_window` column so you can see why a
> cycle is or isn't current; ids come from that table or `GET /cycles/all`.

> **`both` caveat.** `GET /cycles/current` and mentee registration look up *the* open cycle
> with `LIMIT 1` and no ordering, so with two open cycles they pick one unpredictably. Use
> `both` only for admin screens that list cycles; use `long-term` or `ad-hoc` for registration
> flows.

Verify the current state with `GET /api/platform/v1/admin/mentorship/cycles/current` (ADMIN or
MENTORSHIP_ADMIN), or look at `openCycle` in the public `GET /api/cms/v1/mentorship/mentors`.

> The scripts bypass the service's cycle status-transition rules on purpose. They are QA
> fixtures for the local Docker stack and must never be pointed at a shared environment.

---

*Everything below is reference material — dip in as needed.*

## Resetting the environment

The database lives in a Docker volume, so your accounts survive restarts. To wipe everything
and re-seed from scratch — after changing the seed config, after a failed Flyway migration, or
just to get a clean slate:

```shell
./scripts/app-stack.sh up --purge
```

> Both delete the `postgres-data` volume of **this project only**. The seeder **skips accounts
> that already exist**, so without a purge your changes to seeded users won't reach an existing
> database. `docker system prune -a --volumes` also works but wipes every unused image,
> container and volume on your machine — keep it as a last resort.

## Verifying the seed

Logging in is the real test — if you get a token back, the seed worked. This section is for
when you don't, and want to see how far the seeding got.

The seed prints one line per step and stops at the first failure with the HTTP status and
response body, so re-run it and read the output:

```shell
./scripts/app-stack.sh seed
```

A healthy run confirms every QA account with a real login:

```
✅  QA account mentorship-admin@wcc.dev ready (id: 7, roles: ["MENTORSHIP_ADMIN"]).
✅  QA account leader@wcc.dev ready (id: 5, roles: ["LEADER"]).
✅  QA account mentor@wcc.dev ready (id: 4, roles: ["MENTOR"]).
✅  QA account mentor-adhoc@wcc.dev ready (id: 3, roles: ["MENTOR"]).
✅  QA account member@wcc.dev ready (id: 6, roles: ["VIEWER"]).
```

If it fails at *Logging in as admin@wcc.dev*, the backend's own bootstrap did not run — check
`docker logs springboot-app 2>&1 | grep -i seeded` for `Seeded user: admin@wcc.dev` (first
start) or `Reset seeded user credentials: admin@wcc.dev` (every later start; it reads like a
warning but means the bootstrap worked).

You can also query the database directly:

```shell
docker exec postgres psql -U postgres -d wcc -c \
  "SELECT ua.email, ua.enabled, array_agg(rt.name) AS roles \
   FROM user_accounts ua \
   LEFT JOIN user_roles ur ON ur.user_id = ua.id \
   LEFT JOIN role_types rt ON rt.id = ur.role_id \
   GROUP BY ua.email, ua.enabled ORDER BY ua.email;"
```

You'll see a fifth row, `sonali.learn.ai@gmail.com`, with `enabled = f` — an old account that
can't log in. That's expected, nothing to clean up.

## Notes and caveats

* **The mentor is already ACTIVE.** It's activated directly, so no approval email goes out.
  If you want to test the approval flow instead, use the mentor `accept`/`reject` endpoints
  under `PATCH /api/platform/v1/mentors/{mentorId}/accept|reject` — they need the
  `MENTOR_APPROVE` permission, held by ADMIN and MENTORSHIP_ADMIN.
* **Run one stack at a time.** The QA compose shares container names, ports and volume with
  the developer stack `docker-compose.yml` (`./scripts/docker-up.sh`), so they can't both be up
  at once.
* **Local only.** The seed scripts and their plaintext passwords are for local testing, and
  must never be pointed at a deployed environment.

## Reference

### Authentication

Two separate mechanisms guard the API, and each fails with a different error — which is
handy, because the error tells you which one you've missed. Locally the API key is `local`
(`security.api.key` in [`application.yml`](../src/main/resources/application.yml)).

| Path                    | `X-API-KEY` | `Authorization: Bearer` |
|-------------------------|-------------|-------------------------|
| `/api/auth/**`          | not needed  | only after login        |
| `/api/cms/v1/**`        | **required**| not needed              |
| `/api/platform/v1/**`   | **required**| **required**            |

* Missing or wrong API key → `401 {"error":"Unauthorized","message":"Invalid API Key"}`
* Valid API key, missing or expired token → `403 {"message":"Invalid authentication"}`
* Valid token, but the account lacks the role → `403 {"message":"Role denied. User roles: […],
  Required any of: […]"}`

Check the status code first — a `401` is always the API key. Then read the `403` message:
*Invalid authentication* is the token, *Role denied* means you're logged in as the wrong
account, and it helpfully names the roles the endpoint will accept.

Swagger declares the two as separate schemes
([`OpenApiConfig`](../src/main/java/com/wcc/platform/configuration/OpenApiConfig.java)), which
is why its **Authorize** dialog has a field for each, and why neither comes pre-filled.

### Using curl instead

Everything in [Using the API](#using-the-api) works from the terminal too. Start by logging
in:

```shell
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@wcc.dev","password":"wcc-admin"}'
```

Then send the token **and** the API key on every call after that. Swagger handles both for you
once you've authorized; with `curl` you have to pass them each time:

```shell
curl -s http://localhost:8080/api/platform/v1/mentors \
  -H "X-API-KEY: local" \
  -H "Authorization: Bearer <token>"
```

To create the mentors page from the terminal, run this from the repository root so the `@`
file reference resolves:

```shell
curl -X POST "http://localhost:8080/api/platform/v1/page?pageType=MENTORS" \
  -H "X-API-KEY: local" \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d @src/main/resources/init-data/mentorsPage.json
```

Other page types are listed in
[`PageType`](../src/main/java/com/wcc/platform/domain/cms/PageType.java), each with a starter
file in `src/main/resources/init-data/`.

## For contributors

This part is for anyone changing how the seeding works, rather than just using it.

### How the seeding works

No QA account lives in the application source. The backend only bootstraps `admin@wcc.dev`
(`app.seed.users` in `application.yml`, applied by `DevAdminSeeder` on every start); everything
else is created by [`scripts/init-local-env.sh`](../scripts/init-local-env.sh) through the
public API, exactly as a real user would be:

1. **Members and mentors** — every `scripts/seed-data/member-*.json` is `POST`ed to
   `/api/platform/v1/members`, every `mentor-*.json` to `/api/platform/v1/mentors` (and then
   accepted). The backend provisions a user account for each (VIEWER for members, MENTOR for
   mentors).
2. **QA accounts** — for every entry in
   [`scripts/seed-data/qa-accounts.json`](../scripts/seed-data/qa-accounts.json) the script
   looks the account up by email, sets its exact roles with `PUT /api/auth/users/{id}/roles`,
   then sets the password to `QA_PASSWORD` (default `wcc-admin`). Passwords cannot be set via
   the API (only through the email reset flow), so that one step is an SQL `UPDATE` with an
   Argon2id hash produced by the `argon2` CLI in the same parameters the backend uses. Each
   account is then verified by logging in.
3. Cycles, the MENTORS page and mentees as described in
   [Seed data and mentorship cycles](#seed-data-and-mentorship-cycles).

### Adding or changing seeded users

1. Add a member or mentor payload to `scripts/seed-data/` — `member-<name>.json` (the shape of
   `POST /api/platform/v1/members`) or `mentor-<name>.json` (`POST /api/platform/v1/mentors`).
   Copy an existing file; the seed picks files up by prefix.
2. If it should be a **login account**, add it to `scripts/seed-data/qa-accounts.json`:

   ```json
   { "email": "new-user@wcc.dev", "roles": ["LEADER"], "createdBy": "member-new-user.json" }
   ```

   (`createdBy` is documentation only.) The password is always `QA_PASSWORD`.
3. Run `./scripts/app-stack.sh seed` — no rebuild, no database reset. Existing accounts are
   updated in place (roles replaced, password reset).

Valid `roles` values: `ADMIN`, `MENTORSHIP_ADMIN`, `LEADER`, `MENTOR`, `MENTEE`,
`CONTRIBUTOR`, `VIEWER`. Valid `memberTypes`: `DIRECTOR`, `COLLABORATOR`, `EVANGELIST`,
`LEADER`, `MENTEE`, `MENTOR`, `MEMBER`, `PARTNER`, `SPEAKER`, `VOLUNTEER`.

> **Watch out for member types that escalate privileges** — `DIRECTOR`, for example, maps to
> the ADMIN role, so anyone with that member type picks up admin permissions. Use `MEMBER` if
> you just want a neutral, read-only type.

## Troubleshooting

### Backend / Docker

| Symptom                                     | Cause / fix                                                                                     |
|---------------------------------------------|-------------------------------------------------------------------------------------------------|
| Build hangs at `FROM …` pulling an image    | Docker Desktop network is wedged — restart Docker Desktop, then retry.                            |
| Build dies mid-Gradle with no clear error   | On Apple Silicon, check the `Dockerfile` build stage is not an `-alpine` image (see [Apple Silicon](#apple-silicon-m1m4)). |
| Port already in use                         | The regular stack is running, or another process holds 8080/5432 — stop it and retry.             |
| `docker exec postgres …` → *no such container* | The stack isn't up, or containers were renamed — check `docker ps`.                             |

### Authentication

| Symptom                                   | Cause / fix                                                                                       |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `401 Unauthorized` on login               | Wrong password (must be `wcc-admin`) or the seed didn't run — check the logs.                        |
| `401 {"message":"Invalid API Key"}`       | The `X-API-KEY` is missing. In Swagger, fill the `apiKey` field in **Authorize**; in `curl`, add `-H "X-API-KEY: local"`. |
| Call works in Swagger but `401` in the terminal | Swagger sends both credentials once authorized; a copied `curl` has to send the API key explicitly. |
| Only `admin@wcc.dev` can log in            | The seed did not run (e.g. `up --no-seed`, or it failed) — run `./scripts/app-stack.sh seed`.        |
| `403 Invalid authentication` on a call that worked before | Token expired (60-minute TTL) — log in again and re-authorize.                       |
| `403 Role denied`                         | Logged in as the wrong account — the message names the roles the endpoint accepts. Log in as `admin@wcc.dev` and re-authorize. |
| Seeded user changes not taking effect     | Re-run `./scripts/app-stack.sh seed` — accounts are updated in place; a rebuild is not needed.        |
| Login works but `member` is missing       | The account has no linked member — verify the user exists in `user_accounts` with a member id.        |

### API requests

| Symptom                                                    | Cause / fix                                                                        |
|------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `400 Unexpected character (''' (code 39))`                 | A `curl` fragment was pasted into Swagger including its `'` quotes — paste only JSON. |
| `200 OK` but the `mentors` array is empty                  | The mentors CMS page doesn't exist, so the static-file fallback is served. Run `./scripts/app-stack.sh seed` (or `./scripts/init-local-env.sh` against a backend-only stack) — see [Seed data and mentorship cycles](#seed-data-and-mentorship-cycles). Not a cycle problem. |
| `409 Record already exists` creating a page                | The page is already in the database — nothing to fix. Use `PUT` to change an existing page. |
| `GET /cycles/current` returns `404`                        | No cycle is open with a registration window covering today. This does **not** hide mentors; it only gates mentee registration. Run `./scripts/app-stack.sh cycle long-term` (or `ad-hoc`). |
| Mentee registration → `Mentee mentorship type … does not match current cycle type` | The open cycle is the other type. Switch it: `./scripts/app-stack.sh cycle ad-hoc` / `long-term`. |
| Password-reset email never arrives                         | Check MailHog at `http://localhost:8025`; mail is never sent externally in local runs. |
| Reset email arrives for the wrong person                   | The member record still holds a placeholder email — update the member, then re-request. |

## Next steps

With the stack running and the accounts working, here's where to go next. Both use the same
[seeded accounts](#seeded-accounts):

* **Admin portal** — started by the full stack on `http://localhost:3000`. To run it from
  source instead (hot reload), use the backend-only stack and follow
  [`admin-wcc-app/README.md`](../admin-wcc-app/README.md).
* **Playwright test suite** — lives in
  [`Women-Coding-Community/wcc-qa`](https://github.com/Women-Coding-Community/wcc-qa). Its
  defaults (`API_HOST=http://localhost:8080`, `API_KEY=local`,
  `ADMIN_BASE_URL=http://localhost:3000`, the seeded `@wcc.dev` accounts) match the full stack
  — follow the setup instructions there.
