import { Pipe, PipeTransform } from '@angular/core';
import { interval, map, Observable, startWith } from 'rxjs';

@Pipe({
  name: 'timeRemaining',
  standalone: true,
  pure: false,
})
export class TimeRemainingPipe implements PipeTransform {
  transform(
    scheduledStartTime: string,
    withTime: boolean = false,
  ): Observable<string> {
    return interval(1000).pipe(
      startWith(0),
      map(() => {
        const remaining = this.calculateTimeRemaining(scheduledStartTime);

        if (!withTime) {
          return remaining;
        }

        const date = new Date(scheduledStartTime);

        return `${remaining}`;
      }),
    );
  }

  private calculateTimeRemaining(scheduledStartTime: string): string {
    const now = new Date().getTime();
    const startTime = new Date(scheduledStartTime).getTime();
    const diff = startTime - now;

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
}
