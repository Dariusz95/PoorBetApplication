#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${APP_DIR:-$(cd "$SCRIPT_DIR/.." && pwd)}"
BACKUP_DIR="${BACKUP_DIR:-$APP_DIR/backups}"

SERVICE="${1:-}"
OUTPUT_FILE="${2:-}"

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
  echo "Użycie: $0 <auth|match|coupon|wallet> [ścieżka-pliku-wyjściowego]" >&2
  echo "Dostępne bazy: ${!CONTAINER[*]}" >&2
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

# `tr -d '\r'` usuwa końcówkę CR z plików .env zapisanych z zakończeniami
# linii CRLF (Windows) — bez tego wartość zawiera niewidoczny znak `\r` i
# psql/pg_dump dostaje np. rolę "wallet_user\r", która "nie istnieje".
DB_USER="$(grep -E "^${DB_USER_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2- | tr -d '\r')"
DB_NAME="$(grep -E "^${DB_NAME_KEY[$SERVICE]}=" "$ENV_FILE" | tail -n1 | cut -d= -f2- | tr -d '\r')"

if [[ -z "$DB_USER" || -z "$DB_NAME" ]]; then
  echo "BŁĄD: nie udało się odczytać ${DB_USER_KEY[$SERVICE]} / ${DB_NAME_KEY[$SERVICE]} z $ENV_FILE" >&2
  exit 1
fi

if [[ -z "$OUTPUT_FILE" ]]; then
  mkdir -p "$BACKUP_DIR"
  TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
  OUTPUT_FILE="$BACKUP_DIR/${SERVICE}-${DB_NAME}-${TIMESTAMP}.dump"
else
  mkdir -p "$(dirname "$OUTPUT_FILE")"
fi

COMPOSE=(docker compose --env-file "$ENV_FILE" "${COMPOSE_FILES[@]}")

echo "==> [$ENV_LABEL] Dump ${CONTAINER[$SERVICE]} (baza: $DB_NAME, user: $DB_USER) -> $OUTPUT_FILE"

# Format custom (-Fc): skompresowany, pozwala na selektywny/równoległy
# restore przez pg_restore (w przeciwieństwie do zwykłego dumpa SQL).
"${COMPOSE[@]}" exec -T "${CONTAINER[$SERVICE]}" \
  pg_dump -U "$DB_USER" -d "$DB_NAME" -Fc > "$OUTPUT_FILE"

echo "==> Gotowe: $(du -h "$OUTPUT_FILE" | cut -f1) zapisane w $OUTPUT_FILE"
