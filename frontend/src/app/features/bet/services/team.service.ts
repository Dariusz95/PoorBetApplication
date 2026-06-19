import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Uuid } from '@shared/types/uuid.type';
import { Observable, shareReplay, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ShortTeamInfo } from '../types/match.types';

interface CacheEntry<T> {
  value: T;
  expiresAt: number;
}

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly http = inject(HttpClient);

  private BASE_URL = `${environment.backend.baseURL}/api/teams/public`;
  private cache = new Map<Uuid, CacheEntry<Observable<ShortTeamInfo>>>();
  private TTL = 15 * 60 * 1000; // 15 min

  getDetails(teamId: Uuid): Observable<ShortTeamInfo> {
    if (!teamId) {
      throw new Error('Team ID is required');
    }

    const entry = this.cache.get(teamId);

    // cache hit + valid TTL
    if (entry && entry.expiresAt > Date.now()) {
      return entry.value;
    }

    const request$ = this.http
      .get<ShortTeamInfo>(`${this.BASE_URL}/${teamId}`)
      .pipe(
        shareReplay(1),
        tap((team) => {
          this.cache.set(teamId, {
            value: request$,
            expiresAt: Date.now() + this.TTL,
          });
        })
      );

    this.cache.set(teamId, {
      value: request$,
      expiresAt: Date.now() + this.TTL,
    });

    return request$;
  }
}