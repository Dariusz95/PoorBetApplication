import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { PbCardComponent } from '../../../../shared/components/pb-card/pb-card.component';
import { PbLabel } from '../../../../shared/components/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '../../../../shared/components/pb-form-field/pb-form-field.component';
import { IconType } from '../../../../shared/components/pb-icon/pb-icon.model';
import { PbInputComponent } from '../../../../shared/components/pb-input/pb-input.component';
import { RoutePath } from '../../../routing/route-path';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../types/register-request';
import { passwordMatchValidator } from '../../utils/password-match-validator';
import { RegistrationFormGroup } from './types/registration-form-group';
import { RegistrationFormValue } from './types/registration-form-value';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    PbFormFieldComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    PbFormFieldComponent,
    TranslocoDirective,
    PbCardComponent,
    PbLabel,
    PbInputComponent,
    PbButtonComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly IconType = IconType;
  readonly RoutePath = RoutePath;

  form: RegistrationFormGroup;
  submitted = false;

  constructor() {
    this.form = this.formBuilder.group(
      {
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern(
              /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
            ),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      {
        validators: passwordMatchValidator,
      }
    );
  }

  ngOnInit(): void {}

  onSubmit(): void {
    this.submitted = true;

    if (this.form.invalid) {
      return;
    }

    const formValue: RegistrationFormValue = this.form.getRawValue();
    const { email, password } = formValue;

    const request: RegisterRequest = {
      email,
      password,
    };

    this.authService.register(request).subscribe({
      next: (response) => {
        console.log('Rejestracja zakończona sukcesem:', response);
      },
      error: (error) => {
        console.error('Błąd rejestracji:', error);
      },
    });

    this.form.reset();
    this.submitted = false;
  }
}
