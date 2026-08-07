import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ShortTeamInfo } from '@features/bet/types/match.types';
import { Uuid } from '@shared/types/uuid.type';
import { catchError, Observable, of, shareReplay } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateTeamRequest } from '../types/create-team-request';
import { Team } from '../types/team';

interface CacheEntry<T> {
  value: T;
  expiresAt: number;
}

const TEAM_DETAILS_TTL_MS = 15 * 60 * 1000;

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/teams/public`;

  private readonly detailsCache = new Map<
    Uuid,
    CacheEntry<Observable<ShortTeamInfo>>
  >();

  getDetails(teamId: Uuid): Observable<ShortTeamInfo> {
    if (!teamId) {
      throw new Error('Team ID is required');
    }

    const entry = this.detailsCache.get(teamId);

    if (entry && entry.expiresAt > Date.now()) {
      return entry.value;
    }

    const request$ = this.http
      .get<ShortTeamInfo>(`${this.baseUrl}/${teamId}`)
      .pipe(shareReplay(1));

    this.detailsCache.set(teamId, {
      value: request$,
      expiresAt: Date.now() + TEAM_DETAILS_TTL_MS,
    });

    return request$;
  }

  removeFromCache(teamId: Uuid): void {
    this.detailsCache.delete(teamId);
  }

  getMyTeam(): Observable<Team | null> {
    return this.http
      .get<Team>(`${this.baseUrl}/my-team`)
      .pipe(catchError(() => of(null)));
  }

  create(request: CreateTeamRequest): Observable<Team> {
    return this.http.post<Team>(this.baseUrl, request);
  }

  updateName(request: CreateTeamRequest): Observable<Team> {
    return this.http.patch<Team>(this.baseUrl, request);
  }

  uploadLogo(file: File): Observable<Team> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.patch<Team>(`${this.baseUrl}/me/logo`, formData);
  }
}
