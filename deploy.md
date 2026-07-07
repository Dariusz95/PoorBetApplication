1. PRZYGOTOWANIE OBRAZÓW DO PUBLIKACJI
Struktura projektu mikroserwisowego
my-microservices/ ├── docker-compose.yml ├── api-service/ │ ├── Dockerfile │ └── src/ ├── frontend/ │ ├── Dockerfile │ └── src/ └── .dockerignore
Dockerfile best practices - Spring Boot
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
Dockerfile dla Angular
# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
.dockerignore (zawsze)
node_modules npm-debug.log .git .gitignore README.md .env .DS_Store dist build target *.log
2. LOGOWANIE SIĘ DO DOCKER HUB
Z terminala (Docker CLI)
# Zaloguj się (pojawi się prompt na hasło)
docker login

# Lub podaj hasło przez stdin (bezpieczniej w CI/CD)
echo "YOUR_PASSWORD" | docker login -u YOUR_USERNAME --password-stdin

# Lub użyj Personal Access Token zamiast hasła
echo "YOUR_PAT_TOKEN" | docker login -u YOUR_USERNAME --password-stdin

# Weryfikacja - sprawdź plik konfiguracyjny
cat ~/.docker/config.json  # Zawiera zakodowaną autentykację

# Wyloguj się
docker logout
Gdzie uzyskać Personal Access Token:

Zaloguj się na https://hub.docker.com
Przejdź do Account Settings → Security → New Access Token
Nadaj token nazwę: docker-cli lub jenkins itd.
Wybierz uprawnienia: Read & Write
Skopiuj token - już go nie zobaczysz ponownie
3. TAGGING OBRAZÓW - KONWENCJA NAZEWNICTWA
Format tagu Docker
[REGISTRY]/[NAMESPACE]/[REPOSITORY]:[TAG] registry: docker.io (domyślnie, jeśli pominiesz) namespace: Twoja nazwa użytkownika lub nazwa organizacji na Docker Hub repository: Nazwa aplikacji/serwisu tag: Wersja (1.0.0, latest, v1.0.0, etc.)
Tagging - praktyczne przykłady
# Zabuduj obraz bez tagu (domyślnie "latest")
docker build -t api-service .

# Dodaj tag z Twoją nazwą użytkownika
docker tag api-service YOUR_USERNAME/api-service:1.0.0

# Dodaj wiele tagów dla tego samego obrazu
docker tag api-service YOUR_USERNAME/api-service:latest
docker tag api-service YOUR_USERNAME/api-service:1.0.0
docker tag api-service YOUR_USERNAME/api-service:stable

# Wszystkie tagi wskazują na ten sam obraz (te same warstwy)
docker images | grep api-service
Konwencja nazewnictwa dla mikroserwisów
# Dla Twojego username na Docker Hub: mycompany

# API Spring Boot
docker tag api-service mycompany/api-service:1.0.0
docker tag api-service mycompany/api-service:latest

# Frontend Angular
docker tag frontend mycompany/frontend:1.0.0
docker tag frontend mycompany/frontend:latest

# Baza danych - zwykle używasz oficjalnego obrazu, ale możesz customizować
# docker tag my-postgres mycompany/postgres:14-custom
Alternatywa - bezpośredni tag przy buildzie
docker build -t mycompany/api-service:1.0.0 -t mycompany/api-service:latest .
4. PUSH - PUBLIKACJA NA DOCKER HUB
Zabuduj i pushuj wszystkie serwisy
# Zaloguj się pierwszy raz
docker login

# 1. API Service
docker build -t mycompany/api-service:1.0.0 api-service/
docker tag mycompany/api-service:1.0.0 mycompany/api-service:latest
docker push mycompany/api-service:1.0.0
docker push mycompany/api-service:latest

# 2. Frontend
docker build -t mycompany/frontend:1.0.0 frontend/
docker tag mycompany/frontend:1.0.0 mycompany/frontend:latest
docker push mycompany/frontend:1.0.0
docker push mycompany/frontend:latest

# 3. Custom PostgreSQL (jeśli potrzebujesz)
docker build -t mycompany/postgres-custom:14 db/
docker push mycompany/postgres-custom:14
Wersjonowanie - najlepsze praktyki
# Semantic Versioning: MAJOR.MINOR.PATCH
# 1.0.0 = first stable release
# 1.0.1 = patch/bugfix
# 1.1.0 = new feature
# 2.0.0 = breaking change

