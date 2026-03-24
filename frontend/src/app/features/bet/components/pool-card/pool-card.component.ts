import { AsyncPipe, DatePipe, SlicePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, signal } from '@angular/core';
import { Observable, shareReplay, tap } from 'rxjs';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch } from '../../services/match.service';
import { BetSlipService } from '../../services/bet-slip.service';
import { OddsButtonComponent } from '../odds-button/odds-button.component';
import { ShortTeamInfo } from '../live-match-card/live-match.component';

@Component({
  selector: 'app-pool-card',
  imports: [AsyncPipe, DatePipe, SlicePipe, OddsButtonComponent],
  templateUrl: './pool-card.component.html',
  styleUrl: './pool-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PoolCardComponent {
  pool = input.required<PoolMatch>();
  // timeRemaining = input.required<string>();

  private readonly teamService = inject(TeamService);
  private readonly betSlipService = inject(BetSlipService);
  private readonly teamCache = new Map<string, Observable<ShortTeamInfo>>();
  private readonly teamNames = signal<Record<string, string>>({});

  trackByMatchId = (_index: number, match: MatchDto): string => match.matchId;

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

  toggleBet(match: MatchDto, optionValue: string, optionLabel: string, odds: number): void {
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

