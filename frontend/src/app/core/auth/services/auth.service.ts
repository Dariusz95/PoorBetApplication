import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AccountService } from '@core/account/services/account.service';
import {
  BehaviorSubject,
  distinctUntilChanged,
  finalize,
  Observable,
  shareReplay,
  tap,
} from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginRequest } from '../requests/login-request';
import { RefreshTokenRequest } from '../requests/refresh-token-request';
import { RegisterRequest } from '../requests/register-request';
import { JwtAuthStateService } from './jwt-auth-state.service';
import { LoginResponse } from '../responses/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly jwtAuthState = inject(JwtAuthStateService);
  private readonly accountService = inject(AccountService);

  private apiBaseUrl = `${environment.backend.baseURL}/api/users`;

  private readonly isLoggedInSubject = new BehaviorSubject<boolean>(
    this.jwtAuthState.isAuthenticated()
  );
  readonly isLoggedIn$: Observable<boolean> = this.isLoggedInSubject
    .asObservable()
    .pipe(distinctUntilChanged());

  private refreshInProgress$: Observable<LoginResponse> | null = null;

  register(request: RegisterRequest): Observable<any> {
    const url = `${this.apiBaseUrl}/register`;

    return this.http.post(url, request);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    const url = `${this.apiBaseUrl}/login`;

    return this.http.post<LoginResponse>(url, request).pipe(
      tap((response) => this.storeTokens(response))
    );
  }

  loginAsTestUser(): Observable<LoginResponse> {
    const url = `${this.apiBaseUrl}/login-test-user`;

    return this.http.post<LoginResponse>(url, {}).pipe(
      tap((response) => this.storeTokens(response))
    );
  }

  refresh(): Observable<LoginResponse> {
    if (this.refreshInProgress$) {
      return this.refreshInProgress$;
    }

    const request: RefreshTokenRequest = {
      refreshToken: this.jwtAuthState.getRefreshToken() ?? '',
    };

    this.refreshInProgress$ = this.http
      .post<LoginResponse>(`${this.apiBaseUrl}/refresh`, request)
      .pipe(
        tap((response) => this.storeTokens(response)),
        finalize(() => (this.refreshInProgress$ = null)),
        shareReplay(1)
      );

    return this.refreshInProgress$;
  }

  logout(): void {
    const refreshToken = this.jwtAuthState.getRefreshToken();

    this.http
      .post(`${this.apiBaseUrl}/logout`, { refreshToken } as RefreshTokenRequest)
      .subscribe({ error: () => {} });

    this.jwtAuthState.clearToken();
    this.jwtAuthState.clearRefreshToken();
    this.isLoggedInSubject.next(false);
    this.accountService.reset();
  }

  isLoggedIn(): boolean {
    return this.jwtAuthState.isAuthenticated();
  }

  private storeTokens(response: LoginResponse): void {
    if (!response.token) {
      return;
    }

    this.jwtAuthState.setToken(response.token);
    this.jwtAuthState.setRefreshToken(response.refreshToken);
    this.isLoggedInSubject.next(this.jwtAuthState.isAuthenticated());
  }
}
