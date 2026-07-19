import {
  ChangeDetectionStrategy,
  Component,
  effect,
  input,
  signal,
} from '@angular/core';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { LiveMatchTeamComponent } from '../live-match-team/live-match-team.component';

const SCORE_CHANGE_POP_DURATION_MS = 400;

@Component({
  selector: 'app-live-match-card',
  imports: [
    PbCardComponent,
    TranslocoPipe,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    LiveMatchTeamComponent,
  ],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent {
  readonly liveMatch = input.required<LiveMatchEvent>();

  readonly MatchEventType = MatchEventType;

  protected readonly homeScoreChanged = signal(false);
  protected readonly awayScoreChanged = signal(false);

  private previousHomeScore: number | null = null;
  private previousAwayScore: number | null = null;
  private homeScoreTimeout?: ReturnType<typeof setTimeout>;
  private awayScoreTimeout?: ReturnType<typeof setTimeout>;

  constructor() {
    effect(() => {
      const { homeScore, awayScore } = this.liveMatch();

      if (
        this.previousHomeScore !== null &&
        this.previousHomeScore !== homeScore
      ) {
        this.homeScoreChanged.set(true);
        clearTimeout(this.homeScoreTimeout);
        this.homeScoreTimeout = setTimeout(
          () => this.homeScoreChanged.set(false),
          SCORE_CHANGE_POP_DURATION_MS,
        );
      }
      if (
        this.previousAwayScore !== null &&
        this.previousAwayScore !== awayScore
      ) {
        this.awayScoreChanged.set(true);
        clearTimeout(this.awayScoreTimeout);
        this.awayScoreTimeout = setTimeout(
          () => this.awayScoreChanged.set(false),
          SCORE_CHANGE_POP_DURATION_MS,
        );
      }

      this.previousHomeScore = homeScore;
      this.previousAwayScore = awayScore;
    });
  }
}
