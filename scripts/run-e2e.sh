#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${APP_DIR:-$(cd "$SCRIPT_DIR/.." && pwd)}"

COMPOSE=(docker compose --env-file "$APP_DIR/.env.dev" -f "$APP_DIR/docker-compose.yml" -f "$APP_DIR/docker-compose.e2e.yml" -p poorbet-e2e)

export E2E_UID="$(id -u)"
export E2E_GID="$(id -g)"

E2E_GATEWAY_PORT="${E2E_GATEWAY_PORT:-8181}"
E2E_FRONTEND_PORT="${E2E_FRONTEND_PORT:-4300}"
WAIT_ATTEMPTS=60
WAIT_INTERVAL_SECONDS=5

cleanup() {
  echo "==> Zatrzymuję i usuwam stos E2E (poorbet-e2e)..."
  "${COMPOSE[@]}" down -v
}
trap cleanup EXIT

echo "==> Startuję izolowany stos E2E (gateway, frontend + zależności: auth-service, user-db, rabbitmq)..."
"${COMPOSE[@]}" up -d --build gateway frontend

echo "==> Czekam, aż gateway (:$E2E_GATEWAY_PORT) i frontend (:$E2E_FRONTEND_PORT) odpowiadają..."

ready=false
for attempt in $(seq 1 "$WAIT_ATTEMPTS"); do
  gateway_code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 3 "http://localhost:${E2E_GATEWAY_PORT}/api/users/login" -X POST -H 'Content-Type: application/json' -d '{}' || true)"
  frontend_code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 3 "http://localhost:${E2E_FRONTEND_PORT}/" || true)"

  if [[ "$gateway_code" != "000" && "$gateway_code" != "" && "$frontend_code" == "200" ]]; then
    ready=true
    break
  fi

  echo "    (${attempt}/${WAIT_ATTEMPTS}) gateway=${gateway_code:-brak} frontend=${frontend_code:-brak} — czekam ${WAIT_INTERVAL_SECONDS}s..."
  sleep "$WAIT_INTERVAL_SECONDS"
done

if [[ "$ready" != true ]]; then
  echo "BŁĄD: stos E2E nie odpowiedział w wyznaczonym czasie ($((WAIT_ATTEMPTS * WAIT_INTERVAL_SECONDS))s)." >&2
  "${COMPOSE[@]}" logs --tail=50 gateway auth-service frontend >&2
  exit 1
fi

echo "==> Stos gotowy. Buduję i uruchamiam kontener testowy (usługa 'e2e')..."

mkdir -p "$APP_DIR/e2e/playwright-report" "$APP_DIR/e2e/test-results"
"${COMPOSE[@]}" build e2e
"${COMPOSE[@]}" run --rm e2e
