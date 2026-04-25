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
import { Uuid } from '@shared/types/uuid.type';
import { map, Observable } from 'rxjs';
import { BetSlipService } from '../../services/bet-slip.service';
import { TeamService } from '../../services/team.service';
import { MatchDto, PoolMatch } from '../../types/match.types';
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
  private readonly teamNames = signal<Record<string, string>>({});

  getTeamName(teamId: Uuid): Observable<string> {
    return this.teamService.getDetails(teamId).pipe(map((team) => team.name));
  }

  toggleBet(
    match: MatchDto,
    optionValue: string,
    optionLabel: string,
    odds: number,
  ): void {
    if (this.hasStarted()) {
      return;
    }

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

  hasStarted(): boolean {
    return new Date(this.pool().scheduledStartTime).getTime() <= Date.now();
  }

  private getTeamLabel(teamId: string): string {
    return this.teamNames()[teamId] ?? teamId.slice(0, 8);
  }
}
