import { AsyncPipe, DatePipe, KeyValuePipe, SlicePipe } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { interval, startWith, Subject, switchMap, takeUntil } from 'rxjs';
import { LiveMatchComponent } from '../live-match/live-match/live-match.component';
import { LiveMatchEvent, MatchService } from '../services/match.service';

interface PoolWithTimer {
  id: string;
  status: string;
  scheduledStartTime: string;
  matches: any[];
  timeRemaining: string;
}

@Component({
  selector: 'app-bet-page',
  imports: [KeyValuePipe, LiveMatchComponent, AsyncPipe, DatePipe, SlicePipe],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
})
export class BetPageComponent implements OnInit, OnDestroy {
  private readonly matchService = inject(MatchService);
  private destroy$ = new Subject<void>();
  private refreshTrigger = new Subject<void>();

  liveMatches: Record<string, LiveMatchEvent> = {};

  updateLiveMatch(event: LiveMatchEvent) {
    console.log('xx liveMatches', this.liveMatches);
    this.liveMatches[event.id] = event;
  }

  future$ = this.refreshTrigger.pipe(
    startWith(void 0),
    switchMap(() => this.matchService.futureMatch())
  );

  refreshFutureMatches() {
    this.refreshTrigger.next();
  }

  calculateTimeRemaining(scheduledStartTime: string, first: boolean): string {
    const now = new Date().getTime();
    const startTime = new Date(scheduledStartTime).getTime();
    const diff = startTime - now;

    if (diff <= 0) {
      if (first) this.refreshFutureMatches();

      return 'Rozpoczyna się';
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    if (days > 0) {
      return `${days}d ${hours}h`;
    } else if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else if (minutes > 0) {
      return `${minutes}m ${seconds}s`;
    } else {
      return `${seconds}s`;
    }
  }

  ngOnInit() {
    // Stream live matches
    this.matchService.streamMatch().subscribe((match) => {
      console.log('Live Matches:', match);
      if (match.id) {
        this.updateLiveMatch(match);
      }
    });

    // Update timers every second
    interval(1000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        // Timer będzie aktualizowany automatycznie w template
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
