import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { RegisterRequest } from '../types/register-request';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  private apiBaseUrl = `${environment.backend.baseURL}/api/users`;

  register(request: RegisterRequest): Observable<any> {
    const url = `${this.apiBaseUrl}/register`;

    return this.http.post(url, request);
  }
}
