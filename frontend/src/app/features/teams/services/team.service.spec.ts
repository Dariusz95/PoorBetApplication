import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ShortTeamInfo } from '@features/bet/types/match.types';
import { Uuid } from '@shared/types/uuid.type';
import { firstValueFrom } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { CreateTeamRequest } from '../types/create-team-request';
import { TeamService } from './team.service';

describe('TeamService', () => {
  let service: TeamService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
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
    const mockTeamId: Uuid = '550e8400-e29b-41d4-a716-446655440000' as Uuid;
    const baseUrl = '/api/teams/public';

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

      httpMock.expectNone(`${baseUrl}/${mockTeamId}`);
    });

    it('should cache different teams separately', () => {
      const mockTeamId2: Uuid = '550e8400-e29b-41d4-a716-446655440001' as Uuid;

      const mockTeam1: ShortTeamInfo = { id: mockTeamId, name: 'Test Team 1' };
      const mockTeam2: ShortTeamInfo = { id: mockTeamId2, name: 'Test Team 2' };

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

    it('should throw an error when team ID is not provided', () => {
      expect(() => service.getDetails('' as Uuid)).toThrow(
        new Error('Team ID is required'),
      );
    });

    it('should refetch after removeFromCache', () => {
      const mockTeam: ShortTeamInfo = { id: mockTeamId, name: 'Test Team' };

      service.getDetails(mockTeamId).subscribe();
      httpMock.expectOne(`${baseUrl}/${mockTeamId}`).flush(mockTeam);

      service.removeFromCache(mockTeamId);
      service.getDetails(mockTeamId).subscribe();

      const req = httpMock.expectOne(`${baseUrl}/${mockTeamId}`);
      req.flush(mockTeam);
    });
  });

  describe('create', () => {
    it('should POST the request to /api/teams/public', () => {
      const request: CreateTeamRequest = { name: 'Lechia Gdańsk' };

      service.create(request).subscribe();

      const req = httpMock.expectOne('/api/teams/public');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);

      req.flush({
        id: 'team-1',
        name: request.name,
        attackPower: 40,
        defencePower: 40,
        logo: null,
      });
    });
  });

  describe('updateName', () => {
    it('should PATCH the request to /api/teams/public', () => {
      const request: CreateTeamRequest = { name: 'Lechia Gdańsk' };

      service.updateName(request).subscribe();

      const req = httpMock.expectOne('/api/teams/public');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(request);

      req.flush({
        id: 'team-1',
        name: request.name,
        attackPower: 40,
        defencePower: 40,
        logo: null,
      });
    });
  });

  describe('getMyTeam', () => {
    it('should GET the current team from /api/teams/public/my-team', () => {
      let result: unknown;
      service.getMyTeam().subscribe((team) => (result = team));

      const req = httpMock.expectOne('/api/teams/public/my-team');
      expect(req.request.method).toBe('GET');
      req.flush({
        id: 'team-1',
        name: 'Lechia Gdańsk',
        attackPower: 40,
        defencePower: 40,
        logo: '/images/team-1.png',
      });

      expect(result).toEqual({
        id: 'team-1',
        name: 'Lechia Gdańsk',
        attackPower: 40,
        defencePower: 40,
        logo: '/images/team-1.png',
      });
    });

    it('should resolve to null when the request fails (e.g. user has no team yet)', () => {
      let result: unknown;
      service.getMyTeam().subscribe((team) => (result = team));

      const req = httpMock.expectOne('/api/teams/public/my-team');
      req.flush('error', { status: 500, statusText: 'Internal Server Error' });

      expect(result).toBeNull();
    });
  });

  describe('uploadLogo', () => {
    it('should PATCH the file as multipart form data to /api/teams/public/me/logo', () => {
      const file = new File(['logo'], 'logo.png', { type: 'image/png' });

      service.uploadLogo(file).subscribe();

      const req = httpMock.expectOne('/api/teams/public/me/logo');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body instanceof FormData).toBe(true);
      expect((req.request.body as FormData).get('file')).toBe(file);

      req.flush({ id: 'team-1', name: 'Lechia Gdańsk', img: '/images/team-1.png' });
    });

    it('should map the backend `img` field to `logo`', async () => {
      const file = new File(['logo'], 'logo.png', { type: 'image/png' });

      const promise = firstValueFrom(service.uploadLogo(file));

      const req = httpMock.expectOne('/api/teams/public/me/logo');
      req.flush({ id: 'team-1', name: 'Lechia Gdańsk', img: '/images/team-1.png' });

      const result = await promise;

      expect(result).toEqual({
        id: 'team-1',
        name: 'Lechia Gdańsk',
        logo: '/images/team-1.png',
      });
    });
  });
});
