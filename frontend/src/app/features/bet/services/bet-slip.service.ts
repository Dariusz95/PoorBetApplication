import { computed, Injectable, signal } from '@angular/core';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';

export interface SelectedBet {
  matchId: Uuid;
  matchLabel: string;
  betType: BetType;
  optionLabel: string;
  odds: number;
}

@Injectable({
  providedIn: 'root',
})
export class BetSlipService {
  private readonly _selectedBets = signal<SelectedBet[]>([]);
  private readonly _amount = signal<number>(0);

  readonly selectedBets = this._selectedBets.asReadonly();

  readonly totalOdds = computed(() =>
    this._selectedBets().reduce((total, bet) => total * bet.odds, 1),
  );
  readonly selectedCount = computed(() => this._selectedBets().length);

  potentialWin(amount: number | null): number {
    const totalOdds = this.totalOdds();
    const actualAmount = amount ?? 0;

    return totalOdds * actualAmount;
  }

  setAmount(amount: number): void {
    this._amount.set(amount);
  }

  toggleSelection(bet: SelectedBet): void {
    this._selectedBets.update((currentBets) => {
      const existingBet = currentBets.find(
        (currentBet) => currentBet.matchId === bet.matchId,
      );

      if (
        existingBet &&
        existingBet.matchId === bet.matchId &&
        existingBet.betType === bet.betType
      ) {
        return currentBets.filter(
          (currentBet) => currentBet.matchId !== bet.matchId,
        );
      }

      if (existingBet) {
        return currentBets.map((currentBet) =>
          currentBet.matchId === bet.matchId ? bet : currentBet,
        );
      }

      return [...currentBets, bet];
    });
  }

  removeSelection(matchId: Uuid): void {
    this._selectedBets.update((currentBets) =>
      currentBets.filter((bet) => bet.matchId !== matchId),
    );
  }

  isSelected(matchId: Uuid, betType: BetType): boolean {
    return this._selectedBets().some(
      (bet) => bet.matchId === matchId && bet.betType === betType,
    );
  }

  clearSelections(): void {
    this._selectedBets.set([]);
  }
}
