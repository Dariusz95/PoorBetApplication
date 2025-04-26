import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ThemeToggleComponent } from '../../../core/theme/theme-toggle.component';
import { PbInputComponent } from '../../../shared/components/pb-input/pb-input.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    PbInputComponent,
    ThemeToggleComponent,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    PbInputComponent,
  ],
})
export class RegisterComponent implements OnInit {
  registrationForm: FormGroup;
  submitted = false;

  constructor(private fb: FormBuilder) {
    this.registrationForm = this.fb.group(
      {
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
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
        terms: [false, [Validators.requiredTrue]],
      },
      {
        validators: this.passwordMatchValidator,
      }
    );
  }

  ngOnInit(): void {}

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    return null;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.registrationForm.invalid) {
      return;
    }

    // Tutaj dodaj logikę wysyłania formularza rejestracji
    console.log('Form submitted successfully');
    console.log(this.registrationForm.value);

    // Reset formularza po wysłaniu
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
