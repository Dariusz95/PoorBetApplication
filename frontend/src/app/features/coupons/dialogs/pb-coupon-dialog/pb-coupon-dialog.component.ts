import { DialogRef } from '@angular/cdk/dialog';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { CouponData } from '@shared/types/coupon.types';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '../../../../shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '../../../../shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '../../../../shared/ui/pb-card/pb-card.component';
import { CouponBetItemComponent } from './sub-components/coupon-bet-item.component';
import { CouponFooterComponent } from './sub-components/coupon-footer.component';
import { CouponSummaryRowComponent } from './sub-components/coupon-summary-row.component';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';

@Component({
  selector: 'pb-coupon-dialog',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoPipe,
    PbCardComponent,
    CouponFooterComponent,
    CouponSummaryRowComponent,
    CouponBetItemComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbButtonComponent,
    PbIconComponent,
  ],
  templateUrl: './pb-coupon-dialog.component.html',
  styleUrl: './pb-coupon-dialog.component.scss',
})
export class PbCouponDialogComponent implements OnInit {
  couponData!: CouponData;

  private readonly dialogRef = inject(DialogRef<void>);

  ngOnInit(): void {
    if (this.dialogRef.componentInstance) {
      console.log(this.dialogRef.componentInstance);
      //   this.couponData = this.dialogRef.componentInstance.couponData;
    }
  }

  closeCoupon(): void {
    this.dialogRef.close();
  }
}
