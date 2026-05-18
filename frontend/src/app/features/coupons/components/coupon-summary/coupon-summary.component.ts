import { DecimalPipe } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { BetSlipService } from '@features/bet/services/bet-slip.service';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-coupon-summary',
  imports: [TranslocoPipe, DecimalPipe],
  templateUrl: './coupon-summary.component.html',
  styleUrl: './coupon-summary.component.scss',
})
export class CouponSummaryComponent {
  protected readonly betSlipService = inject(BetSlipService);

  amount = input<number | null>(null);

  amountValue = computed(() => {
    return this.amount() || 0;
  });
}
