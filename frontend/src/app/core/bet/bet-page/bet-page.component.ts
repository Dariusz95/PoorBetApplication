import { KeyValuePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { LiveMatchComponent } from '../live-match/live-match/live-match.component';
import { LiveMatchEvent, MatchService } from '../services/match.service';

@Component({
  selector: 'app-bet-page',
  imports: [KeyValuePipe, LiveMatchComponent],
  templateUrl: './bet-page.component.html',
  styleUrl: './bet-page.component.scss',
})
export class BetPageComponent {
  private readonly matchService = inject(MatchService);

  liveMatches: Record<string, LiveMatchEvent> = {};

  updateLiveMatch(event: LiveMatchEvent) {
    this.liveMatches[event.id] = event;
  }

  ngOnInit() {
    this.matchService.streamMatch().subscribe((match) => {
      console.log('Live Matches:', match);
      if (match.id) {
        this.updateLiveMatch(match);
      }
    });
  }
}
