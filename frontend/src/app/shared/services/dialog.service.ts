import { Dialog } from '@angular/cdk/dialog';
import { Injectable, inject } from '@angular/core';
import { PbCouponDialogComponent } from '../../features/coupons/dialogs/pb-coupon-dialog/pb-coupon-dialog.component';
import { CouponDetails } from '../types/coupon.types';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private readonly dialog = inject(Dialog);

  openCouponDialog(couponData: CouponDetails): void {
    const dialogRef = this.dialog.open(PbCouponDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      disableClose: false,
      panelClass: 'coupon-dialog',
    });

    if (dialogRef.componentInstance) {
      dialogRef.componentInstance.couponData = couponData;
    }
  }
}
