import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CouponDetails } from '@shared/types/coupon.types';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
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
}
