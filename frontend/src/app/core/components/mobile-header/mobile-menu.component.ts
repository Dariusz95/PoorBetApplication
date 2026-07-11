import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';
import { AuthService } from '../../auth/services/auth.service';
import { MENU_ITEMS } from './models/menu-items';

@Component({
  selector: 'app-mobile-menu',
  imports: [RouterLink, RouterLinkActive, TranslocoPipe],
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
})
export class MobileMenuComponent {
  private readonly authService = inject(AuthService);

  private readonly isLoggedIn = toSignal(this.authService.isLoggedIn$, {
    initialValue: false,
  });

  readonly menuItems = computed(() =>
    MENU_ITEMS.filter((item) => !item.requiresAuth || this.isLoggedIn()),
  );
}
