# Raport architektoniczny — PoorBetApplication

> **Autor:** Senior/Lead Software Engineer & Software Architect review  
> **Data:** 2026-06-20  
> **Wersja projektu:** 0.0.1-SNAPSHOT (Spring Boot 3.4.1, Java 21)

---

## Executive Summary

**Ocena projektu: 6.5 / 10**

PoorBetApplication to projekt z dobrymi fundamentami architektonicznymi — świadomy wybór wzorców (Outbox Pattern, ShedLock, Client Credentials Flow, baza danych per serwis) i nowoczesny stack technologiczny (Java 21, Spring Boot 3.4.1, Angular 21). Widać dojrzałe myślenie o rozproszeniu odpowiedzialności.

Jednak projekt zawiera kilka **krytycznych błędów produkcyjnych**: bug uniemożliwiający poprawne zapisanie wyniku meczu (away goals zawsze = 0), brak Outbox Pattern na kluczowym przepływie finalizacji meczów (utrata eventów przy awarii RabbitMQ), oraz błędną konfigurację Flyway w dwóch serwisach. Przy uruchomieniu na więcej niż jednej instancji — projekt nie zadziała poprawnie ze względu na stan in-memory w `LiveMatchSimulationManager` i brak blokad w Outbox Publisher.

Projekt nadaje się do nauki i demonstracji wzorców, ale **wymaga naprawy krytycznych błędów przed wdrożeniem produkcyjnym**.

---

## Mocne strony projektu

### 1. Przemyślana architektura eventów (Outbox Pattern)

`wallet-service` i `coupon-service` implementują Outbox Pattern poprawnie — zdarzenia są zapisywane w tej samej transakcji co operacja biznesowa, a dopiero potem publikowane do RabbitMQ przez scheduler. Uodparnia to na utratę eventów przy awarii.

### 2. Shared Auto-Configuration Library (`poorbet-auth-starter`)

Centralizacja konfiguracji bezpieczeństwa w dedykowanej bibliotece to dobra decyzja. Wszystkie serwisy automatycznie dziedziczą spójną logikę walidacji JWT, rozróżnienie tokenów `user`/`service` i ochronę `/internal/**`. Biblioteka właściwie używa `@ConditionalOnMissingBean` — można nadpisać dowolny bean.

### 3. Database per Service

Każdy mikroserwis ma własną, izolowaną bazę PostgreSQL. Poprawna implementacja bez żadnych joinów między serwisami — zgodna z zasadami mikroserwisów.

### 4. ShedLock dla schedulerów

`MatchPoolScheduler` używa ShedLock z JDBC backstore — poprawne rozwiązanie dla środowisk wieloinstancyjnych. Scheduler nie wykona się równolegle na wielu instancjach.

### 5. Migracje Flyway (tam gdzie działają)

`match-service` i `coupon-service` mają poprawne migracje Flyway z `ddl-auto: validate` — zgodne z regułami projektu i bezpieczne produkcyjnie.

### 6. Separacja `internal/` vs `api/` endpointów

Architektoniczne rozróżnienie endpointów wewnętrznych (serwis-do-serwisu) od publicznych (użytkownik) jest dobrą praktyką, dobrze zintegrowaną z logiką autoryzacji token_type.

### 7. ML Model w osobnym serwisie

`odds-engine-service` izoluje logikę ML (Smile library, Random Forest/Gradient Boosting) od logiki domenowej — właściwy podział odpowiedzialności. Custom `ModelReadinessHealthIndicator` jest profesjonalnym rozwiązaniem.

### 8. Resilience4j w match-service

Świadome dodanie mechanizmu odporności na błędy przy komunikacji z `odds-engine-service`.

---

## Problemy architektoniczne

### [CRITICAL] C1 — Bug: Bramki gości zawsze = 0 po zakończeniu meczu ✅ NAPRAWIONY

**Lokalizacja:** `match-service/.../MatchFinishServiceImpl.java`

Brakująca linia `match.setAwayGoals(event.getAwayScore())` została dodana.

---

### [CRITICAL] C2 — Race condition w OutboxPublisher — duplikacja eventów przy wielu instancjach

**Lokalizacja:**  
- `wallet-service/src/main/java/com/poorbet/walletservice/infrastructure/scheduler/OutboxPublisher.java:39-68`  
- `coupon-service/src/main/java/com/poorbet/couponservice/infrastructure/scheduler/OutboxPublisher.java` (identyczny wzorzec)

**Opis problemu:**

```java
@Scheduled(fixedDelay = 5000)
public void publishEvents() {
    List<OutboxEvent> events = outboxRepository.findTop100ByStatus("NEW");
    // brak SELECT FOR UPDATE — przy 2 instancjach oba czytają te same eventy
    for (OutboxEvent event : events) {
        rabbitTemplate.convertAndSend(...);  // wysłane przez instancję A i B
        event.setStatus("SENT");
        outboxRepository.save(event);        // obie zapisują "SENT"
    }
}
```

