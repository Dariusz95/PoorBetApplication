import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CouponCardComponent } from '../coupons/components/coupon-card/coupon-card.component';
import { CouponMobileBarComponent } from '../coupons/components/coupon-mobile-bar/coupon-mobile-bar.component';
import { BetTabsComponent } from './components/bet-tabs/bet-tabs.component';

@Component({
  selector: 'app-bet-page',
  imports: [
    BetTabsComponent,
    CouponCardComponent,
    CouponMobileBarComponent,
    TranslocoDirective,
  ],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
})
export class BetPageComponent {}
