#!/usr/bin/env bash
# --------------------------------------------------------------------------
# Manage the full local application stack (docker/docker-compose.qa.yml):
# PostgreSQL + MailHog + backend API + admin portal + public frontend, plus
# seed data.
#
# Usage:
#   scripts/app-stack.sh up   [--purge] [--no-seed] [--no-build] [--cycle <scenario>]
#   scripts/app-stack.sh down [--purge]
#   scripts/app-stack.sh purge
#   scripts/app-stack.sh seed [--cycle <scenario>]
#   scripts/app-stack.sh cycle <long-term|ad-hoc|both|none | open <cycle_id>>
#   scripts/app-stack.sh logs [service]
#   scripts/app-stack.sh ps
#
# Commands:
#   up      Build (unless --no-build), start in the background, wait until every
#           service is healthy, then run the seed (unless --no-seed).
#           --purge deletes the database volume first, giving a fresh, re-seeded
#           database — use it when a migration error stops the backend from
#           starting, or whenever you want to start from a clean state.
#   down    Stop and remove the containers. Data is kept unless --purge.
#   purge   Same as `down --purge`: remove containers, network AND the
#           postgres-data volume of THIS stack only. Nothing else on your
#           machine is touched.
#   seed    (Re-)run scripts/init-local-env.sh inside the stack. Idempotent.
#   cycle   Switch which mentorship cycle is open AND current, in place, without
#           reseeding: a scenario (long-term | ad-hoc | both | none) or
#           `open <cycle_id>` for one specific cycle (see scripts/seed-cycles.sh).
#   logs    Follow logs for all services or one service.
#
# Environment:
#   WCC_FRONTEND_CONTEXT  build context for the public frontend; a directory
#                         (default ../../wcc-frontend, i.e. a sibling checkout)
#                         or a git URL such as
#                         https://github.com/Women-Coding-Community/wcc-frontend.git
#   CYCLE_SCENARIO        default cycle scenario for `up` / `seed` (long-term)
#   POSTGRES_PORT         host port for PostgreSQL (default 5432); set e.g. 5433
#                         when a local PostgreSQL already uses 5432
# --------------------------------------------------------------------------
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
COMPOSE_FILE="${REPO_ROOT}/docker/docker-compose.qa.yml"

export WCC_FRONTEND_CONTEXT="${WCC_FRONTEND_CONTEXT:-${REPO_ROOT}/../wcc-frontend}"
export CYCLE_SCENARIO="${CYCLE_SCENARIO:-long-term}"
export POSTGRES_PORT="${POSTGRES_PORT:-5432}"

compose() {
  docker compose --ansi never --file "$COMPOSE_FILE" "$@"
}

# Prints the header comment of this file (up to the first non-comment line).
usage() {
  awk 'NR > 1 && !/^#/ { exit } NR > 1 && !/^# -+$/ { sub(/^# ?/, ""); print }' "${BASH_SOURCE[0]}"
}

die() { printf '❌  %s\n' "$*" >&2; exit 1; }

check_frontend_context() {
  case "$WCC_FRONTEND_CONTEXT" in
    http://*|https://*|git@*|ssh://*|git://*) return ;;
  esac
  if [[ ! -d "$WCC_FRONTEND_CONTEXT" ]]; then
    die "Public frontend not found at '${WCC_FRONTEND_CONTEXT}'.
    Clone https://github.com/Women-Coding-Community/wcc-frontend next to this repository,
    or point WCC_FRONTEND_CONTEXT at a checkout or a git URL, e.g.
      WCC_FRONTEND_CONTEXT=https://github.com/Women-Coding-Community/wcc-frontend.git $0 up"
  fi
}

# Parses the shared option flags into globals; leaves positional args in REST.
PURGE=false
SEED=true
BUILD=true
REST=()
parse_flags() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --purge)    PURGE=true ;;
      --no-seed)  SEED=false ;;
      --no-build) BUILD=false ;;
      --cycle)
        [[ $# -ge 2 ]] || die "--cycle needs a scenario (long-term|ad-hoc|both|none)"
        CYCLE_SCENARIO="$2"; shift ;;
      --cycle=*)  CYCLE_SCENARIO="${1#--cycle=}" ;;
      -h|--help)  usage; exit 0 ;;
      --*)        die "Unknown option: $1" ;;
      *)          REST+=("$1") ;;
    esac
    shift
  done
  export CYCLE_SCENARIO
}

cmd_purge() {
  echo "🧹 Removing containers, network and the postgres-data volume of this stack..."
  compose --profile seed down --volumes --remove-orphans
}

cmd_down() {
  if $PURGE; then cmd_purge; else compose --profile seed down --remove-orphans; fi
}

# Runs the one-shot seed container. Only the seed image is (re)built and the
# container starts with --no-deps: `compose run --build` would rebuild every
# image and `run` recreates dependencies whose config drifted, either of which
# restarts the backend or database under a running test suite.
run_seed() {
  compose build --quiet seed
  compose run --rm --no-deps seed "$@"
}

cmd_seed() {
  echo "🌱 Seeding (cycle scenario: ${CYCLE_SCENARIO})..."
  run_seed
}

cmd_cycle() {
  [[ $# -ge 1 ]] || die "Usage: $0 cycle <long-term|ad-hoc|both|none> | cycle open <cycle_id>"
  echo "🔁 Switching cycle: $*..."
  run_seed scripts/seed-cycles.sh "$@"
}

cmd_up() {
  check_frontend_context
  $PURGE && cmd_purge

  local up_args=(up --detach --wait)
  $BUILD && up_args+=(--build)
  echo "🚀 Starting the full stack (frontend context: ${WCC_FRONTEND_CONTEXT})..."
  compose "${up_args[@]}"

  if $SEED; then cmd_seed; fi

  cat <<EOF

✅  Stack is up:
    Backend API     http://localhost:8080   (Swagger: http://localhost:8080/swagger-ui/index.html)
    Admin portal    http://localhost:3000   (admin@wcc.dev / wcc-admin — see docs/qa_local_setup.md)
    Public website  http://localhost:3001
    MailHog inbox   http://localhost:8025
    PostgreSQL      localhost:${POSTGRES_PORT} (db: wcc, user: postgres)

    Cycle scenario: ${CYCLE_SCENARIO}   — switch with: $0 cycle <long-term|ad-hoc|both|none|open <id>>
    Stop:           $0 down            — wipe & restart: $0 up --purge
EOF
}

main() {
  local command="${1:-}"
  [[ -n "$command" ]] || { usage; exit 1; }
  shift
  parse_flags "$@"

  case "$command" in
    up)     cmd_up ;;
    down)   cmd_down ;;
    purge)  cmd_purge ;;
    seed)   cmd_seed ;;
    cycle)  cmd_cycle ${REST[@]+"${REST[@]}"} ;;
    logs)   compose logs --follow ${REST[@]+"${REST[@]}"} ;;
    ps)     compose ps ;;
    -h|--help|help) usage ;;
    *)      die "Unknown command: ${command}. Run '$0 --help'." ;;
  esac
}

main "$@"
