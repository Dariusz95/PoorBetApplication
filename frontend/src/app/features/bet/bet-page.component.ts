import { AsyncPipe, DatePipe, KeyValuePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbTabContainerComponent } from '@shared/ui/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/ui/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/ui/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/ui/pb-tabs/tab-config.model';
import { TabButtonTimeComponent } from '@shared/ui/tab-button-time/tab-button-time.component';
import { map } from 'rxjs';
import { CouponCardComponent } from '../coupons/components/coupon-card/coupon-card.component';
import { LiveMatchesComponent } from './components/live-matches/live-matches.component';
import { PoolCardComponent } from './components/pool-card/pool-card.component';
import { BetTabValue, LiveTabName } from './configs/bet-tab-config';
import { TimeRemainingPipe } from './pipes/time-remaining.pipe';
import { PoolRefreshService } from './services/pool-refresh.service';

@Component({
  selector: 'app-bet-page',
  standalone: true,
  imports: [
    KeyValuePipe,
    LiveMatchesComponent,
    AsyncPipe,
    PoolCardComponent,
    CouponCardComponent,
    PbTabContainerComponent,
    PbTabContentComponent,
    TimeRemainingPipe,
    TabTemplateDirective,
    TabButtonTimeComponent,
    PbIconComponent,
    DatePipe,
    TranslocoDirective,
    CouponCardComponent,
  ],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetPageComponent {
  private readonly poolRefreshService = inject(PoolRefreshService);
  private readonly destroyRef = inject(DestroyRef);

  readonly LIVE_TAB_NAME: LiveTabName = 'live';

  tabTemplateType!: TabConfig<BetTabValue>;

  futureGrouped$ = this.poolRefreshService.futureGrouped$;

  tabs$ = this.futureGrouped$.pipe(
    takeUntilDestroyed(this.destroyRef),
    map((grouped): TabConfig<BetTabValue>[] => {
      const futureTabs: TabConfig<BetTabValue>[] = Object.values(grouped).map(
        (group) => ({
          value: group.id,
          label: group.scheduledStartTime,
        }),
      );

      return [
        { value: this.LIVE_TAB_NAME, label: this.LIVE_TAB_NAME },
        ...futureTabs,
      ];
    }),
  );
}
