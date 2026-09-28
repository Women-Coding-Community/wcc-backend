#!/usr/bin/env bash
# --------------------------------------------------------------------------
# WCC backend seeding script for the local QA / full-application Docker stack.
#
# Seeds, in order:
#   1. the MENTORS CMS page (without it GET /api/cms/v1/mentorship/mentors
#      serves the static fallback and never lists mentors — issue #654)
#   2. the mentorship cycle scenario (scripts/seed-cycles.sh)
#   3. mentors from seed-data/mentor-*.json (registered, then accepted as admin)
#   4. members from seed-data/member-*.json
#   5. the QA login accounts listed in seed-data/qa-accounts.json: roles are
#      assigned via the API and the password is set to QA_PASSWORD (the only
#      thing the API cannot do, so it is one SQL UPDATE with an argon2 hash)
#   6. mentees from seed-data/mentee-*.json, if any, with applications to the
#      mentors (none are shipped at the moment)
#
# Only admin@wcc.dev pre-exists — the backend bootstraps it from
# `app.seed.users` in application.yml — and the script logs in as that account
# to create everything else. Every other QA account is an ordinary member or
# mentor created here; see seed-data/qa-accounts.json for the list.
#
# Idempotent: re-running it on a seeded database is a no-op (409 "already
# exists" responses are treated as success), so it is safe to run after every
# `up` and again whenever the cycle scenario changes.
#
# Runs from the host (defaults below) or inside the `seed` service of
# docker/docker-compose.qa.yml (env overrides). Needs bash, curl, jq, psql and
# argon2 (`brew install argon2` / `apk add argon2`).
#
# Usage:
#   ./scripts/init-local-env.sh
#   CYCLE_SCENARIO=ad-hoc ./scripts/init-local-env.sh
#
# Configuration (env):
#   API_BASE        default http://localhost:8080/api
#   API_KEY         default local (security.api.key in application.yml)
#   ADMIN_EMAIL     default admin@wcc.dev   (bootstrapped by the backend)
#   ADMIN_PASSWORD  default wcc-admin
#   QA_PASSWORD     default wcc-admin — password given to every qa-accounts.json entry
#   CYCLE_SCENARIO  long-term (default) | ad-hoc | both | none
#   DATA_DIR        default scripts/seed-data
#   INIT_DATA_DIR   default src/main/resources/init-data
#   PGHOST/PGPORT/PGUSER/PGPASSWORD/PGDATABASE  see scripts/seed-cycles.sh
# --------------------------------------------------------------------------
set -euo pipefail
shopt -s nullglob  # a seed-data group with no files is simply skipped

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

API_BASE="${API_BASE:-http://localhost:8080/api}"
API_KEY="${API_KEY:-local}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@wcc.dev}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-wcc-admin}"
QA_PASSWORD="${QA_PASSWORD:-wcc-admin}"
CYCLE_SCENARIO="${CYCLE_SCENARIO:-long-term}"
DATA_DIR="${DATA_DIR:-$SCRIPT_DIR/seed-data}"
INIT_DATA_DIR="${INIT_DATA_DIR:-$SCRIPT_DIR/../src/main/resources/init-data}"

# libpq connection for the SQL steps (defaults match docker-compose.qa.yml from the host)
export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5432}"
export PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-MFpFnhhICniFNPA}"
export PGDATABASE="${PGDATABASE:-wcc}"

# How long to wait for the backend to answer /actuator/health/ping
WAIT_ATTEMPTS="${WAIT_ATTEMPTS:-30}"
WAIT_INTERVAL_SECONDS="${WAIT_INTERVAL_SECONDS:-2}"

