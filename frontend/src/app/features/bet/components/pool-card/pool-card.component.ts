import { DatePipe, SlicePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { MatchDto, PoolMatch } from '../../services/match.service';
import { OddsButtonComponent } from '../odds-button/odds-button.component';

@Component({
  selector: 'app-pool-card',
  imports: [DatePipe, SlicePipe, PbButtonComponent, OddsButtonComponent],
  templateUrl: './pool-card.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PoolCardComponent {
  pool = input.required<PoolMatch>();
  timeRemaining = input.required<string>();

  trackByMatchId = (_index: number, match: MatchDto): string => match.matchId;
}
