import { BetOption } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';

export interface SelectedBet {
  matchId: Uuid;
  matchLabel: string;
  betType: BetType;
  optionLabel: BetOption;
  odds: number;
  matchStartTime: string;
}
