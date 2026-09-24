#!/usr/bin/env bash
#
# Dumps the production Postgres database via a `flyctl proxy` tunnel and
# writes a compressed, restorable pg_dump custom-format archive to the
# current directory.
#
# Required environment variables:
#   PROD_DB_HOST     Fly.io Postgres app name to proxy to (flyctl proxy -a)
#   PROD_DB_NAME     Database name to dump
#   PROD_DB_USER     Database user
#   PROD_DB_PASSWORD Database password
#   FLY_API_TOKEN    Fly.io API token with proxy/connect access to PROD_DB_HOST
#
# Usage:
#   ./scripts/db-backup.sh
#
# On success, writes ./wcc-prod-backup-<YYYY-MM-DD>.dump and prints its
# filename to stdout. Exits non-zero on any failure (unreachable database,
# missing tools, empty dump). Never prints credential values.

set -euo pipefail

readonly LOCAL_PROXY_PORT=5433
readonly PROXY_READY_TIMEOUT_SECONDS=30
readonly REQUIRED_VARS=(PROD_DB_HOST PROD_DB_NAME PROD_DB_USER PROD_DB_PASSWORD FLY_API_TOKEN)

PROXY_PID=""

log() {
  echo "[db-backup] $*" >&2
}

fail() {
  log "ERROR: $*"
  exit 1
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

check_required_vars() {
  local missing=()
  local var
  for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var:-}" ]; then
      missing+=("$var")
    fi
  done
  if [ "${#missing[@]}" -gt 0 ]; then
    fail "Missing required environment variable(s): ${missing[*]}"
  fi
}

start_proxy() {
  log "Opening flyctl proxy tunnel to ${PROD_DB_HOST}..."
  FLY_API_TOKEN="${FLY_API_TOKEN}" flyctl proxy "${LOCAL_PROXY_PORT}:5432" -a "${PROD_DB_HOST}" \
    >/tmp/db-backup-proxy.log 2>&1 &
  PROXY_PID=$!
}

wait_for_proxy() {
  local waited=0
  until (echo >"/dev/tcp/127.0.0.1/${LOCAL_PROXY_PORT}") 2>/dev/null; do
    if ! kill -0 "${PROXY_PID}" 2>/dev/null; then
      fail "flyctl proxy exited unexpectedly before becoming ready (check network/credentials, not the values)"
    fi
    waited=$((waited + 1))
    if [ "${waited}" -ge "${PROXY_READY_TIMEOUT_SECONDS}" ]; then
      fail "Timed out waiting for flyctl proxy to become ready after ${PROXY_READY_TIMEOUT_SECONDS}s"
    fi
    sleep 1
  done
  log "Proxy tunnel ready on 127.0.0.1:${LOCAL_PROXY_PORT}"
}

cleanup() {
  if [ -n "${PROXY_PID}" ] && kill -0 "${PROXY_PID}" 2>/dev/null; then
    log "Closing flyctl proxy tunnel..."
    kill "${PROXY_PID}" 2>/dev/null || true
    wait "${PROXY_PID}" 2>/dev/null || true
  fi
}

run_backup() {
  local backup_date
  backup_date="$(date -u +%Y-%m-%d)"
  local output_file="wcc-prod-backup-${backup_date}.dump"

  log "Starting pg_dump against ${PROD_DB_NAME}..."
  # --schema=public --no-acl: Fly.io managed Postgres adds a repmgr schema for
  # HA replication that a plain Postgres instance can't restore; scope the
  # dump to the application schema only (see docs/flyway_migration_troubleshooting.md).
  if ! PGPASSWORD="${PROD_DB_PASSWORD}" pg_dump \
    --host=127.0.0.1 \
    --port="${LOCAL_PROXY_PORT}" \
    --username="${PROD_DB_USER}" \
    --dbname="${PROD_DB_NAME}" \
    --schema=public \
    --no-acl \
    --format=custom \
    --compress=9 \
    --file="${output_file}"; then
    fail "pg_dump failed (database unreachable, connection dropped, or invalid credentials)"
  fi

  if [ ! -s "${output_file}" ]; then
    fail "Backup file ${output_file} was not created or is empty"
  fi

  log "Backup complete: ${output_file}"
  echo "${output_file}"
}

main() {
  check_required_vars

  command_exists flyctl || fail "flyctl is not installed or not on PATH"
  command_exists pg_dump || fail "pg_dump is not installed or not on PATH"

  trap cleanup EXIT

  start_proxy
  wait_for_proxy
  run_backup
}

main "$@"
