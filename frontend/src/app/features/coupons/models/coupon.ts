import { CouponStatus } from '../enums/coupon-status';

export interface Coupon {
  id: string;
  stake: number;
  status: CouponStatus;
  potentialPayout: number;
  createdAt: string;
}
