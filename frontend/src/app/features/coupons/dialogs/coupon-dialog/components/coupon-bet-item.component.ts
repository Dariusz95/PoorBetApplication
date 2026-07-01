import { CommonModule, DecimalPipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { LiveMatchEvent, MatchEventType } from '@features/bet/types/match.types';
import { Bet } from '@features/coupons/types/bet';
import { BetStatus } from '@features/coupons/enums/bet-status';
import { TranslocoPipe } from '@jsverse/transloco';

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
        >{{ bet().betType }}</p>
        @if (isLive()) {
          <div class="coupon-bet__live">
            <span class="coupon-bet__live-dot">
              <span class="coupon-bet__live-dot-ping"></span>
              <span class="coupon-bet__live-dot-core"></span>
            </span>
            <span class="coupon-bet__live-label">{{ 'coupon.live' | transloco }}</span>
            <span class="coupon-bet__live-score">
              {{ liveEvent()!.homeScore }}:{{ liveEvent()!.awayScore }}
            </span>
            <span class="coupon-bet__live-minute">{{ liveEvent()!.minute }}'</span>
          </div>
        } @else if (isEnded()) {
          <span class="coupon-bet__final-score">
            {{ liveEvent()!.homeScore }}:{{ liveEvent()!.awayScore }}
            <span class="coupon-bet__final-label">{{ 'coupon.finalScore' | transloco }}</span>
          </span>
        }
      </div>
      <div class="coupon-bet__right">
        <span class="coupon-bet__odds">{{ bet().odds | number: '1.2-2' }}</span>
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

  isLive = computed(() => {
    const e = this.liveEvent();
    return !!e && e.eventType !== MatchEventType.MatchEnded;
  });

  isEnded = computed(() => {
    const e = this.liveEvent();
    return !!e && e.eventType === MatchEventType.MatchEnded;
  });

  statusIcon = computed(() => {
    switch (this.bet().status) {
      case BetStatus.Won: return 'check_circle';
      case BetStatus.Lost: return 'cancel';
      default: return 'radio_button_unchecked';
    }
  });
}
