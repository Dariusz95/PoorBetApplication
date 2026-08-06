import {
  Component,
  DestroyRef,
  computed,
  inject,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { ToastService } from '@shared/services/toast.service';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/ui/pb-form-field/pb-form-field.component';
import { PbInputIconDirective } from '@shared/ui/pb-input/directives/pb-input-icon.directive';
import { PbInputComponent } from '@shared/ui/pb-input/pb-input.component';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';
import { Observable, finalize, of, switchMap } from 'rxjs';
import { TeamService } from '../../services/team.service';
import { CreateTeamForm } from '../../types/create-team-form';
import { Team } from '../../types/team';

type PageMode = 'loading' | 'view' | 'form';

@Component({
  selector: 'app-create-team-page',
  imports: [
    ReactiveFormsModule,
    TranslocoPipe,
    PbCardComponent,
    PbCardBodyDirective,
    PbFormFieldComponent,
    PbLabel,
    PbInputComponent,
    PbInputIconDirective,
    PbIconComponent,
    PbButtonComponent,
    PbSpinnerComponent,
  ],
  templateUrl: './create-team-page.component.html',
})
export class CreateTeamPageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly teamService = inject(TeamService);
  private readonly toastService = inject(ToastService);
  private readonly transloco = inject(TranslocoService);
  private readonly destroyRef = inject(DestroyRef);

  readonly mode = signal<PageMode>('loading');
  readonly team = signal<Team | null>(null);
  readonly submitting = signal(false);
  readonly isDragging = signal(false);
  readonly selectedFile = signal<File | null>(null);
  readonly logoPreviewUrl = signal<string | null>(null);
  readonly logoError = signal(false);

  readonly isEditingExisting = computed(() => !!this.team());

  readonly form: FormGroup<CreateTeamForm> = this.fb.group({
    name: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(50),
    ]),
  });

  constructor() {
    this.destroyRef.onDestroy(() => this.revokePreviewUrl());

    this.teamService.getMyTeam().subscribe((team) => {
      this.team.set(team);
      this.mode.set(team ? 'view' : 'form');
    });
  }

  startEdit(): void {
    const team = this.team();

    if (!team) {
      return;
    }

    this.form.patchValue({ name: team.name });
    this.selectedFile.set(null);
    this.logoPreviewUrl.set(team.img);
    this.logoError.set(false);
    this.mode.set('form');
  }

  cancelEdit(): void {
    this.revokePreviewUrl();
    this.selectedFile.set(null);
    this.logoPreviewUrl.set(null);
    this.logoError.set(false);
    this.mode.set('view');
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    this.setLogoFile(event.dataTransfer?.files?.[0] ?? null);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.setLogoFile(input.files?.[0] ?? null);
  }

  clearLogoSelection(event: Event): void {
    event.stopPropagation();
    this.revokePreviewUrl();
    this.selectedFile.set(null);
    this.logoPreviewUrl.set(this.team()?.img ?? null);
  }

  onSubmit(): void {
    const file = this.selectedFile();
    const editing = this.isEditingExisting();
    const requiresLogo = !editing;

    if (this.form.invalid || (requiresLogo && !file)) {
      this.form.markAllAsTouched();
      this.logoError.set(requiresLogo && !file);
      return;
    }

    this.submitting.set(true);
    const name = this.form.getRawValue().name;

    const result$: Observable<Team> = editing
      ? this.teamService
          .updateName({ name })
          .pipe(switchMap((team) => (file ? this.teamService.uploadLogo(file) : of(team))))
      : this.teamService
          .create({ name })
          .pipe(switchMap(() => this.teamService.uploadLogo(file!)));

    result$
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe((team) => {
        this.team.set(team);
        this.mode.set('view');
        this.toastService.success(
          this.transloco.translate(
            editing
              ? 'team.form.editSuccessToast'
              : 'team.form.createSuccessToast',
          ),
        );
      });
  }

  private setLogoFile(file: File | null): void {
    this.revokePreviewUrl();
    this.selectedFile.set(file);
    this.logoPreviewUrl.set(file ? URL.createObjectURL(file) : null);

    if (file) {
      this.logoError.set(false);
    }
  }

  private revokePreviewUrl(): void {
    const url = this.logoPreviewUrl();

    if (url?.startsWith('blob:')) {
      URL.revokeObjectURL(url);
    }
  }
}
