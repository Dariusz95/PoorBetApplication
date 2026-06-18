import { Component, inject } from '@angular/core';
import { CouponService } from '@features/coupons/services/coupon.service';

@Component({
  selector: 'app-coupon-list',
  imports: [],
  templateUrl: './coupon-list.component.html',
  styleUrl: './coupon-list.component.scss',
  standalone: true,
})
export class CouponListComponent {
  private readonly couponService = inject(CouponService);

  ngOnInit(): void {
    this.couponService.getOpen().subscribe((response) => {
      console.log('Coupons:', response);
    });
  }
}
