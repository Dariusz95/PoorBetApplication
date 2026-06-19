# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Komendy

```bash
npm run start:dev     # dev server z proxy do gateway (http://host.docker.internal:8081)
npm run start         # dev server bez proxy
npm test              # uruchom testy (Vitest, jednorazowo)
npm run test:watch    # tryb watch

npx prettier --check src/
npx prettier --write src/
```

Uruchomienie pojedynczego testu:
```bash
npx vitest run src/app/features/bet/services/bet-slip.service.spec.ts
```

## Architektura

Angular 21, standalone components, signals. Brak NgModules.

### Struktura `src/app/`

```
core/
  auth/           — logika JWT, interceptory, guardy, strony login/register
  components/     — header, mobile-header, coupon-dropdown, user-balance, language-switcher
  layouts/        — AppLayoutComponent (główna powłoka UI)
  routing/        — RouteFragment (URL segments), RoutePath (enum etykiet), RoutingService
  wallet/         — WalletService (stan salda)

features/
  bet/            — strona główna zakładów; BetSlipService (signal-based koszyk)
  coupons/        — lista kuponów, dialog szczegółów, CouponService

shared/
  ui/             — komponenty design system z prefixem `pb-` (pb-card, pb-button, pb-input…)
  services/       — LiveEventsService (SSE), DialogService, ToastService
  types/          — Uuid, BetType, wallet types
  pipes/          — image-path, validation-error
```

### Kluczowe wzorce

**Routing** — dwa drzewa: `/auth/**` (guestGuard) i `/app/**`. Wszystkie feature routes ładowane lazy. `RouteFragment` zawiera URL segments, `RoutePath` — enum etykiet dla nawigacji.

**Stan** — BetSlipService trzyma zaznaczone zakłady jako `signal<SelectedBet[]>`, WalletService trzyma saldo. Wartości pochodne przez `computed()`. Brak globalnego store (NgRx / Akita).

**SSE** — `LiveEventsService` łączy się z `/api/notifications/stream` przez `ngx-sse-client`, aktualizuje saldo w `WalletService`. Inicjowany jednorazowo po zalogowaniu.

**HTTP** — `environment.backend.baseURL` jest pusty (string `''`); API proxy konfiguruje `src/proxy.config.json` (deweloperski) lub reverse proxy (Docker). Interceptor `authTokenInterceptor` dodaje nagłówek `Authorization: Bearer <token>`. `authErrorInterceptor` obsługuje 401.

**JWT** — token przechowywany w localStorage przez `JwtAuthStateService`. `AuthService` opakowuje stan logowania jako `isLoggedIn$` (Observable) i `isLoggedIn()` (synchronicznie).

**Tłumaczenia** — `@jsverse/transloco`, pliki JSON w `src/assets/i18n/{en,pl}.json`. `getTranslocoModule()` to helper dla testów.

**Design system** — komponenty w `shared/ui/` używają prefixu `pb-`. Dialogi przez Angular CDK Dialog + `DialogService`. Toasty przez `ngx-toastr` + `ToastService`.

### Aliasy ścieżek

| Alias | Ścieżka |
|---|---|
| `@app` | `src/app` |
| `@core` | `src/app/core` |
| `@shared` | `src/app/shared` |
| `@features` | `src/app/features` |
| `@env` | `src/environments` |

### Angular — zasady projektu

- Komponenty standalone — `standalone: true` już nie jest wymagane (domyślne od v20+)
- Brak `ChangeDetectionStrategy.OnPush` w dekoratorach — domyślne od v22+
- Używaj `input()` / `output()` zamiast dekoratorów `@Input` / `@Output`
- Wstrzykiwanie przez `inject()`, nie przez konstruktor
- Stan w signals; wartości pochodne przez `computed()`; `set()` / `update()` zamiast mutacji
- `@if` / `@for` / `@switch` zamiast `*ngIf` / `*ngFor` / `*ngSwitch`
- Brak `ngClass` / `ngStyle` — używaj bindingów `class` / `style`
- Testy: Vitest + jsdom (brak Jest ani Karma)
