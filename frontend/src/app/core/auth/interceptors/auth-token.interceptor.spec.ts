import { HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Observable, of } from 'rxjs';
import { JwtAuthStateService } from '../services/jwt-auth-state.service';
import { authTokenInterceptor } from './auth-token.interceptor';

describe('authTokenInterceptor', () => {
  let jwtAuthState: { getToken: ReturnType<typeof vi.fn> };
  let next: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    jwtAuthState = { getToken: vi.fn() };
    next = vi.fn((req: HttpRequest<unknown>) => of({} as HttpEvent<unknown>));

    TestBed.configureTestingModule({
      providers: [{ provide: JwtAuthStateService, useValue: jwtAuthState }],
    });
  });

  function runInterceptor(request: HttpRequest<unknown>) {
    return TestBed.runInInjectionContext(() =>
      authTokenInterceptor(request, next as HttpHandlerFn),
    ) as Observable<HttpEvent<unknown>>;
  }

  it('should forward the request unchanged when there is no token', () => {
    jwtAuthState.getToken.mockReturnValue(null);
    const request = new HttpRequest('GET', '/api/matches');

    runInterceptor(request).subscribe();

    const forwardedRequest = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwardedRequest.headers.has('Authorization')).toBe(false);
  });

  it('should add the Authorization header when a token is present', () => {
    jwtAuthState.getToken.mockReturnValue('my-jwt-token');
    const request = new HttpRequest('GET', '/api/matches');

    runInterceptor(request).subscribe();

    const forwardedRequest = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwardedRequest.headers.get('Authorization')).toBe(
      'Bearer my-jwt-token',
    );
  });

  it('should not mutate the original request', () => {
    jwtAuthState.getToken.mockReturnValue('my-jwt-token');
    const request = new HttpRequest('GET', '/api/matches');

    runInterceptor(request).subscribe();

    expect(request.headers.has('Authorization')).toBe(false);
  });
});