Przy 2+ instancjach każdy event może zostać wysłany do RabbitMQ wielokrotnie. Brak `@Transactional` — status "SENT" może nie zostać zapisany jeśli aplikacja padnie po wysłaniu do RabbitMQ.

**Konsekwencje:**  
Podwójne wygrane (kupon rozliczony dwukrotnie), podwójne powiadomienia. W przypadku awarii — event może zostać wysłany bez oznaczenia jako SENT, powodując potencjalnie nieskończoną pętlę.

**Rekomendacja:**  
Użyć `SELECT FOR UPDATE SKIP LOCKED` — natywne zapytanie z `SKIP LOCKED` pozwala każdej instancji wziąć inny batch bez kolizji:

```java
// W OutboxRepository:
@Query(value = "SELECT * FROM outbox_event WHERE status = 'NEW' " +
               "ORDER BY created_at LIMIT 100 FOR UPDATE SKIP LOCKED", nativeQuery = true)
List<OutboxEvent> findTop100ByStatusForUpdate();
```

```java
// W OutboxPublisher:
@Transactional  // obowiązkowe — blokada trzymana przez czas transakcji
public void publishEvents() {
    List<OutboxEvent> events = outboxRepository.findTop100ByStatusForUpdate();
    ...
}
```

---

### [CRITICAL] C3 — Brak Outbox na krytycznym przepływie finalizacji meczu — Lost Events ✅ NAPRAWIONY

**Lokalizacja:** `match-service/.../MatchPoolLifecycleManager.java`

Zaimplementowano Outbox Pattern w match-service:
- Nowa tabela `outbox_event` (migracja `V6__create_outbox_event_table.sql`)
- `OutboxService`, `OutboxRepository`, `OutboxPublisher` w pakiecie `infrastructure/outbox`
- `MatchPoolLifecycleManager.handleMatchFinished()` zapisuje event do outbox w tej samej transakcji REQUIRES_NEW
- `OutboxPublisher` (scheduler co 5s) publikuje eventy do RabbitMQ z użyciem `FOR UPDATE SKIP LOCKED`

---

### [CRITICAL] C3b — Constraint `'PENDING'` vs kod `'NEW'` w coupon-service OutboxEvent ✅ NAPRAWIONY

**Lokalizacja:** `coupon-service/src/main/resources/db/migration/V2__create_outbox_event_table.sql`

**Opis problemu:**  
Był to **główny root cause** buga "kupon zostaje OPEN". Migracja V2 definiowała constraint:
```sql
CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
```
Ale `OutboxService.saveEvent()` zapisuje `status("NEW")`. Każde wywołanie `processFinishedMatch()`, gdy kupon miał przejść do WON/LOST, kończyło się `DataIntegrityViolationException` przy zapisie OutboxEvent, co rollbackowało całą transakcję — zakłady zostawały PENDING, kupon OPEN.

**Naprawa:** Migracja `V3__fix_outbox_status_constraint.sql`:
```sql
ALTER TABLE outbox_event DROP CONSTRAINT chk_coupon_outbox_status;
ALTER TABLE outbox_event ADD CONSTRAINT chk_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED'));
```

---

### [CRITICAL] C4 — Flyway path mismatch w wallet-service

**Lokalizacja:**  
- Konfiguracja: `wallet-service/src/main/resources/application.yml` → `locations: classpath:db/migration`  
- Pliki migracji: `wallet-service/src/main/resources/db.migration/` (z **kropką**, nie slashem)

**Opis problemu:**  
`classpath:db/migration` mapuje na katalog `db/migration` w zasobach. Pliki znajdują się w `db.migration` (z kropką) — to jest *inna ścieżka*. Flyway nie znajdzie żadnej migracji.

**Konsekwencje:**  
Tabele w wallet-service powstają tylko przez `ddl-auto: update` (Hibernate) — nie przez Flyway. Schematu nie można wersjonować, rollback jest niemożliwy. W trybie produkcyjnym (`ddl-auto: validate`) aplikacja by nie wystartowała.

**Rekomendacja:**  
Zmienić nazwę katalogu `db.migration` → `db/migration` (strukturę podkatalogu):
```
src/main/resources/
  db/
    migration/
      V1__init_table.sql
      V2__create_outbox_event_table.sql
      V3__create_wallet_reservations.sql
```

---

### [CRITICAL] C5 — Błędna nazwa pliku migracji w auth-service

**Lokalizacja:** `auth-service/src/main/resources/db/migration/V1_create_user_table.sql`

**Opis problemu:**  
Flyway wymaga nazewnictwa `V{numer}__{opis}.sql` z **podwójnym podkreślnikiem**. Plik `V1_create_user_table.sql` (pojedyncze podkreślenie) jest przez Flyway **ignorowany** — nie jest rozpoznawany jako migracja.

**Konsekwencje:**  
Tabela `users` nie powstaje przez Flyway. Aplikacja działa tylko dzięki `generate-ddl: true` i Hibernate — co maskuje problem. Przy wyłączeniu Hibernate DDL — auth-service nie będzie działać.

