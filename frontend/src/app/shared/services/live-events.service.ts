import { LiveAnnouncer } from '@angular/cdk/a11y';
import { inject, Injectable, NgZone, signal } from '@angular/core';
import { AuthService } from '@core/auth/services/auth.service';
import { JwtAuthStateService } from '@core/auth/services/jwt-auth-state.service';
import { AccountService } from '@core/account/services/account.service';
import { TranslocoService } from '@jsverse/transloco';
import { SseClient } from 'ngx-sse-client';
import { filter } from 'rxjs';
import { environment } from 'src/environments/environment';
import {
  AccountProgressEvent,
  WalletBalanceEvent,
} from '../types/wallet.types';

const WALLET_BALANCE_CHANGED = 'wallet.balance-changed';
const ACCOUNT_PROGRESS_CHANGED = 'account.progress-changed';

@Injectable({
  providedIn: 'root',
})
export class LiveEventsService {
  private readonly ngZone = inject(NgZone);
  private readonly sseClient = inject(SseClient);
  private readonly jwtAuthStateService = inject(JwtAuthStateService);
  private readonly authService = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly liveAnnouncer = inject(LiveAnnouncer);
  private readonly translocoService = inject(TranslocoService);
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

          switch (event.type) {
            case WALLET_BALANCE_CHANGED:
              this.handleWalletBalanceChanged(
                JSON.parse(event.data) as WalletBalanceEvent,
              );
              break;
            case ACCOUNT_PROGRESS_CHANGED:
              this.handleAccountProgressChanged(
                JSON.parse(event.data) as AccountProgressEvent,
              );
              break;
          }
        },
        error: (error) => {
          console.error('Wallet SSE connection error', error);
        },
      });
  }

  private handleWalletBalanceChanged(event: WalletBalanceEvent): void {
    this.ngZone.run(() => {
      this.accountService.setBalance(event.balance);
      this.liveAnnouncer.announce(
        this.translocoService.translate('header.balanceUpdated', {
          balance: event.balance,
        }),
        'polite',
      );
    });
  }

  private handleAccountProgressChanged(event: AccountProgressEvent): void {
    this.ngZone.run(() => {
      this.accountService.setProgress(
        event.level,
        event.currentExp,
        event.requiredExpForNextLevel,
        event.winBonusPercent,
      );
    });
  }
}
