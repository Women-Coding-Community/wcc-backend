#!/usr/bin/env bash
# --------------------------------------------------------------------------
# Seed / switch the mentorship cycle scenario for the local QA stack.
#
# There is no API to create or reopen a cycle, so this writes to the
# mentorship_cycles table directly with psql. It deliberately bypasses the
# service's status-transition rules: this is a QA fixture for the local Docker
# stack only. Never run it against a shared environment.
#
# Usage:
#   scripts/seed-cycles.sh [long-term|ad-hoc|both|none]
#   scripts/seed-cycles.sh open <cycle_id>
#   CYCLE_SCENARIO=ad-hoc scripts/seed-cycles.sh
#
#   long-term (default)  LONG_TERM cycle OPEN for today, AD_HOC closed
#   ad-hoc               AD_HOC cycle for the current month OPEN, LONG_TERM closed
#   both                 both OPEN (GET /cycles/current and mentee registration
#                        pick one of them non-deterministically — use only for
#                        admin listing scenarios)
#   none                 every cycle closed (registration-closed scenarios)
#   open <cycle_id>      make that specific cycle (any type/month, e.g. the
#                        October ad-hoc one) OPEN and current: its registration
#                        window is moved to cover today and every other open
#                        cycle is closed. Use `GET /cycles/all` or the table this
#                        script prints to find ids.
#
# "Current" means status = open AND today inside the registration window —
# opening a cycle from the admin portal alone never makes it current if its
# registration dates are in the future or the past.
#
# "OPEN for today" means status = open and a registration window of
# today - 7 .. today + 30, so findOpenCycle() matches on any day the stack runs.
# Any other cycle that is still open is set to closed so the open one wins.
#
# Connection comes from the standard libpq variables (PGHOST, PGPORT, PGUSER,
# PGPASSWORD, PGDATABASE); defaults match docker/docker-compose.qa.yml from the
# host.
# --------------------------------------------------------------------------
set -euo pipefail

export PGHOST="${PGHOST:-localhost}"
export PGPORT="${PGPORT:-5432}"
export PGUSER="${PGUSER:-postgres}"
export PGPASSWORD="${PGPASSWORD:-MFpFnhhICniFNPA}"
export PGDATABASE="${PGDATABASE:-wcc}"

SCENARIO="${1:-${CYCLE_SCENARIO:-long-term}}"
TARGET_ID="${2:-}"

# cycle_statuses ids (V22): 1 draft, 2 open, 3 closed
STATUS_OPEN=2
STATUS_CLOSED=3
# mentorship_types ids (V10): 1 AD_HOC, 2 LONG_TERM
TYPE_AD_HOC=1
TYPE_LONG_TERM=2

print_cycles() {
  psql -q -v ON_ERROR_STOP=1 <<'SQL'
SELECT c.cycle_id, c.cycle_year, t.name AS type, c.cycle_month,
       c.registration_start_date, c.registration_end_date, s.name AS status,
       CURRENT_DATE BETWEEN c.registration_start_date AND c.registration_end_date AS today_in_window
FROM mentorship_cycles c
JOIN mentorship_types t ON t.id = c.mentorship_type
JOIN cycle_statuses s ON s.id = c.status
WHERE c.cycle_year = EXTRACT(YEAR FROM CURRENT_DATE)::int
ORDER BY c.mentorship_type DESC, c.cycle_month;
SQL
}

if [[ "$SCENARIO" == "open" ]]; then
  [[ "$TARGET_ID" =~ ^[0-9]+$ ]] || { echo "Usage: $0 open <cycle_id>" >&2; exit 2; }
  echo "Opening cycle ${TARGET_ID} for today on ${PGHOST}:${PGPORT}/${PGDATABASE}..."
  psql -q -v ON_ERROR_STOP=1 -v id="$TARGET_ID" \
    -v status_open="$STATUS_OPEN" -v status_closed="$STATUS_CLOSED" <<'SQL'
BEGIN;
-- \gset fails ("no rows returned") when the id does not exist, which aborts
-- the script under ON_ERROR_STOP before anything else is closed.
UPDATE mentorship_cycles
SET registration_start_date = CURRENT_DATE - 7,
    registration_end_date   = CURRENT_DATE + 30,
    cycle_start_date        = GREATEST(cycle_start_date, CURRENT_DATE + 31),
    cycle_end_date          = GREATEST(cycle_end_date, CURRENT_DATE + 60),
    status                  = :status_open,
    updated_at              = CURRENT_TIMESTAMP
WHERE cycle_id = :id
RETURNING cycle_id AS opened_cycle_id \gset

