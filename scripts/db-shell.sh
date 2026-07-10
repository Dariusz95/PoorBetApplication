#!/usr/bin/env bash
#
# scripts/db-shell.sh
#
# Otwiera sesję psql wewnątrz kontenera wybranej bazy danych — bez potrzeby
# posiadania klienta `psql` zainstalowanego lokalnie i bez ręcznego wpisywania
# hasła (odczytywane z .env.dev/.env).
#
# Użycie:
#   scripts/db-shell.sh <auth|match|coupon|wallet>                   # interaktywny psql
#   scripts/db-shell.sh <auth|match|coupon|wallet> -c "SELECT ..."   # jedno zapytanie
#
# Działa zarówno lokalnie w dev (wykrywa .env.dev obok siebie i doklejane
# docker-compose.dev.yml), jak i na serwerze produkcyjnym w /opt/poorbet
# (tam jest tylko .env + docker-compose.prod.yml — patrz OPERATIONS.md).
#
# Dlaczego `docker compose exec`, a nie połączenie po porcie hosta?
# Porty baz w prod świadomie NIE są wystawione na zewnątrz (patrz
# .claude/rules/docker.md: "Nie wystawiaj portów bazy danych w środowisku
# prod"), więc jedyna droga podejrzenia produkcyjnej bazy to wejście przez
# kontener. W dev porty są wystawione (5432/5434/5435/5437), ale ten sam
# skrypt działa identycznie w obu środowiskach, więc nawyk się nie zmienia.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${APP_DIR:-$(cd "$SCRIPT_DIR/.." && pwd)}"

SERVICE="${1:-}"
if [[ -n "$SERVICE" ]]; then
  shift
fi

declare -A CONTAINER=(
  [auth]=user-db
  [match]=match-db
  [coupon]=coupon-db
  [wallet]=wallet-db
)
declare -A DB_USER_KEY=(
  [auth]=POSTGRES_AUTH_USER
  [match]=POSTGRES_MATCH_USER
  [coupon]=POSTGRES_COUPON_USER
  [wallet]=POSTGRES_WALLET_USER
)
declare -A DB_NAME_KEY=(
  [auth]=POSTGRES_AUTH_DB
  [match]=POSTGRES_MATCH_DB
  [coupon]=POSTGRES_COUPON_DB
  [wallet]=POSTGRES_WALLET_DB
)

if [[ -z "$SERVICE" || -z "${CONTAINER[$SERVICE]:-}" ]]; then
  echo "Użycie: $0 <auth|match|coupon|wallet> [dodatkowe argumenty psql, np. -c \"SELECT ...\"]" >&2
  echo "Dostępne bazy: ${!CONTAINER[*]}" >&2
  exit 1
fi

# --- wykrycie środowiska: dev (.env.dev, obok docker-compose.dev.yml) -----
# vs prod (.env, obok docker-compose.prod.yml — tak jak na VPS w /opt/poorbet,
# patrz OPERATIONS.md sekcja 1). Dev ma pierwszeństwo, bo lokalnie zwykle
# istnieją oba pliki (np. .env skopiowany ręcznie do testów).
if [[ -f "$APP_DIR/.env.dev" ]]; then
  ENV_FILE="$APP_DIR/.env.dev"
  COMPOSE_FILES=(-f "$APP_DIR/docker-compose.yml" -f "$APP_DIR/docker-compose.dev.yml")
  ENV_LABEL="dev"
elif [[ -f "$APP_DIR/.env" ]]; then
  ENV_FILE="$APP_DIR/.env"
  COMPOSE_FILES=(-f "$APP_DIR/docker-compose.yml" -f "$APP_DIR/docker-compose.prod.yml")
  ENV_LABEL="prod"
else
  echo "BŁĄD: brak .env.dev ani .env w $APP_DIR. Skonfiguruj środowisko — patrz CLAUDE.md." >&2
  exit 1
fi

DB_USER="$(grep -E "^${DB_USER_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2-)"
DB_NAME="$(grep -E "^${DB_NAME_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2-)"

if [[ -z "$DB_USER" || -z "$DB_NAME" ]]; then
  echo "BŁĄD: nie udało się odczytać ${DB_USER_KEY[$SERVICE]} / ${DB_NAME_KEY[$SERVICE]} z $ENV_FILE" >&2
  exit 1
fi

COMPOSE=(docker compose --env-file "$ENV_FILE" "${COMPOSE_FILES[@]}")

echo "==> [$ENV_LABEL] Łączenie z ${CONTAINER[$SERVICE]} (baza: $DB_NAME, user: $DB_USER)"
exec "${COMPOSE[@]}" exec "${CONTAINER[$SERVICE]}" psql -U "$DB_USER" -d "$DB_NAME" "$@"
