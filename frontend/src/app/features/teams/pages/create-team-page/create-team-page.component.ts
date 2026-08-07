import { Component, inject, signal } from '@angular/core';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ToastService } from '@shared/services/toast.service';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';
import { Observable, finalize, map, of, switchMap } from 'rxjs';
import { TeamService } from '../../services/team.service';
import { Team } from '../../types/team';
import { TeamFormValue } from '../../types/team-form-value';
import { TeamFormComponent } from './components/team-form/team-form.component';
import { TeamViewComponent } from './components/team-view/team-view.component';

type PageMode = 'loading' | 'view' | 'form';

@Component({
  selector: 'app-create-team-page',
  imports: [
    TranslocoPipe,
    PbSpinnerComponent,
    TeamViewComponent,
    TeamFormComponent,
  ],
  templateUrl: './create-team-page.component.html',
})
export class CreateTeamPageComponent {
  private readonly teamService = inject(TeamService);
  private readonly toastService = inject(ToastService);
  private readonly transloco = inject(TranslocoService);

  readonly mode = signal<PageMode>('loading');
  readonly team = signal<Team | null>(null);
  readonly submitting = signal(false);

  constructor() {
    this.teamService.getMyTeam().subscribe((team) => {
      this.team.set(team ? team : null);
      this.mode.set(team ? 'view' : 'form');
    });
  }

  startEdit(): void {
    this.mode.set('form');
  }

  handleCancel(): void {
    this.mode.set('view');
  }

  handleSave({ name, logoFile }: TeamFormValue): void {
    const current = this.team();
    const editing = !!current;
    const nameChanged = !editing || name !== current!.name;

    if (editing && !nameChanged && !logoFile) {
      this.mode.set('view');
      return;
    }

    this.submitting.set(true);

    const persist$: Observable<Team> = editing
      ? nameChanged
        ? this.teamService.updateName({ name })
        : of(current!)
      : this.teamService.create({ name });

    const result$: Observable<Team> = persist$.pipe(
      switchMap((team) =>
        logoFile
          ? this.teamService
              .uploadLogo(logoFile)
              .pipe(map((logo) => ({ ...team, logo: logo.logo })))
          : of({ ...team, logo: current?.logo ?? null }),
      ),
    );

    result$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe((team) => {
        this.team.set(team);
        this.mode.set('view');
        this.teamService.removeFromCache(team.id);
        this.toastService.success(
          this.transloco.translate(
            editing
              ? 'team.form.editSuccessToast'
              : 'team.form.createSuccessToast',
          ),
        );
      });
  }
}
