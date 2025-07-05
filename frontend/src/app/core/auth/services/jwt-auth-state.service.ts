import { Injectable, signal } from '@angular/core';

const TOKEN_KEY = 'jwt_token';

@Injectable({
  providedIn: 'root',
})
export class JwtAuthStateService {
  private readonly _isLoggedIn = signal(this.hasToken());

  readonly isLoggedIn = this._isLoggedIn.asReadonly();

  private hasToken(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this._isLoggedIn.set(true);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(TOKEN_KEY);
    this._isLoggedIn.set(false);
  }

  getUserPayload(): any | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }
}
