import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Uuid } from '@shared/types/uuid.type';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { PoolMatch } from '../types/match.types';
import { MatchService } from './match.service';

describe('MatchService', () => {
  let service: MatchService;
  let httpMock: HttpTestingController;
  const baseUrl = '/api/match-pool';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        MatchService,
      ],
    });

    service = TestBed.inject(MatchService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('futureMatch', () => {
    it('should fetch future matches from API', () => {
      const mockMatches: PoolMatch[] = [
        {
          id: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
          status: 'SCHEDULED',
          scheduledStartTime: new Date().toISOString(),
          matches: [
            {
              matchId: '550e8400-e29b-41d4-a716-446655440002' as Uuid,
              homeTeamId: '550e8400-e29b-41d4-a716-446655440003' as Uuid,
              awayTeamId: '550e8400-e29b-41d4-a716-446655440004' as Uuid,
              odds: {
                id: '550e8400-e29b-41d4-a716-446655440005' as Uuid,
                homeWin: 1.5,
                draw: 3.0,
                awayWin: 2.5,
              },
            },
          ],
        },
        {
          id: '550e8400-e29b-41d4-a716-446655440001' as Uuid,
          status: 'SCHEDULED',
          scheduledStartTime: new Date().toISOString(),
          matches: [
            {
              matchId: '550e8400-e29b-41d4-a716-446655440006' as Uuid,
              homeTeamId: '550e8400-e29b-41d4-a716-446655440007' as Uuid,
              awayTeamId: '550e8400-e29b-41d4-a716-446655440008' as Uuid,
              odds: {
                id: '550e8400-e29b-41d4-a716-446655440009' as Uuid,
                homeWin: 1.8,
                draw: 2.8,
                awayWin: 2.2,
              },
            },
          ],
        },
      ];

      const promise = firstValueFrom(service.futureMatch());

      const req = httpMock.expectOne(`${baseUrl}/future`);
      expect(req.request.method).toBe('GET');
      req.flush(mockMatches);

      return promise.then((matches) => {
        expect(matches.length).toBe(2);
        expect(matches).toEqual(mockMatches);
      });
    });

    it('should handle empty list of future matches', () => {
      const promise = firstValueFrom(service.futureMatch());

      const req = httpMock.expectOne(`${baseUrl}/future`);
      req.flush([]);

      return promise.then((matches) => {
        expect(matches).toEqual([]);
        expect(matches.length).toBe(0);
      });
    });

    it('should handle API error when fetching future matches', () => {
      const promise = firstValueFrom(service.futureMatch()).catch((error) => {
        expect(error.status).toBe(500);
      });

      const req = httpMock.expectOne(`${baseUrl}/future`);
      req.flush(
        { message: 'Internal server error' },
        { status: 500, statusText: 'Internal Server Error' },
      );

      return promise;
    });
  });
});
