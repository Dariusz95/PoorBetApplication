import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  private readonly http = inject(HttpClient);

  private apiBaseUrl = `${environment.backend.baseURL}/api/teams`;

  getTeam(teamId: string): any {
    return this.http.get<any>(`${this.apiBaseUrl}/${teamId}`);
  }
}
