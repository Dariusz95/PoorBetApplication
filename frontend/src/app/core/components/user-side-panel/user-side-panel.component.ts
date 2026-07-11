import { DialogRef } from '@angular/cdk/dialog';
import { DecimalPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslocoDirective } from '@jsverse/transloco';
import { AuthService } from '@core/auth/services/auth.service';
import { JwtAuthStateService } from '@core/auth/services/jwt-auth-state.service';
import { RoutePath } from '@core/routing/route-path';
import { RoutingService } from '@core/routing/routing.service';
import { WalletService } from '@core/wallet/services/wallet.service';
import { PbSpinnerComponent } from '@shared/ui/pb-spinner/pb-spinner.component';

@Component({
  selector: 'app-user-side-panel',
  imports: [TranslocoDirective, DecimalPipe, PbSpinnerComponent],
  templateUrl: './user-side-panel.component.html',
  styleUrl: './user-side-panel.component.scss',
})
export class UserSidePanelComponent {
  private readonly dialogRef = inject(DialogRef);
  private readonly authService = inject(AuthService);
  private readonly jwtAuthState = inject(JwtAuthStateService);
  private readonly walletService = inject(WalletService);
  private readonly routingService = inject(RoutingService);

  readonly isLoggedIn = toSignal(this.authService.isLoggedIn$, { initialValue: false });
  readonly balance = this.walletService.balance;
  readonly balanceLoading = this.walletService.loading;
  readonly userEmail = this.jwtAuthState.getSubject();

  constructor() {
    this.walletService.ensureBalanceLoaded();
  }

  close(): void {
    this.dialogRef.close();
  }

  logout(): void {
    this.authService.logout();
    this.dialogRef.close();
  }

  login(): void {
    this.routingService.navigateTo(RoutePath.Login);
    this.dialogRef.close();
  }

  register(): void {
    this.routingService.navigateTo(RoutePath.Register);
    this.dialogRef.close();
  }

  goToCoupons(): void {
    this.routingService.navigateTo(RoutePath.MyCoupons);
    this.dialogRef.close();
  }
}
