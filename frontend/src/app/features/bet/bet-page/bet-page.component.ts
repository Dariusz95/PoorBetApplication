import { AsyncPipe, DatePipe, KeyValuePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnDestroy,
} from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { IconComponent } from '@shared/components/icon/icon.component';
import { PbTabContainerComponent } from '@shared/components/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/components/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/components/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/components/pb-tabs/tab-config.model';
import { logStream } from '@shared/utils/log-stream.util';
import { map } from 'rxjs';
import { BetCouponCardComponent } from '../components/bet-coupon-card/bet-coupon-card.component';
import { LiveMatchesComponent } from '../components/live-matches/live-matches.component';
import { PoolCardComponent } from '../components/pool-card/pool-card.component';
import { TimeRemainingPipe } from '../pipes/time-remaining.pipe';
import { PoolRefreshService } from '../services/pool-refresh.service';
import { BetTabValue, LiveTabName } from './bet-tab-config';

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
  private readonly poolRefreshService = inject(PoolRefreshService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly LIVE_TAB_NAME: LiveTabName = 'live';

  tabTemplateType!: TabConfig<BetTabValue>;

  futureGrouped$ = this.poolRefreshService.futureGrouped$;

  tabs$ = this.futureGrouped$.pipe(
    logStream('XX tabs$'),
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

  ngOnDestroy(): void {
    this.poolRefreshService.destroy();
  }
}
