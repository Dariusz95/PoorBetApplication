import { CouponStatus } from './coupon-status';

export interface RankingCoupon {
  couponId: string;
  stake: number;
  email: string;
  status: CouponStatus;
  potentialPayout: number;
  createdAt: string;
  totalOdds: number;
}
