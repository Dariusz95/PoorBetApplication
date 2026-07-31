import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AccountService } from '@core/account/services/account.service';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { LoginRequest } from '../requests/login-request';
import { RegisterRequest } from '../requests/register-request';
import { LoginResponse } from '../responses/login-response';
import { AuthService } from './auth.service';
import { JwtAuthStateService } from './jwt-auth-state.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let jwtAuthState: any;
  let accountService: any;

  beforeEach(() => {
    const jwtAuthStateSpy = {
      setToken: vi.fn(),
      clearToken: vi.fn(),
      isAuthenticated: vi.fn(),
      setRefreshToken: vi.fn(),
      getRefreshToken: vi.fn(),
      clearRefreshToken: vi.fn(),
    };

    const accountServiceSpy = {
      reset: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService,
        { provide: JwtAuthStateService, useValue: jwtAuthStateSpy },
        { provide: AccountService, useValue: accountServiceSpy },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    jwtAuthState = TestBed.inject(JwtAuthStateService);
    accountService = TestBed.inject(AccountService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('should send POST request with register data', () => {
      const registerRequest: RegisterRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const apiUrl = `/api/users/register`;

      service.register(registerRequest).subscribe();

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerRequest);

      req.flush({ success: true });
    });

    it('should handle register response', () => {
      const registerRequest: RegisterRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const response = { success: true, message: 'Registration successful' };

      service.register(registerRequest).subscribe((result) => {
        expect(result).toEqual(response);
      });

      const req = httpMock.expectOne((req) => req.url.includes('/register'));
      req.flush(response);
    });
  });

  describe('login', () => {
    it('should send POST request with login credentials', () => {
      const loginRequest: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const loginResponse: LoginResponse = {
        tokenType: 'Bearer',
        username: 'testuser',
        roles: ['USER'],
        expiresAt: 0,
        token: 'mock-jwt-token',
        refreshToken: 'mock-refresh-token',
      };

      jwtAuthState.isAuthenticated.mockReturnValue(true);

      service.login(loginRequest).subscribe();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);

      req.flush(loginResponse);
    });

    it('should store access and refresh tokens and update login status on successful login', () => {
      const loginRequest: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const loginResponse: LoginResponse = {
        tokenType: 'Bearer',
        username: 'testuser',
        roles: ['USER'],
        expiresAt: 0,
        token: 'mock-jwt-token',
        refreshToken: 'mock-refresh-token',
      };

      jwtAuthState.isAuthenticated.mockReturnValue(true);

      const promise = service.login(loginRequest).toPromise();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      req.flush(loginResponse);

      return promise!.then(() => {
        expect(jwtAuthState.setToken).toHaveBeenCalledWith('mock-jwt-token');
        expect(jwtAuthState.setRefreshToken).toHaveBeenCalledWith(
          'mock-refresh-token',
        );
      });
    });

    it('should emit isLoggedIn$ true after successful login', () => {
      const loginRequest: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const loginResponse: LoginResponse = {
        tokenType: 'Bearer',
        username: 'testuser',
        roles: ['USER'],
        expiresAt: 0,
        token: 'mock-jwt-token',
        refreshToken: 'mock-refresh-token',
      };

      jwtAuthState.isAuthenticated.mockReturnValue(true);

      let emitted = false;
      service.isLoggedIn$.subscribe((isLoggedIn) => {
        if (isLoggedIn) {
          emitted = true;
          expect(isLoggedIn).toBe(true);
        }
      });

      const promise = service.login(loginRequest).toPromise();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      req.flush(loginResponse);

      return promise!.then(() => {
        expect(emitted).toBe(true);
      });
    });

    it('should not store tokens if response lacks token', () => {
      const loginRequest: LoginRequest = {
        email: 'test@example.com',
        password: 'password123',
      };

      const emptyResponse: any = {};

      const promise = service.login(loginRequest).toPromise();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      req.flush(emptyResponse);

      return promise!.then(() => {
        expect(jwtAuthState.setToken).not.toHaveBeenCalled();
        expect(jwtAuthState.setRefreshToken).not.toHaveBeenCalled();
      });
    });

    it('should handle login error', () => {
      const loginRequest: LoginRequest = {
        email: 'test@example.com',
        password: 'wrongpassword',
      };

      const promise = service
        .login(loginRequest)
        .toPromise()
        .catch((error) => {
          expect(error.status).toBe(401);
          expect(jwtAuthState.setToken).not.toHaveBeenCalled();
        });

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      req.flush(
        { message: 'Invalid credentials' },
        { status: 401, statusText: 'Unauthorized' },
      );

      return promise;
    });
  });

  describe('refresh', () => {
    it('should send POST request with the stored refresh token and store new tokens', () => {
      jwtAuthState.getRefreshToken.mockReturnValue('old-refresh-token');
      jwtAuthState.isAuthenticated.mockReturnValue(true);

      const refreshResponse: LoginResponse = {
        tokenType: 'Bearer',
        username: 'testuser',
        roles: ['USER'],
        expiresAt: 0,
        token: 'new-jwt-token',
        refreshToken: 'new-refresh-token',
      };

      const promise = service.refresh().toPromise();

      const req = httpMock.expectOne((req) => req.url.includes('/refresh'));
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ refreshToken: 'old-refresh-token' });

      req.flush(refreshResponse);

      return promise!.then(() => {
        expect(jwtAuthState.setToken).toHaveBeenCalledWith('new-jwt-token');
        expect(jwtAuthState.setRefreshToken).toHaveBeenCalledWith(
          'new-refresh-token',
        );
      });
    });

    it('should dedupe concurrent refresh calls into a single HTTP request', () => {
      jwtAuthState.getRefreshToken.mockReturnValue('old-refresh-token');
      jwtAuthState.isAuthenticated.mockReturnValue(true);

      const refresh$ = service.refresh();
      expect(service.refresh()).toBe(refresh$);

      refresh$.subscribe();
      service.refresh().subscribe();

      const requests = httpMock.match((req) => req.url.includes('/refresh'));
      expect(requests.length).toBe(1);

      requests[0].flush({
        tokenType: 'Bearer',
        username: 'testuser',
        roles: ['USER'],
        expiresAt: 0,
        token: 'new-jwt-token',
        refreshToken: 'new-refresh-token',
      } as LoginResponse);
    });

    it('should allow a new refresh call after the previous one completed', () => {
      jwtAuthState.getRefreshToken.mockReturnValue('old-refresh-token');
      jwtAuthState.isAuthenticated.mockReturnValue(true);

      service.refresh().subscribe();
      httpMock
        .expectOne((req) => req.url.includes('/refresh'))
        .flush({
          tokenType: 'Bearer',
          username: 'testuser',
          roles: ['USER'],
          expiresAt: 0,
          token: 'first-token',
          refreshToken: 'first-refresh-token',
        } as LoginResponse);

      service.refresh().subscribe();
      httpMock
        .expectOne((req) => req.url.includes('/refresh'))
        .flush({
          tokenType: 'Bearer',
          username: 'testuser',
          roles: ['USER'],
          expiresAt: 0,
          token: 'second-token',
          refreshToken: 'second-refresh-token',
        } as LoginResponse);
    });
  });

  describe('logout', () => {
    it('should clear token and update login status', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(false);

      let emitted = false;
      let emittedValue = true;

      const subscription = service.isLoggedIn$.subscribe((isLoggedIn) => {
        emitted = true;
        emittedValue = isLoggedIn;
      });

      service.logout();
      httpMock.expectOne((req) => req.url.includes('/logout')).flush(null);

      expect(emitted).toBe(true);
      expect(emittedValue).toBe(false);
      expect(jwtAuthState.clearToken).toHaveBeenCalled();
      expect(jwtAuthState.clearRefreshToken).toHaveBeenCalled();

      subscription.unsubscribe();
    });

    it('should reset AccountService state so the next login always refetches fresh account data', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(false);

      service.logout();
      httpMock.expectOne((req) => req.url.includes('/logout')).flush(null);

      expect(accountService.reset).toHaveBeenCalled();
    });

    it('should emit false on isLoggedIn$', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(false);

      let emitted = false;
      let emittedValue = true;

      const subscription = service.isLoggedIn$.subscribe((isLoggedIn) => {
        if (isLoggedIn === false) {
          emitted = true;
          emittedValue = isLoggedIn;
        }
      });

      service.logout();
      httpMock.expectOne((req) => req.url.includes('/logout')).flush(null);

      expect(emitted).toBe(true);
      expect(emittedValue).toBe(false);

      subscription.unsubscribe();
    });

    it('should clear local state even if the backend logout request fails', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(false);

      service.logout();
      httpMock
        .expectOne((req) => req.url.includes('/logout'))
        .flush(null, { status: 500, statusText: 'Server Error' });

      expect(jwtAuthState.clearToken).toHaveBeenCalled();
      expect(jwtAuthState.clearRefreshToken).toHaveBeenCalled();
    });
  });

  describe('isLoggedIn', () => {
    it('should return true when user is authenticated', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(true);
      expect(service.isLoggedIn()).toBe(true);
    });

    it('should return false when user is not authenticated', () => {
      jwtAuthState.isAuthenticated.mockReturnValue(false);
      expect(service.isLoggedIn()).toBe(false);
    });
  });
});
