# Plan wdrożenia deployu — PoorBetApplication

Ten plik to konkretna, etapowa checklista **dla tego repozytorium** — co trzeba zrobić, żeby stopniowo przejść od `make dev` do realnego deployu na serwerze. `deploy.md` to ogólny poradnik o publikowaniu obrazów na Docker Hub (przykłady z fikcyjnym `mycompany/api-service`) — poniżej to samo, ale osadzone w waszej strukturze 8 serwisów, `docker-compose.yml` + `docker-compose.dev.yml`, oraz w regułach z `.claude/rules/docker.md`.

**Rejestr obrazów: GitHub Container Registry (GHCR), nie Docker Hub.** Macie już skonfigurowane logowanie do GitHub Packages dla `poorbet-commons`/`poorbet-auth-starter` (ten sam PAT działa też do GHCR — potrzebuje tylko dodatkowo `write:packages`), a w CI wbudowany `GITHUB_TOKEN` może i budować obraz (czytając biblioteki z GitHub Packages), i pushować go do GHCR — bez żadnych dodatkowych sekretów w repo. Wszędzie w `deploy.md`, gdzie jest `docker.io`/`mycompany`, w praktyce u Was będzie `ghcr.io/dariusz95`.

Etapy są uporządkowane tak, żeby każdy dawał wymierną wartość sam w sobie — nie trzeba robić wszystkiego naraz.

---

## Etap 0 — Co już macie gotowe (punkt startowy)

- `docker-compose.yml` z healthcheckami dla większości serwisów Springowych i wszystkich baz (`pg_isready`, `/actuator/health/readiness`)
- Named volumes dla danych (`postgres_data`-styl: `user_data`, `match_data`, `wallet_data`, `coupon_data`) — zgodnie z regułą "nigdy bind mount na dane bazy"
- Osobna sieć `main_network` (bridge) zamiast domyślnej
- Multi-stage Dockerfile produkcyjne dla większości serwisów, użytkownik nierootowy `appuser` (UID 1001) — zgodnie z regułą "nigdy nie uruchamiamy jako root"
- Profil `application-prod.yml` istnieje już w **każdym** serwisie Springowym
- CI (`tests.yml`) uruchamiający testy per serwis na PR do `main`

To dobry fundament — reszta poniżej to luki między tym co jest, a tym co potrzebne do wdrożenia.

---

## Etap 1 — Naprawić i ujednolicić Dockerfile produkcyjne (fundament, zrobić najpierw) ✅ ZROBIONE

**Korekta w stosunku do pierwszej wersji tego planu:** zakładałem, że `auth-service/Dockerfile` i `match-service/Dockerfile` (budujące `poorbet-commons` lokalnie z katalogu `poorbet-commons/`) są wzorcem do naśladowania. To było błędne założenie — okazało się, że katalog `poorbet-commons/` **w ogóle nie istnieje** w tym repo (`git log -- poorbet-commons` pokazuje commit "Moving Commons to a separate repository" — biblioteka została wydzielona do osobnego repo `PoorBetCommons` i jest teraz pobierana wyłącznie z GitHub Packages, tak jak zresztą mówi `CLAUDE.md`). Czyli **wszystkie 7 Dockerfile serwisów Springowych** było zepsutych, każdy na swój sposób, nie tylko `gateway`.

Co zrobiłem:

- [x] Ujednoliciłem wszystkie 7 Dockerfile (`auth-service`, `gateway`, `match-service`, `coupon-service`, `wallet-service`, `notification-service`, `odds-engine-service`) na jeden wzorzec: multi-stage build z `maven:3.9.9-eclipse-temurin-21-jammy`, kontekst budowania = katalog główny repo, zależności `poorbet-commons`/`poorbet-auth-starter` pobierane z GitHub Packages podczas budowy.
- [x] Uwierzytelnienie do GitHub Packages przekazywane przez **build secrets** (`RUN --mount=type=secret`), zgodnie z sekcją bezpieczeństwa z `deploy.md` — token nie trafia do żadnej warstwy obrazu (plik `settings.xml` jest tworzony i usuwany w tym samym `RUN`). **Wymaga to dwóch sekretów przy buildzie:** `github_actor` (login) i `github_token` (PAT z uprawnieniem `read:packages`), np.:
  ```bash
  docker build --secret id=github_actor,src=./gh_actor.txt \
               --secret id=github_token,src=./gh_token.txt \
               -f auth-service/Dockerfile -t poorbet-auth-service .
  ```
