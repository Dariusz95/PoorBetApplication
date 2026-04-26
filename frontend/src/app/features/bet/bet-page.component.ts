import { AsyncPipe, DatePipe, KeyValuePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnDestroy,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { IconComponent } from '@shared/components/icon/icon.component';
import { PbTabContainerComponent } from '@shared/components/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/components/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/components/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/components/pb-tabs/tab-config.model';
import { map } from 'rxjs';
import { BetCouponCardComponent } from '../bet-coupon-card/bet-coupon-card.component';
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
    BetCouponCardComponent,
    PbTabContainerComponent,
    PbTabContentComponent,
    TimeRemainingPipe,
    TabTemplateDirective,
    IconComponent,
    DatePipe,
    TranslocoDirective,
  ],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetPageComponent implements OnDestroy {
  ngOnDestroy(): void {
    throw new Error('Method not implemented.');
  }
  private readonly poolRefreshService = inject(PoolRefreshService);

  readonly LIVE_TAB_NAME: LiveTabName = 'live';

  tabTemplateType!: TabConfig<BetTabValue>;

  futureGrouped$ = this.poolRefreshService.futureGrouped$;

  tabs$ = this.futureGrouped$.pipe(
    takeUntilDestroyed(),
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
