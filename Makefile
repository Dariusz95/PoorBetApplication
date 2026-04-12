COMPOSE = docker compose --env-file env
COMPOSE_DEV = docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml
TRAIN = $(COMPOSE) run --rm python-trainer python train_model.py

train:
	$(TRAIN)

train-clean:
	$(COMPOSE) build --no-cache python-trainer
	$(TRAIN)

generate:
	./generate-matches.sh
	$(TRAIN)
	$(COMPOSE) up -d odds-service

# ========================
# APP
# ========================

run-app:
	$(COMPOSE) up -d --build simulation-service
	$(MAKE) generate
	$(COMPOSE) up -d --build

# ========================
# DEV ENV
# ========================

app-dev:
	$(COMPOSE_DEV) up -d --build simulation-service user-service
	$(COMPOSE_DEV) up -d --build odds-training
	$(COMPOSE_DEV) run --rm python-trainer python train_model.py
	$(COMPOSE_DEV) up -d --build

dev:
	$(COMPOSE_DEV) up -d --build

# ========================
# DEV
# ========================

odds-training-dev:
	$(COMPOSE_DEV) up -d --build odds-training

user-dev:
	$(COMPOSE_DEV) up -d --build user-service

simulation-dev:
	$(COMPOSE_DEV) up -d --build simulation-service

coupon-dev:
	$(COMPOSE_DEV) up -d --build coupon-service

teams-dev:
	$(COMPOSE_DEV) up -d --build teams-service

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
