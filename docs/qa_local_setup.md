# QA Local Setup — Backend with Seeded Accounts

This guide gets the WCC backend running on your machine with a set of ready-made user
accounts — one per role — so you can test role-based and mentorship flows without creating
any data by hand.

It's just the backend. The admin portal and the Playwright test suite are set up separately,
and both are linked from [Next steps](#next-steps) at the end.

**Read it in order.** *What you get* through *Making the mentor list work* takes you from
nothing to a running backend, working logins and a successful API call. Everything after that
is reference — come back to it when you need it.

<!-- TOC -->

* [QA Local Setup — Backend with Seeded Accounts](#qa-local-setup--backend-with-seeded-accounts)
  * [What you get](#what-you-get)
  * [Prerequisites](#prerequisites)
  * [Quick start](#quick-start)
  * [Seeded accounts](#seeded-accounts)
  * [Using the API](#using-the-api)
  * [Making the mentor list work](#making-the-mentor-list-work)
  * [Resetting the environment](#resetting-the-environment)
  * [Verifying the seed](#verifying-the-seed)
  * [Notes and caveats](#notes-and-caveats)
  * [Reference](#reference)
  * [For contributors](#for-contributors)
  * [Troubleshooting](#troubleshooting)
  * [Next steps](#next-steps)

<!-- TOC -->

## What you get

Running the QA stack starts three containers:

* **the backend**, on `http://localhost:8080`
* **PostgreSQL**, storing data in a Docker volume that survives restarts
* **MailHog**, a mock mail server — outgoing email is captured at `http://localhost:8025`
  instead of being sent, so password-reset and notification flows can be tested safely

It also seeds **four user accounts**, one for each main role (Admin, Mentorship Admin, Mentor,
Leader). The mentor account comes with an **ACTIVE mentor profile** and is eligible for
matching — though there's one extra step before it shows up in mentor lists, covered in
[Making the mentor list work](#making-the-mentor-list-work).

## Prerequisites

* **Docker Desktop** running (`docker ps` should succeed).
* Ports **8080** (API), **5432** (Postgres), **1025/8025** (MailHog) free on your machine.

You don't need Java, Gradle, a database, or SDKMAN — everything runs in containers. Those are
only needed if you want to run the app straight from your IDE.

### Apple Silicon (M1–M4)

Nothing extra to do. You'll see this warning as the stack starts:

```
mailhog The requested image's platform (linux/amd64) does not match the detected host
platform (linux/arm64/v8) and no specific platform was requested
```

It's harmless — MailHog only ships an amd64 image, so it runs under emulation. The inbox works
normally at `http://localhost:8025`.

## Quick start

From the repository root:

```shell
docker compose -f docker/docker-compose.qa.yml up --build
```

The first build takes a few minutes — it compiles the app inside the container, so this is
normal. Once you see the application start log, you'll find:

* API base: `http://localhost:8080`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* MailHog inbox: `http://localhost:8025`

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

## Making the mentor list work

The seeded mentor account is ACTIVE, but `GET /api/cms/v1/mentorship/mentors` comes back
`200 OK` with an empty `mentors` array. That looks like the seed didn't work — it did, and
here's what's actually happening.

Without a `MENTORS` row in the `page` table, the API falls back to the static
[`mentorsPage.json`](../src/main/resources/init-data/mentorsPage.json) — and that fallback
never injects the mentor list, because the file ships with `"mentors": []`. Creating the page
sorts it out, and you only need to do it once per database.

Log in as **`admin@wcc.dev`** first. Creating pages needs ADMIN or LEADER, so the mentor
account will get `403 Role denied` here.

In Swagger, open **`POST /api/platform/v1/page`** under *Platform: Pages*, set `pageType` to
`MENTORS`, and paste the **whole contents** of
`src/main/resources/init-data/mentorsPage.json` into the request body, replacing the `{}`.
There's no way to attach a file in Swagger, so pasting is the only option.

That file's `"mentors": []` looks wrong, but it's fine — the stored page keeps an empty list.
What matters is that a `MENTORS` row exists at all: that's what switches the API off the
fallback and onto the database path, and only that path fills in the live mentors.

To check it worked, call **`GET /api/cms/v1/mentorship/mentors`** — in Swagger it's under the
*Pages: Mentorship* tag. You should see the mentor listed, plus `openCycle` and
`filterSection`. Those two fields only ever come from the database path, so they're the quick
way to tell which one you're getting.

> A known gap, tracked as
> [#654](https://github.com/Women-Coding-Community/wcc-backend/issues/654) — delete this
> section once the mentors page is seeded by the Docker setup.

---

*Everything below is reference material — dip in as needed.*

## Resetting the environment

The database lives in a Docker volume, so your accounts survive restarts. To wipe everything
and re-seed from scratch — after changing the seed config, for instance:

```shell
docker compose -f docker/docker-compose.qa.yml down -v
docker compose -f docker/docker-compose.qa.yml up --build
```

> The `-v` flag deletes the `postgres-data` volume. The seeder **skips accounts that already
> exist**, so without `-v` your changes to seeded users won't reach an existing database.

## Verifying the seed

Logging in is the real test — if you get a token back, the seed worked. This section is for
when you don't, and want to see how far the seeding got.

Start with the application logs:

```shell
docker logs springboot-app 2>&1 | grep -iE "Seeded|mentor profile"
```

What you see depends on whether the database was already populated.

**First run**, or after a [reset](#resetting-the-environment) — the accounts are created:

```
Seeded user: admin@wcc.dev (roles: [Platform Administrator])
Seeded user: mentorship-admin@wcc.dev (roles: [Mentorship Administrator])
Seeded ACTIVE mentor profile: mentor@wcc.dev (id: 4)
Reset seeded user credentials: mentor@wcc.dev (roles: [Mentor In Community])
Seeded user: leader@wcc.dev (roles: [Platform Leader])
```

The mentor really does say *Reset* even on a brand-new database — creating its profile also
creates the account, so the seeder finds one already sitting there a moment later. Nothing is
wrong.

**Every restart afterwards** — all the accounts exist by then, so the seeder resets passwords
and roles rather than recreating anything, and there's no mentor-profile line:

```
Reset seeded user credentials: admin@wcc.dev (roles: [Platform Administrator])
Reset seeded user credentials: mentorship-admin@wcc.dev (roles: [Mentorship Administrator])
Reset seeded user credentials: mentor@wcc.dev (roles: [Mentor In Community])
Reset seeded user credentials: leader@wcc.dev (roles: [Platform Leader])
```

`Reset seeded user credentials` means the seed **worked**. It reads like a warning, but it
isn't one.

No seeding lines at all usually means the entries are missing `enabled: true`, which the
seeder skips silently — see [Adding or changing seeded users](#adding-or-changing-seeded-users).

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
  the regular `docker-compose.yml`, so they can't both be up at once.
* **Local only.** The `qa` profile and its plaintext passwords are for local testing, and
  must never be switched on in a deployed environment.

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

### Adding or changing seeded users

Edit `src/main/resources/application-qa.yml` and add an entry under `app.seed.users`:

```yaml
app:
  seed:
    users:
      - enabled: true            # required — entries without it are skipped silently
        email: new-user@wcc.dev
        password: wcc-admin
        full-name: QA New User
        roles: [LEADER]          # one or more RoleType values
        member-types: [LEADER]   # optional; one or more MemberType values
```

> **Don't leave out `enabled: true`.** A missing `enabled` flag is treated as disabled, and
> the entry is skipped without any error in the logs — which can leave a fresh database with
> no usable accounts at all. If login fails for *every* account, check this first.

Once you've changed the file, rebuild with a clean database (see
[Resetting the environment](#resetting-the-environment)) — otherwise your changes won't
reach the existing one.

Valid `roles` values: `ADMIN`, `MENTORSHIP_ADMIN`, `LEADER`, `MENTOR`, `MENTEE`,
`CONTRIBUTOR`, `VIEWER`. Valid `member-types`: `DIRECTOR`, `COLLABORATOR`, `EVANGELIST`,
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
| No accounts exist at all after a build    | The seeder skips entries without `enabled: true`. Every entry in `app.seed.users` needs it.          |
| `403 Invalid authentication` on a call that worked before | Token expired (60-minute TTL) — log in again and re-authorize.                       |
| `403 Role denied`                         | Logged in as the wrong account — the message names the roles the endpoint accepts. Log in as `admin@wcc.dev` and re-authorize. |
| Seeded user changes not taking effect     | The account already exists; reset with `down -v` (see above).                                        |
| Login works but `member` is missing       | The account has no linked member — verify the user exists in `user_accounts` with a member id.        |

### API requests

| Symptom                                                    | Cause / fix                                                                        |
|------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `400 Unexpected character (''' (code 39))`                 | A `curl` fragment was pasted into Swagger including its `'` quotes — paste only JSON. |
| `200 OK` but the `mentors` array is empty                  | The mentors CMS page doesn't exist, so the static-file fallback is served — see [Making the mentor list work](#making-the-mentor-list-work). Not a cycle problem. |
| `409 Record already exists` creating a page                | The page is already in the database — nothing to fix. Use `PUT` to change an existing page. |
| `GET /cycles/current` returns `404`                        | No mentorship cycle's registration window covers today — the seeded dates have expired. This does **not** hide mentors; it only gates mentee registration. There is no endpoint to reopen a cycle, so it needs a direct database update. |
| Password-reset email never arrives                         | Check MailHog at `http://localhost:8025`; mail is never sent externally in local runs. |
| Reset email arrives for the wrong person                   | The member record still holds a placeholder email — update the member, then re-request. |

### Admin portal

| Symptom                                               | Cause / fix                                                                   |
|-------------------------------------------------------|---------------------------------------------------------------------------------|
| `npm error … ENOENT … open '…/wcc-backend/package.json'` | `npm install` was run from the repo root — `cd admin-wcc-app` first.         |
| Admin portal loads but every request fails            | `NEXT_PUBLIC_API_BASE` missing or wrong in `admin-wcc-app/.env`.                |

## Next steps

With the backend running and the accounts working, here's where to go next. Both need this
stack up first, and both use the same [seeded accounts](#seeded-accounts):

* **Admin portal** — the Next.js app in `admin-wcc-app/`. The QA compose stack doesn't start
  it, so you'll need to run it yourself — see
  [`admin-wcc-app/README.md`](../admin-wcc-app/README.md).
* **Playwright test suite** — lives in
  [`Women-Coding-Community/wcc-qa`](https://github.com/Women-Coding-Community/wcc-qa);
  follow the setup instructions there.
