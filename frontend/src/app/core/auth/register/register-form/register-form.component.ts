import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { IconComponent } from '@shared/components/icon/icon.component';
import { PbLabel } from '@shared/components/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/components/pb-form-field/pb-form-field.component';
import { IconType } from '@shared/components/pb-icon/icon-type.model';
import { PbInputIconDirective } from '@shared/components/pb-input/directives/pb-input-icon.directive';
import { PbInputComponent } from '@shared/components/pb-input/pb-input.component';
import { PbButtonComponent } from '@shared/index';
import { PASSWORD_REGEX } from '@shared/validators/regexes/passwordRegex';
import { passwordMatchValidator } from '../../utils/password-match-validator';
import { RegisterForm } from '../types/register-form';
import { RegisterModel } from '../types/register.model';

@Component({
  selector: 'app-register-form',
  imports: [
    PbFormFieldComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    TranslocoPipe,
    PbLabel,
    PbInputComponent,
    PbButtonComponent,
    PbInputIconDirective,
    IconComponent,
  ],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss',
})
export class RegisterFormComponent {
  private readonly fb = inject(FormBuilder);

  readonly IconType = IconType;

  submitForm = output<RegisterModel>();

  form: FormGroup<RegisterForm>;

  constructor() {
    this.form = this.createForm();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitForm.emit(this.toRegisterModel());
  }

  private createForm(): FormGroup<RegisterForm> {
    return this.fb.group(
      {
        email: this.fb.nonNullable.control('', [
          Validators.required,
          Validators.email,
        ]),
        password: this.fb.nonNullable.control('', [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(PASSWORD_REGEX),
        ]),
        confirmPassword: this.fb.nonNullable.control('', [Validators.required]),
      },
      {
        validators: passwordMatchValidator,
      },
    );
  }

  private toRegisterModel(): RegisterModel {
    const { email, password, confirmPassword } = this.form.getRawValue();

    return { email, password, confirmPassword };
  }
}
