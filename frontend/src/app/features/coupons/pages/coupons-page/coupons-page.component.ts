import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CouponListComponent } from '../../components/coupon-list/coupon-list.component';

@Component({
  selector: 'app-my-coupons-page',
  imports: [TranslocoDirective, CouponListComponent],
  templateUrl: './coupons-page.component.html',
})
export class CouponsPageComponent {}
