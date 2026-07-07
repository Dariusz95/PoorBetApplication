import { BetType } from '@shared/types/bet-type';
import { BetStatus } from './bet-status';

export interface Bet {
  id: string;
  matchId: string;
  homeTeamName: string;
  awayTeamName: string;
  matchStartTime: string;
  status: BetStatus;
  betType: BetType;
  odds: number;
  homeGoals: number | null;
  awayGoals: number | null;
}
