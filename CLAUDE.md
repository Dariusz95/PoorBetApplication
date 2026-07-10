# CLAUDE.md

Ten plik zawiera wskazówki dla Claude Code podczas pracy z tym repozytorium.

Cała dokumentacja, komentarze do kodu, komunikaty commitów, opisy zmian architektonicznych oraz przykłady kodu powinny być przygotowywane w języku polskim, chyba że użytkownik wyraźnie poprosi o użycie innego języka.

## Styl współpracy

To jest projekt do nauki Springa i Dockera. Przy dodawaniu lub modyfikowaniu kodu **zawsze tłumacz złożone koncepcje**, takie jak:

- mechanizmy Springa (np. jak działa `@Transactional`, `@Scheduled`, Client Credentials Flow)
- wzorce architektoniczne (np. Outbox Pattern, saga)
- zachowania Docker Compose (np. `depends_on` z `condition: service_healthy`)
- działanie narzędzi (np. ShedLock, Flyway, Testcontainers)

Wyjaśnienia umieszczaj po wprowadzeniu zmiany — krótko, ale konkretnie.

## Pierwsze uruchomienie

Przed pierwszym `make dev` wymagana jest konfiguracja:

### 1. Plik `.env.dev`

Skopiuj wzorzec `.env.example` i uzupełnij wartości:

```bash
cp .env.example .env.dev
```

Ten sam wzorzec służy też do stworzenia `.env` na potrzeby produkcji — patrz `DEPLOY_PLAN.md`. Pliki `.env` i `.env.dev` są ignorowane przez git i nie są dostarczane z repozytorium.

### 2. Dostęp do GitHub Packages (biblioteki współdzielone)

Biblioteki `poorbet-commons` i `poorbet-auth-starter` są publikowane w GitHub Packages. Żeby Maven mógł je pobrać poza Dockerem (np. do uruchomienia testów lokalnie), wymagany jest plik `~/.m2/settings.xml` z tokenem GitHub:

```xml
<settings>
  <servers>
    <server>
      <id>github-commons</id>
      <username>TWÓJ_LOGIN_GITHUB</username>
      <password>TWÓJ_TOKEN_GITHUB</password>
    </server>
    <server>
      <id>github-auth</id>
      <username>TWÓJ_LOGIN_GITHUB</username>
      <password>TWÓJ_TOKEN_GITHUB</password>
    </server>
  </servers>
</settings>
```

W Dockerze zależności są pobierane podczas budowania obrazu — token musi być dostępny jako argument budowania lub wolumen `~/.m2`.

## Przegląd projektu

PoorBetApplication to platforma do obstawiania wydarzeń sportowych zbudowana w architekturze mikroserwisowej. Backend wykorzystuje Java 21 oraz Spring Boot 3.4.1, natomiast frontend został napisany w Angular 21. Wszystkie usługi uruchamiane są w Dockerze przy użyciu Docker Compose.

## Komendy

### Uruchamianie całego środowiska (tryb deweloperski)

```bash
make dev                  # uruchamia wszystkie usługi z hot-reload
make match-dev            # przebudowuje i restartuje tylko match-service
make coupon-dev           # przebudowuje i restartuje tylko coupon-service
make front-dev            # przebudowuje i restartuje tylko frontend
make wallet-dev           # przebudowuje i restartuje tylko wallet-service
make notification-dev     # przebudowuje i restartuje tylko notification-service
make auth-dev             # przebudowuje i restartuje tylko auth-service
make gate-dev             # przebudowuje i restartuje tylko gateway
make odds-engine-dev      # przebudowuje i restartuje tylko odds-engine-service
make match-db             # uruchamia tylko bazę danych match-service
```

Środowisko deweloperskie korzysta z pliku `.env.dev` oraz łączy konfiguracje z `docker-compose.yml` i `docker-compose.dev.yml`.

Każdy serwis Java w trybie dev ma aktywowany profil `SPRING_PROFILES_ACTIVE=dev`, co powoduje załadowanie pliku `application-dev.yml` nakładającego się na `application.yml`. W profilu dev typowo nadpisywane są poziomy logowania i adresy połączeń.

### Uruchamianie testów

#### Backend (z poziomu katalogu wybranego serwisu)

```bash
cd match-service && ./mvnw test
cd match-service && ./mvnw test -Dtest=MatchPoolServiceImplTest
```

Testy integracyjne backendu wykorzystują Testcontainers, które automatycznie uruchamiają PostgreSQL. Podczas testów aktywowany jest profil `application-test.yml`.

#### Frontend

```bash
cd frontend && npm test
cd frontend && npm run test:watch
```

### Budowanie aplikacji

