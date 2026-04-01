import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { Observable } from 'rxjs';
import { LanguageSwitcherComponent } from '../../../shared/components/language-switcher/language-switcher.component';
import { AuthService } from '../../auth/services/auth.service';
import { WalletBalanceService } from '../../wallet/services/wallet-balance.service';
import { RoutePath } from '../../routing/route-path';
import { UserDropdownComponent } from '../user-dropdown/user-dropdown.component';
import { MENU_ITEMS } from './models/menu-items';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
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
  private readonly walletBalanceService = inject(WalletBalanceService);

  protected readonly RoutePath = RoutePath;
  protected readonly isLoggedIn$: Observable<boolean> =
    this.authService.isLoggedIn$;
  protected readonly balance = this.walletBalanceService.balance;

  menuItems = MENU_ITEMS;

  constructor() {
    this.walletBalanceService.init();
  }
}
