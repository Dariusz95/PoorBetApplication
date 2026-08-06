import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateTeamRequest } from '../types/create-team-request';
import { Team } from '../types/team';

// Fixture used until the "my team" GET endpoint exists on the backend.
const MOCK_MY_TEAM: Team = {
  id: 'mock-team-id',
  name: 'FC Nocna Zmiana',
  img: null,
};

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/teams/public`;

  getMyTeam(): Observable<Team | null> {
    return of(MOCK_MY_TEAM).pipe(delay(400));
  }

  create(request: CreateTeamRequest): Observable<unknown> {
    return this.http.post(this.baseUrl, request);
  }

  updateName(request: CreateTeamRequest): Observable<Team> {
    return this.http.patch<Team>(`${this.baseUrl}/me`, request);
  }

  uploadLogo(file: File): Observable<Team> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.patch<Team>(`${this.baseUrl}/me/logo`, formData);
  }
}
