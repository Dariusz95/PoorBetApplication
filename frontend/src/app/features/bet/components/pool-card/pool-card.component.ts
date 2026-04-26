import { AsyncPipe, SlicePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbCardComponent } from '@shared/components/pb-card/pb-card.component';
import { BetType } from '@shared/types/bet-type';
import { Uuid } from '@shared/types/uuid.type';
import { combineLatest, map, Observable } from 'rxjs';
import { BetSlipService } from '../../services/bet-slip.service';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch } from '../../types/match.types';
import { OddsButtonComponent } from '../odds-button/odds-button.component';

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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PoolCardComponent {
  pool = input.required<PoolMatch>();

  private readonly teamService = inject(TeamService);
  private readonly betSlipService = inject(BetSlipService);

  readonly BetType = BetType;

  getTeamName(teamId: Uuid): Observable<string> {
    return this.teamService.getDetails(teamId).pipe(map((team) => team.name));
  }

  toggleBet(
    match: MatchDto,
    betType: BetType,
    optionLabel: string,
    odds: number,
  ): void {
    if (this.hasStarted()) {
      return;
    }
    combineLatest([
      this.getTeamName(match.homeTeamId),
      this.getTeamName(match.awayTeamId),
    ])
      .pipe(takeUntilDestroyed())
      .subscribe(([homeTeamName, awayTeamName]) => {
        const matchLabel = `${homeTeamName} vs ${awayTeamName}`;

        this.betSlipService.toggleSelection({
          matchId: match.matchId,
          matchLabel,
          betType,
          optionLabel,
          odds,
        });
      });
  }

  isSelected(matchId: Uuid, betType: BetType): boolean {
    return this.betSlipService.isSelected(matchId, betType);
  }

  hasStarted(): boolean {
    return new Date(this.pool().scheduledStartTime).getTime() <= Date.now();
  }
}
