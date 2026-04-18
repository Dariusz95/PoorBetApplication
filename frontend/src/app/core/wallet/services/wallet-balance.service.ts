import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, inject, signal } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment.development';
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

  private BASE_URL = `${environment.backend.baseURL}/api`;

  init(): void {
    const subject = this.jwtAuthStateService.getSubject();

    if (!subject) {
      return;
    }

    this.http.get<WalletResponse>('/api/wallet/me').subscribe({
      next: (wallet) => this.balance.set(wallet.balance),
    });

    this.connectToLiveUpdates(subject);
    this.conn().subscribe((v) => console.log(v));
  }

  private connectToLiveUpdates(subject: string): void {
    this.eventSource?.close();
    console.log('here');
    this.eventSource = new EventSource(`/api/notifications/stream`);

    this.eventSource.addEventListener('wallet.balance-changed', (event) => {
      this.ngZone.run(() => {
        const data: WalletBalanceEvent = JSON.parse(
          (event as MessageEvent).data,
        );
        this.balance.set(data.balance);
      });
    });

    this.eventSource.onerror = () => {
      this.eventSource?.close();
      this.eventSource = null;
    };
  }

  conn(): Observable<any> {
    return new Observable<any>((observer) => {
      const eventSource = new EventSource(
        `${this.BASE_URL}/notifications/stream`,
      );

      eventSource.onmessage = (event) => {
        this.ngZone.run(() => {
          const data: any = JSON.parse(event.data);
          observer.next(data);
        });
      };

      eventSource.onerror = (error) => {
        this.ngZone.run(() => {
          observer.error(error);
        });
        eventSource.close();
      };

      return () => {
        eventSource.close();
      };
    });
  }
}
