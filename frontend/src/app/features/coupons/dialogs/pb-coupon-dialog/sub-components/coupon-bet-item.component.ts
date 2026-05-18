import { CommonModule, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { BetInfo } from '@shared/types/coupon.types';

export type BetStatus = 'PENDING' | 'WON' | 'LOST' | 'CASHED_OUT' | 'LIVE';

export interface CouponBetDto extends BetInfo {
  status?: BetStatus;
}

@Component({
  selector: 'app-coupon-bet-item',
  standalone: true,
  imports: [CommonModule, DecimalPipe, TranslocoPipe],
  template: `
    <li
      class="coupon-bet"
      [class.coupon-bet--won]="status === 'WON'"
      [class.coupon-bet--lost]="status === 'LOST'"
      [class.coupon-bet--live]="status === 'LIVE'"
      role="listitem"
    >
      <div class="coupon-bet__info">
        <p class="coupon-bet__match-name">Fc. Barcelona vs Bayern Monachium</p>
        <!-- <p class="coupon-bet__match-name">{{ bet.matchName }}</p> -->
        <p class="coupon-bet__bet-type">{{ bet.betType }}</p>
        @if (status) {
          <span class="coupon-bet__status">{{ status | transloco }}</span>
        }
      </div>
      <span class="coupon-bet__odds">{{ bet.odds | number: '1.2-2' }}</span>
    </li>
  `,
  styleUrl: './coupon-bet-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponBetItemComponent {
  @Input() bet!: CouponBetDto;
  @Input() betNumber: number = 0;
  @Input() status?: BetStatus;
}
