import { DatePipe } from '@angular/common';
import {
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { TranslocoDirective } from '@jsverse/transloco';
import { PageRequest } from '@shared/interfaces/page-request';
import { PageResponse } from '@shared/interfaces/page-response';
import { DialogService } from '@shared/services/dialog.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';
import { combineLatest, finalize, switchMap, tap } from 'rxjs';
import { CouponStatus } from '../../types/coupon-status';
import { CouponService } from '../../services/coupon.service';
import { Coupon } from '../../types/coupon';
import { CouponFilter } from '../../types/coupon-filter';
import { CouponSummaryComponent } from '../coupon-summary/coupon-summary.component';

type CouponTab = 'open' | 'won' | 'settled';

interface TabConfig {
  id: CouponTab;
  labelKey: string;
}

@Component({
  selector: 'app-coupon-list',
  imports: [
    TranslocoDirective,
    PbIconComponent,
    PbSpinnerComponent,
    DatePipe,
    CouponSummaryComponent,
  ],
  templateUrl: './coupon-list.component.html',
  styleUrl: './coupon-list.component.scss',
})
export class CouponListComponent {
  private readonly couponService = inject(CouponService);
  private readonly routingService = inject(RoutingService);
  private readonly dialogService = inject(DialogService);

  readonly showHeader = input(true);
  readonly scrollable = input(true);

  readonly seeAll = output<void>();

  readonly activeTab = signal<CouponTab>('open');
//   TODO cos bardziej generycznego, moze serwis
  readonly pageRequest = signal<PageRequest>({
    page: 0,
    size: 20,
    sort: 'createdAt',
    direction: 'desc',
  });

  readonly isLoading = signal(true);

  readonly couponPage = toSignal<PageResponse<Coupon> | null>(
    combineLatest([
      toObservable(this.activeTab),
      toObservable(this.pageRequest),
    ]).pipe(
      tap(() => this.isLoading.set(true)),
      switchMap(([tab]) =>
        this.couponService
          .getMyCoupons(this.pageRequest(), this.tabToFilter(tab))
          .pipe(finalize(() => this.isLoading.set(false))),
      ),
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

  private tabToFilter(tab: CouponTab): CouponFilter {
    switch (tab) {
      case 'open':
        return { statuses: [CouponStatus.Open] };
      case 'won':
        return { statuses: [CouponStatus.Won] };
      case 'settled':
        return { statuses: [CouponStatus.Lost, CouponStatus.Won] };
    }
  }
}
