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
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { PbButtonComponent } from '@shared/ui/pb-button/pb-button.component';
import { PbLabel } from '@shared/ui/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '@shared/ui/pb-form-field/pb-form-field.component';
import { ImageType } from '@shared/ui/pb-image/image-type.model';
import { PbInputIconDirective } from '@shared/ui/pb-input/directives/pb-input-icon.directive';
import { PbInputComponent } from '@shared/ui/pb-input/pb-input.component';
import { PASSWORD_REGEX } from '@shared/validators/regexes/passwordRegex';
import { LoginForm } from '../types/login-form';
import { LoginModel } from '../types/login.model';

@Component({
  selector: 'app-login-form',
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
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss',
})
export class LoginFormComponent {
  private readonly fb = inject(FormBuilder);

  readonly RoutePath = RoutePath;
  readonly IconType = ImageType;

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
      email: this.fb.nonNullable.control('', [
        Validators.required,
        Validators.email,
      ]),
      password: this.fb.nonNullable.control('', [
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
