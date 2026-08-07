import { Component, computed, input, output } from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { Team } from '../../../../types/team';

@Component({
  selector: 'app-team-view',
  imports: [
    TranslocoPipe,
    PbCardComponent,
    PbCardBodyDirective,
    PbIconComponent,
    PbButtonComponent,
  ],
  templateUrl: './team-view.component.html',
})
export class TeamViewComponent {
  team = input.required<Team>();

  edit = output<void>();

  readonly attackPercent = computed(() =>
    Math.min(100, Math.max(0, this.team().attackPower)),
  );
  readonly defencePercent = computed(() =>
    Math.min(100, Math.max(0, this.team().defencePower)),
  );
}