**Rekomendacja:**  
Zmienić nazwę pliku na `V1__create_user_table.sql`.

---


INFORMACJA - narazie tego nie robimy 
### [HIGH] H1 — LiveMatchSimulationManager — stan in-memory niekompatybilny z wieloma instancjami

**Lokalizacja:** `match-service/src/main/java/com/poorbet/matchservice/match/matchpool/service/LiveMatchSimulationManager.java`

**Opis problemu:**

```java
private final Map<UUID, LiveMatchSimulation> simulations = new ConcurrentHashMap<>();
private final Sinks.Many<LiveMatchEventDto> sink = Sinks.many().replay().limit(10);
```

Symulacje meczów live i strumień SSE żyją **wyłącznie w pamięci jednej instancji**. Przy load balancerze z 2+ instancjami:
- klient połączony z instancją A nie dostanie eventów z symulacji działającej na instancji B
- `notifyPoolFinished()` emituje event tylko do klientów swojej instancji

**Konsekwencje:**  
Użytkownicy losowo nie widzą wyników live. Frontend wydaje się "zamrożony". Niemożliwe poziome skalowanie match-service.

**Rekomendacja:**  
Zastąpić in-memory sink strumieniem Redis Pub/Sub lub RabbitMQ (topic exchange). SSE endpoint powinien subskrybować się do wspólnego brokera zamiast lokalnego `Sinks.Many`.

---

### [HIGH] H5 — MatchPoolServiceImpl.startPool() wywołuje symulację wewnątrz transakcji

**Lokalizacja:** `match-service/src/main/java/com/poorbet/matchservice/match/matchpool/service/MatchPoolServiceImpl.java:27-39`

**Opis problemu:**

```java
@Transactional
public void startPool(UUID poolId) {
    ...
    pool.setStatus(PoolStatus.STARTED);
    matchPoolRepository.save(pool);
    matchPoolSimulationService.startPoolSimulation(pool.getId()); // wewnątrz transakcji!
}
```

Symulacja startuje **przed** commitem transakcji. Jeśli symulacja natychmiast odczyta dane puli — zobaczy stary stan (BETTABLE), bo commit jeszcze nie nastąpił.

Projekt już ma `AfterCommitHandler` (używany w `MatchFinishServiceImpl`) do tego rodzaju problemu — ale nie jest stosowany tutaj.

**Rekomendacja:**

```java
@Transactional
public void startPool(UUID poolId) {
    ...
    matchPoolRepository.save(pool);
    afterCommitHandler.run(() ->
        matchPoolSimulationService.startPoolSimulation(pool.getId())
    );
}
```

---

## Problemy bezpieczeństwa

### [HIGH] S1 — SecurityConfiguration w auth-service używa field injection

**Lokalizacja:** `auth-service/src/main/java/com/poorbet/authservice/config/SecurityConfiguration.java:20`

**Opis problemu:**

```java
@Autowired
private CorsProperties corsProperties; // narusza reguły projektu (spring.md)
```

Field injection jest jawnie zakazane przez reguły projektu (`spring.md`: "Wyłącznie constructor injection"). Naruszenie spójności kodu — klasy trudniejsze do testowania i podatne na `NullPointerException` w specyficznych kontekstach.

**Rekomendacja:**

```java
private final CorsProperties corsProperties;

public SecurityConfiguration(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
}
```

---

### [HIGH] S2 — Brak walidacji @Valid na InternalWalletController.reserve()

**Lokalizacja:** `wallet-service/src/main/java/com/poorbet/walletservice/controller/InternalWalletController.java:27-33`

**Opis problemu:**

```java
public void debit(@RequestBody @Valid DebitWalletRequest request)   // ma @Valid
public void reserve(@RequestBody ReserveRequest request)             // brak @Valid
```

`reserve()` nie waliduje żądania przychodzącego z `coupon-service`. Jeśli `amount` jest null lub ujemne — wyjątek pojawi się głębiej w serwisie, bez czytelnego komunikatu błędu.

---

### [MEDIUM] S3 — gateway.poorbet.security.enabled: false w obu profilach

**Lokalizacja:** `gateway/src/main/resources/application.yaml`

**Opis problemu:**

```yaml
poorbet:
  security:
    enabled: false  # w production yaml!
```

Gateway nie weryfikuje tokenów JWT — polega na tym, że serwisy backend'owe robią to same. Oznacza to, że żądanie do endpointu `/api/users/register` przechodzi przez gateway bez żadnej weryfikacji na poziomie bramki. Gateway staje się prostym proxy, a nie "security perimeter".

**Ocena:** Akceptowalne dla uproszczonego projektu — backend serwisy weryfikują tokeny. Jednak w produkcji gateway powinien przynajmniej sprawdzać obecność i ważność tokenu.

---

### [MEDIUM] S4 — auth-service generate-ddl: true przy aktywnym Flyway