HEALTH_URL="${API_BASE%/api}/actuator/health/ping"

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
  for tool in curl jq psql argon2; do
    command -v "$tool" > /dev/null 2>&1 || missing+=("$tool")
  done
  [[ ${#missing[@]} -eq 0 ]] || die "Missing required tools: ${missing[*]}"
}

# api METHOD PATH [JSON_FILE_OR_STRING]
# Sends X-API-KEY always and Authorization: Bearer once logged in.
# Sets RESP_STATUS and RESP_BODY; never prints the token.
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

# expect STATUS_REGEX CONTEXT — fail with the response body unless the status matches
expect() {
  local pattern="$1" context="$2"
  [[ "$RESP_STATUS" =~ ^($pattern)$ ]] || die "$context → HTTP $RESP_STATUS: $RESP_BODY"
}

wait_for_api() {
  log "Waiting for the backend at ${HEALTH_URL}..."
  local attempt
  for ((attempt = 1; attempt <= WAIT_ATTEMPTS; attempt++)); do
    if curl -sf "$HEALTH_URL" | grep -q UP; then
      ok "Backend is up."
      return
    fi
    sleep "$WAIT_INTERVAL_SECONDS"
  done
  die "Backend did not become healthy after $((WAIT_ATTEMPTS * WAIT_INTERVAL_SECONDS))s"
}

login() {
  log "Logging in as ${ADMIN_EMAIL}..."
  api POST /auth/login "$(jq -cn --arg e "$ADMIN_EMAIL" --arg p "$ADMIN_PASSWORD" '{email: $e, password: $p}')"
  expect 200 "login as ${ADMIN_EMAIL}"
  TOKEN="$(jq -r '.token // empty' <<< "$RESP_BODY")"
  [[ -n "$TOKEN" ]] || die "Login succeeded but no token in response"
  ok "Logged in (roles: $(jq -c '.roles' <<< "$RESP_BODY"))."
}

# ------------------------------------------------------------------ steps ---

seed_mentors_page() {
  log "Creating MENTORS page..."
  api POST "/platform/v1/page?pageType=MENTORS" "${INIT_DATA_DIR}/mentorsPage.json"
  case "$RESP_STATUS" in
    200|201) ok "MENTORS page created." ;;
    409)     skip "MENTORS page already exists." ;;
    *)       expect "201" "create MENTORS page" ;;
  esac
}

seed_cycles() {
  log "Seeding mentorship cycles (scenario: ${CYCLE_SCENARIO})..."
  bash "${SCRIPT_DIR}/seed-cycles.sh" "$CYCLE_SCENARIO"
}

# Registers (or looks up) and accepts every scripts/seed-data/mentor-*.json.
# Fills MENTOR_IDS_JSON ({email: id}) for the mentee applications. Kept as a
# JSON string rather than a bash associative array so it runs on macOS bash 3.2.
MENTOR_IDS_JSON='{}'

lookup_mentor_id() {
  local email="$1"
  api GET /platform/v1/mentors
  expect 200 "list mentors"
  jq -r --arg e "$email" '.[] | select(.email == $e) | .id' <<< "$RESP_BODY" | head -n 1
}

seed_mentors() {
  local file email id
  for file in "${DATA_DIR}"/mentor-*.json; do
    email="$(jq -r '.email' "$file")"
    log "Registering mentor ${email}..."
    api POST /platform/v1/mentors "$file"
    case "$RESP_STATUS" in
      200|201)
        id="$(jq -r '.id' <<< "$RESP_BODY")"
        ok "Mentor ${email} registered (id: ${id})." ;;
      409)
        id="$(lookup_mentor_id "$email")"
        skip "Mentor ${email} already exists (id: ${id})." ;;
      *) expect "201" "register mentor ${email}" ;;
    esac
    [[ -n "$id" && "$id" != "null" ]] || die "Could not determine id for mentor ${email}"
    MENTOR_IDS_JSON="$(jq -c --arg e "$email" --argjson id "$id" '. + {($e): $id}' <<< "$MENTOR_IDS_JSON")"

    api PATCH "/platform/v1/mentors/${id}/accept"
    case "$RESP_STATUS" in
      200)  ok "Mentor ${email} accepted (ACTIVE)." ;;
      409)  skip "Mentor ${email} is already active." ;;
      *)    expect "200" "accept mentor ${email}" ;;
    esac
  done
}

seed_members() {
  local file email
  for file in "${DATA_DIR}"/member-*.json; do
    email="$(jq -r '.email' "$file")"
    log "Creating member ${email}..."
    api POST /platform/v1/members "$file"
    case "$RESP_STATUS" in
      200|201) ok "Member ${email} created." ;;
      409)     skip "Member ${email} already exists." ;;
      *)       expect "201" "create member ${email}" ;;
    esac
  done
}

# Argon2id hash in the encoded form Spring's Argon2PasswordEncoder verifies;
# parameters mirror the backend (m=65536, t=3, p=1, 32-byte hash, 16-byte salt).
argon2_hash() {
  local salt
  salt="$(head -c 12 /dev/urandom | od -An -tx1 | tr -d ' \n')"
  printf '%s' "$1" | argon2 "$salt" -id -t 3 -m 16 -p 1 -l 32 -e
}

lookup_user_id() {
  local email="$1"
  api GET /auth/users
  expect 200 "list users"
  jq -r --arg e "$email" '.[] | select(.email == $e) | .id' <<< "$RESP_BODY" | head -n 1
}

