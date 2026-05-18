import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Uuid } from '@shared/types/uuid.type';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { ShortTeamInfo } from '../types/match.types';
import { TeamService } from './team.service';

describe('TeamService', () => {
  let service: TeamService;
  let httpMock: HttpTestingController;
  const mockTeamId: Uuid = '550e8400-e29b-41d4-a716-446655440000' as Uuid;
  const baseUrl = '/api/teams/public';

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), TeamService],
    });

    service = TestBed.inject(TeamService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getDetails', () => {
    it('should fetch team details from API', async () => {
      const mockTeam: ShortTeamInfo = {
        id: mockTeamId,
        name: 'Test Team',
      };

      const promise = firstValueFrom(service.getDetails(mockTeamId));

      const req = httpMock.expectOne(`${baseUrl}/${mockTeamId}`);

      expect(req.request.method).toBe('GET');
      req.flush(mockTeam);

      const result = await promise;

      expect(result).toEqual(mockTeam);
      expect(result.name).toBe('Test Team');
    });

    it('should cache team details after first fetch', async () => {
      const mockTeam: ShortTeamInfo = {
        id: mockTeamId,
        name: 'Test Team',
      };

      service.getDetails(mockTeamId).subscribe();
      const req1 = httpMock.expectOne(`${baseUrl}/${mockTeamId}`);
      req1.flush(mockTeam);

      service.getDetails(mockTeamId).subscribe((team) => {
        expect(team).toEqual(mockTeam);
      });

      httpMock.expectNone(`/api/teams/public/${mockTeamId}`);
    });

    it('should throw error when team ID is not provided', () => {
      expect(() => service.getDetails('' as any)).toThrow(
        new Error('Team ID is required'),
      );
    });

    it('should throw error when team ID is null', () => {
      expect(() => service.getDetails('' as any)).toThrow(
        new Error('Team ID is required'),
      );
    });

    it('should throw error when team ID is undefined', () => {
      expect(() => service.getDetails('' as any)).toThrow(
        new Error('Team ID is required'),
      );
    });

    it('should cache different teams separately', (done) => {
      const mockTeamId2: Uuid = '550e8400-e29b-41d4-a716-446655440001' as Uuid;

      const mockTeam1: ShortTeamInfo = {
        id: mockTeamId,
        name: 'Test Team 1',
      };

      const mockTeam2: ShortTeamInfo = {
        id: mockTeamId2,
        name: 'Test Team 2',
      };

      service.getDetails(mockTeamId).subscribe();
      const req1 = httpMock.expectOne(`${baseUrl}/${mockTeamId}`);
      req1.flush(mockTeam1);

      service.getDetails(mockTeamId2).subscribe();
      const req2 = httpMock.expectOne(`${baseUrl}/${mockTeamId2}`);
      req2.flush(mockTeam2);

      service.getDetails(mockTeamId).subscribe((team) => {
        expect(team).toEqual(mockTeam1);
      });

      httpMock.expectNone(`${baseUrl}/${mockTeamId}`);
    });
  });
});