**Lokalizacja:** `auth-service/src/main/resources/application.yaml`

```yaml
spring:
  jpa:
    generate-ddl: true  # PROBLEMATYCZNA kombinacja z Flyway
```

`generate-ddl: true` powoduje, że Spring/Hibernate automatycznie generuje DDL. Przy aktywnym Flyway mogą pojawić się konflikty lub nieprzewidywalne zachowania — np. Hibernate tworzy tabelę, Flyway próbuje ją ponownie utworzyć.

---

### [LOW] S5 — Brak obsługi token revocation

**Opis problemu:**  
JWT są ważne przez 15 minut bez możliwości odwołania. Nie ma blacklisty tokenów. Wylogowanie użytkownika nie unieważnia jego tokenu — może go używać do wygaśnięcia.

---

## Problemy jakości kodu

### [HIGH] Q1 — wallet-service: ddl-auto: update zamiast validate

**Lokalizacja:** `wallet-service/src/main/resources/application.yml`

```yaml
jpa:
  hibernate:
    ddl-auto: update  # NIEBEZPIECZNE w produkcji
```

Reguły projektu (`spring.md`) wyraźnie wymagają `validate`. `update` może automatycznie modyfikować schemat bazy w produkcji — usuwać kolumny, zmieniać typy — bez żadnej kontroli. Jedno wdrożenie może nieodwracalnie uszkodzić dane.

---

### [HIGH] Q2 — CouponService.createCoupon() — N+1 HTTP calls + niekompletna obsługa błędów

**Lokalizacja:** `coupon-service/src/main/java/com/poorbet/couponservice/service/CouponService.java:55-73`

**Opis problemu:**

```java
walletClient.reserve(...);  // 1 HTTP call

dto.getBets().forEach(betDto -> {
    var snapshot = matchClient.getBetSnapshot(...); // N HTTP calls!
    ...
});
```

Dla kuponu z 5 zakładami = **6 synchronicznych HTTP calls** w jednej metodzie transakcyjnej. Każde wywołanie blokuje wątek. Przy zwiększonym ruchu — szybkie wyczerpanie puli wątków.

Ponadto: `catch (WalletTechnicalException)` wysyła event `COUPON_CREATION_FAILED` (żeby zwolnić rezerwację), ale jeśli `matchClient.getBetSnapshot()` rzuci inny wyjątek — rezerwacja zostaje **zablokowana** bez możliwości zwolnienia.

**Rekomendacja:**  
Pobrać snapshoty wszystkich meczów w jednym wywołaniu batch (`matchClient.getBetSnapshots(List<UUID>)`), dodać obsługę wszystkich wyjątków po `walletClient.reserve()`.

---

### [HIGH] Q3 — handleUserCreated() — transakcja oznaczona rollback-only przy idempotencji

**Lokalizacja:** `wallet-service/src/main/java/com/poorbet/walletservice/service/WalletService.java:34-52`

**Opis problemu:**

```java
@Transactional
public void handleUserCreated(UUID userId) {
    try {
        walletRepository.save(wallet);     // rzuca DataIntegrityViolationException
        outboxService.saveEvent(...);       // nigdy nie zostanie zapisany
    } catch (DataIntegrityViolationException e) {
        log.info("exists = {}", e.getMessage()); // transakcja już rollback-only!
    }
}
```

Gdy `DataIntegrityViolationException` zostaje rzucony przez JPA (np. duplikat `user_id`), Spring **oznacza bieżącą transakcję jako rollback-only**. Złapanie wyjątku na poziomie serwisu nie "ratuje" transakcji — żaden zapis po tym miejscu (w tym `outboxService.saveEvent`) nie zostanie skomitowany.

**Rekomendacja:**  
Sprawdzić istnienie portfela **przed** zapisem (zamiast catch na wyjątek):

```java
@Transactional
public void handleUserCreated(UUID userId) {
    if (walletRepository.existsByUserId(userId)) {
        log.info("Wallet already exists for user {}", userId);
        return;
    }
    walletRepository.save(wallet);
    outboxService.saveEvent(WalletEvents.WALLET_CREATED, new WalletCreatedEvent(userId));
}
```

---

### [HIGH] Q4 — jakarta.transaction.Transactional zamiast Spring w MatchFinishServiceImpl

**Lokalizacja:** `match-service/src/main/java/com/poorbet/matchservice/match/matchpool/service/MatchFinishServiceImpl.java:1`

**Opis problemu:**

```java
import jakarta.transaction.Transactional;  // CDI — BŁĘDNY import
// Powinno być:
import org.springframework.transaction.annotation.Transactional; // Spring
```

Import CDI `jakarta.transaction.Transactional` zamiast Springowego oznacza, że:
- propagacja transakcji jest inna (brak `Propagation.REQUIRES_NEW` etc.)
- `readOnly = true` nie działa
- Spring AOP może nie przechwycić adnotacji w niektórych scenariuszach proxy

---

### [MEDIUM] Q5 — Outbox status jako magic String zamiast enum

