import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
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

  beforeEach(() => {
    const jwtAuthStateSpy = {
      setToken: vi.fn(),
      clearToken: vi.fn(),
      isAuthenticated: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService,
        { provide: JwtAuthStateService, useValue: jwtAuthStateSpy },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    jwtAuthState = TestBed.inject(JwtAuthStateService);
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
      };

      jwtAuthState.isAuthenticated.mockReturnValue(true);

      service.login(loginRequest).subscribe();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);

      req.flush(loginResponse);
    });

    it('should store token and update login status on successful login', () => {
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
      };

      jwtAuthState.isAuthenticated.mockReturnValue(true);

      const promise = service.login(loginRequest).toPromise();

      const req = httpMock.expectOne((req) => req.url.includes('/login'));
      req.flush(loginResponse);

      return promise!.then(() => {
        expect(jwtAuthState.setToken).toHaveBeenCalledWith('mock-jwt-token');
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

    it('should not store token if response lacks token', () => {
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

      expect(emitted).toBe(true);
      expect(emittedValue).toBe(false);
      expect(jwtAuthState.clearToken).toHaveBeenCalled();

      subscription.unsubscribe();
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

      expect(emitted).toBe(true);
      expect(emittedValue).toBe(false);

      subscription.unsubscribe();
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
