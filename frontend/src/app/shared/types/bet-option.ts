import { BetType } from './bet-type';

export enum BetOption {
  HomeWin = '1',
  Draw = 'X',
  AwayWin = '2',
}

export const BET_TYPE_TO_OPTION: Record<BetType, BetOption> = {
  [BetType.HomeWin]: BetOption.HomeWin,
  [BetType.Draw]: BetOption.Draw,
  [BetType.AwayWin]: BetOption.AwayWin,
};