**Lokalizacja:** `OutboxService.java`, `OutboxPublisher.java` w obu serwisach

```java
.status("NEW")                          // magic string
outboxRepository.findTop100ByStatus("NEW") // magic string
event.setStatus("SENT");               // magic string
event.setStatus("FAILED");             // magic string
```

Literówka (`"NEW "`, `"new"`, `"NEWA"`) spowoduje ciche pominięcie eventów bez żadnego błędu kompilacji.

**Rekomendacja:**  
Enum `OutboxStatus { NEW, SENT, FAILED }` i zmiana kolumny na `@Enumerated(EnumType.STRING)`.

---

### [MEDIUM] Q6 — Błędny identyfikator w logowaniu WalletService.handleCouponWon()

**Lokalizacja:** `wallet-service/src/main/java/com/poorbet/walletservice/service/WalletService.java:66`

```java
.orElseThrow(() -> new IllegalStateException("Wallet not found: " + event.couponId()));
// Powinno być: event.userId()
```

Komunikat błędu wskazuje `couponId` zamiast `userId` — dezorientuje podczas debugowania.

---

### [MEDIUM] Q7 — @JsonManagedReference na encji JPA

**Lokalizacja:** `coupon-service/src/main/java/com/poorbet/couponservice/domain/Coupon.java:43`

```java
@JsonManagedReference
@OneToMany(...)
private List<Bet> bets = new ArrayList<>();
```

Adnotacja Jacksona (`@JsonManagedReference`) na encji JPA miesza warstwy: domenową z serializacją JSON. Encje nie powinny zależeć od bibliotek serializacji — to odpowiedzialność DTO i mapperów.

---

### [LOW] Q8 — @AllArgsConstructor zamiast @RequiredArgsConstructor

**Lokalizacja:**  
- `coupon-service/CouponService.java` (`@AllArgsConstructor`)  
- `match-service/MatchPoolLifecycleManager.java` (`@AllArgsConstructor`)

`@AllArgsConstructor` generuje konstruktor dla **wszystkich** pól, w tym opcjonalnych i tymczasowych. Przy dodaniu `@Builder.Default` lub pola transient — konstruktor się zmienia niespodziewanie. `@RequiredArgsConstructor` (dla `final` pól) jest bezpieczniejszy.

---

### [LOW] Q9 — notification-service: @EnableScheduling bez żadnego @Scheduled

**Lokalizacja:** `notification-service/src/main/java/com/poorbet/notificationservice/NotificationServiceApplication.java`

`@EnableScheduling` bez schedulerów to pozostałość po usuniętym kodzie. Inicjalizuje framework schedulera bez celu.

---

## Problemy wydajnościowe

### [HIGH] P1 — Brak indeksów na tabeli match

**Lokalizacja:** `match-service/src/main/resources/db/migration/V1__create_match_bets_table.sql`

Tabela `match` nie ma indeksów mimo że zapytania używają: `pool_id`, `status`, `home_team_id`, `away_team_id`. Przy 50 meczach na pulę i 1000+ pul — pełny scan tabeli przy każdym sprawdzeniu stanu puli.

---

### [HIGH] P2 — Outbox Publisher — brak indeksu na outbox_event (wallet-service)

**Lokalizacja:** `wallet-service/src/main/resources/db.migration/V2__create_outbox_event_table.sql`

`coupon-service` ma indeks `idx_coupon_outbox_status_created_at ON outbox_event(status, created_at)`. `wallet-service` go nie ma — przy każdym wywołaniu schedulera (co 5 sekund!) pełny scan tabeli outbox.

---

### [MEDIUM] P3 — OutboxPublisher: pojedynczy save() per event zamiast batch

**Lokalizacja:** `wallet-service/.../OutboxPublisher.java:59-62`

```java
for (OutboxEvent event : events) {
    ...
    outboxRepository.save(event); // N osobnych UPDATE'ów do bazy
}
```

100 eventów = 100 osobnych UPDATE'ów. Użycie `saveAll()` po pętli lub `@Modifying @Query` zbiorczego UPDATE'a byłoby znacznie wydajniejsze.

---

### [LOW] P4 — Brak paginacji w OutboxPublisher (stały limit 100)

`findTop100ByStatus("NEW")` — przy dużym backlogu kolejne cykle mogą nie nadążać. Brakuje monitorowania długości kolejki outbox.

---

## Problemy z Docker i wdrożeniem

### [HIGH] D1 — Brak healthchecks i condition: service_healthy w depends_on

**Lokalizacja:** `docker-compose.yml`

Tylko `auth-service` ma healthcheck. Żaden serwis nie używa `condition: service_healthy` w `depends_on`. Docker uruchamia serwisy sekwencyjnie tylko pod względem startu kontenera — nie gotowości aplikacji. Przy zimnym starcie: `match-service` startuje zanim PostgreSQL jest gotowy → crash loop.

Reguły projektu (`docker.md`) explicite wymagają healthchecków i warunków `service_healthy`.

