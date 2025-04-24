import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { PbInputComponent } from '../../../shared/components/pb-input/pb-input.component';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-register',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    CommonModule,
    PbInputComponent,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  standalone: true,
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string = '';
  value = 'Clear me';

  constructor(
    private fb: FormBuilder,
    private userService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      //TODO match password
      repeatPassword: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit() {
    console.log('d1dxll');
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      // return;
    }

    const { name, email, password } = this.registerForm.value;

    this.userService.register({ name, email, password }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Wystąpił błąd podczas rejestracji.';
      },
    });
  }
}
