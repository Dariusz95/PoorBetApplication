import { Component, input, output } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { TabConfig } from '../../interfaces/tab-config';
import { CouponTab } from '../../types/coupon-tab';

@Component({
  selector: 'app-coupon-list-tabs',
  imports: [TranslocoPipe],
  templateUrl: './coupon-list-tabs.component.html',
  styleUrl: './coupon-list-tabs.component.scss',
})
export class CouponListTabsComponent {
  readonly tabs = input.required<TabConfig[]>();
  readonly activeTab = input.required<CouponTab>();
  readonly total = input(0);

  readonly tabChange = output<CouponTab>();
}
