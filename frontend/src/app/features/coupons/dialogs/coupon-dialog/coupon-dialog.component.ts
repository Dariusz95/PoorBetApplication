import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { BetStatus } from '@features/coupons/enums/bet-status';
import { CouponStatus } from '@features/coupons/enums/coupon-status';
import { CouponSummaryComponent } from '@features/coupons/components/coupon-summary/coupon-summary.component';
import { CouponDetails } from '@features/coupons/types/coupon-details';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '../../../../shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '../../../../shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '../../../../shared/ui/pb-card/pb-card.component';
import { CouponBetsListComponent } from './components/coupon-bets-list.component';

@Component({
  selector: 'coupon-dialog',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoPipe,
    PbCardComponent,
    CouponBetsListComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbButtonComponent,
    PbIconComponent,
    CouponSummaryComponent,
  ],
  templateUrl: './coupon-dialog.component.html',
  styleUrl: './coupon-dialog.component.scss',
})
export class PbCouponDialogComponent {
  private readonly dialogRef = inject(DialogRef<void>);
  readonly data = inject<CouponDetails>(DIALOG_DATA);

  readonly wonCount = computed(() => this.data.bets.filter((b) => b.status === BetStatus.Won).length);
  readonly lostCount = computed(() => this.data.bets.filter((b) => b.status === BetStatus.Lost).length);

  readonly CouponStatus = CouponStatus;

  closeCoupon(): void {
    this.dialogRef.close();
  }
}