UPDATE mentorship_cycles
SET status = :status_closed, updated_at = CURRENT_TIMESTAMP
WHERE status = :status_open AND cycle_id <> :id;
COMMIT;
SQL
  print_cycles
  echo "Cycle ${TARGET_ID} is now open and current."
  exit 0
fi

case "$SCENARIO" in
  long-term) LT_STATUS=$STATUS_OPEN;   AH_STATUS=$STATUS_CLOSED ;;
  ad-hoc)    LT_STATUS=$STATUS_CLOSED; AH_STATUS=$STATUS_OPEN ;;
  both)      LT_STATUS=$STATUS_OPEN;   AH_STATUS=$STATUS_OPEN ;;
  none)      LT_STATUS=$STATUS_CLOSED; AH_STATUS=$STATUS_CLOSED ;;
  *)
    echo "Unknown cycle scenario '$SCENARIO'. Use one of: long-term, ad-hoc, both, none, open <cycle_id>" >&2
    exit 2
    ;;
esac

echo "Applying cycle scenario '$SCENARIO' on ${PGHOST}:${PGPORT}/${PGDATABASE}..."

psql -q -v ON_ERROR_STOP=1 \
  -v lt_status="$LT_STATUS" -v ah_status="$AH_STATUS" \
  -v type_lt="$TYPE_LONG_TERM" -v type_ah="$TYPE_AD_HOC" \
  -v status_open="$STATUS_OPEN" -v status_closed="$STATUS_CLOSED" <<'SQL'
BEGIN;

-- LONG_TERM: the application treats one long-term cycle per year as canonical
-- (repository create() updates by year + type), so reuse the year's row when it
-- exists and only insert one when it does not.
UPDATE mentorship_cycles
SET registration_start_date = CURRENT_DATE - 7,
    registration_end_date   = CURRENT_DATE + 30,
    cycle_start_date        = CURRENT_DATE + 31,
    cycle_end_date          = CURRENT_DATE + 210,
    status                  = :lt_status,
    updated_at              = CURRENT_TIMESTAMP
WHERE cycle_year = EXTRACT(YEAR FROM CURRENT_DATE)::int
  AND mentorship_type = :type_lt;

INSERT INTO mentorship_cycles
  (cycle_year, mentorship_type, cycle_month, registration_start_date, registration_end_date,
   cycle_start_date, cycle_end_date, status, max_mentees_per_mentor, description)
SELECT EXTRACT(YEAR FROM CURRENT_DATE)::int, :type_lt, EXTRACT(MONTH FROM CURRENT_DATE)::int,
       CURRENT_DATE - 7, CURRENT_DATE + 30, CURRENT_DATE + 31, CURRENT_DATE + 210,
       :lt_status, 5, 'QA seed: long-term cycle'
WHERE NOT EXISTS (
  SELECT 1 FROM mentorship_cycles
  WHERE cycle_year = EXTRACT(YEAR FROM CURRENT_DATE)::int AND mentorship_type = :type_lt
);

-- AD_HOC: one cycle per month; upsert the current month's row.
INSERT INTO mentorship_cycles
  (cycle_year, mentorship_type, cycle_month, registration_start_date, registration_end_date,
   cycle_start_date, cycle_end_date, status, max_mentees_per_mentor, description)
VALUES (EXTRACT(YEAR FROM CURRENT_DATE)::int, :type_ah, EXTRACT(MONTH FROM CURRENT_DATE)::int,
        CURRENT_DATE - 7, CURRENT_DATE + 30, CURRENT_DATE + 31, CURRENT_DATE + 60,
        :ah_status, 5, 'QA seed: ad-hoc cycle')
ON CONFLICT (cycle_year, mentorship_type, cycle_month) DO UPDATE
SET registration_start_date = EXCLUDED.registration_start_date,
    registration_end_date   = EXCLUDED.registration_end_date,
    cycle_start_date        = EXCLUDED.cycle_start_date,
    cycle_end_date          = EXCLUDED.cycle_end_date,
    status                  = EXCLUDED.status,
    updated_at              = CURRENT_TIMESTAMP;

-- Close every other open cycle so findOpenCycle() (LIMIT 1) is deterministic.
UPDATE mentorship_cycles
SET status = :status_closed, updated_at = CURRENT_TIMESTAMP
WHERE status = :status_open
  AND NOT (
        (mentorship_type = :type_lt AND cycle_year = EXTRACT(YEAR FROM CURRENT_DATE)::int
           AND :lt_status = :status_open)
     OR (mentorship_type = :type_ah AND cycle_year = EXTRACT(YEAR FROM CURRENT_DATE)::int
           AND cycle_month = EXTRACT(MONTH FROM CURRENT_DATE)::int AND :ah_status = :status_open)
  );

COMMIT;
SQL

print_cycles
echo "Cycle scenario '$SCENARIO' applied."