# Produkcyjnie zawsze używaj tagów wersji zamiast "latest"
# latest to niebezpieczne - ktoś może przypadkowo wdrożyć nową wersję
Script do automatycznego push'a wszystkich serwisów
#!/bin/bash
# publish.sh

USERNAME="mycompany"
VERSION="1.0.0"

# Tablica serwisów
SERVICES=("api-service" "frontend")

for service in "${SERVICES[@]}"; do
  echo "Building $service..."
  docker build -t $USERNAME/$service:$VERSION $service/
  docker tag $USERNAME/$service:$VERSION $USERNAME/$service:latest
  
  echo "Pushing $service..."
  docker push $USERNAME/$service:$VERSION
  docker push $USERNAME/$service:latest
done

echo "All services published successfully!"
Uruchomienie:

chmod +x publish.sh
./publish.sh
5. WERYFIKACJA - SPRAWDZENIE PUBLIKACJI
Sprawdź lokalnie czy obrazy są zatagowane
docker images | grep mycompany

# Output:
# REPOSITORY                  TAG        IMAGE ID      CREATED
# mycompany/api-service       1.0.0      abc123def     5 minutes ago
# mycompany/api-service       latest     abc123def     5 minutes ago
# mycompany/frontend          1.0.0      xyz789uvw     4 minutes ago
# mycompany/frontend          latest     xyz789uvw     4 minutes ago
Sprawdź na Docker Hub
Zaloguj się na https://hub.docker.com
Przejdź do My Repositories
Kliknij na repozytorium (np. api-service)
Przejdź do zakładki Tags - powinieneś widzieć 1.0.0 i latest
Sprawdź Image Details - informacje o rozmiarze, datzie publikacji, skanowaniu bezpieczeństwa
CLI - sprawdzanie tagów
# Lista wszystkich tagów w repozytorium (wymaga docker-content-trust)
# Lub użyj Docker Hub API

curl -s https://registry.hub.docker.com/v2/repositories/mycompany/api-service/tags/ | jq
6. POBIERANIE OBRAZÓW - DOCKER PULL
Na innym środowisku
# Zaloguj się na nowym serwerze
docker login

# Pobierz i uruchom obraz
docker pull mycompany/api-service:1.0.0
docker run -d -p 8080:8080 --name api-service mycompany/api-service:1.0.0

# Lub sprawdź co się stawiło
docker images mycompany/api-service
Docker Compose z obrazami z Docker Hub
version: '3.9'

services:
  api-service:
    image: mycompany/api-service:1.0.0
    container_name: api-service
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/mydb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    depends_on:
      - postgres
    networks:
      - microservices

  frontend:
    image: mycompany/frontend:1.0.0
    container_name: frontend
    ports:
      - "80:80"
    networks:
      - microservices

  postgres:
    image: postgres:15-alpine
    container_name: postgres
    environment:
      POSTGRES_DB: mydb
      POSTGRES_PASSWORD: postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - microservices

volumes:
  postgres_data:

networks:
  microservices:
    driver: bridge
Uruchomienie:

docker compose pull  # Pobierz najnowsze obrazy
docker compose up -d # Uruchom z pobranych obrazów
7. NAJLEPSZE PRAKTYKI PRODUKCYJNE
🔒 A. BEZPIECZEŃSTWO - NIE PUBLIKUJ SEKRETÓW
# ❌ NIGDY nie rób tego
docker build -t myapp . --build-arg DB_PASSWORD=secret123
ENV GITHUB_TOKEN=ghp_xxxxxxxxxxxx

# ✅ POPRAWNIE - użyj build secrets
docker build -t myapp . --secret github_token=/path/to/token.txt

# Dockerfile
RUN --mount=type=secret,id=github_token \
    cat /run/secrets/github_token > ~/.github/token
.gitignore - zabezpieczenie
.env *.key *.pem secrets/ config/production.yaml
.dockerignore
.git .gitignore .env README.md docker-compose.yml test/ .github/ node_modules dist build target *.log
B. WERSJONOWANIE - SemVer
# Każdy release ma wersję w Dockerfile/source code
# Zawsze tag produkcją dokładnie - nigdy nie używaj "latest"

# docker-compose.yml (produkcja)
services:
  api:
    image: mycompany/api-service:1.2.3  # ✅ Konkretna wersja
    # image: mycompany/api-service:latest  # ❌ Nigdy!
