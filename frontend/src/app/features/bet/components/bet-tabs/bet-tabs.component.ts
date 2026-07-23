import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective, TranslocoPipe } from '@jsverse/transloco';
import { DialogService } from '@shared/services/dialog.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbSkeletonComponent } from '@shared/ui/pb-skeleton/pb-skeleton.component';
import { PbTabContainerComponent } from '@shared/ui/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/ui/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/ui/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/ui/pb-tabs/tab-config.model';
import { TabButtonTimeComponent } from '@shared/ui/tab-button-time/tab-button-time.component';
import { concatMap, delay, map, of } from 'rxjs';
import { PoolRefreshService } from '../../services/pool-refresh.service';
import { BetTabValue, LiveTabName } from '../../types/bet-tab.types';
import { LiveMatchSkeletonComponent } from '../live-match-skeleton/live-match-skeleton.component';
import { LiveMatchesComponent } from '../live-matches/live-matches.component';
import { PoolCardComponent } from '../pool-card/pool-card.component';

@Component({
  selector: 'app-bet-tabs',
  imports: [
    KeyValuePipe,
    AsyncPipe,
    LiveMatchesComponent,
    LiveMatchSkeletonComponent,
    PbSkeletonComponent,
    PoolCardComponent,
    PbTabContainerComponent,
    PbTabContentComponent,
    TabTemplateDirective,
    TabButtonTimeComponent,
    TranslocoDirective,
    TranslocoPipe,
    PbButtonComponent,
    PbIconComponent,
  ],
  templateUrl: './bet-tabs.component.html',
  styleUrl: './bet-tabs.component.scss',
})
export class BetTabsComponent {
  private readonly poolRefreshService = inject(PoolRefreshService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialogService = inject(DialogService);

  readonly LIVE_TAB_NAME: LiveTabName = 'live';
  readonly skeletonFutureTabPlaceholders = [0, 1, 2];
  readonly skeletonCardPlaceholders = [0, 1];

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
    concatMap((tabs, index) => (index === 0 ? of(tabs).pipe(delay(750)) : of(tabs))),
  );

  openHelp(): void {
    this.dialogService.openBetTabsHelpDialog();
  }
}
