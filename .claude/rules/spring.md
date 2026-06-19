# Reguły Spring Boot — obowiązują w całym projekcie

## Wstrzykiwanie zależności
- Wyłącznie constructor injection — nigdy @Autowired na polach
- Jeśli konstruktor ma więcej niż 3 parametry, rozważ rozbicie klasy
- @RequiredArgsConstructor (Lombok) jest akceptowalne

## Konfiguracja
- Tylko application.yml — nie używamy application.properties
- Wartości grupowane przez @ConfigurationProperties z prefiksem
- Nigdy @Value dla wielu powiązanych właściwości — użyj @ConfigurationProperties
- Profile: local, dev, prod — każdy ma swój application-{profil}.yml

## Warstwa REST
- Nigdy nie zwracamy encji JPA z kontrolera — zawsze DTO
- Mapowanie Entity ↔ DTO przez dedykowany Mapper (np. MapStruct)
- Walidacja requestów przez @Valid + Bean Validation na DTO
- Globalny handler wyjątków: @RestControllerAdvice
- Kody HTTP muszą być semantyczne: 201 dla tworzenia, 204 dla usunięcia

## Transakcje
- @Transactional tylko na warstwie serwisowej — nigdy w kontrolerze
- Metody odczytu: @Transactional(readOnly = true)
- Uwaga na N+1: sprawdzaj zapytania przez spring.jpa.show-sql=true lokalnie

## Baza danych
- Migracje wyłącznie przez Flyway — zero ręcznego DDL
- Nazewnictwo migracji: V{numer}__{opis_snake_case}.sql
- spring.jpa.hibernate.ddl-auto=validate (nie create-drop, nie update!)
- Repozytoria dziedziczą po JpaRepository lub CrudRepository

## Autokonfiguracje (dotyczy modułów autoconfig)
- Rejestracja w: META-INF/spring/
  org.springframework.boot.autoconfigure.AutoConfiguration.imports
- Zawsze @ConditionalOn... — autokonfiguracja nie może być bezwarunkowa
- Właściwości przez @ConfigurationProperties z własnym prefiksem
- Dokumentuj: co bean robi, kiedy się aktywuje, jak nadpisać

## Testy
- Unit: JUnit 5 + Mockito — bez kontekstu Springa
- Integracyjne: Testcontainers z prawdziwym PostgreSQL — zero H2
- @SpringBootTest tylko gdy naprawdę potrzeba pełnego kontekstu
- Preferuj @WebMvcTest dla kontrolerów, @DataJpaTest dla repozytoriów
- Nazwa testu: should_{oczekiwany_rezultat}_when_{warunek}

## Bezpieczeństwo
- Nigdy nie logujemy danych wrażliwych (hasła, tokeny, PII)
- Stack trace nigdy nie trafia do odpowiedzi API
- Sekrety przez zmienne środowiskowe — nigdy hardkodowane w kodzie