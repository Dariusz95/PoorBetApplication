import { Dialog } from '@angular/cdk/dialog';
import { Overlay } from '@angular/cdk/overlay';
import { Injectable, inject } from '@angular/core';
import { CouponDetails } from '@features/coupons/types/coupon-details';
import { BetTabsHelpComponent } from '../../features/bet/components/bet-tabs-help/bet-tabs-help.component';
import { CouponCardComponent } from '../../features/coupons/components/coupon-card/coupon-card.component';
import { PbCouponDialogComponent } from '../../features/coupons/dialogs/coupon-dialog/coupon-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private readonly dialog = inject(Dialog);
  private readonly overlay = inject(Overlay);

  openCouponDialog(couponData: CouponDetails): void {
    this.dialog.open<PbCouponDialogComponent, CouponDetails>(
      PbCouponDialogComponent,
      {
        width: '600px',
        maxWidth: '90vw',
        disableClose: true,
        backdropClass: 'coupon-dialog-backdrop',
        ariaLabelledBy: 'coupon-dialog-title',
        data: couponData,
      },
    );
  }

  openCouponSheet(): void {
    this.dialog.open<CouponCardComponent>(CouponCardComponent, {
      positionStrategy: this.overlay.position().global().centerVertically(),
      width: '100%',
      panelClass: 'coupon-mobile-sheet',
      hasBackdrop: true,
      backdropClass: 'coupon-mobile-sheet-backdrop',
    });
  }

  openBetTabsHelpDialog(): void {
    this.dialog.open<BetTabsHelpComponent>(BetTabsHelpComponent, {
      width: '640px',
      maxWidth: '90vw',
      backdropClass: 'coupon-dialog-backdrop',
      ariaLabelledBy: 'bet-tabs-help-title',
    });
  }
}
