import { inject, Injectable, NgZone, signal } from '@angular/core';
import { AuthService } from '@core/auth/services/auth.service';
import { JwtAuthStateService } from '@core/auth/services/jwt-auth-state.service';
import { WalletService } from '@core/wallet/services/wallet.service';
import { SseClient } from 'ngx-sse-client';
import { filter } from 'rxjs';
import { environment } from 'src/environments/environment';
import { WalletBalanceEvent } from '../types/wallet.types';

@Injectable({
  providedIn: 'root',
})
export class LiveEventsService {
  private readonly ngZone = inject(NgZone);
  private readonly sseClient = inject(SseClient);
  private readonly jwtAuthStateService = inject(JwtAuthStateService);
  private readonly authService = inject(AuthService);
  private readonly walletService = inject(WalletService);
  private readonly baseUrl = `${environment.backend.baseURL}/api/notifications/stream`;

  private initialized = signal(false);

  init(): void {
    if (this.initialized()) {
      return;
    }

    this.authService.isLoggedIn$
      .pipe(filter((loggedIn) => loggedIn))
      .subscribe(() => {
        this.initialized.set(true);
        this.connectToLiveUpdates();
      });

    // this.initialized.set(true);
    // this.connectToLiveUpdates();
  }

  private connectToLiveUpdates(): void {
    this.sseClient
      .stream(
        this.baseUrl,
        {
          keepAlive: true,
          reconnectionDelay: 3_000,
          responseType: 'event',
        },
        {
          withCredentials: true,
          headers: {
            Authorization: `Bearer ${this.jwtAuthStateService.getToken()}`,
          },
        },
      )
      .pipe(
        filter((event): event is MessageEvent => event instanceof MessageEvent),
      )
      .subscribe({
        next: (event) => {
          if (
            typeof event.data !== 'string' ||
            !event.data.trim().startsWith('{')
          ) {
            return;
          }

          const walletUpdate = JSON.parse(
            event.data,
          ) as Partial<WalletBalanceEvent>;

          if (typeof walletUpdate.balance !== 'number') {
            return;
          }

          const nextBalance = walletUpdate.balance;
          this.ngZone.run(() => {
            this.walletService.setBalance(nextBalance);
          });
        },
        error: (error) => {
          console.error('Wallet SSE connection error', error);
        },
      });
  }
}
