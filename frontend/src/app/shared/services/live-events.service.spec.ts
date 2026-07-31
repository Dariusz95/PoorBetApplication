import { LiveAnnouncer } from '@angular/cdk/a11y';
import { TestBed } from '@angular/core/testing';
import { AccountService } from '@core/account/services/account.service';
import { AuthService } from '@core/auth/services/auth.service';
import { JwtAuthStateService } from '@core/auth/services/jwt-auth-state.service';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { SseClient } from 'ngx-sse-client';
import { Observable, Subject } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LiveEventsService } from './live-events.service';

describe('LiveEventsService', () => {
  let service: LiveEventsService;
  let loggedIn$: Subject<boolean>;
  let tokenRefreshed$: Subject<void>;
  let sseClient: { stream: ReturnType<typeof vi.fn> };
  let activeConnections: number;

  beforeEach(() => {
    loggedIn$ = new Subject<boolean>();
    tokenRefreshed$ = new Subject<void>();
    activeConnections = 0;

    const stream = vi.fn(
      () =>
        new Observable(() => {
          activeConnections++;
          return () => {
            activeConnections--;
          };
        }),
    );
    sseClient = { stream };

    TestBed.configureTestingModule({
      imports: [getTranslocoModule()],
      providers: [
        LiveEventsService,
        { provide: SseClient, useValue: sseClient },
        {
          provide: AuthService,
          useValue: { isLoggedIn$: loggedIn$.asObservable() },
        },
        {
          provide: JwtAuthStateService,
          useValue: {
            getToken: () => 'a-token',
            tokenRefreshed$: tokenRefreshed$.asObservable(),
          },
        },
        {
          provide: AccountService,
          useValue: { setBalance: vi.fn(), setProgress: vi.fn() },
        },
        { provide: LiveAnnouncer, useValue: { announce: vi.fn() } },
      ],
    });

    service = TestBed.inject(LiveEventsService);
  });

  it('should open exactly one SSE connection when the user logs in', () => {
    service.init();
    loggedIn$.next(true);

    expect(sseClient.stream).toHaveBeenCalledTimes(1);
    expect(activeConnections).toBe(1);
  });

  it('should close the connection when the user logs out', () => {
    service.init();
    loggedIn$.next(true);
    loggedIn$.next(false);

    expect(activeConnections).toBe(0);
  });

  it('should close the previous connection before opening a new one on re-login (regression: stale SSE stream from a previous user)', () => {
    service.init();
    loggedIn$.next(true);
    loggedIn$.next(true);

    expect(sseClient.stream).toHaveBeenCalledTimes(2);
    expect(activeConnections).toBe(1);
  });

  it('should reconnect with exactly one connection when a different user logs in after logout', () => {
    service.init();
    loggedIn$.next(true);
    loggedIn$.next(false);
    loggedIn$.next(true);

    expect(sseClient.stream).toHaveBeenCalledTimes(2);
    expect(activeConnections).toBe(1);
  });

  it('should not register the isLoggedIn$ subscription twice when init() is called again', () => {
    service.init();
    service.init();
    loggedIn$.next(true);

    expect(sseClient.stream).toHaveBeenCalledTimes(1);
  });

  it('disconnect() should be safe to call when there is no active connection', () => {
    expect(() => service.disconnect()).not.toThrow();
  });

  it('should reconnect with a fresh connection when the token is refreshed while connected (regression: stale Authorization header on SSE reconnect)', () => {
    service.init();
    loggedIn$.next(true);

    tokenRefreshed$.next();

    expect(sseClient.stream).toHaveBeenCalledTimes(2);
    expect(activeConnections).toBe(1);
  });

  it('should not open a connection on token refresh before the user is logged in', () => {
    service.init();

    tokenRefreshed$.next();

    expect(sseClient.stream).not.toHaveBeenCalled();
  });

  it('should not reconnect on token refresh after logout', () => {
    service.init();
    loggedIn$.next(true);
    loggedIn$.next(false);
    sseClient.stream.mockClear();

    tokenRefreshed$.next();

    expect(sseClient.stream).not.toHaveBeenCalled();
  });
});