**Rekomendacja** — dla każdej bazy danych:

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U $$POSTGRES_USER"]
  interval: 10s
  timeout: 5s
  retries: 5
```

Dla każdego serwisu Spring Boot:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health/readiness"]
  interval: 15s
  timeout: 5s
  retries: 5
  start_period: 30s
```

I w `depends_on`:

```yaml
depends_on:
  match-db:
    condition: service_healthy
  rabbitmq:
    condition: service_healthy
```

---

### [MEDIUM] D2 — Brak profilu application-prod.yml

Żaden serwis nie ma `application-prod.yml`. Konfiguracja produkcyjna jest de facto taka sama jak domyślna `application.yml` (z zmiennymi środowiskowymi). Brakuje dedykowanego profilu prod z:
- `ddl-auto: validate` (wszędzie)
- `show-sql: false`
- zaostrzonym logowaniem
- ustawieniami connection pool (HikariCP)

---

### [MEDIUM] D3 — Niespójne bazy obrazów Dockerfile

**Lokalizacja:** różne Dockerfiles

- `wallet-service`: `eclipse-temurin:21-jdk-alpine` (brak multi-stage build)
- `notification-service`: `eclipse-temurin:21-jdk-alpine` (brak multi-stage build)
- `match-service`: `eclipse-temurin:21-jre-alpine` (runtime)
- `gateway`: `eclipse-temurin:21-jdk` (pełne JDK w runtime!)

Reguły projektu wymagają `eclipse-temurin:21-jre-alpine` jako obraz runtime. `wallet-service` i `notification-service` nie mają multi-stage build — kopiują gotowy JAR, ale używają JDK zamiast JRE (większy obraz). `gateway` używa pełnego JDK w runtime bez uzasadnienia.

---

### [LOW] D4 — Brak USER w Dockerfiles

Reguły projektu (`docker.md`) wymagają `USER 1001` — żaden Dockerfile nie uruchamia aplikacji jako non-root. W kontenerze uruchomionym jako root — eskalacja uprawnień przy podatności jest trywialnie łatwa.

---

## Analiza Event Driven Architecture

### Przepływ zakończenia meczu — analiza krytyczna

```
MatchPoolScheduler (ShedLock ✅)
  → MatchPoolServiceImpl.startPool() ← PROBLEM H5 (symulacja w transakcji)
      → LiveMatchSimulation (in-memory ← PROBLEM H1)
          → MatchFinishServiceImpl.finishMatch() ← PROBLEM C1 (away goals = 0)
              → MatchPoolLifecycleManager.handleMatchFinished()
                  → sendPoolFinishedEventsAsync() ← PROBLEM C3 (brak Outbox!)
                      → RabbitMQ: MATCH_FINISHED
                          → CouponService.processFinishedMatch()
                              → OutboxPublisher (COUPON_WON/LOST) ← PROBLEM C2 (race condition)
                                  → WalletService.handleCouponWon()
```

**Kluczowy wniosek:** Krytyczny przepływ biznesowy (rozliczenie kuponów po meczu) ma **4 krytyczne defekty** na 8 kroków. Przy jednoczesnym wystąpieniu C1+C3 — kupony mogą być rozliczone z błędnym wynikiem i/lub nigdy nie zostać rozliczone.

### Idempotencja

- `WalletService.handleCouponWon()` sprawdza `reservation.getStatus() == COMMITTED` — idempotencja ✅
- `CouponProcessingService.processFinishedMatch()` — brak sprawdzenia czy kupon nie był już rozliczony. Przy duplikacie eventu (C2) — kupon zostanie przetworzony dwukrotnie.

### Gwarancja dostarczenia

- RabbitMQ bez `persistent: true` dla wiadomości — przy restarcie RabbitMQ nieutrwalone wiadomości przepadają.
- Brak konfiguracji Dead Letter Queue (DLQ) — wiadomości których nie udało się przetworzyć są tracone.

---

## Rekomendacje Senior Developera

### 1. Napraw błąd away goals — 30 minut roboty, production-blocker

Jeden brakujący `setter` blokuje podstawową funkcję projektu.

### 2. Wdróż Outbox Pattern w match-service dla MATCH_FINISHED

Obecna implementacja `sendPoolFinishedEventsAsync` to "fire and forget" bez gwarancji dostarczenia. Wzorzec jest już w projekcie — wystarczy przenieść.

### 3. Napraw OutboxPublisher — SELECT FOR UPDATE SKIP LOCKED

Bez tej zmiany projekt **nie może działać na więcej niż 1 instancji** żadnego z serwisów wysyłających eventy.

### 4. Ujednolicić konfigurację Flyway

`wallet-service` i `auth-service` mają błędy w konfiguracji Flyway które maskuje Hibernate. Napraw strukturę katalogów i nazewnictwo pliku zanim projekt zostanie wdrożony na środowisko bez `ddl-auto: update`.

### 5. Rozdziel konfigurację na profile: default, dev, prod

