import { BetType } from "@shared/types/bet-type";
import { Uuid } from "@shared/types/uuid.type";

export interface Bet {
  matchId: Uuid;
  betType: BetType;
}
