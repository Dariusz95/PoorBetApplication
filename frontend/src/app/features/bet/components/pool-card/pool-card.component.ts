import { AsyncPipe, SlicePipe } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { BET_TYPE_TO_OPTION } from '@shared/types/bet-option';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { combineLatest, map, Observable, of, switchMap } from 'rxjs';
import { BetSlipService } from '../../services/bet-slip.service';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch, ShortTeamInfo } from '../../types/match.types';
import { OddsButtonComponent } from '../odds-button/odds-button.component';

interface TeamNames {
  home: string;
  away: string;
}

@Component({
  selector: 'app-pool-card',
  imports: [
    AsyncPipe,
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

  readonly teamNamesMap = toSignal(
    toObservable(this.pool).pipe(
      switchMap((pool) => {
        const matches = pool.matches;
        if (!matches.length) return of({} as Record<string, TeamNames>);

        return combineLatest(
          matches.map((match) =>
            combineLatest([
              this.teamService.getDetails(match.homeTeamId).pipe(map((t: ShortTeamInfo) => t.name)),
              this.teamService.getDetails(match.awayTeamId).pipe(map((t: ShortTeamInfo) => t.name)),
            ]).pipe(
              map(([home, away]): [string, TeamNames] => [match.matchId, { home, away }]),
            ),
          ),
        ).pipe(map((entries) => Object.fromEntries(entries)));
      }),
    ),
    { initialValue: {} as Record<string, TeamNames> },
  );

  getTeamName(teamId: Uuid): Observable<string> {
    return this.teamService.getDetails(teamId).pipe(map((team) => team.name));
  }

  toggleBet(match: MatchDto, betType: BetType, odds: number): void {
    if (this.hasStarted()) return;

    const names = this.teamNamesMap()[match.matchId];
    const home = names?.home ?? match.homeTeamId;
    const away = names?.away ?? match.awayTeamId;

    this.betSlipService.toggleSelection({
      matchId: match.matchId,
      matchLabel: `${home} vs ${away}`,
      betType,
      optionLabel: BET_TYPE_TO_OPTION[betType],
      odds,
    });
  }

  isSelected(matchId: Uuid, betType: BetType): boolean {
    return this.betSlipService.isSelected(matchId, betType);
  }

  hasStarted(): boolean {
    return new Date(this.pool().scheduledStartTime).getTime() <= Date.now();
  }
}
