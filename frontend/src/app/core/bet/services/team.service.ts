import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private readonly http = inject(HttpClient);

  private apiBaseUrl = `${environment.backend.baseURL}/api/team`;

  getTeam(teamId:string):any{
    return this.http.get<any>(`${this.apiBaseUrl}/${teamId}`);
  }
}
