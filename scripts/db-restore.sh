#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${APP_DIR:-$(cd "$SCRIPT_DIR/.." && pwd)}"

SERVICE="${1:-}"
DUMP_FILE="${2:-}"

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

if [[ -z "$SERVICE" || -z "${CONTAINER[$SERVICE]:-}" || -z "$DUMP_FILE" ]]; then
  echo "Użycie: $0 <auth|match|coupon|wallet> <ścieżka-do-dumpa>" >&2
  echo "Dostępne bazy: ${!CONTAINER[*]}" >&2
  exit 1
fi

if [[ ! -f "$DUMP_FILE" ]]; then
  echo "BŁĄD: plik dumpa nie istnieje: $DUMP_FILE" >&2
  exit 1
fi

# --- wykrycie środowiska na podstawie pliku env -----
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

DB_USER="$(grep -E "^${DB_USER_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2- | tr -d '\r')"
DB_NAME="$(grep -E "^${DB_NAME_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2- | tr -d '\r')"

if [[ -z "$DB_USER" || -z "$DB_NAME" ]]; then
  echo "BŁĄD: nie udało się odczytać ${DB_USER_KEY[$SERVICE]} / ${DB_NAME_KEY[$SERVICE]} z $ENV_FILE" >&2
  exit 1
fi

COMPOSE=(docker compose --env-file "$ENV_FILE" "${COMPOSE_FILES[@]}")

echo "==> [$ENV_LABEL] Restore $DUMP_FILE -> ${CONTAINER[$SERVICE]} (baza: $DB_NAME, user: $DB_USER)"
if [[ "$ENV_LABEL" == "prod" ]]; then
  echo "!!! UWAGA: to jest środowisko PRODUKCYJNE. Baza '$DB_NAME' zostanie NADPISANA." >&2
fi
read -r -p "Kontynuować? Ta operacja nadpisze istniejące dane w '$DB_NAME' [y/N]: " CONFIRM
if [[ "$CONFIRM" != "y" && "$CONFIRM" != "Y" ]]; then
  echo "Przerwano." >&2
  exit 1
fi

# --clean --if-exists: usuwa istniejące obiekty przed odtworzeniem, żeby
# restore był idempotentny na już zainicjalizowanej bazie.
"${COMPOSE[@]}" exec -T "${CONTAINER[$SERVICE]}" \
  pg_restore -U "$DB_USER" -d "$DB_NAME" --clean --if-exists --no-owner < "$DUMP_FILE"

echo "==> Restore zakończony."
