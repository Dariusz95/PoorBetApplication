import { TestBed } from '@angular/core/testing';
import { BetOption } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { beforeEach, describe, expect, it } from 'vitest';
import { BetSlipService } from './bet-slip.service';
import { SelectedBet } from '../types/bet-slip.types';

describe('BetSlipService', () => {
  let service: BetSlipService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BetSlipService);
  });

  it('should add and remove the same selection on toggle', () => {
    const bet: SelectedBet = {
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.HomeWin,
      optionLabel: BetOption.HomeWin,
      odds: 1.8,
    };

    service.toggleSelection(bet);
    expect(service.selectedBets()).toEqual([bet]);
    expect(service.isSelected(bet.matchId, BetType.HomeWin)).toBe(true);

    service.toggleSelection(bet);
    expect(service.selectedBets()).toEqual([]);
    expect(service.isSelected(bet.matchId, BetType.HomeWin)).toBe(false);
  });

  it('should replace selection for the same match with a different option', () => {
    service.toggleSelection({
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.HomeWin,
      optionLabel: BetOption.HomeWin,
      odds: 1.8,
    });

    service.toggleSelection({
      matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
      matchLabel: 'A vs B',
      betType: BetType.Draw,
      optionLabel: BetOption.Draw,
      odds: 3.25,
    });

    expect(service.selectedBets()).toEqual([
      {
        matchId: '550e8400-e29b-41d4-a716-446655440000' as Uuid,
        matchLabel: 'A vs B',
        betType: BetType.Draw,
        optionLabel: BetOption.Draw,
        odds: 3.25,
      },
    ]);
    expect(service.totalOdds()).toBe(3.25);
  });
});