- [x] Naprawiłem `gateway/Dockerfile` (usunięty błędny `COPY ../...`).
- [x] Dodałem brakujący etap budowania w `wallet-service/Dockerfile` i `notification-service/Dockerfile` (wcześniej tylko `COPY target/*.jar`, zakładały ręczny `mvn package` na hoście).
- [x] Ujednoliciłem runtime image do `eclipse-temurin:21-jre-alpine` wszędzie — `auth-service/Dockerfile` wcześniej używał `amazoncorretto:21-alpine3.20`, co łamało regułę z `.claude/rules/docker.md` ("Bazowy obraz runtime: eclipse-temurin:21-jre-alpine (nie JDK!)").
- [x] Ujednoliciłem `EXPOSE 8080` na wszystkich serwisach poza `gateway` (tam poprawnie `EXPOSE 8081`, bo `gateway/src/main/resources/application.yaml` jawnie ustawia `server.port: 8081` — to jedyny serwis z niestandardowym portem, resztę pilnuje domyślny port Springa 8080).
- [x] Dodałem **`.dockerignore` w katalogu głównym** — brakowało go, a teraz wszystkie buildy używają kontekstu = cały root repo (~635 MB z `.git`, `target/`, `node_modules`), więc bez niego każdy `docker build` byłby bardzo wolny i wysyłałby zbędne dane do demona Dockera.

Nie ruszałem (świadomie, poza zakresem "napraw build"):
- `frontend/Dockerfile` — działał poprawnie już wcześniej (nie zależy od `poorbet-commons`), zostawiony bez zmian.
- Rozbieżność `frontend/nginx.conf` (port 8080) vs reguła "Angular prod: 80" — to kwestia konwencji, nie bug, do decyzji później.

**Do zapamiętania na Etap 6 (CI, GHCR):** w GitHub Actions `github_actor`/`github_token` do build secrets to po prostu `${{ github.actor }}` i `${{ secrets.GITHUB_TOKEN }}` (z uprawnieniem `packages: write` w bloku `permissions:` workflow) — **ten sam token** posłuży do (a) uwierzytelnienia Mavena przy pobieraniu `poorbet-commons`/`poorbet-auth-starter` (build secret, jak wyżej) i (b) `docker login ghcr.io` przy pushu gotowego obrazu. Zero dodatkowych sekretów do skonfigurowania w repo — inaczej niż przy Docker Hub, gdzie trzeba by osobnego `DOCKER_USERNAME`/`DOCKER_PAT`.

---

## Etap 2 — `.env.example` ✅ ZROBIONE

Reguła w `.claude/rules/docker.md` mówi wprost: "`.env.example` zawsze aktualny — `.env` nigdy nie trafia do gita". Plik `.env.example` wcześniej nie istniał.

