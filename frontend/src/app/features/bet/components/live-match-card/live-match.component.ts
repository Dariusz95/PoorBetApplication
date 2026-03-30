import { AsyncPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnInit,
} from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { Observable } from 'rxjs';
import { TeamService } from '../../services/team.service';
import {
  LiveMatchEvent,
  MatchEventType,
  ShortTeamInfo,
} from '../../types/match.types';
import { PbCardComponent } from '@shared/components/pb-card/pb-card.component';

@Component({
  selector: 'app-live-match-card',
  standalone: true,
  imports: [AsyncPipe, PbCardComponent, TranslocoDirective],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent implements OnInit {
  private readonly teamService = inject(TeamService);

  liveMatch = input.required<LiveMatchEvent>();

  homeTeam$!: Observable<ShortTeamInfo>;
  awayTeam$!: Observable<ShortTeamInfo>;

  MatchEventType = MatchEventType;

  ngOnInit(): void {
    this.homeTeam$ = this.teamService.getTeam(this.liveMatch().homeTeamId);
    this.awayTeam$ = this.teamService.getTeam(this.liveMatch().awayTeamId);
  }
}
