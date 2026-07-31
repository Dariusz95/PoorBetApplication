import {
  HttpErrorResponse,
  HttpHandlerFn,
  HttpRequest,
} from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { RouteFragment } from '../../routing/route-fragment';
import { AuthService } from '../services/auth.service';
import { authErrorInterceptor } from './auth-error.interceptor';

describe('authErrorInterceptor', () => {
  let authService: {
    logout: ReturnType<typeof vi.fn>;
    refresh: ReturnType<typeof vi.fn>;
  };
  let router: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authService = { logout: vi.fn(), refresh: vi.fn() };
    router = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
  });

  function runInterceptor(
    request: HttpRequest<unknown>,
    next: HttpHandlerFn,
  ) {
    return TestBed.runInInjectionContext(() =>
      authErrorInterceptor(request, next),
    );
  }

  it('should pass through successful responses without side effects', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const next: HttpHandlerFn = vi.fn(() => of({} as any));

    runInterceptor(request, next).subscribe();

    expect(authService.refresh).not.toHaveBeenCalled();
    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should refresh the token and retry the original request with the new token on a 401', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn((req: HttpRequest<unknown>) =>
      req === request ? throwError(() => error) : of({ ok: true } as any),
    );
    authService.refresh.mockReturnValue(of({ token: 'new-token' } as any));

    let result: unknown;
    runInterceptor(request, next).subscribe((res) => (result = res));

    expect(authService.refresh).toHaveBeenCalled();
    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(next).toHaveBeenCalledTimes(2);

    const retriedRequest = (next as ReturnType<typeof vi.fn>).mock
      .calls[1][0] as HttpRequest<unknown>;
    expect(retriedRequest.headers.get('Authorization')).toBe(
      'Bearer new-token',
    );
    expect(result).toEqual({ ok: true });
  });

  it('should log out and redirect to login when the refresh itself fails', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 401 });
    const refreshError = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));
    authService.refresh.mockReturnValue(throwError(() => refreshError));

    runInterceptor(request, next).subscribe({
      error: (err) => expect(err).toBe(refreshError),
    });

    expect(authService.refresh).toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith([
      RouteFragment.Slash,
      RouteFragment.Auth,
      RouteFragment.Login,
    ]);
  });

  it.each([
    ['login', '/api/users/login'],
    ['register', '/api/users/register'],
    ['refresh', '/api/users/refresh'],
    ['logout', '/api/users/logout'],
  ])('should not attempt refresh on a 401 from the %s endpoint', (_, url) => {
    const request = new HttpRequest('POST', url, {});
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: () => {},
    });

    expect(authService.refresh).not.toHaveBeenCalled();
    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not attempt refresh on non-401 errors', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 500 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: (err) => expect(err).toBe(error),
    });

    expect(authService.refresh).not.toHaveBeenCalled();
    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should re-throw the original error for non-401 errors', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 403 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    let caught: unknown;
    runInterceptor(request, next).subscribe({
      error: (err) => (caught = err),
    });

    expect(caught).toBe(error);
  });
});
