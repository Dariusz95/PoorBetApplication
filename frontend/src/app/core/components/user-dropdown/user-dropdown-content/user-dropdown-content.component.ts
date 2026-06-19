import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { AuthService } from '@core/auth/services/auth.service';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { UserMenuAction } from '../user-menu-action';

interface UserMenuItem {
  value: UserMenuAction;
  labelKey: string;
  icon: string;
  visible: (isLoggedIn: boolean) => boolean;
  action?: () => void;
}

@Component({
  selector: 'app-user-dropdown-content',
  imports: [TranslocoDirective, PbIconComponent],
  templateUrl: './user-dropdown-content.component.html',
  styleUrls: ['./user-dropdown-content.component.scss'],
})
export class UserDropdownContentComponent {
  private readonly authService = inject(AuthService);
  private readonly routingService = inject(RoutingService);

  private readonly isLoggedIn = toSignal(this.authService.isLoggedIn$, {
    initialValue: false,
  });

  private readonly allMenuItems: UserMenuItem[] = [
    {
      value: 'settings',
      labelKey: 'user.settings',
      icon: 'settings',
      visible: () => true,
      action: () => console.log('settings clicked'),
    },
    {
      value: 'login',
      labelKey: 'user.login',
      icon: 'login',
      visible: (isLoggedIn) => !isLoggedIn,
      action: () => this.routingService.navigateTo(RoutePath.Login),
    },
    {
      value: 'register',
      labelKey: 'user.register',
      icon: 'person_add',
      visible: (isLoggedIn) => !isLoggedIn,
      action: () => this.routingService.navigateTo(RoutePath.Register),
    },
    {
      value: 'logout',
      labelKey: 'user.logout',
      icon: 'logout',
      visible: (isLoggedIn) => isLoggedIn,
      action: () => this.authService.logout(),
    },
  ];

  readonly menuItems = computed(() =>
    this.allMenuItems.filter((item) => item.visible(this.isLoggedIn())),
  );

  executeAction(item: UserMenuItem): void {
    item.action?.();
  }
}
