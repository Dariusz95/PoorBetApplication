import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbInputComponent } from '../../../../shared/components/pb-input/pb-input.component';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../types/register-request';
import { passwordMatchValidator } from '../../utils/password-match-validator';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    PbInputComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    PbInputComponent,
    TranslocoDirective,
  ],
})
export class RegisterComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  registrationForm: FormGroup;
  submitted = false;

  constructor() {
    this.registrationForm = this.formBuilder.group(
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

    if (this.registrationForm.invalid) {
      return;
    }

    const { email, password } = this.registrationForm.value;

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

    this.registrationForm.reset();
    this.submitted = false;
  }

  getErrorMessage(controlName: string): string {
    const control = this.registrationForm.get(controlName);

    if (!control || !control.errors || !control.touched) {
      return '';
    }

    if (control.errors['required']) {
      return 'To pole jest wymagane';
    }

    if (control.errors['email']) {
      return 'Podaj prawidłowy adres email';
    }

    if (control.errors['minlength']) {
      return `Minimalna długość to ${control.errors['minlength'].requiredLength} znaków`;
    }

    if (control.errors['pattern']) {
      return 'Hasło musi zawierać dużą literę, małą literę, cyfrę i znak specjalny';
    }

    if (control.errors['passwordMismatch']) {
      return 'Hasła nie są identyczne';
    }

    return 'Nieprawidłowa wartość';
  }

  hasError(controlName: string): boolean {
    const control = this.registrationForm.get(controlName);
    return !!control && control.touched && control.invalid;
  }
}
