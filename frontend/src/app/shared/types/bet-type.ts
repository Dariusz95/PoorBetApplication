export enum BetType {
  HomeWin = 'HOME_WIN',
  Draw = 'DRAW',
  AwayWin = 'AWAY_WIN',
  Over2_5 = 'OVER_2_5',
  Under2_5 = 'UNDER_2_5',
  Over3_5 = 'OVER_3_5',
  Under3_5 = 'UNDER_3_5',
}

const GOAL_MARKET_BET_TYPES: ReadonlySet<BetType> = new Set([
  BetType.Over2_5,
  BetType.Under2_5,
  BetType.Over3_5,
  BetType.Under3_5,
]);

export function isGoalMarketBetType(betType: BetType): boolean {
  return GOAL_MARKET_BET_TYPES.has(betType);
}
