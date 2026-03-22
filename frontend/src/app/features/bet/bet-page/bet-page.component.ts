import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { interval, startWith, Subject, switchMap, takeUntil } from 'rxjs';
import { LiveMatchComponent } from '../components/live-match-card/live-match.component';
import { PoolCardComponent } from '../components/pool-card/pool-card.component';
import { LiveMatchEvent, MatchService } from '../services/match.service';

@Component({
  selector: 'app-bet-page',
  standalone: true,
  imports: [KeyValuePipe, LiveMatchComponent, AsyncPipe, PoolCardComponent],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
})
export class BetPageComponent implements OnInit, OnDestroy {
  private readonly matchService = inject(MatchService);
  private readonly destroy$ = new Subject<void>();
  private readonly refreshTrigger = new Subject<void>();

  liveMatches: Record<string, LiveMatchEvent> = {};

  future$ = this.refreshTrigger.pipe(
    startWith(void 0),
    switchMap(() => this.matchService.futureMatch())
  );

  updateLiveMatch(event: LiveMatchEvent): void {
    this.liveMatches[event.id] = event;
  }

  refreshFutureMatches(): void {
    this.refreshTrigger.next();
  }

  calculateTimeRemaining(scheduledStartTime: string, isFirst: boolean): string {
    const now = new Date().getTime();
    const startTime = new Date(scheduledStartTime).getTime();
    const diff = startTime - now;

    if (diff <= 0) {
      if (isFirst) {
        this.refreshFutureMatches();
      }

      return 'Rozpoczyna się';
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    if (days > 0) {
      return `${days}d ${hours}h`;
    }

    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }

    if (minutes > 0) {
      return `${minutes}m ${seconds}s`;
    }

    return `${seconds}s`;
  }

  ngOnInit(): void {
    this.matchService
      .streamMatch()
      .pipe(takeUntil(this.destroy$))
      .subscribe((match) => {
        if (match.id) {
          this.updateLiveMatch(match);
        }
      });

    interval(1000)
      .pipe(takeUntil(this.destroy$))
      .subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
