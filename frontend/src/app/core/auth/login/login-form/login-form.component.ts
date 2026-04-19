import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RoutePath } from '@core/routing/route-path';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbLabel } from '@shared/components/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/components/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '@shared/components/pb-input/pb-input.component';
import { PbButtonComponent } from '@shared/index';
import { PASSWORD_REGEX } from '@shared/validators/regexes/passwordRegex';
import { LoginForm } from '../types/login-form';
import { LoginModel } from '../types/login.model';

@Component({
  selector: 'app-login-form',
  imports: [
    PbFormFieldComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    TranslocoPipe,
    PbLabel,
    PbInputComponent,
    PbButtonComponent,
  ],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss',
})
export class LoginFormComponent {
  private readonly fb = inject(FormBuilder);

  readonly RoutePath = RoutePath;

  submitForm = output<LoginModel>();

  form: FormGroup<LoginForm>;

  constructor() {
    this.form = this.createForm();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();

      return;
    }

    this.submitForm.emit(this.toLoginModel());
  }

  private createForm(): FormGroup<LoginForm> {
    return this.fb.group({
      email: this.fb.nonNullable.control('asd@asd.pl', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.nonNullable.control('zaq1@WSX', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(PASSWORD_REGEX),
      ]),
    });
  }

  private toLoginModel(): LoginModel {
    const { email, password } = this.form.getRawValue();

    return { email, password };
  }
}
