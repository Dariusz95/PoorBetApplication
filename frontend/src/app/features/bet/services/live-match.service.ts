import { DestroyRef, Injectable, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BehaviorSubject } from 'rxjs';
import { LiveMatchEvent, MatchEventType } from '../types/match.types';
import { MatchService } from './match.service';

@Injectable({
  providedIn: 'root',
})
export class LiveMatchService {
  private readonly matchService = inject(MatchService);
  private readonly destroyRef = inject(DestroyRef);

  private liveMatchesSubject = new BehaviorSubject<
    Record<string, LiveMatchEvent>
  >({});

  liveMatches$ = this.liveMatchesSubject.asObservable();

  constructor() {
    this.listenToLiveMatches();
  }

  private listenToLiveMatches(): void {
    this.matchService
      .streamMatch()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (
          [MatchEventType.Heartbeat, MatchEventType.MatchPoolFinished].includes(
            event.eventType,
          )
        )
          return;

        this.updateLiveMatch(event);
      });
  }

  private updateLiveMatch(event: LiveMatchEvent): void {
    const current = this.liveMatchesSubject.value;

    this.liveMatchesSubject.next({
      ...current,
      [event.id]: event,
    });
  }

  cleanupEndedMatches(): void {
    const current = this.liveMatchesSubject.value;
    const updated = { ...current };
    let cleaned = false;

    Object.keys(updated).forEach((key) => {
      const shouldClean = [
        MatchEventType.MatchEnded,
        MatchEventType.MatchPoolFinished,
      ].includes(updated[key].eventType);

      if (shouldClean) {
        delete updated[key];
        cleaned = true;
      }
    });

    if (cleaned) {
      this.liveMatchesSubject.next(updated);
      console.log('[LiveMatchService] Cleaned up ended matches');
    }
  }
}
