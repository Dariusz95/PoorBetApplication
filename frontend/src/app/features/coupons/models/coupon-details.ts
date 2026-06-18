import { Bet } from './bet';
import { Coupon } from './coupon';

export interface CouponDetails extends Coupon {
  totalOdds: number;
  bets: Bet[];
}
