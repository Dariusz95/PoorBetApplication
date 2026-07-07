import { SlicePipe } from '@angular/common';
import { Component, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { BET_TYPE_TO_OPTION } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { BetSlipService } from '../../services/bet-slip.service';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch, ShortTeamInfo } from '../../types/match.types';
import { OddsButtonComponent } from '../odds-button/odds-button.component';

interface TeamDetails {
  home: ShortTeamInfo;
  away: ShortTeamInfo;
}

type TeamSide = 'home' | 'away';

@Component({
  selector: 'app-pool-card',
  imports: [
    SlicePipe,
    OddsButtonComponent,
    PbCardComponent,
    TranslocoDirective,
  ],
  templateUrl: './pool-card.component.html',
  styleUrl: './pool-card.component.scss',
})
export class PoolCardComponent {
  pool = input.required<PoolMatch>();

  private readonly teamService = inject(TeamService);
  private readonly betSlipService = inject(BetSlipService);

  readonly BetType = BetType;

  readonly teamDetailsMap = toSignal(
    toObservable(this.pool).pipe(
      switchMap((pool) => {
        const matches = pool.matches;
        if (!matches.length) return of({} as Record<string, TeamDetails>);

        return combineLatest(
          matches.map((match) =>
            combineLatest([
              this.teamService.getDetails(match.homeTeamId),
              this.teamService.getDetails(match.awayTeamId),
            ]).pipe(
              map(([home, away]): [string, TeamDetails] => [
                match.matchId,
                { home, away },
              ]),
            ),
          ),
        ).pipe(map((entries) => Object.fromEntries(entries)));
      }),
    ),
    { initialValue: {} as Record<string, TeamDetails> },
  );

  private readonly imgErrors = signal<
    Record<Uuid, { home: boolean; away: boolean }>
  >({});

  hasImgError(matchId: Uuid, side: TeamSide): boolean {
    return this.imgErrors()[matchId]?.[side] ?? false;
  }

  markImgError(matchId: Uuid, side: TeamSide): void {
    this.imgErrors.update((errors) => {
      const current = errors[matchId] ?? { home: false, away: false };
      
      return { ...errors, [matchId]: { ...current, [side]: true } };
    });
  }

  toggleBet(match: MatchDto, betType: BetType, odds: number): void {
    if (this.hasStarted()) return;

    const details = this.teamDetailsMap()[match.matchId];
    const home = details?.home.name ?? match.homeTeamId;
    const away = details?.away.name ?? match.awayTeamId;

    this.betSlipService.toggleSelection({
      matchId: match.matchId,
      matchLabel: `${home} vs ${away}`,
      betType,
      optionLabel: BET_TYPE_TO_OPTION[betType],
      odds,
      matchStartTime: this.pool().scheduledStartTime,
    });
  }

  isSelected(matchId: Uuid, betType: BetType): boolean {
    return this.betSlipService.isSelected(matchId, betType);
  }

  hasStarted(): boolean {
    return new Date(this.pool().scheduledStartTime).getTime() <= Date.now();
  }
}
