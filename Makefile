IMAGE_TAG ?= latest

COMPOSE = docker compose --env-file env
COMPOSE_DEV = docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml
COMPOSE_PROD_BUILD = docker compose --env-file .env.dev --env-file .env.secrets.local -f docker-compose.yml -f docker-compose.prod-build.yml
COMPOSE_PROD = IMAGE_TAG=$(IMAGE_TAG) docker compose \
	--env-file .env.dev \
	-f docker-compose.yml \
	-f docker-compose.prod.yml


# ========================
# PROD
# ========================

prod-pull:
	$(COMPOSE_PROD_BUILD) pull

prod:
	$(COMPOSE_PROD_BUILD) up -d --build

odds-prod:
	$(COMPOSE_PROD_BUILD) up -d --build odds-engine-service

front-prod:
	$(COMPOSE_PROD_BUILD) up -d --build frontend

auth-prod:
	$(COMPOSE_PROD_BUILD) up -d --build auth-service

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
