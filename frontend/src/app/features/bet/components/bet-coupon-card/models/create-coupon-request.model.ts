import { Bet } from './bet.model';

export interface CreateCouponRequest {
  stake: number;
  bets: Bet[];
}
