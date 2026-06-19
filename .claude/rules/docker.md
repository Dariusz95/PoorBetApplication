# Reguły Docker Compose — środowisko lokalne i produkcja

## Pliki konfiguracyjne
- docker-compose.yml — środowisko lokalne (dev)
- docker-compose.prod.yml — produkcja (nadpisuje bazowy)
- .env.example zawsze aktualny — .env nigdy nie trafia do gita

## Kolejność uruchamiania
- Każdy serwis zależny od bazy musi mieć depends_on z warunkiem:
  condition: service_healthy
- Nigdy nie zakładamy że serwis jest gotowy — zawsze healthcheck

## Healthcheck — obowiązkowy na każdym serwisie
  # PostgreSQL:
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER"]
    interval: 10s
    timeout: 5s
    retries: 5

  # Spring Boot (wymaga spring-boot-starter-actuator):
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
    interval: 15s
    timeout: 5s
    retries: 5
    start_period: 30s

## Wolumeny i dane
- Dane PostgreSQL przez named volume: postgres_data
- Nigdy bind mount na dane bazy — tylko named volumes
- Bind mount dopuszczalny wyłącznie dla kodu w trybie dev (hot reload)

## Porty (konwencja projektu)
- PostgreSQL:   5432
- Spring Boot:  8080 (każdy mikroserwis ma własny port: 8081, 8082...)
- Angular dev:  4200
- Angular prod: 80 (nginx)
- Nie wystawiaj portów bazy danych w środowisku prod

## Sieci
- Definiuj własne sieci — nie polegaj na domyślnej bridge
- Backend i baza w jednej sieci; frontend w osobnej z backendem
- Serwisy komunikują się po nazwach (DNS Compose), nie po IP

## Dockerfile
- Multi-stage build dla Spring Boot (builder + runtime)
- Bazowy obraz runtime: eclipse-temurin:21-jre-alpine (nie JDK!)
- Nginx:alpine dla Angular
- Kolejność warstw: zależności przed kodem źródłowym (cache!)
- Nigdy nie uruchamiamy jako root — USER 1001 w każdym obrazie

## Zmienne środowiskowe
- Sekrety przez Docker secrets lub zewnętrzny vault — nie przez env w compose
- Zmienne konfiguracyjne (nie sekrety) przez environment: w compose
- Spring czyta je automatycznie: SPRING_DATASOURCE_URL → spring.datasource.url