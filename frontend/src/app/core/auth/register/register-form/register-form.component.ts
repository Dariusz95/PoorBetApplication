import { Component, inject, input, output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/ui/pb-form-field/pb-form-field.component';
import { PbInputIconDirective } from '@shared/ui/pb-input/directives/pb-input-icon.directive';
import { PbInputComponent } from '@shared/ui/pb-input/pb-input.component';
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
    TranslocoPipe,
    PbLabel,
    PbInputComponent,
    PbButtonComponent,
    PbInputIconDirective,
    PbIconComponent,
  ],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss',
})
export class RegisterFormComponent {
  private readonly fb = inject(FormBuilder);

  submitting = input<boolean>(false);
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
