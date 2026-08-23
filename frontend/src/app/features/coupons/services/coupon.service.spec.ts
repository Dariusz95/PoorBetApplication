import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LiveMatchService } from '@features/bet/services/live-match.service';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { Uuid } from '@shared/types/uuid.type';
import { BehaviorSubject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { CouponDetails } from '../types/coupon-details';
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

  describe('getMyLiveCoupons', () => {
    it('should GET /api/coupons/me/live with comma-separated matchIds', () => {
      service.getMyLiveCoupons(['match-1', 'match-2']).subscribe();

      const req = httpMock.expectOne((r) => r.url === '/api/coupons/me/live');
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('matchIds')).toBe('match-1,match-2');

      req.flush([]);
    });
  });

  describe('watchSettlement', () => {
    let liveMatches$: BehaviorSubject<Record<string, LiveMatchEvent>>;

    function matchEndedEvent(id: string): LiveMatchEvent {
      return {
        id: id as Uuid,
        minute: 90,
        homeTeamId: 'home' as Uuid,
        awayTeamId: 'away' as Uuid,
        homeScore: 1,
        awayScore: 0,
        eventType: MatchEventType.MatchEnded,
        eventData: null,
      };
    }

    beforeEach(() => {
      vi.useFakeTimers();
      liveMatches$ = new BehaviorSubject<Record<string, LiveMatchEvent>>({});

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          CouponService,
          { provide: LiveMatchService, useValue: { liveMatches$ } },
        ],
      });
      service = TestBed.inject(CouponService);
      httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it('should keep refetching until every match in the coupon has ended and the coupon settles', () => {
      const emissions: CouponDetails[] = [];
      service
        .watchSettlement('coupon-1', ['match-a', 'match-b'])
        .subscribe((coupon) => emissions.push(coupon));

      // match-a ends first - match-b hasn't resolved yet, coupon stays OPEN
      liveMatches$.next({ 'match-a': matchEndedEvent('match-a') });
      vi.advanceTimersByTime(2000);
      httpMock
        .expectOne('/api/coupons/coupon-1')
        .flush({ id: 'coupon-1', status: CouponStatus.Open });

      expect(emissions).toHaveLength(1);
      expect(emissions[0].status).toBe(CouponStatus.Open);

      // match-b ends - the coupon is now fully settled
      liveMatches$.next({
        'match-a': matchEndedEvent('match-a'),
        'match-b': matchEndedEvent('match-b'),
      });
      vi.advanceTimersByTime(2000);
      httpMock
        .expectOne('/api/coupons/coupon-1')
        .flush({ id: 'coupon-1', status: CouponStatus.Won });

      expect(emissions).toHaveLength(2);
      expect(emissions[1].status).toBe(CouponStatus.Won);

      // further ticks reporting the same two matches as ended must not
      // trigger another fetch - the subscription already completed
      liveMatches$.next({
        'match-a': matchEndedEvent('match-a'),
        'match-b': matchEndedEvent('match-b'),
      });
      vi.advanceTimersByTime(2000);
      httpMock.expectNone('/api/coupons/coupon-1');
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

      const req = httpMock.expectOne('/api/coupons/public/ranking/payout');
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