Brak profilu `prod` to techłdług który się pojawi w momencie pierwszego wdrożenia produkcyjnego.

---

## Quick Wins (do wdrożenia w 1 dzień)

| # | Zadanie | Status | Impact |
|---|---------|--------|--------|
| QW1 | Dodaj `match.setAwayGoals(event.getAwayScore())` | ✅ Naprawiony | **Critical** |
| QW2 | Zmień `V1_create_user_table.sql` → `V1__create_user_table.sql` | ✅ Naprawiony | Critical |
| QW3 | Przenieś pliki wallet migracji `db.migration` → `db/migration` | ✅ Naprawiony | Critical |
| QW4 | Zmień `ddl-auto: update` → `validate` w wallet-service | ✅ Naprawiony | High |
| QW5 | Zmień import `jakarta.transaction.Transactional` na Spring | ✅ Naprawiony | High |
| QW6 | Zmień `@Autowired` field → constructor injection w SecurityConfiguration | ✅ Naprawiony | Medium |
| QW7 | Napraw wiadomość błędu: `event.couponId()` → `event.userId()` | ✅ Naprawiony | Low |
| QW8 | Usuń `@EnableScheduling` z notification-service | ✅ Naprawiony | Low |
| QW9 | `@AllArgsConstructor` → `@RequiredArgsConstructor` w MatchPoolLifecycleManager | ✅ Naprawiony | Low |

---

## Średnioterminowe usprawnienia (1–4 tygodnie)

### MT1 — Outbox Pattern dla MATCH_FINISHED w match-service ✅ NAPRAWIONY

Zaimplementowano — patrz C3 powyżej.

### MT2 — SELECT FOR UPDATE SKIP LOCKED w OutboxPublisher

Zmiana zapytania w obu `OutboxRepository` + dodanie `@Transactional` na `publishEvents()`. Kluczowe dla multi-instancji.

### MT3 — Healthchecks i depends_on z warunkami w docker-compose ✅ NAPRAWIONY

Dodano healthchecki dla wszystkich baz danych (pg_isready), RabbitMQ (rabbitmq-diagnostics ping), Redis (redis-cli ping) oraz serwisów Spring Boot (actuator/health/readiness). Zmieniono `depends_on` na `condition: service_healthy`.

### MT4 — Refaktor handleUserCreated() — pre-check zamiast catch

Zastąpić `catch (DataIntegrityViolationException)` na `existsByUserId()` pre-check.

### MT5 — Batch endpoint w match-service dla snapshots

`matchClient.getBetSnapshot()` wywołuje N razy dla N zakładów. Dodać `getBetSnapshots(List<UUID>)` i zastąpić pętlę jednym wywołaniem.

### MT6 — Outbox status jako enum

Zastąpić magic strings `"NEW"/"SENT"/"FAILED"` enumem `OutboxStatus` w obu serwisach.

### MT7 — application-prod.yml dla każdego serwisu

Stworzyć pliki `application-prod.yml` z: `show-sql: false`, `ddl-auto: validate`, zaostrzonym logowaniem, Actuator security.

### MT8 — Indeksy dla tabeli match

Dodać brakujące indeksy (patrz migracje SQL poniżej).

### MT9 — Dead Letter Queue dla RabbitMQ

Skonfigurować DLQ dla każdej kolejki — wiadomości które nie mogą być przetworzone trafiają do DLQ zamiast być tracone.

### MT10 — Ujednolicić Dockerfiles

Wszystkie runtime obrazy na `eclipse-temurin:21-jre-alpine`. Dodać `USER 1001` do wszystkich Dockerfiles. Multi-stage build dla `wallet-service` i `notification-service`.

---

## Długoterminowy plan rozwoju architektury

### DL1 — LiveMatchSimulation przez Redis Pub/Sub lub WebSocket

Zastąpić `ConcurrentHashMap<UUID, LiveMatchSimulation>` i in-memory `Sinks.Many` wspólnym brokerem. Opcje:
- **Redis Pub/Sub** — najprostsze, pasuje bo Redis już jest w projekcie
- **RabbitMQ fanout exchange** — spójne z resztą architektury
- **WebSocket zamiast SSE** — lepsze wsparcie dla połączeń przy load balancerze

### DL2 — API Gateway jako Security Perimeter

Obecny gateway jest prostym reverse proxy. W produkcji warto dodać:
- rate limiting per użytkownik
- walidację JWT na poziomie gateway (żeby odciążyć backend serwisy)
- centralne logowanie żądań (access log)

### DL3 — Observability Stack

Projekt nie ma żadnego monitorowania. Minimalna produkcyjna konfiguracja:
- **Micrometer + Prometheus** dla metryk (Spring Boot Actuator już jest w odds-engine)
- **Distributed tracing** (Micrometer Tracing z Zipkin/Jaeger) — krytyczne dla debugowania przepływów między serwisami
- **Structured logging** (JSON przez Logback/Logstash)
- Metryki długości kolejki outbox (alert gdy `status = 'NEW'` rośnie)

