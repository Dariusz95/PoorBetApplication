import { DecimalPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
} from '@angular/core';
import { CouponService } from '@features/coupons/services/coupon.service';
import { LiveCouponView } from '@features/coupons/types/live-coupon-view';
import { TranslocoPipe } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { Uuid } from '@shared/types/uuid.type';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';

@Component({
  selector: 'app-live-match-coupons',
  imports: [TranslocoPipe, DecimalPipe, PbIconComponent],
  templateUrl: './live-match-coupons.component.html',
  styleUrl: './live-match-coupons.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchCouponsComponent {
  private readonly couponService = inject(CouponService);
  private readonly dialogService = inject(DialogService);

  readonly coupons = input<LiveCouponView[]>([]);

  protected openCoupon(couponId: Uuid): void {
    this.couponService
      .getCouponDetails(couponId)
      .subscribe((details) => this.dialogService.openCouponDialog(details));
  }
}
