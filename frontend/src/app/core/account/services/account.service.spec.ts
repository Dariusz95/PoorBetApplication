import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AccountService } from './account.service';

describe('AccountService', () => {
  let service: AccountService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AccountService,
      ],
    });
    service = TestBed.inject(AccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getAccountState', () => {
    it('should GET /api/account/me and update balance/level/exp signals', () => {
      service.getAccountState().subscribe();

      const req = httpMock.expectOne('/api/account/me');
      expect(req.request.method).toBe('GET');

      req.flush({
        userId: 'user-1',
        balance: 42.5,
        level: 5,
        currentExp: 950,
        requiredExpForNextLevel: 1500,
        winBonusPercent: 5,
      });

      expect(service.balance()).toBe(42.5);
      expect(service.level()).toBe(5);
      expect(service.currentExp()).toBe(950);
      expect(service.requiredExpForNextLevel()).toBe(1500);
      expect(service.winBonusPercent()).toBe(5);
    });

    it('should set requiredExpForNextLevel to null at the max level', () => {
      service.getAccountState().subscribe();

      const req = httpMock.expectOne('/api/account/me');
      req.flush({
        userId: 'user-1',
        balance: 100,
        level: 15,
        currentExp: 25000,
        requiredExpForNextLevel: null,
        winBonusPercent: 15,
      });

      expect(service.requiredExpForNextLevel()).toBeNull();
    });
  });

  describe('ensureAccountStateLoaded', () => {
    it('should fetch account state only once when called multiple times', () => {
      service.ensureAccountStateLoaded();
      service.ensureAccountStateLoaded();

      const req = httpMock.expectOne('/api/account/me');
      req.flush({
        userId: 'user-1',
        balance: 100,
        level: 1,
        currentExp: 0,
        requiredExpForNextLevel: 100,
        winBonusPercent: 1,
      });

      service.ensureAccountStateLoaded();
      httpMock.expectNone('/api/account/me');
    });
  });
});
