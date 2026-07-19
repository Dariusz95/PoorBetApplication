import { Component } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';

@Component({
  selector: 'app-ranking-page',
  imports: [TranslocoPipe, PbIconComponent],
  template: `
    <section
      class="flex min-h-[60vh] flex-col items-center justify-center gap-3 px-4 text-center"
    >
      <pb-icon icon="leaderboard" size="lg" color="primary" />
      <h1 class="t-heading-lg">{{ "ranking.title" | transloco }}</h1>
      <p class="t-body-md t-muted">{{ "ranking.inProgress" | transloco }}</p>
    </section>
  `,
})
export class RankingPageComponent {}