- [x] Stworzyłem `.env.example` w katalogu głównym z wszystkimi kluczami z `.env`/`.env.dev` (są identyczne w obu — sprawdziłem `diff`). Wartości nie-sekretne (nazwy baz, użytkownicy, porty, adresy `auth-service` w sieci Docker) zostawione jako sensowne domyślne; hasła i sekrety (`POSTGRES_*_PASSWORD`, `RABBITMQ_PASSWORD`, `JWT_SECRET`, `*_CLIENT_SECRET`) zastąpione placeholderem `changeme`.
- [x] Zaktualizowałem `CLAUDE.md` (sekcja "Pierwsze uruchomienie") — `cp .env.dev.example .env.dev` (plik, który nie istniał) zamienione na `cp .env.example .env.dev`, plus wzmianka, że ten sam wzorzec służy do stworzenia `.env` na produkcję.
- [x] Przy okazji naprawiłem błąd w przykładowym `~/.m2/settings.xml` w `CLAUDE.md` (sekcja 2) — był jeden `<server><id>github</id>`, a root `pom.xml` deklaruje dwa repozytoria: `github-commons` i `github-auth`. Maven dopasowuje dane logowania po dokładnym `id`, więc z jednym wpisem `github` uwierzytelnienie nie zadziałałoby dla żadnego z nich. To dokładnie ta sama konfiguracja, na którą polegają build secrets z Etapu 1.

---

## Etap 3 — `docker-compose.prod.yml` ✅ ZROBIONE

Reguła w `.claude/rules/docker.md` zakłada istnienie `docker-compose.prod.yml` obok `docker-compose.yml` (analogicznie do istniejącego `docker-compose.dev.yml`) — wcześniej nie istniał.

**Jak działa komunikacja między kontenerami w prod (bo to determinuje resztę tego etapu):** `docker-compose.yml` podpina wszystkie serwisy do sieci `main_network`, a Docker Compose ma wbudowany DNS tłumaczący nazwę serwisu (np. `auth-service`) na adres kontenera — dokładnie to samo już działa w dev, bo `gateway`'s `application.yaml` używa `http://auth-service:8080` itd. Kluczowe: `ports:` w compose wystawia port **na hosta**, nie ma nic wspólnego z komunikacją między kontenerami — te zawsze gadają po porcie wewnętrznym w ramach `main_network`, niezależnie od `ports:`. Stąd:

- [x] `docker-compose.prod.yml` z `image: ghcr.io/dariusz95/poorbet-<serwis>:${IMAGE_TAG}` dla każdego serwisu aplikacyjnego (`frontend`, `gateway`, `auth-service`, `match-service`, `coupon-service`, `wallet-service`, `notification-service`, `odds-engine-service`) — zero `build:`, serwer tylko `pull`uje gotowe obrazy z GHCR (Etap 5/6). Zmienna `IMAGE_TAG` jest wymagana (bez domyślnej wartości) — brak jej celowo psuje odwołanie do obrazu, żeby nie dało się przypadkiem odpalić bez świadomego podania wersji (nigdy `latest`).
- [x] `SPRING_PROFILES_ACTIVE=prod` dodane na każdym serwisie Springowym — potwierdziłem przez `docker compose config`, że merguje się poprawnie z istniejącymi zmiennymi z bazowego `docker-compose.yml` (`AUTH_ISSUER_URI`, `RABBITMQ_*` itd.), mimo że base plik używa formy mapy (`<<: *auth-config`), a prod overlay listy — Compose scala je po kluczu zmiennej, nie nadpisuje całości.
- [x] **Porty tylko na `frontend` (`80:8080`) i `gateway` (`8081:8081`)** — reszta serwisów aplikacyjnych, obie kolejki/cache (`redis`, `rabbitmq`) i wszystkie 4 bazy danych **bez `ports:` w ogóle** (a nie tylko bez portów baz, jak pierwotnie zakładałem — dotyczy każdego serwisu, który nie musi być osiągalny z zewnątrz). Baza `docker-compose.yml` już i tak nie definiuje żadnych `ports:` — to wyłącznie `docker-compose.dev.yml` je dodaje (razem z portami debugowania JDWP), więc w prod overlay wystarczyło ich po prostu nie powtórzyć.
- [x] Bez wolumenów bind-mount kodu — nie trzeba nic dodatkowo wyłączać, bo w bazowym `docker-compose.yml` ich i tak nie ma (są wyłącznie w `docker-compose.dev.yml`).
- [x] `restart: unless-stopped` na wszystkich serwisach aplikacyjnych; bazy zostają przy `restart: always` (już tak mają w bazowym pliku).
- [x] **Przy okazji:** `user-db/Dockerfile`, `match-db/Dockerfile`, `wallet-db/Dockerfile`, `coupon-db/Dockerfile` to gołe `FROM postgres:16` bez żadnej customizacji — więc w prod zamiast je budować/publikować, `docker-compose.prod.yml` ciągnie bezpośrednio `postgres:16` z Docker Hub.
- [x] Dodałem `COMPOSE_PROD` i targety `prod-pull`/`prod` w `Makefile`, analogicznie do istniejącego `COMPOSE_DEV`/`dev`.
- [x] Zweryfikowane przez `docker compose ... config` — merge z bazowym plikiem jest poprawny (sprawdziłem porty, obrazy, zmienne środowiskowe i wolumeny per serwis).

