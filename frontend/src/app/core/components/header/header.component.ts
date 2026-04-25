import { Component, inject } from '@angular/core';
import { TranslocoDirective } from '@jsverse/transloco';
import { Observable } from 'rxjs';
import { LanguageSwitcherComponent } from '../../../shared/components/language-switcher/language-switcher.component';
import { AuthService } from '../../auth/services/auth.service';
import { RoutePath } from '../../routing/route-path';
import { UserDropdownComponent } from '../user-dropdown/user-dropdown.component';
import { MENU_ITEMS } from './models/menu-items';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    LanguageSwitcherComponent,
    TranslocoDirective,
    UserDropdownComponent,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);

  protected readonly RoutePath = RoutePath;
  protected readonly isLoggedIn$: Observable<boolean> =
    this.authService.isLoggedIn$;

  menuItems = MENU_ITEMS;
}
