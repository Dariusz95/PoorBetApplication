import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { isNotNil } from '@shared/utils/is-not-nil.util';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ShortTeamInfo } from '../types/match.types';
import { Uuid } from '@shared/types/uuid.type';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly http = inject(HttpClient);

  private cache = new Map<Uuid, ShortTeamInfo>();
  private BASE_URL = `${environment.backend.baseURL}/api/teams/public`;

  getDetails(teamId: Uuid): Observable<ShortTeamInfo> {
    if (!teamId) {
      throw new Error('Team ID is required');
    }

    if (this.cache.has(teamId)) {
      return of(this.cache.get(teamId)!);
    }

    return this.http.get<ShortTeamInfo>(`${this.BASE_URL}/${teamId}`).pipe(
      tap((team) => {
        if (!isNotNil(team)) return;

        this.cache.set(teamId, team);
      }),
    );
  }
}