### DL4 — Saga Pattern dla tworzenia kuponu

Obecnie `CouponService.createCoupon()` to synchroniczna "quasi-saga":
1. reserve() w wallet-service  
2. getBetSnapshot() w match-service (N razy)  
3. save() coupon  

Brak kompensacji przy błędzie kroku 2 lub 3 (rezerwacja zostaje zablokowana). Formalna Choreography Saga z eventami (`RESERVATION_CREATED` → `SNAPSHOTS_FETCHED` → `COUPON_CREATED`) lub Orchestration Saga dałaby pełną kontrolę nad kompensacją.

### DL5 — Kubernetes Readiness

Przed K8s wymagane:
- wszystkie serwisy: `/actuator/health/liveness` i `/actuator/health/readiness`  
- `HorizontalPodAutoscaler` ma sens dopiero po naprawie H1 i C2  
- ConfigMaps zamiast ENV w docker-compose  
- Secrets przez Vault lub K8s Secrets zamiast `.env`

### DL6 — Event Schema Registry

Przy wzroście liczby serwisów i eventów — brak wersjonowania schematu eventów staje się problemem. Rozważyć Avro + Confluent Schema Registry lub OpenAPI async specification.

---

## Brakujące migracje SQL

### Match Service — V5__add_match_indexes.sql

Plik: `match-service/src/main/resources/db/migration/V5__add_match_indexes.sql`

```sql
-- Indeks dla wyszukiwania meczów po puli (najczęstsze zapytanie)
CREATE INDEX IF NOT EXISTS idx_match_pool_id ON match(pool_id);

-- Indeks dla filtrowania po statusie (np. countByPoolIdAndStatus)
CREATE INDEX IF NOT EXISTS idx_match_pool_id_status ON match(pool_id, status);

-- Indeksy dla drużyn (join/lookup)
CREATE INDEX IF NOT EXISTS idx_match_home_team_id ON match(home_team_id);
CREATE INDEX IF NOT EXISTS idx_match_away_team_id ON match(away_team_id);
```

### Wallet Service — V4__add_outbox_index.sql

Plik: `wallet-service/src/main/resources/db/migration/V4__add_outbox_index.sql`

```sql
-- Indeks kluczowy dla OutboxPublisher (zapytanie co 5 sekund)
CREATE INDEX IF NOT EXISTS idx_wallet_outbox_status_created_at
    ON outbox_event(status, created_at);
```

---

## Priorytetowa lista zmian

Posortowana od największego wpływu biznesowego i technicznego:

| Priorytet | ID | Problem | Status | Kategoría |
|-----------|-----|---------|--------|-----------|
| 1 | C1 | Bug: away goals = 0 po meczu | ✅ Naprawiony | Business-blocker |
| 2 | C3b | Constraint 'PENDING' vs 'NEW' w coupon OutboxEvent | ✅ Naprawiony | Root cause "kupon OPEN" |
| 3 | C3 | Brak Outbox dla MATCH_FINISHED — lost events | ✅ Naprawiony | Data loss |
| 4 | C4 | Flyway path mismatch w wallet-service | ✅ Naprawiony | Infrastructure |
| 5 | C5 | Błędna nazwa migracji w auth-service | ✅ Naprawiony | Infrastructure |
| 6 | D1 | Brak healthchecks w docker-compose | ✅ Naprawiony | Operations |
| 7 | C2 | Race condition OutboxPublisher — duplikaty | ⚠️ Częściowo (SKIP LOCKED już w repo) | Multi-instance |
| 8 | H1 | LiveMatchSimulationManager in-memory | Nie naprawiony | Multi-instance |
| 9 | Q2 | N+1 HTTP calls + niekompletna kompensacja | ✅ Naprawiony | Performance + correctness |
| 10 | Q3 | rollback-only w handleUserCreated | ✅ Naprawiony | Correctness |
| 11 | H5 | startPool() — symulacja wewnątrz transakcji | ✅ Naprawiony | Correctness |
| 12 | Q4 | jakarta.transaction.Transactional | ✅ Naprawiony | Correctness |
| 13 | MT9 | Dead Letter Queue | ✅ Naprawiony | Reliability |
| 14 | DL3 | Observability (metrics + tracing) | Nie naprawiony | Operations |
| 15 | Q5 | Magic strings w Outbox status | ✅ Naprawiony | Code quality |
| 16 | D2 | Brak application-prod.yml | ✅ Naprawiony | Operations |
| 17 | P1 | Brak indeksów na tabeli match | ✅ Naprawiony (V5) | Performance |
| 18 | P2 | Brak indeksu na outbox_event w wallet | ✅ Naprawiony (V4) | Performance |
| 19 | S1 | @Autowired field injection w SecurityConfig | ✅ Naprawiony | Code quality |

---

*Raport wygenerowany na podstawie analizy kodu źródłowego, plików konfiguracyjnych, migracji bazodanowych i konfiguracji Docker.*
