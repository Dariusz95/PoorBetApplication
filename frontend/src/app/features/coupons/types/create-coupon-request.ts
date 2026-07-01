import { CouponSelection } from './coupon-selection.model';

export interface CreateCouponRequest {
  stake: number;
  bets: CouponSelection[];
}
