import {
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  TouchedChangeEvent,
  Validators,
} from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { filter, map } from 'rxjs';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/ui/pb-form-field/pb-form-field.component';
import { PbInputIconDirective } from '@shared/ui/pb-input/directives/pb-input-icon.directive';
import { PbInputIconRightDirective } from '@shared/ui/pb-input/directives/pb-input-icon-right.directive';
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
    PbInputIconRightDirective,
    PbIconComponent,
  ],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss',
})
export class RegisterFormComponent {
  private readonly fb = inject(FormBuilder);

  submitting = input<boolean>(false);
  submitForm = output<RegisterModel>();

  form: FormGroup<RegisterForm> = this.createForm();

  showPassword = signal(false);
  showConfirmPassword = signal(false);
  passwordFieldType = computed(() =>
    this.showPassword() ? 'text' : 'password',
  );
  confirmPasswordFieldType = computed(() =>
    this.showConfirmPassword() ? 'text' : 'password',
  );

  private readonly passwordValue = toSignal(
    this.form.controls.password.valueChanges,
    { initialValue: this.form.controls.password.value },
  );

  readonly passwordTouched = toSignal(
    this.form.controls.password.events.pipe(
      filter(
        (event): event is TouchedChangeEvent =>
          event instanceof TouchedChangeEvent,
      ),
      map((event) => event.touched),
    ),
    { initialValue: this.form.controls.password.touched },
  );

  passwordRequirements = computed(() => {
    const value = this.passwordValue();

    return [
      {
        met: value.length >= 8,
        labelKey: 'auth.register.passwordRequirements.length',
      },
      {
        met: /[a-z]/.test(value),
        labelKey: 'auth.register.passwordRequirements.lowercase',
      },
      {
        met: /[A-Z]/.test(value),
        labelKey: 'auth.register.passwordRequirements.uppercase',
      },
      {
        met: /\d/.test(value),
        labelKey: 'auth.register.passwordRequirements.digit',
      },
      {
        met: /[@$!%*?&]/.test(value),
        labelKey: 'auth.register.passwordRequirements.special',
      },
      {
        met: /^[A-Za-z\d@$!%*?&]*$/.test(value),
        labelKey: 'auth.register.passwordRequirements.allowedChars',
      },
    ];
  });

  passwordValid = computed(() =>
    this.passwordRequirements().every((requirement) => requirement.met),
  );

  togglePasswordVisibility(): void {
    this.showPassword.update((visible) => !visible);
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword.update((visible) => !visible);
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
