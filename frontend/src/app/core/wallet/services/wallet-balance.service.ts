import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, inject, signal } from '@angular/core';
import { SseClient } from 'ngx-sse-client';
import { environment } from 'src/environments/environment';
import { WalletBalanceEvent, WalletResponse } from '../types/wallet.types';

@Injectable({
  providedIn: 'root',
})
export class WalletBalanceService {
  private readonly http = inject(HttpClient);
  private readonly ngZone = inject(NgZone);
  private readonly sseClient = inject(SseClient);

  readonly balance = signal<number | null>(null);

  private BASE_URL = `${environment.backend.baseURL}/api`;

  init(): void {
    this.http.get<WalletResponse>('/api/wallet/me').subscribe({
      next: (wallet) => this.balance.set(wallet.balance),
    });

    this.connectToLiveUpdates();
  }

  private connectToLiveUpdates(): void {
    this.sseClient
      .stream(
        `${this.BASE_URL}/notifications/stream`,
        {
          keepAlive: true,
          reconnectionDelay: 1000,
          responseType: 'event',
        },
        {},
        'GET',
      )
      .subscribe({
        next: (event) => {
          if (event.type === 'error') {
            const errorEvent = event as ErrorEvent;
            console.error(errorEvent.error, errorEvent.message);
            return;
          }

          const messageEvent = event as MessageEvent;

          if (messageEvent.type === 'wallet.balance-changed') {
            this.ngZone.run(() => {
              const data: WalletBalanceEvent = JSON.parse(messageEvent.data);
              this.balance.set(data.balance);
            });
          }
        },
        error: (err) => {
          console.error('SSE error:', err);
        },
      });
  }
}
