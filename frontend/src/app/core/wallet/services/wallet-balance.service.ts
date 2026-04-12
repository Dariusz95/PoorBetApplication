import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, inject, signal } from '@angular/core';
import { JwtAuthStateService } from '../../auth/services/jwt-auth-state.service';
import { WalletBalanceEvent, WalletResponse } from '../types/wallet.types';

@Injectable({
  providedIn: 'root',
})
export class WalletBalanceService {
  private readonly http = inject(HttpClient);
  private readonly ngZone = inject(NgZone);
  private readonly jwtAuthStateService = inject(JwtAuthStateService);

  readonly balance = signal<number | null>(null);

  private eventSource: EventSource | null = null;

  init(): void {
    const subject = this.jwtAuthStateService.getSubject();

    if (!subject) {
      return;
    }

    this.http.get<WalletResponse>('/api/wallet/me').subscribe({
      next: (wallet) => this.balance.set(wallet.balance),
    });

    this.connectToLiveUpdates(subject);
  }

  private connectToLiveUpdates(subject: string): void {
    this.eventSource?.close();

    this.eventSource = new EventSource(
      `/api/notifications/wallet/live?subject=${encodeURIComponent(subject)}`,
    );

    this.eventSource.addEventListener('wallet-balance-updated', (event) => {
      this.ngZone.run(() => {
        const data: WalletBalanceEvent = JSON.parse((event as MessageEvent).data);
        this.balance.set(data.balance);
      });
    });

    this.eventSource.onerror = () => {
      this.eventSource?.close();
      this.eventSource = null;
    };
  }
}
