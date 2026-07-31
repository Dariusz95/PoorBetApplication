export interface WalletBalanceEvent {
  userId: string;
  balance: number;
}

export interface AccountProgressEvent {
  userId: string;
  level: number;
  currentExp: number;
  requiredExpForNextLevel: number | null;
  winBonusPercent: number;
}
