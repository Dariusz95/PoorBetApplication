import { CommonModule, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { BetDetails } from '@shared/types/coupon.types';

@Component({
  selector: 'app-coupon-bet-item',
  standalone: true,
  imports: [CommonModule, DecimalPipe, TranslocoPipe],
  template: `
    <li
      class="coupon-bet"
      [class.coupon-bet--won]="bet.status === 'WON'"
      [class.coupon-bet--lost]="bet.status === 'LOST'"
      [class.coupon-bet--pending]="bet.status === 'PENDING'"
      role="listitem"
    >
      <div class="coupon-bet__info">
        <p class="coupon-bet__match-name">
          {{ bet.homeTeamName + ' vs ' + bet.awayTeamName }}
        </p>
        <p class="coupon-bet__bet-type">{{ bet.betType }}</p>
        @if (bet.status) {
          <span class="coupon-bet__status">{{ bet.status | transloco }}</span>
        }
      </div>
      <span class="coupon-bet__odds">{{ bet.odds | number: '1.2-2' }}</span>
    </li>
  `,
  styleUrl: './coupon-bet-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponBetItemComponent {
  @Input() bet!: BetDetails;
}
