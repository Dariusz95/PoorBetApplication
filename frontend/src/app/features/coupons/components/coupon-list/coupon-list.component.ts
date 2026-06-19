import { Component, computed, inject, input, output, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { switchMap } from 'rxjs';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { DialogService } from '@shared/services/dialog.service';
import { PageResponse } from '@shared/interfaces/page-response';
import { CouponStatus } from '../../enums/coupon-status';
import { Coupon } from '../../models/coupon';
import { CouponService } from '../../services/coupon.service';
import { CouponSummaryComponent } from '../coupon-summary/coupon-summary.component';

type CouponTab = 'open' | 'won' | 'settled';

interface TabConfig {
  id: CouponTab;
  labelKey: string;
}

@Component({
  selector: 'app-coupon-list',
  imports: [TranslocoDirective, PbIconComponent, DatePipe, CouponSummaryComponent],
  templateUrl: './coupon-list.component.html',
  styleUrl: './coupon-list.component.scss',
})
export class CouponListComponent {
  private readonly couponService = inject(CouponService);
  private readonly routingService = inject(RoutingService);
  private readonly dialogService = inject(DialogService);

  showHeader = input(true);
  scrollable = input(true);

  readonly seeAll = output<void>();

  readonly activeTab = signal<CouponTab>('open');

  readonly couponPage = toSignal<PageResponse<Coupon> | null>(
    toObservable(this.activeTab).pipe(
      switchMap((tab) => this.loadForTab(tab)),
    ),
    { initialValue: null },
  );

  readonly coupons = computed(() => this.couponPage()?.content ?? []);
  readonly total = computed(() => this.couponPage()?.totalElements ?? 0);

  readonly tabs: TabConfig[] = [
    { id: 'open', labelKey: 'couponList.tabOpen' },
    { id: 'won', labelKey: 'couponList.tabWon' },
    { id: 'settled', labelKey: 'couponList.tabSettled' },
  ];

  readonly CouponStatus = CouponStatus;

  selectTab(tab: CouponTab): void {
    this.activeTab.set(tab);
  }

  navigateToAll(): void {
    this.seeAll.emit();
    this.routingService.navigateTo(RoutePath.MyCoupons);
  }

  openDetails(coupon: Coupon): void {
    this.couponService.getCouponDetails(coupon.id).subscribe((details) => {
      this.dialogService.openCouponDialog(details);
    });
  }

  statusLabelKey(status: CouponStatus): string {
    switch (status) {
      case CouponStatus.Won:
        return 'couponList.statusWon';
      case CouponStatus.Lost:
        return 'couponList.statusLost';
      default:
        return 'couponList.statusOpen';
    }
  }

  emptyTitleKey(): string {
    switch (this.activeTab()) {
      case 'won':
        return 'couponList.emptyWonTitle';
      case 'settled':
        return 'couponList.emptySettledTitle';
      default:
        return 'couponList.emptyTitle';
    }
  }

  private loadForTab(tab: CouponTab) {
    switch (tab) {
      case 'open':
        return this.couponService.getOpen();
      case 'won':
        return this.couponService.getWon();
      case 'settled':
        return this.couponService.getSettled();
    }
  }
}
