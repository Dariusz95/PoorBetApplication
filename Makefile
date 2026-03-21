COMPOSE = docker compose
COMPOSE_DEV = docker compose -f docker-compose.yml -f docker-compose.dev.yml
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
	sleep 15
	$(MAKE) generate
	$(COMPOSE) up -d --build

# ========================
# DEV ENV
# ========================

run-app-dev:
	$(COMPOSE_DEV) up -d --build simulation-service user-service
	sleep 15
	$(COMPOSE_DEV) up -d --build odds-training
	$(COMPOSE_DEV) run --rm python-trainer python train_model.py
	$(COMPOSE_DEV) up -d --build

dev:
	$(COMPOSE_DEV) up -d --build

# ========================
# DEV
# ========================

user-dev:
	$(COMPOSE_DEV) up -d --build user-service

simulation-dev:
	$(COMPOSE_DEV) up -d --build simulation-service

teams-dev:
	$(COMPOSE_DEV) up -d --build teams-service

match-dev:
	$(COMPOSE_DEV) up -d --build match-service

gate-dev:
	$(COMPOSE_DEV) up -d --build gateway

front:
	$(COMPOSE_DEV) up -d --build frontend
