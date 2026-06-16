import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { CouponSummaryComponent } from '@features/coupons/components/coupon-summary/coupon-summary.component';
import { TranslocoPipe } from '@jsverse/transloco';
import { CouponDetails } from '@shared/types/coupon.types';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '../../../../shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '../../../../shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '../../../../shared/ui/pb-card/pb-card.component';
import { CouponBetsListComponent } from './components/coupon-bets-list.component';
import { CouponDialogData } from './models/coupon-dialog-data.model';

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
export class PbCouponDialogComponent implements OnInit {
  couponData!: CouponDetails;

  private readonly dialogRef = inject(DialogRef<void>);
  readonly data = inject<CouponDialogData>(DIALOG_DATA);
  ngOnInit(): void {
    this.couponData = this.data.data;
  }

  closeCoupon(): void {
    this.dialogRef.close();
  }
}