```bash
# Budowanie pojedynczego serwisu
cd match-service && ./mvnw package -DskipTests

# Budowanie frontendu
cd frontend && npm run build
```

### Formatowanie i lintowanie

```bash
cd frontend && npx prettier --check src/
cd frontend && npx prettier --write src/
```

### Debugowanie zdalne (IntelliJ / VS Code)

W trybie dev każdy serwis Java uruchamia się z agentem JDWP na dedykowanym porcie. Pozwala to podpiąć debugger IDE bez restartowania kontenera.

| Serwis | Port debugowania |
| --- | --- |
| `gateway` | 5081 |
| `auth-service` | 5079 |
| `match-service` | 5070 |
| `coupon-service` | 5076 |
| `wallet-service` | 5077 |
| `notification-service` | 5078 |
| `odds-engine-service` | 5090 |

### Porty baz danych (dostęp z hosta)

W trybie dev bazy są dostępne bezpośrednio z hosta (np. do podpięcia DBeaver):

| Baza | Port hosta |
| --- | --- |
| `user-db` | 5432 |
| `match-db` | 5434 |
| `coupon-db` | 5435 |
| `wallet-db` | 5437 |
| RabbitMQ Management UI | 15672 |

Alternatywnie, bez klienta SQL na hoście, `scripts/db-shell.sh <auth|match|coupon|wallet>` otwiera psql wewnątrz kontenera (działa też na produkcji, gdzie porty baz nie są wystawione na zewnątrz — patrz `.claude/rules/docker.md`). Skróty: `make db-auth`, `make db-match`, `make db-coupon`, `make db-wallet`.

## Architektura

### Mapa serwisów

| Serwis                 | Port (dev) | Przeznaczenie                                                                                            |
| ---------------------- | ---------- | -------------------------------------------------------------------------------------------------------- |
| `gateway`              | 8081       | Spring Cloud Gateway – główny punkt wejścia do systemu, odpowiedzialny za routing żądań                  |
| `auth-service`         | 8089       | Uwierzytelnianie użytkowników, rejestracja, logowanie, generowanie JWT oraz udostępnianie endpointu JWKS |
| `match-service`        | 8083       | Zarządzanie meczami, drużynami, symulacją spotkań i pobieraniem kursów                                   |
| `odds-engine-service`  | 8090       | Wyliczanie kursów przy użyciu modelu ML (biblioteka Smile)                                               |
| `coupon-service`       | 8086       | Tworzenie oraz rozliczanie kuponów                                                                       |
| `wallet-service`       | 8087       | Zarządzanie portfelem użytkownika, rezerwacjami środków i operacjami finansowymi                         |
| `notification-service` | 8088       | Powiadomienia SSE wysyłane do frontendu                                                                  |
| `frontend`             | 4200       | Aplikacja SPA oparta o Angular                                                                           |

### Konwencja URL API

Wszystkie endpointy backendu mają prefiks `/api/<nazwa-zasobu>/**`. Gateway routuje żądania na podstawie tego prefiksu — np. `/api/coupons/**` trafia do coupon-service. Dodając nowy endpoint, zawsze używaj tego schematu.

### Struktura pakietów serwisów

Każdy serwis Java stosuje tę samą strukturę pakietów:

```text
config/         — konfiguracja Springa (Beans, Security, RabbitMQ)
controller/     — kontrolery REST (@RestController)
domain/         — encje JPA i logika domenowa
dto/            — obiekty transferu danych (request/response)
repository/     — repozytoria Spring Data JPA
service/        — logika biznesowa (@Service)
infrastructure/ — integracje zewnętrzne (klienty HTTP, publishery zdarzeń)
mapper/         — mapowania Entity ↔ DTO (jeśli serwis używa MapStruct)
```

Przy dodawaniu nowej funkcjonalności trzymaj się tej struktury.

### Przepływ żądań

```text
Przeglądarka → Gateway (8081) → Docelowy mikroserwis
```

Gateway kieruje ruch na podstawie prefiksu ścieżki (np. `/api/coupons/**` → coupon-service).

Autoryzacja obsługiwana jest przez każdy mikroserwis niezależnie przy użyciu biblioteki `poorbet-auth-starter`, która weryfikuje tokeny JWT na podstawie endpointu JWKS udostępnianego przez auth-service.

W środowisku deweloperskim zabezpieczenia gatewaya są wyłączone.

## Uwierzytelnianie i autoryzacja

Auth-service generuje tokeny JWT podpisywane kluczem RSA.

Pozostałe mikroserwisy weryfikują tokeny przy pomocy biblioteki `poorbet-auth-starter`, konfigurowanej za pomocą właściwości `poorbet.security.*`.

