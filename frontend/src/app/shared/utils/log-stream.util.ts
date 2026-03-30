import { tap } from 'rxjs/operators';

export function logStream<T>(label: string) {
  return tap<T>({
    next: (value) => console.log(`[%c${label}%c]`, 'color: green', '', value),
    error: (err) => console.error(`[${label}] ERROR`, err),
    complete: () => console.log(`[${label}] COMPLETE`),
  });
}
