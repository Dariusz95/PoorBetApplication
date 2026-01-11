train:
	docker-compose run --rm --build python-trainer python train_model.py

train-clean:
	docker-compose build --no-cache python-trainer
	docker-compose run --rm python-trainer python train_model.py

generate:
	./generate-matches.sh
	docker-compose run --rm python-trainer python train_model.py
	docker-compose up -d odds-service

run-app:
	docker-compose up -d --build simulation-service

# 	until [ "`docker inspect -f {{.State.Health.Status}} simulation-service`" = "healthy" ]; do \
# 		echo "Czekam na simulation-service..."; \
# 		sleep 2; \
# 	done
# 	sleep 15

	./generate-matches.sh
	docker-compose run --rm python-trainer python train_model.py
	docker-compose up -d --build