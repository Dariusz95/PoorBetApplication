import { Component } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { CouponCardComponent } from '../coupons/components/coupon-card/coupon-card.component';
import { BetTabsComponent } from './components/bet-tabs/bet-tabs.component';

@Component({
  selector: 'app-bet-page',
  imports: [BetTabsComponent, CouponCardComponent, TranslocoDirective],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
})
export class BetPageComponent {}