C. IMAGE SCANNING - Sprawdzanie vulnerabilności
# Docker Scout - skanowanie obrazu przed publikacją
docker scout cves mycompany/api-service:1.0.0

# Lub w Docker Hub:
# Przejdź do repozytorium → "Vulnerability scanning"
# Enable "Scan on push"
D. Dokumentacja w Docker Hub
Utwórz README.md w repozytorium Docker Hub:

Zaloguj się na https://hub.docker.com
Kliknij na repozytorium
Przejdź do Edit description
Wklej zawartość README.md z Twojego repo (GitHub)
Docker Hub wyświetli README jako dokumentację
Przykład README.md:

# API Service

Spring Boot microservice dla zarządzania użytkownikami.

## Użycie

```bash
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb \
  mycompany/api-service:1.0.0
Zmienne środowiskowe
SPRING_DATASOURCE_URL - Connection string do PostgreSQL
SPRING_DATASOURCE_USERNAME - DB user
SPRING_DATASOURCE_PASSWORD - DB password
Porty
8080 - API HTTP
### E. CI/CD Pipeline - GitHub Actions ```yaml name: Publish to Docker Hub on: push: tags: - 'v*' jobs: push: runs-on: ubuntu-latest steps: - uses: actions/checkout@v3 - name: Set up Docker Buildx uses: docker/setup-buildx-action@v2 - name: Log in to Docker Hub uses: docker/login-action@v2 with: username: ${{ secrets.DOCKER_USERNAME }} password: ${{ secrets.DOCKER_PAT }} - name: Build and push API Service uses: docker/build-push-action@v4 with: context: ./api-service push: true tags: | ${{ secrets.DOCKER_USERNAME }}/api-service:${{ github.ref_name }} ${{ secrets.DOCKER_USERNAME }}/api-service:latest - name: Build and push Frontend uses: docker/build-push-action@v4 with: context: ./frontend push: true tags: | ${{ secrets.DOCKER_USERNAME }}/frontend:${{ github.ref_name }} ${{ secrets.DOCKER_USERNAME }}/frontend:latest
Sekrety w GitHub:

Przejdź do Settings → Secrets and variables → Actions
Dodaj:
DOCKER_USERNAME = Twoja nazwa na Docker Hub
DOCKER_PAT = Personal Access Token (nie zwykłe hasło!)
F. Konfiguracja buildx dla wieloplatformowych obrazów
# Obsługuj arm64 (M1/M2 Mac) i amd64 (Intel/AMD)
docker buildx create --name multiplatform --use

# Build dla obu platform
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t mycompany/api-service:1.0.0 \
  --push \
  api-service/
G. Private repositories - dla podatliwych danych
# docker-compose.yml - pull z prywatnego repo
services:
  api:
    image: mycompany/api-service-private:1.0.0
    # Wymaga docker login wcześniej!
Sekrety w Kubernetes:

kubectl create secret docker-registry dockerhub \
  --docker-server=docker.io \
  --docker-username=mycompany \
  --docker-password=$DOCKER_PAT
PODSUMOWANIE - Workflow dla Twojego projektu
1. Lokalna publikacja:

docker login

# Build
docker build -t mycompany/api-service:1.0.0 api-service/
docker build -t mycompany/frontend:1.0.0 frontend/

# Tag
docker tag mycompany/api-service:1.0.0 mycompany/api-service:latest
docker tag mycompany/frontend:1.0.0 mycompany/frontend:latest

# Push
docker push mycompany/api-service:1.0.0
docker push mycompany/api-service:latest
docker push mycompany/frontend:1.0.0
docker push mycompany/frontend:latest
2. Pobieranie na innym środowisku:

docker login
docker compose pull
docker compose up -d
3. Produkcja:

Zawsze używaj konkretnych tagów wersji (1.0.0, nie latest)
Włącz skanowanie bezpieczeństwa na Docker Hub
Przechowuj sekrety w environment variables, nigdy w obrazie
Używaj Private Access Token, nie zwykłych haseł
Sources:

https://docs.docker.com/get-started/docker-concepts/building-images/build-tag-and-publish-an-image/
https://docs.docker.com/reference/cli/docker/image/pull/
https://docs.docker.com/docker-hub/
https://docs.docker.com/engine/security/
https://docs.docker.com/build/ci/github-actions/
https://docs.docker.com/compose/
Let me know if you have any other questions!