import { BetStatus } from "@features/coupons/dialogs/pb-coupon-dialog/sub-components/coupon-bet-item.component";
import { BetType } from "./bet-type";

export interface CouponDetails {
  id: string;
  stake: number;
  status: any; //TODO: Change to CouponStatus when implemented
  // status: CouponStatus;
  potentialPayout: number;
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

