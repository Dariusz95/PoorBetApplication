import {
  HttpContext,
  HttpErrorResponse,
  HttpHandlerFn,
  HttpRequest,
} from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { TranslocoService } from '@jsverse/transloco';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { ToastService } from '@shared/services/toast.service';
import { httpErrorInterceptor } from './http-error.interceptor';
import { SKIP_ERROR_TOAST } from '../tokens/skip-error-toast.token';

describe('httpErrorInterceptor', () => {
  let toastService: { error: ReturnType<typeof vi.fn> };
  let transloco: { translate: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    toastService = { error: vi.fn() };
    transloco = { translate: vi.fn((key: string) => key) };

    TestBed.configureTestingModule({
      providers: [
        { provide: ToastService, useValue: toastService },
        { provide: TranslocoService, useValue: transloco },
      ],
    });
  });

  function runInterceptor(
    request: HttpRequest<unknown>,
    next: HttpHandlerFn,
  ) {
    return TestBed.runInInjectionContext(() =>
      httpErrorInterceptor(request, next),
    );
  }

  it('should pass through successful responses without showing a toast', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const next: HttpHandlerFn = vi.fn(() => of({} as any));

    runInterceptor(request, next).subscribe();

    expect(toastService.error).not.toHaveBeenCalled();
  });

  it('should show the mapped message for a known error code', () => {
    const request = new HttpRequest('POST', '/api/users/login', {});
    const error = new HttpErrorResponse({
      status: 401,
      error: {
        code: 'INVALID_CREDENTIALS',
        message: 'Nieprawidłowy adres e-mail lub hasło.',
        timestamp: '2026-07-18T12:00:00Z',
      },
    });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({ error: () => {} });

    expect(toastService.error).toHaveBeenCalledWith(
      'auth.login.invalidCredentials',
    );
  });

  it('should show the mapped translation key (not the raw backend message) for a known business error code', () => {
    const request = new HttpRequest('POST', '/api/coupons', {});
    const error = new HttpErrorResponse({
      status: 400,
      error: {
        code: 'INSUFFICIENT_FUNDS',
        message: 'Niewystarczające środki na koncie.',
        timestamp: '2026-07-18T12:00:00Z',
      },
    });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({ error: () => {} });

    expect(toastService.error).toHaveBeenCalledWith('errors.insufficientFunds');
  });

  it('should show a generic fallback toast for an unrecognized error shape', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 500, error: null });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({ error: () => {} });

    expect(toastService.error).toHaveBeenCalledWith('errors.generic');
  });

  it('should show a neutral toast for a duplicate-registration error without confirming the email exists', () => {
    const request = new HttpRequest('POST', '/api/users/register', {});
    const error = new HttpErrorResponse({
      status: 409,
      error: {
        code: 'RESOURCE_ALREADY_EXISTS',
        message: 'User with email address a@b.pl already exists',
        timestamp: '2026-07-18T12:00:00Z',
      },
    });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({ error: () => {} });

    expect(toastService.error).toHaveBeenCalledWith(
      'errors.resourceAlreadyExists',
    );
  });

  it('should skip the toast entirely when SKIP_ERROR_TOAST is set on the request', () => {
    const request = new HttpRequest('GET', '/api/matches', undefined, {
      context: new HttpContext().set(SKIP_ERROR_TOAST, true),
    });
    const error = new HttpErrorResponse({
      status: 400,
      error: {
        code: 'VALIDATION_ERROR',
        message: 'Invalid input',
        timestamp: '2026-07-18T12:00:00Z',
      },
    });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    runInterceptor(request, next).subscribe({ error: () => {} });

    expect(toastService.error).not.toHaveBeenCalled();
  });

  it('should re-throw the original error', () => {
    const request = new HttpRequest('GET', '/api/matches');
    const error = new HttpErrorResponse({ status: 500, error: null });
    const next: HttpHandlerFn = vi.fn(() => throwError(() => error));

    let caught: unknown;
    runInterceptor(request, next).subscribe({
      error: (err) => (caught = err),
    });

    expect(caught).toBe(error);
  });
});
