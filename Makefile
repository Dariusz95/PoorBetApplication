IMAGE_TAG ?=

COMPOSE = docker compose --env-file env
COMPOSE_DEV = docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml
COMPOSE_PROD_BUILD_LOCAL = docker compose --env-file .env.dev --env-file .env.secrets.local -f docker-compose.yml -f docker-compose.prod-build.yml
COMPOSE_PROD = IMAGE_TAG=$(IMAGE_TAG) docker compose \
	--env-file .env.dev \
	-f docker-compose.yml \
	-f docker-compose.prod.yml


# ========================
# PROD (GHCR)
# ========================

prod-pull:
	@if [ -z "$(IMAGE_TAG)" ]; then \
		echo "BŁĄD: IMAGE_TAG nie ustawiony. Użyj: make prod-pull IMAGE_TAG=v0.1.6 (nigdy 'latest' w prod)"; \
		exit 1; \
	fi
	$(COMPOSE_PROD) pull

prod:
	@if [ -z "$(IMAGE_TAG)" ]; then \
		echo "BŁĄD: IMAGE_TAG nie ustawiony. Użyj: make prod IMAGE_TAG=v0.1.6 (nigdy 'latest' w prod)"; \
		exit 1; \
	fi
	$(COMPOSE_PROD) up -d

release:
	scripts/release.sh $(VERSION)

# ========================
# PROD-BUILD-LOCAL (lokalny prod)
# ========================

prod-build-local-pull:
	$(COMPOSE_PROD_BUILD_LOCAL) pull

prod-build-local:
	$(COMPOSE_PROD_BUILD_LOCAL) up -d --build

odds-prod-build-local:
	$(COMPOSE_PROD_BUILD_LOCAL) up -d --build odds-engine-service

front-prod-build-local:
	$(COMPOSE_PROD_BUILD_LOCAL) up -d --build frontend

auth-prod-build-local:
	$(COMPOSE_PROD_BUILD_LOCAL) up -d --build auth-service

# ========================
# DEV
# ========================

odds-engine-dev:
	$(COMPOSE_DEV) up -d --build odds-engine-service

dev:
	$(COMPOSE_DEV) up -d --build

auth-dev:
	$(COMPOSE_DEV) up -d --build auth-service

simulation-dev:
	$(COMPOSE_DEV) up -d --build simulation-service

coupon-dev:
	$(COMPOSE_DEV) up -d --build coupon-service

match-dev:
	$(COMPOSE_DEV) up -d --build match-service

gate-dev:
	$(COMPOSE_DEV) up -d --build gateway

front-dev:
	$(COMPOSE_DEV) up -d --build frontend

wallet-dev:
	$(COMPOSE_DEV) up -d --build wallet-service

notification-dev:
	$(COMPOSE_DEV) up -d --build notification-service

match-db:
	$(COMPOSE_DEV) up -d --build match-db

# ========================
# PODGLĄD BAZ DANYCH
# ========================
# Otwiera psql wewnątrz kontenera danej bazy (scripts/db-shell.sh).
# Przykład jednego zapytania zamiast sesji interaktywnej:
#   make db-match ARGS='-c "SELECT * FROM match LIMIT 10;"'

db-auth:
	scripts/db-shell.sh auth $(ARGS)

db-match:
	scripts/db-shell.sh match $(ARGS)

db-coupon:
	scripts/db-shell.sh coupon $(ARGS)

db-wallet:
	scripts/db-shell.sh wallet $(ARGS)
