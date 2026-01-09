import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  input,
} from '@angular/core';
import { LiveMatchEvent } from '../../services/match.service';
import { TeamService } from '../../services/team.service';

@Component({
  selector: 'app-live-match',
  imports: [],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent {
  liveMatch = input.required<LiveMatchEvent>();

  private readonly teamService = inject(TeamService);

  constructor() {
    effect(() => {
      if (this.liveMatch()) {
        return;
      }

      this.teamService
        .getTeam(this.liveMatch().homeTeamId)
        .subscribe((team: any) => {
          console.log('Home Team:', team);
        });

      this.teamService
        .getTeam(this.liveMatch().awayTeamId)
        .subscribe((team: any) => {
          console.log('Away Team:', team);
        });
    });
  }
}
