import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LiveEventsService } from '@shared/services/live-events.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  private readonly liveEventsService = inject(LiveEventsService);

  constructor() {
    this.liveEventsService.init();
  }
}
