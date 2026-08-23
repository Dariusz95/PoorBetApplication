import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, Injector } from '@angular/core';
import { LiveMatchService } from '@features/bet/services/live-match.service';
import { MatchEventType } from '@features/bet/types/match.types';
import { PageRequest } from '@shared/interfaces/page-request';
import { PageResponse } from '@shared/interfaces/page-response';
import { buildParams } from '@shared/utils/http-params.builder';
import {
  filter,
  map,
  Observable,
  switchMap,
  takeWhile,
  tap,
  timer,
} from 'rxjs';
import { environment } from 'src/environments/environment';
import { Coupon } from '../types/coupon';
import { CouponDetails } from '../types/coupon-details';
import { CouponFilter } from '../types/coupon-filter';
import { CouponStatus } from '../types/coupon-status';
import { CreateCouponRequest } from '../types/create-coupon-request';
import { LiveCouponView } from '../types/live-coupon-view';
import { RankingResponse } from '../types/ranking-response';

const COUPON_SETTLEMENT_DELAY_MS = 3000;

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly injector = inject(Injector);
  private readonly baseUrl = `${environment.backend.baseURL}/api/coupons`;

  create(request: CreateCouponRequest): Observable<CouponDetails> {
    return this.http.post<CouponDetails>(this.baseUrl, request);
  }

  getMyCoupons(
    request: PageRequest,
    filter: CouponFilter,
  ): Observable<PageResponse<Coupon>> {
    return this.http.get<PageResponse<Coupon>>(`${this.baseUrl}/me`, {
      params: buildParams(request, filter as Record<string, unknown>),
    });
  }

  getCouponDetails(couponId: string): Observable<CouponDetails> {
    return this.http.get<CouponDetails>(`${this.baseUrl}/${couponId}`);
  }

  getMyLiveCoupons(matchIds: string[]): Observable<LiveCouponView[]> {
    const params = new HttpParams().set('matchIds', matchIds.join(','));

    return this.http.get<LiveCouponView[]>(`${this.baseUrl}/me/live`, {
      params,
    });
  }

  watchSettlement(
    couponId: string,
    matchIds: string[],
  ): Observable<CouponDetails> {
    const pendingMatchIds = new Set(matchIds);
    const liveMatchService = this.injector.get(LiveMatchService);

    return liveMatchService.liveMatches$.pipe(
      map((liveMatches) =>
        Object.values(liveMatches)
          .filter(
            (event) =>
              pendingMatchIds.has(event.id) &&
              event.eventType === MatchEventType.MatchEnded,
          )
          .map((event) => event.id),
      ),
      filter((newlyEndedMatchIds) => newlyEndedMatchIds.length > 0),
      tap((newlyEndedMatchIds) =>
        newlyEndedMatchIds.forEach((id) => pendingMatchIds.delete(id)),
      ),
      switchMap(() => timer(COUPON_SETTLEMENT_DELAY_MS)),
      switchMap(() => this.getCouponDetails(couponId)),
      takeWhile((coupon) => coupon.status === CouponStatus.Open, true),
    );
  }

  getPublicCouponDetails(couponId: string): Observable<CouponDetails> {
    return this.http.get<CouponDetails>(`${this.baseUrl}/public/${couponId}`);
  }

  getHighestTotalOdds(): Observable<RankingResponse> {
    return this.http.get<RankingResponse>(
      `${this.baseUrl}/public/ranking/total-odds`,
    );
  }

  getHighestPayout(): Observable<RankingResponse> {
    return this.http.get<RankingResponse>(
      `${this.baseUrl}/public/ranking/payout`,
    );
  }
}
