import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslocoDirective } from '@jsverse/transloco';
import { map } from 'rxjs';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { DropdownOption } from '../../../shared/components/pb-dropdown/dropdown-option';
import { PbDropdownComponent } from '../../../shared/components/pb-dropdown/pb-dropdown.component';
import { LiveEventsService } from '../../../shared/services/live-events.service';
import { AuthService } from '../../auth/services/auth.service';
import { RoutePath } from '../../routing/route-path';
import { RoutingService } from '../../routing/routing.service';
import { UserBalanceComponent } from '../user-balance/user-balance.component';
import { UserMenuAction } from './user-menu-action';

interface UserDropdownOption extends DropdownOption<UserMenuAction> {
  visible: (isLoggedIn: boolean) => boolean;
}

@Component({
  selector: 'app-user-dropdown',
  standalone: true,
  imports: [
    CommonModule,
    TranslocoDirective,
    PbDropdownComponent,
    ReactiveFormsModule,
    IconComponent,
    UserBalanceComponent,
  ],
  templateUrl: './user-dropdown.component.html',
  styleUrl: './user-dropdown.component.scss',
})
export class UserDropdownComponent {
  protected readonly isLoggedIn$ = inject(AuthService).isLoggedIn$;
  private readonly routingService = inject(RoutingService);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  userMenuOptions: UserDropdownOption[] = [
    {
      value: 'settings',
      label: 'user.settings',
      icon: 'settings',
      visible: () => true,
      action: () => console.log('settings clicked'),
    },
    {
      value: 'login',
      label: 'user.login',
      icon: 'login',
      visible: (isLoggedIn) => !isLoggedIn,
      action: () => this.routingService.navigateTo(RoutePath.Login),
    },
    {
      value: 'register',
      label: 'user.register',
      icon: 'person_add',
      visible: (isLoggedIn) => !isLoggedIn,
    },
    {
      value: 'logout',
      label: 'user.logout',
      icon: 'logout',
      visible: (isLoggedIn) => isLoggedIn,
      action: () => this.authService.logout(),
    },
  ];

  filteredOptions$ = this.isLoggedIn$.pipe(
    takeUntilDestroyed(this.destroyRef),
    map((isLoggedIn) =>
      this.userMenuOptions.filter((opt) => opt.visible(isLoggedIn)),
    ),
  );
}
