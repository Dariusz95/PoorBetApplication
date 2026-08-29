import { TestBed } from '@angular/core/testing';
import { BetOption } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { beforeEach, describe, expect, it } from 'vitest';
import { BetSlipStore } from './bet-slip.store';
import { SelectedBet } from '../types/bet-slip.types';

describe('BetSlipStore', () => {
  let store: InstanceType<typeof BetSlipStore>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(BetSlipStore);
  });

  it('should add and remove the same selection on toggle', () => {
    const bet: SelectedBet = {
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.HomeWin,
      optionLabel: BetOption.HomeWin,
      odds: 1.8,
      matchStartTime: '2026-07-01T18:00:00Z',
    };

    store.toggleSelection(bet);
    expect(store.selectedBets()).toEqual([bet]);
    expect(store.isSelected(bet.matchId, BetType.HomeWin)).toBe(true);

    store.toggleSelection(bet);
    expect(store.selectedBets()).toEqual([]);
    expect(store.isSelected(bet.matchId, BetType.HomeWin)).toBe(false);
  });

  it('should replace selection for the same match with a different option', () => {
    store.toggleSelection({
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.HomeWin,
      optionLabel: BetOption.HomeWin,
      odds: 1.8,
      matchStartTime: '2026-07-01T18:00:00Z',
    });

    store.toggleSelection({
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.Draw,
      optionLabel: BetOption.Draw,
      odds: 3.25,
      matchStartTime: '2026-07-01T18:00:00Z',
    });

    expect(store.selectedBets()).toEqual([
      {
        matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
        matchLabel: 'A vs B',
        betType: BetType.Draw,
        optionLabel: BetOption.Draw,
        odds: 3.25,
        matchStartTime: '2026-07-01T18:00:00Z',
      },
    ]);
    expect(store.totalOdds()).toBe(3.25);
  });

  describe('isStarted', () => {
    it('should return true when the match start time is in the past', () => {
      const past = new Date(Date.now() - 60_000).toISOString();

      expect(store.isStarted(past)).toBe(true);
    });

    it('should return false when the match start time is in the future', () => {
      const future = new Date(Date.now() + 60_000).toISOString();

      expect(store.isStarted(future)).toBe(false);
    });
  });

  describe('removeStartedSelections', () => {
    it('should only remove selections whose match has already started', () => {
      const started: SelectedBet = {
        matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
        matchLabel: 'A vs B',
        betType: BetType.HomeWin,
        optionLabel: BetOption.HomeWin,
        odds: 1.8,
        matchStartTime: new Date(Date.now() - 60_000).toISOString(),
      };
      const upcoming: SelectedBet = {
        matchId: '550e8400-e29b-41d4-a716-446655440001' as Uuid,
        matchLabel: 'C vs D',
        betType: BetType.HomeWin,
        optionLabel: BetOption.HomeWin,
        odds: 2.1,
        matchStartTime: new Date(Date.now() + 60_000).toISOString(),
      };

      store.toggleSelection(started);
      store.toggleSelection(upcoming);
      expect(store.hasStartedSelections()).toBe(true);

      store.removeStartedSelections();

      expect(store.selectedBets()).toEqual([upcoming]);
      expect(store.hasStartedSelections()).toBe(false);
    });
  });
});
