import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { BetDetails } from '@shared/types/coupon.types';
import { CouponBetItemComponent } from './coupon-bet-item.component';

@Component({
  selector: 'app-coupon-bets-list',
  standalone: true,
  imports: [CommonModule, CouponBetItemComponent],
  template: `
    <section class="coupon__bets">
      <ul class="coupon__bets-list" role="list">
        @for (bet of bets; track $index) {
          <app-coupon-bet-item [bet]="bet" />
        }
      </ul>
    </section>
  `,
  styleUrl: './coupon-bets-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CouponBetsListComponent {
  @Input() bets: BetDetails[] = [];
}
