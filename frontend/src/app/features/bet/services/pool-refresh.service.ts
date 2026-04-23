import { Injectable, inject } from '@angular/core';
import { Uuid } from '@shared/types/uuid.type';
import { toUuid } from '@shared/utils/uuid.util';
import {
  BehaviorSubject,
  Observable,
  Subject,
  Subscription,
  map,
  startWith,
  switchMap,
  tap,
  timer,
} from 'rxjs';
import { PoolMatch } from '../types/match.types';
import { LiveMatchService } from './live-match.service';
import { MatchService } from './match.service';

export interface GroupedPools {
  [key: Uuid]: PoolMatch;
}

@Injectable({
  providedIn: 'root',
})
export class PoolRefreshService {
  private readonly matchService = inject(MatchService);
  private readonly liveMatchService = inject(LiveMatchService);
  private readonly refreshTrigger = new BehaviorSubject<void>(undefined);
  private refreshSubscription: Subscription | null = null;

  futureGrouped$: Observable<GroupedPools> = this.refreshTrigger.pipe(
    switchMap(() => this.matchService.futureMatch()),
    map((pools) => this.sortPoolMatchesByDateAsc(pools)),
    tap((pools) => this.scheduleNextRefreshAndRearange(pools)),
    map((pools) => this.groupPoolsById(pools)),
  );

private sortPoolMatchesByDateAsc(pools: PoolMatch[]): PoolMatch[] {
  return [...pools].sort((a, b) => {
    const dateA = new Date(a.scheduledStartTime).getTime();
    const dateB = new Date(b.scheduledStartTime).getTime();

    return dateA - dateB;
  });
}
  private groupPoolsById(pools: PoolMatch[]): GroupedPools {
    const grouped: GroupedPools = {};

    pools.forEach((pool) => {
      const key = toUuid(pool.id);

      grouped[key] = pool;
    });

    return grouped;
  }

  private scheduleNextRefreshAndRearange(pools: PoolMatch[]): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }

    const nextRefreshTime = this.getNextRefreshTime(pools);

    if (nextRefreshTime === null) return;

    const now = Date.now();
    const delay = Math.max(0, nextRefreshTime - now);

    if (delay <= 0) return;

    this.refreshSubscription = timer(delay).subscribe(() => {
      this.liveMatchService.cleanupEndedMatches();
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

    if (!upcomingTimes.length) return null;

    return upcomingTimes[0];
  }

  manualRefresh(): void {
    this.refreshTrigger.next();
  }

  destroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }

    this.refreshTrigger.complete();
  }
}
