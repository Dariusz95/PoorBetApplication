import { BetType } from './bet-type';

export enum BetOption {
  HomeWin = '1',
  Draw = 'X',
  AwayWin = '2',
  Over2_5 = '+2.5',
  Under2_5 = '-2.5',
  Over3_5 = '+3.5',
  Under3_5 = '-3.5',
}

export const BET_TYPE_TO_OPTION: Record<BetType, BetOption> = {
  [BetType.HomeWin]: BetOption.HomeWin,
  [BetType.Draw]: BetOption.Draw,
  [BetType.AwayWin]: BetOption.AwayWin,
  [BetType.Over2_5]: BetOption.Over2_5,
  [BetType.Under2_5]: BetOption.Under2_5,
  [BetType.Over3_5]: BetOption.Over3_5,
  [BetType.Under3_5]: BetOption.Under3_5,
};
