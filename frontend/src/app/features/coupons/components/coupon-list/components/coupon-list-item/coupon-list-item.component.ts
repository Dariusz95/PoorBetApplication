import { DatePipe } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { Coupon } from '../../../../types/coupon';
import { CouponStatus } from '../../../../types/coupon-status';
import { CouponSummaryComponent } from '../../../coupon-summary/coupon-summary.component';

@Component({
  selector: 'app-coupon-list-item',
  imports: [TranslocoPipe, PbIconComponent, DatePipe, CouponSummaryComponent],
  templateUrl: './coupon-list-item.component.html',
  styleUrl: './coupon-list-item.component.scss',
})
export class CouponListItemComponent {
  readonly coupon = input.required<Coupon>();

  readonly open = output<void>();

  readonly statusIcon = computed(() => {
    switch (this.coupon().status) {
      case CouponStatus.Won:
        return 'check_circle';
      case CouponStatus.Lost:
        return 'cancel';
      case CouponStatus.Open:
        return 'sports_soccer';
    }
  });

  readonly statusLabelKey = computed(() => {
    switch (this.coupon().status) {
      case CouponStatus.Won:
        return 'couponList.statusWon';
      case CouponStatus.Lost:
        return 'couponList.statusLost';
      case CouponStatus.Open:
        return 'couponList.statusOpen';
    }
  });
}
