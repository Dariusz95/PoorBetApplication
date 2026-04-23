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
import { AuthCardHeaderComponent } from '../../components/auth-card-header/auth-card-header.component';
import { RegisterRequest } from '../../requests/register-request';
import { AuthService } from '../../services/auth.service';
import { RegisterFormComponent } from '../register-form/register-form.component';
import { RegisterModel } from '../types/register.model';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    PbCardComponent,
    PbButtonComponent,
    TranslocoPipe,
    RegisterFormComponent,
    AuthCardHeaderComponent,
  ],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly RoutePath = RoutePath;
  readonly submitted = signal(false);

  onSubmitForm(event: RegisterModel): void {
    this.submitted.set(true);

    const request = this.getRegisterRequest(event);

    this.authService
      .register(request)
      .pipe(finalize(() => this.submitted.set(false)))
      .subscribe({
        next: (response) => {
          console.log('[REGISTER RES]: ', response);
          this.router.navigate(RouteLink[RoutePath.Login]);
        },
        error: (error) => {
          console.error('[REGISTER ERR]: ', error);
        },
      });
  }

  private getRegisterRequest(event: RegisterModel): RegisterRequest {
    return {
      email: event.email,
      password: event.password,
    };
  }
}
