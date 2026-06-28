import { CouponStatus } from '../enums/coupon-status';

export interface CouponFilter {
  statuses?: CouponStatus[];
}
