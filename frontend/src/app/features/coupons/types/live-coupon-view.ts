import { Uuid } from '@shared/types/uuid.type';

export interface LiveCouponView {
  couponId: Uuid;
  matchId: Uuid;
  totalOdds: number;
  potentialPayout: number;
}
