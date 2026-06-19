import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PageResponse } from '@shared/interfaces/page-response';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Coupon } from '../models/coupon';
import { CouponDetails } from '../models/coupon-details';
import { CreateCouponRequest } from '../models/create-coupon-request';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/coupons`;

  create(request: CreateCouponRequest): Observable<CouponDetails> {
    return this.http.post<CouponDetails>(this.baseUrl, request);
  }

  getOpen(page = 0, size = 20): Observable<PageResponse<Coupon>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Coupon>>(`${this.baseUrl}/me/open`, { params });
  }

  getWon(page = 0, size = 20): Observable<PageResponse<Coupon>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Coupon>>(`${this.baseUrl}/me/won`, { params });
  }

  getSettled(page = 0, size = 20): Observable<PageResponse<Coupon>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Coupon>>(`${this.baseUrl}/me/settled`, { params });
  }

  getCouponDetails(couponId: string): Observable<CouponDetails> {
    return this.http.get<CouponDetails>(`${this.baseUrl}/${couponId}`);
  }
}
