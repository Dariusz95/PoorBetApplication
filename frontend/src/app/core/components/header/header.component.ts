import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { RoutePath } from '../../routing/route-path';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';
import { UserDropdownComponent } from '../user-dropdown/user-dropdown.component';
import { MENU_ITEMS } from './models/menu-items';
import { CouponDropdownComponent } from "../coupon-dropdown/coupon-dropdown.component";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    LanguageSwitcherComponent,
    TranslocoDirective,
    UserDropdownComponent,
    CouponDropdownComponent
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
