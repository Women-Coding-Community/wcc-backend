# WCC Admin Portal

Next.js admin frontend for the WCC platform. It manages CMS content, members, mentors and
mentorship matching by calling the backend API in this same repository.

## Prerequisites

- **Node.js 18 or newer** (`node -v`)
- **The backend running on `http://localhost:8080`.** The portal is a pure client — it has no
  database of its own and will not work without it. See
  [`docs/qa_local_setup.md`](../docs/qa_local_setup.md) to start the backend with pre-seeded
  accounts.

This app is **not** started by the Docker compose stacks; run it separately as below.

## Setup

```shell
cd admin-wcc-app
cp .env.example .env
npm install
npm run dev
```

The portal is then available at `http://localhost:3000`.

> Run `npm install` from **inside `admin-wcc-app/`**, not from the repository root. The root
> has no `package.json`, and npm will fail with
> `npm error … ENOENT … open '…/wcc-backend/package.json'`.

## Configuration

Edit `.env` after copying it:

| Variable               | Local value             | Notes                                        |
| ---------------------- | ----------------------- | -------------------------------------------- |
| `NEXT_PUBLIC_API_BASE` | `http://localhost:8080` | Backend URL.                                 |
| `NEXT_PUBLIC_API_KEY`  | `local`                 | **Required** — see below.                    |
| `NEXT_PUBLIC_APP_URL`  | `http://localhost:3000` | This app's own URL.                          |
| `PORT`                 | _(optional)_            | Overrides the dev-server port; default 3000. |

**Set `NEXT_PUBLIC_API_KEY=local`.** `.env.example` ships it empty, but the backend enables
API-key security by default (`security.enabled: true`, `security.api.key: local` in
`application.yml`) and rejects unkeyed requests to `/api/platform/**` and `/api/cms/**`.

The failure this causes is confusing: [`lib/api.ts`](lib/api.ts) only attaches the
`X-API-KEY` header when the value is non-empty, and the login endpoint does not require it —
so **login succeeds and then every screen fails with 401**. If that is what you are seeing,
this is why.

## Logging in

Use any of the accounts seeded by the QA backend stack — all with password `wcc-admin`:

| Email                      | Role             |
| -------------------------- | ---------------- |
| `admin@wcc.dev`            | ADMIN            |
| `mentorship-admin@wcc.dev` | MENTORSHIP_ADMIN |
| `mentor@wcc.dev`           | MENTOR           |
| `leader@wcc.dev`           | LEADER           |

Sessions last 60 minutes by default (`security.token.ttl-minutes`), after which calls start
returning 403 and you need to log in again.

## Available scripts

| Command                | Purpose                                    |
| ---------------------- | ------------------------------------------ |
| `npm run dev`          | Development server with hot reload.        |
| `npm run build`        | Production build.                          |
| `npm start`            | Serve a production build.                  |
| `npm test`             | Jest unit tests.                           |
| `npm run lint`         | Next.js ESLint checks.                     |
| `npm run format`       | Format with Prettier.                      |
| `npm run format:check` | Verify formatting without writing changes. |

## Troubleshooting

| Symptom                                                  | Cause / fix                                                                                     |
| -------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| `npm error … ENOENT … open '…/wcc-backend/package.json'` | `npm install` was run from the repository root — `cd admin-wcc-app` first.                      |
| Login works, then every page shows an error              | `NEXT_PUBLIC_API_KEY` is empty — set it to `local` and restart the dev server.                  |
| Every request fails immediately, including login         | Backend not running, or `NEXT_PUBLIC_API_BASE` is wrong.                                        |
| Port 3000 already in use                                 | Set `PORT=3001` in `.env`, or stop the other process.                                           |
| Mentor list is empty though a mentor exists              | Backend data issue, not the portal — see [`docs/qa_local_setup.md`](../docs/qa_local_setup.md). |

> Changes to `.env` are read at server start. Restart `npm run dev` after editing it.
