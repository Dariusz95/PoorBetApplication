import { AsyncPipe, DatePipe, SlicePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  signal,
} from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbCardComponent } from '@shared/components/pb-card/pb-card.component';
import { Observable, shareReplay, tap } from 'rxjs';
import { BetSlipService } from '../../services/bet-slip.service';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch, ShortTeamInfo } from '../../types/match.types';
import { OddsButtonComponent } from '../odds-button/odds-button.component';

@Component({
  selector: 'app-pool-card',
  imports: [
    AsyncPipe,
    DatePipe,
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
  private readonly teamCache = new Map<string, Observable<ShortTeamInfo>>();
  private readonly teamNames = signal<Record<string, string>>({});

  getTeamName(teamId: string): Observable<ShortTeamInfo> {
    if (!this.teamCache.has(teamId)) {
      this.teamCache.set(
        teamId,
        this.teamService.getTeam(teamId).pipe(
          tap((team) =>
            this.teamNames.update((current) => ({
              ...current,
              [teamId]: (team as ShortTeamInfo).name,
            })),
          ),
          shareReplay(1),
        ),
      );
    }

    return this.teamCache.get(teamId)!;
  }

  toggleBet(
    match: MatchDto,
    optionValue: string,
    optionLabel: string,
    odds: number,
  ): void {
    this.betSlipService.toggleSelection({
      matchId: match.matchId,
      matchLabel: `${this.getTeamLabel(match.homeTeamId)} vs ${this.getTeamLabel(match.awayTeamId)}`,
      optionValue,
      optionLabel,
      odds,
    });
  }

  isSelected(matchId: string, optionValue: string): boolean {
    return this.betSlipService.isSelected(matchId, optionValue);
  }

  private getTeamLabel(teamId: string): string {
    return this.teamNames()[teamId] ?? teamId.slice(0, 8);
  }
}
