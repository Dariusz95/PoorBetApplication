import {
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ToastService } from '@shared/services/toast.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { finalize } from 'rxjs';
import { TeamService } from '../../../../services/team.service';
import { PowerType } from '../../../../types/power-type';
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
  private readonly teamService = inject(TeamService);
  private readonly toastService = inject(ToastService);
  private readonly transloco = inject(TranslocoService);

  team = input.required<Team>();

  edit = output<void>();
  teamUpdated = output<Team>();

  readonly increasingPower = signal<PowerType | null>(null);

  readonly attackPercent = computed(() =>
    Math.min(100, Math.max(0, this.team().attackPower)),
  );
  readonly defencePercent = computed(() =>
    Math.min(100, Math.max(0, this.team().defencePower)),
  );

  increasePower(powerType: PowerType): void {
    if (this.increasingPower()) {
      return;
    }

    this.increasingPower.set(powerType);

    this.teamService
      .increasePower(powerType)
      .pipe(finalize(() => this.increasingPower.set(null)))
      .subscribe((team) => {
        this.teamUpdated.emit(team);
        this.showToast(powerType);
      });
  }

  private showToast(powerType: PowerType): void {
    this.toastService.success(
      this.transloco.translate(
        powerType === 'ATTACK'
          ? 'team.view.increaseAttackSuccessToast'
          : 'team.view.increaseDefenceSuccessToast',
      ),
    );
  }
}
