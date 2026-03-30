import { HttpClient } from '@angular/common/http';
import { inject, Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LiveMatchEvent, PoolMatch } from '../types/match.types';

@Injectable({
  providedIn: 'root',
})
export class MatchService {
  private readonly http = inject(HttpClient);
  private readonly ngZone = inject(NgZone);

  private BASE_URL = `${environment.backend.baseURL}/api/match-pool`;

  streamMatch(): Observable<LiveMatchEvent> {
    return new Observable<LiveMatchEvent>((observer) => {
      const eventSource = new EventSource(`${this.BASE_URL}/live`);

      eventSource.onmessage = (event) => {
        this.ngZone.run(() => {
          const data: LiveMatchEvent = JSON.parse(event.data);
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

  futureMatch(): Observable<PoolMatch[]> {
    return this.http.get<PoolMatch[]>(`${this.BASE_URL}/future`);
  }
}
