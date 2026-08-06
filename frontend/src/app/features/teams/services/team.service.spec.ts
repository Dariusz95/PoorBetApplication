import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
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

  describe('create', () => {
    it('should POST the request to /api/teams/public', () => {
      const request: CreateTeamRequest = { name: 'Lechia Gdańsk' };

      service.create(request).subscribe();

      const req = httpMock.expectOne('/api/teams/public');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);

      req.flush({});
    });
  });

  describe('updateName', () => {
    it('should PATCH the request to /api/teams/public/me', () => {
      const request: CreateTeamRequest = { name: 'Lechia Gdańsk' };

      service.updateName(request).subscribe();

      const req = httpMock.expectOne('/api/teams/public/me');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(request);

      req.flush({ id: 'team-1', name: request.name, img: null });
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

      req.flush({ id: 'team-1', name: 'Lechia Gdańsk', img: 'team-1.png' });
    });
  });
});
