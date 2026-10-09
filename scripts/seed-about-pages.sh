#!/usr/bin/env bash
# --------------------------------------------------------------------------
# Seed the "About Us" CMS pages for the LOCAL stack only.
#
# Creates one row in the `page` table for every page served by
# AboutController (/api/cms/v1/about/**), using the default content in
# src/main/resources/init-data/:
#
#   ABOUT_US         aboutUsPage.json         GET /api/cms/v1/about
#   TEAM             teamPage.json            GET /api/cms/v1/team
#   COLLABORATOR     collaboratorPage.json    GET /api/cms/v1/collaborators
#   CODE_OF_CONDUCT  codeOfConductPage.json   GET /api/cms/v1/code-of-conduct
#   PARTNERS         partnersPage.json        GET /api/cms/v1/partners
#   CELEBRATE_HER    celebrateHerPage.json    GET /api/cms/v1/celebrateHer
#
# Without a database row the public API serves the static JSON fallback, but
# the page cannot be edited (PUT /api/platform/v1/page returns 404), so the
# admin portal has nothing to update until the page has been seeded.
#
# Idempotent: a page that already exists (HTTP 409) is left untouched, so
# local edits survive a re-run. Pass --force to overwrite them with the
# init-data content instead (PUT).
#
# Refuses to run against anything other than the local stack (localhost,
# host.docker.internal or the springboot-app container) unless
# ALLOW_REMOTE=true.
#
# Runs from the host or inside the `seed` service of
# docker/docker-compose.qa.yml; init-local-env.sh calls it automatically.
# Needs bash, curl and jq.
#
# Usage:
#   ./scripts/seed-about-pages.sh            # create missing pages
#   ./scripts/seed-about-pages.sh --force    # also reset existing pages
#
# Configuration (env):
#   API_BASE        default http://localhost:8080/api
#   API_KEY         default local (security.api.key in application.yml)
#   ADMIN_EMAIL     default admin@wcc.dev
#   ADMIN_PASSWORD  default wcc-admin
#   INIT_DATA_DIR   default src/main/resources/init-data
#   ALLOW_REMOTE    default false
# --------------------------------------------------------------------------
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

API_BASE="${API_BASE:-http://localhost:8080/api}"
API_KEY="${API_KEY:-local}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@wcc.dev}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-wcc-admin}"
INIT_DATA_DIR="${INIT_DATA_DIR:-$SCRIPT_DIR/../src/main/resources/init-data}"
ALLOW_REMOTE="${ALLOW_REMOTE:-false}"

FORCE=false
case "${1:-}" in
  "")      ;;
  --force) FORCE=true ;;
  *)       echo "Usage: $0 [--force]" >&2; exit 2 ;;
esac

# pageType:file — pageType must match a PageType enum constant
ABOUT_PAGES=(
  "ABOUT_US:aboutUsPage.json"
  "TEAM:teamPage.json"
  "COLLABORATOR:collaboratorPage.json"
  "CODE_OF_CONDUCT:codeOfConductPage.json"
  "PARTNERS:partnersPage.json"
  "CELEBRATE_HER:celebrateHerPage.json"
)

TOKEN=""
RESP_STATUS=""
RESP_BODY=""

# ---------------------------------------------------------------- helpers ---

log()  { printf '➡️  %s\n' "$*"; }
ok()   { printf '✅  %s\n' "$*"; }
skip() { printf '↩️  %s\n' "$*"; }
die()  { printf '❌  %s\n' "$*" >&2; exit 1; }

require_tools() {
  local missing=()
  for tool in curl jq; do
    command -v "$tool" > /dev/null 2>&1 || missing+=("$tool")
  done
  [[ ${#missing[@]} -eq 0 ]] || die "Missing required tools: ${missing[*]}"
}

require_local() {
  [[ "$ALLOW_REMOTE" == "true" ]] && return
  [[ "$API_BASE" =~ ^https?://(localhost|127\.0\.0\.1|host\.docker\.internal|springboot-app)(:[0-9]+)?/ ]] \
    || die "API_BASE=${API_BASE} is not the local stack. Set ALLOW_REMOTE=true to override."
}

# api METHOD PATH [JSON_FILE_OR_STRING] — same contract as init-local-env.sh
api() {
  local method="$1" path="$2" data="${3:-}"
  local args=(-s -S -X "$method" "${API_BASE}${path}"
    -H "accept: application/json"
    -H "Content-Type: application/json"
    -H "X-API-KEY: ${API_KEY}")
  [[ -n "$TOKEN" ]] && args+=(-H "Authorization: Bearer ${TOKEN}")
  if [[ -n "$data" ]]; then
    if [[ -f "$data" ]]; then args+=(--data-binary "@${data}"); else args+=(--data-binary "$data"); fi
  fi

  local tmp
  tmp="$(mktemp)"
  RESP_STATUS="$(curl "${args[@]}" -o "$tmp" -w '%{http_code}')" || { rm -f "$tmp"; die "curl failed: $method $path"; }
  RESP_BODY="$(cat "$tmp")"
  rm -f "$tmp"
}

expect() {
  local pattern="$1" context="$2"
  [[ "$RESP_STATUS" =~ ^($pattern)$ ]] || die "$context → HTTP $RESP_STATUS: $RESP_BODY"
}

login() {
  log "Logging in as ${ADMIN_EMAIL}..."
  api POST /auth/login "$(jq -cn --arg e "$ADMIN_EMAIL" --arg p "$ADMIN_PASSWORD" '{email: $e, password: $p}')"
  expect 200 "login as ${ADMIN_EMAIL}"
  TOKEN="$(jq -r '.token // empty' <<< "$RESP_BODY")"
  [[ -n "$TOKEN" ]] || die "Login succeeded but no token in response"
}

# ------------------------------------------------------------------ steps ---

seed_page() {
  local page_type="$1" file="${INIT_DATA_DIR}/$2"
  [[ -f "$file" ]] || die "Missing ${file}"

  log "Creating ${page_type} page..."
  api POST "/platform/v1/page?pageType=${page_type}" "$file"
  case "$RESP_STATUS" in
    200|201) ok "${page_type} page created." ; return ;;
    409)     ;;
    *)       expect "201" "create ${page_type} page" ;;
  esac

  if ! $FORCE; then
    skip "${page_type} page already exists (use --force to reset it)."
    return
  fi
  api PUT "/platform/v1/page?pageType=${page_type}" "$file"
  expect 200 "reset ${page_type} page"
  ok "${page_type} page reset to init-data content."
}

# ------------------------------------------------------------------- main ---

echo "📄 WCC about pages seed — ${API_BASE}"
require_tools
require_local
login
for entry in "${ABOUT_PAGES[@]}"; do
  seed_page "${entry%%:*}" "${entry#*:}"
done
ok "About pages seeded."
