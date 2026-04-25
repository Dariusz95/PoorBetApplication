import { TestBed } from '@angular/core/testing';
import { BetSlipService } from './bet-slip.service';

describe('BetSlipService', () => {
    let service: BetSlipService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(BetSlipService);
    });

    it('should add and remove the same selection on toggle', () => {
        const bet = {
            matchId: 'match-1',
            matchLabel: 'A vs B',
            optionValue: 'HOME_WIN',
            optionLabel: '1',
            odds: 1.8,
        };

        service.toggleSelection(bet);
        expect(service.selectedBets()).toEqual([bet]);
        expect(service.isSelected('match-1', 'HOME_WIN')).toBe(true);

        service.toggleSelection(bet);
        expect(service.selectedBets()).toEqual([]);
        expect(service.isSelected('match-1', 'HOME_WIN')).toBe(false);
    });

    it('should replace selection for the same match with a different option', () => {
        service.toggleSelection({
            matchId: 'match-1',
            matchLabel: 'A vs B',
            optionValue: 'HOME_WIN',
            optionLabel: '1',
            odds: 1.8,
        });

        service.toggleSelection({
            matchId: 'match-1',
            matchLabel: 'A vs B',
            optionValue: 'DRAW',
            optionLabel: 'X',
            odds: 3.25,
        });

        expect(service.selectedBets()).toEqual([
            {
                matchId: 'match-1',
                matchLabel: 'A vs B',
                optionValue: 'DRAW',
                optionLabel: 'X',
                odds: 3.25,
            },
        ]);
        expect(service.totalOdds()).toBe(3.25);
    });
});
