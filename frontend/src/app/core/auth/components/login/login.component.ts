import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { PbCardComponent } from '../../../../shared/components/pb-card/pb-card.component';
import { PbLabel } from '../../../../shared/components/pb-form-field/directives/pb-label';
import { PbFormFieldComponent } from '../../../../shared/components/pb-form-field/pb-form-field.component';
import { PbInputComponent } from '../../../../shared/components/pb-input/pb-input.component';
import { RoutePath } from '../../../routing/route-path';
import { LoginRequest } from '../../requests/login-request';
import { AuthService } from '../../services/auth.service';
import { LoginFormGroup } from './types/login-form-group';
import { LoginFormValue } from './types/login-form-value';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
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
export class LoginComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly RoutePath = RoutePath;

  form: LoginFormGroup;

  constructor() {
    this.form = this.formBuilder.group({
      email: new FormControl('asd@asd.pl', {
        nonNullable: true,
        validators: [Validators.required, Validators.email],
      }),
      password: new FormControl('zaq1@WSX', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
          ),
        ],
      }),
    });
  }

  submitted = false;

  ngOnInit(): void {}

  onSubmit(): void {
    this.submitted = true;
    console.log('this.form.invalid',this.form.invalid)
    if (this.form.invalid) {
      return;
    }

    const formValue: LoginFormValue = this.form.getRawValue();
    const { email, password } = formValue;

    const request: LoginRequest = {
      email,
      password,
    };

    this.authService.login(request)
    .subscribe({
      next: (response) => {
        console.log('Logowanie zakończone sukcesem:', response);
      },
      error: (error) => {
        console.error('Błąd logowania:', error);
      },
    });

    this.form.reset();
    this.submitted = false;
  }
}
