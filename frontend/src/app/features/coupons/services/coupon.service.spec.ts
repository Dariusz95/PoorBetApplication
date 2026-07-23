import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { CouponStatus } from '../types/coupon-status';
import { CreateCouponRequest } from '../types/create-coupon-request';
import { CouponService } from './coupon.service';

describe('CouponService', () => {
  let service: CouponService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        CouponService,
      ],
    });
    service = TestBed.inject(CouponService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('create', () => {
    it('should POST the request to /api/coupons', () => {
      const request: CreateCouponRequest = {
        stake: 10,
        bets: [],
      };

      service.create(request).subscribe();

      const req = httpMock.expectOne('/api/coupons');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(request);

      req.flush({});
    });
  });

  describe('getMyCoupons', () => {
    it('should GET /api/coupons/me with pagination and filter params', () => {
      service
        .getMyCoupons(
          { page: 1, size: 10, sort: 'createdAt', direction: 'desc' },
          { statuses: [CouponStatus.Won] },
        )
        .subscribe();

      const req = httpMock.expectOne((r) => r.url === '/api/coupons/me');
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('page')).toBe('1');
      expect(req.request.params.get('size')).toBe('10');
      expect(req.request.params.get('sort')).toBe('createdAt,desc');
      expect(req.request.params.get('statuses')).toBe('WON');

      req.flush({
        content: [],
        totalElements: 0,
        totalPages: 0,
        size: 10,
        number: 1,
        first: true,
        last: true,
      });
    });
  });

  describe('getCouponDetails', () => {
    it('should GET /api/coupons/:id', () => {
      service.getCouponDetails('coupon-1').subscribe();

      const req = httpMock.expectOne('/api/coupons/coupon-1');
      expect(req.request.method).toBe('GET');

      req.flush({});
    });
  });

  describe('getPublicCouponDetails', () => {
    it('should GET /api/coupons/public/:id', () => {
      service.getPublicCouponDetails('coupon-1').subscribe();

      const req = httpMock.expectOne('/api/coupons/public/coupon-1');
      expect(req.request.method).toBe('GET');

      req.flush({});
    });
  });

  describe('getHighestTotalOdds', () => {
    it('should GET /api/coupons/public/ranking/total-odds', () => {
      service.getHighestTotalOdds().subscribe();

      const req = httpMock.expectOne('/api/coupons/public/ranking/total-odds');
      expect(req.request.method).toBe('GET');

      req.flush({
        ranking: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: 20,
          number: 0,
          first: true,
          last: true,
        },
        lastUpdatedAt: '2026-07-22T12:00:00Z',
      });
    });
  });

  describe('getHighestPayout', () => {
    it('should GET /api/coupons/public/ranking/payout', () => {
      service.getHighestPayout().subscribe();

      const req = httpMock.expectOne(
        '/api/coupons/public/ranking/payout',
      );
      expect(req.request.method).toBe('GET');

      req.flush({
        ranking: {
          content: [],
          totalElements: 0,
          totalPages: 0,
          size: 20,
          number: 0,
          first: true,
          last: true,
        },
        lastUpdatedAt: '2026-07-22T12:00:00Z',
      });
    });
  });
});
