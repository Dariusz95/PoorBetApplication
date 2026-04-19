import { Injectable, signal } from '@angular/core';

const TOKEN_KEY = 'jwt_token';

@Injectable({
  providedIn: 'root',
})
export class JwtAuthStateService {
  private readonly _isLoggedIn = signal(this.hasValidToken());

  readonly isLoggedIn = this._isLoggedIn.asReadonly();

  private hasValidToken(): boolean {
    const token = this.getToken();

    if (!token) {
      return false;
    }

    return !this.isTokenExpired(token);
  }

  private isTokenExpired(token: string): boolean {
    const payload = this.parseJwtPayload(token);

    if (!payload?.exp || typeof payload.exp !== 'number') {
      return true;
    }

    const expiresAtMs = payload.exp * 1000;
    return Date.now() >= expiresAtMs;
  }

  private parseJwtPayload(token: string): any | null {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this._isLoggedIn.set(!this.isTokenExpired(token));

    if (this.isTokenExpired(token)) {
      this.clearToken();
    }
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
    this._isLoggedIn.set(false);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();

    if (!token || this.isTokenExpired(token)) {
      this.clearToken();
      return false;
    }

    this._isLoggedIn.set(true);
    return true;
  }

  getUserPayload(): any | null {
    const token = this.getToken();

    if (!token) {
      return null;
    }

    const payload = this.parseJwtPayload(token);

    if (!payload || this.isTokenExpired(token)) {
      this.clearToken();
      return null;
    }

    return payload;
  }

  getSubject(): string | null {
    const payload = this.getUserPayload();

    return payload?.sub ?? null;
  }
}
