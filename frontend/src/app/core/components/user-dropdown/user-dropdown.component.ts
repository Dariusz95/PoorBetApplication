import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { Observable } from 'rxjs';
import { DropdownOption } from '../../../shared/components/pb-dropdown/dropdown-option';
import { PbDropdownComponent } from '../../../shared/components/pb-dropdown/pb-dropdown.component';
import { AuthService } from '../../auth/services/auth.service';
import { RoutePath } from '../../routing/route-path';
import { RoutingService } from '../../routing/routing.service';
import { UserMenuAction } from './user-menu-action';

@Component({
  selector: 'app-user-dropdown',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoDirective,
    PbDropdownComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './user-dropdown.component.html',
  styleUrl: './user-dropdown.component.scss',
})
export class UserDropdownComponent {
  private readonly routingService = inject(RoutingService);

  protected readonly authService = inject(AuthService);
  protected readonly RoutePath = RoutePath;
  protected readonly isLoggedIn$: Observable<boolean> =
    this.authService.isLoggedIn$;

  userMenuControl = new FormControl<UserMenuAction | null>(null);

  userMenuOptions: DropdownOption<UserMenuAction>[] = [
    {
      value: 'settings',
      label: 'user.settings',
    },
    {
      value: 'login',
      label: 'auth.register.login',
    },
    {
      value: 'register',
      label: 'auth.register.title',
    },
    {
      value: 'logout',
      label: 'auth.logout',
    },
  ];

  constructor() {
    this.userMenuControl.valueChanges.subscribe((value) => {
      console.log(value);

      if (value) {
        this.handleMenuSelection(value);
      }
    });
  }

  private handleMenuSelection(value: UserMenuAction): void {
    switch (value) {
      case 'login':
        console.log('here');
        this.routingService.navigateTo(RoutePath.Login);
        break;

      case 'logout':
        this.authService.logout();
        break;
    }
    this.userMenuControl.setValue(null);
  }
}
