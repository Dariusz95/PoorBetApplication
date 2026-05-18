import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';

export interface CouponSelection {
  matchId: Uuid;
  betType: BetType;
}
