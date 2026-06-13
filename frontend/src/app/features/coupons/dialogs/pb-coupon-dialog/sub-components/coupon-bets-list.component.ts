import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { BetInfo } from '@shared/types/coupon.types';
import {
  BetStatus,
  CouponBetDto,
  CouponBetItemComponent,
} from './coupon-bet-item.component';

@Component({
  selector: 'app-coupon-bets-list',
  standalone: true,
  imports: [CommonModule, CouponBetItemComponent],
  template: `
    <section class="coupon__bets">
      <ul class="coupon__bets-list" role="list">
        @for (bet of bets; track $index; let i = $index) {
          <app-coupon-bet-item
            [bet]="bet"
            [betNumber]="i + 1"
            [status]="getBetStatus(bet)"
          />
        }
      </ul>
    </section>
  `,
  styleUrl: './coupon-bets-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponBetsListComponent {
  @Input() bets: BetInfo[] = [];
  @Input() betStatuses: Map<string, BetStatus> = new Map();

  getBetStatus(bet: BetInfo): BetStatus | undefined {
    const betWithStatus = bet as CouponBetDto;

    return betWithStatus.status;
  }
}
