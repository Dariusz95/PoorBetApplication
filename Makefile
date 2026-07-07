COMPOSE = docker compose --env-file env
COMPOSE_DEV = docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml
COMPOSE_PROD = docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml

# ========================
# DEV ENV
# ========================

dev:
	$(COMPOSE_DEV) up -d --build

# ========================
# PROD ENV
# ========================

prod-pull:
	$(COMPOSE_PROD) pull

prod:
	$(COMPOSE_PROD) up -d

# ========================
# DEV
# ========================

odds-engine-dev:
	$(COMPOSE_DEV) up -d --build odds-engine-service

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
