import { DecimalPipe } from '@angular/common';
import {
  Component,
  computed,
  inject,
  signal,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { CouponService } from '@features/coupons/services/coupon.service';
import { RankingCoupon } from '@features/coupons/types/ranking-coupon';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbImageComponent } from '@shared/ui/pb-image/pb-image.component';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';
import { tableColumns } from '@shared/ui/pb-table/pb-table-columns.builder';
import { PbTableComponent } from '@shared/ui/pb-table/pb-table.component';
import { PbCellContext, PbColumnDef } from '@shared/ui/pb-table/pb-table.types';
import { PbTabsComponent } from '@shared/ui/pb-tabs/pb-tabs/pb-tabs.component';
import { TabConfig } from '@shared/ui/pb-tabs/tab-config.model';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { RankingEntry } from '../types/ranking-entry';

type RankingMetric = 'odds' | 'payout';

const PAGE_SIZE = 10;

@Component({
  selector: 'app-ranking-page',
  imports: [
    DecimalPipe,
    TranslocoPipe,
    PbCardComponent,
    PbTabsComponent,
    PbTableComponent,
    PbSpinnerComponent,
    PbIconComponent,
    PbImageComponent,
  ],
  templateUrl: './ranking-page.component.html',
  styleUrl: './ranking-page.component.scss',
})
export class RankingPageComponent {
  private readonly couponService = inject(CouponService);
  private readonly transloco = inject(TranslocoService);
  private readonly dialogService = inject(DialogService);

  private readonly lang = toSignal(this.transloco.langChanges$);

  private readonly rankCellTpl =
    viewChild.required<TemplateRef<PbCellContext<RankingEntry, number>>>(
      'rankCell',
    );
  private readonly playerCellTpl =
    viewChild.required<TemplateRef<PbCellContext<RankingEntry, string>>>(
      'playerCell',
    );
  private readonly oddsCellTpl =
    viewChild.required<TemplateRef<PbCellContext<RankingEntry, number>>>(
      'oddsCell',
    );
  private readonly payoutCellTpl =
    viewChild.required<TemplateRef<PbCellContext<RankingEntry, number>>>(
      'payoutCell',
    );
  private readonly previewCellTpl =
    viewChild.required<TemplateRef<PbCellContext<RankingEntry, string>>>(
      'previewCell',
    );

  readonly metricTabs = computed<TabConfig<RankingMetric>[]>(() => {
    this.lang();
    return [
      { value: 'odds', label: this.transloco.translate('ranking.metricOdds') },
      {
        value: 'payout',
        label: this.transloco.translate('ranking.metricPayout'),
      },
    ];
  });

  activeMetric = signal<RankingMetric>('odds');

  readonly isLoading = signal(true);
  readonly hasError = signal(false);

  private readonly rankingCoupons = toSignal(
    toObservable(this.activeMetric).pipe(
      tap(() => {
        this.isLoading.set(true);
        this.hasError.set(false);
      }),
      switchMap((metric) =>
        (metric === 'odds'
          ? this.couponService.getHighestTotalOdds()
          : this.couponService.getHighestPayout()
        ).pipe(
          map((page) => page.content),
          tap(() => this.isLoading.set(false)),
          catchError(() => {
            this.isLoading.set(false);
            this.hasError.set(true);
            return of<RankingCoupon[]>([]);
          }),
        ),
      ),
    ),
    { initialValue: [] as RankingCoupon[] },
  );

  rows = computed<RankingEntry[]>(() =>
    this.rankingCoupons().map((coupon, index) => ({
      id: coupon.couponId,
      rank: index + 1,
      email: coupon.email,
      totalOdds: coupon.totalOdds,
      potentialPayout: coupon.potentialPayout,
      createdAt: coupon.createdAt,
    })),
  );

  columns = computed<PbColumnDef<RankingEntry>[]>(() => {
    this.lang();
    const t = (key: string) =>
      this.transloco.translate(`ranking.columns.${key}`);

    const builder = tableColumns<RankingEntry>()
      .add('rank', {
        header: t('rank'),
        width: '56px',
        align: 'center',
        cellTemplate: this.rankCellTpl(),
      })
      .add('email', {
        header: t('player'),
        cellTemplate: this.playerCellTpl(),
      });

    if (this.activeMetric() === 'odds') {
      builder
        .add('totalOdds', {
          header: t('odds'),
          align: 'right',
          cellTemplate: this.oddsCellTpl(),
        })
        .add('potentialPayout', {
          header: t('payout'),
          align: 'right',
          cellTemplate: this.payoutCellTpl(),
        });
    } else {
      builder
        .add('potentialPayout', {
          header: t('payout'),
          align: 'right',
          cellTemplate: this.payoutCellTpl(),
        })
        .add('totalOdds', {
          header: t('odds'),
          align: 'right',
          formatter: (value) => `${value.toFixed(2)}x`,
        });
    }

    builder
      .add('createdAt', {
        header: t('date'),
        align: 'right',
        formatter: (value) => this.formatDate(value),
      })
      .add('id', {
        header: t('preview'),
        align: 'center',
        width: '64px',
        cellTemplate: this.previewCellTpl(),
      });

    return builder.build();
  });

  onMetricChange(metric: RankingMetric): void {
    this.activeMetric.set(metric);
  }

  openPreview(couponId: string): void {
    this.couponService
      .getPublicCouponDetails(couponId)
      .subscribe((details) => this.dialogService.openCouponDialog(details));
  }

  private formatDate(value: string): string {
    return new Date(value).toLocaleDateString('pl-PL');
  }
}
