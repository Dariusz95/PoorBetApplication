import { CommonModule, DecimalPipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { BetTypeLabelComponent } from '@features/coupons/components/bet-type-label/bet-type-label.component';
import { Bet } from '@features/coupons/types/bet';
import { BetStatus } from '@features/coupons/types/bet-status';

@Component({
  selector: 'app-coupon-bet-item',
  standalone: true,
  imports: [CommonModule, DecimalPipe, BetTypeLabelComponent],
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
        <app-bet-type-label
          [betType]="this.bet().betType"
          [betStatus]="this.bet().status"
        />
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
            <span class="coupon-bet__live-minute"
              >{{ liveEvent()!.minute }}'</span
            >
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
          >{{ statusIcon() }}</span
        >
      </div>
    </li>
  `,
  styleUrl: './coupon-bet-item.component.scss',
})
export class CouponBetItemComponent {
  readonly bet = input.required<Bet>();
  readonly liveEvent = input<LiveMatchEvent | undefined>();

  readonly isLive = computed(() => {
    const event = this.liveEvent();

    return !!event && event.eventType !== MatchEventType.MatchEnded;
  });

  readonly isEnded = computed(() => {
    const event = this.liveEvent();

    return !!event && event.eventType === MatchEventType.MatchEnded;
  });

  readonly hasStoredResult = computed(() => {
    const bet = this.bet();
    return (
      bet.homeGoals !== null &&
      bet.homeGoals !== undefined &&
      bet.awayGoals !== null &&
      bet.awayGoals !== undefined
    );
  });

  readonly statusIcon = computed(() => {
    switch (this.bet().status) {
      case BetStatus.Won:
        return 'check_circle';
      case BetStatus.Lost:
        return 'cancel';
      default:
        return 'radio_button_unchecked';
    }
  });
}
