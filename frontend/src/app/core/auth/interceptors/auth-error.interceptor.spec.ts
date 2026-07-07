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
  let authService: { logout: ReturnType<typeof vi.fn> };
  let router: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    authService = { logout: vi.fn() };
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

    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should log out and redirect to login on a 401 for a non-auth request', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: (err) => expect(err).toBe(error),
    });

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith([
      RouteFragment.Slash,
      RouteFragment.Auth,
      RouteFragment.Login,
    ]);
  });

  it('should not log out on a 401 from the login endpoint', () => {
    const request = new HttpRequest('POST', '/api/users/login', {});
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: () => {},
    });

    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not log out on a 401 from the register endpoint', () => {
    const request = new HttpRequest('POST', '/api/users/register', {});
    const error = new HttpErrorResponse({ status: 401 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: () => {},
    });

    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not log out on non-401 errors', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 500 });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({
      error: (err) => expect(err).toBe(error),
    });

    expect(authService.logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should re-throw the original error', () => {
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
