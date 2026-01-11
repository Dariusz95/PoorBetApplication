import { HttpClient } from '@angular/common/http';
import { inject, Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface LiveMatchEvent {
  id: string;
  minute: number;
  homeTeamId: string;
  awayTeamId: string;
  homeScore: number;
  awayScore: number;
  eventType: MatchEventType;
  eventData: null | string;
}

export enum MatchEventType {
  MatchStarted = 'MATCH_STARTED',
  Live = 'LIVE',
  MatchEnded = 'MATCH_ENDED',
  Heartbeat = 'HEARTBEAT',
}

export interface OddsDto {
  id: string; 
  homeWin: number;
  draw: number;
  awayWin: number;
}

export interface MatchDto {
  matchId: string;
  homeTeamId: string;
  awayTeamId: string;
  odds: OddsDto;
}

export interface PoolMatch {
  id: string; 
  status: string;
  scheduledStartTime: string;
  matches: MatchDto[];
}

@Injectable({
  providedIn: 'root',
})
export class MatchService {
  private readonly http = inject(HttpClient);
  private readonly ngZone = inject(NgZone);

  private apiBaseUrl = `${environment.backend.baseURL}/api/match`;

  streamMatch(): Observable<LiveMatchEvent> {
    return new Observable<LiveMatchEvent>((observer) => {
      const eventSource = new EventSource(this.apiBaseUrl);

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

      // Cleanup przy odsubskrybowaniu
      return () => {
        eventSource.close();
      };
    });
  }

  futureMatch(): Observable<PoolMatch[]> {
    return this.http.get<PoolMatch[]>(`${this.apiBaseUrl}/future`);
  }
}
