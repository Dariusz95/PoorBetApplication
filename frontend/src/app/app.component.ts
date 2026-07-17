import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  NavigationEnd,
  Router,
  RouterOutlet,
  TitleStrategy,
} from '@angular/router';
import { TranslocoService } from '@jsverse/transloco';
import { LiveEventsService } from '@shared/services/live-events.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  private readonly liveEventsService = inject(LiveEventsService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly titleStrategy = inject(TitleStrategy);
  private readonly translocoService = inject(TranslocoService);

  constructor() {
    this.initLiveEvents();
    this.initTranslations();
    this.initRouterEvents();
  }

  private initLiveEvents(): void {
    this.liveEventsService.init();
  }

  private initRouterEvents(): void {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        document.getElementById('main-content')?.focus();
      });
  }

  private initTranslations(): void {
    this.translocoService
      .selectTranslation()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.titleStrategy.updateTitle(this.router.routerState.snapshot);
      });
  }
}
