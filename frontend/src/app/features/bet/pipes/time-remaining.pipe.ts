import { Pipe, PipeTransform } from '@angular/core';
import { interval, map, Observable, startWith } from 'rxjs';

@Pipe({
  name: 'timeRemaining',
  standalone: true,
  pure: false,
})
export class TimeRemainingPipe implements PipeTransform {
  /**
   * @param scheduledStartTime ISO string of pool start time
   * @param withTime If true, include formatted time (hh:mm) + timer, else just timer
   */
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

        // Include time in format: "14:44 - 1h 20m"
        const date = new Date(scheduledStartTime);
        const timeStr = date.toLocaleTimeString('pl-PL', {
          hour: '2-digit',
          minute: '2-digit',
        });

        return `${timeStr} - ${remaining}`;
      }),
    );
  }

  private calculateTimeRemaining(scheduledStartTime: string): string {
    const now = new Date().getTime();
    const startTime = new Date(scheduledStartTime).getTime();
    const diff = startTime - now;

    if (diff <= 0) {
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
}
