import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TranslocoDirective, TranslocoPipe } from '@jsverse/transloco';
import { PbCardComponent } from '../../../../shared/components/pb-card/pb-card.component';
import { PbFormFieldComponent } from '../../../../shared/components/pb-form-field/pb-form-field.component';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../types/register-request';
import { passwordMatchValidator } from '../../utils/password-match-validator';
import { PbLabel } from "../../../../shared/components/pb-form-field/directives/pb-label";
import { PbButtonComponent } from "../../../../shared/components/pb-button/pb-button.component";

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
    TranslocoPipe,
    PbCardComponent,
    PbLabel,
    PbButtonComponent
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

}
