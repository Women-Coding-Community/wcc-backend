# QA Local Setup — Backend with Seeded Accounts

A guide for the QA team to run the WCC backend locally with a known set of pre-seeded
user accounts (one per role), so role-based and mentorship flows can be tested without
manually creating data.

<!-- TOC -->

* [QA Local Setup — Backend with Seeded Accounts](#qa-local-setup--backend-with-seeded-accounts)
  * [What you get](#what-you-get)
  * [Prerequisites](#prerequisites)
  * [Quick start](#quick-start)
  * [Seeded accounts](#seeded-accounts)
  * [Logging in](#logging-in)
  * [Verifying the seed](#verifying-the-seed)
  * [Resetting the environment](#resetting-the-environment)
  * [How the seeding works](#how-the-seeding-works)
  * [Adding or changing seeded users](#adding-or-changing-seeded-users)
  * [Notes and caveats](#notes-and-caveats)
  * [Troubleshooting](#troubleshooting)

<!-- TOC -->

## What you get

Running the QA stack starts the backend, a PostgreSQL database and a MailHog mock mail
server, and seeds **four user accounts** — one for each main role (Admin, Mentorship
Admin, Mentor, Leader). The mentor account is provisioned with an **ACTIVE mentor
profile**, so it appears in the admin portal's mentor list and is eligible for matching.

## Prerequisites

* **Docker Desktop** running (`docker ps` should succeed).
* Ports **8080** (API), **5432** (Postgres), **1025/8025** (MailHog) free on your machine.

No Java, Gradle or database installation is needed — everything runs in containers.

## Quick start

From the repository root:

```shell
docker compose -f docker/docker-compose.qa.yml up --build
```

The first build takes a few minutes (it compiles the app inside the container). When you
see the application start log, the API is available at:

* API base: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* MailHog inbox (outgoing emails): `http://localhost:8025`

To stop the stack, press `Ctrl+C`, or in another terminal:

```shell
docker compose -f docker/docker-compose.qa.yml down
```

## Seeded accounts

All accounts use the password **`wcc-admin`**.

| Email                      | Role             | Member type | Notes                                            |
|----------------------------|------------------|-------------|--------------------------------------------------|
| `admin@wcc.dev`            | ADMIN            | MEMBER      | Linked to its own generated member *QA Admin*    |
| `mentorship-admin@wcc.dev` | MENTORSHIP_ADMIN | MEMBER      | Can approve/reject mentors and manage matches    |
| `mentor@wcc.dev`           | MENTOR           | MENTOR      | Has an **ACTIVE** mentor profile                 |
| `leader@wcc.dev`           | LEADER           | LEADER      |                                                  |

## Logging in

Authenticate against the login endpoint to receive a bearer token:

```shell
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"mentor@wcc.dev","password":"wcc-admin"}'
```

The response contains a `token`, the account `roles`, and the linked `member` profile:

```json
{
  "token": "…",
  "expiresAt": "…",
  "roles": ["MENTOR"],
  "member": { "id": 5, "fullName": "QA Mentor", "email": "mentor@wcc.dev", … }
}
```

Use the token on subsequent calls:

```shell
curl -s http://localhost:8080/api/platform/v1/mentors \
  -H "Authorization: Bearer <token>"
```

The same credentials work to log into the admin portal (`admin-wcc-app`) when it is
pointed at `http://localhost:8080`.

## Verifying the seed

Check the application logs for the seeding messages:

```shell
docker logs springboot-app 2>&1 | grep -iE "Seeded|mentor profile"
```

Expected lines:

```
Seeded user: admin@wcc.dev (roles: [Platform Administrator])
Seeded user: mentorship-admin@wcc.dev (roles: [Mentorship Administrator])
Seeded ACTIVE mentor profile: mentor@wcc.dev (id: …)
Seeded user: leader@wcc.dev (roles: [Platform Leader])
```

Or query the database directly:

```shell
docker exec postgres psql -U postgres -d wcc -c \
  "SELECT ua.email, array_agg(rt.name) AS roles \
   FROM user_accounts ua \
   LEFT JOIN user_roles ur ON ur.user_id = ua.id \
   LEFT JOIN role_types rt ON rt.id = ur.role_id \
   GROUP BY ua.email ORDER BY ua.email;"
```

## Resetting the environment

The database is stored in a Docker volume, so accounts persist across restarts. To wipe
everything and re-seed from a clean state (for example after changing the seed config):

```shell
docker compose -f docker/docker-compose.qa.yml down -v
docker compose -f docker/docker-compose.qa.yml up --build
```

> The `-v` flag deletes the `postgres-data` volume. The seeder is idempotent and **skips
> accounts that already exist**, so without `-v` changes to seeded users will not be
> applied to an existing database.

## How the seeding works

* The QA stack runs Spring profiles `docker,qa` (set in
  [`docker/docker-compose.qa.yml`](../docker/docker-compose.qa.yml)).
* The `qa` profile loads
  [`src/main/resources/application-qa.yml`](../src/main/resources/application-qa.yml),
  which defines the seeded users under `app.seed.users`.
* On startup, `DevAdminSeeder` (an `ApplicationRunner`) reads that list and:
  * creates a member matching each user's email,
  * creates the user account with the configured password and roles,
  * for users with the `MENTOR` role, creates a full mentor profile and activates it.
* All four accounts (including admin) are defined in `app.seed.users`. The base
  `application.yml` seeds just the admin; the `qa` profile adds the remaining roles.

## Adding or changing seeded users

Edit `src/main/resources/application-qa.yml` and add an entry under `app.seed.users`:

```yaml
app:
  seed:
    users:
      - email: new-user@wcc.dev
        password: wcc-admin
        full-name: QA New User
        roles: [LEADER]          # one or more RoleType values
        member-types: [LEADER]   # optional; one or more MemberType values
```

After changing the file, rebuild with a clean database (see
[Resetting the environment](#resetting-the-environment)).

Valid `roles` values: `ADMIN`, `MENTORSHIP_ADMIN`, `LEADER`, `MENTOR`, `MENTEE`,
`CONTRIBUTOR`, `VIEWER`. Valid `member-types`: `DIRECTOR`, `COLLABORATOR`, `EVANGELIST`,
`LEADER`, `MENTEE`, `MENTOR`, `MEMBER`, `PARTNER`, `SPEAKER`, `VOLUNTEER`.

> **Avoid mapping a member type that escalates privileges** — for example `DIRECTOR` maps
> to the ADMIN role, so a member with that type would gain admin permissions. Use `MEMBER`
> for a neutral, read-only member type.

## Notes and caveats

* **Admin uses a generated profile.** The `admin@wcc.dev` account is always linked to its
  own generated member (*QA Admin*). Like all seeded accounts, it is defined as a list
  entry under `app.seed.users` — not separately configured.
* **Mentor is ACTIVE.** The seeded mentor is activated directly (no approval email is
  sent). To instead test the mentor approval flow, see the mentor `accept`/`reject`
  endpoints under `PATCH /api/platform/v1/mentors/{mentorId}/accept|reject` (requires the
  `MENTOR_APPROVE` permission — held by ADMIN and MENTORSHIP_ADMIN).
* **Single stack.** The QA compose uses the same container names, ports and volume as the
  regular `docker-compose.yml`, so run one or the other — not both at the same time.
* **Local only.** The `qa` profile and its plaintext passwords are intended for local
  testing and must never be activated in a deployed environment.

## Troubleshooting

| Symptom                                   | Cause / fix                                                                                  |
|-------------------------------------------|----------------------------------------------------------------------------------------------|
| Build hangs at `FROM …` pulling an image  | Docker Desktop network is wedged — restart Docker Desktop, then retry.                        |
| `401 Unauthorized` on login               | Wrong password (must be `wcc-admin`) or the seed didn't run — check the logs.                 |
| Seeded user changes not taking effect     | The account already exists; reset with `down -v` (see above).                                 |
| Port already in use                       | The regular stack is running, or another process holds 8080/5432 — stop it and retry.        |
| Login works but `member` is missing       | The account has no linked member — verify the user exists in `user_accounts` with a member id. |
