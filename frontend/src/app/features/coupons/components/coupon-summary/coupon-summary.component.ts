import { Component, input } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-coupon-summary',
  imports: [TranslocoPipe],
  templateUrl: './coupon-summary.component.html',
  styleUrl: './coupon-summary.component.scss',
})
export class CouponSummaryComponent {
  surface = input(true);
  showOdds = input(true);
  amountLabelKey = input('bet.coupon.amount');
  oddsLabelKey = input('bet.coupon.totalOdds');
  payoutLabelKey = input('bet.coupon.potentialWin');
}
