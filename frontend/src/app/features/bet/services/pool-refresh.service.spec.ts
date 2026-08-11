import { TestBed } from '@angular/core/testing';
import { Uuid } from '@shared/types/uuid.type';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PoolMatch } from '../types/match.types';
import { LiveMatchService } from './live-match.service';
import { MatchService } from './match.service';
import { PoolRefreshService } from './pool-refresh.service';

describe('PoolRefreshService', () => {
  let service: PoolRefreshService;
  let matchService: { futureMatch: ReturnType<typeof vi.fn> };

  const pools: PoolMatch[] = [
    {
      id: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      status: 'SCHEDULED',
      scheduledStartTime: new Date(Date.now() + 60_000).toISOString(),
      matches: [],
    },
  ];

  beforeEach(() => {
    matchService = { futureMatch: vi.fn().mockReturnValue(of(pools)) };

    TestBed.configureTestingModule({
      providers: [
        PoolRefreshService,
        { provide: MatchService, useValue: matchService },
        {
          provide: LiveMatchService,
          useValue: { cleanupEndedMatches: vi.fn() },
        },
      ],
    });

    service = TestBed.inject(PoolRefreshService);
  });

  it('should share a single futureMatch() HTTP call across multiple subscribers', () => {
    service.futureGrouped$.subscribe();
    service.futureGrouped$.subscribe();
    service.futureGrouped$.subscribe();

    expect(matchService.futureMatch).toHaveBeenCalledTimes(1);
  });

  it('should replay the latest grouped pools to a late subscriber', () => {
    service.futureGrouped$.subscribe();

    let result: unknown;
    service.futureGrouped$.subscribe((grouped) => (result = grouped));

    expect(result).toEqual({
      '550e8400-e29b-41d4-a716-446655440000': pools[0],
    });
    expect(matchService.futureMatch).toHaveBeenCalledTimes(1);
  });
});
