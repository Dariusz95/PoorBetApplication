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
import { PbCardBodyDirective } from '@shared/ui/pb-card/directives/pb-card-body.directive';
import { PbCardHeaderDirective } from '@shared/ui/pb-card/directives/pb-card-header.directive';
import { finalize } from 'rxjs';
import { PbButtonComponent } from '../../../../shared/ui/pb-button/pb-button.component';
import { PbCardComponent } from '../../../../shared/ui/pb-card/pb-card.component';
import { RoutePath } from '../../../routing/route-path';
import { AuthCardHeaderComponent } from '../../components/auth-card-header/auth-card-header.component';
import { LoginRequest } from '../../requests/login-request';
import { AuthService } from '../../services/auth.service';
import { LoginFormComponent } from '../login-form/login-form.component';
import { LoginModel } from '../types/login.model';
import { PbCardFooterDirective } from "@shared/ui/pb-card/directives/pb-card-footer.directive.";

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    PbCardComponent,
    PbButtonComponent,
    TranslocoPipe,
    LoginFormComponent,
    AuthCardHeaderComponent,
    PbCardHeaderDirective,
    PbCardBodyDirective,
    PbCardFooterDirective
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
        next: () => {
          this.router.navigate(RouteLink[RoutePath.App]);
        },
        error: (error) => {
          console.error('[LOGIN ERR]: ', error);
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
