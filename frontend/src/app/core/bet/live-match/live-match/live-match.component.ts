import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
} from '@angular/core';
import { Observable } from 'rxjs';
import { LiveMatchEvent, MatchEventType } from '../../services/match.service';
import { TeamService } from '../../services/team.service';
import { AsyncPipe } from '@angular/common';

export interface ShortTeamInfo {
  id: string;
  name: string;
}

@Component({
  selector: 'app-live-match',
  imports: [AsyncPipe],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent {
  liveMatch = input.required<LiveMatchEvent>();

  private readonly teamService = inject(TeamService);

  homeTeam$!: Observable<ShortTeamInfo>;
  awayTeam$!: Observable<ShortTeamInfo>;

  MatchEventType = MatchEventType;

  ngOnInit(): void {
    this.homeTeam$ = this.teamService.getTeam(this.liveMatch().homeTeamId);
    this.awayTeam$ = this.teamService.getTeam(this.liveMatch().awayTeamId);
  }
}
