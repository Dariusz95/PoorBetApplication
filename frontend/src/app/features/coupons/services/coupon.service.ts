import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PageRequest } from '@shared/interfaces/page-request';
import { PageResponse } from '@shared/interfaces/page-response';
import { buildParams } from '@shared/utils/http-params.builder';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Coupon } from '../types/coupon';
import { CouponDetails } from '../types/coupon-details';
import { CouponFilter } from '../types/coupon-filter';
import { CreateCouponRequest } from '../types/create-coupon-request';
import { RankingCoupon } from '../types/ranking-coupon';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
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

  getPublicCouponDetails(couponId: string): Observable<CouponDetails> {
    return this.http.get<CouponDetails>(`${this.baseUrl}/public/${couponId}`);
  }

  getHighestTotalOdds(): Observable<PageResponse<RankingCoupon>> {
    return this.http.get<PageResponse<RankingCoupon>>(
      `${this.baseUrl}/public/ranking/total-odds`,
    );
  }

  getHighestPayout(): Observable<PageResponse<RankingCoupon>> {
    return this.http.get<PageResponse<RankingCoupon>>(
      `${this.baseUrl}/public/ranking/payout`,
    );
  }
}
