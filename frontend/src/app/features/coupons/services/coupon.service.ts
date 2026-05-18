import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CouponData } from '@shared/types/coupon.types';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateCouponRequest } from '../models/create-coupon-request';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/coupons`;

  createCoupon(request: CreateCouponRequest): Observable<CouponData> {
    return this.http.post<CouponData>(this.baseUrl, request);
  }
}
