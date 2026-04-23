import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone, inject, signal } from '@angular/core';
import { SseClient } from 'ngx-sse-client';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class WalletBalanceService {
  private readonly http = inject(HttpClient);
  private readonly ngZone = inject(NgZone);
  private readonly sseClient = inject(SseClient);

  readonly balance = signal<number | null>(null);

  private BASE_URL = `${environment.backend.baseURL}/api/notifications/stream`;

  init(): void {
    return;
  }

  private connectToLiveUpdates(): void {
    return;
  }
}
