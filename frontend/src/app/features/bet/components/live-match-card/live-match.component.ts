import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  signal,
} from '@angular/core';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { LiveCouponView } from '@features/coupons/types/live-coupon-view';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { LiveMatchCouponsComponent } from '../live-match-coupons/live-match-coupons.component';
import { LiveMatchProgressComponent } from '../live-match-progress/live-match-progress.component';
import { LiveMatchTeamComponent } from '../live-match-team/live-match-team.component';

const SCORE_CHANGE_POP_DURATION_MS = 400;
const FIRST_HALF_END_MINUTE = 45;

@Component({
  selector: 'app-live-match-card',
  imports: [
    PbCardComponent,
    TranslocoPipe,
    PbCardBodyDirective,
    LiveMatchTeamComponent,
    LiveMatchProgressComponent,
    LiveMatchCouponsComponent,
  ],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent {
  readonly liveMatch = input.required<LiveMatchEvent>();
  readonly coupons = input<LiveCouponView[]>([]);

  readonly MatchEventType = MatchEventType;

  protected readonly homeScoreChanged = signal(false);
  protected readonly awayScoreChanged = signal(false);

  protected readonly half = computed(() =>
    this.liveMatch().minute <= FIRST_HALF_END_MINUTE ? 1 : 2,
  );

  private previousHomeScore: number | null = null;
  private previousAwayScore: number | null = null;
  private homeScoreTimeout?: ReturnType<typeof setTimeout>;
  private awayScoreTimeout?: ReturnType<typeof setTimeout>;

  constructor() {
    effect(() => {
      const { homeScore, awayScore } = this.liveMatch();

      const hasHomeScored =
        this.previousHomeScore !== null && this.previousHomeScore !== homeScore;
      const hasAwayScored =
        this.previousAwayScore !== null && this.previousAwayScore !== awayScore;

      if (hasHomeScored) {
        this.homeScoreChanged.set(true);

        clearTimeout(this.homeScoreTimeout);
        this.homeScoreTimeout = setTimeout(
          () => this.homeScoreChanged.set(false),
          SCORE_CHANGE_POP_DURATION_MS,
        );
      }
      if (hasAwayScored) {
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
