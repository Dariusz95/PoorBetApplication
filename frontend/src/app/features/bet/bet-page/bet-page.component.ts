import { animate, style, transition, trigger } from '@angular/animations';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import {
  afterRenderEffect,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map, tap } from 'rxjs';
import { logStream } from '../../../shared/utils/log-stream';
import { BetCouponCardComponent } from '../components/bet-coupon-card/bet-coupon-card.component';
import { LiveMatchComponent } from '../components/live-match-card/live-match.component';
import { PoolCardComponent } from '../components/pool-card/pool-card.component';
import { TimeRemainingPipe } from '../pipes/time-remaining.pipe';
import { LiveMatchEvent, MatchService } from '../services/match.service';
import { PoolRefreshService } from '../services/pool-refresh.service';

@Component({
  selector: 'app-bet-page',
  standalone: true,
  imports: [
    KeyValuePipe,
    LiveMatchComponent,
    AsyncPipe,
    PoolCardComponent,
    BetCouponCardComponent,
    TimeRemainingPipe,
  ],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
  animations: [
    trigger('fadeInOut', [
      transition('* <=> *', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate(
          '300ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' }),
        ),
      ]),
    ]),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BetPageComponent implements OnInit, OnDestroy {
  private readonly matchService = inject(MatchService);
  private readonly poolRefreshService = inject(PoolRefreshService);
  private readonly destroyRef = inject(DestroyRef);
  // @ViewChild('tabsContainer') tabsContainer!: ElementRef<HTMLElement>;
  tabsContainer = viewChild<ElementRef>('tabsContainer');

  liveMatches: Record<string, LiveMatchEvent> = {};
  selectedTab = signal('Live');
  isHeaderScrolled: boolean = false;

  indicatorStyle = signal({ left: '0px', width: '0px' });

  futureGrouped$ = this.poolRefreshService.futureGrouped$;

  cdr = inject(ChangeDetectorRef);
  tabs$ = this.futureGrouped$.pipe(
    logStream('tabs$'),
    map((grouped) => ['Live', ...Object.keys(grouped)]),
    tap(() => {
      this.selectTab('Live');
      this.cdr.detectChanges();
    }),
  );

  constructor() {
    afterRenderEffect(() => {
      const tab = this.selectedTab();

      this.updateIndicatorPosition();
    });
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    this.isHeaderScrolled = target.scrollTop > 0;
  }

  updateLiveMatch(event: LiveMatchEvent): void {
    this.liveMatches[event.id] = event;
  }

  selectTab(tab: string): void {
    this.selectedTab.set(tab);
  }

  private updateIndicatorPosition(): void {
    if (!this.tabsContainer()) {
      return;
    }

    const activeButton = this.tabsContainer()!.nativeElement.querySelector(
      '.tab-button.active',
    ) as HTMLElement | null;
    console.log(activeButton);
    if (!activeButton) {
      return;
    }

    const left = activeButton.offsetLeft;
    const width = activeButton.offsetWidth;

    this.indicatorStyle.set({ left: `${left}px`, width: `${width}px` });
  }

  ngOnInit(): void {
    this.listenToLiveMatches();
    this.selectTab('Live');
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
