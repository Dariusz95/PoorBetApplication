import { Dialog } from '@angular/cdk/dialog';
import { Overlay } from '@angular/cdk/overlay';
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { PbIconComponent } from '@shared/ui/icon/pb-icon.component';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { RoutePath } from '../../routing/route-path';
import { RoutingService } from '../../routing/routing.service';
import { CouponDropdownComponent } from '../coupon-dropdown/coupon-dropdown.component';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';
import { UserBalanceComponent } from '../user-balance/user-balance.component';
import { UserSidePanelComponent } from '../user-side-panel/user-side-panel.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    LanguageSwitcherComponent,
    TranslocoDirective,
    PbIconComponent,
    CouponDropdownComponent,
    UserBalanceComponent,
    AsyncPipe,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private readonly authService = inject(AuthService);
  private readonly dialog = inject(Dialog);
  private readonly overlay = inject(Overlay);
  
  protected readonly routingService = inject(RoutingService);

  protected readonly RoutePath = RoutePath;
  protected readonly isLoggedIn$: Observable<boolean> =
    this.authService.isLoggedIn$;

  openUserPanel(): void {
    this.dialog.open(UserSidePanelComponent, {
      positionStrategy: this.overlay.position().global().right('0').top('0'),
      height: '100dvh',
      width: '320px',
      maxWidth: '85vw',
      panelClass: 'user-side-panel',
      hasBackdrop: true,
      backdropClass: 'user-side-panel-backdrop',
    });
  }
}
