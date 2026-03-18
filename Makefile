train:
	docker-compose run --rm --build python-trainer python train_model.py

train-clean:
	docker compose build --no-cache python-trainer
	docker compose run --rm python-trainer python train_model.py

generate:
	./generate-matches.sh
	docker compose run --rm python-trainer python train_model.py
	docker compose up -d odds-service

run-app:
	docker compose up -d --build simulation-service

# 	until [ "`docker inspect -f {{.State.Health.Status}} simulation-service`" = "healthy" ]; do \
# 		echo "Czekam na simulation-service..."; \
# 		sleep 2; \
# 	done
	sleep 15

	./generate-matches.sh
	docker compose run --rm python-trainer python train_model.py
	docker compose up -d --build

run-app-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build simulation-service

	sleep 15

	./generate-matches.sh
	docker compose -f docker-compose.yml -f docker-compose.dev.yml run --rm python-trainer python train_model.py
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

user-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build user-service

run-simulation-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build simulation-service

gate-dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build gateway

dev:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build

front:
	docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build frontend