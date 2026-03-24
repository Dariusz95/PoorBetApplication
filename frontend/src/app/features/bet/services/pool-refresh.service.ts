import { Injectable, inject } from '@angular/core';
import {
  Observable,
  Subject,
  map,
  of,
  startWith,
  switchMap,
  tap,
  timer,
} from 'rxjs';
import { MatchService, PoolMatch } from './match.service';

export interface GroupedPools {
  [key: string]: PoolMatch[];
}

@Injectable({
  providedIn: 'root',
})
export class PoolRefreshService {
  private readonly matchService = inject(MatchService);
  private readonly refreshTrigger = new Subject<void>();
  private refreshSubscription: any;

  private readonly REFRESH_OFFSET_MS = 30 * 1000; // 30 seconds

  futureGrouped$: Observable<GroupedPools> = this.refreshTrigger.pipe(
    startWith(void 0),
    tap(() => console.log('[PoolRefresh] Refresh triggered')),
    switchMap(() => this.matchService.futureMatch()),
    tap((pools) => {
      console.log(`[PoolRefresh] Fetched ${pools.length} pools`);
      this.scheduleNextRefresh(pools);
    }),
    map((pools) => this.groupPoolsByStartTime(pools)),
  );

  constructor() {
    this.refreshTrigger
      .pipe(
        tap(() => console.log('[PoolRefresh] Refresh triggered')),
        switchMap(() => of({})),
      )
      .subscribe(() => {
        console.log('[PoolRefresh] Initial trigger');
      });
  }

  private groupPoolsByStartTime(pools: PoolMatch[]): GroupedPools {
    const grouped: GroupedPools = {};

    pools.forEach((pool) => {
      const key = pool.scheduledStartTime;
      if (!grouped[key]) {
        grouped[key] = [];
      }

      if (!grouped[key].some((existing) => existing.id === pool.id)) {
        grouped[key].push(pool);
      }
    });

    return grouped;
  }

  private scheduleNextRefresh(pools: PoolMatch[]): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }

    const nextRefreshTime = this.getNextRefreshTime(pools);

    if (nextRefreshTime === null) {
      console.log('[PoolRefresh] No upcoming pools to schedule');
      return;
    }

    const now = Date.now();
    const delay = Math.max(0, nextRefreshTime - now);

    if (delay <= 0) {
      console.log(
        '[PoolRefresh] Next refresh time is in the past, skipping timer',
      );
      return;
    }

    console.log(
      `[PoolRefresh] Scheduled next refresh in ${Math.round(delay / 1000)}s`,
    );

    this.refreshSubscription = timer(delay).subscribe(() => {
      console.log('[PoolRefresh] Timer triggered, refreshing...');
      this.refreshTrigger.next();
    });
  }

  private getNextRefreshTime(pools: PoolMatch[]): number | null {
    if (!pools.length) {
      return null;
    }

    const now = Date.now();

    const upcomingTimes = pools
      .map((p) => new Date(p.scheduledStartTime).getTime())
      .filter((time) => time > now)
      .sort((a, b) => a - b);

    if (!upcomingTimes.length) {
      return null;
    }

    const closestStart = upcomingTimes[0];
    return closestStart - this.REFRESH_OFFSET_MS;
  }

  manualRefresh(): void {
    console.log('[PoolRefresh] Manual refresh triggered');
    this.refreshTrigger.next();
  }

  destroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }

    this.refreshTrigger.complete();
  }
}
