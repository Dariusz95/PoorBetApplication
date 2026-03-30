import { computed, Injectable, signal } from '@angular/core';

export interface SelectedBet {
  matchId: string;
  matchLabel: string;
  optionValue: string;
  optionLabel: string;
  odds: number;
}

@Injectable({
  providedIn: 'root',
})
export class BetSlipService {
  private readonly selectedBetsState = signal<SelectedBet[]>([]);

  readonly selectedBets = this.selectedBetsState.asReadonly();
  readonly totalOdds = computed(() =>
    this.selectedBetsState().reduce((total, bet) => total * bet.odds, 1),
  );
  readonly selectedCount = computed(() => this.selectedBetsState().length);

  toggleSelection(bet: SelectedBet): void {
    this.selectedBetsState.update((currentBets) => {
      const existingBet = currentBets.find(
        (currentBet) => currentBet.matchId === bet.matchId,
      );

      if (
        existingBet &&
        existingBet.matchId === bet.matchId &&
        existingBet.optionValue === bet.optionValue
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

  removeSelection(matchId: string): void {
    this.selectedBetsState.update((currentBets) =>
      currentBets.filter((bet) => bet.matchId !== matchId),
    );
  }

  isSelected(matchId: string, optionValue: string): boolean {
    return this.selectedBetsState().some(
      (bet) => bet.matchId === matchId && bet.optionValue === optionValue,
    );
  }

  clearSelections(): void {
    this.selectedBetsState.set([]);
  }
}
