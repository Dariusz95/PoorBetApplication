import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Uuid } from '@shared/types/uuid.type';
import { Observable, of, tap } from 'rxjs';
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
  private cache = new Map<Uuid, CacheEntry<ShortTeamInfo>>();
  private TTL = 15 * 60 * 1000; // 15 min

  getDetails(teamId: Uuid): Observable<ShortTeamInfo> {
    if (!teamId) {
      throw new Error('Team ID is required');
    }

    const entry = this.cache.get(teamId);

    if (entry && entry.expiresAt > Date.now()) {
      return of(entry.value);
    }

    return this.http.get<ShortTeamInfo>(`${this.BASE_URL}/${teamId}`).pipe(
      tap((team) => {
        this.cache.set(teamId, {
          value: team,
          expiresAt: Date.now() + this.TTL,
        });
      }),
    );
  }
}
