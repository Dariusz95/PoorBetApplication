import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginRequest } from '../requests/login-request';
import { RegisterRequest } from '../requests/register-request';
import { JwtAuthStateService } from './jwt-auth-state.service';
import { LoginResponse } from '../responses/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly jwtAuthState = inject(JwtAuthStateService);

  private apiBaseUrl = `${environment.backend.baseURL}/api/users`;

  private readonly isLoggedInSubject = new BehaviorSubject<boolean>(
    this.jwtAuthState.isLoggedIn()
  );
  readonly isLoggedIn$: Observable<boolean> =
    this.isLoggedInSubject.asObservable();

  register(request: RegisterRequest): Observable<any> {
    const url = `${this.apiBaseUrl}/register`;
    return this.http.post(url, request);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    const url = `${this.apiBaseUrl}/login`;
    return this.http.post<LoginResponse>(url, request).pipe(
      tap((response) => {
        if (response.token) {
          this.jwtAuthState.setToken(response.token);
          this.isLoggedInSubject.next(true);
        }
      })
    );
  }

  logout(): void {
    this.jwtAuthState.clearToken();
    this.isLoggedInSubject.next(false);
  }

  isLoggedIn(): boolean {
    return this.jwtAuthState.isLoggedIn();
  }
}
