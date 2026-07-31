import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { JwtAuthStateService } from './jwt-auth-state.service';

function base64UrlEncode(value: string): string {
  return btoa(value).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function buildToken(payload: Record<string, unknown>): string {
  const header = base64UrlEncode(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const body = base64UrlEncode(JSON.stringify(payload));

  return `${header}.${body}.signature`;
}

describe('JwtAuthStateService', () => {
  let service: JwtAuthStateService;

  const futureExp = Math.floor(Date.now() / 1000) + 3600;
  const pastExp = Math.floor(Date.now() / 1000) - 3600;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(JwtAuthStateService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('setToken / getToken', () => {
    it('should store a valid token and make it retrievable', () => {
      const token = buildToken({ sub: 'user-1', exp: futureExp });

      service.setToken(token);

      expect(service.getToken()).toBe(token);
    });

    it('should clear the token instead of storing it when it is already expired', () => {
      const token = buildToken({ sub: 'user-1', exp: pastExp });

      service.setToken(token);

      expect(service.getToken()).toBeNull();
    });
  });

  describe('tokenRefreshed$', () => {
    it('should emit when a valid token is set', () => {
      const emissions: void[] = [];
      service.tokenRefreshed$.subscribe((value) => emissions.push(value));

      service.setToken(buildToken({ sub: 'user-1', exp: futureExp }));

      expect(emissions.length).toBe(1);
    });

    it('should not emit when the token being set is already expired', () => {
      const emissions: void[] = [];
      service.tokenRefreshed$.subscribe((value) => emissions.push(value));

      service.setToken(buildToken({ sub: 'user-1', exp: pastExp }));

      expect(emissions.length).toBe(0);
    });
  });

  describe('clearToken', () => {
    it('should remove the stored token', () => {
      service.setToken(buildToken({ sub: 'user-1', exp: futureExp }));

      service.clearToken();

      expect(service.getToken()).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('should return false when there is no token', () => {
      expect(service.isAuthenticated()).toBe(false);
    });

    it('should return true for a valid, non-expired token', () => {
      service.setToken(buildToken({ sub: 'user-1', exp: futureExp }));

      expect(service.isAuthenticated()).toBe(true);
    });

    it('should return false and clear the token when it is expired', () => {
      localStorage.setItem('jwt_token', buildToken({ sub: 'user-1', exp: pastExp }));

      expect(service.isAuthenticated()).toBe(false);
      expect(service.getToken()).toBeNull();
    });
  });

  describe('getUserPayload', () => {
    it('should return null when there is no token', () => {
      expect(service.getUserPayload()).toBeNull();
    });

    it('should return the decoded payload for a valid token', () => {
      service.setToken(buildToken({ sub: 'user-1', exp: futureExp, roles: ['USER'] }));

      expect(service.getUserPayload()).toEqual({
        sub: 'user-1',
        exp: futureExp,
        roles: ['USER'],
      });
    });

    it('should return null for a malformed token', () => {
      localStorage.setItem('jwt_token', 'not-a-jwt');

      expect(service.getUserPayload()).toBeNull();
    });
  });

  describe('getSubject', () => {
    it('should return null when there is no token', () => {
      expect(service.getSubject()).toBeNull();
    });

    it('should return the sub claim for a valid token', () => {
      service.setToken(buildToken({ sub: 'user-42', exp: futureExp }));

      expect(service.getSubject()).toBe('user-42');
    });
  });

  describe('setRefreshToken / getRefreshToken / clearRefreshToken', () => {
    it('should return null when there is no refresh token', () => {
      expect(service.getRefreshToken()).toBeNull();
    });

    it('should store a refresh token and make it retrievable', () => {
      service.setRefreshToken('a-refresh-token');

      expect(service.getRefreshToken()).toBe('a-refresh-token');
    });

    it('should remove the stored refresh token', () => {
      service.setRefreshToken('a-refresh-token');

      service.clearRefreshToken();

      expect(service.getRefreshToken()).toBeNull();
    });

    it('should not affect the access token', () => {
      service.setToken(buildToken({ sub: 'user-1', exp: futureExp }));
      service.setRefreshToken('a-refresh-token');

      service.clearRefreshToken();

      expect(service.getToken()).not.toBeNull();
    });
  });
});
