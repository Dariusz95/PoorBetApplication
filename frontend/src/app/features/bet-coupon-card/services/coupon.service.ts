import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CreateCouponRequest } from '../models/create-coupon-request';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/coupons`;

  createCoupon(request: CreateCouponRequest): Observable<void> {
    return this.http.post<void>(this.baseUrl, request);
  }
}
