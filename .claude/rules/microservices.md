## Konwencje mikroserwisów

- Każdy serwis posiada własny schemat bazy danych — brak joinów między serwisami
- Komunikacja między serwisami:
  - REST (synchronizacyjnie) lub
  - zdarzenia (asynchronicznie) — wybór zależy od przypadku użycia
- Każdy serwis posiada własne migracje Flyway
- Wspólne DTO są udostępniane wyłącznie przez dedykowany moduł `shared-lib` — nigdy nie kopiuj ich ręcznie
- Endpointy healthcheck:
  - każdy serwis musi udostępniać `/actuator/health`
  - wymagane do poprawnego działania healthchecków w Dockerze