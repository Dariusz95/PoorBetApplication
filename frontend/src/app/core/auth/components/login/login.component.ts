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
  FormGroup,
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
import { LoginForm } from './types/login-form';
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

  form!: FormGroup<LoginForm>;

  constructor() {
    this.initForm();
  }

  submitted = false;

  ngOnInit(): void {}

  onSubmit(): void {
    this.submitted = true;
    
    if (this.form.invalid) {
      return;
    }

    const formValue: LoginFormValue = this.form.getRawValue();
    const { email, password } = formValue;

    const request: LoginRequest = {
      email,
      password,
    };

    this.authService.login(request).subscribe({
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

  private initForm(): void {
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
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
          ),
        ],
      }),
    });
  }
}
