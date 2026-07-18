import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import {
  LiveMatchEvent,
  MatchEventType,
} from '@features/bet/types/match.types';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { LiveMatchTeamComponent } from '../live-match-team/live-match-team.component';

@Component({
  selector: 'app-live-match-card',
  imports: [
    PbCardComponent,
    TranslocoPipe,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    LiveMatchTeamComponent,
  ],
  templateUrl: './live-match.component.html',
  styleUrl: './live-match.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchComponent {
  readonly liveMatch = input.required<LiveMatchEvent>();

  readonly MatchEventType = MatchEventType;
}
