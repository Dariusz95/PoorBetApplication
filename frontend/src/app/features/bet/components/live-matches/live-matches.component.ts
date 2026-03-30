import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { LiveMatchService } from '../../services/live-match.service';
import { LiveMatchComponent } from '../live-match-card/live-match.component';

@Component({
  selector: 'app-live-matches',
  standalone: true,
  imports: [KeyValuePipe, LiveMatchComponent, AsyncPipe, TranslocoDirective],
  templateUrl: './live-matches.component.html',
  styleUrl: './live-matches.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LiveMatchesComponent {
  private readonly liveMatchService = inject(LiveMatchService);

  liveMatches$ = this.liveMatchService.liveMatches$;
}
