import { AsyncPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnInit,
} from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { Observable } from 'rxjs';
import { TeamService } from '../../services/team.service';
import {
  LiveMatchEvent,
  MatchEventType,
  ShortTeamInfo,
} from '../../types/match.types';

@Component({
  selector: 'app-live-match-card',
  standalone: true,
  imports: [AsyncPipe, PbCardComponent, TranslocoPipe, PbCardHeaderDirective],
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
    this.homeTeam$ = this.teamService.getDetails(this.liveMatch().homeTeamId);
    this.awayTeam$ = this.teamService.getDetails(this.liveMatch().awayTeamId);
  }
}
