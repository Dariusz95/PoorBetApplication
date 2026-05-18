export interface CouponData {
  id: string;
  stake: number;
  potentialWin: number;
  odds: number;
  bets: BetInfo[];
  createdAt: Date;
}

export interface BetInfo {
  matchId: string;
  matchName: string;
  betType: string;
  odds: number;
}
