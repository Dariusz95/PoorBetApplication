import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouteLink } from '@core/routing/route-link';
import { TranslocoPipe } from '@jsverse/transloco';
import { finalize } from 'rxjs';
import { PbButtonComponent } from '../../../../shared/components/pb-button/pb-button.component';
import { PbCardComponent } from '../../../../shared/components/pb-card/pb-card.component';
import { RoutePath } from '../../../routing/route-path';
import { LoginRequest } from '../../requests/login-request';
import { AuthService } from '../../services/auth.service';
import { LoginFormComponent } from '../login-form/login-form.component';
import { LoginModel } from '../types/login.model';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    PbCardComponent,
    PbButtonComponent,
    TranslocoPipe,
    LoginFormComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly RoutePath = RoutePath;
  readonly submitted = signal(false);

  onSubmitForm(event: LoginModel): void {
    this.submitted.set(true);

    const request = this.getLoginRequest(event);

    this.authService
      .login(request)
      .pipe(finalize(() => this.submitted.set(false)))
      .subscribe({
        next: (response) => {
          console.log('Logowanie zakończone sukcesem:', response);
          // this.router.createUrlTree(RouteLink[RoutePath.App]);
          this.router.navigate(RouteLink[RoutePath.App]);
        },
        error: (error) => {
          console.error('Błąd logowania:', error);
        },
      });
  }

  private getLoginRequest(event: LoginModel): LoginRequest {
    return {
      email: event.email,
      password: event.password,
    };
  }
}