Komunikacja między serwisami wykorzystuje Client Credentials Flow. Każdy serwis posiada własne dane dostępowe:

```text
AUTH_SERVICE_CLIENT_ID
AUTH_SERVICE_CLIENT_SECRET
```

Adnotacje `@PreAuthorize` wykorzystują stałe z klasy `PoorbetPermissions` znajdującej się w bibliotece współdzielonej.

## Komunikacja asynchroniczna (RabbitMQ)

Mikroserwisy komunikują się asynchronicznie za pomocą RabbitMQ i wspólnego schematu zdarzeń z biblioteki `poorbet-commons`.

Każdy serwis definiuje własne kolejki, exchange oraz bindingi w konfiguracji RabbitMQ wykorzystując `EventRegistry`.

Zdarzenia definiowane są jako `EventDefinition<T>`.

### Outbox Pattern

Serwisy `coupon-service` oraz `wallet-service` wykorzystują wzorzec Outbox.

Zdarzenia domenowe zapisywane są najpierw w tabeli `outbox_event` w ramach tej samej transakcji co operacja biznesowa, a następnie publikowane do RabbitMQ.

Pozwala to uniknąć utraty komunikatów w przypadku błędu lub wycofania transakcji.

### Główne przepływy zdarzeń

* `USER_CREATED` → wallet-service tworzy portfel użytkownika
* `MATCHES_FINISHED` → coupon-service rozlicza kupony
* `COUPON_WON` / `COUPON_LOST` → wallet-service aktualizuje saldo użytkownika
* Zmiany salda portfela → notification-service wysyła powiadomienia SSE

## Cykl życia meczu

Match-service zarządza koncepcją puli meczów (Match Pool).

Mecze są:

1. Planowane z wyprzedzeniem
2. Uruchamiane w trybie live
3. Symulowane
4. Kończone i rozliczane

Do harmonogramowania wykorzystywany jest ShedLock oparty o JDBC, który zapobiega wielokrotnemu uruchomieniu tego samego zadania na wielu instancjach.

Dane meczów na żywo przechowywane są w Redisie.

Kursy pobierane są z odds-engine-service, który wykorzystuje model ML zbudowany przy użyciu biblioteki Smile.

## Baza danych

Każdy mikroserwis posiada własną, niezależną bazę PostgreSQL.

Migracje schematu realizowane są za pomocą Flyway:

```text
classpath:db/migration
```

Match-service korzysta z konfiguracji:

```yaml
ddl-auto: validate
```

Oznacza to, że encje muszą być zgodne z migracjami Flyway.

## Biblioteki współdzielone

### poorbet-commons

Zawiera:

* EventDefinition
* EventRegistry
* MessagingProperties
* wspólne abstrakcje RabbitMQ

### poorbet-auth-starter

Zawiera:

* automatyczną konfigurację Spring Security
* walidację JWT
* PoorbetPermissions
* CurrentUserProvider

Biblioteki publikowane są w GitHub Packages i konfigurowane w głównym pliku `pom.xml`.

Do budowania projektu poza Dockerem wymagane są dane dostępowe do GitHub Packages skonfigurowane w Maven Settings.

## Frontend

Frontend został zbudowany w Angular 21 jako aplikacja SPA wykorzystująca Standalone Components oraz Signals.

### Główne elementy

#### Uwierzytelnianie

* `auth-token.interceptor.ts` – dodawanie tokenu JWT do żądań
* `auth-error.interceptor.ts` – obsługa błędów 401
* Guardy znajdują się w `core/auth/guards/`

#### Internacjonalizacja

* `@jsverse/transloco`

#### Powiadomienia

* `ngx-sse-client`
* połączenie SSE z notification-service

#### Stylowanie

* Tailwind CSS
* Angular Material
* Bootstrap Icons

#### Aliasowanie ścieżek

* `@app`
* `@core`
* `@shared`
* `@features`
* `@env`

Konfiguracja znajduje się w:

* `vitest.config.ts`
* `tsconfig`

#### Testy

* Vitest
* jsdom

Projekt nie wykorzystuje Jest ani Karma.

### Struktura funkcjonalna

Funkcjonalności znajdują się w:

```text
src/app/features/
```

Przykłady:

```text
bet/
coupons/
```

Katalog `core/` zawiera:

* uwierzytelnianie
* interceptory
* komponent nagłówka
* wspólne komponenty układu aplikacji

## Proxy deweloperskie

W środowisku lokalnym Angular wykorzystuje proxy skonfigurowane w:

```text
src/proxy.config.json
```

Proxy używane jest przez:

```bash
npm run start:dev
```

Podczas pracy w Dockerze frontend komunikuje się z backendem przez gateway dostępny na porcie `8081`.
