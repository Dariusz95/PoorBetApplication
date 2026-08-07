import { Component, DestroyRef, computed, effect, inject, input, output, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbCardComponent } from '@shared/ui/pb-card/pb-card.component';
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/ui/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '@shared/ui/pb-input/pb-input.component';
import { PbInputIconDirective } from '@shared/ui/pb-input/directives/pb-input-icon.directive';
import { CreateTeamForm } from '../../../../types/create-team-form';
import { Team } from '../../../../types/team';
import { TeamFormValue } from '../../../../types/team-form-value';
import { TeamLogoDropzoneComponent } from '../team-logo-dropzone/team-logo-dropzone.component';

@Component({
  selector: 'app-team-form',
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
    TeamLogoDropzoneComponent,
  ],
  templateUrl: './team-form.component.html',
})
export class TeamFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  team = input<Team | null>(null);
  submitting = input(false);

  save = output<TeamFormValue>();
  cancel = output<void>();

  readonly isEditingExisting = computed(() => !!this.team());

  readonly selectedFile = signal<File | null>(null);
  readonly logoPreviewUrl = signal<string | null>(null);
  readonly logoError = signal(false);

  readonly form: FormGroup<CreateTeamForm> = this.fb.group({
    name: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.maxLength(50),
    ]),
  });

  constructor() {
    this.destroyRef.onDestroy(() => this.revokePreviewUrl());

    effect(() => {
      const team = this.team();

      if (team) {
        this.form.patchValue({ name: team.name });
        this.logoPreviewUrl.set(team.logo);
      }
    });
  }

  onLogoFileSelected(file: File | null): void {
    this.revokePreviewUrl();
    this.selectedFile.set(file);
    this.logoPreviewUrl.set(file ? URL.createObjectURL(file) : null);

    if (file) {
      this.logoError.set(false);
    }
  }

  onLogoCleared(): void {
    this.revokePreviewUrl();
    this.selectedFile.set(null);
    this.logoPreviewUrl.set(this.team()?.logo ?? null);
  }

  onSubmit(): void {
    const file = this.selectedFile();
    const requiresLogo = !this.isEditingExisting();

    if (this.form.invalid || (requiresLogo && !file)) {
      this.form.markAllAsTouched();
      this.logoError.set(requiresLogo && !file);
      return;
    }

    this.save.emit({ name: this.form.getRawValue().name, logoFile: file });
  }

  private revokePreviewUrl(): void {
    const url = this.logoPreviewUrl();

    if (url?.startsWith('blob:')) {
      URL.revokeObjectURL(url);
    }
  }
}
