import { BetStatus } from '@features/coupons/enums/bet-status';
import { BetType } from './bet-type';
import { CouponStatus } from '@features/coupons/enums/coupon-status';

export interface CouponDetails {
  id: string;
  stake: number;
  status: CouponStatus;
  potentialPayout: number;
  totalOdds: number;
  createdAt: string;
  bets: BetDetails[];
}
export interface BetDetails {
  id: string;
  matchId: string;
  homeTeamName: string;
  awayTeamName: string;
  matchStartTime: string;
  status: BetStatus;
  betType: BetType;
  odds: number;
}