---

## Etap 4 — Uzupełnić brakujące healthchecki ✅ ZROBIONE

Reguła w `.claude/rules/microservices.md`: "każdy serwis musi udostępniać `/actuator/health`, wymagane do poprawnego działania healthchecków w Dockerze". Okazało się większe niż zakładałem — dwa osobne, poważniejsze problemy, nie tylko brakujące sekcje `healthcheck:`.

**Problem 1 — `gateway` i `notification-service` w ogóle nie miały `spring-boot-starter-actuator`.** Sprawdziłem `pom.xml` wszystkich 7 serwisów — `auth-service`, `match-service`, `coupon-service`, `wallet-service`, `odds-engine-service` mają actuator jawnie w zależnościach, `gateway` i `notification-service` nie. Endpoint `/actuator/health` po prostu tam nie istniał, więc jakikolwiek healthcheck by zawiódł niezależnie od konfiguracji Dockera.

- [x] Dodałem `spring-boot-starter-actuator` do `gateway/pom.xml` i `notification-service/pom.xml`.
- [x] Dodałem do obu `management.endpoints.web.exposure.include: health,info` + `management.endpoint.health.probes.enabled: true` (wzorzec skopiowany z `auth-service`) — bez tego endpoint `/actuator/health/readiness` (health groups) nie istnieje, tylko ogólny `/actuator/health`.
- [x] Sprawdziłem (rozpakowując i dekompilując lokalnie zcache'owany `.jar` `poorbet-auth-starter`), że biblioteka ma dedykowany, zawsze-permitujący `securityMatcher("/actuator/**")` — niezależny od `poorbet.security.enabled`/`unprotected-paths` aplikacji. Więc **nie trzeba** dopisywać `/actuator/health/**` do `unprotected-paths` w `gateway`/`notification-service` (te wpisy w innych serwisach są redundantne, nie wymagane).
- [x] Zweryfikowane: `gateway` kompiluje się poprawnie z nową zależnością (`mvn -f gateway/pom.xml compile`, korzystając z już skonfigurowanego `~/.m2/settings.xml` na Twoim hoście — nie dotykałem samego tokenu). `notification-service` na hoście nie skompilował się, ale to niezwiązany z tą zmianą problem — `RabbitConfig.java` (nietknięty przeze mnie) nie widzi pola `log` z Lomboka, klasyczny objaw niekompatybilności Lomboka z bardzo nowym JDK-iem na Twoim hoście (JDK 25), a nie z realnym buildem Dockera (który i tak wymusza JDK 21 przez obraz `maven:3.9.9-eclipse-temurin-21-jammy` z Etapu 1).

**Problem 2 — poważniejszy: `curl`, którego już używały wszystkie healthchecki w bazowym `docker-compose.yml`, w ogóle nie istnieje w obrazie produkcyjnym.** Zweryfikowałem to bezpośrednio (`docker run eclipse-temurin:21-jre-alpine which curl` → brak). Healthchecki "działały" wyłącznie w dev, bo `docker-compose.dev.yml` buduje z zupełnie innego obrazu (`maven:3.9.9-eclipse-temurin-21`, pełny Debian z curlem) — w prod (obraz z Etapu 1, `eclipse-temurin:21-jre-alpine`) każdy healthcheck zawiodłby, a przez `depends_on: condition: service_healthy` żaden zależny serwis by w ogóle nie wystartował. To była ukryta bomba zegarowa, która ujawniłaby się dopiero przy pierwszym realnym deployu.

- [x] Zamieniłem wszystkie 7 healthchecków (5 istniejących + 2 nowe) z `curl -f` na `wget --no-verbose --tries=1 --spider` — `wget` jest w `eclipse-temurin:21-jre-alpine` domyślnie (zweryfikowane przez `docker run`), więc nie trzeba doinstalowywać żadnego pakietu w Dockerfile.
- [x] Dodałem `healthcheck:` do `gateway` (port **8081** — nie 8080, bo `gateway/application.yaml` jawnie ustawia `server.port: 8081`) i `notification-service` (port 8080).
- [x] Zweryfikowane przez `docker compose config` — zarówno `docker-compose.prod.yml`, jak i `docker-compose.dev.yml` nadal poprawnie się scalają po tych zmianach w bazowym pliku.

---

## Etap 5 — Rejestr obrazów: GHCR i konwencja tagowania ✅ ZROBIONE (decyzje podjęte)

Zamiast Docker Hub z `deploy.md` — **GitHub Container Registry**, bo już macie skonfigurowane logowanie do GitHub Packages dla `poorbet-commons`/`poorbet-auth-starter`, a w CI ten sam wbudowany `GITHUB_TOKEN` obsłuży i pobieranie bibliotek, i push obrazów (Etap 6), bez dodatkowych sekretów.

- [x] Konwencja nazw: `ghcr.io/dariusz95/poorbet-<serwis>:<tag>`, np. `ghcr.io/dariusz95/poorbet-auth-service:0.1.0`. Nazwa właściciela **musi być małymi literami** (Docker wymaga lowercase w nazwach obrazów) — `dariusz95`, nie `Dariusz95`. Już użyte w `docker-compose.prod.yml` (Etap 3).
- [x] Tagować wersjami (`v0.1.0` itd.), **nigdy** samym `latest` w prod. Tag Gita `v*` wyzwoli workflow z Etapu 6.
  - **Decyzja:** jeden globalny tag dla wszystkich 8 serwisów (nie osobne wersje per serwis) — patrz uzasadnienie niżej.
- [x] **Decyzja: pakiety w GHCR będą publiczne.** Serwer produkcyjny robi zwykły `docker pull` bez logowania — mniej sekretów do zarządzania na serwerze (Etap 8 nie potrzebuje kroku `docker login ghcr.io`). Kod źródłowy zostaje w prywatnym repo — publiczne są wyłącznie zbudowane obrazy (nie zawierają sekretów, tylko skompilowany kod i wersje bibliotek). Trzeba będzie **ręcznie ustawić widoczność na "Public"** dla każdego z 8 pakietów w ustawieniach GHCR po pierwszym pushu z Etapu 6 (`Package settings → Change visibility`) — nowo utworzone pakiety domyślnie dziedziczą prywatność z repo źródłowego.
- [x] Powiązanie pakietu GHCR z repozytorium źródłowym dzieje się automatycznie przy pushu z `docker/build-push-action` (poprawny `GITHUB_TOKEN`) — nic dodatkowego do zrobienia, zweryfikować po pierwszym pushu w Etapie 6.

---

## Etap 6 — CI: automatyczny build i push obrazów do GHCR ✅ ZROBIONE

Nowy plik `.github/workflows/publish-images.yml`, uruchamiany po pushu tagu `v*` — nie trzeba nic ręcznie budować/pushować jak w `deploy.md` sekcja 4.

- [x] `permissions: contents: read, packages: write` na poziomie workflow — jedyne uprawnienie potrzebne do pushowania do GHCR, żadnych sekretów do dodania w ustawieniach repo.
- [x] Dwa osobne joby publikujące (analogicznie do podziału `test`/`frontend-test` w `tests.yml`): `publish-services` (macierz 7 serwisów Springowych, `context: .` + `file: <serwis>/Dockerfile`, z build secrets `github_actor`/`github_token` dla GitHub Packages — jak w Etapie 1) i `publish-frontend` (osobny job, `context: ./frontend`, bez sekretów Maven — frontend nie zależy od `poorbet-commons`).
- [x] `docker/login-action@v3` (`registry: ghcr.io`, `username: ${{ github.actor }}`, `password: ${{ secrets.GITHUB_TOKEN }}`) + `docker/build-push-action@v5`, tagi w formacie `ghcr.io/dariusz95/poorbet-<serwis>:${{ github.ref_name }}`.
- [x] **Bramkowanie zielonymi testami zrobione porządniej niż pierwotnie zakładałem.** `tests.yml` triggeruje się tylko na `pull_request: branches: [main]` — nie na push tagu — więc zwykłe `needs:` w tym samym workflow nie zadziała (to różne eventy), a `workflow_run` ma znane problemy z rozróżnianiem tagów od gałęzi w `head_branch`. Zamiast tego: dodałem `workflow_call:` do `on:` w `tests.yml` (bez zmiany istniejącej logiki testów) i `publish-images.yml` wywołuje go jako `uses: ./.github/workflows/tests.yml` z `needs: test` na obu jobach publikujących — zero duplikacji logiki testów, jeden faktyczny przebieg testów na tag, blokujący publikację obrazów w razie czerwonego wyniku.
- [x] **Przy okazji naprawiłem realną lukę w pokryciu testami:** macierz w `tests.yml` w ogóle nie obejmowała `auth-service` ani `gateway` (tylko `coupon-service`, `match-service`, `odds-engine-service`, `wallet-service`, `notification-service`). Skoro `publish-images.yml` miał bramkować publikację tych dwóch serwisów zielonymi testami, a testy w ogóle się dla nich nie uruchamiały, bramka byłaby fasadą — dodałem oba do macierzy.
- [ ] **Nie zweryfikowane end-to-end** — nie mogę tego przetestować bez realnego pusha tagu do GitHuba (celowo tego nie zrobiłem — to nieodwracalna akcja publikująca pakiety, zostawiam Tobie). Warto przy pierwszym tagu obserwować zakładkę Actions.

**Co ewentualnie skonfigurować ręcznie (poza kodem repo), przed/po pierwszym tagu:**
- Docker Hub: nic — nie jest używany.
- GitHub, do sprawdzenia *przed* pierwszym tagiem tylko jeśli push do GHCR zwróci 403: Settings repo → Actions → General → "Workflow permissions" (nowe repo domyślnie ma `GITHUB_TOKEN` tylko do odczytu; jawne `permissions: packages: write` w workflow zwykle to nadpisuje, ale warto wiedzieć gdzie sprawdzić). Żadnych sekretów (`DOCKER_USERNAME`/`DOCKER_PAT`) nie trzeba dodawać.
- GitHub, do zrobienia *po* pierwszym udanym pushu (jednorazowo, ręcznie w UI): `github.com/dariusz95?tab=packages` → każdy z 8 nowych pakietów → Package settings → Change visibility → Public (nowe pakiety domyślnie dziedziczą prywatność repo źródłowego, a zdecydowaliśmy się na publiczne — Etap 5).

---

## Etap 7 — Sekrety i konfiguracja produkcyjna

- [ ] Wygenerować **nowe** hasła/sekrety dla prod (`POSTGRES_*_PASSWORD`, `RABBITMQ_PASSWORD`, `JWT_SECRET`, `*_CLIENT_SECRET`) — nie kopiować wartości deweloperskich z `.env.dev`. `openssl rand -base64 32` na każdy sekret osobno.
- [x] ~~`AUTH_ISSUER_URI`/`AUTH_JWKS_URI` muszą wskazywać na publiczny adres produkcyjny~~ — **błędne założenie, poprawione.** Sprawdziłem w kodzie: te dwa URL-e są używane wyłącznie do komunikacji serwis-do-serwisu wewnątrz `main_network` (walidacja `iss` w JWT, pobranie kluczy JWKS) — przeglądarka nigdy ich nie odpytuje bezpośrednio. Zostają jak w dev: `http://auth-service:8080` / `http://auth-service:8080/.well-known/jwks.json`. Zmiana na publiczną domenę byłaby nie tylko zbędna, ale i myląca.
- [ ] Docelowo sekrety przez Docker secrets albo zewnętrzny vault (reguła `docker.md`: "Sekrety przez Docker secrets lub zewnętrzny vault — nie przez env w compose") — na start wystarczy `.env` na serwerze poza gitem, ale to świadomy dług do spłacenia później, nie od razu.

---

## Etap 8 — Serwer, reverse proxy, TLS, DNS

Tego `deploy.md` w ogóle nie porusza (kończy się na `docker compose pull && docker compose up -d`), a jest niezbędne do realnego wystawienia aplikacji. Poniżej konkretna checklista **zakładająca, że VPS i Docker już masz gotowe** (Docker Engine + plugin `docker compose` zainstalowane, dostęp SSH działa) — zostaje tylko warstwa aplikacji i sieci.

**Ważna zmiana od czasu napisania pierwszej wersji tego etapu:** `frontend/nginx.conf` ma teraz własny `location /api/` proxujący do `http://gateway:8081` **wewnątrz sieci Docker** (naprawiony w tej sesji — wcześniej frontend serwował tylko statyki, więc każde żądanie `/api/**` z przeglądarki dostawało 405, bo trafiało w `try_files` zamiast do gatewaya). To upraszcza resztę tego etapu: reverse proxy na hoście musi znać **tylko jeden upstream — kontener `frontend`**, nie musi dzielić ruchu między `frontend` i `gateway` po prefiksie ścieżki. Konsekwentnie usunąłem `ports: "8081:8081"` z `gateway` w `docker-compose.prod.yml` — nic z zewnątrz nie musi już trafiać do gatewaya bezpośrednio, tylko `frontend`.

### 8.0 — Zanim zaczniesz

- [ ] **Pierwszy tag wypchnięty do GitHuba** (`git tag v0.1.0 && git push origin v0.1.0`), żeby Etap 6 (`publish-images.yml`) faktycznie zbudował i opublikował 8 obrazów do GHCR — bez tego `docker-compose.prod.yml` nie ma czego pullować. Sprawdź zakładkę Actions, aż wszystkie joby będą zielone.
- [ ] Domena wskazująca rekordem `A` na IP VPS (jeśli deploy ma być pod domeną, nie samym IP).

### 8.1 — Pliki na serwerze

- [ ] Katalog roboczy, np. `/opt/poorbet`, właściciel = user, którym się łączysz (nie root).
- [ ] Skopiować na serwer (scp) tylko trzy pliki: `docker-compose.yml`, `docker-compose.prod.yml`, `.env.example` — **nie trzeba klonować całego repo**, obrazy są już zbudowane w GHCR, serwer tylko je uruchamia.
- [ ] `mv .env.example .env`, uzupełnić realnymi wartościami (Etap 7), `chmod 600 .env`.

### 8.2 — Pierwsze uruchomienie

```bash
cd /opt/poorbet
export IMAGE_TAG=v0.1.0   # tag z kroku 8.0

docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml pull
docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml up -d
docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml ps
```

Wszystkie serwisy powinny dojść do `healthy` (Etap 4). `docker login ghcr.io` **nie jest potrzebny** — pakiety GHCR są publiczne (decyzja z Etapu 5).

Na tym etapie appka działa lokalnie na serwerze (`curl http://127.0.0.1:80` powinno odpowiadać) — jeszcze nic nie jest wystawione publicznie z TLS.

### 8.3 — Firewall

- [ ] `ufw allow OpenSSH && ufw allow 80/tcp && ufw allow 443/tcp && ufw enable`. Tylko te trzy porty otwarte na świat — bazy, RabbitMQ, `gateway` i tak nie mają już `ports:` w `docker-compose.prod.yml` (8.-wstęp wyżej), więc nie ma czego dodatkowo blokować.

### 8.4 — Reverse proxy (Nginx na hoście) + TLS (Certbot)

Jeden upstream — sam `frontend` (patrz uwaga na początku etapu):

```nginx
server {
    listen 80;
    server_name twojadomena.pl;

    location / {
        proxy_pass http://127.0.0.1:80;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # SSE (/api/notifications/stream) — bez tego powiadomienia docierają
        # z opóźnieniem albo wcale, bo nginx buforuje odpowiedź.
        proxy_buffering off;
        proxy_read_timeout 3600s;
    }
}
```

```bash
sudo apt install -y nginx certbot python3-certbot-nginx
# wklej powyższy config do /etc/nginx/sites-available/poorbet, potem:
sudo ln -s /etc/nginx/sites-available/poorbet /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx

sudo certbot --nginx -d twojadomena.pl   # odpowiedz "yes" na przekierowanie http→https
```

Certbot sam dopisuje `listen 443 ssl` i instaluje systemd timer do automatycznego odnawiania — zweryfikuj jednorazowo: `sudo certbot renew --dry-run`.

### 8.5 — Aktualizacja aplikacji (kolejne wersje)

```bash
# lokalnie: git tag vX.Y.Z && git push origin vX.Y.Z   → CI buduje i publikuje do GHCR
# na serwerze:
export IMAGE_TAG=vX.Y.Z
docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml pull
docker compose --env-file .env -f docker-compose.yml -f docker-compose.prod.yml up -d
```
Krótka przerwa w dostępności podmienianych serwisów (nie blue-green) — akceptowalne przy tej skali. Migracje Flyway nakładają się automatycznie przy starcie — **zrób backup (Etap 9) przed każdą aktualizacją**.

---

## Etap 9 — Backup baz danych

Nie ma tego w `deploy.md`, a jest krytyczne przy prawdziwych danych użytkowników (portfele, kupony):

- [ ] Cykliczny `pg_dump` dla `user-db`, `match-db`, `coupon-db`, `wallet-db` (np. cron na hoście albo dodatkowy kontener).
- [ ] Test odtworzenia backupu — nie tylko robienie kopii, ale sprawdzenie, że da się z niej odzyskać dane.

---

## Kolejność rekomendowana

1. Etap 1 (Dockerfile) i Etap 4 (healthchecki) — bez tego nic dalej nie ma sensu, to naprawa realnych bugów.
2. Etap 2 (`.env.example`) — szybkie, zero ryzyka.
3. Etap 3 (`docker-compose.prod.yml`) — pozwala odpalić prod lokalnie/na serwerze ręcznie, jeszcze bez CI.
4. Etap 7 i 8 — pierwszy realny deploy na serwer, ręcznie.
5. Etap 5 i 6 — automatyzacja buildu/publikacji obrazów, gdy ręczny proces już działa i wiadomo, że ma sens go automatyzować.
6. Etap 9 — backupy, najpóźniej zanim pojawią się prawdziwi użytkownicy.

To, co dalej (Kubernetes, schema registry dla eventów) jest już opisane w `ARCHITECTURE_REVIEW.md` (sekcje DL5, DL6) — świadomie pominięte tutaj, bo to kolejny poziom skali, nie pierwszy deploy.