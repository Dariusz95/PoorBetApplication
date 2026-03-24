import { AsyncPipe, KeyValuePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PbTabContainerComponent } from '@shared/components/pb-tabs/pb-tab-container/pb-tab-container.component';
import { PbTabContentComponent } from '@shared/components/pb-tabs/pb-tab-content/pb-tab-content.component';
import { TabTemplateDirective } from '@shared/components/pb-tabs/pb-tab-template.directive';
import { TabConfig } from '@shared/components/pb-tabs/tab-config.model';
import { logStream } from '@shared/utils/log-stream';
import { map } from 'rxjs';
import { BetCouponCardComponent } from '../components/bet-coupon-card/bet-coupon-card.component';
import { LiveMatchComponent } from '../components/live-match-card/live-match.component';
import { PoolCardComponent } from '../components/pool-card/pool-card.component';
import { TimeRemainingPipe } from '../pipes/time-remaining.pipe';
import { LiveMatchEvent, MatchService } from '../services/match.service';
import { PoolRefreshService } from '../services/pool-refresh.service';
import { BetTabValue, LiveTabName } from './bet-tab-config';

@Component({
  selector: 'app-bet-page',
  standalone: true,
  imports: [
    KeyValuePipe,
    LiveMatchComponent,
    AsyncPipe,
    PoolCardComponent,
    BetCouponCardComponent,
    PbTabContainerComponent,
    PbTabContentComponent,
    TimeRemainingPipe,
    TabTemplateDirective,
  ],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetPageComponent implements OnInit, OnDestroy {
  private readonly matchService = inject(MatchService);
  private readonly poolRefreshService = inject(PoolRefreshService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly LIVE_TAB_NAME: LiveTabName = 'live';

  liveMatches: Record<string, LiveMatchEvent> = {};

  tabTemplateType!: TabConfig<BetTabValue>;

  futureGrouped$ = this.poolRefreshService.futureGrouped$;

  tabs$ = this.futureGrouped$.pipe(
    logStream('tabs$'),
    map((grouped): TabConfig<BetTabValue>[] => {
      const dates = Object.keys(grouped);
      return [
        { value: this.LIVE_TAB_NAME, label: this.LIVE_TAB_NAME },
        ...dates.map((date) => ({
          value: date,
          label: date,
        })),
      ];
    }),
  );

  updateLiveMatch(event: LiveMatchEvent): void {
    this.liveMatches[event.id] = event;
  }

  ngOnInit(): void {
    this.listenToLiveMatches();
  }

  ngOnDestroy(): void {
    this.poolRefreshService.destroy();
  }

  private listenToLiveMatches(): void {
    this.matchService
      .streamMatch()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((match) => {
        if (match.id) {
          this.updateLiveMatch(match);
        }
      });
  }
}
