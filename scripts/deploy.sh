#!/usr/bin/env bash
#
# scripts/deploy.sh
#
# Uruchamiane NA SERWERZE VPS, w katalogu roboczym /opt/poorbet (albo tam,
# gdzie faktycznie leży ten skrypt — patrz wykrywanie APP_DIR poniżej).
# Wywoływane automatycznie przez CD (.github/workflows/publish-images.yml,
# job `deploy`, po udanej publikacji wszystkich 8 obrazów) albo ręcznie przez
# operatora jako fallback:
#
#   cd /opt/poorbet
#   IMAGE_TAG=v0.1.6 ./scripts/deploy.sh
#
# Idempotentny — bezpieczny do wielokrotnego odpalenia z tym samym IMAGE_TAG
# (docker compose pull/up -d nic nie zmienia, jeśli obrazy i stan kontenerów
# już są zgodne z żądanym tagiem).

set -euo pipefail

# --- IMAGE_TAG: wymagany, bez wartości domyślnej ---------------------------
# Zgodnie z decyzją z DEPLOY_PLAN.md Etap 5 ("nigdy `latest` w prod") — brak
# jawnego podania tagu ma głośno wywalić deploy, a nie po cichu ściągnąć,
# cokolwiek aktualnie jest podpięte pod :latest (a i tak nic by nie było,
# bo CI nigdy nie publikuje obrazu z tagiem `latest`).
if [[ -z "${IMAGE_TAG:-}" ]]; then
  echo "BŁĄD: zmienna IMAGE_TAG jest wymagana, np.:" >&2
  echo "  IMAGE_TAG=v0.1.6 ./scripts/deploy.sh" >&2
  exit 1
fi
export IMAGE_TAG

# --- APP_DIR: domyślnie katalog nadrzędny względem tego skryptu -------------
# Pozwala uruchomić skrypt z dowolnego cwd (np. z SSH: `~/scripts/deploy.sh`)
# i nadal poprawnie trafić na docker-compose*.yml/.env obok. Można nadpisać
# jawnie zmienną APP_DIR, jeśli katalog roboczy kiedyś się zmieni.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${APP_DIR:-$(cd "$SCRIPT_DIR/.." && pwd)}"
ENV_FILE="$APP_DIR/.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "BŁĄD: brak $ENV_FILE. Jednorazowa konfiguracja z DEPLOY_PLAN.md Etap 8.1/8.2 nie została zrobiona." >&2
  exit 1
fi

COMPOSE=(docker compose --env-file "$ENV_FILE" \
  -f "$APP_DIR/docker-compose.yml" \
  -f "$APP_DIR/docker-compose.prod.yml")

echo "==> Deploy IMAGE_TAG=$IMAGE_TAG (APP_DIR=$APP_DIR)"

echo "==> docker compose pull"
"${COMPOSE[@]}" pull

echo "==> docker compose up -d"
# --wait (Compose v2.17+) czeka, aż wszystkie serwisy z healthcheckiem
# (już zdefiniowane w docker-compose.yml, Etap 4) osiągną `healthy`, i kończy
# się kodem != 0, jeśli któryś nie wstanie w wyznaczonym czasie. To jedyny
# automatyczny "safety net" w tym flow — bez tego `up -d` zwraca 0
# natychmiast, niezależnie od tego, czy nowe obrazy w ogóle wstają, a CD
# fałszywie pokazywałby zielony deploy.
# --remove-orphans sprząta kontenery serwisów usuniętych z compose files.
"${COMPOSE[@]}" up -d --remove-orphans --wait --wait-timeout 180

echo "==> Status kontenerów"
"${COMPOSE[@]}" ps

echo "==> Porządki: usuwanie osieroconych (dangling) warstw obrazów"
docker image prune -f

echo "==> Porządki: usuwanie starszych tagów poorbet-* (inne niż aktualny IMAGE_TAG)"
# Celowo NIE -a/-f na całym `docker image prune` (usunęłoby też obrazy
# niezwiązane z tym projektem). Usuwamy tylko stare wersje obrazów poorbet;
# błąd usunięcia pojedynczego obrazu (np. bo wciąż referowany) nie przerywa
# skryptu — to tylko sprzątanie, nie krytyczny krok deployu.
docker image ls --format '{{.Repository}}:{{.Tag}}' \
  | grep -E '^ghcr\.io/dariusz95/poorbet-' \
  | grep -v ":${IMAGE_TAG}$" \
  | while read -r old_image; do
      docker image rm "$old_image" 2>/dev/null || true
    done

echo "==> Deploy IMAGE_TAG=$IMAGE_TAG zakończony pomyślnie."
