import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CreateCouponRequest } from '../components/bet-coupon-card/models/create-coupon-request.model';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.backend.baseURL}/api/coupons`;

  createCoupon(request: CreateCouponRequest): Observable<any> {
    return this.http.post<any>(this.baseUrl, request);
  }
}