# Turns the members/mentors created above into login accounts: exact roles via
# the API, then the QA password (no API exists for that) and enabled = true via
# SQL. Finally proves each account can log in.
seed_accounts() {
  local accounts_file="${DATA_DIR}/qa-accounts.json"
  [[ -f "$accounts_file" ]] || { skip "No ${accounts_file} — skipping QA accounts."; return; }

  local email roles user_id hash
  while IFS=$'\t' read -r email roles; do
    log "Configuring QA account ${email} (roles: ${roles})..."
    user_id="$(lookup_user_id "$email")"
    [[ -n "$user_id" && "$user_id" != "null" ]] \
      || die "No user account for ${email} — was its member/mentor seed file created first?"

    api PUT "/auth/users/${user_id}/roles" "$(jq -cn --argjson r "$roles" '{roles: $r}')"
    expect 200 "set roles for ${email}"

    hash="$(argon2_hash "$QA_PASSWORD")"
    psql -q -v ON_ERROR_STOP=1 -v email="$email" -v hash="$hash" <<'SQL'
UPDATE user_accounts
SET password_hash = :'hash', enabled = true, updated_at = CURRENT_TIMESTAMP
WHERE email = :'email';
SQL

    local saved_token="$TOKEN"
    TOKEN=""
    api POST /auth/login "$(jq -cn --arg e "$email" --arg p "$QA_PASSWORD" '{email: $e, password: $p}')"
    TOKEN="$saved_token"
    expect 200 "login check for ${email}"
    ok "QA account ${email} ready (id: ${user_id}, roles: $(jq -c '.roles' <<< "$RESP_BODY"))."
  done < <(jq -r '.[] | [.email, (.roles | tojson)] | @tsv' "$accounts_file")
}

# Mentee registration only works against the currently open cycle and must use
# its mentorship type, so both are read back from the API rather than hardcoded.
seed_mentees() {
  local files=("${DATA_DIR}"/mentee-*.json)
  if [[ ${#files[@]} -eq 0 ]]; then
    skip "No mentee-*.json in ${DATA_DIR} — skipping mentee registrations."
    return
  fi
  if [[ "$CYCLE_SCENARIO" == "none" ]]; then
    skip "Cycle scenario is 'none' — skipping mentee registrations (registration is closed)."
    return
  fi

  api GET /platform/v1/admin/mentorship/cycles/current
  expect 200 "get current cycle"
  local cycle_type cycle_year
  cycle_type="$(jq -r '.mentorshipType' <<< "$RESP_BODY")"
  cycle_year="$(jq -r '.cycleYear | tostring' <<< "$RESP_BODY")"
  log "Open cycle: ${cycle_type} ${cycle_year} (id: $(jq -r '.cycleId' <<< "$RESP_BODY"))"

  local file email payload
  for file in "${DATA_DIR}"/mentee-*.json; do
    email="$(jq -r '.mentee.email' "$file")"
    log "Registering mentee ${email} for ${cycle_type} ${cycle_year}..."
    payload="$(jq -c --arg type "$cycle_type" --argjson year "$cycle_year" --argjson ids "$MENTOR_IDS_JSON" '
      {
        mentee: .mentee,
        mentorshipType: $type,
        cycleYear: $year,
        applications: [ .applications[]
          | select($ids[.mentorEmail] != null)
          | { mentorId: $ids[.mentorEmail], priorityOrder, whyMentor, applicationMessage } ]
      }' "$file")"
    api POST /platform/v1/mentees "$payload"
    case "$RESP_STATUS" in
      200|201) ok "Mentee ${email} registered." ;;
      409)     skip "Mentee ${email} already registered." ;;
      *)       expect "201" "register mentee ${email}" ;;
    esac
  done
}

print_summary() {
  echo
  log "Summary"
  api GET /cms/v1/mentorship/mentors
  expect 200 "public mentors page"
  printf '   Public mentors listed : %s\n' "$(jq '.mentors | length' <<< "$RESP_BODY")"
  printf '   Open cycle (public)   : %s\n' "$(jq -c '.openCycle // "none"' <<< "$RESP_BODY")"
  api GET /platform/v1/admin/mentorship/cycles/current
  if [[ "$RESP_STATUS" == "200" ]]; then
    printf '   Current cycle (admin) : %s %s (%s → %s)\n' \
      "$(jq -r '.mentorshipType' <<< "$RESP_BODY")" "$(jq -r '.cycleYear | tostring' <<< "$RESP_BODY")" \
      "$(jq -r '.registrationStartDate' <<< "$RESP_BODY")" "$(jq -r '.registrationEndDate' <<< "$RESP_BODY")"
  else
    printf '   Current cycle (admin) : none open (HTTP %s)\n' "$RESP_STATUS"
  fi
  echo
  ok "Seed complete."
}

# ------------------------------------------------------------------- main ---

echo "🚀 WCC backend seed — ${API_BASE} (cycle scenario: ${CYCLE_SCENARIO})"
require_tools
wait_for_api
login
seed_mentors_page
seed_cycles
seed_mentors
seed_members
seed_accounts
seed_mentees
print_summary
