import { computed, Signal } from '@angular/core';
import {
  patchState,
  signalStore,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { SelectedBet } from '../types/bet-slip.types';

// NgRx SignalStore = sformalizowany wzorzec, który ten serwis i tak realizował
// ręcznie (prywatny `signal`, `computed`, `update`). Stan trzymamy jako jawnie
// otypowaną tablicę `SelectedBet[]` — na jeden mecz przypada dokładnie jeden
// zakład (klucz `matchId`), a kolekcja jest na tyle mała, że nie potrzebuje
// `withEntities`/`entityMap`.
//
// Wewnętrzny slice nazywa się `_selectedBets`; na zewnątrz wystawiamy go jako
// jawnie otypowany `computed<SelectedBet[]>` (`selectedBets`), żeby typ elementu
// nie zależał od inferencji `withState`.
interface BetSlipState {
  _selectedBets: SelectedBet[];
}

const initialState: BetSlipState = {
  _selectedBets: [],
};

/** Czy mecz danego zakładu już się rozpoczął (kurs stał się nieaktualny). */
function isMatchStarted(matchStartTime: string): boolean {
  return new Date(matchStartTime).getTime() <= Date.now();
}

export const BetSlipStore = signalStore(
  { providedIn: 'root' },
  withState<BetSlipState>(initialState),
  withComputed(({ _selectedBets }) => ({
    selectedBets: computed<SelectedBet[]>(() => _selectedBets()),
    selectedCount: computed<number>(() => _selectedBets().length),
    totalOdds: computed<number>(() =>
      _selectedBets().reduce((total, bet) => total * bet.odds, 1),
    ),
  })),
  withMethods((store) => {
    const bets: Signal<SelectedBet[]> = store._selectedBets;

    return {
      toggleSelection(bet: SelectedBet): void {
        const current = bets().find((entry) => entry.matchId === bet.matchId);

        if (current?.betType === bet.betType) {
          // Ten sam kurs kliknięty ponownie -> zdejmij.
          patchState(store, {
            _selectedBets: bets().filter(
              (entry) => entry.matchId !== bet.matchId,
            ),
          });
          return;
        }

        // Nowy zakład albo inny kurs tego samego meczu -> dodaj lub podmień.
        patchState(store, {
          _selectedBets: current
            ? bets().map((entry) =>
                entry.matchId === bet.matchId ? bet : entry,
              )
            : [...bets(), bet],
        });
      },

      removeSelection(matchId: Uuid): void {
        patchState(store, {
          _selectedBets: bets().filter((entry) => entry.matchId !== matchId),
        });
      },

      clearSelections(): void {
        patchState(store, { _selectedBets: [] });
      },

      removeStartedSelections(): void {
        patchState(store, {
          _selectedBets: bets().filter(
            (entry) => !isMatchStarted(entry.matchStartTime),
          ),
        });
      },

      isSelected(matchId: Uuid, betType: BetType): boolean {
        return bets().some(
          (entry) => entry.matchId === matchId && entry.betType === betType,
        );
      },

      hasStartedSelections(): boolean {
        return bets().some((entry) => isMatchStarted(entry.matchStartTime));
      },

      isStarted(matchStartTime: string): boolean {
        return isMatchStarted(matchStartTime);
      },

      potentialWin(amount: number | null): number {
        return store.totalOdds() * (amount ?? 0);
      },
    };
  }),
);
