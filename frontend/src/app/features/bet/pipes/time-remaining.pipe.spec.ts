import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { TimeRemainingPipe } from './time-remaining.pipe';

const NOW = new Date('2026-01-01T00:00:00.000Z').getTime();

function futureIso(msFromNow: number): string {
  return new Date(NOW + msFromNow).toISOString();
}

function firstEmission(
  pipe: TimeRemainingPipe,
  target: string,
  withTime?: boolean,
): string {
  let latest: string | undefined;

  const subscription = pipe.transform(target, withTime).subscribe((value) => {
    latest = value;
  });
  subscription.unsubscribe();

  return latest!;
}

describe('TimeRemainingPipe', () => {
  let pipe: TimeRemainingPipe;

  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(NOW);
    pipe = new TimeRemainingPipe();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should format remaining time in days and hours when more than a day away', () => {
    const value = firstEmission(
      pipe,
      futureIso(2 * 24 * 60 * 60 * 1000 + 3 * 60 * 60 * 1000),
    );

    expect(value).toBe('2d 3h');
  });

  it('should format remaining time in hours and minutes when less than a day away', () => {
    const value = firstEmission(pipe, futureIso(5 * 60 * 60 * 1000 + 30 * 60 * 1000));

    expect(value).toBe('5h 30m');
  });

  it('should format remaining time in minutes and seconds when less than an hour away', () => {
    const value = firstEmission(pipe, futureIso(10 * 60 * 1000 + 15 * 1000));

    expect(value).toBe('10m 15s');
  });

  it('should format remaining time in seconds when less than a minute away', () => {
    const value = firstEmission(pipe, futureIso(45 * 1000));

    expect(value).toBe('45s');
  });

  it('should show 0s for a match that has already started', () => {
    const value = firstEmission(pipe, futureIso(-60 * 1000));

    expect(value).toBe('0s');
  });

  it('should emit the same format regardless of the withTime flag', () => {
    const target = futureIso(10 * 60 * 1000);

    const withoutTime = firstEmission(pipe, target, false);
    const withTime = firstEmission(pipe, target, true);

    expect(withTime).toBe(withoutTime);
  });
});
