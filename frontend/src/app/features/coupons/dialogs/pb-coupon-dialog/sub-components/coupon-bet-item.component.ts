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
      class="coupon__bet-item"
      [class.coupon__bet-item--won]="status === 'WON'"
      [class.coupon__bet-item--lost]="status === 'LOST'"
      [class.coupon__bet-item--live]="status === 'LIVE'"
      role="listitem"
    >
      <div class="coupon__bet-match">
  
        <div class="coupon__bet-info">
          <p class="coupon__match-name">{{ bet.matchName }}</p>
          <p class="coupon__bet-type">{{ bet.betType }}</p>
          @if (status) {
            <span class="coupon__bet-status">{{ status | transloco }}</span>
          }
        </div>
      </div>
      <span class="coupon__bet-odds">{{ bet.odds | number: '1.2-2' }}</span>
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
