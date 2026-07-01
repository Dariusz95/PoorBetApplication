import { CommonModule, DecimalPipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { LiveMatchEvent, MatchEventType } from '@features/bet/types/match.types';
import { Bet } from '@features/coupons/types/bet';
import { BetStatus } from '@features/coupons/types/bet-status';
import { TranslocoPipe } from '@jsverse/transloco';
import { BET_TYPE_TO_OPTION } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';

@Component({
  selector: 'app-coupon-bet-item',
  standalone: true,
  imports: [CommonModule, DecimalPipe, TranslocoPipe],
  template: `
    <li
      class="coupon-bet"
      [class.coupon-bet--won]="bet().status === 'WON'"
      [class.coupon-bet--lost]="bet().status === 'LOST'"
      [class.coupon-bet--pending]="bet().status === 'PENDING'"
      role="listitem"
    >
      <div class="coupon-bet__info">
        <p class="coupon-bet__match-name">
          {{ bet().homeTeamName }} - {{ bet().awayTeamName }}
        </p>
        <p
          class="coupon-bet__bet-type"
          [class.coupon-bet__bet-type--won]="bet().status === 'WON'"
          [class.coupon-bet__bet-type--lost]="bet().status === 'LOST'"
        >{{ 'coupon.match' | transloco }}: {{ betLabel() }}</p>
        <span class="coupon-bet__odds">{{ bet().odds | number: '1.2-2' }}</span>
      </div>
      <div class="coupon-bet__right">
        @if (isLive()) {
          <div class="coupon-bet__live">
            <span class="coupon-bet__live-dot">
              <span class="coupon-bet__live-dot-ping"></span>
              <span class="coupon-bet__live-dot-core"></span>
            </span>
            <span class="coupon-bet__live-score">
              {{ liveEvent()!.homeScore }}:{{ liveEvent()!.awayScore }}
            </span>
            <span class="coupon-bet__live-minute">{{ liveEvent()!.minute }}'</span>
          </div>
        } @else if (isEnded()) {
          <span class="coupon-bet__final-score">
            {{ liveEvent()!.homeScore }}:{{ liveEvent()!.awayScore }}
            <span class="coupon-bet__final-label">FT</span>
          </span>
        } @else if (hasStoredResult()) {
          <span class="coupon-bet__final-score">
            {{ bet().homeGoals }}:{{ bet().awayGoals }}
            <span class="coupon-bet__final-label">FT</span>
          </span>
        }
        <span
          class="coupon-bet__status-icon material-icons"
          [class.coupon-bet__status-icon--won]="bet().status === 'WON'"
          [class.coupon-bet__status-icon--lost]="bet().status === 'LOST'"
          [class.coupon-bet__status-icon--pending]="bet().status === 'PENDING'"
          aria-hidden="true"
        >{{ statusIcon() }}</span>
      </div>
    </li>
  `,
  styleUrl: './coupon-bet-item.component.scss',
})
export class CouponBetItemComponent {
  bet = input.required<Bet>();
  liveEvent = input<LiveMatchEvent | undefined>();

  betLabel = computed(() => BET_TYPE_TO_OPTION[this.bet().betType as BetType] ?? this.bet().betType);

  isLive = computed(() => {
    const e = this.liveEvent();
    return !!e && e.eventType !== MatchEventType.MatchEnded;
  });

  isEnded = computed(() => {
    const e = this.liveEvent();
    return !!e && e.eventType === MatchEventType.MatchEnded;
  });

  hasStoredResult = computed(() => {
    const b = this.bet();
    return b.homeGoals !== null && b.homeGoals !== undefined &&
           b.awayGoals !== null && b.awayGoals !== undefined;
  });

  statusIcon = computed(() => {
    switch (this.bet().status) {
      case BetStatus.Won: return 'check_circle';
      case BetStatus.Lost: return 'cancel';
      default: return 'radio_button_unchecked';
    }
  });
}
