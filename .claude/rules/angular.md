## Best practices dla TypeScript i Angular

Jesteś ekspertem w TypeScript, Angular oraz skalowalnych aplikacjach webowych. Tworzysz kod funkcjonalny, łatwy w utrzymaniu, wydajny oraz dostępny, zgodny z najlepszymi praktykami Angulara i TypeScript.

---

## TypeScript – najlepsze praktyki

- Używaj ścisłego typowania (`strict mode`)
- Preferuj inferencję typów, gdy typ jest oczywisty
- Unikaj typu `any`; używaj `unknown`, gdy typ jest nieznany

---

## Angular – najlepsze praktyki

- Zawsze używaj komponentów standalone (bez NgModules)
- NIE ustawiaj `standalone: true` w dekoratorach Angulara — w Angular v20+ jest to domyślne
- NIE ustawiaj `changeDetection: ChangeDetectionStrategy.OnPush` — w Angular v22+ jest to domyślne
- Używaj signals do zarządzania stanem
- Implementuj lazy loading dla feature routes
- NIE używaj `@HostBinding` i `@HostListener` — zamiast tego używaj `host` w dekoratorze `@Component` lub `@Directive`
- Używaj `NgOptimizedImage` dla wszystkich statycznych obrazów  
  - Nie działa dla obrazów base64 inline

---

## Wymagania dostępności (Accessibility)

- Kod musi przechodzić wszystkie testy AXE
- Musi spełniać WCAG AA:
  - odpowiedni kontrast kolorów
  - poprawna obsługa fokusu
  - właściwe atrybuty ARIA

---

## Komponenty

- Komponenty powinny być małe i mieć jedną odpowiedzialność
- Używaj `input()` i `output()` zamiast dekoratorów
- Używaj `computed()` do wartości pochodnych
- Preferuj inline template dla małych komponentów
- Dla nowych formularzy używaj Signal Forms (`@angular/forms/signals`) – stabilne w Angular v22+
- Jeśli nie używasz Signal Forms:
  - preferuj Reactive Forms zamiast Template-driven forms
- NIE używaj `ngClass` — używaj bindingów `class`
- NIE używaj `ngStyle` — używaj bindingów `style`
- Przy zewnętrznych template i stylach używaj ścieżek względnych względem pliku `.ts`

---

## Zarządzanie stanem

- Stan lokalny przechowuj w signals
- Używaj `computed()` do wartości wyliczanych
- Transformacje stanu muszą być czyste i przewidywalne
- NIE używaj `mutate` na signalach — używaj `set()` lub `update()`

---

## Szablony (templates)

- Trzymaj logikę w template minimalną
- Używaj natywnej składni kontrolnej:
  - `@if`
  - `@for`
  - `@switch`
- Zamiast dyrektyw `*ngIf`, `*ngFor`, `*ngSwitch`
- Używaj `async` pipe dla Observable
- Nie zakładaj dostępności globali (np. `new Date()`)

---

## Serwisy

- Serwisy powinny mieć jedną odpowiedzialność
- Używaj `providedIn: 'root'` dla singletonów
- Preferuj dekorator `@Service` zamiast `@Injectable({ providedIn: 'root' })` w Angular v22+
- Używaj funkcji `inject()` zamiast konstruktorów do wstrzykiwania zależności