import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbTabContainerComponent } from '@shared/ui/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/ui/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/ui/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/ui/pb-tabs/tab-config.model';
import { TabButtonTimeComponent } from '@shared/ui/tab-button-time/tab-button-time.component';
import { map } from 'rxjs';
import { PoolCardComponent } from '../pool-card/pool-card.component';
import { LiveMatchesComponent } from '../live-matches/live-matches.component';
import { BetTabValue, LiveTabName } from '../../types/bet-tab.types';
import { PoolRefreshService } from '../../services/pool-refresh.service';

@Component({
  selector: 'app-bet-tabs',
  imports: [
    KeyValuePipe,
    AsyncPipe,
    LiveMatchesComponent,
    PoolCardComponent,
    PbTabContainerComponent,
    PbTabContentComponent,
    TabTemplateDirective,
    TabButtonTimeComponent,
    TranslocoDirective,
  ],
  templateUrl: './bet-tabs.component.html',
})
export class BetTabsComponent {
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
