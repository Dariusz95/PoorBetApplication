import { Dialog } from '@angular/cdk/dialog';
import { Injectable, inject } from '@angular/core';
import { CouponDetails } from '@features/coupons/types/coupon-details';
import { PbCouponDialogComponent } from '../../features/coupons/dialogs/coupon-dialog/coupon-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private readonly dialog = inject(Dialog);

  openCouponDialog(couponData: CouponDetails): void {
    this.dialog.open<PbCouponDialogComponent, CouponDetails>(
      PbCouponDialogComponent,
      {
        width: '600px',
        maxWidth: '90vw',
        disableClose: true,
        backdropClass: 'coupon-dialog-backdrop',
        data: couponData,
      },
    );
  }
}
