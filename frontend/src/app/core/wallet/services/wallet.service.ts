import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { environment } from 'src/environments/environment.development';

interface WalletResponse {
  userId: string;
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class WalletService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/wallet`;

  private readonly _balance = signal<number | null>(null);
  readonly balance = computed(() => this._balance());

  setBalance(balance: number): void {
    this._balance.set(balance);
  }

  getBalance(): Observable<number> {
    return this.http.get<WalletResponse>(`${this.baseUrl}/me`).pipe(
      map((response) => response.balance),
      tap((balance) => this._balance.set(balance)),
    );
  }
}
